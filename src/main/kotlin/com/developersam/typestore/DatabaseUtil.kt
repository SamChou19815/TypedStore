package com.developersam.typestore

import com.google.cloud.BaseServiceException
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreException
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Transaction

/**
 * The globally used datastore object.
 */
val defaultDatastore: Datastore = DatastoreOptions.getDefaultInstance().service

/**
 * [transaction] is an inline function that performs a DB transaction defined in [f] and returns
 * the result.
 */
inline fun <reified T> Datastore.transaction(crossinline f: (Datastore) -> T): T {
    val txn: Transaction = newTransaction()
    try {
        val value = f(this)
        txn.commit()
        return value
    } catch (e: Exception) {
        txn.rollback()
        throw DatastoreException(BaseServiceException.UNKNOWN_CODE, e.message, null, e);
    } finally {
        if (txn.isActive) {
            txn.rollback()
        }
    }
}
