package typedstore

import com.google.cloud.datastore.StructuredQuery.Builder
import com.google.cloud.datastore.StructuredQuery.OrderBy

/**
 * [TypedOrderBuilder] is used for order DSL.
 *
 * @property backingBuilder the reference to the builder in the query builder.
 * @param Tbl the type of that table the builder is associated to.
 * @param V the type of the value to query.
 */
class TypedOrderBuilder<Tbl : TypedTable<Tbl>, V> internal constructor(
        private val backingBuilder: Builder<V>
) {

    /**
     * [asc] sets the order on this property in ascending order.
     * It will reset previously set order, if any.
     */
    fun Property<Tbl, *>.asc() {
        backingBuilder.addOrderBy(OrderBy.asc(name))
    }

    /**
     * [desc] sets the order on this property in descending order.
     * It will reset previously set order, if any.
     */
    fun Property<Tbl, *>.desc() {
        backingBuilder.addOrderBy(OrderBy.desc(name))
    }

}

