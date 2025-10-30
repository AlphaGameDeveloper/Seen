<h1 align=center>
    <img src=branding/logo.png alt="Seen Logo" width="32"/>
    Seen
</h1>
<p align="center">
    <strong>A free, private, and AI-powered mental health application.</strong> <br />
    <!-- haha badges -->
    <!-- badge: gpl v3 -->
    <img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="GPL v3 License Badge" />
    <!-- badge: language kotlin -->
    <img src="https://img.shields.io/badge/Language-Kotlin-F88C00.svg" alt="Kotlin Language Badge" />
    <!-- badge: platform android -->
    <img src="https://img.shields.io/badge/Platform-Android-3DDC84.svg" alt="Android Platform Badge" />
    <!-- badge: Made In Novato -->
    <img src="https://img.shields.io/badge/Made%20In-Novato-FF5733.svg" alt="Made In Novato Badge" />
</p>

## About
Seen was created for the Congressional App Challenge, 2025.  2<sup>nd</sup> district of California.

Seen is a mental health application, leveraging AI to provide users with a private space to reflect on their thoughts and emotions. The app offers mood tracking, journaling, and AI-powered insights to help users better understand and manage their mental well-being.

## Congressional App Challenge

Seen was created as an entry for the Congressional App Challenge, a competition that encourages students to learn coding and computer science by creating their own apps. The challenge is sponsored by members of the U.S. House of Representatives, and winners are recognized for their achievements in technology and innovation.

### AI Disclosure
This project was developed with the assistance of AI tools; Specifically, GitHub Copilot was used to help generate code snippets and provide suggestions during development. All code and content were reviewed and modified to ensure accuracy and safety.

<details>
    <summary>What was AI used for?</summary>
Artificial Intelligence Tooling was used for:
<ul>
    <li>Generating boilerplate/repetitive code for Android components.</li>
    <li>Providing code suggestions and completions.</li>
    <li>Assisting in writing documentation and comments.</li>
    <li>Generating rough draft UI layout code snippets. (Which get heavily modified)</li>
    <li>Generating debug screens and debug utilities.</li>
</ul>
</details>

### What did we NOT use AI for?
<ul>
    <li>Designing the overall architecture of the app.</li>
    <ol>
        <li>Deciding on features and functionality.</li>
        <li>Planning user experience and interface design.</li>
        <li>Tech stack selection. - Frontend & Backend</li>
        <li>Security and privacy considerations.</li>
    </ol>
    <li>Color schemes</li>
    <li>Making critical design decisions, such as encryption methods (and implementations) and data storage solutions.</li>
    <li>... etc. (you get the idea)</li>
</ul>

AI use is scattered throughout the codebase, thanks to GitHub Copilot's deep integration with Visual Studio Code, with the autocomplete suggestions popping up here and there. However, all code was reviewed and modified to ensure it met our standards for quality, security, and privacy.

## Tech Stack
Seen's tech stack includes:
- **Frontend**: The Android application is built using:
    - **Kotlin**: The primary programming language used for Android development.
    - **Jetpack Compose**: For building the user interface in a declarative way.
    - **SQLite**: For local data storage.
- **Backend**: The server the app talks to is built using:
    - **Python**: The primary programming language for the backend.
    - **Flask**: A lightweight web framework for Python.
    - **OpenRouter**: For AI model hosting and management.

<details>
<summary>
Privacy
</summary>
To keep our privacy-first approach, we make sure that all data is stored on the app, and only required information is sent to the backend for AI processing. The backend server handles requests from the app, processes them using AI models hosted on OpenRouter, and sends back the results.  After that, the backend forgets all user data to ensure privacy.
</details>

## Why isn't this on Google Play Store?
Tl;dr: Google Play Store has strict policies regarding apps that deal with mental health, and they require extensive documentation and compliance measures to ensure user safety and data privacy. As a free app developed for a competition, we have not pursued the necessary certifications and compliance required by Google Play Store.

However, pre-built APKs are available for download in the [Releases](https://github.com/AlphaGameDeveloper/Seen/releases) section. You can also build the app from source by following the instructions below.

<details>
<summary>
Instructions for installing APKs from unknown sources
</summary>
<h3>Installing the Seen APK</h3>
To install the Seen APK on your Android device, follow these steps:
<ol>
<li>Download the APK file from the <a href="https://github.com/AlphaGameDeveloper/Seen/releases">Releases</a> section.</li>
<li>Enable installation from unknown sources:
    <ol>
        <li>Go to your device's Settings.</li>
        <li>Navigate to Security or Apps & notifications.</li>
        <li>Find the option for "Install unknown apps" or "Unknown sources" and enable it for the app you will use to install the APK (e.g., your browser or file manager).</li>
    </ol>
</li>
<li>Locate the downloaded APK file using a file manager app.</li>
<li>Tap on the APK file to begin the installation process.</li>
<li>Follow the on-screen prompts to complete the installation.</li>
</ol>
</details>

## Building from Source
To build Seen from source, follow these steps:
1. Clone the repository:
    ```bash
    git clone https://github.com/AlphaGameDeveloper/Seen.git
    cd Seen
    ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Connect an Android device or start an emulator.
5. Run the app from Android Studio.

## Credits
Seen is developed by [Damien Boisvert](https://github.com/AlphaGameDeveloper) and [Alexander Cameron](https://github.com/AManOMG8) for the Congressional App Challenge 2025, 2<sup>nd</sup> district of California.  Licensed under GPL v3, Some Rights Reserved.

## ⚠️ Disclaimer
Seen is **not** a substitute for professional mental health care. If you are experiencing a mental health crisis, please seek help from a qualifimmmmed professional or contact emergency services immediately.
- 988 Suicide & Crisis Lifeline: Call or text 988
- National Alliance on Mental Illness (NAMI): Call 1-800-950-NAMI (6264)

This software is provided *"as is"* without warranty of any kind. The developers are not responsible for any consequences arising from the use of this software, to the fullest extent permitted by law.  See the [LICENSE](LICENSE) file for more information.
