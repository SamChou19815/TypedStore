package typedstore

import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor

/**
 * [TypedAncestorQueryBuilder] is a simply container of the datastore query parameters for ancestor
 * queries.
 *
 * @param table the table to query.
 * @param ancestor the ancestor key to match.
 */
class TypedAncestorQueryBuilder<Tbl : TypedTable<Tbl>> internal constructor(
        val table: Tbl, ancestor: Key
) {

    /**
     * [backingBuilder] is the backing field for this type-safe builder.
     */
    private val backingBuilder: EntityQuery.Builder =
            Query.newEntityQueryBuilder().setKind(table.tableName).setFilter(hasAncestor(ancestor))

    /**
     * The internally used typed order builder for DSL.
     */
    private val typedOrderBuilder: TypedOrderBuilder<Tbl> = TypedOrderBuilder(backingBuilder)

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
    fun withLimit(limit: Int) {
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

    internal fun build(): EntityQuery = backingBuilder.build()

}