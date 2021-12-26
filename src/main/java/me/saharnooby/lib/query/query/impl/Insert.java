package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.AbstractQuery;
import me.saharnooby.lib.query.query.Expression;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.*;

/**
 * An <code>INSERT</code> query.
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
	 * Adds an <code>IGNORE</code> clause to the query.
	 * @return This object.
	 */
	public Insert ignore() {
		this.ignore = true;
		return this;
	}

	/**
	 * Adds a value to be inserted for the specified column.
	 * @param column Column name.
	 * @param value Column value.
	 * @return This object.
	 */
	public Insert value(@NonNull String column, @NonNull Object value) {
		SQLUtil.validateIdentifier(column);
		return valueExpr(column, "?", value);
	}

	/**
	 * Adds an SQL expression value to be inserted for the specified column.
	 * @param column Column name.
	 * @param expr SQL expression.
	 * @param params Expression parameters (for filling in placeholders <code>?</code>).
	 * @return This object.
	 */
	public Insert valueExpr(@NonNull String column, @NonNull String expr, @NonNull Object... params) {
		SQLUtil.validateIdentifier(column);
		SQLUtil.validatePlaceholderCount(expr, params);
		this.insert.put(column, new Expression(expr, params));
		return this;
	}

	/**
	 * Adds a <code>NULL</code> value to be inserted for the specified column.
	 * @param column Column name.
	 * @return This object.
	 */
	public Insert valueNull(@NonNull String column) {
		return valueExpr(column, "NULL");
	}

	/**
	 * If specified value is null, adds a <code>NULL</code> value for the specified column,
	 * othwerise adds specified value as a value for the column.
	 * @param column Column name.
	 * @param value Nullable value.
	 * @return This object.
	 */
	public Insert valueNullable(@NonNull String column, Object value) {
		return value == null ? valueNull(column) : value(column, value);
	}

	/**
	 * Adds <code>ON DUPLICATE KEY UPDATE</code> clause with all values added using
	 * <code>value*()</code> methods, exclusing colums marked as keys.
	 * @param keys Key columns that should not be updated.
	 * @return This object.
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
