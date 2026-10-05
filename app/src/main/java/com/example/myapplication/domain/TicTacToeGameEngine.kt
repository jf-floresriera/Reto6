package com.example.myapplication.domain

import kotlin.random.Random

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: MOTOR DE LÓGICA DE NEGOCIO (GAME ENGINE)
 * ============================================================================
 * Esta clase contiene la lógica pura del juego Triqui, totalmente desacoplada
 * de la interfaz de usuario (UI) o del framework de Android.
 *
 * Principio de Responsabilidad Única (SRP):
 * Se encarga únicamente de:
 * 1. Validar combinaciones ganadoras (filas, columnas, diagonales).
 * 2. Determinar el movimiento de la Inteligencia Artificial según la dificultad.
 * 3. Evaluar el estado final del juego (Victoria, Empate, En curso).
 */
class TicTacToeGameEngine {

    companion object {
        /**
         * Las 8 líneas o combinaciones posibles para ganar en un tablero de 3x3:
         * - 3 Filas horizontales: (0,1,2), (3,4,5), (6,7,8)
         * - 3 Columnas verticales: (0,3,6), (1,4,7), (2,5,8)
         * - 2 Diagonales: (0,4,8), (2,4,6)
         */
        val WINNING_COMBINATIONS = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Filas
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columnas
            listOf(0, 4, 8), listOf(2, 4, 6)                  // Diagonales
        )
    }

    /**
     * Verifica si existe un ganador en el tablero actual.
     * @return Par con el tipo de ganador y la línea ganadora (si existe).
     */
    fun checkWinner(board: List<BoardTile>): Pair<GameWinner, List<Int>?> {
        for (combination in WINNING_COMBINATIONS) {
            val (a, b, c) = combination
            if (board[a] != BoardTile.EMPTY && board[a] == board[b] && board[b] == board[c]) {
                val winner = if (board[a] == BoardTile.HUMAN) GameWinner.HUMAN else GameWinner.COMPUTER
                return Pair(winner, combination)
            }
        }

        // Si no hay ganador pero el tablero está lleno, es un empate
        if (board.none { it == BoardTile.EMPTY }) {
            return Pair(GameWinner.TIE, null)
        }

        // El juego aún no termina
        return Pair(GameWinner.NONE, null)
    }

    /**
     * Obtiene los índices de todas las casillas desocupadas (EMPTY).
     */
    fun getAvailableMoves(board: List<BoardTile>): List<Int> {
        return board.indices.filter { board[it] == BoardTile.EMPTY }
    }

    /**
     * Calcula el siguiente movimiento de la Inteligencia Artificial (CPU)
     * basándose en la dificultad seleccionada.
     */
    fun getCpuMove(board: List<BoardTile>, difficulty: DifficultyLevel): Int? {
        val availableMoves = getAvailableMoves(board)
        if (availableMoves.isEmpty()) return null

        return when (difficulty) {
            DifficultyLevel.EASY -> getEasyMove(availableMoves)
            DifficultyLevel.MEDIUM -> getMediumMove(board, availableMoves)
            DifficultyLevel.HARD -> getHardMove(board, availableMoves)
        }
    }

    /**
     * ESTRATEGIA FÁCIL:
     * Elige una posición completamente aleatoria entre las disponibles.
     */
    private fun getEasyMove(availableMoves: List<Int>): Int {
        return availableMoves[Random.nextInt(availableMoves.size)]
    }

    /**
     * ESTRATEGIA MEDIA:
     * 1. Revisa si la CPU puede ganar en este turno.
     * 2. Revisa si el Humano puede ganar en el próximo turno y lo BLOQUEA.
     * 3. Si nada de lo anterior aplica, elige de forma aleatoria.
     */
    private fun getMediumMove(board: List<BoardTile>, availableMoves: List<Int>): Int {
        // 1. ¿Puedo ganar en este movimiento?
        val winningMove = findWinningMove(board, BoardTile.COMPUTER, availableMoves)
        if (winningMove != null) return winningMove

        // 2. ¿Debo bloquear una victoria inminente del humano?
        val blockingMove = findWinningMove(board, BoardTile.HUMAN, availableMoves)
        if (blockingMove != null) return blockingMove

        // 3. De lo contrario, movimiento aleatorio
        return getEasyMove(availableMoves)
    }

    /**
     * ESTRATEGIA DIFÍCIL (IA Inteligente / Minimax Simplificado):
     * 1. Ganar si es posible.
     * 2. Bloquear si el usuario está por ganar.
     * 3. Tomar el centro (casilla 4) que da mayor ventaja táctica.
     * 4. Tomar las esquinas estratégicas (0, 2, 6, 8).
     * 5. Tomar los lados restantes.
     */
    private fun getHardMove(board: List<BoardTile>, availableMoves: List<Int>): Int {
        // 1. Ganar si hay oportunidad
        val winningMove = findWinningMove(board, BoardTile.COMPUTER, availableMoves)
        if (winningMove != null) return winningMove

        // 2. Bloquear al jugador humano
        val blockingMove = findWinningMove(board, BoardTile.HUMAN, availableMoves)
        if (blockingMove != null) return blockingMove

        // 3. Tomar el centro si está libre (Posición clave en Triqui)
        if (availableMoves.contains(4)) return 4

        // 4. Tomar esquinas libres
        val corners = listOf(0, 2, 6, 8).filter { availableMoves.contains(it) }
        if (corners.isNotEmpty()) {
            return corners[Random.nextInt(corners.size)]
        }

        // 5. Tomar bordes restantes
        return getEasyMove(availableMoves)
    }

    /**
     * Función auxiliar para verificar si simulando una marca en cualquiera de las
     * casillas disponibles para 'tileType' se obtiene una línea de 3 completa.
     */
    private fun findWinningMove(
        board: List<BoardTile>,
        tileType: BoardTile,
        availableMoves: List<Int>
    ): Int? {
        for (move in availableMoves) {
            val simulatedBoard = board.toMutableList().apply { set(move, tileType) }
            val (winner, _) = checkWinner(simulatedBoard)
            if ((tileType == BoardTile.COMPUTER && winner == GameWinner.COMPUTER) ||
                (tileType == BoardTile.HUMAN && winner == GameWinner.HUMAN)
            ) {
                return move
            }
        }
        return null
    }
}
