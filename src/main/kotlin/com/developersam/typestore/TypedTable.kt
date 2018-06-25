package com.developersam.typestore

import com.developersam.typestore.Property.BlobProperty
import com.developersam.typestore.Property.BoolProperty
import com.developersam.typestore.Property.DateTimeProperty
import com.developersam.typestore.Property.DoubleProperty
import com.developersam.typestore.Property.EnumProperty
import com.developersam.typestore.Property.KeyProperty
import com.developersam.typestore.Property.LatLngProperty
import com.developersam.typestore.Property.LongProperty
import com.developersam.typestore.Property.LongStringProperty
import com.developersam.typestore.Property.NullableBlobProperty
import com.developersam.typestore.Property.NullableBoolProperty
import com.developersam.typestore.Property.NullableDateTimeProperty
import com.developersam.typestore.Property.NullableDoubleProperty
import com.developersam.typestore.Property.NullableKeyProperty
import com.developersam.typestore.Property.NullableLatLngProperty
import com.developersam.typestore.Property.NullableLongProperty
import com.developersam.typestore.Property.NullableLongStringProperty
import com.developersam.typestore.Property.NullableStringProperty
import com.developersam.typestore.Property.StringProperty

/**
 * [TypedTable] represents a set of all entities of the same kind. This class is mostly used for
 * defining the type and structure of an entity.
 *
 * @constructor `tableName` specifies the name of the table/entity kind. If not given, it defaults
 * to the simple. name of the class.
 * @param Tbl type of the table.
 */
open class TypedTable<Tbl : TypedTable<Tbl>> protected constructor(tableName: String? = null) {

    /**
     * [tableName] returns the name of the table.
     */
    val tableName: String = (tableName ?: javaClass.simpleName)
            .removeSuffix(suffix = "Table").removeSuffix(suffix = "Entity")

    /**
     * [registeredProperties] contains a list of all registered properties. This list is designed
     * to be dynamically patched when extending this class with those xProperty methods.
     */
    internal val registeredProperties: HashSet<Property<Tbl, *>> = hashSetOf()

    /**
     * [register] tries to register the given [property] and throws an [IllegalArgumentException] if
     * a property with the same name is already registered.
     */
    private fun register(property: Property<Tbl, *>) {
        if (!registeredProperties.add(element = property)) {
            throw IllegalArgumentException("You have already registered a property with " +
                    "name \"${property.name}\"")
        }
    }

    /**
     * [keyProperty] declares, registers, and returns a not-null key property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun keyProperty(name: String): KeyProperty<Tbl> =
            KeyProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableKeyProperty] declares, registers, and returns a nullable key property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableKeyProperty(name: String): NullableKeyProperty<Tbl> =
            NullableKeyProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [longProperty] declares, registers, and returns a not-null long property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun longProperty(name: String): LongProperty<Tbl> =
            LongProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableLongProperty] declares, registers, and returns a nullable long property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLongProperty(name: String): NullableLongProperty<Tbl> =
            NullableLongProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [doubleProperty] declares, registers, and returns a not-null double property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun doubleProperty(name: String): DoubleProperty<Tbl> =
            DoubleProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableDoubleProperty] declares, registers, and returns a nullable double property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableDoubleProperty(name: String): NullableDoubleProperty<Tbl> =
            NullableDoubleProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [boolProperty] declares, registers, and returns a not-null boolean property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun boolProperty(name: String): BoolProperty<Tbl> =
            BoolProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableBoolProperty] declares, registers, and returns a nullable boolean property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableBoolProperty(name: String): NullableBoolProperty<Tbl> =
            NullableBoolProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [stringProperty] declares, registers, and returns a not-null string property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun stringProperty(name: String): StringProperty<Tbl> =
            StringProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableStringProperty] declares, registers, and returns a nullable string property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableStringProperty(name: String): NullableStringProperty<Tbl> =
            NullableStringProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [longStringProperty] declares, registers, and returns a not-null long string property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun longStringProperty(name: String): LongStringProperty<Tbl> =
            LongStringProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableLongStringProperty] declares, registers, and returns a nullable long string property
     * with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLongStringProperty(name: String): NullableLongStringProperty<Tbl> =
            NullableLongStringProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [enumProperty] declares, registers, and returns a not-null enum property with [name] and
     * [clazz].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun <E: Enum<E>> enumProperty(name: String, clazz: Class<E>): EnumProperty<Tbl, E> =
            EnumProperty<Tbl, E>(name = name, clazz = clazz).also { register(property = it) }

    /**
     * [datetimeProperty] declares, registers, and returns a not-null date-time property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun datetimeProperty(name: String): DateTimeProperty<Tbl> =
            DateTimeProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableDatetimeProperty] declares, registers, and returns a nullable date-time property
     * with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableDatetimeProperty(name: String): NullableDateTimeProperty<Tbl> =
            NullableDateTimeProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [blobProperty] declares, registers, and returns a not-null blob property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun blobProperty(name: String): BlobProperty<Tbl> =
            BlobProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableBlobProperty] declares, registers, and returns a nullable blob property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableBlobProperty(name: String): NullableBlobProperty<Tbl> =
            NullableBlobProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [latLngProperty] declares, registers, and returns a not-null lat-lng property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun latLngProperty(name: String): LatLngProperty<Tbl> =
            LatLngProperty<Tbl>(name = name).also { register(property = it) }

    /**
     * [nullableLatLngProperty] declares, registers, and returns a nullable lat-lng property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLatLngProperty(name: String): NullableLatLngProperty<Tbl> =
            NullableLatLngProperty<Tbl>(name = name).also { register(property = it) }

}
