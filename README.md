# TypedStore

![CircleCI branch](https://img.shields.io/circleci/project/github/SamChou19815/typed-store/master.svg)
[![Release](https://jitpack.io/v/SamChou19815/typed-store.svg)](https://jitpack.io/#SamChou19815/typed-store)
![GitHub](https://img.shields.io/github/license/SamChou19815/typed-store.svg)

<img src="https://developersam.com/assets/app-icons/typed-store.png" width="100%" />

A declarative and type-safe client library for GCP Datastore.

## Gradle Config

Read the docs [here](https://docs.developersam.com/typed-store/)

Add this to your `build.gradle` to use the artifact.

```groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.SamChou19815:typed-store:0.8.0'
}
```

## What We Need and Design Goals

GCP Datastore Client Library is great because you don't need to handle the API in raw requests by
yourself. However, it does not provide a type-safe way for you to write maintainable code.

If you take a look at the javadoc of the
[Entity class](https://googlecloudplatform.github.io/google-cloud-java/google-cloud-clients/apidocs/com/google/cloud/datastore/BaseEntity.html)
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

### Declaring a Table

```kotlin
object FooTable : TypedTable<FooTable>() {
    val bar = stringProperty(name = "bar")
    val answer42 = longProperty(name = "answer")
}
```

You can see all the supported data types in the
[TypedTable](./src/main/kotlin/typedstore/TypedTable.kt) class.

### Declaring an Entity with Its Companion

```kotlin
import com.google.cloud.datastore.Entity // We use GCP Datastore entity

class FooEntity(entity: Entity) : TypedEntity<FooTable>(entity = entity) {
  val bar: String = FooTable.bar.delegatedValue
  val answer42: Long get() = FooTable.answer42.delegatedValue

  companion object : TypedEntityCompanion<FooTable, FooEntity>(table = FooTable) {
    override fun create(entity: Entity): FooEntity = FooEntity(entity = entity)
  }
}
```

Although the generics declaration is a little ugly, it's needed for type-safe CRUD.

### Sample CRUD

```kotlin
// Create
val obj = FooEntity.insert {
    // You need to explicitly declare all the fields. Otherwise, it will throw an exception.
    table.bar gets "haha"
    // The second way of setting things
    this[FooTable.answer42] = 42
    // The type system ensures `this[BarTable.bar] = 42` is a compile time error.
}
// Read
val entities = FooEntity.query {
  // filter, order, and limit are all optional
  filter { table.answer42 eq 42 }
  order { table.answer42.desc() }
  withLimit(limit = 3)
}.toList()
// Update
val updated = FooEntity.update(entity = obj) { FooTable.bar gets "Oh, no!" }
// Delete
fun d() = FooEntity.delete(updated.key)
```

Notes:
- You can see a list of all supported DB operations in the
[TypedEntityCompanion](./src/main/kotlin/typedstore/TypedEntityCompanion.kt) class.
- You can see a list of all supported filters in the
[TypedFilterBuilder](./src/main/kotlin/typedstore/TypedFilterBuilder.kt) class.

### Transaction

Assuming your datastore object is `datastore`, you can write an inline function `transaction` in
this way and simply use it. The code for transaction is put inside the inlined lambda expression.

```kotlin
inline fun <reified T> transaction(crossinline f: () -> T): T = datastore.transaction(f)
```

### More Examples

The test also shows some example usage. You can read those tests or add some of yours and make a
pull request.

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
