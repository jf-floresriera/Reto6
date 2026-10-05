package com.example.myapplication.domain

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: ENUMERACIONES (ENUMS) EN ARQUITECTURA DE SOFTWARE
 * ============================================================================
 * Representa el estado de una casilla individual dentro del tablero 3x3 del Triqui.
 *
 * Utilizar un 'enum' en lugar de cadenas de texto ("X", "O") o números enteros (1, 2, 0)
 * nos ofrece **Seguridad de Tipos (Type Safety)**: evita errores tipográficos en tiempo
 * de compilación y facilita el procesamiento en las vistas con 'when' expresivos.
 */
enum class BoardTile(val symbol: String) {
    /** Casilla vacía disponible para ser marcada. */
    EMPTY(""),

    /** Marca colocada por el Jugador Humano ('X'). */
    HUMAN("X"),

    /** Marca colocada por la Computadora / Inteligencia Artificial ('O'). */
    COMPUTER("O")
}
