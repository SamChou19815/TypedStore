package com.developersam.typestore

import com.google.cloud.datastore.Entity

/**
 * [TypedEntityCreator] defines how a typed entity can be created.
 *
 * For internal use only.
 */
internal interface TypedEntityCreator<E: TypedEntity> {

    /**
     * [create] creates a [TypedEntity] from an [Entity] from GCP Datastore.
     */
    fun create(entity: Entity): E

}
