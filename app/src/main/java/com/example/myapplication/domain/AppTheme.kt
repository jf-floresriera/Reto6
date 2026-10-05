package com.example.myapplication.domain

/**
 * Representa los temas de color y estilos visuales seleccionables en la aplicación.
 */
enum class AppTheme(val displayName: String) {
    /** Tema predeterminado Material 3 basado en tonos azules y púrpuras. */
    CLASSIC("Clásico Material"),

    /** Tema Océano basado en tonos turquesa y azul marino. */
    OCEAN("Océano Azul"),

    /** Tema Oscuro Neón enfocado en alto contraste. */
    DARK_NEON("Neón Oscuro"),

    /** Tema Bosque basado en tonos verdes orgánicos. */
    FOREST("Bosque Verde")
}
