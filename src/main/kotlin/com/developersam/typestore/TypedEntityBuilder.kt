package com.developersam.typestore

import com.developersam.typestore.PropertyType.BLOB
import com.developersam.typestore.PropertyType.BOOL
import com.developersam.typestore.PropertyType.DOUBLE
import com.developersam.typestore.PropertyType.KEY
import com.developersam.typestore.PropertyType.LAT_LNG
import com.developersam.typestore.PropertyType.LONG
import com.developersam.typestore.PropertyType.LONG_STRING
import com.developersam.typestore.PropertyType.STRING
import com.developersam.typestore.PropertyType.TIMESTAMP
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.LatLng
import com.google.cloud.datastore.StringValue

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
        private val table: Tbl, private val partialBuilder: Entity.Builder,
        private val unusedProperties: HashSet<Property<Tbl, *>>
) {

    internal constructor(table: Tbl, newKey: Key) : this(
            table = table, partialBuilder = Entity.newBuilder(newKey),
            unusedProperties = hashSetOf()
    )

    internal constructor(table: Tbl, existingEntity: E) : this(
            table = table, partialBuilder = Entity.newBuilder(existingEntity.entity),
            unusedProperties = HashSet(table.registeredProperties)
    )

    /**
     * [set] sets the value of the [property] to be [value].
     */
    operator fun <T> set(property: Property<Tbl, T>, value: T) {
        unusedProperties.remove(element = property)
        if (value == null) {
            partialBuilder.setNull(property.name)
            return
        }
        when (property.type) {
            KEY -> partialBuilder.set(property.name, value as Key)
            LONG -> partialBuilder.set(property.name, value as Long)
            DOUBLE -> partialBuilder.set(property.name, value as Double)
            BOOL -> partialBuilder.set(property.name, value as Boolean)
            STRING -> partialBuilder.set(property.name, value as String)
            LONG_STRING -> {
                val stringValue = StringValue.newBuilder(value as String)
                        .setExcludeFromIndexes(true).build()
                partialBuilder.set(property.name, stringValue)
            }
            BLOB -> partialBuilder.set(property.name, value as Blob)
            TIMESTAMP -> partialBuilder.set(property.name, value as Timestamp)
            LAT_LNG -> partialBuilder.set(property.name, value as LatLng)
        }
    }

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
