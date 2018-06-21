package com.developersam.typestore

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Key

/**
 * [TypedTable] represents a set of all entities of the same kind. This class is mostly used for
 * defining the type and structure of an entity.
 *
 * @constructor `tableName` specifies the name of the table/entity kind. If not given, it defaults
 * to the simple. name of the class.
 */
open class TypedTable @JvmOverloads protected constructor(tableName: String? = null) {

    /**
     * [tableName] returns the name of the table.
     */
    val tableName: String = tableName ?: javaClass.simpleName

    /**
     * [registeredProperties] contains a list of all registered properties. This list is designed
     * to be dynamically patched when extending this class with those xProperty methods.
     */
    private val registeredProperties: ArrayList<Property<*>> = arrayListOf()

    /**
     * [keyProperty] declares, registers, and returns a not-null key property with [name].
     */
    protected fun keyProperty(name: String): Property<Key> =
            Property<Key>(name = name, type = PropertyType.KEY)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableKeyProperty] declares, registers, and returns a nullable key property with [name].
     */
    protected fun nullableKeyProperty(name: String): Property<Key?> =
            Property<Key?>(name = name, type = PropertyType.KEY)
                    .also { registeredProperties.add(element = it) }

    /**
     * [longProperty] declares, registers, and returns a not-null long property with [name].
     */
    protected fun longProperty(name: String): Property<Long> =
            Property<Long>(name = name, type = PropertyType.LONG)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableLongProperty] declares, registers, and returns a nullable long property with [name].
     */
    protected fun nullableLongProperty(name: String): Property<Long?> =
            Property<Long?>(name = name, type = PropertyType.LONG)
                    .also { registeredProperties.add(element = it) }

    /**
     * [doubleProperty] declares, registers, and returns a not-null double property with [name].
     */
    protected fun doubleProperty(name: String): Property<Double> =
            Property<Double>(name = name, type = PropertyType.DOUBLE)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableDoubleProperty] declares, registers, and returns a nullable double property with
     * [name].
     */
    protected fun nullableDoubleProperty(name: String): Property<Double?> =
            Property<Double?>(name = name, type = PropertyType.DOUBLE)
                    .also { registeredProperties.add(element = it) }

    /**
     * [boolProperty] declares, registers, and returns a not-null boolean property with [name].
     */
    protected fun boolProperty(name: String): Property<Boolean> =
            Property<Boolean>(name = name, type = PropertyType.BOOL)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableBoolProperty] declares, registers, and returns a nullable boolean property with
     * [name].
     */
    protected fun nullableBoolProperty(name: String): Property<Boolean?> =
            Property<Boolean?>(name = name, type = PropertyType.BOOL)
                    .also { registeredProperties.add(element = it) }

    /**
     * [stringProperty] declares, registers, and returns a not-null string property with [name].
     */
    protected fun stringProperty(name: String): Property<String> =
            Property<String>(name = name, type = PropertyType.STRING)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableStringProperty] declares, registers, and returns a nullable string property with
     * [name].
     */
    protected fun nullableStringProperty(name: String): Property<String?> =
            Property<String?>(name = name, type = PropertyType.STRING)
                    .also { registeredProperties.add(element = it) }

    /**
     * [blobProperty] declares, registers, and returns a not-null blob property with [name].
     */
    protected fun blobProperty(name: String): Property<Blob> =
            Property<Blob>(name = name, type = PropertyType.BLOB)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableBlobProperty] declares, registers, and returns a nullable blob property with [name].
     */
    protected fun nullableBlobProperty(name: String): Property<Blob?> =
            Property<Blob?>(name = name, type = PropertyType.BLOB)
                    .also { registeredProperties.add(element = it) }

    /**
     * [timestampProperty] declares, registers, and returns a not-null timestamp property with
     * [name].
     */
    protected fun timestampProperty(name: String): Property<Timestamp> =
            Property<Timestamp>(name = name, type = PropertyType.TIMESTAMP)
                    .also { registeredProperties.add(element = it) }

    /**
     * [nullableTimestampProperty] declares, registers, and returns a nullable timestamp property
     * with [name].
     */
    protected fun nullableTimestampProperty(name: String): Property<Timestamp?> =
            Property<Timestamp?>(name = name, type = PropertyType.TIMESTAMP)
                    .also { registeredProperties.add(element = it) }

}
