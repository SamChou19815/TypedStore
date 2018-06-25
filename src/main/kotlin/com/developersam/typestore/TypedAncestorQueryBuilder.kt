package com.developersam.typestore

import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery.OrderBy
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor

/**
 * [TypedAncestorQueryBuilder] is a simply container of the datastore query parameters for ancestor
 * queries.
 *
 * @param table the table to query.
 * @param ancestor the ancestor key to match.
 */
class TypedAncestorQueryBuilder<Tbl : TypedTable<Tbl>> internal constructor(
        table: Tbl, ancestor: Key
) {

    /**
     * [backingBuilder] is the backing field for this type-safe builder.
     */
    private val backingBuilder: EntityQuery.Builder =
            Query.newEntityQueryBuilder().setKind(table.tableName).setFilter(hasAncestor(ancestor))

    /**
     * [order] sets the order.
     */
    var order: OrderBy
        get() = throw UnsupportedOperationException()
        set(value) {
            backingBuilder.setOrderBy(value)
        }

    /**
     * [limit] sets the limit.
     */
    var limit: Int
        get() = throw UnsupportedOperationException()
        set(value) {
            backingBuilder.setLimit(value)
        }

    internal fun build(): EntityQuery = backingBuilder.build()

}