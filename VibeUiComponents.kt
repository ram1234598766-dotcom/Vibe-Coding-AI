package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectFile
import com.example.data.VibeMessage
import com.example.data.VibeProject
import com.example.viewmodel.VibeViewModel
import kotlinx.coroutines.delay
import kotlin.math.*

// Theme Colors
val ColorSlate950 = Color(0xFF020617)
val ColorSlate900 = Color(0xFF0F172A)
val ColorSlate700 = Color(0xFF334155)
val ColorSlate400 = Color(0xFF94A3B8)
val ColorSlate300 = Color(0xFFCBD5E1)
val ColorSlate200 = Color(0xFFE2E8F0)
val ColorIndigo600 = Color(0xFF4F46E5)
val ColorIndigo500 = Color(0xFF6366F1)
val ColorIndigo100 = Color(0xFFE0E7FF)
val ColorLavenderBg = Color(0xFFEADDFF)
val ColorBgLight = Color(0xFFF3F4F9)

@Composable
fun VibeHeader(
    currentProject: VibeProject?,
    onAddProjectClick: () -> Unit,
    projects: List<VibeProject>,
    onSelectProject: (Int) -> Unit
) {
    var showProjectMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White.copy(alpha = 0.85f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo and Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorIndigo600)
                    .clickable { showProjectMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.clickable { showProjectMenu = true }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentProject?.name ?: "VibeCode AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorSlate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Project",
                        tint = ColorSlate700,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "SYNTHESIZING IDEA #${currentProject?.id ?: 42}",
                    fontSize = 10.sp,
                    color = ColorIndigo600,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
        }

        DropdownMenu(
            expanded = showProjectMenu,
            onDismissRequest = { showProjectMenu = false },
            modifier = Modifier.background(Color.White)
        ) {
            Text(
                text = "My Projects",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorSlate400,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            projects.forEach { proj ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(proj.name, fontWeight = FontWeight.Medium, color = ColorSlate900)
                            Text(proj.description, fontSize = 11.sp, color = ColorSlate400, maxLines = 1)
                        }
                    },
                    onClick = {
                        onSelectProject(proj.id)
                        showProjectMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = ColorIndigo600)
                    }
                )
            }
            Divider(color = ColorSlate200)
            DropdownMenuItem(
                text = { Text("Create New Workspace", color = ColorIndigo600, fontWeight = FontWeight.Bold) },
                onClick = {
                    onAddProjectClick()
                    showProjectMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ColorIndigo600)
                }
            )
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ColorSlate200)
                .border(1.dp, ColorSlate300, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "JD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorSlate700
            )
        }
    }
}

@Composable
fun VibeBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color.White)
            .drawBehind {
                // Top Border
                drawLine(
                    color = ColorSlate200,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val tabs = listOf(
            TabItem("Project", "📁", Icons.Outlined.Folder, Icons.Default.Folder),
            TabItem("Vibe", "🪄", Icons.Outlined.AutoAwesome, Icons.Default.AutoAwesome),
            TabItem("Ship", "🚀", Icons.Outlined.RocketLaunch, Icons.Default.RocketLaunch),
            TabItem("Settings", "⚙️", Icons.Outlined.Settings, Icons.Default.Settings)
        )

        tabs.forEach { tab ->
            val isSelected = currentTab == tab.name
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(tab.name) }
                    .testTag("nav_tab_${tab.name.lowercase()}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) ColorIndigo100 else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.emoji,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tab.name,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) ColorIndigo600 else ColorSlate400,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

data class TabItem(
    val name: String,
    val emoji: String,
    val outlineIcon: ImageVector,
    val filledIcon: ImageVector
)

@Composable
fun ProjectTabScreen(
    currentFiles: List<ProjectFile>,
    selectedFile: ProjectFile?,
    onSelectFile: (Int) -> Unit,
    onUpdateFileContent: (Int, String) -> Unit,
    onCreateFile: (String, String, String) -> Unit,
    onDeleteFile: (ProjectFile) -> Unit
) {
    var showNewFileDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editingText by remember { mutableStateOf("") }

    // Keep editing content in sync when selected file changes
    LaunchedEffect(selectedFile) {
        editingText = selectedFile?.content ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // File tabs/chips row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentFiles) { file ->
                    val isFileSelected = file.id == selectedFile?.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFileSelected) ColorIndigo600 else Color.White)
                            .border(
                                1.dp,
                                if (isFileSelected) ColorIndigo600 else ColorSlate300,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onSelectFile(file.id)
                                isEditing = false
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = when (file.language) {
                                    "typescript", "javascript" -> "⚡️"
                                    "kotlin" -> "☕️"
                                    "css" -> "🎨"
                                    else -> "📄"
                                },
                                fontSize = 12.sp
                            )
                            Text(
                                text = file.fileName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFileSelected) Color.White else ColorSlate700
                            )
                            if (currentFiles.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete File",
                                    tint = if (isFileSelected) Color.White.copy(alpha = 0.7f) else ColorSlate400,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onDeleteFile(file) }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { showNewFileDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ColorIndigo600,
                    contentColor = Color.White
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New File", modifier = Modifier.size(18.dp))
            }
        }

        // Active Editor Area (Mac OS style block)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ColorSlate900)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header of Editor
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Traffic lights
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    }

                    // Tab Title File Name
                    Text(
                        text = selectedFile?.fileName ?: "no_file.txt",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ColorSlate400,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )

                    // Edit Toggle Button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEditing) {
                            TextButton(
                                onClick = {
                                    selectedFile?.let { onUpdateFileContent(it.id, editingText) }
                                    isEditing = false
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Green)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", fontSize = 11.sp)
                            }
                        }
                        TextButton(
                            onClick = {
                                if (isEditing) {
                                    editingText = selectedFile?.content ?: ""
                                }
                                isEditing = !isEditing
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = ColorIndigo100)
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Undo else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Cancel" else "Edit",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isEditing) "Cancel" else "Edit", fontSize = 11.sp)
                        }
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.05f))

                // Scrollable Code Display/Editor
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = editingText,
                            onValueChange = { editingText = it },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .testTag("code_editor_input"),
                            cursorBrush = Brush.verticalGradient(listOf(Color.White, Color.White))
                        )
                    } else {
                        // Styled Monospaced Read-Only Display
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            val lines = selectedFile?.content?.lines() ?: listOf("// No content available.")
                            lines.forEachIndexed { idx, line ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = String.format("%02d", idx + 1),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = ColorSlate700,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = line,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = getHighlighterColorForLine(line),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New File Dialog
    if (showNewFileDialog) {
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text("Create New Vibe File") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("File Name (e.g. index.html)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorIndigo600,
                            cursorColor = ColorIndigo600
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_file_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            val ext = newFileName.substringAfterLast('.', "")
                            val lang = when (ext) {
                                "kt" -> "kotlin"
                                "css" -> "css"
                                "html" -> "html"
                                "js", "jsx" -> "javascript"
                                "tsx", "ts" -> "typescript"
                                else -> "text"
                            }
                            onCreateFile(newFileName, "// New Vibe file: $newFileName\n", lang)
                            newFileName = ""
                            showNewFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorIndigo600)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) {
                    Text("Cancel", color = ColorSlate700)
                }
            },
            containerColor = Color.White
        )
    }
}

// Very simple, ultra-performant code block highlighters
fun getHighlighterColorForLine(line: String): Color {
    val trimmed = line.trim()
    return when {
        trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*") -> Color(0xFF64748B) // Comment
        trimmed.contains("import ") || trimmed.contains("package ") -> Color(0xFF818CF8) // System imports
        trimmed.contains("const ") || trimmed.contains("let ") || trimmed.contains("val ") || trimmed.contains("var ") -> Color(0xFFF472B6) // Declarations
        trimmed.contains("export ") || trimmed.contains("default ") || trimmed.contains("class ") || trimmed.contains("fun ") || trimmed.contains("function ") -> Color(0xFF38BDF8) // Keywords
        trimmed.contains("<") && trimmed.contains(">") -> Color(0xFFFCA5A5) // JSX/HTML tags
        else -> Color(0xFFE2E8F0) // normal code
    }
}

@Composable
fun VibeTabScreen(
    currentMessages: List<VibeMessage>,
    isGenerating: Boolean,
    onSendVibe: (String) -> Unit,
    currentProject: VibeProject?
) {
    var promptInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto scroll chat to bottom when messages list size changes
    LaunchedEffect(currentMessages.size) {
        if (currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Project Aura indicator Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ColorSlate900)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Aura Matrix: ${currentProject?.auraLevel ?: "FLOW"}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorIndigo100,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${currentProject?.vibeScore ?: 85}% Alignment Score",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "🔴 Quantum Co-generation Active • Unlimited Tokens",
                    fontSize = 9.sp,
                    color = Color(0xFF10B981),
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (currentProject?.auraLevel) {
                        "PURE" -> "✨"
                        "FLOW" -> "✦"
                        "HYPER" -> "⚡️"
                        else -> "🔮"
                    },
                    fontSize = 20.sp
                )
            }
        }

        // Lavender Chat Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ColorLavenderBg)
                .border(1.dp, ColorIndigo100, RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Scrollable Chat area
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentMessages) { msg ->
                        val isUser = msg.sender == "USER"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            if (!isUser) {
                                // AI Avatar representation
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, ColorIndigo100, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(msg.auraSymbol, fontSize = 14.sp, color = ColorIndigo600)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            // Message Bubble
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 260.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) ColorIndigo600 else Color.White)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = msg.message,
                                    fontSize = 13.sp,
                                    color = if (isUser) Color.White else ColorSlate900,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    if (isGenerating) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🔮", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .padding(12.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("AI is writing...", fontSize = 12.sp, color = ColorSlate400, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Suggestion Chips Row for High Architecture
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val suggestions = listOf(
                        "🌌 Train QML Classifier Layer",
                        "🔮 Superpose Qubits |ψ⟩ = α|0⟩ + β|1⟩",
                        "⚡️ Entangle Qubits via CNOT gates",
                        "🧬 Initialize Variational VQC Ansatz",
                        "🧩 Generate Quantum Neural Network"
                    )
                    suggestions.forEach { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, ColorIndigo100, RoundedCornerShape(12.dp))
                                .clickable { promptInput = suggestion }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorIndigo600
                            )
                        }
                    }
                }

                // Input box styled exactly like theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(1.dp, ColorIndigo100, RoundedCornerShape(18.dp))
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡️", fontSize = 16.sp)
                    }

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Describe your next vibe...", fontSize = 13.sp, color = ColorSlate400) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vibe_prompt_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = ColorIndigo600
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (promptInput.isNotBlank() && !isGenerating) {
                                onSendVibe(promptInput)
                                promptInput = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        })
                    )

                    IconButton(
                        onClick = {
                            if (promptInput.isNotBlank() && !isGenerating) {
                                onSendVibe(promptInput)
                                promptInput = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        },
                        enabled = promptInput.isNotBlank() && !isGenerating,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = ColorIndigo600,
                            disabledContainerColor = ColorSlate200,
                            contentColor = Color.White,
                            disabledContentColor = ColorSlate400
                        ),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("vibe_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Send vibe",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShipTabScreen(
    isCompiling: Boolean,
    compilationLog: List<String>,
    isSimulationRunning: Boolean,
    onCompile: () -> Unit,
    onStopSimulation: () -> Unit,
    currentFiles: List<ProjectFile>,
    currentProject: VibeProject?,
    qubitTheta: Float = 0f,
    qubitPhi: Float = 0f,
    appliedGates: List<String> = emptyList(),
    qmlTrainingActive: Boolean = false,
    qmlEpoch: Int = 0,
    qmlLoss: Float = 0.85f,
    qmlAccuracy: Float = 0.50f,
    qmlLossHistory: List<Float> = emptyList(),
    onApplyGate: (String) -> Unit = {},
    onStartQmlTraining: () -> Unit = {},
    onStopQmlTraining: () -> Unit = {}
) {
    val logScrollState = rememberScrollState()

    // Auto-scroll logs as they load
    LaunchedEffect(compilationLog.size) {
        logScrollState.animateScrollTo(logScrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isSimulationRunning) {
            // Deploy / Ship landing panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(1.dp, ColorSlate200, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ColorIndigo100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = ColorIndigo600, modifier = Modifier.size(32.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ship Your Active Vibe",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorSlate900
                        )
                        Text(
                            text = "Bundle, compile, and run the hot-reload simulation container of your project files in real-time.",
                            fontSize = 12.sp,
                            color = ColorSlate700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Button(
                        onClick = onCompile,
                        enabled = !isCompiling,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorIndigo600),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ship_compile_button")
                    ) {
                        if (isCompiling) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Deploy Simulation Sandbox", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Terminal compilation box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ColorSlate950)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ColorSlate700))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ColorSlate700))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ColorSlate700))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("vibe_sandbox_compiler.sh", fontSize = 10.sp, color = ColorSlate400, fontFamily = FontFamily.Monospace)
                    }

                    Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 10.dp))

                    if (compilationLog.isEmpty()) {
                        Text(
                            text = "Ready to ship... Click Deploy above to initiate the synthesis.",
                            fontFamily = FontFamily.Monospace,
                            color = ColorSlate700,
                            fontSize = 12.sp
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            compilationLog.forEach { logLine ->
                                Text(
                                    text = logLine,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = if (logLine.contains("❌") || logLine.contains("🛑")) Color(0xFFEF4444)
                                    else if (logLine.contains("🟢") || logLine.contains("🚀")) Color(0xFF10B981)
                                    else Color(0xFFCBD5E1),
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Live sandbox simulator screen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ColorSlate900)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                            Text("SIMULATOR CONTAINER ACTIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Green, fontFamily = FontFamily.Monospace)
                        }
                        IconButton(onClick = onStopSimulation, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop simulation", tint = Color.Red)
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 10.dp))

                    // Multi-mode Selector Tabs
                    var simMode by remember { mutableStateOf(0) } // 0 = QML Neural Classifier, 1 = Bloch Sphere, 2 = Quantum Oscillation

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorSlate950)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("🌌 QML Classifier", "🔮 Bloch Qubits", "🌊 Wave Oscillation").forEachIndexed { index, modeTitle ->
                            val isSelected = simMode == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ColorIndigo600 else Color.Transparent)
                                    .clickable { simMode = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = modeTitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else ColorSlate400
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated live runtime interface based on files in project
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (simMode) {
                            0 -> QmlClassifierSimulation(
                                qmlTrainingActive = qmlTrainingActive,
                                qmlEpoch = qmlEpoch,
                                qmlLoss = qmlLoss,
                                qmlAccuracy = qmlAccuracy,
                                qmlLossHistory = qmlLossHistory,
                                onStartTraining = onStartQmlTraining,
                                onStopTraining = onStopQmlTraining
                            )
                            1 -> BlochSphereQubitSimulation(
                                qubitTheta = qubitTheta,
                                qubitPhi = qubitPhi,
                                appliedGates = appliedGates,
                                onApplyGate = onApplyGate
                            )
                            else -> {
                                val isWaveOscillation = currentFiles.any { it.fileName.contains("quantum") || it.fileName.contains("wave") }
                                if (isWaveOscillation) {
                                    WaveOscillationSimulation()
                                } else {
                                    VibeCheckWidgetSimulation(currentProject?.auraLevel ?: "FLOW")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaveOscillationSimulation() {
    var time by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            time += 0.05f
            delay(16) // ~60fps
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quantum Interference Simulator", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text("Active wave amplitude metrics", color = ColorSlate400, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSlate950)
                .border(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()
                val midY = size.height / 2f
                val amplitude = 50f * scale

                for (x in 0 until size.width.toInt() step 2) {
                    val angle = (x.toFloat() / size.width) * 4f * 3.14159f + time
                    val y = midY + sin(angle) * amplitude
                    if (x == 0) {
                        path.moveTo(0f, y)
                    } else {
                        path.lineTo(x.toFloat(), y)
                    }
                }

                drawPath(
                    path = path,
                    color = ColorIndigo500,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw secondary overlapping particle wave
                val secPath = Path()
                for (x in 0 until size.width.toInt() step 4) {
                    val angle = (x.toFloat() / size.width) * 6f * 3.14159f - time * 1.5f
                    val y = midY + sin(angle) * (amplitude * 0.4f)
                    if (x == 0) {
                        secPath.moveTo(0f, y)
                    } else {
                        secPath.lineTo(x.toFloat(), y)
                    }
                }
                drawPath(
                    path = secPath,
                    color = Color(0xFF10B981),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { scale = (scale + 0.2f).coerceAtMost(2f) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorIndigo600),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Amplify", fontSize = 11.sp)
            }
            Button(
                onClick = { scale = (scale - 0.2f).coerceAtLeast(0.2f) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorSlate700),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Dampen", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun VibeCheckWidgetSimulation(auraLevel: String) {
    var stateToggle by remember { mutableStateOf(true) }
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Vibe Checker Widget", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Live React simulator container", color = ColorSlate400, fontSize = 11.sp, modifier = Modifier.padding(bottom = 20.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .drawBehind {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(ColorIndigo500, Color(0xFFE0E7FF), ColorIndigo500)),
                        startAngle = rotation,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(ColorSlate950),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (stateToggle) "PURE" else "FLOW",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (stateToggle) "✨ alignment active" else "⚡️ hyper reactive",
                        color = if (stateToggle) Color(0xFF10B981) else ColorIndigo500,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { stateToggle = !stateToggle },
            colors = ButtonDefaults.buttonColors(containerColor = ColorIndigo600),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(180.dp)
        ) {
            Text("Trigger React State Toggle", fontSize = 12.sp)
        }
    }
}

@Composable
fun SettingsTabScreen(
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    vibeLevel: String,
    onVibeLevelChange: (String) -> Unit,
    currentProject: VibeProject?,
    filesCount: Int,
    msgCount: Int
) {
    var showApiKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // API settings card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(1.dp, ColorSlate200, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("AI Engine Credentials", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorSlate900)
                Text(
                    "Input your custom Gemini API key to run real-time generation. If empty, the app uses local sandbox simulation.",
                    fontSize = 11.sp,
                    color = ColorSlate700
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onApiKeyChange,
                    label = { Text("Gemini API Key", fontSize = 12.sp) },
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = ColorSlate700
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorIndigo600,
                        cursorColor = ColorIndigo600
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("api_key_field")
                )
            }
        }

        // Vibe tuning segment selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(1.dp, ColorSlate200, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Vibe Alignment Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorSlate900)
                Text(
                    "Adjusts generation temperature and tone parameters matching your creative mood.",
                    fontSize = 11.sp,
                    color = ColorSlate700
                )

                val vibeLevels = listOf("Chill", "Flow", "Hyper", "Pure")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorSlate200)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    vibeLevels.forEach { lvl ->
                        val isSelected = vibeLevel == lvl
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ColorIndigo600 else Color.Transparent)
                                .clickable { onVibeLevelChange(lvl) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lvl,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else ColorSlate700
                            )
                        }
                    }
                }
            }
        }

        // Project statistics
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(1.dp, ColorSlate200, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Project Analytics", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorSlate900)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Files", fontSize = 11.sp, color = ColorSlate400)
                        Text("$filesCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorIndigo600)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Vibe Actions", fontSize = 11.sp, color = ColorSlate400)
                        Text("$msgCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorIndigo600)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Alignment", fontSize = 11.sp, color = ColorSlate400)
                        Text("${currentProject?.vibeScore ?: 85}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorIndigo600)
                    }
                }
            }
        }
    }
}

@Composable
fun QmlClassifierSimulation(
    qmlTrainingActive: Boolean,
    qmlEpoch: Int,
    qmlLoss: Float,
    qmlAccuracy: Float,
    qmlLossHistory: List<Float>,
    onStartTraining: () -> Unit,
    onStopTraining: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Variational Quantum Circuit Optimizer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Optimizing Qubit weights via gradient descent backprop", color = ColorSlate400, fontSize = 11.sp)

        // Loss & Accuracy stats card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSlate950)
                .border(1.dp, Color.White.copy(alpha = 0.05f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("EPOCH TRAINED", fontSize = 9.sp, color = ColorSlate400, fontFamily = FontFamily.Monospace)
                Text("$qmlEpoch / 100", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("QUANTUM LOSS", fontSize = 9.sp, color = ColorSlate400, fontFamily = FontFamily.Monospace)
                Text(String.format("%.4f", qmlLoss), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (qmlLoss < 0.1f) Color(0xFF10B981) else Color(0xFFEF4444))
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ML ACCURACY", fontSize = 9.sp, color = ColorSlate400, fontFamily = FontFamily.Monospace)
                Text(String.format("%.2f%%", qmlAccuracy * 100f), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ColorIndigo500)
            }
        }

        // Live Cost Path Chart
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSlate950)
                .border(1.dp, Color.White.copy(alpha = 0.05f))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = size.height * i / gridLines
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }

                if (qmlLossHistory.size > 1) {
                    val path = Path()
                    val dx = size.width / 20f
                    val maxLossVal = 0.9f
                    qmlLossHistory.forEachIndexed { idx, valLoss ->
                        val x = idx * dx
                        val y = size.height * (1f - (valLoss / maxLossVal)).coerceIn(0f, 1f)
                        if (idx == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = ColorIndigo500,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                } else {
                    // Placeholder line
                    drawLine(
                        color = ColorIndigo500.copy(alpha = 0.3f),
                        start = Offset(0f, size.height * 0.8f),
                        end = Offset(size.width, size.height * 0.2f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
            if (qmlLossHistory.isEmpty()) {
                Text("Click Start Optimization below to run QNN Gradient Descent", color = ColorSlate400, fontSize = 11.sp, modifier = Modifier.align(Alignment.Center))
            }
        }

        // Control button
        Button(
            onClick = {
                if (qmlTrainingActive) {
                    onStopTraining()
                } else {
                    onStartTraining()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (qmlTrainingActive) Color(0xFFEF4444) else ColorIndigo600),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            Text(if (qmlTrainingActive) "🛑 Halt Gradient descent" else "⚡️ Start QML Optimization Loop", fontSize = 12.sp)
        }
    }
}

@Composable
fun BlochSphereQubitSimulation(
    qubitTheta: Float,
    qubitPhi: Float,
    appliedGates: List<String>,
    onApplyGate: (String) -> Unit
) {
    val alpha = cos(qubitTheta.toDouble() / 2.0).toFloat()
    val beta = sin(qubitTheta.toDouble() / 2.0).toFloat()
    val prob0 = alpha * alpha
    val prob1 = beta * beta

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Quantum Bloch Sphere & Gates", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("|ψ⟩ = " + String.format("%.3f", alpha) + "|0⟩ + " + String.format("%.3f", beta) + "e^{iφ}|1⟩", color = ColorSlate400, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

        // Linear meters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ColorSlate950)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Prob(|0⟩) Amplitude", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(String.format("%.1f%%", prob0 * 100f), color = ColorIndigo500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = prob0.coerceIn(0f, 1f),
                color = ColorIndigo500,
                trackColor = ColorSlate900,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Prob(|1⟩) Amplitude", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(String.format("%.1f%%", prob1 * 100f), color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = prob1.coerceIn(0f, 1f),
                color = Color(0xFF10B981),
                trackColor = ColorSlate900,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
            )
        }

        // 3D-like Holographic Bloch Sphere Projection
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSlate950)
                .border(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.height.coerceAtMost(size.width) * 0.35f

                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    center = center,
                    radius = radius,
                    style = Stroke(width = 1.5f * density)
                )

                drawOval(
                    color = Color.White.copy(alpha = 0.06f),
                    topLeft = Offset(center.x - radius, center.y - radius * 0.3f),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 0.6f),
                    style = Stroke(width = 1f * density)
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(center.x, center.y - radius - 10f),
                    end = Offset(center.x, center.y + radius + 10f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(center.x - radius - 10f, center.y),
                    end = Offset(center.x + radius + 10f, center.y),
                    strokeWidth = 1f
                )

                drawCircle(color = Color.Red, center = Offset(center.x, center.y - radius), radius = 3f * density)
                drawCircle(color = Color.Blue, center = Offset(center.x, center.y + radius), radius = 3f * density)

                val sinT = sin(qubitTheta.toDouble()).toFloat()
                val cosP = cos(qubitPhi.toDouble()).toFloat()
                val sinP = sin(qubitPhi.toDouble()).toFloat()
                val cosT = cos(qubitTheta.toDouble()).toFloat()

                val projectedX = center.x + radius * (sinT * cosP - 0.3f * sinT * sinP)
                val projectedY = center.y - radius * cosT + radius * (0.2f * sinT * sinP)

                drawLine(
                    color = ColorIndigo500,
                    start = center,
                    end = Offset(projectedX, projectedY),
                    strokeWidth = 3f * density,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color(0xFFE0E7FF),
                    center = Offset(projectedX, projectedY),
                    radius = 5f * density
                )
            }

            Text("|0⟩", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp))
            Text("|1⟩", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp))
            Text("θ=" + String.format("%.2f", qubitTheta) + " rad, φ=" + String.format("%.2f", qubitPhi) + " rad", color = ColorIndigo100, fontSize = 9.sp, modifier = Modifier.align(Alignment.BottomStart).padding(8.dp), fontFamily = FontFamily.Monospace)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gate Log: " + if (appliedGates.isEmpty()) "ø" else appliedGates.joinToString(" → "), color = ColorSlate400, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
        }

        // Gate triggers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("H", "X", "Y", "Z", "Reset").forEach { gate ->
                Button(
                    onClick = { onApplyGate(gate) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (gate == "Reset") ColorSlate700 else ColorIndigo600),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text(gate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
