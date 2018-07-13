package typedstore.gson

import com.google.cloud.datastore.Key
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * [KeyTypeAdapter] is the type adapter for Key.
 */
object KeyTypeAdapter : JsonDeserializer<Key>, JsonSerializer<Key> {

    /**
     * Simple implementation with `Key.fromUrlSafe`.
     */
    override fun deserialize(element: JsonElement, t: Type, c: JsonDeserializationContext): Key =
            Key.fromUrlSafe(element.asJsonPrimitive.asString)

    /**
     * Simple implementation with `Key.toUrlSafe`.
     */
    override fun serialize(key: Key, t: Type, c: JsonSerializationContext): JsonElement =
            JsonPrimitive(key.toUrlSafe())

}
