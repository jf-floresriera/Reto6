package com.example.myapplication.domain

enum class GameWinner {
    NONE,
    HUMAN,
    COMPUTER,
    TIE
}

data class GameState(
    val board: List<BoardTile> = List(9) { BoardTile.EMPTY },
    val isHumanTurn: Boolean = true,
    val winner: GameWinner = GameWinner.NONE,
    val winningLine: List<Int>? = null,
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0,
    val difficulty: DifficultyLevel = DifficultyLevel.EXPERT,
    val gameMode: GameMode = GameMode.ONE_PLAYER,
    val soundEnabled: Boolean = true,
    val selectedTheme: AppTheme = AppTheme.CLASSIC,
    val showGameModeDialog: Boolean = false,
    val showDifficultyDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val isCpuThinking: Boolean = false
) {
    val isGameOver: Boolean
        get() = winner != GameWinner.NONE
}
