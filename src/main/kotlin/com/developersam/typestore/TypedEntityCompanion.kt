package com.developersam.typestore

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreException
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
) : TypedEntityCreator<E> {

    /**
     * [get] returns the entity with the given [key].
     * If no such entity is found, it will throw an [DatastoreException]
     */
    operator fun get(key: Key): E = create(entity = datastore[key])

    /**
     * [query] uses the given query builder [builder] to constructor a query and returns the result
     * in sequence.
     * If [builder] is not given, it will be a query with no restrictions on this type of
     * entity.
     */
    fun query(builder: TypedQueryBuilder.() -> Unit = {}): Sequence<E> =
            TypedQueryBuilder(table = table).apply(builder)
                    .build()
                    .let { datastore.run(it) }
                    .asSequence()
                    .map { create(entity = it) }

    /**
     * [insert] inserts a new entity created by [builder] with an optional [parent] into datastore.
     * It returns the key of the newly created entity.
     */
    fun insert(parent: Key? = null, builder: (TypedEntityBuilder<E>) -> Unit): Key {
        val incompleteKey = datastore.newKeyFactory()
                .let { factory ->
                    parent?.let { factory.addAncestor(PathElement.of(it.kind, it.id)) } ?: factory
                }
                .setKind(table.tableName)
                .newKey()
        val newEntity = datastore.allocateId(incompleteKey)
                .let { Entity.newBuilder(it) }
                .let { TypedEntityBuilder(partialBuilder = it, creator = this) }
                .apply(builder)
                .buildRawEntity()
        return datastore.add(newEntity).key
    }

    /**
     * [update] updates the [entity] with the given [builder], puts the updated one into the
     * database and returns the key of the entity.
     */
    fun update(entity: E, builder: (TypedEntityBuilder<E>) -> Unit): Key {
        val updatedEntity = Entity.newBuilder(entity.rawEntity)
                .let { TypedEntityBuilder(partialBuilder = it, creator = this) }
                .apply(builder)
                .buildRawEntity()
        return datastore.put(updatedEntity).key
    }

    /**
     * [upsert] updates the entity [entity] into the database according to [builder] in it's given,
     * or inserts according to the [builder] if it's not given.
     * In either case, the key of the new entity is returned.
     */
    fun upsert(entity: E?, builder: (TypedEntityBuilder<E>) -> Unit): Key =
            entity?.let { update(entity = it, builder = builder) } ?: insert(builder = builder)

    /**
     * [delete] deletes the given [entity] from the datastore.
     */
    fun delete(entity: TypedEntity): Unit = datastore.delete(entity.key)

    /**
     * [delete] deletes the entity with [key] from the datastore.
     */
    fun delete(key: Key): Unit = datastore.delete(key)

}
