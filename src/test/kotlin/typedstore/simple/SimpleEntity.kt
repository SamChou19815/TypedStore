package typedstore.simple

import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import com.google.cloud.datastore.Entity
import java.time.LocalDateTime

/**
 * [SimpleEntity] is a very simple entity just to test whether the system works.
 */
class SimpleEntity(entity: Entity) : TypedEntity<SimpleTable>(entity = entity) {
    val simpleProp: Long = SimpleTable.simpleProp.delegatedValue
    val simpleDate: LocalDateTime = SimpleTable.simpleDate.delegatedValue
    val simpleEnum: SimpleEnum = SimpleTable.simpleEnum.delegatedValue

    companion object : TypedEntityCompanion<SimpleTable, SimpleEntity>(table = SimpleTable) {
        override fun create(entity: Entity): SimpleEntity = SimpleEntity(entity = entity)
    }
}
