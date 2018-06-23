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
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StructuredQuery.CompositeFilter
import com.google.cloud.datastore.StructuredQuery.Filter
import com.google.cloud.datastore.StructuredQuery.PropertyFilter

/**
 * [TypedFilter] represents a set of filter adapters provided by this system that is type-safe.
 *
 * @param Tbl precise type of the table to apply the filter.
 */
sealed class TypedFilter<Tbl: TypedTable<Tbl>> {

    /**
     * [asFilter] returns the [Filter] form of this typed filter.
     */
    abstract val asFilter: Filter

    /**
     * [and] connects this filter and [other] filter.
     */
    infix fun and(other: TypedFilter<Tbl>): TypedFilter<Tbl> = And(f1 = this, f2 = other)

    /**
     * [Eq] represents a filter that requires the [property] to be equal to [value].
     */
    internal class Eq<Tbl: TypedTable<Tbl>, T>(
            private val property: Property<Tbl, T>, private val value: T
    ) : TypedFilter<Tbl>() {

        override val asFilter: Filter
            get() {
                val name = property.name
                if (value == null) {
                    return PropertyFilter.isNull(name)
                }
                return when (property.type) {
                    KEY -> PropertyFilter.eq(name, value as Key)
                    LONG -> PropertyFilter.eq(property.name, value as Long)
                    DOUBLE -> PropertyFilter.eq(property.name, value as Double)
                    BOOL -> PropertyFilter.eq(property.name, value as Boolean)
                    STRING -> PropertyFilter.eq(property.name, value as String)
                    BLOB -> PropertyFilter.eq(property.name, value as Blob)
                    TIMESTAMP -> PropertyFilter.eq(property.name, value as Timestamp)
                    LAT_LNG -> throw UnsupportedOperationException("LAT_LANG is not supported.")
                }
            }

    }

    /**
     * [Lt] represents a filter that requires the [property] to be less than [value].
     */
    internal class Lt<Tbl: TypedTable<Tbl>, T>(
            private val property: Property<Tbl, T>, private val value: T
    ) : TypedFilter<Tbl>() {

        override val asFilter: Filter
            get() {
                if (value == null) {
                    throw IllegalArgumentException("Value cannot be null!")
                }
                return when (property.type) {
                    KEY -> PropertyFilter.lt(property.name, value as Key)
                    LONG -> PropertyFilter.lt(property.name, value as Long)
                    DOUBLE -> PropertyFilter.lt(property.name, value as Double)
                    BOOL -> PropertyFilter.lt(property.name, value as Boolean)
                    STRING -> PropertyFilter.lt(property.name, value as String)
                    BLOB -> PropertyFilter.lt(property.name, value as Blob)
                    TIMESTAMP -> PropertyFilter.lt(property.name, value as Timestamp)
                    LAT_LNG -> throw UnsupportedOperationException("LAT_LANG is not supported.")
                }
            }

    }

    /**
     * [Le] represents a filter that requires the [property] to be less than or equal to [value].
     */
    internal class Le<Tbl: TypedTable<Tbl>, T>(
            private val property: Property<Tbl, T>, private val value: T
    ) : TypedFilter<Tbl>() {

        override val asFilter: Filter
            get() {
                if (value == null) {
                    throw IllegalArgumentException("Value cannot be null!")
                }
                return when (property.type) {
                    KEY -> PropertyFilter.le(property.name, value as Key)
                    LONG -> PropertyFilter.le(property.name, value as Long)
                    DOUBLE -> PropertyFilter.le(property.name, value as Double)
                    BOOL -> PropertyFilter.le(property.name, value as Boolean)
                    STRING -> PropertyFilter.le(property.name, value as String)
                    BLOB -> PropertyFilter.le(property.name, value as Blob)
                    TIMESTAMP -> PropertyFilter.le(property.name, value as Timestamp)
                    LAT_LNG -> throw UnsupportedOperationException("LAT_LANG is not supported.")
                }
            }

    }

    /**
     * [Gt] represents a filter that requires the [property] to be greater than [value].
     */
    internal class Gt<Tbl: TypedTable<Tbl>, T>(
            private val property: Property<Tbl, T>, private val value: T
    ) : TypedFilter<Tbl>() {

        override val asFilter: Filter
            get() {
                if (value == null) {
                    throw IllegalArgumentException("Value cannot be null!")
                }
                return when (property.type) {
                    KEY -> PropertyFilter.gt(property.name, value as Key)
                    LONG -> PropertyFilter.gt(property.name, value as Long)
                    DOUBLE -> PropertyFilter.gt(property.name, value as Double)
                    BOOL -> PropertyFilter.gt(property.name, value as Boolean)
                    STRING -> PropertyFilter.gt(property.name, value as String)
                    BLOB -> PropertyFilter.gt(property.name, value as Blob)
                    TIMESTAMP -> PropertyFilter.gt(property.name, value as Timestamp)
                    LAT_LNG -> throw UnsupportedOperationException("LAT_LANG is not supported.")
                }
            }

    }

    /**
     * [Ge] represents a filter that requires the [property] to be greater than or equal to [value].
     */
    internal class Ge<Tbl: TypedTable<Tbl>, T>(
            private val property: Property<Tbl, T>, private val value: T
    ) : TypedFilter<Tbl>() {

        override val asFilter: Filter
            get() {
                if (value == null) {
                    throw IllegalArgumentException("Value cannot be null!")
                }
                return when (property.type) {
                    KEY -> PropertyFilter.ge(property.name, value as Key)
                    LONG -> PropertyFilter.ge(property.name, value as Long)
                    DOUBLE -> PropertyFilter.ge(property.name, value as Double)
                    BOOL -> PropertyFilter.ge(property.name, value as Boolean)
                    STRING -> PropertyFilter.ge(property.name, value as String)
                    BLOB -> PropertyFilter.ge(property.name, value as Blob)
                    TIMESTAMP -> PropertyFilter.ge(property.name, value as Timestamp)
                    LAT_LNG -> throw UnsupportedOperationException("LAT_LANG is not supported.")
                }
            }

    }

    /**
     * [And] represents a filter that requires the [f1] and [f2] to be both satisfied.
     */
    private class And<Tbl: TypedTable<Tbl>>(
            private val f1: TypedFilter<Tbl>, private val f2: TypedFilter<Tbl>
    ) : TypedFilter<Tbl>() {

        override val asFilter: Filter get() = CompositeFilter.and(f1.asFilter, f2.asFilter)

    }

}
