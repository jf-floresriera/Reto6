package com.example.myapplication.domain

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: NIVELES DE DIFICULTAD Y LÓGICA DE NEGOCIO
 * ============================================================================
 * Enum que define los tres niveles de dificultad exigidos para el bot del juego.
 *
 * Cada constante incluye un nombre descriptivo en español para mostrar en la interfaz
 * gráfica y facilitar la localización/traducción de la app.
 */
enum class DifficultyLevel(val label: String) {
    /**
     * Nivel Fácil:
     * La CPU selecciona casillas disponibles de forma completamente aleatoria.
     */
    EASY("Fácil"),

    /**
     * Nivel Medio:
     * La CPU evalúa si puede ganar inmediatamente o si debe bloquear un movimiento
     * ganador del jugador humano. De lo contrario, juega aleatorio.
     */
    MEDIUM("Medio"),

    /**
     * Nivel Difícil / Imposible:
     * La CPU evalúa todas las jugadas estratégicas (estrategia Minimax / heurística inteligente)
     * para garantizar la victoria o el empate perfecto.
     */
    HARD("Difícil")
}
