package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.AbstractQuery;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>CREATE TABLE</code> query.
 * @author saharNooby
 * @since 18:11 14.11.2019
 */
@RequiredArgsConstructor
public final class CreateTable extends AbstractQuery {

	@RequiredArgsConstructor
	private static final class Column {

		final String name;
		final String type;

		boolean notNull;
		boolean autoIncrement;
		boolean primaryKey;
		Object defaultValue;

	}

	private final String database;
	private final String table;

	private boolean ifNotExists;

	private final List<Column> columns = new ArrayList<>();

	/**
	 * Adds <code>IF NOT EXISTS</code> clause to the query.
	 * @return This object.
	 */
	public CreateTable ifNotExists() {
		this.ifNotExists = true;
		return this;
	}

	/**
	 * Adds a column to the table.
	 * @param name Column name.
	 * @param type Column type.
	 * @return This object.
	 */
	public CreateTable col(@NonNull String name, @NonNull String type) {
		SQLUtil.validateIdentifier(name);

		if (this.columns.stream().anyMatch(c -> c.name.equals(name))) {
			throw new IllegalArgumentException("Column " + name + " already exists");
		}

		this.columns.add(new Column(name, type));

		return this;
	}

	/**
	 * Adds an <code>INT</code> column.
	 * @param name Column name.
	 * @return This
	 */
	public CreateTable integer(@NonNull String name) {
		return col(name, "INT");
	}

	/**
	 * Adds a <code>BIGINT</code> column.
	 * @param name Column name.
	 * @return This
	 */
	public CreateTable bigint(@NonNull String name) {
		return col(name, "BIGINT");
	}

	/**
	 * Adds a <code>TINYINT(1)</code> column.
	 * @param name Column name.
	 * @return This
	 */
	public CreateTable bool(@NonNull String name) {
		return col(name, "TINYINT(1)");
	}

	/**
	 * Adds a <code>VARCHAR(size)</code> column.
	 * @param name Column name.
	 * @param size Max characters.
	 * @return This
	 */
	public CreateTable varchar(@NonNull String name, int size) {
		return col(name, "VARCHAR(" + size + ")");
	}

	/**
	 * Adds a <code>CHAR(size)</code> column.
	 * @param name Column name.
	 * @param size Max characters.
	 * @return This
	 */
	public CreateTable character(@NonNull String name, int size) {
		return col(name, "CHAR(" + size + ")");
	}

	/**
	 * Adds a <code>TEXT</code> column.
	 * @param name Column name.
	 * @return This
	 */
	public CreateTable text(@NonNull String name) {
		return col(name, "TEXT");
	}

	/**
	 * Sets <code>NOT NULL</code> for the last added column.
	 * @return This object.
	 */
	public CreateTable NN() {
		lastColumn().notNull = true;
		return this;
	}

	/**
	 * Adds <code>AUTO_INCREMENT</code> clause for the last added column.
	 * @return This object.
	 */
	public CreateTable AI() {
		lastColumn().autoIncrement = true;
		return this;
	}

	/**
	 * Adds <code>PRIMARY KEY</code> clause for the last added column.
	 * @return This object.
	 */
	public CreateTable PK() {
		lastColumn().primaryKey = true;
		return this;
	}

	/**
	 * Sets default value for the last added column.
	 * @param value Value to set, must not be null.
	 * @return This object.
	 */
	public CreateTable defaultValue(@NonNull Object value) {
		lastColumn().defaultValue = value;
		return this;
	}

	/**
	 * Adds an <code>INT AUTO_INCREMENT PRIMARY KEY</code> column.
	 * @param name Column name.
	 * @return This object.
	 */
	public CreateTable intKey(@NonNull String name) {
		return integer(name).AI().PK();
	}

	private Column lastColumn() {
		if (this.columns.isEmpty()) {
			throw new IllegalStateException("No columns added");
		}

		return this.columns.get(this.columns.size() - 1);
	}

	@Override
	public String getSQL() {
		StringBuilder sb = new StringBuilder();

		sb.append("CREATE TABLE ");

		if (this.ifNotExists) {
			sb.append("IF NOT EXISTS ");
		}

		if (this.database != null) {
			sb.append("`").append(this.database).append("`.");
		}

		sb.append("`").append(this.table).append("` (");

		List<String> primaryKeys = new ArrayList<>();

		for (Column col : this.columns) {
			sb.append("`").append(col.name).append("` ").append(col.type);

			if (col.notNull) {
				sb.append(" NOT NULL");
			}

			if (col.autoIncrement) {
				sb.append(" AUTO_INCREMENT");
			}

			if (col.defaultValue != null) {
				sb.append(" DEFAULT ?");
			}

			sb.append(", ");

			if (col.primaryKey) {
				primaryKeys.add(col.name);
			}
		}

		if (!primaryKeys.isEmpty()) {
			sb.append("PRIMARY KEY (");

			for (String col : primaryKeys) {
				sb.append("`").append(col).append("`, ");
			}

			sb.setLength(sb.length() - 2);

			sb.append(")");
		} else if (!this.columns.isEmpty()) {
			sb.setLength(sb.length() - 2);
		}

		sb.append(");");

		return sb.toString();
	}

	@Override
	public List<Object> getParams() {
		List<Object> list = new ArrayList<>();

		for (Column col : this.columns) {
			if (col.defaultValue != null) {
				list.add(col.defaultValue);
			}
		}

		return list;
	}

}
