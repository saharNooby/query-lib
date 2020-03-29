package me.saharnooby.lib.query.query;

import lombok.NonNull;
import me.saharnooby.lib.query.util.SQLUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an SQL query, which can have WHERE clause.
 * @author saharNooby
 * @since 17:05 15.11.2019
 */
public abstract class ConditionalQuery<T extends ConditionalQuery<T>> extends AbstractQuery {

	protected final List<Expression> conditions = new ArrayList<>();

	/**
	 * Adds a WHERE condition that checks that specified column has specified value.
	 * Conditions are chained with AND operation.
	 * @param column Column name.
	 * @param value Value.
	 * @return This.
	 */
	public final T where(@NonNull String column, @NonNull Object value) {
		SQLUtil.validateIdentifier(column);
		return whereExpr("`" + column + "` = ?", value);
	}

	/**
	 * Adds an SQL expression as a WHERE condition.
	 * Conditions are chained with AND operation.
	 * @param expr SQL expression.
	 * @param params Expression parameters (filled in placeholders '?').
	 * @return This.
	 */
	@SuppressWarnings("unchecked")
	public final T whereExpr(@NonNull String expr, @NonNull Object... params) {
		SQLUtil.validatePlaceholderCount(expr, params);
		this.conditions.add(new Expression(expr, params));
		return (T) this;
	}

	/**
	 * Adds a WHERE condition that checks that specified column has a NULL value.
	 * Conditions are chained with AND operation.
	 * @param column Column name.
	 * @return This.
	 */
	public final T whereNull(@NonNull String column) {
		return whereExpr("`" + column + "` IS NULL");
	}

	/**
	 * Adds a WHERE condition that checks that specified column has specified value, which can be NULL.
	 * Conditions are chained with AND operation.
	 * @param column Column name.
	 * @return This.
	 */
	public final T whereNullable(@NonNull String column, Object value) {
		return value == null ? whereNull(column) : where(column, value);
	}

	protected void appendConditions(@NonNull StringBuilder sb) {
		if (this.conditions.isEmpty()) {
			return;
		}

		sb.append("WHERE ");

		for (Expression condition : this.conditions) {
			sb.append("(").append(condition.expr).append(") AND ");
		}

		sb.setLength(sb.length() - 5);
		sb.append(" ");
	}

}
