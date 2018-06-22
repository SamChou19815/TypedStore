package com.developersam.typestore

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key

/**
 * [TypedEntity] represents an entity with full data from the datastore in [entity].
 *
 * @property entity the entity for GCP datastore.
 */
abstract class TypedEntity protected constructor(val entity: Entity) {

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
    protected val <T> Property<T>.delegatedValue: T
        get() {
            val value: Any? = when (type) {
                PropertyType.KEY -> entity.takeUnless { it.isNull(name) }?.getKey(name)
                PropertyType.LONG -> entity.takeUnless { it.isNull(name) }?.getLong(name)
                PropertyType.DOUBLE -> entity.takeUnless { it.isNull(name) }?.getDouble(name)
                PropertyType.BOOL -> entity.takeUnless { it.isNull(name) }?.getBoolean(name)
                PropertyType.STRING -> entity.takeUnless { it.isNull(name) }?.getString(name)
                PropertyType.BLOB -> entity.takeUnless { it.isNull(name) }?.getBlob(name)
                PropertyType.TIMESTAMP -> entity.takeUnless { it.isNull(name) }?.getTimestamp(name)
                PropertyType.LAT_LNG -> entity.takeUnless { it.isNull(name) }?.getLatLng(name)
            }
            @Suppress(names = ["UNCHECKED_CAST"])
            return value as T
        }

}
