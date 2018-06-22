package com.developersam.typestore

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.PathElement

/**
 * [TypedEntityCompanion] is designed to be the companion object of a [TypedEntity], so that typed
 * CRUD operations can be launched directly from there.
 *
 * @property table the table associated with the entity.
 * @param E specific type of the typed entity.
 */
abstract class TypedEntityCompanion<E : TypedEntity>(
        private val datastore: Datastore = defaultDatastore, private val table: TypedTable
) {

    /**
     * [create] creates a [TypedEntity] from an [Entity] from GCP Datastore.
     */
    protected abstract fun create(entity: Entity): E

    /**
     * [get] returns the entity with the given [key] or `null` if it does not exist.
     */
    operator fun get(key: Key): E? = datastore[key]?.let { create(entity = it) }

    /**
     * [get] returns the entity with the given [key].
     * If no such entity is found, it will throw an [IllegalArgumentException]
     */
    fun getNotNull(key: Key): E = get(key = key).let { checkNotNull(value = it) }

    /**
     * [contains] tests whether an entity with given [key] exists in the datastore.
     */
    operator fun contains(key: Key): Boolean = datastore[key] != null

    /**
     * [any] tests and returns whether there exists any entity as specified by the query in
     * [builder].
     */
    fun any(builder: TypedQueryBuilder.() -> Unit): Boolean =
            TypedQueryBuilder(table = table).apply(builder)
                    .build()
                    .let { datastore.run(it) }
                    .hasNext()

    /**
     * [all] simply returns all entities without any restrictions.
     */
    fun all(): Sequence<E> = query(builder = {})

    /**
     * [query] uses the given query builder [builder] to constructor a query and returns the result
     * in sequence.
     */
    fun query(builder: TypedQueryBuilder.() -> Unit): Sequence<E> =
            TypedQueryBuilder(table = table).apply(builder)
                    .build()
                    .let { datastore.run(it) }
                    .asSequence()
                    .map { create(entity = it) }

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
    fun insert(parent: Key? = null, builder: (TypedEntityBuilder<E>) -> Unit): E {
        val newKey = createNewKey(parent = parent)
        val newEntity = Entity.newBuilder(newKey)
                .let { TypedEntityBuilder<E>(partialBuilder = it) }
                .apply(builder)
                .buildEntity()
        return datastore.add(newEntity).let { create(entity = it) }
    }

    /**
     * [batchInsert] inserts a collection of entities built from an optional [parent], collection of
     * source data in [source] and a [builder].
     * It returns a list of newly created entities.
     */
    fun <T : Any> batchInsert(
            parent: Key? = null, source: Iterable<T>, builder: (TypedEntityBuilder<E>, T) -> Unit
    ): List<E> {
        val newEntities = source.map { s ->
            Entity.newBuilder(createNewKey(parent = parent))
                    .let { TypedEntityBuilder<E>(partialBuilder = it) }
                    .apply { builder(this, s) }
                    .buildEntity()
        }
        return datastore.add(*newEntities.toTypedArray()).map { create(entity = it) }
    }

    /**
     * [update] updates the [entity] with the given [builder], puts the updated one into the
     * database and returns the updated entity.
     */
    fun update(entity: E, builder: (TypedEntityBuilder<E>) -> Unit): E {
        val updatedEntity = Entity.newBuilder(entity.entity)
                .let { TypedEntityBuilder<E>(partialBuilder = it) }
                .apply(builder)
                .buildEntity()
        return datastore.put(updatedEntity).let { create(entity = it) }
    }

    /**
     * [batchUpdate] updates a list of [entities] with the specified [builder] and returns a list
     * of the updated entities.
     */
    fun batchUpdate(entities: List<E>, builder: (TypedEntityBuilder<E>, E) -> Unit): List<E> {
        val updatedEntities = entities.map { e ->
            Entity.newBuilder(e.entity)
                    .let { TypedEntityBuilder<E>(partialBuilder = it) }
                    .apply { builder(this, e) }
                    .buildEntity()
        }
        return datastore.put(*updatedEntities.toTypedArray()).map { create(entity = it) }
    }

    /**
     * [upsert] updates the entity [entity] into the database according to [builder] in it's given,
     * or inserts according to the [builder] if it's not given.
     * In either case, the key of the new entity is returned.
     */
    fun upsert(entity: E?, builder: (TypedEntityBuilder<E>) -> Unit): E =
            entity?.let { update(entity = it, builder = builder) } ?: insert(builder = builder)

    /**
     * [delete] deletes the given [entities] from the datastore.
     */
    fun delete(vararg entities: TypedEntity): Unit =
            datastore.delete(*entities.map(TypedEntity::key).toTypedArray())

    /**
     * [delete] deletes the entities with given [keys] from the datastore.
     */
    fun delete(vararg keys: Key): Unit = datastore.delete(*keys)

}
