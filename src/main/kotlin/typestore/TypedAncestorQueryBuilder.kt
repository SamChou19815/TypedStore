package typestore

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
     * [asc] sets the order on this property in ascending order.
     * It will reset previously set order, if any.
     */
    fun Property<Tbl, *>.asc() {
        backingBuilder.setOrderBy(OrderBy.asc(name))
    }

    /**
     * [desc] sets the order on this property in descending order.
     * It will reset previously set order, if any.
     */
    fun Property<Tbl, *>.desc() {
        backingBuilder.setOrderBy(OrderBy.desc(name))
    }

    /**
     * [withLimit] sets the limit.
     * It will reset previously set limit, if any.
     */
    fun withLimit(limit: Int) {
        backingBuilder.setLimit(limit)
    }

    internal fun build(): EntityQuery = backingBuilder.build()

}