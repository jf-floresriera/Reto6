package com.example.myapplication.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.GameState
import com.example.myapplication.domain.GameWinner
import com.example.myapplication.ui.components.DifficultyDialog
import com.example.myapplication.ui.components.GameBoardView
import com.example.myapplication.ui.components.ScoreBoardCard
import com.example.myapplication.ui.components.VictoryDialog

/**
 * ============================================================================
 * CONCEPTO EDUCATIVO: DISEÑO ADAPTATIVO (PORTRAIT VS. LANDSCAPE) EN RETO 6
 * ============================================================================
 * 'TicTacToeScreen' adapta su estructura visual dependiendo de la orientación
 * de la pantalla obtenida desde 'LocalConfiguration.current.orientation':
 *
 * 1. **Modo Retrato (Portrait):** Disposición en Columna vertical.
 * 2. **Modo Paisaje (Landscape):** Disposición en Fila de dos columnas (Pantalla dividida)
 *    para evitar que el tablero quede aplastado o recortado al girar el dispositivo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Triqui / Tic-Tac-Toe",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.showDifficultyDialog(true) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración de Dificultad"
                        )
                    }
                    IconButton(onClick = { viewModel.resetBoard() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar Tablero"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLandscape) {
                // DISPOSICIÓN PARA MODO HORIZONTAL (LANDSCAPE)
                LandscapeContent(
                    uiState = uiState,
                    onTileClick = viewModel::onTileClicked,
                    onResetBoard = viewModel::resetBoard,
                    onResetScores = viewModel::resetScores,
                    onDifficultyClick = { viewModel.showDifficultyDialog(true) }
                )
            } else {
                // DISPOSICIÓN PARA MODO VERTICAL (PORTRAIT)
                PortraitContent(
                    uiState = uiState,
                    onTileClick = viewModel::onTileClicked,
                    onResetBoard = viewModel::resetBoard,
                    onResetScores = viewModel::resetScores,
                    onDifficultyClick = { viewModel.showDifficultyDialog(true) }
                )
            }

            // Diálogo de final de juego (Victoria / Derrota / Empate)
            if (uiState.winner != GameWinner.NONE) {
                VictoryDialog(
                    winner = uiState.winner,
                    onPlayAgain = { viewModel.resetBoard() },
                    onDismiss = { /* Permite cerrar y revisar el tablero ganandor */ }
                )
            }

            // Diálogo de selección de dificultad
            if (uiState.showDifficultyDialog) {
                DifficultyDialog(
                    currentDifficulty = uiState.difficulty,
                    onDifficultySelected = { level -> viewModel.setDifficulty(level) },
                    onDismiss = { viewModel.showDifficultyDialog(false) }
                )
            }
        }
    }
}

/**
 * Vista para Modo Vertical (Portrait)
 */
@Composable
private fun PortraitContent(
    uiState: GameState,
    onTileClick: (Int) -> Unit,
    onResetBoard: () -> Unit,
    onResetScores: () -> Unit,
    onDifficultyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Marcador histórico persistente
        ScoreBoardCard(
            humanWins = uiState.humanWins,
            computerWins = uiState.computerWins,
            ties = uiState.ties,
            difficulty = uiState.difficulty,
            onDifficultyClick = onDifficultyClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Indicador de turno actual
        TurnStatusCard(uiState = uiState)

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Tablero 3x3 de Triqui
        GameBoardView(
            board = uiState.board,
            winningLine = uiState.winningLine,
            onTileClick = onTileClick,
            modifier = Modifier.fillMaxWidth(0.95f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Botones de Acción
        ActionButtonsRow(
            onResetBoard = onResetBoard,
            onResetScores = onResetScores
        )
    }
}

/**
 * Vista Adaptativa para Modo Horizontal (Landscape)
 */
@Composable
private fun LandscapeContent(
    uiState: GameState,
    onTileClick: (Int) -> Unit,
    onResetBoard: () -> Unit,
    onResetScores: () -> Unit,
    onDifficultyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna Izquierda: Marcador, Estado y Botones
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ScoreBoardCard(
                humanWins = uiState.humanWins,
                computerWins = uiState.computerWins,
                ties = uiState.ties,
                difficulty = uiState.difficulty,
                onDifficultyClick = onDifficultyClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            TurnStatusCard(uiState = uiState)

            Spacer(modifier = Modifier.height(16.dp))

            ActionButtonsRow(
                onResetBoard = onResetBoard,
                onResetScores = onResetScores
            )
        }

        // Columna Derecha: Tablero Centrado
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            GameBoardView(
                board = uiState.board,
                winningLine = uiState.winningLine,
                onTileClick = onTileClick,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}

/**
 * Tarjeta de estado de turno actual
 */
@Composable
private fun TurnStatusCard(uiState: GameState) {
    val statusText = when {
        uiState.isGameOver -> "Partida Finalizada"
        uiState.isCpuThinking -> "Pensando movimiento CPU..."
        uiState.isHumanTurn -> "Tu Turno (X)"
        else -> "Turno de la CPU (O)"
    }

    val containerColor = when {
        uiState.isGameOver -> MaterialTheme.colorScheme.secondaryContainer
        uiState.isCpuThinking -> MaterialTheme.colorScheme.tertiaryContainer
        uiState.isHumanTurn -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Text(
            text = statusText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/**
 * Botones de Reinicio de Partida y Reset de Marcadores
 */
@Composable
private fun ActionButtonsRow(
    onResetBoard: () -> Unit,
    onResetScores: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onResetBoard,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Nueva Partida", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedButton(
            onClick = onResetScores,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text(text = "Reiniciar Marcador", fontSize = 13.sp)
        }
    }
}
