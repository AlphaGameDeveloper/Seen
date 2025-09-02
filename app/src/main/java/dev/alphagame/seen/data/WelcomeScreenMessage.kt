package dev.alphagame.seen.data

import android.util.Log
import java.util.Random;

class WelcomeScreenMessage {
    companion object {
        private val messages: Array<String> = arrayOf(
            "Welcome Text Option 1",
            "Welcome Text Option 2",
            "Welcome Text Option 3",
            "Welcome Text Option 4"
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
