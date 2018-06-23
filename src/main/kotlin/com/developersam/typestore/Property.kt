package com.developersam.typestore

import com.google.cloud.datastore.StructuredQuery.OrderBy

/**
 * [Property] represents a property in the entity with a specified type.
 *
 * @property name name of the property.
 * @property type type of the property in this framework.
 * @param Tbl type of the table.
 * @param T type of the property.
 */
class Property<Tbl: TypedTable<Tbl>, T> internal constructor(
        internal val name: String, internal val type: PropertyType
) {

    override fun toString(): String = "Property{ name: \"$name\", type: ${type.name} }"

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Property<*, *> -> false
        else -> name == other.name
    }

    override fun hashCode(): Int = name.hashCode()

    /**
     * [eq] creates and returns an equality filter from this property and a [value].
     */
    infix fun eq(value: T): TypedFilter<Tbl> = TypedFilter.Eq(property = this, value = value)

    /**
     * [lt] creates and returns a less-than filter from this property and a [value].
     */
    infix fun lt(value: T): TypedFilter<Tbl> = TypedFilter.Lt(property = this, value = value)

    /**
     * [le] creates and returns a less-than-or-eq filter from this property and a [value].
     */
    infix fun le(value: T): TypedFilter<Tbl> = TypedFilter.Le(property = this, value = value)

    /**
     * [gt] creates and returns an greater-than filter from this property and a [value].
     */
    infix fun gt(value: T): TypedFilter<Tbl> = TypedFilter.Gt(property = this, value = value)

    /**
     * [ge] creates and returns an greater-than-or-eq filter from this property and a [value].
     */
    infix fun ge(value: T): TypedFilter<Tbl> = TypedFilter.Ge(property = this, value = value)

    /**
     * [asc] returns an order on this property in ascending order.
     */
    fun asc(): OrderBy = OrderBy.asc(name)

    /**
     * [desc] returns an order on this property in descending order.
     */
    fun desc(): OrderBy = OrderBy.desc(name)

}
