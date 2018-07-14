package typedstore

import com.google.cloud.datastore.Cursor
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
     * The internally used typed filter builder for DSL.
     */
    private val typedFilterBuilder: TypedFilterBuilder<Tbl> = TypedFilterBuilder()

    /**
     * The internally used typed order builder for DSL.
     */
    private val typedOrderBuilder: TypedOrderBuilder<Tbl> = TypedOrderBuilder(backingBuilder)

    /**
     * [filter] starts a filter DSL.
     *
     * All filters declared in filter will be merged by and.
     */
    fun filter(config: TypedFilterBuilder<Tbl>.() -> Unit): Unit = typedFilterBuilder.config()

    /**
     * [order] starts a order DSL.
     *
     * All orders declared in order will be merged according to the sequence of declaration.
     */
    fun order(config: TypedOrderBuilder<Tbl>.() -> Unit): Unit = typedOrderBuilder.config()

    /**
     * [withLimit] sets the limit.
     * It will reset previously set limit, if any.
     */
    infix fun withLimit(limit: Int) {
        backingBuilder.setLimit(limit)
    }

    /**
     * [startAt] sets the start cursor to be [cursor].
     * It will reset previously set cursor, if any.
     */
    fun startAt(cursor: Cursor) {
        backingBuilder.setStartCursor(cursor)
    }

    /**
     * [endAt] sets the end cursor to be [cursor].
     * It will reset previously set cursor, if any.
     */
    fun endAt(cursor: Cursor) {
        backingBuilder.setEndCursor(cursor)
    }

    /**
     * [build] will build the [EntityQuery] for the query.
     */
    internal fun build(): EntityQuery {
        typedFilterBuilder.backingFilter?.let { backingBuilder.setFilter(it) }
        return backingBuilder.build()
    }

}
