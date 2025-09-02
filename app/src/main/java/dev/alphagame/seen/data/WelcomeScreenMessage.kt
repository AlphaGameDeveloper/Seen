package dev.alphagame.seen.data

import android.util.Log
import java.util.Random;

class WelcomeScreenMessage {
    companion object {
        private val messages: Array<String> = arrayOf(
            "Take a moment to check in with yourself",
            "It's ok to not be ok, it's not ok to stay that way",
            "'There is hope, even when your brain tells you there isn't' - John Green",
            "'You are not alone. You are seen. I am with you. You are not alone.' - Shonda Rhimes",
            "Welcome back!",
            "Good to see you again!",
            "Let's begin something great!",
            "Ready to make progress?",
            "Hope you're having a good day!",
            "Let's make today count!"
        )

        fun getRandomWelcomeScreenMessage(): String {
            // damien - java.util.Random.nextInt is between 0 (inclusive) and
            // X (exclusive), so we don't need to subtract 1 from size because
            // the function already does that. :3
            val index = Random().nextInt(messages.size)
            val message =  messages[index]

            Log.d("WelcomeScreenMessage","Chose welcome screen message index $index ($message)")
            return message
        }
    }
}
