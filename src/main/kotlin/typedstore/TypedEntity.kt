package typedstore

import typedstore.Property.EnumProperty
import typedstore.PropertyType.BLOB
import typedstore.PropertyType.BOOL
import typedstore.PropertyType.DATE_TIME
import typedstore.PropertyType.DOUBLE
import typedstore.PropertyType.ENUM
import typedstore.PropertyType.KEY
import typedstore.PropertyType.LAT_LNG
import typedstore.PropertyType.LONG
import typedstore.PropertyType.LONG_STRING
import typedstore.PropertyType.STRING
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StringValue

/**
 * [TypedEntity] represents an entity with full data from the datastore in [entity].
 *
 * @property entity the entity for GCP datastore.
 * @param Tbl the type of that table the entity is associated to.
 */
abstract class TypedEntity<Tbl : TypedTable<Tbl>> protected constructor(
        @field:Transient val entity: Entity
) {

    /**
     * [key] returns the key of the entity.
     */
    val key: Key get() = entity.key

    /**
     * [safeDelegate] uses [f] to delegate a value with property null checks.
     */
    private inline fun <T> Property<Tbl, *>.safeDelegate(f: (Entity).(String) -> T): T? =
            entity.takeIf { it.contains(name) }?.takeUnless { it.isNull(name) }?.f(name)

    /**
     * [delegatedValue] automatically finds and returns the correct value associated with the
     * property from the property and the [entity] provided.
     *
     * @param T type of the value stored in the property.
     */
    protected val <T> Property<Tbl, T>.delegatedValue: T
        get() {
            val value: Any? = when (type) {
                KEY -> safeDelegate(Entity::getKey)
                LONG -> safeDelegate(Entity::getLong)
                DOUBLE -> safeDelegate(Entity::getDouble)
                BOOL -> safeDelegate(Entity::getBoolean)
                STRING -> safeDelegate(Entity::getString)
                LONG_STRING -> safeDelegate { getValue<StringValue>(it).get() }
                ENUM -> safeDelegate { name ->
                    val enumProp = this@delegatedValue as EnumProperty
                    enumProp.valueOf(getString(name))
                }
                BLOB -> safeDelegate(Entity::getBlob)
                DATE_TIME -> safeDelegate { getTimestamp(it).toLocalDateTime() }
                LAT_LNG -> safeDelegate(Entity::getLatLng)
            }
            @Suppress(names = ["UNCHECKED_CAST"])
            return value as T
        }

}
