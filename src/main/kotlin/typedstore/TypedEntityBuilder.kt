package typedstore

import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.LatLng
import com.google.cloud.datastore.StringValue
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
import java.time.LocalDateTime

/**
 * [TypedEntityBuilder] is responsible for building a [TypedEntity].
 *
 * @constructor the internal constructor is called by a [partialBuilder] and a [table] associated
 * with the entity.
 * @property table the table associated with the builder.
 * @property partialBuilder the partially constructed builder.
 * @property unusedProperties the properties that have not been set by the user.
 * @param Tbl precise type of the [table].
 * @param E precise type of the [TypedEntity].
 */
class TypedEntityBuilder<Tbl : TypedTable<Tbl>, E : TypedEntity<Tbl>> private constructor(
        val table: Tbl, private val partialBuilder: Entity.Builder,
        private val unusedProperties: HashSet<Property<Tbl, *>>
) {

    internal constructor(table: Tbl, newKey: Key) : this(
            table = table, partialBuilder = Entity.newBuilder(newKey),
            unusedProperties = HashSet(table.registeredProperties)
    )

    internal constructor(table: Tbl, existingEntity: E) : this(
            table = table, partialBuilder = Entity.newBuilder(existingEntity.entity),
            unusedProperties = hashSetOf()
    )

    /**
     * [gets] sets the value of this property to be [value].
     */
    infix fun <T> Property<Tbl, T>.gets(value: T) {
        unusedProperties.remove(element = this)
        if (value == null) {
            partialBuilder.setNull(name)
            return
        }
        when (type) {
            KEY -> partialBuilder.set(name, value as Key)
            LONG -> partialBuilder.set(name, value as Long)
            DOUBLE -> partialBuilder.set(name, value as Double)
            BOOL -> partialBuilder.set(name, value as Boolean)
            STRING -> partialBuilder.set(name, value as String)
            LONG_STRING -> {
                val stringValue = StringValue.newBuilder(value as String)
                        .setExcludeFromIndexes(true).build()
                partialBuilder.set(name, stringValue)
            }
            ENUM -> partialBuilder.set(name, (value as Enum<*>).name)
            BLOB -> partialBuilder.set(name, value as Blob)
            DATE_TIME -> {
                val datetime = value as LocalDateTime
                partialBuilder.set(name, datetime.toGcpTimestamp())
            }
            LAT_LNG -> partialBuilder.set(name, value as LatLng)
        }
    }

    /**
     * [set] sets the value of the [property] to be [value].
     */
    operator fun <T> set(property: Property<Tbl, T>, value: T): Unit = property gets value

    /**
     * [buildEntity] builds the builder into a raw Datastore [Entity].
     *
     * If some fields are not properly declared, an [IllegalStateException] will be thrown.
     */
    internal fun buildEntity(): Entity {
        if (unusedProperties.isNotEmpty()) {
            throw IllegalStateException("Some property has not been declared. This is bad.")
        }
        return partialBuilder.build()
    }

}
