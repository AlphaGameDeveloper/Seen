package dev.alphagame.seen.translations

class FrenchTranslation : Translation() {
    override val TRANSLATION = TranslationCode.FRENCH

    override val appName = "Seen"
    override val version = "Version"
    override val settings = "Paramètres"
    override val about = "À propos"
    override val back = "Retour"
    override val info = "Info"

    // Welcome Screen
    override val welcomeTitle = "Bienvenue sur Seen"
    override val welcomeSubtitle = "Prenez un moment pour faire le point sur vous-même"
    override val startQuiz = "Commencer l'évaluation"
    override val viewNotes = "Voir les notes"
    override val viewMoodHistory = "Voir l'historique de l'humeur"

    override val welcomeScreenMessages = listOf(
        "Prenez un moment pour faire le point sur vous-même",
        "Ce n'est pas grave de ne pas aller bien",
        "Il y a de l'espoir, même quand votre cerveau vous dit le contraire",
        "Vous n'êtes pas seul(e). Vous êtes vu(e).",
        "Content de vous revoir !",
        "Ravi de vous revoir !",
        "Commençons quelque chose de formidable !",
        "Prêt(e) à faire des progrès ?",
        "J'espère que vous passez une bonne journée !",
        "Faisons que cette journée compte !"
    )

    // PHQ-9 Questions
    override val phq9Questions = listOf(
        "Peu d'intérêt ou de plaisir à faire les choses?",
        "Se sentir déprimé(e), triste ou désespéré(e)?",
        "Difficulté à s'endormir, à rester endormi(e) ou dormir trop?",
        "Se sentir fatigué(e) ou avoir peu d'énergie?",
        "Manque d'appétit ou manger de trop?",
        "Se sentir mal dans sa peau?",
        "Difficulté à se concentrer sur les choses?",
        "Bouger ou parler lentement ou être agité(e)/nerveux(se)?",
        "Pensées que vous seriez mieux mort(e) ou de vous faire du mal?"
    )

    override val phq9Options = listOf(
        "😐 Jamais",
        "😕 Plusieurs jours",
        "😞 Plus de la moitié des jours",
        "😢 Presque tous les jours"
    )

    // Quiz/Question Screen
    override val questionProgress = "Question %d sur %d"

    // Result Screen
    override val resultTitle = "Évaluation terminée"
    override val resultScore = "Votre score"
    override val resultMinimal = "Dépression minimale"
    override val resultMild = "Dépression légère"
    override val resultModerate = "Dépression modérée"
    override val resultSevere = "Dépression sévère"
    override val resultDescription = "Cette évaluation fournit des informations sur votre état de santé mentale actuel. Envisagez de consulter un professionnel de la santé pour un diagnostic approprié et un soutien."
    override val retakeQuiz = "Refaire l'évaluation"
    override val saveResult = "Sauvegarder le résultat"
    override val talkToSomeone = "📞 Parler à quelqu'un – 988"
    override val learnAboutDepression = "📚 En savoir plus sur la dépression"
    override val returnToHome = "🔄 Retour à l'accueil"

    // Settings Screen
    override val appearance = "Apparence"
    override val colorScheme = "Schéma de couleurs"
    override val language = "Langue"
    override val themeAuto = "Auto (Système)"
    override val themeLight = "Clair"
    override val themeDark = "Sombre"
    override val deleteAllData = "Supprimer toutes les données"
    override val deleteDataWarning = "Ceci supprimera définitivement tout votre historique d'humeur, vos notes et vos paramètres. Cette action ne peut pas être annulée."
    override val deleteDataConfirmation = "Êtes-vous sûr de vouloir supprimer toutes les données ?"
    override val cancel = "Annuler"
    override val delete = "Supprimer"
    override val createdBy = "Créé par Damien Boisvert & Alexander Cameron"

    // Settings Screen - Additional
    override val appTitle = "Seen - Évaluation de la santé mentale"
    override val builtOn = "Construit le"
    override val branch = "Branche"
    override val commitMessage = "Message de commit"
    override val assessmentSettings = "Paramètres d'évaluation"
    override val savePHQ9Data = "Sauvegarder les données d'évaluation PHQ-9"
    override val savePHQ9DataDescription = "Stocker les réponses détaillées aux questions localement pour suivre les progrès"
    override val dataManagement = "Gestion des données"
    override val deleteAllDataDescription = "Cela supprimera définitivement toutes vos notes, résultats d'évaluation, entrées d'humeur et réinitialisera tous les paramètres de l'application aux valeurs par défaut. Cette action ne peut pas être annulée."
    override val privacyAndData = "Confidentialité et données"
    override val privacyDescription = "• Toutes les données sont stockées localement sur votre appareil\n• Aucune information personnelle n'est partagée avec des tiers\n• Les résultats d'évaluation restent entièrement privés\n• Vous pouvez supprimer vos données à tout moment"
    override val importantNotice = "Avis important"
    override val disclaimerText = "Cette application ne remplace pas les conseils médicaux professionnels, le diagnostic ou le traitement. Si vous vivez une crise de santé mentale, veuillez contacter les services d'urgence ou consulter un professionnel de la santé qualifié immédiatement."
    override val deleteConfirmTitle = "Supprimer toutes les données ?"
    override val deleteConfirmText = "Cela supprimera définitivement toutes vos notes, résultats d'évaluation, entrées d'humeur et paramètres de l'application. Toutes les données seront effacées et les paramètres seront réinitialisés aux valeurs par défaut. Cette action ne peut pas être annulée.\n\nÊtes-vous sûr de vouloir continuer ?"
    override val deleteAll = "Tout supprimer"
    override val dataDeletedTitle = "Données supprimées avec succès"
    override val dataDeletedText = "Toutes vos données ont été définitivement supprimées et les paramètres ont été réinitialisés aux valeurs par défaut. L'application va maintenant se fermer."
    override val continueButton = "Continuer"

    // Notes Screen
    override val notes = "Notes"
    override val addNote = "Ajouter une note"
    override val editNote = "Modifier la note"
    override val deleteNote = "Supprimer la note"
    override val noteHint = "Écrivez vos pensées ici..."
    override val save = "Sauvegarder"
    override val howAreYouFeeling = "Comment vous sentez-vous ?"
    override val saveNote = "💾 Sauvegarder la note"

    // Mood History Screen
    override val moodHistory = "Historique de l'humeur"
    override val noMoodData = "Aucune entrée d'humeur pour le moment.\nUtilisez le widget pour commencer le suivi !"
    override val score = "Score"
    override val date = "Date"
    override val clearHistory = "Effacer l'historique"
    override val clearMoodHistory = "Effacer l'historique de l'humeur"
    override val clearMoodHistoryConfirmation = "Êtes-vous sûr de vouloir supprimer toutes les entrées d'humeur ? Cette action ne peut pas être annulée."
    override val deleteMoodEntry = "Supprimer l'entrée d'humeur"
    override val deleteMoodEntryConfirmation = "Êtes-vous sûr de vouloir supprimer cette entrée d'humeur %s du %s ?"
    override val todaysStats = "Statistiques d'aujourd'hui"
    override val entriesToday = "Entrées aujourd'hui : %d"
    override val totalEntries = "Total des entrées : %d"
    override val todaysMoods = "Humeurs d'aujourd'hui : %s"
    override val graphText = "Un graphique s'affichera ici une fois que vous aurez terminé deux évaluations PHQ-9 ou plus.\nRevenez plus tard !"
    override val moodEntries = "Entrées d'humeur"
    override val phqResponses = "Réponses au PHQ-9"

    // Onboarding Screen
    override val onboardingTitle = "Bienvenue sur Seen"
    override val onboardingDescription = "Seen est un outil d'évaluation de la santé mentale qui vous aide à suivre votre humeur et votre bien-être au fil du temps en utilisant le questionnaire PHQ-9."
    override val onboardingPrivacy = "Votre vie privée est importante pour nous. Toutes les données sont stockées localement sur votre appareil et ne sont jamais partagées avec des tiers."
    override val onboardingContinue = "Commencer"
    override val onboardingSkip = "Passer"
    override val onboardingWelcomeTitle = "Bienvenue sur Seen"
    override val onboardingWelcomeDesc = "Un compagnon de santé mentale simple et privé conçu pour vous aider à suivre votre bien-être."
    override val onboardingPHQ9Title = "Évaluation PHQ-9"
    override val onboardingPHQ9Desc = "Passez le questionnaire PHQ-9 cliniquement validé pour évaluer votre santé mentale et suivre les changements au fil du temps."
    override val onboardingNotesTitle = "Notes personnelles"
    override val onboardingNotesDesc = "Gardez des notes privées sur vos pensées, sentiments et expériences quotidiennes dans un espace sécurisé."
    override val onboardingPrivacyTitle = "Votre vie privée d'abord"
    override val onboardingPrivacyDesc = "Toutes vos données restent sur votre appareil. Nous ne collectons, stockons ou partageons jamais vos informations personnelles."
    override val onboardingNoAdsTitle = "Aucune publicité"
    override val onboardingNoAdsDesc = "Profitez d'une expérience sans distraction. Cette application est entièrement gratuite et n'affichera jamais de publicités."
    override val onboardingNotificationsTitle = "Restez Connecté"
    override val onboardingNotificationsDesc = "Autorisez les notifications pour recevoir des rappels pour vos bilans de santé mentale et les mises à jour importantes de l'application. Vous pouvez modifier cela à tout moment dans les paramètres."
    override val next = "Suivant"

    // Common
    override val yes = "Oui"
    override val no = "Non"
    override val ok = "OK"
    override val error = "Erreur"
    override val loading = "Chargement..."

    // Update Check
    override val checkForUpdates = "Vérifier les mises à jour"
    override val checkingForUpdates = "Vérification des mises à jour..."
    override val updateAvailable = "Mise à jour disponible"
    override val requiredUpdate = "Mise à jour requise"
    override val noUpdatesAvailable = "Vous êtes à jour !"
    override val noUpdatesAvailableMessage = "Vous utilisez la dernière version de l'application. Aucune mise à jour n'est disponible pour le moment."
    override val updateCheckFailed = "Échec de la vérification des mises à jour"
    override val updateCheckFailedMessage = "Impossible de vérifier les mises à jour. Veuillez vérifier votre connexion internet et réessayer plus tard."
    override val noInternetConnection = "Aucune connexion internet"
    override val noInternetConnectionMessage = "Veuillez vérifier votre connexion internet et réessayer."
    override val internetTroubleshootingTips = "• Vérifiez votre connexion Wi-Fi\n• Vérifiez vos données mobiles\n• Réessayez dans un moment"
    override val updateNow = "Mettre à jour maintenant"
    override val updateLater = "Plus tard"
    override val downloadUpdate = "Télécharger"
    override val whatsNew = "Nouveautés :"
    override val currentVersion = "Version actuelle :"
    override val latestVersion = "Dernière version :"
    override val updateRequired = "Cette mise à jour est requise pour continuer à utiliser l'application."

    // Update Notifications
    override val updateNotificationTitle = "Nouvelle mise à jour disponible"
    override val updateNotificationMessage = "Une nouvelle version de Seen est disponible. Appuyez pour télécharger."
    override val updateNotificationDontRemind = "Ne plus me rappeler"
    override val enableBackgroundUpdateChecks = "Activer les vérifications automatiques"
    override val enableBackgroundUpdateChecksDescription = "Vérifier automatiquement les nouvelles versions toutes les 15 minutes et recevoir des notifications"

    // Enhanced Onboarding
    override val onboardingGetStarted = "Commencer"
    override val onboardingConfigureApp = "Configurer l'application"
    override val onboardingAITitle = "Fonctionnalités IA"
    override val onboardingAIDesc = "Activez les insights et suggestions alimentés par l'IA pour améliorer votre parcours de santé mentale. Tout le traitement se fait localement sur votre appareil."
    override val onboardingAIEnabled = "Activer les fonctionnalités IA"
    override val onboardingAIDisabled = "Garder les fonctionnalités IA désactivées"
    override val onboardingNotificationConfigTitle = "Préférences de notification"
    override val onboardingNotificationConfigDesc = "Personnalisez la façon dont Seen vous tient informé de votre parcours de santé mentale et des mises à jour de l'application."
    override val onboardingAllowNotifications = "Autoriser les notifications"
    override val onboardingEnableReminders = "Activer les rappels doux pour les bilans de santé mentale"
    override val onboardingEnableUpdateChecks = "Activer les notifications de mise à jour automatiques"
    override val onboardingGeneralSettingsTitle = "Personnalisez votre expérience"
    override val onboardingGeneralSettingsDesc = "Choisissez votre thème et langue préférés pour que Seen vous convienne parfaitement."
    override val onboardingChooseTheme = "Choisir le thème"
    override val onboardingChooseLanguage = "Choisir la langue"
    override val onboardingDataPrivacyTitle = "Données et confidentialité"
    override val onboardingDataPrivacyDesc = "Contrôlez comment vos données d'évaluation sont stockées et assurez-vous que votre vie privée est protégée."
    override val onboardingEnableDataStorage = "Sauvegarder les données d'évaluation détaillées pour le suivi des progrès"
    override val onboardingComplete = "Terminer la configuration"
    override val onboardingSetupComplete = "Tout est prêt !"
    override val onboardingSetupCompleteDesc = "Seen est maintenant configuré selon vos préférences. Vous pouvez toujours modifier ces paramètres plus tard."
    override val onboardingReadyToUse = "Commencer à utiliser Seen"

    // Analytics
    override val onboardingAnalyticsTitle = "Aidez à améliorer Seen"
    override val onboardingAnalyticsDesc = "Partagez des données d'utilisation anonymes pour nous aider à améliorer Seen pour tous"
    override val onboardingAnalyticsEnabled = "📈 Partager les données d'utilisation"
    override val onboardingAnalyticsDisabled = "🥺 Garder les données privées"
    override val analyticsPrivacyNote = "Nous ne collectons que des statistiques d'utilisation anonymes pour améliorer l'application. Aucune donnée de santé personnelle n'est jamais partagée."
    override val analyticsDataUsage = "Les données collectées incluent les modèles d'utilisation de l'application, les interactions avec les fonctionnalités et les rapports de plantage. Toutes les données sont anonymes et ne peuvent pas être liées à vous."

    // Settings Screen - Additional Sections
    override val notifications = "Notifications"
    override val enableReminders = "Activer les rappels"
    override val enableRemindersDescription = "Recevez des rappels doux pour les vérifications de santé mentale"
    override val encryption = "Chiffrement"
    override val enableEncryption = "Activer le chiffrement sur l'appareil"
    override val encryptionDescription = "Données sécurisées et privées, sauvegardées sur votre appareil."
    override val aiFeatures = "Fonctionnalités IA"
    override val enableAIFeatures = "Activer les fonctionnalités IA"
    override val aiFeaturesDescription = "Obtenez des perspectives et suggestions personnalisées (Bientôt disponible)"
    override val analytics = "Analytiques"
    override val enableAnalytics = "Activer les analytiques"
    override val analyticsDescription = "Aidez à améliorer Seen en partageant des données d'utilisation anonymes"

    // Welcome Screen - Additional
    override val phq9AssessmentTitle = "Évaluation PHQ-9"
    override val phq9AssessmentDescription = "Un questionnaire confidentiel pour aider à évaluer votre santé mentale et bien-être au cours des deux dernières semaines."

    // Notes Screen - Additional
    override val noEntriesMessage = "Aucune donnée de journal pour le moment. \nPour commencer, utilisez le bouton \" Ajouter une entrée \" dans le coin inférieur droit !"

    // Debug Screens
    override val databaseEncryptionDebug = "Debug du chiffrement de base de données"

    // Resources Screen
    override val backToResults = "Retour aux résultats"
    override val resourcesAndSupport = "Ressources et support"
    override val yourScore = "Votre score"

    // AI Analysis & Results
    override val aiAnalysisInProgress = "Analyse IA en cours..."
    override val aiAnalysisReady = "Analyse IA prête ! Tapez ci-dessous pour voir les perspectives."
    override val aiAnalysisUnavailable = "Analyse IA indisponible. Voir les ressources de base à la place."
    override val preparingAiAnalysis = "Préparation de l'analyse IA..."
    override val viewAiAnalysis = "Voir l'analyse IA"
    override val viewResourcesAndSupport = "Voir les ressources et le support"

    // Resources Screen - Additional Content
    override val crisisSupport = "Support de Crise"
    override val crisisSupportDescription = "Si vous avez des pensées d'automutilation ou de suicide, veuillez chercher de l'aide immédiatement."
    override val maintainingMentalWellness = "Maintenir le Bien-être Mental"
    override val maintainingWellnessDescription = "Votre score indique des symptômes minimes. Voici des ressources pour maintenir votre santé mentale."
    override val supportTreatmentOptions = "Options de Support et Traitement"
    override val supportTreatmentDescription = "Votre score suggère des symptômes légers à modérés. Considérez ces options de support."
    override val immediateProfessionalHelp = "Aide Professionnelle Immédiate"
    override val immediateProfessionalDescription = "Votre score indique des symptômes plus sévères. L'aide professionnelle est fortement recommandée."
    override val educationalResources = "Ressources Éducatives"
    override val educationalResourcesDescription = "Apprenez-en plus sur la santé mentale et la dépression."
    override val backToResults2 = "Retour aux Résultats"
    override val mentalHealthTips = "Conseils de Santé Mentale"
    override val mindfulnessResources = "Ressources de Pleine Conscience"
    override val findATherapist = "Trouver un Thérapeute"
    override val findProfessionalHelpNow = "Trouver de l'Aide Professionnelle Maintenant"
    override val crisisResources = "Ressources de Crise"
    override val nimhDepressionInfo = "Info Dépression NIMH"
    override val mentalHealthGov = "MentalHealth.gov"
    override val namiResources = "Ressources NAMI"

    // Health Status
    override val serviceHealthStatus = "État de santé des services"
    override val analyticsService = "Analytiques"
    override val releasesService = "Versions"
    override val aiService = "IA"
    override val healthyStatus = "Sain"
    override val unhealthyStatus = "Défaillant"
    override val unknownStatus = "Inconnu"
    override val refreshStatus = "Actualiser"
    override val closeDialog = "Fermer"

    //Moods
    override val veryHappy = "Très heureux"
    override val happy = "Heureux"
    override val neutral = "Neutre"
    override val sad = "Triste"
    override val verySad = "Très triste"
    override val angry = "En colère"
    override val anxious = "Anxieux"
    override val excited = "Excité"
}
