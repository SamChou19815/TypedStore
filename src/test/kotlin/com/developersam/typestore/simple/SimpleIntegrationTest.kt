package com.developersam.typestore.simple

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * [SimpleIntegrationTest] tests how different parts of the typed-wrappers can work together by
 * a very simple example.
 */
class SimpleIntegrationTest {

    @Test
    fun simpleIntegrationTest() {
        // Create
        val obj = SimpleEntity.insert { it[SimpleTable.simpleProp] = 1 }
        val key = obj.key
        // Read
        val objFromKey = SimpleEntity.getNotNull(key = key)
        val objFromQuery = SimpleEntity.query { filter = SimpleTable.simpleProp eq 1 }.first()
        assertEquals(objFromKey.simpleProp, objFromQuery.simpleProp)
        // Update
        val newKey = SimpleEntity.update(entity = objFromKey) { it[SimpleTable.simpleProp] = 2 }.key
        val newObj = SimpleEntity.getNotNull(key = newKey)
        assertEquals(2, newObj.simpleProp)
        assertEquals(newKey, key)
        val newKeyWithUpdate = SimpleEntity.upsert(newObj) { it[SimpleTable.simpleProp] = 3 }.key
        assertEquals(3, SimpleEntity.getNotNull(key = newKeyWithUpdate).simpleProp)
        // Batch Create & Update
        SimpleEntity.apply {
            val es = batchInsert(source = listOf(5L, 6L)) { t, n -> t[SimpleTable.simpleProp] = n }
            val size = batchUpdate(entities = es) { t, _ -> t[SimpleTable.simpleProp] = 10 }.size
            assertEquals(2, size)
        }
        // Delete
        val keysToBeDeleted = SimpleEntity.all().map { it.key }.toList().toTypedArray()
        SimpleEntity.delete(*keysToBeDeleted)
        for (deletedKey in keysToBeDeleted) {
            assertTrue(deletedKey !in SimpleEntity)
        }
    }

}
