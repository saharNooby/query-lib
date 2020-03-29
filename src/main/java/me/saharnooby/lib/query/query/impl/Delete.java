package me.saharnooby.lib.query.query.impl;

import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.ConditionalQuery;
import me.saharnooby.lib.query.query.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A DELETE query.
 * @author saharNooby
 * @since 21:17 14.11.2019
 */
@RequiredArgsConstructor
public final class Delete extends ConditionalQuery<Delete> {

	private final String database;
	private final String table;

	@Override
	public String getSQL() {
		StringBuilder sb = new StringBuilder();

		sb.append("DELETE FROM ");

		if (this.database != null) {
			sb.append("`").append(this.database).append("`.");
		}

		sb.append("`").append(this.table).append("` ");

		appendConditions(sb);

		sb.append(";");

		return sb.toString();
	}

	@Override
	public List<Object> getParams() {
		List<Object> params = new ArrayList<>();

		for (Expression condition : this.conditions) {
			Collections.addAll(params, condition.params);
		}

		return params;
	}

}
