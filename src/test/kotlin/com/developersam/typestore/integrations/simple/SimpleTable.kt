package com.developersam.typestore.integrations.simple

import com.developersam.typestore.TypedTable

/**
 * [SimpleTable] is a very simple table just to test whether the system works.
 */
object SimpleTable : TypedTable<SimpleTable>() {
    val simpleProp = longProperty(name = "Simple")
}
