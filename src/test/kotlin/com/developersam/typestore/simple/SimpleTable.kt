package com.developersam.typestore.simple

import com.developersam.typestore.TypedTable

/**
 * [SimpleTable] is a very simple table just to test whether the system works.
 */
object SimpleTable : TypedTable<SimpleTable>() {
    val simpleProp = longProperty(name = "Simple")
    val simpleDate = datetimeProperty(name = "date")
    val simpleEnum = enumProperty(name = "enum", clazz = SimpleEnum::class.java)
}
