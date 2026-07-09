package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.api.SystemInstruction
import com.example.data.AppDatabase
import com.example.data.ProjectFile
import com.example.data.VibeMessage
import com.example.data.VibeProject
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.Serializable
import kotlin.math.*

class VibeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.vibeDao()

    // Preferences / Settings state
    private val sharedPrefs = application.getSharedPreferences("vibe_prefs", Context.MODE_PRIVATE)
    
    private val _apiKey = MutableStateFlow(sharedPrefs.getString("api_key", "") ?: "")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _vibeLevel = MutableStateFlow(sharedPrefs.getString("vibe_level", "Flow") ?: "Flow")
    val vibeLevel: StateFlow<String> = _vibeLevel.asStateFlow()

    // Navigation & Screen state
    private val _currentTab = MutableStateFlow("Project") // "Project", "Vibe", "Ship", "Settings"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Database backed states
    val projects: StateFlow<List<VibeProject>> = dao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProjectId = MutableStateFlow<Int?>(null)
    val selectedProjectId: StateFlow<Int?> = _selectedProjectId.asStateFlow()

    val currentProject: StateFlow<VibeProject?> = _selectedProjectId
        .flatMapLatest { id ->
            if (id != null) {
                flow<VibeProject?> {
                    val p = dao.getProjectById(id)
                    emit(p)
                }
            } else {
                flowOf<VibeProject?>(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentFiles: StateFlow<List<ProjectFile>> = _selectedProjectId
        .flatMapLatest { id ->
            if (id != null) dao.getFilesByProject(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFileId = MutableStateFlow<Int?>(null)
    val selectedFileId: StateFlow<Int?> = _selectedFileId.asStateFlow()

    val selectedFile: StateFlow<ProjectFile?> = combine(currentFiles, _selectedFileId) { files, fileId ->
        if (fileId != null) {
            files.find { it.id == fileId } ?: files.firstOrNull()
        } else {
            files.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentMessages: StateFlow<List<VibeMessage>> = _selectedProjectId
        .flatMapLatest { id ->
            if (id != null) dao.getMessagesByProject(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI generation progress
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError.asStateFlow()

    // Active Simulation state (used in "Ship" tab)
    private val _isCompiling = MutableStateFlow(false)
    val isCompiling: StateFlow<Boolean> = _isCompiling.asStateFlow()

    private val _compilationLog = MutableStateFlow<List<String>>(emptyList())
    val compilationLog: StateFlow<List<String>> = _compilationLog.asStateFlow()

    private val _isSimulationRunning = MutableStateFlow(false)
    val isSimulationRunning: StateFlow<Boolean> = _isSimulationRunning.asStateFlow()

    // --- QUANTUM MECHANICS & MACHINE LEARNING COPROCESSOR STATE ---
    private val _qubitTheta = MutableStateFlow(0f)
    val qubitTheta: StateFlow<Float> = _qubitTheta.asStateFlow()

    private val _qubitPhi = MutableStateFlow(0f)
    val qubitPhi: StateFlow<Float> = _qubitPhi.asStateFlow()

    private val _appliedGates = MutableStateFlow<List<String>>(emptyList())
    val appliedGates: StateFlow<List<String>> = _appliedGates.asStateFlow()

    private val _qmlTrainingActive = MutableStateFlow(false)
    val qmlTrainingActive: StateFlow<Boolean> = _qmlTrainingActive.asStateFlow()

    private val _qmlEpoch = MutableStateFlow(0)
    val qmlEpoch: StateFlow<Int> = _qmlEpoch.asStateFlow()

    private val _qmlLoss = MutableStateFlow(0.85f)
    val qmlLoss: StateFlow<Float> = _qmlLoss.asStateFlow()

    private val _qmlAccuracy = MutableStateFlow(0.50f)
    val qmlAccuracy: StateFlow<Float> = _qmlAccuracy.asStateFlow()

    private val _qmlLossHistory = MutableStateFlow<List<Float>>(emptyList())
    val qmlLossHistory: StateFlow<List<Float>> = _qmlLossHistory.asStateFlow()

    fun startQmlTraining() {
        if (_qmlTrainingActive.value) return
        _qmlTrainingActive.value = true
        _qmlEpoch.value = 0
        _qmlLoss.value = 0.85f
        _qmlAccuracy.value = 0.50f
        _qmlLossHistory.value = listOf(0.85f)

        viewModelScope.launch {
            val lossHistory = mutableListOf(0.85f)
            for (epoch in 1..100) {
                if (!_qmlTrainingActive.value) break
                delay(60) // High frequency real-time updates
                _qmlEpoch.value = epoch

                val noise = (Math.random().toFloat() - 0.5f) * 0.04f
                val targetLoss = 0.01f + 0.84f * exp(-epoch / 25f)
                val currentLoss = (targetLoss + noise).coerceIn(0.005f, 1.0f)
                _qmlLoss.value = currentLoss
                lossHistory.add(currentLoss)
                if (lossHistory.size > 20) {
                    lossHistory.removeAt(0)
                }
                _qmlLossHistory.value = lossHistory.toList()

                val currentAcc = 0.50f + 0.494f * (1.0f - exp(-epoch / 20f)) + (Math.random().toFloat() - 0.5f) * 0.02f
                _qmlAccuracy.value = currentAcc.coerceIn(0.50f, 0.999f)
            }
            _qmlTrainingActive.value = false
        }
    }

    fun stopQmlTraining() {
        _qmlTrainingActive.value = false
    }

    fun applyGate(gateName: String) {
        val gates = _appliedGates.value.toMutableList()
        if (gates.size >= 8) gates.removeAt(0)
        gates.add(gateName)
        _appliedGates.value = gates

        when (gateName) {
            "H" -> {
                _qubitTheta.value = 3.14159f / 2f
                _qubitPhi.value = 0f
            }
            "X" -> {
                _qubitTheta.value = 3.14159f - _qubitTheta.value
            }
            "Z" -> {
                _qubitPhi.value = (_qubitPhi.value + 3.14159f) % (2f * 3.14159f)
            }
            "Y" -> {
                _qubitTheta.value = 3.14159f - _qubitTheta.value
                _qubitPhi.value = (_qubitPhi.value + 3.14159f / 2f) % (2f * 3.14159f)
            }
            "Reset" -> {
                _qubitTheta.value = 0f
                _qubitPhi.value = 0f
                _appliedGates.value = emptyList()
            }
        }
    }

    // Seed mock data if no projects exist
    init {
        viewModelScope.launch {
            projects.take(1).collect { list ->
                if (list.isEmpty()) {
                    seedDatabase()
                } else {
                    _selectedProjectId.value = list.first().id
                }
            }
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun selectProject(projectId: Int) {
        _selectedProjectId.value = projectId
        _selectedFileId.value = null // reset selection to let default apply
    }

    fun selectFile(fileId: Int) {
        _selectedFileId.value = fileId
    }

    fun setApiKey(key: String) {
        _apiKey.value = key
        sharedPrefs.edit().putString("api_key", key).apply()
    }

    fun setVibeLevel(level: String) {
        _vibeLevel.value = level
        sharedPrefs.edit().putString("vibe_level", level).apply()
    }

    // Creates a brand new empty Vibe project
    fun createProject(name: String, description: String) {
        viewModelScope.launch {
            val proj = VibeProject(
                name = name,
                description = description,
                vibeScore = 80,
                auraLevel = "PURE"
            )
            val id = dao.insertProject(proj).toInt()
            
            // Add a default starting file
            val file = ProjectFile(
                projectId = id,
                fileName = "vibe_checker.tsx",
                content = """export default function VibeCheck() {
    // Write your natural language vibe...
    return (
        <div className="text-xl font-bold text-indigo-600">
            ✨ Pure Vibe Coding Project Initiated! ✨
        </div>
    );
}
""",
                language = "typescript"
            )
            dao.insertFile(file)
            _selectedProjectId.value = id
            _selectedFileId.value = null
        }
    }

    // Update code in selected file manually
    fun updateFileContent(fileId: Int, content: String) {
        viewModelScope.launch {
            val current = currentFiles.value.find { it.id == fileId } ?: return@launch
            val updated = current.copy(content = content, updatedAt = System.currentTimeMillis())
            dao.updateFile(updated)

            // Recalculate vibe score of project slightly for visual feedback
            currentProject.value?.let { proj ->
                val lines = content.lines().size
                val score = (70 + (lines * 2 % 30)).coerceIn(60, 99)
                dao.updateProject(proj.copy(vibeScore = score, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    // Create a new empty file in the project
    fun createProjectFile(fileName: String, content: String, language: String) {
        val projectId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            val file = ProjectFile(
                projectId = projectId,
                fileName = fileName,
                content = content,
                language = language
            )
            val fileId = dao.insertFile(file).toInt()
            _selectedFileId.value = fileId
        }
    }

    // Deletes selected file
    fun deleteProjectFile(file: ProjectFile) {
        viewModelScope.launch {
            dao.deleteFile(file)
            if (_selectedFileId.value == file.id) {
                _selectedFileId.value = null
            }
        }
    }

    // Delete a project
    fun deleteProject(project: VibeProject) {
        viewModelScope.launch {
            dao.deleteProject(project)
            if (_selectedProjectId.value == project.id) {
                val rem = dao.getAllProjects().firstOrNull()?.firstOrNull()
                _selectedProjectId.value = rem?.id
                _selectedFileId.value = null
            }
        }
    }

    // AI Vibe Input - Send prompt to Gemini!
    fun sendVibe(vibeText: String) {
        val projectId = _selectedProjectId.value ?: return
        if (vibeText.isBlank()) return

        viewModelScope.launch {
            // 1. Add User message to chat history
            val userMsg = VibeMessage(
                projectId = projectId,
                sender = "USER",
                message = vibeText,
                auraSymbol = "⚡️"
            )
            dao.insertMessage(userMsg)

            _isGenerating.value = true
            _generationError.value = null

            // 2. Fetch all project files to provide context
            val filesContext = currentFiles.value.joinToString("\n\n") { file ->
                "--- File: ${file.fileName} (${file.language}) ---\n${file.content}"
            }

            // 3. Resolve active API Key
            val resolvedKey = _apiKey.value.ifBlank { BuildConfig.GEMINI_API_KEY }

            if (resolvedKey.isNotBlank() && resolvedKey != "MY_GEMINI_API_KEY") {
                try {
                    val systemPrompt = """
                        You are VibeCode AI, a top-tier aesthetic vibe coding companion.
                        The user is working on an Android/Web project. Here is their project context with existing files:
                        $filesContext
                        
                        The user sent the vibe: "$vibeText"
                        
                        You must respond in a valid JSON object matching this schema:
                        {
                          "assistantResponse": "Supportive developer feedback describing changes.",
                          "auraSymbol": "✨" or "⚡️" or "✦" or "🔮",
                          "vibeScore": 85, (estimation from 60 to 100 based on aesthetic appeal/code quality)
                          "auraLevel": "PURE" or "FLOW" or "HYPER" or "CHILL",
                          "updatedFiles": [
                            {
                              "fileName": "filename.extension",
                              "content": "Entire updated file content",
                              "language": "language"
                            }
                          ]
                        }
                        Make sure the JSON matches this structure exactly, do not output anything outside of the JSON block. Do not include markdown code fence formatting like ```json in your raw output. Just raw JSON.
                    """.trimIndent()

                    val temperature = when (_vibeLevel.value) {
                        "Chill" -> 0.4f
                        "Flow" -> 0.7f
                        "Hyper" -> 1.0f
                        "Pure" -> 0.8f
                        else -> 0.7f
                    }

                    val response = RetrofitClient.geminiService.generateContent(
                        model = "gemini-3.5-flash",
                        apiKey = resolvedKey,
                        request = GenerateContentRequest(
                            contents = listOf(
                                Content(parts = listOf(Part(text = vibeText)))
                            ),
                            systemInstruction = SystemInstruction(parts = listOf(Part(text = systemPrompt))),
                            generationConfig = GenerationConfig(
                                temperature = temperature,
                                responseMimeType = "application/json"
                            )
                        )
                    )

                    val outputJsonStr = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!outputJsonStr.isNullOrBlank()) {
                        parseAndApplyAiResponse(projectId, outputJsonStr)
                    } else {
                        throw Exception("Empty response from AI engine")
                    }

                } catch (e: Exception) {
                    _generationError.value = "AI failed: ${e.message}. Using high-fidelity local generator fallback."
                    // Fallback to high-fidelity simulated response so it always works!
                    simulateVibeGeneration(projectId, vibeText)
                }
            } else {
                // No valid key entered, simulate locally!
                simulateVibeGeneration(projectId, vibeText)
            }

            _isGenerating.value = false
        }
    }

    private suspend fun parseAndApplyAiResponse(projectId: Int, jsonStr: String) {
        withContext(Dispatchers.IO) {
            try {
                val cleanedStr = jsonStr.trim().removePrefix("```json").removeSuffix("```").trim()
                val json = JSONObject(cleanedStr)
                
                val responseMsg = json.optString("assistantResponse", "Synthesized your code changes successfully.")
                val auraSymbol = json.optString("auraSymbol", "✦")
                val vibeScore = json.optInt("vibeScore", 85)
                val auraLevel = json.optString("auraLevel", "FLOW")

                // Update Project info
                dao.getProjectById(projectId)?.let { proj ->
                    dao.updateProject(
                        proj.copy(
                            vibeScore = vibeScore,
                            auraLevel = auraLevel,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                // Apply updated files
                val filesArray = json.optJSONArray("updatedFiles")
                if (filesArray != null) {
                    for (i in 0 until filesArray.length()) {
                        val fileObj = filesArray.getJSONObject(i)
                        val name = fileObj.getString("fileName")
                        val content = fileObj.getString("content")
                        val lang = fileObj.getString("language")

                        val existing = dao.getFileByName(projectId, name)
                        if (existing != null) {
                            dao.updateFile(existing.copy(content = content, updatedAt = System.currentTimeMillis()))
                        } else {
                            dao.insertFile(ProjectFile(projectId = projectId, fileName = name, content = content, language = lang))
                        }
                    }
                }

                // Insert AI message
                dao.insertMessage(
                    VibeMessage(
                        projectId = projectId,
                        sender = "AI",
                        message = responseMsg,
                        auraSymbol = auraSymbol
                    )
                )

            } catch (e: Exception) {
                // If JSON parsing fails, fallback
                simulateVibeGeneration(projectId, "Fallback from parsing error: ${e.message}")
            }
        }
    }

    private suspend fun simulateVibeGeneration(projectId: Int, userVibe: String) {
        delay(1500) // Simulating think time
        
        val replyMessage: String
        val auraSymbol: String
        val scoreOffset: Int
        var targetFileContent = ""
        var targetFileName = ""
        var targetLang = ""

        val vibeLower = userVibe.lowercase()
        if (vibeLower.contains("button") || vibeLower.contains("color") || vibeLower.contains("theme") || vibeLower.contains("style")) {
            targetFileName = "App.css"
            targetLang = "css"
            scoreOffset = 8
            auraSymbol = "✨"
            replyMessage = "I've synthesized a premium CSS variables sheet focusing on high contrast slate background, neon indigo buttons with massive backdrop blur shadows, and fluid scaling transitions."
            targetFileContent = """/* VibeCode Professional Polish stylesheet */
:root {
  --primary: #6366F1;
  --bg-slate: #0F172A;
  --accent: #10B981;
  --light-bg: #F3F4F9;
  --glow-shadow: 0 0 25px rgba(99, 102, 241, 0.4);
}

.glow-effect {
  background: radial-gradient(circle at 50% 50%, var(--bg-slate) 0%, #020617 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: var(--glow-shadow);
  transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

button {
  background-color: var(--primary);
  color: white;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  border: none;
  box-shadow: 0 4px 6px -1px rgba(99, 102, 241, 0.2);
}

button:active {
  transform: scale(0.95);
}
"""
        } else if (vibeLower.contains("animate") || vibeLower.contains("physics") || vibeLower.contains("canvas") || vibeLower.contains("wave")) {
            targetFileName = "vibe_checker.tsx"
            targetLang = "typescript"
            scoreOffset = 12
            auraSymbol = "🔮"
            replyMessage = "Added a fluid wave simulator running directly inside an interactive HTML canvas with high-frame-rate requestAnimationFrame! It captures your user vibe perfectly."
            targetFileContent = """export default function VibeCheck() {
  const [frequency, setFrequency] = useState(0.02);
  
  return (
    <div className="flex flex-col items-center gap-4 p-6 bg-slate-900 rounded-2xl border border-white/10 shadow-xl">
      <div className="text-sm font-mono text-indigo-400">quantum_waves.tsx</div>
      
      {/* Simulation Box */}
      <div className="w-full h-40 bg-black/40 rounded-xl relative overflow-hidden flex items-center justify-center">
        <div className="absolute inset-0 flex items-center justify-center opacity-30">
          <div className="w-32 h-32 rounded-full bg-indigo-500/20 blur-xl animate-pulse"></div>
        </div>
        <div className="text-center z-10 text-xs font-mono text-emerald-400">
          Oscillating at {frequency.toFixed(3)} rad/s
        </div>
      </div>
      
      <div className="flex gap-2">
        <button onClick={() => setFrequency(f => f + 0.005)} className="bg-indigo-600 text-white px-3 py-1.5 rounded-lg text-xs">
          ⚡️ Accelerate Vibe
        </button>
        <button onClick={() => setFrequency(f => Math.max(0.001, f - 0.005))} className="bg-slate-700 text-white px-3 py-1.5 rounded-lg text-xs">
          💤 Dampen
        </button>
      </div>
    </div>
  );
}
"""
        } else {
            targetFileName = "vibe_checker.tsx"
            targetLang = "typescript"
            scoreOffset = 5
            auraSymbol = "✦"
            replyMessage = "I've updated the core renderer with your custom idea: '$userVibe'. Added interactive aura counters, a state observer pattern, and a dynamic developer sandbox overlay."
            targetFileContent = """export default function VibeCheck() {
  const [aura, setAura] = useState('pure');
  const [vibesCount, setVibesCount] = useState(42);

  return (
    <div className="p-6 bg-[#0F172A] text-white rounded-3xl shadow-xl border border-white/5">
      <div className="flex items-center justify-between mb-4">
        <span className="text-xs font-mono text-slate-500">vibe_checker.tsx</span>
        <span className="px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 text-[10px]">Active</span>
      </div>
      
      <p className="text-sm text-slate-300 leading-relaxed mb-4">
        Currently manifesting: <span className="text-indigo-400 font-semibold">$userVibe</span>
      </p>

      <div className="p-4 bg-white/5 rounded-2xl mb-4 flex items-center justify-between">
        <div>
          <p className="text-xs text-slate-400">Project Aura</p>
          <p className="text-lg font-bold text-indigo-400 uppercase tracking-wider">{aura}</p>
        </div>
        <div className="text-2xl">{aura === 'pure' ? '✨' : '⚡️'}</div>
      </div>

      <div className="flex gap-2">
        <button 
          onClick={() => {
            setAura(aura === 'pure' ? 'flow' : 'pure');
            setVibesCount(c => c + 1);
          }}
          className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-xs py-3 rounded-xl transition-all"
        >
          Toggle Aura State ({vibesCount})
        </button>
      </div>
    </div>
  );
}
"""
        }

        // Apply changes
        dao.getProjectById(projectId)?.let { proj ->
            val finalScore = (proj.vibeScore + scoreOffset).coerceIn(60, 99)
            val auraString = when {
                finalScore > 90 -> "PURE"
                finalScore > 80 -> "FLOW"
                finalScore > 70 -> "HYPER"
                else -> "CHILL"
            }
            dao.updateProject(
                proj.copy(
                    vibeScore = finalScore,
                    auraLevel = auraString,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        // Write the file
        val existing = dao.getFileByName(projectId, targetFileName)
        if (existing != null) {
            dao.updateFile(existing.copy(content = targetFileContent, updatedAt = System.currentTimeMillis()))
        } else {
            dao.insertFile(ProjectFile(projectId = projectId, fileName = targetFileName, content = targetFileContent, language = targetLang))
        }

        // Add AI response to chat
        dao.insertMessage(
            VibeMessage(
                projectId = projectId,
                sender = "AI",
                message = replyMessage,
                auraSymbol = auraSymbol
            )
        )
    }

    // "Ship" or compile project simulation
    fun compileAndVerifyProject() {
        val project = currentProject.value ?: return
        viewModelScope.launch {
            _isCompiling.value = true
            _compilationLog.value = emptyList()
            
            val logs = mutableListOf<String>()
            fun addLog(msg: String) {
                logs.add(msg)
                _compilationLog.value = logs.toList()
            }

            addLog("⚡️ Initializing build pipeline for Project: ${project.name}...")
            delay(400)
            addLog("📦 Checking target platform compatibility (Android 36 / Compose Engine)")
            delay(500)
            addLog("📝 Bundling file matrix [${currentFiles.value.joinToString { it.fileName }}]")
            delay(500)
            
            // Check for potential compiler errors (e.g. syntax)
            var hasError = false
            currentFiles.value.forEach { file ->
                addLog("🔍 Verification checking file: ${file.fileName}...")
                delay(300)
                if (file.content.contains("<<<<") || file.content.contains("====")) {
                    addLog("❌ COMPILER ERROR: Git merge conflicts detected in ${file.fileName}!")
                    hasError = true
                }
            }

            if (hasError) {
                addLog("🛑 Build failed. Fix syntax or restore files.")
                _isCompiling.value = false
                return@launch
            }

            addLog("🏗️ Compiling modules with high-optimizations enabled...")
            delay(800)
            addLog("🎨 Injecting Professional Polish Design tokens into output bundles")
            delay(400)
            addLog("🟢 Build successful! Generated runtime executable with vibe level: ${project.auraLevel}")
            delay(300)
            addLog("🚀 Simulation sandbox is hot-reloaded and active!")

            _isCompiling.value = false
            _isSimulationRunning.value = true
        }
    }

    fun stopSimulation() {
        _isSimulationRunning.value = false
    }

    private suspend fun seedDatabase() {
        // Create Default Project #1
        val p1Id = dao.insertProject(
            VibeProject(
                name = "Vibe Checker Widget",
                description = "An interactive widget assessing the aura of your code base in real-time.",
                vibeScore = 85,
                auraLevel = "PURE"
            )
        ).toInt()

        dao.insertFile(
            ProjectFile(
                projectId = p1Id,
                fileName = "vibe_checker.tsx",
                content = """export default function VibeCheck() {
  // The AI is currently generating...
  const [aura, setAura] = useState('pure');

  return (
    <div className="glow-effect">
      {aura === 'pure' ? '✨' : '⚡️'}
    </div>
  );
}""",
                language = "typescript"
            )
        )

        dao.insertFile(
            ProjectFile(
                projectId = p1Id,
                fileName = "App.css",
                content = """:root {
  --indigo-glow: rgba(99, 102, 241, 0.4);
}

.glow-effect {
  padding: 24px;
  border-radius: 16px;
  background: #0F172A;
  border: 1px solid rgba(255,255,255,0.1);
  text-align: center;
}""",
                language = "css"
            )
        )

        dao.insertMessage(
            VibeMessage(
                projectId = p1Id,
                sender = "AI",
                message = "Welcome to VibeCode AI! I've preloaded your Vibe Checker Widget. Type a vibe like 'add a holographic background pulse' or 'change to dynamic slider interface' to code immediately!",
                auraSymbol = "✨"
            )
        )

        // Create Default Project #2
        val p2Id = dao.insertProject(
            VibeProject(
                name = "Quantum Oscillation Engine",
                description = "Simulates quantum amplitudes under variable wave interference constraints.",
                vibeScore = 92,
                auraLevel = "PURE"
            )
        ).toInt()

        dao.insertFile(
            ProjectFile(
                projectId = p2Id,
                fileName = "quantum_waves.kt",
                content = """package quantum

import kotlin.math.sin
import kotlin.math.PI

class QuantumOscillator(val wavelength: Double) {
    fun getAmplitude(x: Double, t: Double): Double {
        val k = 2.0 * PI / wavelength
        val omega = 5.0
        return sin(k * x - omega * t)
    }
}""",
                language = "kotlin"
            )
        )

        dao.insertMessage(
            VibeMessage(
                projectId = p2Id,
                sender = "AI",
                message = "Quantum Engine preloaded. Type a vibe to add multi-slit wave interference or interactive power controls.",
                auraSymbol = "🔮"
            )
        )

        // Create Default Project #3 - Quantum Neural Classifier
        val p3Id = dao.insertProject(
            VibeProject(
                name = "Quantum Neural Net (QML)",
                description = "Classifies multi-dimensional states using simulated Variational Quantum Circuits (VQC).",
                vibeScore = 96,
                auraLevel = "PURE"
            )
        ).toInt()

        dao.insertFile(
            ProjectFile(
                projectId = p3Id,
                fileName = "quantum_classifier.py",
                content = """# Simulated Quantum Neural Network with QML Qubits
import numpy as np

class VariationalQuantumCircuit:
    def __init__(self, num_qubits=2):
        self.num_qubits = num_qubits
        self.weights = np.random.randn(num_qubits, 3) # Rotations theta, phi, lambda

    def rx_gate(self, theta):
        return np.array([
            [np.cos(theta/2), -1j*np.sin(theta/2)],
            [-1j*np.sin(theta/2), np.cos(theta/2)]
        ])

    def evaluate(self, state_vector):
        # Applies parametrized rotation gates to optimize qubit states
        print("Feeding qubit states through variational layers...")
        return np.abs(np.dot(self.weights, state_vector))

# Initialize VQC classifier model
vqc = VariationalQuantumCircuit(num_qubits=2)
""",
                language = "python"
            )
        )

        dao.insertMessage(
            VibeMessage(
                projectId = p3Id,
                sender = "AI",
                message = "Variational Quantum Circuit QML environment fully initialized! You can simulate state vectors, apply Pauli gate operators, or launch real-time gradient descent training right from the simulation tab.",
                auraSymbol = "🌌"
            )
        )

        // Select the third project by default
        _selectedProjectId.value = p3Id
    }
}
