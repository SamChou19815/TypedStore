package typestore

import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.LatLng
import com.google.cloud.datastore.StructuredQuery.OrderBy
import typestore.PropertyType.BLOB
import typestore.PropertyType.BOOL
import typestore.PropertyType.DATE_TIME
import typestore.PropertyType.DOUBLE
import typestore.PropertyType.ENUM
import typestore.PropertyType.KEY
import typestore.PropertyType.LAT_LNG
import typestore.PropertyType.LONG
import typestore.PropertyType.LONG_STRING
import typestore.PropertyType.STRING
import java.time.LocalDateTime

/**
 * [Property] represents a property in the entity with a specified type.
 *
 * @property name name of the property.
 * @property type type of the property in this framework.
 * @param Tbl type of the table.
 * @param T type of the property.
 */
sealed class Property<Tbl : TypedTable<Tbl>, T>(
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

    /**
     * [KeyProperty] is the not-null key property.
     */
    class KeyProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Key>(name = name, type = KEY)

    /**
     * [NullableKeyProperty] is the nullable key property.
     */
    class NullableKeyProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Key?>(name = name, type = KEY)

    /**
     * [LongProperty] is the not-null long property.
     */
    class LongProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Long>(name = name, type = LONG)

    /**
     * [NullableLongProperty] is the nullable long property.
     */
    class NullableLongProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Long?>(name = name, type = LONG)

    /**
     * [DoubleProperty] is the not-null double property.
     */
    class DoubleProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Double>(name = name, type = DOUBLE)

    /**
     * [NullableDoubleProperty] is the nullable double property.
     */
    class NullableDoubleProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Double?>(name = name, type = DOUBLE)

    /**
     * [BoolProperty] is the not-null boolean property.
     */
    class BoolProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Boolean>(name = name, type = BOOL)

    /**
     * [NullableBoolProperty] is the nullable boolean property.
     */
    class NullableBoolProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Boolean?>(name = name, type = BOOL)

    /**
     * [StringProperty] is the not-null string property.
     */
    class StringProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, String>(name = name, type = STRING)

    /**
     * [NullableStringProperty] is the not-null string property.
     */
    class NullableStringProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, String?>(name = name, type = STRING)

    /**
     * [LongStringProperty] is the nullable string property.
     */
    class LongStringProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, String>(name = name, type = LONG_STRING)

    /**
     * [NullableLongStringProperty] is the nullable string property.
     */
    class NullableLongStringProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, String?>(name = name, type = LONG_STRING)

    /**
     * [EnumProperty] is the not-null enum property.
     *
     * @param E the precise type of the enum.
     */
    class EnumProperty<Tbl : TypedTable<Tbl>, E : Enum<E>> internal constructor(
            name: String, clazz: Class<E>
    ) : Property<Tbl, E>(name = name, type = ENUM) {

        /**
         * [values] is the collection of all enum values.
         */
        private val values: Array<E> = clazz.enumConstants

        /**
         * [valueOf] returns the enum corresponds to [s].
         */
        fun valueOf(s: String): E = values.first { it.name == s }

    }

    /**
     * [DateTimeProperty] is the not-null date-time property.
     */
    class DateTimeProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, LocalDateTime>(name = name, type = DATE_TIME) {

        /**
         * [isPast] creates and returns a filter that returns all datetime in the past.
         * It assumes that the datetime is recorded in UTC timezone.
         */
        fun isPast(): TypedFilter<Tbl> = TypedFilter.Lt(property = this, value = nowInUTC())

        /**
         * [isFuture] creates and returns a filter that returns all datetime in the future.
         * It assumes that the datetime is recorded in UTC timezone.
         */
        fun isFuture(): TypedFilter<Tbl> = TypedFilter.Gt(property = this, value = nowInUTC())

    }

    /**
     * [NullableDateTimeProperty] is the nullable date-time property.
     */
    class NullableDateTimeProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, LocalDateTime?>(name = name, type = DATE_TIME) {

        /**
         * [isPast] creates and returns a filter that returns all datetime in the past.
         * It assumes that the datetime is recorded in UTC timezone.
         */
        fun isPast(): TypedFilter<Tbl> = TypedFilter.Lt(property = this, value = nowInUTC())

        /**
         * [isFuture] creates and returns a filter that returns all datetime in the future.
         * It assumes that the datetime is recorded in UTC timezone.
         */
        fun isFuture(): TypedFilter<Tbl> = TypedFilter.Gt(property = this, value = nowInUTC())

    }

    /**
     * [BlobProperty] is the not-null blob property.
     */
    class BlobProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Blob>(name = name, type = BLOB)

    /**
     * [NullableBlobProperty] is the nullable blob property.
     */
    class NullableBlobProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, Blob?>(name = name, type = BLOB)

    /**
     * [LatLngProperty] is the not-null lat-lng property.
     */
    class LatLngProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, LatLng>(name = name, type = LAT_LNG)

    /**
     * [NullableLatLngProperty] is the nullable lat-lng property.
     */
    class NullableLatLngProperty<Tbl : TypedTable<Tbl>> internal constructor(name: String) :
            Property<Tbl, LatLng?>(name = name, type = LAT_LNG)

}
