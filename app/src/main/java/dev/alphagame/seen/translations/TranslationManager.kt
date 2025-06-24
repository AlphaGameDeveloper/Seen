package dev.alphagame.seen.translations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.alphagame.seen.data.PreferencesManager

val LocalTranslation = compositionLocalOf<Translation> { 
    EnglishTranslation()
}

@Composable
fun TranslationProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val translation = remember(preferencesManager.language) {
        Translation.getTranslation(preferencesManager.language)
    }
    
    CompositionLocalProvider(LocalTranslation provides translation) {
        content()
    }
}

@Composable
fun rememberTranslation(): Translation {
    return LocalTranslation.current
}
