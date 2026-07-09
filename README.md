# Vibe Workspace

Vibe Workspace is a Jetpack Compose Android application that serves as an integrated workspace, AI chat assistant, and quantum machine learning simulator[span_0](start_span)[span_0](end_span)[span_1](start_span)[span_1](end_span). 

## 🚀 Features

The application interface is built entirely with Jetpack Compose and is divided into four primary tabs[span_2](start_span)[span_2](end_span)[span_3](start_span)[span_3](end_span):

*   **Project Workspace:** Users can create and select projects, as well as create, update, read, and delete individual project files[span_4](start_span)[span_4](end_span). A floating dialog is provided to create a new workspace by entering a workspace name and a short description[span_5](start_span)[span_5](end_span).
*   **Vibe Assistant:** An AI chat interface where users can send a "vibe" (text prompt) and receive generated responses based on their current workspace context[span_6](start_span)[span_6](end_span).
*   **Ship & Simulate:**
    *   Provides functionality to compile and verify the current project code, displaying a live compilation log[span_7](start_span)[span_7](end_span).
    *   Features a quantum simulation tracker that monitors qubit properties (such as theta and phi) and tracks applied quantum gates[span_8](start_span)[span_8](end_span).
    *   Includes a Quantum Machine Learning (QML) training interface that allows users to start and stop training while tracking the current epoch, loss, accuracy, and loss history[span_9](start_span)[span_9](end_span).
*   **Settings:** Allows users to configure their API key and "Vibe Level," while displaying metrics like the total file count and message count for the current project[span_10](start_span)[span_10](end_span).

## 🛠 Tech Stack & Libraries

*   **UI Framework:** Android Jetpack Compose (Material 3) with edge-to-edge rendering[span_11](start_span)[span_11](end_span)[span_12](start_span)[span_12](end_span).
*   **Architecture:** Model-View-ViewModel (MVVM) utilizing `StateFlow` and `collectAsStateWithLifecycle` for reactive UI updates[span_13](start_span)[span_13](end_span).
*   **AI & Cloud Services:** Integrated with the Firebase AI bill of materials (`firebase.ai`) and Firebase AppCheck Recaptcha[span_14](start_span)[span_14](end_span).
*   **Local Storage:** Room Database for local data persistence, utilizing Kotlin Symbol Processing (KSP) for compiler generation[span_15](start_span)[span_15](end_span).
*   **Networking & Serialization:** Retrofit and OkHttp (with logging interceptors) for network requests, coupled with Moshi for JSON serialization[span_16](start_span)[span_16](end_span).
*   **Concurrency:** Kotlin Coroutines (`kotlinx.coroutines.android` and `kotlinx.coroutines.core`)[span_17](start_span)[span_17](end_span).
*   **Testing:** Accommodates extensive testing setups including JUnit, Espresso, Robolectric, and Roborazzi for Compose screenshot testing[span_18](start_span)[span_18](end_span).

## ⚙️ Configuration & Project Setup

### Prerequisites
*   **Java Environment:** Source and target compatibility are set to Java Version 11[span_19](start_span)[span_19](end_span).
*   **Android SDK:** Minimum SDK is 24, and Compile/Target SDK is 36[span_20](start_span)[span_20](end_span).

### Environment Variables & Secrets
This project heavily relies on the Secrets Gradle Plugin to manage sensitive credentials[span_21](start_span)[span_21](end_span). You must define your keys in a `.env` file at the root of the project (an `.env.example` file is expected by convention)[span_22](start_span)[span_22](end_span).
*   The release signing config specifically requires `KEYSTORE_PATH` (defaults to `my-upload-key.jks`), `STORE_PASSWORD`, and `KEY_PASSWORD` to be present as system environment variables[span_23](start_span)[span_23](end_span).

### Gradle Optimization
The project is highly optimized for fast builds via configurations defined in `gradle.properties`[span_24](start_span)[span_24](end_span):
*   Allocates 4GB of memory to the JVM (`-Xmx4g`)[span_25](start_span)[span_25](end_span).
*   Enables parallel execution, configuration caching, and standard build caching[span_26](start_span)[span_26](end_span).
*   Limits the maximum number of Gradle workers to 4[span_27](start_span)[span_27](end_span).
*   Uses an in-process execution strategy for the Kotlin compiler[span_28](start_span)[span_28](end_span).
*   Configures `googleServices.missing.passthrough=true` to allow builds to proceed even if the `google-services.json` file is absent[span_29](start_span)[span_29](end_span). Similarly, the `build.gradle.kts` file sets the missing Google Services plugin strategy to `WARN` rather than failing the build[span_30](start_span)[span_30](end_span).

## 🚀 Building the Project

Because the build files utilize the `com.example` application ID and standard Gradle conventions[span_31](start_span)[span_31](end_span), you can build the debug version locally. 

To build a debug variant (which automatically uses the included `debug.keystore` with standard 'android' credentials)[span_32](start_span)[span_32](end_span):
```bash
./gradlew assembleDebug
