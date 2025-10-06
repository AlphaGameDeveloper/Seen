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

        @Composable
        fun getRandomWelcomeScreenMessageGenerator(): () -> String {
            val translation = rememberTranslation()
            val messages = translation.welcomeScreenMessages
            val random = Random()

            fun _generator(): String {
                val index = random.nextInt(messages.size)
                val msg = messages[index]
                Log.d("WelcomeScreenMessage", "Generator called, using index $index ($msg) :3")
                return msg
            }
            return ::_generator
        }
    }
}
