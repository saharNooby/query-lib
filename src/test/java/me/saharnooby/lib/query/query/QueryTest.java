package me.saharnooby.lib.query.query;

import me.saharnooby.lib.query.query.impl.Delete;
import me.saharnooby.lib.query.query.impl.Insert;
import me.saharnooby.lib.query.query.impl.Select;
import me.saharnooby.lib.query.query.impl.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author saharNooby
 * @since 12:56 17.11.2019
 */
class QueryTest {

	@Test
	void testCreateTable() {
		Assertions.assertEquals(
				"CREATE TABLE `t` (`k` INT AUTO_INCREMENT, `b` BIGINT DEFAULT ?, PRIMARY KEY (`k`));",
				Query.createTable("t").intKey("k").bigint("b").defaultValue(123L).getSQL()
		);
		Assertions.assertEquals(
				"CREATE TABLE `db`.`t` (`k` INT AUTO_INCREMENT, `b` BIGINT DEFAULT ?, PRIMARY KEY (`k`));",
				Query.createTable("db", "t").intKey("k").bigint("b").defaultValue(123L).getSQL()
		);

		Assertions.assertEquals(
				"CREATE TABLE IF NOT EXISTS `t` (`k` INT AUTO_INCREMENT, PRIMARY KEY (`k`));",
				Query.createTable("t").intKey("k").ifNotExists().getSQL()
		);
	}

	@Test
	void testInsertInto() {
		Insert insert = Query.insertInto("t").value("a", 123).valueExpr("b", "1 + ?", 456).onDuplicateKeyUpdateExcept("a");

		Assertions.assertEquals(
				"INSERT INTO `t` (`a`, `b`) VALUES (?, 1 + ?) ON DUPLICATE KEY UPDATE `b` = 1 + ?;",
				insert.getSQL()
		);

		Assertions.assertEquals(Arrays.asList(123, 456, 456), insert.getParams());

		insert = Query.insertInto("db", "t").value("a", 123).valueExpr("b", "1 + ?", 456).onDuplicateKeyUpdateExcept("a");

		Assertions.assertEquals(
				"INSERT INTO `db`.`t` (`a`, `b`) VALUES (?, 1 + ?) ON DUPLICATE KEY UPDATE `b` = 1 + ?;",
				insert.getSQL()
		);
	}

	@Test
	void testUpdate() {
		Update update = Query.update("t").value("a", 123).valueExpr("b", "1 + ?", 456).where("c", "lol").whereExpr("`d` = ? * 5", 10);

		Assertions.assertEquals(
				"UPDATE `t` SET `a` = ?, `b` = 1 + ? WHERE (`c` = ?) AND (`d` = ? * 5) ;",
				update.getSQL()
		);

		Assertions.assertEquals(Arrays.asList(123, 456, "lol", 10), update.getParams());

		update = Query.update("db", "t").value("a", 123).valueExpr("b", "1 + ?", 456).where("c", "lol").whereExpr("`d` = ? * 5", 10);

		Assertions.assertEquals(
				"UPDATE `db`.`t` SET `a` = ?, `b` = 1 + ? WHERE (`c` = ?) AND (`d` = ? * 5) ;",
				update.getSQL()
		);
	}

	@Test
	void delete() {
		Assertions.assertEquals("DELETE FROM `t` ;", Query.deleteFrom("t").getSQL());

		Delete delete = Query.deleteFrom("t").where("c", "lol").whereExpr("`d` = ? * 5", 10);

		Assertions.assertEquals(
				"DELETE FROM `t` WHERE (`c` = ?) AND (`d` = ? * 5) ;",
				delete.getSQL()
		);

		Assertions.assertEquals(Arrays.asList("lol", 10), delete.getParams());

		Assertions.assertEquals("DELETE FROM `db`.`t` ;", Query.deleteFrom("db", "t").getSQL());
	}

	@Test
	void testSelect() {
		Select select = Query.select("a", "b").from("t").where("c", "lol").whereExpr("`d` = ? * 5", 10).limit(10);

		Assertions.assertEquals(
				"SELECT `a`, `b` FROM `t` WHERE (`c` = ?) AND (`d` = ? * 5) LIMIT 10;",
				select.getSQL()
		);

		Assertions.assertEquals(Arrays.asList("lol", 10), select.getParams());

		select = Query.select("a", "b").from("db", "t").where("c", "lol").whereExpr("`d` = ? * 5", 10).limit(10);

		Assertions.assertEquals(
				"SELECT `a`, `b` FROM `db`.`t` WHERE (`c` = ?) AND (`d` = ? * 5) LIMIT 10;",
				select.getSQL()
		);
	}

}