package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.domain.BoardTile
import com.example.myapplication.domain.DifficultyLevel
import com.example.myapplication.domain.GameState
import com.example.myapplication.domain.GameWinner
import com.example.myapplication.domain.TicTacToeGameEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: VIEWMODEL Y ROTACIÓN DE PANTALLA (RETO 6)
 * ============================================================================
 * ¿Por qué esta clase resuelve el problema de la rotación de pantalla?
 *
 * 1. **Supervivencia a Cambios de Configuración:**
 *    Cuando el usuario gira la pantalla (Portrait <-> Landscape), Android destruye
 *    la Activity actual y la vuelve a crear. Las variables tradicionales de la Activity
 *    se reiniciarían. Sin embargo, el 'ViewModel' permanece ALMACENADO EN MEMORIA por el
 *    ViewModelStoreOwner y NO SE DESTRUYE durante la rotación.
 *
 * 2. **SavedStateHandle:**
 *    Permite guardar el estado incluso si el sistema operativo mata el proceso por
 *    falta de memoria en segundo plano.
 *
 * 3. **StateFlow:**
 *    Flujo de datos reactivo e inmutable que la UI de Compose escucha continuamente.
 */
class TicTacToeViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val gameEngine = TicTacToeGameEngine()

    // Estado privado mutable
    private val _uiState = MutableStateFlow(
        GameState(
            humanWins = preferencesManager.getHumanWins(),
            computerWins = preferencesManager.getComputerWins(),
            ties = preferencesManager.getTies(),
            difficulty = preferencesManager.getDifficulty()
        )
    )

    // Estado público inmutable expuesto a la vista Compose
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    /**
     * Procesa la pulsación de una casilla por parte del jugador humano.
     */
    fun onTileClicked(index: Int) {
        val currentState = _uiState.value

        // Ignorar el toque si:
        // - El juego ya terminó
        // - Es turno de la CPU o la CPU está pensando
        // - La casilla ya está ocupada
        if (currentState.isGameOver ||
            !currentState.isHumanTurn ||
            currentState.isCpuThinking ||
            currentState.board[index] != BoardTile.EMPTY
        ) {
            return
        }

        // 1. Marcar la casilla del jugador humano
        val updatedBoard = currentState.board.toMutableList().apply {
            set(index, BoardTile.HUMAN)
        }

        // 2. Verificar si el humano ganó o empató
        val (winner, winningLine) = gameEngine.checkWinner(updatedBoard)

        if (winner != GameWinner.NONE) {
            handleGameEnd(updatedBoard, winner, winningLine)
        } else {
            // El juego continúa: Pasar el turno a la Computadora (CPU)
            _uiState.update {
                it.copy(
                    board = updatedBoard,
                    isHumanTurn = false,
                    isCpuThinking = true
                )
            }
            // Ejecutar el turno de la CPU de forma asíncrona (Corrutinas)
            triggerCpuMove()
        }
    }

    /**
     * Ejecuta el movimiento de la Inteligencia Artificial simulando una breve pausa
     * para dar una sensación natural de pensamiento.
     */
    private fun triggerCpuMove() {
        viewModelScope.launch {
            // Pausa deliberada de 500ms para efecto visual
            delay(500)

            val currentState = _uiState.value
            val cpuMove = gameEngine.getCpuMove(currentState.board, currentState.difficulty)

            if (cpuMove != null) {
                val updatedBoard = currentState.board.toMutableList().apply {
                    set(cpuMove, BoardTile.COMPUTER)
                }

                val (winner, winningLine) = gameEngine.checkWinner(updatedBoard)

                if (winner != GameWinner.NONE) {
                    handleGameEnd(updatedBoard, winner, winningLine)
                } else {
                    // Volver al turno del Jugador Humano
                    _uiState.update {
                        it.copy(
                            board = updatedBoard,
                            isHumanTurn = true,
                            isCpuThinking = false
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isCpuThinking = false) }
            }
        }
    }

    /**
     * Maneja la finalización de la partida, actualiza marcadores y los guarda
     * en SharedPreferences (Persistencia).
     */
    private fun handleGameEnd(
        board: List<BoardTile>,
        winner: GameWinner,
        winningLine: List<Int>?
    ) {
        val current = _uiState.value
        var newHumanWins = current.humanWins
        var newComputerWins = current.computerWins
        var newTies = current.ties

        when (winner) {
            GameWinner.HUMAN -> newHumanWins++
            GameWinner.COMPUTER -> newComputerWins++
            GameWinner.TIE -> newTies++
            GameWinner.NONE -> {}
        }

        // Guardar persistentemente en almacenamiento local
        preferencesManager.saveScores(newHumanWins, newComputerWins, newTies)

        _uiState.update {
            it.copy(
                board = board,
                winner = winner,
                winningLine = winningLine,
                humanWins = newHumanWins,
                computerWins = newComputerWins,
                ties = newTies,
                isCpuThinking = false
            )
        }
    }

    /**
     * Inicia una nueva partida reseteando el tablero sin borrar el historial.
     */
    fun resetBoard() {
        _uiState.update {
            it.copy(
                board = List(9) { BoardTile.EMPTY },
                isHumanTurn = true,
                winner = GameWinner.NONE,
                winningLine = null,
                isCpuThinking = false
            )
        }
    }

    /**
     * Cambia la dificultad y la guarda en SharedPreferences.
     */
    fun setDifficulty(difficulty: DifficultyLevel) {
        preferencesManager.saveDifficulty(difficulty)
        _uiState.update { it.copy(difficulty = difficulty) }
        resetBoard()
    }

    /**
     * Muestra u oculta el diálogo modal de dificultad.
     */
    fun showDifficultyDialog(show: Boolean) {
        _uiState.update { it.copy(showDifficultyDialog = show) }
    }

    /**
     * Borra el historial de marcadores de SharedPreferences y reinicia contadores.
     */
    fun resetScores() {
        preferencesManager.resetScores()
        _uiState.update {
            it.copy(
                humanWins = 0,
                computerWins = 0,
                ties = 0
            )
        }
        resetBoard()
    }
}
