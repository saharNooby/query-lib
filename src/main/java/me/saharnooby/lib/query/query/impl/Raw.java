package me.saharnooby.lib.query.query.impl;

import lombok.RequiredArgsConstructor;
import me.saharnooby.lib.query.query.AbstractQuery;

import java.util.List;

/**
 * A raw query, containing arbitrary SQL text.
 * @author saharNooby
 * @since 21:26 14.11.2019
 */
@RequiredArgsConstructor
public final class Raw extends AbstractQuery {

	private final String sql;
	private final List<Object> params;

	@Override
	public String getSQL() {
		return this.sql;
	}

	@Override
	public List<Object> getParams() {
		return this.params;
	}

}
