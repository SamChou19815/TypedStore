package typedstore

import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery.OrderBy

/**
 * [TypedQueryBuilder] is a simply container of the datastore query parameters.
 *
 * @param table the table to query.
 */
class TypedQueryBuilder<Tbl : TypedTable<Tbl>> internal constructor(val table: Tbl) {

    /**
     * [backingBuilder] is the backing field for this type-safe builder.
     */
    private val backingBuilder: EntityQuery.Builder =
            Query.newEntityQueryBuilder().setKind(table.tableName)

    /**
     * Internally used typed filter for DSL.
     */
    private val typedFilterBuilder: TypedFilterBuilder<Tbl> = TypedFilterBuilder()

    /**
     * [filter] starts a filter DSL.
     *
     * All filters declared in filter will be merged by and.
     */
    fun filter(config: TypedFilterBuilder<Tbl>.() -> Unit): Unit = typedFilterBuilder.config()

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

    /**
     * [build] will build the [EntityQuery] for the query.
     */
    internal fun build(): EntityQuery {
        typedFilterBuilder.backingFilter?.let { backingBuilder.setFilter(it) }
        return backingBuilder.build()
    }

}
