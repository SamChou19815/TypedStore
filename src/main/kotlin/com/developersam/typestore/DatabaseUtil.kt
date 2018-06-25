package com.developersam.typestore

import com.google.cloud.BaseServiceException
import com.google.cloud.NoCredentials
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreException
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Transaction
import java.time.LocalDateTime

/**
 * [DatastoreOptions.Builder.setupLocalDevIfOnLocal] provides an easy-to-use function to
 * automatically detect if the user is on the local environment and setup the datastore in local-dev
 * mode.
 */
fun DatastoreOptions.Builder.setupLocalDevIfOnLocal() {
    val emulatorHostEnv: String? = System.getenv("DATASTORE_EMULATOR_HOST")
    val hostEnv: String? = System.getenv("DATASTORE_HOST")
    val projectIdEnv: String? = System.getenv("DATASTORE_PROJECT_ID")
    hostEnv?.let { setHost(it) }
    projectIdEnv?.let { setProjectId(projectIdEnv) }
    // setup for local dev environment
    emulatorHostEnv?.let { setCredentials(NoCredentials.getInstance()) }
}

/**
 * The globally used datastore object.
 */
val defaultDatastore: Datastore = DatastoreOptions
        .newBuilder()
        .apply(block = DatastoreOptions.Builder::setupLocalDevIfOnLocal)
        .build().service

/**
 * [transaction] is an inline function that performs a DB transaction defined in [f] and returns
 * the result.
 */
inline fun <reified T> Datastore.transaction(crossinline f: () -> T): T {
    val txn: Transaction = newTransaction()
    try {
        val value = f()
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

/**
 * [transaction] is the syntactic sugar for using [Datastore.transaction] for using the default
 * datastore.
 */
inline fun <reified T> transaction(crossinline f: () -> T): T = defaultDatastore.transaction(f)

/**
 * [Timestamp.toLocalDateTime] returns the [LocalDateTime] version of this time.
 */
internal fun Timestamp.toLocalDateTime(): LocalDateTime = toSqlTimestamp().toLocalDateTime()

/**
 * [LocalDateTime.toGcpTimestamp] returns the GCP [Timestamp] version of this time.
 */
internal fun LocalDateTime.toGcpTimestamp(): Timestamp =
        Timestamp.of(java.sql.Timestamp.valueOf(this))
