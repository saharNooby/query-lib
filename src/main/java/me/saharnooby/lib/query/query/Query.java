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
	 * @return A <code>CREATE TABLE</code> query.
	 */
	public static CreateTable createTable(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new CreateTable(null, tableName);
	}

	/**
	 * @param tableName Name of the table to be updated.
	 * @return An <code>INSERT</code> query.
	 */
	public static Insert insertInto(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new Insert(null, tableName);
	}

	/**
	 * @param tableName Name of the table to be updated.
	 * @return An <code>UPDATE</code> query.
	 */
	public static Update update(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new Update(null, tableName);
	}

	/**
	 * @param tableName Name of the table to be updated.
	 * @return A <code>DELETE</code> query.
	 */
	public static Delete deleteFrom(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		return new Delete(null, tableName);
	}

	/**
	 * @param columns Names of the columns to select. If empty, caller then must
	 *                specify selected columns or expressions itself.
	 * @return A <code>SELECT</code> query.
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
	 * @return A <code>CREATE TABLE</code> query.
	 */
	public static CreateTable createTable(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new CreateTable(database, tableName);
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be updated.
	 * @return An <code>INSERT</code> query.
	 */
	public static Insert insertInto(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new Insert(database, tableName);
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be updated.
	 * @return An <code>UPDATE</code> query.
	 */
	public static Update update(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new Update(database, tableName);
	}

	/**
	 * @param database Name of the database containing the table.
	 * @param tableName Name of the table to be updated.
	 * @return A <code>DELETE</code> query.
	 */
	public static Delete deleteFrom(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(database);
		SQLUtil.validateIdentifier(tableName);
		return new Delete(database, tableName);
	}

	/**
	 * Creates a raw query from arbitrary SQL text.
	 * @param sql SQL query.
	 * @param params Parameters (for filling in <code>?</code> placeholders).
	 * @return A query.
	 */
	public static AbstractQuery of(@NonNull String sql, @NonNull Object... params) {
		SQLUtil.validatePlaceholderCount(sql, params);

		return new Raw(sql, Arrays.asList(params));
	}

}
