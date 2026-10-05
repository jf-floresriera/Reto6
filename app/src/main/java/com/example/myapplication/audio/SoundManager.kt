package com.example.myapplication.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Gestor de efectos de sonido sintetizados mediante AudioTrack.
 *
 * Permite reproducir retroalimentación auditiva para movimientos,
 * victoria, derrota y empate sin requerir archivos multimedia externos.
 */
class SoundManager {

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Reproduce el efecto de sonido al presionar el jugador humano.
     */
    fun playHumanMove(soundEnabled: Boolean) {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequency = 523.25, durationMs = 100) // Nota C5
        }
    }

    /**
     * Reproduce el efecto de sonido al mover la CPU.
     */
    fun playComputerMove(soundEnabled: Boolean) {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequency = 392.00, durationMs = 120) // Nota G4
        }
    }

    /**
     * Reproduce la secuencia de victoria.
     */
    fun playWinSound(soundEnabled: Boolean) {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequency = 523.25, durationMs = 100) // C5
            playTone(frequency = 659.25, durationMs = 100) // E5
            playTone(frequency = 783.99, durationMs = 250) // G5
        }
    }

    /**
     * Reproduce el tono de derrota.
     */
    fun playLoseSound(soundEnabled: Boolean) {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequency = 392.00, durationMs = 120) // G4
            playTone(frequency = 329.63, durationMs = 120) // E4
            playTone(frequency = 261.63, durationMs = 250) // C4
        }
    }

    /**
     * Reproduce el tono de empate.
     */
    fun playTieSound(soundEnabled: Boolean) {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequency = 329.63, durationMs = 150)
            playTone(frequency = 329.63, durationMs = 200)
        }
    }

    /**
     * Genera y sintetiza una onda sinusoidal utilizando AudioTrack.
     */
    private fun playTone(frequency: Double, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate) / 1000
        val sample = DoubleArray(numSamples)
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            sample[i] = sin(2.0 * Math.PI * i.toDouble() / (sampleRate / frequency))
        }

        var idx = 0
        for (dVal in sample) {
            val valInt = (dVal * 32767).toInt().toShort()
            generatedSnd[idx++] = (valInt.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (valInt.toInt() and 0xff00 ushr 8).toByte()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSnd.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
    }
}
