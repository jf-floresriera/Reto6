package com.example.myapplication.domain

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: ESTADO INMUTABLE Y UNIDIRECTIONAL DATA FLOW (UDF)
 * ============================================================================
 * Esta Data Class representa una "fotografía" completa e inmutable del estado del
 * juego en un instante de tiempo determinado.
 *
 * En Jetpack Compose, el estado fluye en una sola dirección:
 * ViewModel (Emite GameState) ---> Pantalla Composable (Renderiza la UI)
 *
 * Al ser inmutable, garantizamos que la UI se redibuje (recomposición) de manera
 * eficiente y segura únicamente cuando cambia alguna propiedad.
 */

enum class GameWinner {
    NONE,
    HUMAN,
    COMPUTER,
    TIE
}

data class GameState(
    /**
     * Arreglo de 9 posiciones que representa la cuadrícula 3x3 del tablero.
     * Posiciones:
     *  0 | 1 | 2
     *  ---------
     *  3 | 4 | 5
     *  ---------
     *  6 | 7 | 8
     */
    val board: List<BoardTile> = List(9) { BoardTile.EMPTY },

    /** Indica si actualmente es el turno del jugador humano. */
    val isHumanTurn: Boolean = true,

    /** Ganador del juego actual (NONE si la partida sigue en curso). */
    val winner: GameWinner = GameWinner.NONE,

    /** Lista con los 3 índices de las casillas que conforman la línea ganadora (ej. [0, 1, 2]). */
    val winningLine: List<Int>? = null,

    /** Marcador acumulado de victorias del jugador humano. */
    val humanWins: Int = 0,

    /** Marcador acumulado de victorias de la computadora. */
    val computerWins: Int = 0,

    /** Marcador acumulado de empates. */
    val ties: Int = 0,

    /** Nivel de dificultad actual para la CPU. */
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,

    /** Indica si se debe mostrar el diálogo de selección de dificultad. */
    val showDifficultyDialog: Boolean = false,

    /** Indica si hay un movimiento de la CPU en proceso (para bloquear clicks del usuario). */
    val isCpuThinking: Boolean = false
) {
    /** Propiedad calculada: Retorna verdades si el juego ha terminado. */
    val isGameOver: Boolean
        get() = winner != GameWinner.NONE
}
