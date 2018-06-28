package typestore

import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery.OrderBy

/**
 * [TypedQueryBuilder] is a simply container of the datastore query parameters.
 *
 * @param table the table to query.
 */
class TypedQueryBuilder<Tbl: TypedTable<Tbl>> internal constructor(table: Tbl) {

    /**
     * [backingBuilder] is the backing field for this type-safe builder.
     */
    private val backingBuilder: EntityQuery.Builder =
            Query.newEntityQueryBuilder().setKind(table.tableName)

    /**
     * [filter] sets the filter.
     */
    var filter: TypedFilter<Tbl>
        get() = throw UnsupportedOperationException()
        set(value) {
            backingBuilder.setFilter(value.asFilter)
        }

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
