# Vibe Workspace

An Android-based integrated development workspace, AI assistant, and quantum machine learning simulator built natively with Jetpack Compose.

## 🚀 About the Project

Vibe Workspace brings an entire coding and simulation environment to your mobile device. With built-in generative AI capabilities via Firebase AI and localized Quantum Machine Learning tracking, it offers a futuristic approach to mobile development, AI chat, and quantum experimentation.

## ✨ Features

* **Project Workspace:** Create, edit, and manage modular programming environments and project files seamlessly.
* **AI "Vibe" Chat:** An integrated AI assistant to help you brainstorm, generate code, or troubleshoot right within your workspace context.
* **Ship & Simulate:**
    * **Compiler Verification:** Run and verify project code locally with a real-time compilation log.
    * **Quantum Simulator:** Monitor qubit states (θ, φ) and apply simulated quantum gates.
    * **QML Training Tracker:** Monitor real-time local Quantum Machine Learning metrics including Epochs, Loss, and Accuracy.
* **Custom Settings:** Configure your API keys securely, adjust your AI "Vibe Level", and track workspace metrics.

## 🛠 Tech Stack

* **UI Framework:** Android Jetpack Compose (Material 3) with full edge-to-edge layout integration.
* **Architecture:** MVVM (Model-View-ViewModel) powered by Kotlin Coroutines & `StateFlow`.
* **Database:** Room Database for local, offline file and project storage.
* **AI Integration:** Firebase AI BOM (`firebase-ai`).
* **Network & Serialization:** Retrofit, OkHttp with logging interceptors, and Moshi.
* **Build System:** Gradle configured with KSP, Roborazzi for UI testing, and the Secrets Plugin for managing API keys securely.

## ⚙️ Detailed Installation & Setup

Follow these precise steps to get the development environment running locally on your machine.

### Prerequisites
Before you begin, ensure your system meets the following requirements:
* **Android Studio:** Ladybug or a newer version is highly recommended.
* **Java Development Kit (JDK):** Version 11 is required for source and target compatibility[span_0](start_span)[span_0](end_span).
* **Android SDK:** Target SDK is 36, and Minimum SDK is 24[span_1](start_span)[span_1](end_span). 

### Step 1: Clone the Repository
Open your terminal and clone the repository to your local machine:
```bash
git clone <repository-url>
cd vibe-workspace
