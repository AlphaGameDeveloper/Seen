package dev.alphagame.seen.data

import android.util.Log
import java.util.Random;

class WelcomeScreenMessage {

    companion object {
        val messages: Array<String> = arrayOf(
            "Take a moment to check in with yourself",
            "It's ok to not be ok",
            "'There is hope, even when your brain tells you there isn't' - John Green",
            "Welcome Text Option 4"
        )

        fun getRandomWelcomeScreenMessage(): String {
            val random = Random()
            val index = random.nextInt(messages.size)
            val message =  messages[index]
            Log.d("WelcomeScreenMessage", "Chose welcome screen message index $index ($message)")
            return message
        }


    }
}
