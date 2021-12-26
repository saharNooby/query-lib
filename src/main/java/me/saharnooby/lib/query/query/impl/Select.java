package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.ConditionalQuery;
import me.saharnooby.lib.query.query.Expression;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>SELECT</code> query.
 * @author saharNooby
 * @since 21:14 14.11.2019
 */
@RequiredArgsConstructor
public final class Select extends ConditionalQuery<Select> {

	private final List<Expression> expressions = new ArrayList<>();
	private boolean all;

	private String database;
	private String table;

	private String orderBy;
	private Object[] orderByParams;
	private boolean desc;

	private Long limit;
	private Long offset;

	private boolean forUpdate;

	/**
	 * Adds <code>*</code> to the selected expression list. No other columns can be added.
	 * @return This object.
	 */
	public Select all() {
		if (!this.expressions.isEmpty()) {
			throw new IllegalStateException("Some columns were added");
		}

		this.all = true;

		return this;
	}

	/**
	 * Adds a column to the selected expression list.
	 * @param name Column name.
	 * @return This object.
	 */
	public Select col(@NonNull String name) {
		SQLUtil.validateIdentifier(name);
		return expr("`" + name + "`");
	}

	/**
	 * Adds an SQL expression to the selected expression list.
	 * @param expr SQL expression.
	 * @param params Expression parameters (for filling in <code>?</code> placeholders).
	 * @return This object.
	 */
	public Select expr(@NonNull String expr, @NonNull Object... params) {
		SQLUtil.validatePlaceholderCount(expr, params);

		if (this.all) {
			throw new IllegalStateException("Can't add expressions to SELECT when selecting all columns");
		}

		this.expressions.add(new Expression(expr, params));

		return this;
	}

	/**
	 * Sets the name of the table to select from.
	 * @param tableName Name of the table.
	 * @return This object.
	 */
	public Select from(@NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		this.table = tableName;
		return this;
	}

	/**
	 * Sets names of the database and the table to select from.
	 * @param database Name of the database.
	 * @param tableName Name of the table.
	 * @return This object.
	 */
	public Select from(@NonNull String database, @NonNull String tableName) {
		SQLUtil.validateIdentifier(tableName);
		this.database = database;
		this.table = tableName;
		return this;
	}

	/**
	 * Adds <code>ORDER BY column</code> clause to the query.
	 * @param column Column name.
	 * @return This object.
	 */
	public Select orderBy(@NonNull String column) {
		SQLUtil.validateIdentifier(column);
		return orderByExpr("`" + column + "`");
	}

	/**
	 * Adds <code>ORDER BY expression</code> clause to the query.
	 * @param expr SQL expression.
	 * @param params Expression parameters (for filling in <code>?</code> placeholders).
	 * @return This object.
	 */
	public Select orderByExpr(@NonNull String expr, @NonNull Object... params) {
		SQLUtil.validatePlaceholderCount(expr, params);
		this.orderBy = expr;
		this.orderByParams = params;
		return this;
	}

	/**
	 * Adds <code>DESC</code> clause to the <code>ORDER BY</code> clause.
	 * @return This object.
	 */
	public Select desc() {
		if (this.orderBy == null) {
			throw new IllegalStateException("Specify order expression first");
		}

		this.desc = true;

		return this;
	}

	/**
	 * Adds <code>LIMIT limit</code> clause to the query.
	 * @param limit Limit, must be positive.
	 * @return This object.
	 */
	public Select limit(long limit) {
		if (limit < 1) {
			throw new IllegalArgumentException("" + limit);
		}

		this.limit = limit;

		return this;
	}

	/**
	 * Adds <code>OFFSET offset</code> clause to the query.
	 * @param offset Offset, must be non-negative.
	 * @return This object.
	 */
	public Select offset(long offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("" + offset);
		}

		this.offset = offset;

		return this;
	}

	/**
	 * Adds <code>FOR UPDATE</code> clause to the end of the query.
	 * @return This object.
	 */
	public Select forUpdate() {
		this.forUpdate = true;

		return this;
	}

	@Override
	public String getSQL() {
		if (!this.all && this.expressions.isEmpty()) {
			throw new IllegalStateException("Selected expression list is empty");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");

		if (this.all) {
			sb.append("* ");
		} else {
			for (Expression expression : this.expressions) {
				sb.append(expression.expr).append(", ");
			}

			sb.setLength(sb.length() - 2);
			sb.append(" ");
		}

		if (this.table != null) {
			sb.append("FROM ");

			if (this.database != null) {
				sb.append("`").append(this.database).append("`.");
			}

			sb.append("`").append(this.table).append("` ");
		}

		appendConditions(sb);

		if (this.orderBy != null) {
			sb.append("ORDER BY (").append(this.orderBy).append(") ").append(this.desc ? "DESC" : "ASC").append(' ');
		}

		if (this.limit != null) {
			sb.append("LIMIT ").append(this.limit).append(' ');
		}

		if (this.offset != null) {
			sb.append("OFFSET ").append(this.offset).append(' ');
		}

		if (this.forUpdate) {
			sb.append("FOR UPDATE");
		}

		sb.append(";");

		return sb.toString();
	}

	@Override
	public List<Object> getParams() {
		List<Object> params = new ArrayList<>();

		for (Expression expression : this.expressions) {
			Collections.addAll(params, expression.params);
		}

		for (Expression condition : this.conditions) {
			Collections.addAll(params, condition.params);
		}

		if (this.orderByParams != null) {
			Collections.addAll(params, this.orderByParams);
		}

		return params;
	}

}
