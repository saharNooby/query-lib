# query-lib

Tired of writing endless `try-catch` blocks for `Connection`s, `PreparedStatement`s and `ResultSet`s? Try this!

This very lightweight (27 KB) library with no dependencies allows you to:
- generate SQL queries
- wrap existing queries (for example, if they are complex)
- execute an update
- execute a query and then map single or all results
- execute a batch

All of this without any `try-catch` blocks and with (usually) much shorter code. `SQLException`s are still checked.

The library is intended to be as easily integrated into the existing code as possible.

## Example

Vanilla Java:

```java
Optional<String> result;
String sql = "SELECT `value` FROM `table` WHERE `key` = ?";
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    p.setInt(1, key);
    try (ResultSet set = p.executeQuery()) {
        result = set.next() ? Optional.ofNullable(set.getString(1)) : Optional.empty();
    }
}
result.ifPresent(System.out::print);

```

With query-lib:

```java
Query.select("value")
        .from("table")
        .where("key", key)
        .queryAndMap(source, s -> s.getString(1))
        .ifPresent(System.out::print);
```

You can see more examples in [EXAMPLES.md](https://github.com/saharNooby/query-lib/blob/master/EXAMPLES.md).

## How to Use

### Add as a dependency

This library is available in Maven Central.

#### Maven

```xml
<dependency>
    <groupId>me.saharnooby</groupId>
    <artifactId>query-lib</artifactId>
    <version>1.2.2</version>
</dependency>
```

#### Gradle

```groovy
dependencies {
	implementation 'me.saharnooby:query-lib:1.2.2'
}
```

#### Other build systems

You can download prebuilt JARs from [GitHub releases](https://github.com/saharNooby/query-lib/releases) or build them yourself.

### Usage

Use any of the static methods of `me.saharnooby.lib.query.query.Query` to
obtain an `AbstractQuery` object, then you can specify some values and
call `update`, `query`, `queryAndMap` or `queryAndMapAll`.

`query` returns a `ResultSetWrapper` which allows you to map the set using
specified mapper functions.

To build and execute a batch of queries, create `me.saharnooby.lib.query.batch.BatchBuilder`,
call `add` as many times as needed, then `execute`.

## Building

You will need Git, Maven and JDK 8 or higher.

```shell
git clone https://github.com/saharNooby/query-lib.git
cd query-lib
mvn clean install
```
