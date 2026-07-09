package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.VibeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: VibeViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val vibeLevel by viewModel.vibeLevel.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()
    val currentFiles by viewModel.currentFiles.collectAsStateWithLifecycle()
    val selectedFile by viewModel.selectedFile.collectAsStateWithLifecycle()
    val currentMessages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isCompiling by viewModel.isCompiling.collectAsStateWithLifecycle()
    val compilationLog by viewModel.compilationLog.collectAsStateWithLifecycle()
    val isSimulationRunning by viewModel.isSimulationRunning.collectAsStateWithLifecycle()

    val qubitTheta by viewModel.qubitTheta.collectAsStateWithLifecycle()
    val qubitPhi by viewModel.qubitPhi.collectAsStateWithLifecycle()
    val appliedGates by viewModel.appliedGates.collectAsStateWithLifecycle()
    val qmlTrainingActive by viewModel.qmlTrainingActive.collectAsStateWithLifecycle()
    val qmlEpoch by viewModel.qmlEpoch.collectAsStateWithLifecycle()
    val qmlLoss by viewModel.qmlLoss.collectAsStateWithLifecycle()
    val qmlAccuracy by viewModel.qmlAccuracy.collectAsStateWithLifecycle()
    val qmlLossHistory by viewModel.qmlLossHistory.collectAsStateWithLifecycle()

    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var newProjectName by remember { mutableStateOf("") }
    var newProjectDesc by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgLight),
        topBar = {
            VibeHeader(
                currentProject = currentProject,
                onAddProjectClick = { showCreateProjectDialog = true },
                projects = projects,
                onSelectProject = { id -> viewModel.selectProject(id) }
            )
        },
        bottomBar = {
            VibeBottomNavigation(
                currentTab = currentTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
            )
        },
        containerColor = ColorBgLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ColorBgLight)
        ) {
            when (currentTab) {
                "Project" -> {
                    ProjectTabScreen(
                        currentFiles = currentFiles,
                        selectedFile = selectedFile,
                        onSelectFile = { id -> viewModel.selectFile(id) },
                        onUpdateFileContent = { id, content -> viewModel.updateFileContent(id, content) },
                        onCreateFile = { name, content, lang -> viewModel.createProjectFile(name, content, lang) },
                        onDeleteFile = { file -> viewModel.deleteProjectFile(file) }
                    )
                }
                "Vibe" -> {
                    VibeTabScreen(
                        currentMessages = currentMessages,
                        isGenerating = isGenerating,
                        onSendVibe = { text -> viewModel.sendVibe(text) },
                        currentProject = currentProject
                    )
                }
                "Ship" -> {
                    ShipTabScreen(
                        isCompiling = isCompiling,
                        compilationLog = compilationLog,
                        isSimulationRunning = isSimulationRunning,
                        onCompile = { viewModel.compileAndVerifyProject() },
                        onStopSimulation = { viewModel.stopSimulation() },
                        currentFiles = currentFiles,
                        currentProject = currentProject,
                        qubitTheta = qubitTheta,
                        qubitPhi = qubitPhi,
                        appliedGates = appliedGates,
                        qmlTrainingActive = qmlTrainingActive,
                        qmlEpoch = qmlEpoch,
                        qmlLoss = qmlLoss,
                        qmlAccuracy = qmlAccuracy,
                        qmlLossHistory = qmlLossHistory,
                        onApplyGate = { gate -> viewModel.applyGate(gate) },
                        onStartQmlTraining = { viewModel.startQmlTraining() },
                        onStopQmlTraining = { viewModel.stopQmlTraining() }
                    )
                }
                "Settings" -> {
                    SettingsTabScreen(
                        apiKey = apiKey,
                        onApiKeyChange = { key -> viewModel.setApiKey(key) },
                        vibeLevel = vibeLevel,
                        onVibeLevelChange = { lvl -> viewModel.setVibeLevel(lvl) },
                        currentProject = currentProject,
                        filesCount = currentFiles.size,
                        msgCount = currentMessages.size
                    )
                }
            }
        }
    }

    // Create New Workspace Dialog
    if (showCreateProjectDialog) {
        AlertDialog(
            onDismissRequest = { showCreateProjectDialog = false },
            title = { Text("Create New Vibe Workspace") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        label = { Text("Workspace Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorIndigo600,
                            cursorColor = ColorIndigo600
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_project_name")
                    )

                    OutlinedTextField(
                        value = newProjectDesc,
                        onValueChange = { newProjectDesc = it },
                        label = { Text("Short Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorIndigo600,
                            cursorColor = ColorIndigo600
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_project_desc")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProjectName.isNotBlank()) {
                            viewModel.createProject(newProjectName, newProjectDesc)
                            newProjectName = ""
                            newProjectDesc = ""
                            showCreateProjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorIndigo600)
                ) {
                    Text("Create Workspace")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProjectDialog = false }) {
                    Text("Cancel", color = ColorSlate700)
                }
            },
            containerColor = Color.White
        )
    }
}
