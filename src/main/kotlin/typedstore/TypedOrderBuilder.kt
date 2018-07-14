package typedstore

import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.StructuredQuery

/**
 * [TypedOrderBuilder] is used for order DSL.
 *
 * @param Tbl the type of that table the builder is associated to.
 * @property backingBuilder the reference to the builder in the query builder.
 */
class TypedOrderBuilder<Tbl : TypedTable<Tbl>> internal constructor(
        private val backingBuilder: EntityQuery.Builder
) {

    /**
     * [asc] sets the order on this property in ascending order.
     * It will reset previously set order, if any.
     */
    fun Property<Tbl, *>.asc() {
        backingBuilder.addOrderBy(StructuredQuery.OrderBy.asc(name))
    }

    /**
     * [desc] sets the order on this property in descending order.
     * It will reset previously set order, if any.
     */
    fun Property<Tbl, *>.desc() {
        backingBuilder.addOrderBy(StructuredQuery.OrderBy.desc(name))
    }

}

