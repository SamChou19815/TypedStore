package typedstore

import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.KeyQuery
import com.google.cloud.datastore.ProjectionEntity
import com.google.cloud.datastore.ProjectionEntityQuery
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery

/**
 * [TypedQueryBuilder] is a simply container of the datastore query parameters, which can include
 * an optional ancestor to be included in the query.
 *
 * @property table the table to query.
 * @param Tbl precise type of the [table].
 * @param V type of the query result.
 */
sealed class TypedQueryBuilder<Tbl : TypedTable<Tbl>, V>(
        val table: Tbl, ancestor: Key?
) {

    /**
     * [backingBuilder] is the backing field for this type-safe builder.
     */
    protected abstract val backingBuilder: StructuredQuery.Builder<V>

    /**
     * The internally used typed filter builder for DSL.
     */
    private val typedFilterBuilder: TypedFilterBuilder<Tbl> = TypedFilterBuilder(ancestor)

    /**
     * The internally used typed order builder for DSL.
     */
    private var typedOrderBuilder: TypedOrderBuilder<Tbl, V>? = null

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
    fun order(config: TypedOrderBuilder<Tbl, V>.() -> Unit): Unit {
        val orderBuilder = typedOrderBuilder ?: TypedOrderBuilder(backingBuilder = backingBuilder)
        typedOrderBuilder = orderBuilder
        orderBuilder.config()
    }

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
    internal fun build(): StructuredQuery<V> {
        typedFilterBuilder.backingFilter?.let { backingBuilder.setFilter(it) }
        return backingBuilder.build()
    }

    /**
     * [ForEntity] is a specialized query builder for entities.
     */
    class ForEntity<Tbl : TypedTable<Tbl>>(table: Tbl, ancestor: Key?) :
            TypedQueryBuilder<Tbl, Entity>(table = table, ancestor = ancestor) {

        /**
         * @see TypedQueryBuilder.backingBuilder
         */
        override val backingBuilder: EntityQuery.Builder =
                Query.newEntityQueryBuilder().setKind(table.tableName)

    }

    /**
     * [ForKey] is a specialized query builder for keys.
     */
    class ForKey<Tbl : TypedTable<Tbl>>(table: Tbl, ancestor: Key?) :
            TypedQueryBuilder<Tbl, Key>(table = table, ancestor = ancestor) {

        /**
         * @see TypedQueryBuilder.backingBuilder
         */
        override val backingBuilder: KeyQuery.Builder =
                Query.newKeyQueryBuilder().setKind(table.tableName)

    }

    /**
     * [ForProjection] is a specialized query builder for projection entities.
     *
     * @property projections a non-empty collection of properties to select.
     */
    class ForProjection<Tbl : TypedTable<Tbl>>(
            private val projections: Collection<Property<Tbl, *>>, table: Tbl, ancestor: Key?
    ) : TypedQueryBuilder<Tbl, ProjectionEntity>(table = table, ancestor = ancestor) {

        init {
            if (projections.isEmpty()) {
                throw IllegalArgumentException("Empty Projection is not allowed!")
            }
        }

        /**
         * @see TypedQueryBuilder.backingBuilder
         */
        override val backingBuilder: ProjectionEntityQuery.Builder =
                Query.newProjectionEntityQueryBuilder().setKind(table.tableName)
                        .let { DatastoreVarargAdapter.setProjections(it, projections) }

    }

}
