package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.ConditionalQuery;
import me.saharnooby.lib.query.query.Expression;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.*;

/**
 * An UPDATE query.
 * @author saharNooby
 * @since 17:03 15.11.2019
 */
@RequiredArgsConstructor
public final class Update extends ConditionalQuery<Update> {

	private final String database;
	private final String table;

	private final Map<String, Expression> expressions = new LinkedHashMap<>();

	/**
	 * Adds a value for the specified column.
	 * @param column Column name.
	 * @param value Value.
	 * @return This.
	 */
	public Update value(@NonNull String column, @NonNull Object value) {
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
	public Update valueExpr(@NonNull String column, @NonNull String expr, @NonNull Object... params) {
		SQLUtil.validateIdentifier(column);
		SQLUtil.validatePlaceholderCount(expr, params);
		this.expressions.put(column, new Expression(expr, params));

		return this;
	}

	/**
	 * Adds a NULL value for the specified column.
	 * @param column Column name.
	 * @return This.
	 */
	public Update valueNull(@NonNull String column) {
		return valueExpr(column, "NULL");
	}

	/**
	 * If specified value is null, adds a NULL value for the specified column, else adds specified value as value for the column.
	 * @param column Column name.
	 * @param value Nullable value.
	 * @return This.
	 */
	public Update valueNullable(@NonNull String column, Object value) {
		return value == null ? valueNull(column) : value(column, value);
	}

	@Override
	public String getSQL() {
		if (this.expressions.isEmpty()) {
			throw new IllegalStateException("No values specified");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE ");

		if (this.database != null) {
			sb.append("`").append(this.database).append("`.");
		}

		sb.append("`").append(this.table).append("` SET ");

		this.expressions.forEach((k, v) -> sb.append("`").append(k).append("` = ").append(v.expr).append(", "));

		sb.setLength(sb.length() - 2);
		sb.append(" ");

		appendConditions(sb);

		sb.append(";");

		return sb.toString();
	}

	@Override
	public List<Object> getParams() {
		List<Object> params = new ArrayList<>();

		for (Expression expression : this.expressions.values()) {
			Collections.addAll(params, expression.params);
		}

		for (Expression condition : this.conditions) {
			Collections.addAll(params, condition.params);
		}

		return params;
	}

}
