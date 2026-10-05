package com.example.myapplication.domain

/**
 * Representa el estado inmutable de la pantalla de juego.
 */
enum class GameWinner {
    NONE,
    HUMAN,
    COMPUTER,
    TIE
}

data class GameState(
    /** Cuadrícula 3x3 del tablero. */
    val board: List<BoardTile> = List(9) { BoardTile.EMPTY },

    /** Indica si es el turno del jugador humano. */
    val isHumanTurn: Boolean = true,

    /** Ganador del juego actual. */
    val winner: GameWinner = GameWinner.NONE,

    /** Índices de las casillas de la línea ganadora. */
    val winningLine: List<Int>? = null,

    /** Marcador acumulado de victorias del jugador humano. */
    val humanWins: Int = 0,

    /** Marcador acumulado de victorias de la computadora. */
    val computerWins: Int = 0,

    /** Marcador acumulado de empates. */
    val ties: Int = 0,

    /** Nivel de dificultad actual para la CPU. */
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,

    /** Control del estado de audio (activado/desactivado). */
    val soundEnabled: Boolean = true,

    /** Tema visual seleccionado en la aplicación. */
    val selectedTheme: AppTheme = AppTheme.CLASSIC,

    /** Control de visibilidad del diálogo de dificultad. */
    val showDifficultyDialog: Boolean = false,

    /** Control de visibilidad del diálogo de temas visuales. */
    val showThemeDialog: Boolean = false,

    /** Indica si la CPU está procesando su movimiento. */
    val isCpuThinking: Boolean = false
) {
    /** Retorna verdadero si el juego ha finalizado. */
    val isGameOver: Boolean
        get() = winner != GameWinner.NONE
}
