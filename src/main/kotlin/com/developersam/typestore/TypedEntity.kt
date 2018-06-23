package com.developersam.typestore

import com.developersam.typestore.PropertyType.BLOB
import com.developersam.typestore.PropertyType.BOOL
import com.developersam.typestore.PropertyType.DOUBLE
import com.developersam.typestore.PropertyType.KEY
import com.developersam.typestore.PropertyType.LAT_LNG
import com.developersam.typestore.PropertyType.LONG
import com.developersam.typestore.PropertyType.STRING
import com.developersam.typestore.PropertyType.TIMESTAMP
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key

/**
 * [TypedEntity] represents an entity with full data from the datastore in [entity].
 *
 * @property entity the entity for GCP datastore.
 * @param Tbl the type of that table the entity is associated to.
 */
abstract class TypedEntity<Tbl : TypedTable<Tbl>> protected constructor(val entity: Entity) {

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
                BLOB -> safeDelegate(Entity::getBlob)
                TIMESTAMP -> safeDelegate(Entity::getTimestamp)
                LAT_LNG -> safeDelegate(Entity::getLatLng)
            }
            @Suppress(names = ["UNCHECKED_CAST"])
            return value as T
        }

}
