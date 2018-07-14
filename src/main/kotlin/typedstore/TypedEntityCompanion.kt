package typedstore

import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.Query
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

    /**
     * [create] creates a [TypedEntity] from an `Entity` from GCP Datastore.
     */
    protected abstract fun create(entity: Entity): E

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
     * [allKeys] simply returns all keys without any restrictions.
     */
    fun allKeys(): Sequence<Key> =
            Query.newKeyQueryBuilder().setKind(table.tableName).build()
                    .let { datastore.run(it) }
                    .asSequence()

    /**
     * [any] tests and returns whether there exists any entity as specified by the query in
     * [builder].
     */
    fun any(builder: TypedQueryBuilder<Tbl>.() -> Unit = {}): Boolean =
            TypedQueryBuilder(table = table).apply(builder)
                    .build()
                    .let { datastore.run(it) }
                    .hasNext()

    /**
     * [all] simply returns all entities without any restrictions.
     */
    fun all(): Sequence<E> = query(builder = {})

    /**
     * [allDescenders] returns all entities which is the descender of the given [ancestor]'s key,
     * without any other restrictions.
     */
    fun allDescenders(ancestor: Key): Sequence<E> = query(ancestor = ancestor)

    /**
     * [queryRaw] uses the given query builder [builder] to construct a query and returns the raw
     * query result.
     */
    private fun queryRaw(builder: TypedQueryBuilder<Tbl>.() -> Unit): QueryResults<Entity> =
            datastore.run(TypedQueryBuilder(table = table).apply(block = builder).build())

    /**
     * [query] uses the given query builder [builder] to construct a query and returns the result
     * in sequence.
     */
    fun query(builder: TypedQueryBuilder<Tbl>.() -> Unit): Sequence<E> =
            queryRaw(builder = builder).asSequence().map(transform = ::create)

    /**
     * [queryCursored] uses the given query builder [builder] to construct a query and returns the
     * result in sequence along with a cursor after.
     */
    fun queryCursored(builder: TypedQueryBuilder<Tbl>.() -> Unit): Pair<Sequence<E>, Cursor> {
        val result = queryRaw(builder = builder)
        return result.asSequence().map(transform = ::create) to result.cursorAfter
    }

    /**
     * [queryRaw] uses the given query builder [builder] to construct a query and returns the raw
     * query result.
     */
    /**
     * [queryRaw] uses the given [ancestor] key and the given query [builder] to construct a query
     * and returns the raw query result.
     */
    private fun queryRaw(
            ancestor: Key, builder: TypedAncestorQueryBuilder<Tbl>.() -> Unit
    ): QueryResults<Entity> =
            TypedAncestorQueryBuilder(table = table, ancestor = ancestor)
                    .apply(block = builder)
                    .build()
                    .let { datastore.run(it) }

    /**
     * [query] uses the given [ancestor] key and the given query [builder] to construct a query and
     * returns the result in sequence.
     */
    fun query(ancestor: Key, builder: TypedAncestorQueryBuilder<Tbl>.() -> Unit = {}): Sequence<E> =
            queryRaw(ancestor = ancestor, builder = builder).asSequence().map(transform = ::create)

    /**
     * [queryCursored] uses the given [ancestor] key and the given query builder [builder] to
     * construct a query and returns the result in sequence along with a cursor after.
     */
    fun queryCursored(
            ancestor: Key, builder: TypedAncestorQueryBuilder<Tbl>.() -> Unit = {}
    ): Pair<Sequence<E>, Cursor> {
        val result = queryRaw(ancestor = ancestor, builder = builder)
        return result.asSequence().map(transform = ::create) to result.cursorAfter
    }

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
