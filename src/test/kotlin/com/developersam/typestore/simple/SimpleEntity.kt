package com.developersam.typestore.simple

import com.developersam.typestore.TypedEntity
import com.developersam.typestore.TypedEntityCompanion
import com.google.cloud.datastore.Entity

/**
 * [SimpleEntity] is a very simple entity just to test whether the system works.
 */
class SimpleEntity(entity: Entity) : TypedEntity<SimpleTable>(entity = entity) {
    val simpleProp = SimpleTable.simpleProp.delegatedValue
    val simpleDate = SimpleTable.simpleDate.delegatedValue

    companion object : TypedEntityCompanion<SimpleTable, SimpleEntity>(table = SimpleTable) {
        override fun create(entity: Entity): SimpleEntity = SimpleEntity(entity = entity)
    }
}
