package consumer

import com.phantomvk.identifier.IdentifierManager

@Suppress("unused")
object LinkSmoke {
    fun ref(): Class<*> = IdentifierManager::class.java
}
