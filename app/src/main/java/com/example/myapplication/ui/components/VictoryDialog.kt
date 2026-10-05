package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.GameWinner

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: DIÁLOGOS DE ALERTA Y FEEDBACK AL USUARIO
 * ============================================================================
 * 'VictoryDialog' notifica el resultado del juego (Ganaste, Perdiste o Empate)
 * de forma contextual utilizando 'AlertDialog' de Material 3.
 */
@Composable
fun VictoryDialog(
    winner: GameWinner,
    onPlayAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    if (winner == GameWinner.NONE) return

    val (title, message, emoji) = when (winner) {
        GameWinner.HUMAN -> Triple("¡Felicidades! 🎉", "¡Has derrotado a la Inteligencia Artificial!", "🏆")
        GameWinner.COMPUTER -> Triple("¡CPU Victoriosa! 🤖", "La computadora ha ganado esta partida. ¡Inténtalo de nuevo!", "💻")
        GameWinner.TIE -> Triple("¡Empate Perfecto! 🤝", "Ha sido una partida muy disputada.", "⚖️")
        GameWinner.NONE -> Triple("", "", "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = emoji,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onPlayAgain,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Jugar de Nuevo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ver Tablero")
            }
        }
    )
}
