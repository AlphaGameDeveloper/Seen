// Seen - Mental Health Application
//     Copyright (C) 2025  Damien Boisvert
//                   2025  Alexander Cameron
//
//     Seen is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.
//
//     Seen is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.
//
//     You should have received a copy of the GNU General Public License
//     along with Seen.  If not, see <https://www.gnu.org/licenses/>.

package dev.alphagame.seen.translations

class SpanishTranslation : Translation() {
    override val TRANSLATION = TranslationCode.SPANISH

    override val appName = "Seen"
    override val version = "Versión"
    override val settings = "Configuración"
    override val about = "Acerca de"
    override val back = "Atrás"
    override val info = "Info"

    // Welcome Screen
    override val welcomeTitle = "Bienvenido a Seen"
    override val welcomeSubtitle = "Tómate un momento para reflexionar sobre ti mismo"
    override val startQuiz = "Comenzar evaluación"
    override val viewNotes = "Ver notas"
    override val viewMoodHistory = "Ver historial de estado de ánimo"

    override val welcomeScreenMessages = listOf(
        "Tómate un momento para reflexionar sobre ti mismo",
        "Está bien no estar bien",
        "Hay esperanza, incluso cuando tu mente te dice lo contrario",
        "No estás solo. Eres visto.",
        "¡Bienvenido de nuevo!",
        "¡Qué bueno verte otra vez!",
        "¡Comencemos algo genial!",
        "¿Listo para progresar?",
        "¡Espero que tengas un buen día!",
        "¡Hagamos que este día cuente!"
    )

    // PHQ-9 Questions
    override val phq9Questions = listOf(
        "¿Poco interés o placer en hacer cosas?",
        "¿Sentirse deprimido, triste o sin esperanza?",
        "¿Problemas para dormirse, mantenerse dormido o dormir demasiado?",
        "¿Sentirse cansado o tener poca energía?",
        "¿Poco apetito o comer en exceso?",
        "¿Sentirse mal consigo mismo?",
        "¿Problemas para concentrarse en las cosas?",
        "¿Moverse o hablar lentamente o estar inquieto/nervioso?",
        "¿Pensamientos de que estaría mejor muerto o de hacerse daño?"
    )

    override val phq9Options = listOf(
        "😐 Para nada",
        "😕 Varios días",
        "😞 Más de la mitad de los días",
        "😢 Casi todos los días"
    )

    // Quiz/Question Screen
    override val questionProgress = "Pregunta %d de %d"

    // Result Screen
    override val resultTitle = "Evaluación completa"
    override val resultScore = "Tu puntuación"
    override val resultMinimal = "Depresión mínima"
    override val resultMild = "Depresión leve"
    override val resultModerate = "Depresión moderada"
    override val resultSevere = "Depresión severa"
    override val resultDescription = "Esta evaluación proporciona información sobre tu estado actual de salud mental. Considera consultar con un profesional de la salud para un diagnóstico adecuado y apoyo."
    override val retakeQuiz = "Repetir evaluación"
    override val saveResult = "Guardar resultado"
    override val talkToSomeone = "📞 Hablar con alguien – 988"
    override val learnAboutDepression = "📚 Aprender sobre la depresión"
    override val returnToHome = "🔄 Volver al inicio"

    // Settings Screen
    override val appearance = "Apariencia"
    override val colorScheme = "Esquema de colores"
    override val language = "Idioma"
    override val themeAuto = "Auto (Sistema)"
    override val themeLight = "Claro"
    override val themeDark = "Oscuro"
    override val deleteAllData = "Eliminar todos los datos"
    override val deleteDataWarning = "Esto eliminará permanentemente todo tu historial de estado de ánimo, notas y configuraciones. Esta acción no se puede deshacer."
    override val deleteDataConfirmation = "¿Estás seguro de que quieres eliminar todos los datos?"
    override val cancel = "Cancelar"
    override val delete = "Eliminar"
    override val createdBy = "Creado por Damien Boisvert & Alexander Cameron"

    // Notes Screen
    override val notes = "Notas"
    override val addNote = "Agregar nota"
    override val editNote = "Editar nota"
    override val deleteNote = "Eliminar nota"
    override val noteHint = "Escribe tus pensamientos aquí..."
    override val save = "Guardar"
    override val howAreYouFeeling = "¿Qué hay de nuevo?"
    override val saveNote = "💾 Guardar nota"

    // Mood History Screen
    override val moodHistory = "Historial de estado de ánimo"
    override val noMoodData = "No hay entradas de estado de ánimo aún.\n¡Usa el widget para empezar a hacer seguimiento!"
    override val score = "Puntuación"
    override val date = "Fecha"
    override val clearHistory = "Limpiar historial"
    override val clearMoodHistory = "Limpiar historial de estado de ánimo"
    override val clearMoodHistoryConfirmation = "¿Estás seguro de que quieres eliminar todas las entradas de estado de ánimo? Esta acción no se puede deshacer."
    override val deleteMoodEntry = "Eliminar entrada de estado de ánimo"
    override val deleteMoodEntryConfirmation = "¿Estás seguro de que quieres eliminar esta entrada de estado de ánimo %s del %s?"
    override val todaysStats = "Estadísticas de hoy"
    override val entriesToday = "Entradas hoy: %d"
    override val totalEntries = "Total de entradas: %d"
    override val todaysMoods = "Estados de ánimo de hoy: %s"
    override val graphText = "Un gráfico se mostrará aquí una vez que completes dos o más evaluaciones PHQ-9.\n¡Vuelve más tarde!"
    override val moodEntries = "Entradas de estado de ánimo"
    override val phqResponses = "Respuestas del PHQ-9"
    // PHQ-9 Deletion Dialog & Severity
    override val deletePHQ9ResponsesTitle = "Eliminar respuestas PHQ-9"
    override val phq9SeverityMinimal = "Depresión mínima"
    override val phq9SeverityMild = "Depresión leve"
    override val phq9SeverityModerate = "Depresión moderada"
    override val phq9SeverityModeratelySevere = "Depresión moderadamente severa"
    override val phq9SeveritySevere = "Depresión severa"
    override val phq9SeverityUnknown = "Desconocido"
    override val deletePHQ9EntryTitle = "¿Eliminar entrada PHQ9?"
    override val deletePHQ9EntryDescription = "¿Eliminar resultado PHQ-9 con puntuación %s de %s?\n\nLos datos eliminados no se pueden recuperar."
    override val deletePHQ9EntryConfirm = "Eliminar"
    override val deletePHQ9EntryCancel = "Cancelar"
    override val deletePHQ9EntryContentDescription = "Eliminar entrada PHQ-9"

    // Onboarding Screen
    override val onboardingTitle = "Bienvenido a Seen"
    override val onboardingDescription = "Seen es una herramienta de evaluación de salud mental que te ayuda a seguir tu estado de ánimo y bienestar a lo largo del tiempo usando el cuestionario PHQ-9."
    override val onboardingPrivacy = "Tu privacidad es importante para nosotros. Todos los datos se almacenan localmente en tu dispositivo y nunca se comparten con terceros."
    override val onboardingContinue = "Comenzar"
    override val onboardingSkip = "Saltar"
    override val onboardingWelcomeTitle = "Bienvenido a Seen"
    override val onboardingWelcomeDesc = "Un compañero de salud mental simple y privado diseñado para ayudarte a seguir tu bienestar."
    override val onboardingPHQ9Title = "Evaluación PHQ-9"
    override val onboardingPHQ9Desc = "Realiza el cuestionario PHQ-9 clínicamente validado para evaluar tu salud mental y seguir los cambios a lo largo del tiempo."
    override val onboardingNotesTitle = "Notas personales"
    override val onboardingNotesDesc = "Mantén notas privadas sobre tus pensamientos, sentimientos y experiencias diarias en un espacio seguro."
    override val onboardingPrivacyTitle = "Tu privacidad primero"
    override val onboardingPrivacyDesc = "Todos tus datos permanecen en tu dispositivo. Nunca recopilamos, almacenamos o compartimos tu información personal."
    override val onboardingNoAdsTitle = "Sin anuncios"
    override val onboardingNoAdsDesc = "Disfruta de una experiencia sin distracciones. Esta aplicación es completamente gratuita y nunca mostrará anuncios."
    override val onboardingNotificationsTitle = "Mantente Conectado"
    override val onboardingNotificationsDesc = "Permite notificaciones para recibir recordatorios de tus evaluaciones de salud mental y actualizaciones importantes de la aplicación. Puedes cambiar esto en cualquier momento en la configuración."
    override val next = "Siguiente"

    // Settings Screen - Additional
    override val appTitle = "Seen - Evaluación de Salud Mental"
    override val builtOn = "Construido el"
    override val branch = "Rama"
    override val commitMessage = "Mensaje de commit"
    override val assessmentSettings = "Configuración de evaluación"
    override val savePHQ9Data = "Guardar datos de evaluación PHQ-9"
    override val savePHQ9DataDescription = "Almacenar respuestas detalladas de preguntas localmente para seguir el progreso"
    override val dataManagement = "Gestión de datos"
    override val deleteAllDataDescription = "Esto eliminará permanentemente todas tus notas, resultados de evaluación, entradas de estado de ánimo y restablecerá toda la configuración de la aplicación a los valores predeterminados. Esta acción no se puede deshacer."
    override val privacyAndData = "Privacidad y datos"
    override val privacyDescription = "• Todos los datos están encriptados y almacenados localmente en su dispositivo\n• No se comparte información personal con terceros\n• Los resultados de evaluación permanecen completamente privados\n• Puedes eliminar tus datos en cualquier momento"
    override val importantNotice = "Aviso importante"
    override val disclaimerText = "Esta aplicación no es un sustituto del consejo médico profesional, diagnóstico o tratamiento. Si estás experimentando una crisis de salud mental, por favor contacta servicios de emergencia o consulta con un proveedor de atención médica calificado inmediatamente."
    override val deleteConfirmTitle = "¿Eliminar todos los datos?"
    override val deleteConfirmText = "Esto eliminará permanentemente todas tus notas, resultados de evaluación, entradas de estado de ánimo y configuración de la aplicación. Todos los datos serán borrados y la configuración se restablecerá a valores predeterminados. Esta acción no se puede deshacer.\n\n¿Estás seguro de que quieres continuar?"
    override val deleteAll = "Eliminar todo"
    override val dataDeletedTitle = "Datos eliminados exitosamente"
    override val dataDeletedText = "Todos tus datos han sido eliminados permanentemente y la configuración se ha restablecido a los valores predeterminados. La aplicación se cerrará ahora."
    override val continueButton = "Continuar"

    // Common
    override val yes = "Sí"
    override val no = "No"
    override val ok = "OK"
    override val error = "Error"
    override val loading = "Cargando..."

    // Update Check
    override val checkForUpdates = "Buscar actualizaciones"
    override val checkingForUpdates = "Buscando actualizaciones..."
    override val updateAvailable = "Actualización disponible"
    override val requiredUpdate = "Actualización requerida"
    override val noUpdatesAvailable = "¡Estás al día!"
    override val noUpdatesAvailableMessage = "Estás ejecutando la última versión de la aplicación. No hay actualizaciones disponibles en este momento."
    override val updateCheckFailed = "Error al buscar actualizaciones"
    override val updateCheckFailedMessage = "No se pueden verificar las actualizaciones. Por favor, verifica tu conexión a internet e inténtalo más tarde."
    override val noInternetConnection = "Sin conexión a internet"
    override val noInternetConnectionMessage = "Por favor, verifica tu conexión a internet e inténtalo de nuevo."
    override val internetTroubleshootingTips = "• Verifica tu conexión Wi-Fi\n• Verifica tus datos móviles\n• Inténtalo de nuevo en un momento"
    override val updateNow = "Actualizar ahora"
    override val updateLater = "Más tarde"
    override val downloadUpdate = "Descargar"
    override val whatsNew = "Novedades:"
    override val currentVersion = "Versión actual:"
    override val latestVersion = "Última versión:"
    override val updateRequired = "Esta actualización es requerida para continuar usando la aplicación."

    // Update Notifications
    override val updateNotificationTitle = "Nueva actualización disponible"
    override val updateNotificationMessage = "Una nueva versión de Seen está disponible. Toca para descargar."
    override val updateNotificationDontRemind = "No recordarme"
    override val enableBackgroundUpdateChecks = "Habilitar verificaciones automáticas de actualización"
    override val enableBackgroundUpdateChecksDescription = "Verificar automáticamente nuevas versiones cada 15 minutos y recibir notificaciones"

    // Enhanced Onboarding
    override val onboardingGetStarted = "Comenzar"
    override val onboardingConfigureApp = "Configurar aplicación"
    override val onboardingAITitle = "Características de IA"
    override val onboardingAIDesc = "Habilita insights y sugerencias impulsados por IA para mejorar tu viaje de salud mental. Todo el procesamiento ocurre localmente en tu dispositivo."
    override val onboardingAIEnabled = "Habilitar características de IA"
    override val onboardingAIDisabled = "Mantener características de IA deshabilitadas"
    override val onboardingNotificationConfigTitle = "Preferencias de notificación"
    override val onboardingNotificationConfigDesc = "Personaliza cómo Seen te mantiene informado sobre tu viaje de salud mental y actualizaciones de la aplicación."
    override val onboardingAllowNotifications = "Permitir notificaciones"
    override val onboardingEnableReminders = "Habilitar recordatorios suaves para evaluaciones de salud mental"
    override val onboardingEnableUpdateChecks = "Habilitar notificaciones automáticas de actualización"
    override val onboardingGeneralSettingsTitle = "Personaliza tu experiencia"
    override val onboardingGeneralSettingsDesc = "Elige tu tema e idioma preferidos para que Seen se sienta perfecto para ti."
    override val onboardingChooseTheme = "Elegir tema"
    override val onboardingChooseLanguage = "Elegir idioma"
    override val onboardingDataPrivacyTitle = "Datos y privacidad"
    override val onboardingDataPrivacyDesc = "Controla cómo se almacenan tus datos de evaluación y asegúrate de que tu privacidad esté protegida."
    override val onboardingEnableDataStorage = "Guardar datos detallados de evaluación para seguimiento de progreso"
    override val onboardingComplete = "Completar configuración"
    override val onboardingSetupComplete = "¡Todo listo!"
    override val onboardingSetupCompleteDesc = "Seen ahora está configurado según tus preferencias. Siempre puedes cambiar estas configuraciones más tarde."
    override val onboardingReadyToUse = "Comenzar a usar Seen"

    // Analytics
    override val onboardingAnalyticsTitle = "Ayuda a mejorar Seen"
    override val onboardingAnalyticsDesc = "Comparte datos de uso anónimos para ayudarnos a mejorar Seen para todos"
    override val onboardingAnalyticsEnabled = "📈 Compartir datos de uso"
    override val onboardingAnalyticsDisabled = "🥺 Mantener datos privados"
    override val analyticsPrivacyNote = "Solo recopilamos estadísticas de uso anónimas para mejorar la aplicación. Nunca se comparten datos de salud personal."
    override val analyticsDataUsage = "Los datos recopilados incluyen patrones de uso de la aplicación, interacciones con funciones e informes de fallos. Todos los datos son anónimos y no se pueden vincular a ti."

    // Settings Screen - Additional Sections
    override val notifications = "Notificaciones"
    override val enableReminders = "Habilitar recordatorios"
    override val enableRemindersDescription = "Recibe recordatorios suaves para chequeos de salud mental"
    override val encryption = "Encriptación"
    override val enableEncryption = "Habilitar encriptación en el dispositivo"
    override val encryptionDescription = "Datos seguros y privados, guardados en tu dispositivo."
    override val aiFeatures = "Funciones de IA"
    override val enableAIFeatures = "Habilitar funciones de IA"
    override val aiFeaturesDescription = "Obtén perspectivas y sugerencias personalizadas (Próximamente)"
    override val analytics = "Análisis"
    override val enableAnalytics = "Habilitar análisis"
    override val analyticsDescription = "Ayuda a mejorar Seen compartiendo datos de uso anónimos"

    // Welcome Screen - Additional
    override val phq9AssessmentTitle = "Evaluación PHQ-9"
    override val phq9AssessmentDescription = "Un cuestionario confidencial para ayudar a evaluar tu salud mental y bienestar durante las últimas dos semanas."

    // Notes Screen - Additional
    override val noEntriesMessage = "Aún no hay datos de entradas de diario. \nPara comenzar, usa el botón \"Agregar entrada\" en la esquina inferior derecha."

    // Debug Screens
    override val databaseEncryptionDebug = "Debug de encriptación de base de datos"

    // Resources Screen
    override val backToResults = "Volver a resultados"
    override val resourcesAndSupport = "Recursos y apoyo"
    override val yourScore = "Tu puntuación"

    // AI Analysis & Results
    override val aiAnalysisInProgress = "Análisis de IA en progreso..."
    override val aiAnalysisReady = "¡Análisis de IA listo! Toca abajo para ver las perspectivas."
    override val aiAnalysisUnavailable = "Análisis de IA no disponible. Ve recursos básicos en su lugar."
    override val preparingAiAnalysis = "Preparando análisis de IA..."
    override val viewAiAnalysis = "Ver análisis de IA"
    override val viewResourcesAndSupport = "Ver recursos y apoyo"

    // Resources Screen - Additional Content
    override val crisisSupport = "Apoyo en Crisis"
    override val crisisSupportDescription = "Si tienes pensamientos de autolesión o suicidio, por favor busca ayuda inmediatamente."
    override val maintainingMentalWellness = "Mantener el Bienestar Mental"
    override val maintainingWellnessDescription = "Tu puntuación indica síntomas mínimos. Aquí hay recursos para mantener tu salud mental."
    override val supportTreatmentOptions = "Opciones de Apoyo y Tratamiento"
    override val supportTreatmentDescription = "Tu puntuación sugiere síntomas leves a moderados. Considera estas opciones de apoyo."
    override val immediateProfessionalHelp = "Ayuda Profesional Inmediata"
    override val immediateProfessionalDescription = "Tu puntuación indica síntomas más severos. Se recomienda fuertemente ayuda profesional."
    override val educationalResources = "Recursos Educativos"
    override val educationalResourcesDescription = "Aprende más sobre salud mental y depresión."
    override val backToResults2 = "Volver a Resultados"
    override val mentalHealthTips = "Consejos de Salud Mental"
    override val mindfulnessResources = "Recursos de Atención Plena"
    override val findATherapist = "Encontrar un Terapeuta"
    override val findProfessionalHelpNow = "Encontrar Ayuda Profesional Ahora"
    override val crisisResources = "Recursos de Crisis"
    override val nimhDepressionInfo = "Información sobre Depresión NIMH"
    override val mentalHealthGov = "MentalHealth.gov"
    override val namiResources = "Recursos NAMI"

    // Health Status
    override val serviceHealthStatus = "Estado de salud de servicios"
    override val analyticsService = "Análisis"
    override val releasesService = "Versiones"
    override val aiService = "IA"
    override val healthyStatus = "Saludable"
    override val unhealthyStatus = "No saludable"
    override val unknownStatus = "Desconocido"
    override val refreshStatus = "Actualizar"
    override val closeDialog = "Cerrar"

    //Moods
    override val veryHappy = "Muy Felizoverride"
    override val happy = "Feliz"
    override val neutral = "Neutral"
    override val sad = "Triste"
    override val verySad = "Muy Triste"
    override val angry = "Enojado"
    override val anxious = "Ansioso"
    override val excited = "Emocionado"

    override val deletePHQ9Result = "¿Eliminar el resultado PHQ-9 con puntuación %s de %s?\n\nLos datos eliminados no se pueden recuperar."
}
