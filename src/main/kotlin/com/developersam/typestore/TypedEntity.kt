package com.developersam.typestore

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key

/**
 * [TypedEntity] represents an entity with full data from the datastore in [rawEntity].
 *
 * @property rawEntity the entity for GCP datastore.
 */
abstract class TypedEntity protected constructor(val rawEntity: Entity) {

    /**
     * [key] returns the key of the entity.
     */
    val key: Key get() = rawEntity.key

    /**
     * [delegatedValue] automatically finds and returns the correct value associated with the
     * property from the property and the [rawEntity] provided.
     *
     * @param T type of the value stored in the property.
     */
    protected val <T> Property<T>.delegatedValue: T
        get() {
            val value: Any? = when (type) {
                PropertyType.KEY -> rawEntity.takeUnless { it.isNull(name) }?.getKey(name)
                PropertyType.LONG -> rawEntity.takeUnless { it.isNull(name) }?.getLong(name)
                PropertyType.DOUBLE -> rawEntity.takeUnless { it.isNull(name) }?.getDouble(name)
                PropertyType.BOOL -> rawEntity.takeUnless { it.isNull(name) }?.getBoolean(name)
                PropertyType.STRING -> rawEntity.takeUnless { it.isNull(name) }?.getString(name)
                PropertyType.BLOB -> rawEntity.takeUnless { it.isNull(name) }?.getBlob(name)
                PropertyType.TIMESTAMP ->
                    rawEntity.takeUnless { it.isNull(name) }?.getTimestamp(name)
            }
            @Suppress(names = ["UNCHECKED_CAST"])
            return value as T
        }

}
