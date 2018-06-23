# TypedStore

A declarative and type-safe client library for GCP Datastore.

## What We Need and Design Goals

GCP Datastore Client Library is great because you don't need to handle the API in raw requests by
yourself. However, it does not provide a type-safe way for you to write maintainable code.

If you take a look at the javadoc of the [Entity class](https://googlecloudplatform.github.io/google-cloud-java/google-cloud-clients/apidocs/com/google/cloud/datastore/BaseEntity.html) 
you will see that it provides a lot of get methods with different types. Although you don't need to
do the type casts by yourself now, it is still unsafe to use: the types of each property is not
enforced by the type system.

Inspired by the design of [Jetbrains' Exposed framework](https://github.com/JetBrains/Exposed), this
library aims to provide a type-safe framework for writing scalable and maintainable GCP Datastore 
code in Kotlin. We choose Kotlin because its type system has built-in support for explicit 
nullability check, which suits the purpose of this library.

Instead of writing your code imperatively to update or request some data, you write declarative code
with this framework. You specify the structure of the entity in `TypedTable` and `TypedEntity` 
object. You specify how to retrieve and update data by DSL-like lambda expression in Kotlin.

## Getting Started

Declaring a table:

```kotlin
object FooTable : TypedTable<FooTable>() {
    val bar = stringProperty(name = "bar")
    val answer42 = longProperty(name = "answer")
}
```

You can see all the supported data types in `TypedTable` class.

Declaring an entity with its companion:

```kotlin
import com.google.cloud.datastore.Entity // We use GCP Datastore entity

class FooEntity(entity: Entity) : TypedEntity<FooTable>(entity = entity) {
  val bar: String = FooTable.bar.delegatedValue
  val answer42: Long get() = FooTable.answer42.delegatedValue
  
  companion object : TypedEntityCompanion<FooEntity>(table = FooTable) {
    override fun create(entity: Entity): FooEntity = FooEntity(entity = entity)
  }
}
```

Sample CRUD:

```kotlin
// Create
val obj = FooEntity.insert { 
    it[FooTable.bar] = "haha"
    it[FooTable.answer42] = 42
}
// Read
val entities = FooEntity.query { 
  filter = FooTable.answer42 eq 42
  limit = 3
}.toList()
// Update
val updated = FooEntity.update(entity = obj) { it[FooTable.bar] = "Oh, no!" }
// Delete
fun d() = FooEntity.delete(updated.key)
```

Transaction: 

Assuming your datastore object is `datastore`, you can write an inline function `transaction` in
this way and simply use it. The code for transaction is put inside the inlined lambda expression.

```kotlin
inline fun <reified T> transaction(crossinline f: () -> T): T = datastore.transaction(f)
```

## Notes

- When engineering your application, you should think of this library as a simple wrapper for GCP 
Datastore. In other words, this library changes the style of your DB related code but does not
magically solves all your problems. For example, you are still subject to all the constrains on GCP.
- Currently, caching is not supported.
- Although you can use this library in plain Java, you will get more verbose syntax and will lose
the nullability type-check.

## License

MIT

## Contributing

This project is still in prototype and the support for GCP Datastore is not complete.

You can create a pull request or an issue.
