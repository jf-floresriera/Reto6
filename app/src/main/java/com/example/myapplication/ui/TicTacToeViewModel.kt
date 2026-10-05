package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SoundManager
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.domain.AppTheme
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
 * ViewModel que conserva el estado inmutable del juego durante cambios de configuración.
 */
class TicTacToeViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val gameEngine = TicTacToeGameEngine()
    private val soundManager = SoundManager()

    private val _uiState = MutableStateFlow(
        GameState(
            humanWins = preferencesManager.getHumanWins(),
            computerWins = preferencesManager.getComputerWins(),
            ties = preferencesManager.getTies(),
            difficulty = preferencesManager.getDifficulty(),
            soundEnabled = preferencesManager.isSoundEnabled(),
            selectedTheme = preferencesManager.getTheme()
        )
    )

    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    /**
     * Procesa la interacción del jugador humano en una casilla.
     */
    fun onTileClicked(index: Int) {
        val currentState = _uiState.value

        if (currentState.isGameOver ||
            !currentState.isHumanTurn ||
            currentState.isCpuThinking ||
            currentState.board[index] != BoardTile.EMPTY
        ) {
            return
        }

        // Reproducir sonido de movimiento humano
        soundManager.playHumanMove(currentState.soundEnabled)

        val updatedBoard = currentState.board.toMutableList().apply {
            set(index, BoardTile.HUMAN)
        }

        val (winner, winningLine) = gameEngine.checkWinner(updatedBoard)

        if (winner != GameWinner.NONE) {
            handleGameEnd(updatedBoard, winner, winningLine)
        } else {
            _uiState.update {
                it.copy(
                    board = updatedBoard,
                    isHumanTurn = false,
                    isCpuThinking = true
                )
            }
            triggerCpuMove()
        }
    }

    /**
     * Ejecuta el movimiento de la máquina de forma asíncrona.
     */
    private fun triggerCpuMove() {
        viewModelScope.launch {
            delay(500)

            val currentState = _uiState.value
            val cpuMove = gameEngine.getCpuMove(currentState.board, currentState.difficulty)

            if (cpuMove != null) {
                // Reproducir sonido de movimiento de la computadora
                soundManager.playComputerMove(currentState.soundEnabled)

                val updatedBoard = currentState.board.toMutableList().apply {
                    set(cpuMove, BoardTile.COMPUTER)
                }

                val (winner, winningLine) = gameEngine.checkWinner(updatedBoard)

                if (winner != GameWinner.NONE) {
                    handleGameEnd(updatedBoard, winner, winningLine)
                } else {
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
     * Gestiona el cierre del juego, sonido del resultado y persistencia.
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
            GameWinner.HUMAN -> {
                newHumanWins++
                soundManager.playWinSound(current.soundEnabled)
            }
            GameWinner.COMPUTER -> {
                newComputerWins++
                soundManager.playLoseSound(current.soundEnabled)
            }
            GameWinner.TIE -> {
                newTies++
                soundManager.playTieSound(current.soundEnabled)
            }
            GameWinner.NONE -> {}
        }

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

    /** Reiniciar tablero sin borrar contadores. */
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

    /** Cambiar y guardar nivel de dificultad. */
    fun setDifficulty(difficulty: DifficultyLevel) {
        preferencesManager.saveDifficulty(difficulty)
        _uiState.update { it.copy(difficulty = difficulty) }
        resetBoard()
    }

    /** Alternar y guardar estado del sonido (Activado/Desactivado). */
    fun toggleSound() {
        val newSoundState = !_uiState.value.soundEnabled
        preferencesManager.saveSoundEnabled(newSoundState)
        _uiState.update { it.copy(soundEnabled = newSoundState) }
    }

    /** Seleccionar y guardar tema visual. */
    fun setTheme(theme: AppTheme) {
        preferencesManager.saveTheme(theme)
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    /** Visibilidad de diálogos. */
    fun showDifficultyDialog(show: Boolean) {
        _uiState.update { it.copy(showDifficultyDialog = show) }
    }

    fun showThemeDialog(show: Boolean) {
        _uiState.update { it.copy(showThemeDialog = show) }
    }

    /** Reiniciar marcadores acumulados. */
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
