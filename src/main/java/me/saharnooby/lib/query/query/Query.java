package me.saharnooby.lib.query.query;

import lombok.NonNull;
import me.saharnooby.lib.query.query.impl.*;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.Arrays;

/**
 * Utility class for creating query objects.
 * @author saharNooby
 * @since 18:11 14.11.2019
 */
public final class Query {

	/**
	 * @param tableName Name of the table to be created.
	 * @return A 'CREATE TABLE' query.
	 */
	public static CreateTable createTable(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new CreateTable(null, tableName);
	}

	/**
	 * @param tableName Name of the table to be updated.
	 * @return An INSERT query.
	 */
	public static Insert insertInto(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new Insert(null, tableName);
	}

	/**
	 * @param tableName Name of the table to be updated.
	 * @return An UPDATE query.
	 */
	public static Update update(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new Update(null, tableName);
	}

	/**
	 * @param tableName Name of the table to be updated.
	 * @return A DELETE query.
	 */
	public static Delete deleteFrom(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new Delete(null, tableName);
	}

	/**
	 * @param columns Names of the columns to select, may be empty.
	 * @return A SELECT query.
	 */
	public static Select select(@NonNull String... columns) {
		Select select = new Select();

		for (String column : columns) {
			select.col(column);
		}

		return select;
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be created.
	 * @return A 'CREATE TABLE' query.
	 */
	public static CreateTable createTable(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new CreateTable(database, tableName);
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be updated.
	 * @return An INSERT query.
	 */
	public static Insert insertInto(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new Insert(database, tableName);
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be updated.
	 * @return An UPDATE query.
	 */
	public static Update update(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new Update(database, tableName);
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be updated.
	 * @return A DELETE query.
	 */
	public static Delete deleteFrom(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new Delete(database, tableName);
	}

	/**
	 * Creates a raw query from arbitrary SQL text.
	 * @param sql SQL query.
	 * @param params Parameters (filled in '?' placeholders).
	 * @return A query.
	 */
	public static AbstractQuery of(@NonNull String sql, @NonNull Object... params) {
		SQLUtil.validatePlaceholderCount(sql, params);

		return new Raw(sql, Arrays.asList(params));
	}

}
