package typedstore

import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.LatLng
import typedstore.Property.EnumProperty
import typedstore.PropertyType.BLOB
import typedstore.PropertyType.BOOL
import typedstore.PropertyType.DATE_TIME
import typedstore.PropertyType.DOUBLE
import typedstore.PropertyType.KEY
import typedstore.PropertyType.LAT_LNG
import typedstore.PropertyType.LONG
import typedstore.PropertyType.LONG_STRING
import typedstore.PropertyType.STRING
import java.time.LocalDateTime

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
    protected fun keyProperty(name: String): Property<Tbl, Key> =
            Property<Tbl, Key>(name = name, type = KEY).also { register(property = it) }

    /**
     * [nullableKeyProperty] declares, registers, and returns a nullable key property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableKeyProperty(name: String): Property<Tbl, Key?> =
            Property<Tbl, Key?>(name = name, type = KEY).also { register(property = it) }

    /**
     * [longProperty] declares, registers, and returns a not-null long property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun longProperty(name: String): Property<Tbl, Long> =
            Property<Tbl, Long>(name = name, type = LONG).also { register(property = it) }

    /**
     * [nullableLongProperty] declares, registers, and returns a nullable long property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLongProperty(name: String): Property<Tbl, Long?> =
            Property<Tbl, Long?>(name = name, type = LONG).also { register(property = it) }

    /**
     * [doubleProperty] declares, registers, and returns a not-null double property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun doubleProperty(name: String): Property<Tbl, Double> =
            Property<Tbl, Double>(name = name, type = DOUBLE).also { register(property = it) }

    /**
     * [nullableDoubleProperty] declares, registers, and returns a nullable double property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableDoubleProperty(name: String): Property<Tbl, Double?> =
            Property<Tbl, Double?>(name = name, type = DOUBLE).also { register(property = it) }

    /**
     * [boolProperty] declares, registers, and returns a not-null boolean property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun boolProperty(name: String): Property<Tbl, Boolean> =
            Property<Tbl, Boolean>(name = name, type = BOOL).also { register(property = it) }

    /**
     * [nullableBoolProperty] declares, registers, and returns a nullable boolean property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableBoolProperty(name: String): Property<Tbl, Boolean?> =
            Property<Tbl, Boolean?>(name = name, type = BOOL).also { register(property = it) }

    /**
     * [stringProperty] declares, registers, and returns a not-null string property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun stringProperty(name: String): Property<Tbl, String> =
            Property<Tbl, String>(name = name, type = STRING).also { register(property = it) }

    /**
     * [nullableStringProperty] declares, registers, and returns a nullable string property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableStringProperty(name: String): Property<Tbl, String?> =
            Property<Tbl, String?>(name = name, type = STRING).also { register(property = it) }

    /**
     * [longStringProperty] declares, registers, and returns a not-null long string property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun longStringProperty(name: String): Property<Tbl, String> =
            Property<Tbl, String>(name = name, type = LONG_STRING).also { register(property = it) }

    /**
     * [nullableLongStringProperty] declares, registers, and returns a nullable long string property
     * with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLongStringProperty(name: String): Property<Tbl, String?> =
            Property<Tbl, String?>(name = name, type = LONG_STRING).also { register(property = it) }

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
    protected fun datetimeProperty(name: String): Property<Tbl, LocalDateTime> =
            Property<Tbl, LocalDateTime>(name = name, type = DATE_TIME).also { register(it) }

    /**
     * [blobProperty] declares, registers, and returns a not-null blob property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun blobProperty(name: String): Property<Tbl, Blob> =
            Property<Tbl, Blob>(name = name, type = BLOB).also { register(property = it) }

    /**
     * [nullableBlobProperty] declares, registers, and returns a nullable blob property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableBlobProperty(name: String): Property<Tbl, Blob?> =
            Property<Tbl, Blob?>(name = name, type = BLOB).also { register(property = it) }

    /**
     * [latLngProperty] declares, registers, and returns a not-null lat-lng property with [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun latLngProperty(name: String): Property<Tbl, LatLng> =
            Property<Tbl, LatLng>(name = name, type = LAT_LNG).also { register(property = it) }

    /**
     * [nullableLatLngProperty] declares, registers, and returns a nullable lat-lng property with
     * [name].
     *
     * @throws IllegalArgumentException if the property with this name is already registered.
     */
    protected fun nullableLatLngProperty(name: String): Property<Tbl, LatLng?> =
            Property<Tbl, LatLng?>(name = name, type = LAT_LNG).also { register(property = it) }

}
