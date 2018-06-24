package com.developersam.typestore

/**
 * [PropertyType] represents a set of supported types in GCP Datastore in their not-nullable form.
 *
 * Currently, it does not contain all the supported types.
 */
internal enum class PropertyType {
    KEY, LONG, DOUBLE, BOOL, STRING, LONG_STRING, BLOB, TIMESTAMP, LAT_LNG
}
