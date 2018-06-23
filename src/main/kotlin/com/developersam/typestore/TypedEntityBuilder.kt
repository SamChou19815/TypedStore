package com.developersam.typestore

import com.developersam.typestore.PropertyType.BLOB
import com.developersam.typestore.PropertyType.BOOL
import com.developersam.typestore.PropertyType.DOUBLE
import com.developersam.typestore.PropertyType.KEY
import com.developersam.typestore.PropertyType.LAT_LNG
import com.developersam.typestore.PropertyType.LONG
import com.developersam.typestore.PropertyType.STRING
import com.developersam.typestore.PropertyType.TIMESTAMP
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.LatLng

/**
 * [TypedEntityBuilder] is responsible for building a [TypedEntity].
 *
 * @constructor the internal constructor is called by a [partialBuilder] and a [table] associated
 * with the entity.
 * @param Tbl precise type of the [table].
 * @param E precise type of the [TypedEntity].
 */
class TypedEntityBuilder<Tbl : TypedTable<Tbl>, E : TypedEntity<Tbl>> internal constructor(
        private val table: Tbl, private val partialBuilder: Entity.Builder
) {

    private val registeredProperties: HashSet<Property<Tbl, *>> = hashSetOf()

    /**
     * [set] sets the value of the [property] to be [value].
     */
    operator fun <T> set(property: Property<Tbl, T>, value: T) {
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
            BLOB -> partialBuilder.set(property.name, value as Blob)
            TIMESTAMP -> partialBuilder.set(property.name, value as Timestamp)
            LAT_LNG -> partialBuilder.set(property.name, value as LatLng)
        }
    }

    /**
     * [buildEntity] builds the builder into a raw Datastore [Entity].
     */
    internal fun buildEntity(): Entity {
        for (property in table.registeredProperties) {
            TODO(reason = "Either here or somewhere else, check for exhaustiveness.")
        }
        return partialBuilder.build()
    }

}
