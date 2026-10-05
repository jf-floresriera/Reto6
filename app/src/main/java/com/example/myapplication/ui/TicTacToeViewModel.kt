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
import com.example.myapplication.domain.GameMode
import com.example.myapplication.domain.GameState
import com.example.myapplication.domain.GameWinner
import com.example.myapplication.domain.TicTacToeGameEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            gameMode = preferencesManager.getGameMode(),
            soundEnabled = preferencesManager.isSoundEnabled(),
            selectedTheme = preferencesManager.getTheme()
        )
    )

    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    fun onTileClicked(index: Int) {
        val currentState = _uiState.value

        if (currentState.isGameOver ||
            currentState.isCpuThinking ||
            currentState.board[index] != BoardTile.EMPTY
        ) {
            return
        }

        if (currentState.gameMode == GameMode.ONE_PLAYER) {
            if (!currentState.isHumanTurn) return

            // Sonido de movimiento del jugador adaptado al tema actual
            soundManager.playHumanMove(currentState.selectedTheme, currentState.soundEnabled)

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
        } else {
            val activePlayerTile = if (currentState.isHumanTurn) BoardTile.HUMAN else BoardTile.COMPUTER

            if (currentState.isHumanTurn) {
                soundManager.playHumanMove(currentState.selectedTheme, currentState.soundEnabled)
            } else {
                soundManager.playComputerMove(currentState.selectedTheme, currentState.soundEnabled)
            }

            val updatedBoard = currentState.board.toMutableList().apply {
                set(index, activePlayerTile)
            }

            val (winner, winningLine) = gameEngine.checkWinner(updatedBoard)

            if (winner != GameWinner.NONE) {
                handleGameEnd(updatedBoard, winner, winningLine)
            } else {
                _uiState.update {
                    it.copy(
                        board = updatedBoard,
                        isHumanTurn = !currentState.isHumanTurn,
                        isCpuThinking = false
                    )
                }
            }
        }
    }

    private fun triggerCpuMove() {
        viewModelScope.launch {
            delay(500)

            val currentState = _uiState.value
            val cpuMove = gameEngine.getCpuMove(currentState.board, currentState.difficulty)

            if (cpuMove != null) {
                // Sonido de movimiento de la computadora adaptado al tema actual
                soundManager.playComputerMove(currentState.selectedTheme, currentState.soundEnabled)

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

    fun setDifficulty(difficulty: DifficultyLevel) {
        preferencesManager.saveDifficulty(difficulty)
        _uiState.update { it.copy(difficulty = difficulty) }
        resetBoard()
    }

    fun setGameMode(gameMode: GameMode) {
        preferencesManager.saveGameMode(gameMode)
        _uiState.update { it.copy(gameMode = gameMode) }
        resetBoard()
    }

    fun toggleSound() {
        val newSoundState = !_uiState.value.soundEnabled
        preferencesManager.saveSoundEnabled(newSoundState)
        _uiState.update { it.copy(soundEnabled = newSoundState) }
    }

    fun setTheme(theme: AppTheme) {
        preferencesManager.saveTheme(theme)
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    fun showGameModeDialog(show: Boolean) {
        _uiState.update { it.copy(showGameModeDialog = show) }
    }

    fun showDifficultyDialog(show: Boolean) {
        _uiState.update { it.copy(showDifficultyDialog = show) }
    }

    fun showThemeDialog(show: Boolean) {
        _uiState.update { it.copy(showThemeDialog = show) }
    }

    fun showAboutDialog(show: Boolean) {
        _uiState.update { it.copy(showAboutDialog = show) }
    }

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
