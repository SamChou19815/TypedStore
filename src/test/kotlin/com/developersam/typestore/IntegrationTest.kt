package com.developersam.typestore

import com.google.cloud.datastore.Entity
import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * [IntegrationTest] tests how different parts of the typed-wrappers can work together.
 */
class IntegrationTest {

    private object SimpleTable : TypedTable() {
        val simpleProp = longProperty(name = "Simple")
    }

    private class SimpleEntity(entity: Entity) : TypedEntity(rawEntity = entity) {
        val simpleProp = SimpleTable.simpleProp.delegatedValue

        companion object : TypedEntityCompanion<SimpleEntity>(table = SimpleTable) {
            override fun create(entity: Entity): SimpleEntity = SimpleEntity(entity = entity)
        }
    }

    @Test
    fun simpleIntegrationTest() {
        // Create
        val key = SimpleEntity.insert { it[SimpleTable.simpleProp] = 1 }
        // Read
        val objFromKey = SimpleEntity[key]
        val objFromQuery = SimpleEntity.query { filter = SimpleTable.simpleProp eq 1 }.first()
        assertEquals(objFromKey.simpleProp, objFromQuery.simpleProp)
        // Update
        val newKey = SimpleEntity.update(entity = objFromKey) { it[SimpleTable.simpleProp] = 2 }
        assertEquals(2, SimpleEntity[newKey].simpleProp)
        // Delete
        SimpleEntity.delete(key = newKey)
        assertEquals(0, SimpleEntity.query().count())
    }

}
