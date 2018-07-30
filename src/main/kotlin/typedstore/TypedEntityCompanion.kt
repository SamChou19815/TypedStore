package typedstore

import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.ProjectionEntity
import com.google.cloud.datastore.QueryResults

/**
 * [TypedEntityCompanion] is designed to be the companion object of a [TypedEntity], so that typed
 * CRUD operations can be launched directly from there.
 *
 * @property table the table associated with the entity.
 * @param Tbl specific type of the typed table.
 * @param E specific type of the typed entity.
 */
abstract class TypedEntityCompanion<Tbl : TypedTable<Tbl>, E : TypedEntity<Tbl>>(
        private val datastore: Datastore = defaultDatastore, private val table: Tbl
) {

    /*
     * --------------------------------------------------------------------------------
     * Part 0: Responsibility from Subclasses
     * --------------------------------------------------------------------------------
     */

    /**
     * [create] creates a [TypedEntity] from an `Entity` from GCP Datastore.
     */
    protected abstract fun create(entity: Entity): E

    /*
     * --------------------------------------------------------------------------------
     * Part 1: Reading
     * --------------------------------------------------------------------------------
     */

    /**
     * [get] returns the entity with the given [key] or `null` if it does not exist.
     * The caller of the method needs to ensure that the key actually corresponds to this table,
     * otherwise the behavior is unspecified.
     */
    operator fun get(key: Key): E? = datastore[key]?.let(block = ::create)

    /**
     * [get] returns a sequence of entities with the given [keys].
     * The caller of the method needs to ensure that the key actually corresponds to this table,
     * otherwise the behavior is unspecified.
     */
    operator fun get(keys: Iterable<Key>): Sequence<E> =
            datastore[keys].asSequence().map(transform = ::create)

    /**
     * [get] returns the entity with the given [key].
     * The caller of the method needs to ensure that the key actually corresponds to this table,
     * otherwise the behavior is unspecified.
     * If no such entity is found, it will throw an [IllegalArgumentException]
     */
    fun getNotNull(key: Key): E = get(key = key).let { checkNotNull(value = it) }

    /**
     * [contains] tests whether an entity with given [key] exists in the datastore.
     */
    operator fun contains(key: Key): Boolean = datastore[key] != null

    /**
     * [any] tests and returns whether there exists any entity as specified by the query in
     * [builder]. It can also accept an additional [ancestor] as part of the filter.
     */
    fun any(
            ancestor: Key? = null, builder: TypedQueryBuilder.ForKey<Tbl>.() -> Unit = {}
    ): Boolean =
            TypedQueryBuilder.ForKey(table = table, ancestor = ancestor).apply(builder)
                    .build().let { datastore.run(it) }.hasNext()

    /**
     * [all] simply returns all entities without any restrictions.
     *
     * If [ancestor] is supplied, it will return all entities that has the ancestor without any
     * restriction.
     */
    fun all(ancestor: Key? = null): Sequence<E> = query(ancestor = ancestor) {}

    /**
     * [allKeys] simply returns all keys without any restrictions.
     *
     * It can take an optional parameter [ancestor] as the ancestor key.
     */
    fun allKeys(ancestor: Key? = null): Sequence<Key> = queryKeys(ancestor = ancestor) {}

    /**
     * [entityQueryRaw] uses the given entity query builder [builder] to construct a query and
     * returns the raw entity query result. It can also accept an additional [ancestor] as part of
     * the filter.
     */
    private fun entityQueryRaw(
            ancestor: Key?, builder: TypedQueryBuilder.ForEntity<Tbl>.() -> Unit
    ): QueryResults<Entity> =
            TypedQueryBuilder.ForEntity(table = table, ancestor = ancestor)
                    .apply(block = builder)
                    .build()
                    .let { datastore.run(it) }

    /**
     * [keyQueryRaw] uses the given key query builder [builder] to construct a query and returns the
     * raw key query result. It can also accept an additional [ancestor] as part of the filter.
     */
    private fun keyQueryRaw(
            ancestor: Key?, builder: TypedQueryBuilder.ForKey<Tbl>.() -> Unit
    ): QueryResults<Key> =
            TypedQueryBuilder.ForKey(table = table, ancestor = ancestor)
                    .apply(block = builder)
                    .build()
                    .let { datastore.run(it) }

    /**
     * [projectionEntityQueryRaw] uses the given entity query builder [builder] to construct a query
     * and returns the raw projection entity query result. It can also accept an additional
     * [ancestor] as part of the filter.
     *
     * @param projections the collection contains a non-empty set of properties to select.
     */
    private fun projectionEntityQueryRaw(
            ancestor: Key?, projections: Collection<Property<Tbl, *>>,
            builder: TypedQueryBuilder.ForProjection<Tbl>.() -> Unit
    ): QueryResults<ProjectionEntity> =
            TypedQueryBuilder.ForProjection(
                    projections = projections, table = table, ancestor = ancestor
            ).apply(block = builder).build().let { datastore.run(it) }

    /**
     * [query] uses the given query builder [builder] to construct a query and returns the result
     * in sequence. It can also accept an additional [ancestor] as part of the filter.
     */
    fun query(
            ancestor: Key? = null, builder: TypedQueryBuilder.ForEntity<Tbl>.() -> Unit
    ): Sequence<E> =
            entityQueryRaw(builder = builder, ancestor = ancestor)
                    .asSequence().map(transform = ::create)

    /**
     * [queryKeys] uses the given key query builder [builder] to construct a key query and returns
     * the keys in sequence. It can also accept an additional [ancestor] as part of the filter.
     */
    fun queryKeys(
            ancestor: Key? = null, builder: TypedQueryBuilder.ForKey<Tbl>.() -> Unit
    ): Sequence<Key> =
            keyQueryRaw(builder = builder, ancestor = ancestor).asSequence()

    /**
     * [queryProjections] uses the given projection query builder [builder] to construct a
     * projection query and returns the result in sequence. It can also accept an additional
     * [ancestor] as part of the filter.
     *
     * @param projections the collection contains a non-empty set of properties to select.
     */
    fun queryProjections(
            ancestor: Key? = null, projections: Collection<Property<Tbl, *>>,
            builder: TypedQueryBuilder.ForProjection<Tbl>.() -> Unit
    ): Sequence<ProjectionEntity> =
            projectionEntityQueryRaw(
                    ancestor = ancestor, projections = projections, builder = builder
            ).asSequence()

    /**
     * [queryCursored] uses the given query builder [builder] to construct a query and returns the
     * result in list along with a cursor after. It can also accept an additional [ancestor] as part
     * of the filter.
     */
    fun queryCursored(
            ancestor: Key? = null, builder: TypedQueryBuilder.ForEntity<Tbl>.() -> Unit
    ): Pair<List<E>, Cursor> {
        val result = entityQueryRaw(ancestor = ancestor, builder = builder)
        val entities = result.asSequence().map(transform = ::create).toList()
        val cursorAfter = result.cursorAfter
        return entities to cursorAfter
    }

    /**
     * [queryKeysCursored] uses the given query builder [builder] to construct a query and returns
     * the keys in list along with a cursor after. It can also accept an additional [ancestor] as
     * part of the filter.
     */
    fun queryKeysCursored(
            ancestor: Key? = null, builder: TypedQueryBuilder.ForKey<Tbl>.() -> Unit
    ): Pair<List<Key>, Cursor> {
        val result = keyQueryRaw(ancestor = ancestor, builder = builder)
        val keys = result.asSequence().toList()
        val cursorAfter = result.cursorAfter
        return keys to cursorAfter
    }

    /**
     * [queryProjectionsCursored] uses the given query builder [builder] to construct a query and
     * returns the projections in list along with a cursor after. It can also accept an additional
     * [ancestor] as part of the filter.
     *
     * @param projections the collection contains a non-empty set of properties to select.
     */
    fun queryProjectionsCursored(
            ancestor: Key? = null, projections: Collection<Property<Tbl, *>>,
            builder: TypedQueryBuilder.ForProjection<Tbl>.() -> Unit
    ): Pair<List<ProjectionEntity>, Cursor> {
        val result = projectionEntityQueryRaw(
                ancestor = ancestor, projections = projections, builder = builder
        )
        val p = result.asSequence().toList()
        val cursorAfter = result.cursorAfter
        return p to cursorAfter
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 2: Creation
     * --------------------------------------------------------------------------------
     */

    /**
     * [createNewKey] creates and returns a new key for an new entity, with a nullable [parent] as
     * a possible parameter for the key.
     */
    private fun createNewKey(parent: Key?): Key = datastore.newKeyFactory()
            .let { factory ->
                parent?.let { factory.addAncestor(PathElement.of(it.kind, it.id)) } ?: factory
            }
            .setKind(table.tableName)
            .newKey()
            .let { datastore.allocateId(it) }

    /**
     * [insert] inserts a new entity created by [builder] with an optional [parent] into datastore.
     * It returns the newly created entity.
     */
    fun insert(parent: Key? = null, builder: TypedEntityBuilder<Tbl, E>.() -> Unit): E {
        val newKey = createNewKey(parent = parent)
        val newEntity = TypedEntityBuilder<Tbl, E>(table = table, newKey = newKey)
                .apply(block = builder)
                .buildEntity()
        return datastore.add(newEntity).let { create(entity = it) }
    }

    /**
     * [batchInsert] inserts a collection of entities built from an optional [parent], collection of
     * source data in [source] and a [builder].
     * It returns a list of newly created entities' keys.
     */
    fun <T : Any> batchInsert(
            parent: Key? = null, source: List<T>,
            builder: TypedEntityBuilder<Tbl, E>.(T) -> Unit
    ): List<E> {
        val newEntities = source.map { s ->
            TypedEntityBuilder<Tbl, E>(table = table, newKey = createNewKey(parent = parent))
                    .apply { builder(s) }
                    .buildEntity()
        }
        return DatastoreVarargAdapter.add(datastore, newEntities).map(transform = ::create)
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 3: Update
     * --------------------------------------------------------------------------------
     */

    /**
     * [update] updates the [entity] with the given [builder], puts the updated one into the
     * database and returns the updated entity.
     */
    fun update(entity: E, builder: TypedEntityBuilder<Tbl, E>.() -> Unit): E {
        val updatedEntity = TypedEntityBuilder(table = table, existingEntity = entity)
                .apply(block = builder)
                .buildEntity()
        // Optimize if nothing is updated.
        return if (entity.entity == updatedEntity) entity else {
            create(entity = datastore.put(updatedEntity))
        }
    }

    /**
     * [batchUpdate] updates a list of [entities] with the specified [builder].
     */
    fun batchUpdate(entities: List<E>, builder: TypedEntityBuilder<Tbl, E>.(E) -> Unit) {
        val updatedEntities = entities.map { e ->
            TypedEntityBuilder(table = table, existingEntity = e)
                    .apply { builder(e) }
                    .buildEntity()
        }
        DatastoreVarargAdapter.put(datastore, updatedEntities)
    }

    /**
     * [batchUpdate] updates a list of [entities] according to a list of [source] with the specified
     * [builder].
     *
     * Requires: [entities] and [source] must have the same size.
     */
    fun <T : Any> batchUpdate(
            entities: List<E>, source: List<T>,
            builder: TypedEntityBuilder<Tbl, E>.(T) -> Unit
    ) {
        val len = entities.size
        if (len != source.size) {
            throw IllegalArgumentException("Entity-source size mismatch!")
        }
        val updatedEntities = ArrayList<Entity>(len)
        for (i in 0 until len) {
            TypedEntityBuilder(table = table, existingEntity = entities[i])
                    .apply { builder(source[i]) }
                    .let { updatedEntities.add(element = it.buildEntity()) }
        }
        DatastoreVarargAdapter.put(datastore, updatedEntities)
    }

    /**
     * [upsert] updates the entity [entity] into the database according to [builder] in it's given,
     * or inserts according to the [builder] if it's not given.
     * In either case, the key of the new entity is returned.
     */
    fun upsert(entity: E?, builder: TypedEntityBuilder<Tbl, E>.() -> Unit): E =
            entity?.let { update(entity = it, builder = builder) } ?: insert(builder = builder)

    /*
     * --------------------------------------------------------------------------------
     * Part 4: Deletion
     * --------------------------------------------------------------------------------
     */

    /**
     * [delete] deletes the entity with the given [key].
     * There is no restriction on the type of the entity backed by the key.
     */
    fun delete(key: Key): Unit = datastore.delete(key)

    /**
     * [delete] deletes a collection of entities with the specified collection of [keys].
     * There is no restriction on the types of the entities backed by the keys.
     */
    fun delete(keys: Collection<Key>): Unit = DatastoreVarargAdapter.delete(datastore, keys)

    /**
     * [deleteAll] deletes all entities in this table.
     */
    fun deleteAll(): Unit = delete(keys = allKeys().toList())

}
