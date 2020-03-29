package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.AbstractQuery;
import me.saharnooby.lib.query.query.Expression;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.*;

/**
 * An INSERT query.
 * @author saharNooby
 * @since 21:28 14.11.2019
 */
@RequiredArgsConstructor
public final class Insert extends AbstractQuery {

	private final String database;
	private final String table;

	private boolean ignore;

	private final Map<String, Expression> insert = new LinkedHashMap<>();
	private final Map<String, Expression> update = new LinkedHashMap<>();

	/**
	 * Adds an IGNORE clause to the query.
	 * @return This.
	 */
	public Insert ignore() {
		this.ignore = true;
		return this;
	}

	/**
	 * Adds a value for the specified column.
	 * @param column Column name.
	 * @param value Column value.
	 * @return This.
	 */
	public Insert value(@NonNull String column, @NonNull Object value) {
		SQLUtil.validateIdentifier(column);
		return valueExpr(column, "?", value);
	}

	/**
	 * Adds an SQL expression value for the specified column.
	 * @param column Column name.
	 * @param expr SQL expression.
	 * @param params Expression parameters (filled in placeholders '?').
	 * @return This.
	 */
	public Insert valueExpr(@NonNull String column, @NonNull String expr, @NonNull Object... params) {
		SQLUtil.validateIdentifier(column);
		SQLUtil.validatePlaceholderCount(expr, params);
		this.insert.put(column, new Expression(expr, params));
		return this;
	}

	/**
	 * Adds a NULL value for the specified column.
	 * @param column Column name.
	 * @return This.
	 */
	public Insert valueNull(@NonNull String column) {
		return valueExpr(column, "NULL");
	}

	/**
	 * If specified value is null, adds a NULL value for the specified column, else adds specified value as value for the column.
	 * @param column Column name.
	 * @param value Nullable value.
	 * @return This.
	 */
	public Insert valueNullable(@NonNull String column, Object value) {
		return value == null ? valueNull(column) : value(column, value);
	}

	/**
	 * Adds 'ON DUPLICATE KEY UPDATE' clause with all values added using valueX() methods except those marked as keys.
	 * @param keys Key columns.
	 * @return This.
	 */
	public Insert onDuplicateKeyUpdateExcept(@NonNull String... keys) {
		this.update.clear();

		Set<String> keySet = new HashSet<>();
		Collections.addAll(keySet, keys);
		this.insert.forEach((k, v) -> {
			if (!keySet.contains(k)) {
				this.update.put(k, v);
			}
		});

		return this;
	}

	@Override
	public String getSQL() {
		if (this.insert.isEmpty()) {
			throw new IllegalStateException("No values specified");
		}

		if (this.ignore && !this.update.isEmpty()) {
			throw new IllegalStateException("Can't use INGORE with ON DUPLICATE KEY UPDATE");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("INSERT");

		if (this.ignore) {
			sb.append(" IGNORE");
		}

		sb.append(" INTO ");

		if (this.database != null) {
			sb.append("`").append(this.database).append("`.");
		}

		sb.append("`").append(this.table).append("` (");

		this.insert.forEach((k, v) -> sb.append("`").append(k).append("`, "));

		sb.setLength(sb.length() - 2);

		sb.append(") VALUES (");

		this.insert.forEach((k, v) -> sb.append(v.expr).append(", "));

		sb.setLength(sb.length() - 2);

		sb.append(")");

		if (!this.update.isEmpty()) {
			sb.append(" ON DUPLICATE KEY UPDATE ");

			this.update.forEach((k, v) -> sb.append("`").append(k).append("` = ").append(v.expr).append(", "));

			sb.setLength(sb.length() - 2);
		}

		sb.append(";");

		return sb.toString();
	}

	@Override
	public List<Object> getParams() {
		List<Object> params = new ArrayList<>();

		for (Expression expression : this.insert.values()) {
			Collections.addAll(params, expression.params);
		}

		for (Expression expression : this.update.values()) {
			Collections.addAll(params, expression.params);
		}

		return params;
	}

}
