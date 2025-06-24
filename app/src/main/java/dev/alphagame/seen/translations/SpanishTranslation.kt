package dev.alphagame.seen.translations

class SpanishTranslation : Translation() {
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
    override val resultScore = "Tu puntuación: %d"
    override val resultMinimal = "Depresión mínima"
    override val resultMild = "Depresión leve"
    override val resultModerate = "Depresión moderada"
    override val resultSevere = "Depresión severa"
    override val resultDescription = "Esta evaluación proporciona información sobre tu estado actual de salud mental. Considera consultar con un profesional de la salud para un diagnóstico adecuado y apoyo."
    override val retakeQuiz = "Repetir evaluación"
    override val saveResult = "Guardar resultado"
    override val individualScores = "Puntuaciones individuales: %s"
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
    override val howAreYouFeeling = "¿Cómo te sientes?"
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
    override val privacyDescription = "• Todos los datos se almacenan localmente en tu dispositivo\n• No se comparte información personal con terceros\n• Los resultados de evaluación permanecen completamente privados\n• Puedes eliminar tus datos en cualquier momento"
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
}
