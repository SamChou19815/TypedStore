package typedstore

import com.google.cloud.datastore.BaseEntity
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StringValue
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
     * [delegatedValue] automatically finds and returns the correct value associated with the
     * property from the property and the [entity] provided.
     *
     * @param T type of the value stored in the property.
     */
    protected val <T> Property<Tbl, T>.delegatedValue: T
        get() {
            val value: Any? = when (type) {
                KEY -> safeDelegate(entity = entity, f = BaseEntity<Key>::getKey)
                LONG -> safeDelegate(entity = entity, f = BaseEntity<Key>::getLong)
                DOUBLE -> safeDelegate(entity = entity, f = BaseEntity<Key>::getDouble)
                BOOL -> safeDelegate(entity = entity, f = BaseEntity<Key>::getBoolean)
                STRING -> safeDelegate(entity = entity, f = BaseEntity<Key>::getString)
                LONG_STRING -> safeDelegate(entity = entity) { getValue<StringValue>(it).get() }
                ENUM -> safeDelegate(entity = entity) { name ->
                    val enumProp = this@delegatedValue as EnumProperty
                    enumProp.valueOf(getString(name))
                }
                BLOB -> safeDelegate(entity = entity, f = BaseEntity<Key>::getBlob)
                DATE_TIME -> safeDelegate(entity = entity) { getTimestamp(it).toLocalDateTime() }
                LAT_LNG -> safeDelegate(entity = entity, f = BaseEntity<Key>::getLatLng)
            }
            @Suppress(names = ["UNCHECKED_CAST"])
            return value as T
        }

}
