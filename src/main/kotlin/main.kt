import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.chain
import com.elbekD.bot.feature.chain.terminateChain
import com.elbekD.bot.http.await
import com.elbekD.bot.types.BotCommand
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

const val TOKEN = "1915026166:AAGDQgil7OYETNqQcMkpdve3TeUHSWNSN30"
const val USERNAME = "CleaningQueueBot"

fun main() {
    val bot = Bot.createPolling(USERNAME, TOKEN)
    Queue.readFromFile()
    with(bot) {
        setMyCommands(
            listOf(
                BotCommand("/getqueue", "Get current queue"),
                BotCommand("/setqueue", "Set new queue"),
                BotCommand("/round", "Make round")
            )
        )

        onCommand("/start") { msg, _ ->
            sendMessage(msg.chat.id, "Hello! type /setQueue to set Queue")
        }

        chain("/setqueue") {
            sendMessage(
                it.chat.id,
                "Okay! Enter new names separated by commas or 'cancel' to cancel"
            )
        }.then {
            it.text?.let { s ->
                if (s.trim().lowercase() == "cancel") {
                    sendMessage(it.chat.id, "Okay!")
                    terminateChain(it.chat.id)
                } else {
                    Queue.rewrite(
                        s.split(",").map { name ->
                            name.trim().split(" ").joinToString(" ") { subName ->
                                subName.lowercase().replaceFirstChar(Char::titlecase)
                            }
                        }
                    )
                    sendMessage(
                        it.chat.id,
                        "Fine! There's new queue: \n${Queue.namesByLines()}"
                    )
                }
            } ?: sendMessage(it.chat.id, "You haven't sent names")
        }.build()

        onCommand("/getqueue") { msg, _ ->
            sendMessage(msg.chat.id, "Here's current queue: \n${Queue.namesByLines()}")
        }

        onCommand("/round") { msg, _ ->
            Queue.round()
            sendMessage(msg.chat.id, "Here's new queue: \n${Queue.namesByLines()}")
        }

        start()
        println("Started!")
    }
}


