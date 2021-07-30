import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.io.path.writeText

object Queue {
    private val queue = ArrayDeque<String>()

    fun readFromFile() {
        thread {
            val path = Path("src") / "main" / "resources" / "queue.json"
            val text = try {
                path.readText()
            } catch (e: Exception) {
                ""
            }
            if (text.isNotEmpty()) {
                queue.clear()
                val read = ArrayDeque(
                    try {
                        Json.decodeFromString<List<String>>(text)
                    } catch (e: Exception) {
                        emptyList()
                    }
                )
                while (read.isNotEmpty()) {
                    queue.addLast(read.removeFirst())
                }
            }
        }
    }

    fun rewrite(list: List<String>) {
        queue.clear()
        list.forEach(queue::add)

    }

    fun round() {
        if (queue.isNotEmpty()) queue.addLast(queue.removeFirst())
        thread {
            val path = Path("src\\main\\resources\\queue.json")
            path.writeText(
                Json.encodeToString(queue.toList())
            )
        }
    }

    fun namesByLines() = queue.joinToString(",\n")
}