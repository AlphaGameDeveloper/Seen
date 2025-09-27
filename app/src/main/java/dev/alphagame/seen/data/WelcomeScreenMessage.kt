package dev.alphagame.seen.data

import android.util.Log
import androidx.compose.runtime.Composable
import dev.alphagame.seen.translations.rememberTranslation
import java.util.Random

class WelcomeScreenMessage {
    companion object {
        @Composable
        fun getRandomWelcomeScreenMessage(): String {
            val translation = rememberTranslation()
            val messages = translation.welcomeScreenMessages

            // damien - java.util.Random.nextInt is between 0 (inclusive) and
            // X (exclusive), so we don't need to subtract 1 from size because
            // the function already does that. :3
            val index = Random().nextInt(messages.size)
            val message = messages[index]

            Log.d("WelcomeScreenMessage","Chose welcome screen message index $index ($message)")
            return message
        }
    }
}
