package com.developersam.typestore.integrations.simple

import com.developersam.typestore.TypedEntity
import com.developersam.typestore.TypedEntityCompanion
import com.google.cloud.datastore.Entity

/**
 * [SimpleEntity] is a very simple entity just to test whether the system works.
 */
class SimpleEntity(entity: Entity) : TypedEntity(entity = entity) {
    val simpleProp = SimpleTable.simpleProp.delegatedValue

    companion object : TypedEntityCompanion<SimpleEntity>(table = SimpleTable) {
        override fun create(entity: Entity): SimpleEntity = SimpleEntity(entity = entity)
    }
}
