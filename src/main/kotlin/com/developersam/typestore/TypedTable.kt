package com.developersam.typestore

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.LatLng

/**
 * [TypedTable] represents a set of all entities of the same kind. This class is mostly used for
 * defining the type and structure of an entity.
 *
 * @constructor `tableName` specifies the name of the table/entity kind. If not given, it defaults
 * to the simple. name of the class.
 * @param Tbl type of the table.
 */
open class TypedTable<Tbl: TypedTable<Tbl>> protected constructor(tableName: String? = null) {

    /**
     * [tableName] returns the name of the table.
     */
    val tableName: String = tableName ?: javaClass.simpleName

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
    protected fun keyProperty(name: String): Property<Tbl, Key> =
            Property<Tbl, Key>(name = name, type = PropertyType.KEY)
                    .also { register(property = it) }

    /**
     * [nullableKeyProperty] declares, registers, and returns a nullable key property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableKeyProperty(name: String): Property<Tbl, Key?> =
            Property<Tbl, Key?>(name = name, type = PropertyType.KEY)
                    .also { register(property = it) }

    /**
     * [longProperty] declares, registers, and returns a not-null long property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun longProperty(name: String): Property<Tbl, Long> =
            Property<Tbl, Long>(name = name, type = PropertyType.LONG)
                    .also { register(property = it) }

    /**
     * [nullableLongProperty] declares, registers, and returns a nullable long property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLongProperty(name: String): Property<Tbl, Long?> =
            Property<Tbl, Long?>(name = name, type = PropertyType.LONG)
                    .also { register(property = it) }

    /**
     * [doubleProperty] declares, registers, and returns a not-null double property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun doubleProperty(name: String): Property<Tbl, Double> =
            Property<Tbl, Double>(name = name, type = PropertyType.DOUBLE)
                    .also { register(property = it) }

    /**
     * [nullableDoubleProperty] declares, registers, and returns a nullable double property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableDoubleProperty(name: String): Property<Tbl, Double?> =
            Property<Tbl, Double?>(name = name, type = PropertyType.DOUBLE)
                    .also { register(property = it) }

    /**
     * [boolProperty] declares, registers, and returns a not-null boolean property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun boolProperty(name: String): Property<Tbl, Boolean> =
            Property<Tbl, Boolean>(name = name, type = PropertyType.BOOL)
                    .also { register(property = it) }

    /**
     * [nullableBoolProperty] declares, registers, and returns a nullable boolean property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableBoolProperty(name: String): Property<Tbl, Boolean?> =
            Property<Tbl, Boolean?>(name = name, type = PropertyType.BOOL)
                    .also { register(property = it) }

    /**
     * [stringProperty] declares, registers, and returns a not-null string property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun stringProperty(name: String): Property<Tbl, String> =
            Property<Tbl, String>(name = name, type = PropertyType.STRING)
                    .also { register(property = it) }

    /**
     * [nullableStringProperty] declares, registers, and returns a nullable string property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableStringProperty(name: String): Property<Tbl, String?> =
            Property<Tbl, String?>(name = name, type = PropertyType.STRING)
                    .also { register(property = it) }

    /**
     * [longStringProperty] declares, registers, and returns a not-null long string property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun longStringProperty(name: String): Property<Tbl, String> =
            Property<Tbl, String>(name = name, type = PropertyType.LONG_STRING)
                    .also { register(property = it) }

    /**
     * [nullableLongStringProperty] declares, registers, and returns a nullable long string property
     * with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLongStringProperty(name: String): Property<Tbl, String?> =
            Property<Tbl, String?>(name = name, type = PropertyType.LONG_STRING)
                    .also { register(property = it) }

    /**
     * [blobProperty] declares, registers, and returns a not-null blob property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun blobProperty(name: String): Property<Tbl, Blob> =
            Property<Tbl, Blob>(name = name, type = PropertyType.BLOB)
                    .also { register(property = it) }

    /**
     * [nullableBlobProperty] declares, registers, and returns a nullable blob property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableBlobProperty(name: String): Property<Tbl, Blob?> =
            Property<Tbl, Blob?>(name = name, type = PropertyType.BLOB)
                    .also { register(property = it) }

    /**
     * [timestampProperty] declares, registers, and returns a not-null timestamp property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun timestampProperty(name: String): Property<Tbl, Timestamp> =
            Property<Tbl, Timestamp>(name = name, type = PropertyType.TIMESTAMP)
                    .also { register(property = it) }

    /**
     * [nullableTimestampProperty] declares, registers, and returns a nullable timestamp property
     * with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableTimestampProperty(name: String): Property<Tbl, Timestamp?> =
            Property<Tbl, Timestamp?>(name = name, type = PropertyType.TIMESTAMP)
                    .also { register(property = it) }

    /**
     * [latLngProperty] declares, registers, and returns a not-null lat-lng property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun latLngProperty(name: String): Property<Tbl, LatLng> =
            Property<Tbl, LatLng>(name = name, type = PropertyType.LAT_LNG)
                    .also { register(property = it) }

    /**
     * [nullableLatLngProperty] declares, registers, and returns a nullable lat-lng property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLatLngProperty(name: String): Property<Tbl, LatLng?> =
            Property<Tbl, LatLng?>(name = name, type = PropertyType.LAT_LNG)
                    .also { register(property = it) }

}
