package dev.alphagame.seen.data

import android.util.Log
import java.util.Random;

class WelcomeScreenMessage {

    companion object {
        val messages: Array<String> = arrayOf(
            "Welcome Text Option 1",
            "Welcome Text Option 2",
            "Welcome Text Option 3",
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
