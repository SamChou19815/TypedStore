package typedstore

import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StructuredQuery.CompositeFilter
import com.google.cloud.datastore.StructuredQuery.Filter
import com.google.cloud.datastore.StructuredQuery.PropertyFilter
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor
import typedstore.PropertyType.BLOB
import typedstore.PropertyType.BOOL
import typedstore.PropertyType.DATE_TIME
import typedstore.PropertyType.DOUBLE
import typedstore.PropertyType.ENUM
import typedstore.PropertyType.KEY
import typedstore.PropertyType.LONG
import typedstore.PropertyType.STRING
import java.time.LocalDateTime

/**
 * [TypedFilterBuilder] is used for filter DSL.
 *
 * @param Tbl the type of that table the builder is associated to.
 */
class TypedFilterBuilder<Tbl : TypedTable<Tbl>> internal constructor(ancestor: Key?) {

    /**
     * [_backingFilter] is the backing field for the filter builder.
     */
    private var _backingFilter: Filter? = ancestor?.let { hasAncestor(it) }

    /**
     * [backingFilter] is the backing field for the filter builder, accessible for query builder.
     */
    internal val backingFilter: Filter? get() = _backingFilter

    /**
     * [Filter.add] adds the [filter] to the set of all filters.
     */
    private fun Filter.add() {
        _backingFilter = _backingFilter?.let { CompositeFilter.and(it, this) } ?: this
    }

    /**
     * [eq] registers an equality filter from this property and a [value].
     */
    infix fun <T> Property<Tbl, T>.eq(value: T): Unit =
            if (value == null) PropertyFilter.isNull(name).add() else when (type) {
                KEY -> PropertyFilter.eq(name, value as Key)
                LONG -> PropertyFilter.eq(name, value as Long)
                DOUBLE -> PropertyFilter.eq(name, value as Double)
                BOOL -> PropertyFilter.eq(name, value as Boolean)
                STRING -> PropertyFilter.eq(name, value as String)
                ENUM -> PropertyFilter.eq(name, (value as Enum<*>).name)
                BLOB -> PropertyFilter.eq(name, value as Blob)
                DATE_TIME -> PropertyFilter.eq(name, (value as LocalDateTime).toGcpTimestamp())
                else -> throw UnsupportedOperationException(
                        "${type.name} is unsupported."
                )
            }.add()

    /**
     * [lt] registers a less-than filter from this property and a [value].
     */
    infix fun <T> Property<Tbl, T>.lt(value: T): Unit =
            if (value == null) {
                throw IllegalArgumentException("Value cannot be null!")
            } else when (type) {
                KEY -> PropertyFilter.lt(name, value as Key)
                LONG -> PropertyFilter.lt(name, value as Long)
                DOUBLE -> PropertyFilter.lt(name, value as Double)
                BOOL -> PropertyFilter.lt(name, value as Boolean)
                STRING -> PropertyFilter.lt(name, value as String)
                ENUM -> PropertyFilter.lt(name, (value as Enum<*>).name)
                BLOB -> PropertyFilter.lt(name, value as Blob)
                DATE_TIME ->
                    PropertyFilter.lt(name, (value as LocalDateTime).toGcpTimestamp())
                else -> throw UnsupportedOperationException(
                        "${type.name} is unsupported."
                )
            }.add()

    /**
     * [le] registers a less-than-or-eq filter from this property and a [value].
     */
    infix fun <T> Property<Tbl, T>.le(value: T): Unit =
            if (value == null) {
                throw IllegalArgumentException("Value cannot be null!")
            } else when (type) {
                KEY -> PropertyFilter.le(name, value as Key)
                LONG -> PropertyFilter.le(name, value as Long)
                DOUBLE -> PropertyFilter.le(name, value as Double)
                BOOL -> PropertyFilter.le(name, value as Boolean)
                STRING -> PropertyFilter.le(name, value as String)
                ENUM -> PropertyFilter.le(name, (value as Enum<*>).name)
                BLOB -> PropertyFilter.le(name, value as Blob)
                DATE_TIME ->
                    PropertyFilter.le(name, (value as LocalDateTime).toGcpTimestamp())
                else -> throw UnsupportedOperationException(
                        "${type.name} is unsupported."
                )
            }.add()

    /**
     * [gt] registers an greater-than filter from this property and a [value].
     */
    infix fun <T> Property<Tbl, T>.gt(value: T): Unit =
            if (value == null) {
                throw IllegalArgumentException("Value cannot be null!")
            } else when (type) {
                KEY -> PropertyFilter.gt(name, value as Key)
                LONG -> PropertyFilter.gt(name, value as Long)
                DOUBLE -> PropertyFilter.gt(name, value as Double)
                PropertyType.BOOL -> PropertyFilter.gt(name, value as Boolean)
                STRING -> PropertyFilter.gt(name, value as String)
                ENUM -> PropertyFilter.gt(name, (value as Enum<*>).name)
                BLOB -> PropertyFilter.gt(name, value as Blob)
                DATE_TIME ->
                    PropertyFilter.gt(name, (value as LocalDateTime).toGcpTimestamp())
                else -> throw UnsupportedOperationException(
                        "${type.name} is unsupported."
                )
            }.add()

    /**
     * [ge] registers an greater-than-or-eq filter from this property and a [value].
     */
    infix fun <T> Property<Tbl, T>.ge(value: T): Unit =
            if (value == null) {
                throw IllegalArgumentException("Value cannot be null!")
            } else when (type) {
                KEY -> PropertyFilter.ge(name, value as Key)
                LONG -> PropertyFilter.ge(name, value as Long)
                DOUBLE -> PropertyFilter.ge(name, value as Double)
                BOOL -> PropertyFilter.ge(name, value as Boolean)
                STRING -> PropertyFilter.ge(name, value as String)
                ENUM -> PropertyFilter.ge(name, (value as Enum<*>).name)
                BLOB -> PropertyFilter.ge(name, value as Blob)
                DATE_TIME ->
                    PropertyFilter.ge(name, (value as LocalDateTime).toGcpTimestamp())
                else -> throw UnsupportedOperationException(
                        "${type.name} is unsupported."
                )
            }.add()

    /**
     * [isPast] registers a filter that returns all datetime in the past.
     * It assumes that the datetime is recorded in UTC timezone.
     */
    fun Property<Tbl, LocalDateTime>.isPast(): Unit = this lt nowInUTC()

    /**
     * [isFuture] registers a filter that returns all datetime in the future.
     * It assumes that the datetime is recorded in UTC timezone.
     */
    fun Property<Tbl, LocalDateTime>.isFuture(): Unit = this gt nowInUTC()

}
