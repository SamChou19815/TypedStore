package typedstore.gson

import com.google.cloud.datastore.Cursor
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * [CursorTypeAdapter] is the type adapter for Cursor.
 */
object CursorTypeAdapter : JsonDeserializer<Cursor>, JsonSerializer<Cursor> {

    /**
     * Simple implementation with `Cursor.fromUrlSafe`.
     */
    override fun deserialize(element: JsonElement, t: Type, c: JsonDeserializationContext): Cursor =
            Cursor.fromUrlSafe(element.asJsonPrimitive.asString)

    /**
     * Simple implementation with `Cursor.toUrlSafe`.
     */
    override fun serialize(cursor: Cursor, t: Type, c: JsonSerializationContext): JsonElement =
            JsonPrimitive(cursor.toUrlSafe())

}
