package typedstore

import typedstore.PropertyType.ENUM

/**
 * [Property] represents a property in the entity with a specified type.
 *
 * @property name name of the property.
 * @property type type of the property in this framework.
 * @param Tbl type of the table.
 * @param T type of the property.
 */
open class Property<Tbl : TypedTable<Tbl>, T> internal constructor(
        internal val name: String, internal val type: PropertyType
) {

    /**
     * Returns JSON-like string representation.
     */
    override fun toString(): String = "Property{ name: \"$name\", type: ${type.name} }"

    /**
     * Checks equality with [other] only based on name.
     */
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is Property<*, *> -> name == other.name
        else -> false
    }

    /**
     * Returns the hashcode that is only based on name.
     */
    override fun hashCode(): Int = name.hashCode()

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

}
