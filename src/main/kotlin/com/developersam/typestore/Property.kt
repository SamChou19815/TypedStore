package com.developersam.typestore

import com.google.cloud.datastore.StructuredQuery.OrderBy

/**
 * [Property] represents a property in the entity with a specified type.
 *
 * @property name name of the property.
 * @property type type of the property in this framework.
 * @param T type of the property.
 */
class Property<T> internal constructor(
        internal val name: String, internal val type: PropertyType
) {

    /**
     * [eq] creates and returns an equality filter from this property and a [value].
     */
    infix fun eq(value: T): TypedFilter = TypedFilter.Eq(property = this, value = value)

    /**
     * [lt] creates and returns a less-than filter from this property and a [value].
     */
    infix fun lt(value: T): TypedFilter = TypedFilter.Lt(property = this, value = value)

    /**
     * [le] creates and returns a less-than-or-eq filter from this property and a [value].
     */
    infix fun le(value: T): TypedFilter = TypedFilter.Le(property = this, value = value)

    /**
     * [gt] creates and returns an greater-than filter from this property and a [value].
     */
    infix fun gt(value: T): TypedFilter = TypedFilter.Gt(property = this, value = value)

    /**
     * [ge] creates and returns an greater-than-or-eq filter from this property and a [value].
     */
    infix fun ge(value: T): TypedFilter = TypedFilter.Ge(property = this, value = value)

    /**
     * [asc] returns an order on this property in ascending order.
     */
    fun asc(): OrderBy = OrderBy.asc(name)

    /**
     * [desc] returns an order on this property in descending order.
     */
    fun desc(): OrderBy = OrderBy.desc(name)

}
