### CREATE TABLE

Vanilla Java:

```java
String sql = "CREATE TABLE IF NOT EXISTS `table` (" +
        "`firstKey` VARCHAR(16) NOT NULL, " +
        "`secondKey` INT NOT NULL, " +
        "`textValue` TEXT, " +
        "`longValue` BIGINT, " +
        "PRIMARY KEY(`firstKey`, `secondKey`));";
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    p.executeUpdate();
}
```

With query-lib:

```java
Query.createTable("table")
        .ifNotExists()
        .varchar("firstKey", 16).PK().NN()
        .integer("secondKey").PK().NN()
        .text("textValue")
        .bigint("longValue")
        .update(source);
```

### SELECT

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

### INSERT

Vanilla Java:

```java
String sql = "INSERT INTO `table` (`key`, `value`) VALUES (?, ?);"
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    p.setInt(1, key);
    p.setString(2, value);
    p.executeUpdate();
}

```

With query-lib:

```java
Query.insertInto("table").value("key", key).value("value", value).update(source);
```

### INSERT ON DUPLICATE KEY UPDATE

Vanilla Java:

```java
String sql = "INSERT INTO `table` (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = ?;"
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    p.setInt(1, key);
    p.setString(2, value);
    p.setString(3, value);
    p.executeUpdate();
}

```

With query-lib:

```java
Query.insertInto("table")
        .value("key", key)
        .value("value", value)
        .onDuplicateKeyUpdateExcept("key")
        .update(source);
```

### UPDATE

Vanilla Java:

```java
String sql = "UPDATE `table` SET `value` = ? WHERE `key` = ?;"
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    p.setString(1, value);
    p.setInt(2, key);
    p.executeUpdate();
}

```

With query-lib:

```java
Query.update("table").value("value", value).where("key", key).update(source);
```

### DELETE

Vanilla Java:

```java
String sql = "DELETE FROM `table` WHERE `key` = ?;"
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    p.setInt(1, key);
    p.executeUpdate();
}

```

With query-lib:

```java
Query.deleteFrom("table").where("key", key).update(source);
```

### Raw query

Raw queries allow you to use updates, batches and set mapping with arbitrary SQL statements.

```java
Query.raw("SELECT `key` FROM (SELECT * FROM `table`) WHERE `key` = ?;", key)
        .queryAndMapAll(source, s -> s.getInt(1))
        .forEach(System.out::print);
```

### Batches

Vanilla Java:

```java
List<String> valuesToBeInsert = Arrays.asList("x", "y", "z");
String sql = "INSERT INTO `table` (`value`) VALUES (?);"
try (Connection con = source.getConnection(); PreparedStatement p = source.getConnection().prepareStatement(sql)) {
    for (String v : valuesToBeInsert) {
        p.setString(1, v);
        p.addBatch();
    }
    p.executeBatch();
}

```

With query-lib:

```java
List<String> valuesToBeInsert = Arrays.asList("x", "y", "z");
BatchBuilder builder = new BatchBuilder();
valuesToBeInsert.forEach(v -> builder.add(Query.insertInto("table").value("value", v)));
builder.execute(source);
```