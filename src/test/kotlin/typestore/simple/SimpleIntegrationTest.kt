package typestore.simple

import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import typestore.nowInUTC

/**
 * [SimpleIntegrationTest] tests how different parts of the typed-wrappers can work together by
 * a very simple example.
 */
class SimpleIntegrationTest {

    @Test
    fun simpleIntegrationTest() {
        // Create
        val key = SimpleEntity.insert {
            table.simpleProp gets 1
            table.simpleDate gets nowInUTC()
            table.simpleEnum gets SimpleEnum.A
        }.key
        // Read
        val objFromKey = SimpleEntity.getNotNull(key = key)
        val objFromQuery = SimpleEntity.query {
            filter {
                table.simpleProp eq 1
                table.simpleDate.isPast()
                table.simpleEnum eq SimpleEnum.A
            }
        }.first()
        assertEquals(objFromKey.simpleProp, objFromQuery.simpleProp)
        // Update
        kotlin.run {
            val newKey = SimpleEntity.update(entity = objFromKey) { table.simpleProp gets 2 }.key
            val newObj = SimpleEntity.getNotNull(key = newKey)
            assertEquals(2, newObj.simpleProp)
            assertEquals(newKey, key)
            val newKeyWithUpdate = SimpleEntity.upsert(newObj) { table.simpleProp gets 3 }.key
            assertEquals(3, SimpleEntity.getNotNull(key = newKeyWithUpdate).simpleProp)
        }
        // Batch Create & Update
        SimpleEntity.apply {
            val entities = batchInsert(source = listOf(5L, 6L)) { n ->
                table.simpleProp gets n
                table.simpleDate gets nowInUTC()
                table.simpleEnum gets SimpleEnum.B
            }
            val size = batchUpdate(entities = entities) { table.simpleProp gets 10 }.size
            assertEquals(2, size)
        }
        // Delete All
        kotlin.run {
            val keysToBeDeleted = SimpleEntity.all().map { it.key }.toList().toTypedArray()
            SimpleEntity.delete(*keysToBeDeleted)
            for (deletedKey in keysToBeDeleted) {
                assertTrue(deletedKey !in SimpleEntity)
            }
        }
    }

}
