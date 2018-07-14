package typedstore.simple

import com.google.cloud.datastore.Key
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import typedstore.nowInUTC

/**
 * [SimpleIntegrationTest] tests how different parts of the typed-wrappers can work together by
 * a very simple example.
 */
class SimpleIntegrationTest {

    /**
     * Create the first object and returns the key.
     */
    private fun create(): Key = SimpleEntity.insert {
        table.simpleProp gets 1
        table.simpleDate gets nowInUTC()
        table.simpleEnum gets SimpleEnum.A
    }.key

    /**
     * Read from the given [key], query and compare.
     */
    private fun read(key: Key): SimpleEntity {
        val objFromKey = SimpleEntity.getNotNull(key = key)
        val objFromQuery = SimpleEntity.query {
            filter {
                table.simpleProp eq 1
                table.simpleDate.isPast()
                table.simpleEnum eq SimpleEnum.A
            }
            order {
                table.simpleProp.desc()
                table.simpleDate.asc()
            }
            withLimit(limit = 100)
        }.first()
        assertEquals(objFromKey.simpleProp, objFromQuery.simpleProp)
        return objFromKey
    }

    /**
     * Update from the given object [obj].
     */
    private fun update(obj: SimpleEntity) {
        val newKey = SimpleEntity.update(entity = obj) { table.simpleProp gets 2 }.key
        val newObj = SimpleEntity.getNotNull(key = newKey)
        assertEquals(2, newObj.simpleProp)
        assertEquals(newKey, obj.key)
        val newKeyWithUpdate = SimpleEntity.upsert(newObj) { table.simpleProp gets 3 }.key
        assertEquals(3, SimpleEntity.getNotNull(key = newKeyWithUpdate).simpleProp)
    }

    /**
     * Run some batch operations
     */
    private fun batchOperations() {
        SimpleEntity.apply {
            val entities = batchInsert(source = listOf(5L, 6L)) { n ->
                table.simpleProp gets n
                table.simpleDate gets nowInUTC()
                table.simpleEnum gets SimpleEnum.B
            }
            val size = batchUpdate(entities = entities) { table.simpleProp gets 10 }.size
            assertEquals(2, size)
        }
    }

    /**
     * Delete all data to clean up.
     */
    private fun deleteAll() {
        val keysToBeDeleted = SimpleEntity.all().map { it.key }.toList()
        SimpleEntity.delete(keys = keysToBeDeleted)
        SimpleEntity.deleteAll()
        for (deletedKey in keysToBeDeleted) {
            assertTrue(deletedKey !in SimpleEntity)
        }
    }

    @Test
    fun simpleIntegrationTest() {
        val key = create() // C
        val obj = read(key = key) // R
        update(obj = obj) // U
        batchOperations() // C & U
        deleteAll() // D
    }

}
