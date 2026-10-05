package com.example.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.BoardTile

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: DECLARATIVE UI & COMPOSITION IN JETPACK COMPOSE
 * ============================================================================
 * 'GameBoardView' renderiza la cuadrícula 3x3 del Triqui de forma puramente declarativa.
 *
 * Ventajas del diseño moderno en Jetpack Compose:
 * 1. Sin necesidad de XML ni findViewById.
 * 2. Recomposición automática cuando cambia la lista 'board'.
 * 3. Animación fluida de escala y color para las marcas 'X' y 'O'.
 * 4. Resaltado visual en dorado para la combinación ganadora.
 */
@Composable
fun GameBoardView(
    board: List<BoardTile>,
    winningLine: List<Int>?,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val isWinningTile = winningLine?.contains(index) == true

                        TileCell(
                            tile = board[index],
                            isWinningTile = isWinningTile,
                            onClick = { onTileClick(index) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TileCell(
    tile: BoardTile,
    isWinningTile: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Animación de escala al aparecer la marca
    val scale by animateFloatAsState(
        targetValue = if (tile != BoardTile.EMPTY) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TileScale"
    )

    // Colores personalizados
    val humanColor = Color(0xFF2196F3)    // Azul vibrante para X
    val computerColor = Color(0xFFE91E63) // Rosa/Rojo para O
    val winningGold = Color(0xFFFFD700)   // Dorado para línea ganadora

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isWinningTile -> winningGold.copy(alpha = 0.35f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "TileBgColor"
    )

    val borderColor = when {
        isWinningTile -> winningGold
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val symbolColor = when (tile) {
        BoardTile.HUMAN -> humanColor
        BoardTile.COMPUTER -> computerColor
        BoardTile.EMPTY -> Color.Transparent
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isWinningTile) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tile.symbol,
            color = symbolColor,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.scale(scale)
        )
    }
}
