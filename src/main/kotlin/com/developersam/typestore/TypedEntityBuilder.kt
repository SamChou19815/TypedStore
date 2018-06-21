package com.developersam.typestore

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key

/**
 * [TypedEntityBuilder] is responsible for building a [TypedEntity].
 *
 * @param E precise type of the [TypedEntity].
 */
class TypedEntityBuilder<E: TypedEntity> internal constructor(
        private val partialBuilder: Entity.Builder, private val creator: TypedEntityCreator<E>
) {

    /**
     * [set] sets the value of the [property] to be [value].
     */
    operator fun <T> set(property: Property<T>, value: T) {
        if (value == null) {
            partialBuilder.setNull(property.name)
            return
        }
        when (property.type) {
            PropertyType.KEY -> partialBuilder.set(property.name, value as Key)
            PropertyType.LONG -> partialBuilder.set(property.name, value as Long)
            PropertyType.DOUBLE -> partialBuilder.set(property.name, value as Double)
            PropertyType.BOOL -> partialBuilder.set(property.name, value as Boolean)
            PropertyType.STRING -> partialBuilder.set(property.name, value as String)
            PropertyType.BLOB -> partialBuilder.set(property.name, value as Blob)
            PropertyType.TIMESTAMP -> partialBuilder.set(property.name, value as Timestamp)
        }
    }

    /**
     * [buildRawEntity] builds the builder into a raw Datastore [Entity].
     */
    internal fun buildRawEntity(): Entity = partialBuilder.build()

    /**
     * [build] builds the builder into a full [TypedEntity]
     */
    internal fun build(): E = creator.create(entity = buildRawEntity())

}
