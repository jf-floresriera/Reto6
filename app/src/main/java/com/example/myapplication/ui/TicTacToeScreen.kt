package com.example.myapplication.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.AppTheme
import com.example.myapplication.domain.GameState
import com.example.myapplication.domain.GameWinner
import com.example.myapplication.ui.components.DifficultyDialog
import com.example.myapplication.ui.components.GameBoardView
import com.example.myapplication.ui.components.ScoreBoardCard
import com.example.myapplication.ui.components.ThemeDialog
import com.example.myapplication.ui.components.VictoryDialog

/**
 * Pantalla principal responsiva de la aplicación con soporte para temas de color.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Aplicar paleta de colores dinámicamente según el tema seleccionado
    val colorScheme = when (uiState.selectedTheme) {
        AppTheme.CLASSIC -> lightColorScheme(
            primary = Color(0xFF2196F3),
            primaryContainer = Color(0xFFE3F2FD),
            onPrimaryContainer = Color(0xFF0D47A1)
        )
        AppTheme.OCEAN -> lightColorScheme(
            primary = Color(0xFF009688),
            primaryContainer = Color(0xFFE0F2F1),
            onPrimaryContainer = Color(0xFF004D40)
        )
        AppTheme.DARK_NEON -> darkColorScheme(
            primary = Color(0xFF00E676),
            primaryContainer = Color(0xFF1B5E20),
            onPrimaryContainer = Color(0xFFB9F6CA)
        )
        AppTheme.FOREST -> lightColorScheme(
            primary = Color(0xFF4CAF50),
            primaryContainer = Color(0xFFE8F5E9),
            onPrimaryContainer = Color(0xFF1B5E20)
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
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
                        // Conmutador de Sonido
                        IconButton(onClick = { viewModel.toggleSound() }) {
                            Icon(
                                imageVector = if (uiState.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Activar o Desactivar Sonido"
                            )
                        }
                        // Selector de Tema
                        IconButton(onClick = { viewModel.showThemeDialog(true) }) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Seleccionar Tema Visual"
                            )
                        }
                        // Selector de Dificultad
                        IconButton(onClick = { viewModel.showDifficultyDialog(true) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configuración de Dificultad"
                            )
                        }
                        // Reiniciar Tablero
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
                    LandscapeContent(
                        uiState = uiState,
                        onTileClick = viewModel::onTileClicked,
                        onResetBoard = viewModel::resetBoard,
                        onResetScores = viewModel::resetScores,
                        onDifficultyClick = { viewModel.showDifficultyDialog(true) },
                        onSoundToggleClick = viewModel::toggleSound,
                        onThemeClick = { viewModel.showThemeDialog(true) }
                    )
                } else {
                    PortraitContent(
                        uiState = uiState,
                        onTileClick = viewModel::onTileClicked,
                        onResetBoard = viewModel::resetBoard,
                        onResetScores = viewModel::resetScores,
                        onDifficultyClick = { viewModel.showDifficultyDialog(true) },
                        onSoundToggleClick = viewModel::toggleSound,
                        onThemeClick = { viewModel.showThemeDialog(true) }
                    )
                }

                // Diálogos Modales
                if (uiState.winner != GameWinner.NONE) {
                    VictoryDialog(
                        winner = uiState.winner,
                        onPlayAgain = { viewModel.resetBoard() },
                        onDismiss = { }
                    )
                }

                if (uiState.showDifficultyDialog) {
                    DifficultyDialog(
                        currentDifficulty = uiState.difficulty,
                        onDifficultySelected = { level -> viewModel.setDifficulty(level) },
                        onDismiss = { viewModel.showDifficultyDialog(false) }
                    )
                }

                if (uiState.showThemeDialog) {
                    ThemeDialog(
                        currentTheme = uiState.selectedTheme,
                        onThemeSelected = { theme -> viewModel.setTheme(theme) },
                        onDismiss = { viewModel.showThemeDialog(false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PortraitContent(
    uiState: GameState,
    onTileClick: (Int) -> Unit,
    onResetBoard: () -> Unit,
    onResetScores: () -> Unit,
    onDifficultyClick: () -> Unit,
    onSoundToggleClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ScoreBoardCard(
            humanWins = uiState.humanWins,
            computerWins = uiState.computerWins,
            ties = uiState.ties,
            difficulty = uiState.difficulty,
            soundEnabled = uiState.soundEnabled,
            selectedTheme = uiState.selectedTheme,
            onDifficultyClick = onDifficultyClick,
            onSoundToggleClick = onSoundToggleClick,
            onThemeClick = onThemeClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        TurnStatusCard(uiState = uiState)

        Spacer(modifier = Modifier.height(12.dp))

        GameBoardView(
            board = uiState.board,
            winningLine = uiState.winningLine,
            onTileClick = onTileClick,
            modifier = Modifier.fillMaxWidth(0.95f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButtonsRow(
            onResetBoard = onResetBoard,
            onResetScores = onResetScores
        )
    }
}

@Composable
private fun LandscapeContent(
    uiState: GameState,
    onTileClick: (Int) -> Unit,
    onResetBoard: () -> Unit,
    onResetScores: () -> Unit,
    onDifficultyClick: () -> Unit,
    onSoundToggleClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                soundEnabled = uiState.soundEnabled,
                selectedTheme = uiState.selectedTheme,
                onDifficultyClick = onDifficultyClick,
                onSoundToggleClick = onSoundToggleClick,
                onThemeClick = onThemeClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            TurnStatusCard(uiState = uiState)

            Spacer(modifier = Modifier.height(16.dp))

            ActionButtonsRow(
                onResetBoard = onResetBoard,
                onResetScores = onResetScores
            )
        }

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

@Composable
private fun TurnStatusCard(uiState: GameState) {
    val statusText = when {
        uiState.isGameOver -> "Partida Finalizada"
        uiState.isCpuThinking -> "Procesando movimiento de la CPU..."
        uiState.isHumanTurn -> "Turno del Jugador (X)"
        else -> "Turno de la Computadora (O)"
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
