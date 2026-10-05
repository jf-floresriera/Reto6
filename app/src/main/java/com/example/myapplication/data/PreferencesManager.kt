package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.domain.DifficultyLevel

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: PERSISTENCIA DE DATOS LOCAL CON SHAREDPREFERENCES (RETO 6)
 * ============================================================================
 * 'SharedPreferences' es una API nativa de Android diseñada para almacenar pares de
 * Clave-Valor (Key-Value) de forma persistente en un archivo XML interno del dispositivo.
 *
 * En este reto (según la guía oficial de Harding University):
 * 1. Nombre del archivo de preferencias: "ttt_prefs.xml"
 * 2. Claves de persistencia: "mHumanWins", "mComputerWins", "mTies", "mDifficultyLevel"
 * 3. Sobrevive al cierre completo de la aplicación, apagado del dispositivo y reinicios.
 */
class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ttt_prefs"
        private const val KEY_HUMAN_WINS = "mHumanWins"
        private const val KEY_COMPUTER_WINS = "mComputerWins"
        private const val KEY_TIES = "mTies"
        private const val KEY_DIFFICULTY = "mDifficultyLevel"
    }

    /**
     * Guarda el marcador completo de partidas en SharedPreferences.
     */
    fun saveScores(humanWins: Int, computerWins: Int, ties: Int) {
        sharedPreferences.edit()
            .putInt(KEY_HUMAN_WINS, humanWins)
            .putInt(KEY_COMPUTER_WINS, computerWins)
            .putInt(KEY_TIES, ties)
            .apply() // .apply() realiza el guardado asíncrono en segundo plano
    }

    /**
     * Obtiene las victorias del jugador humano (0 si no se ha guardado nada).
     */
    fun getHumanWins(): Int = sharedPreferences.getInt(KEY_HUMAN_WINS, 0)

    /**
     * Obtiene las victorias de la computadora (0 por defecto).
     */
    fun getComputerWins(): Int = sharedPreferences.getInt(KEY_COMPUTER_WINS, 0)

    /**
     * Obtiene la cantidad de empates (0 por defecto).
     */
    fun getTies(): Int = sharedPreferences.getInt(KEY_TIES, 0)

    /**
     * EXTRA CHALLENGE 1:
     * Guarda el nivel de dificultad seleccionado convirtiendo el enum a String.
     */
    fun saveDifficulty(difficulty: DifficultyLevel) {
        sharedPreferences.edit()
            .putString(KEY_DIFFICULTY, difficulty.name)
            .apply()
    }

    /**
     * EXTRA CHALLENGE 1:
     * Recupera la dificultad guardada convirtiendo la cadena de texto de nuevo al Enum.
     */
    fun getDifficulty(): DifficultyLevel {
        val savedName = sharedPreferences.getString(KEY_DIFFICULTY, DifficultyLevel.EASY.name)
        return try {
            DifficultyLevel.valueOf(savedName ?: DifficultyLevel.EASY.name)
        } catch (e: Exception) {
            DifficultyLevel.EASY
        }
    }

    /**
     * Reinicia el marcador a cero en el almacenamiento persistente ttt_prefs.xml.
     */
    fun resetScores() {
        sharedPreferences.edit()
            .putInt(KEY_HUMAN_WINS, 0)
            .putInt(KEY_COMPUTER_WINS, 0)
            .putInt(KEY_TIES, 0)
            .apply()
    }
}
