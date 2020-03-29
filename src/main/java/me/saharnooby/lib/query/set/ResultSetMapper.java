package me.saharnooby.lib.query.set;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a ResultSet to a value.
 * @author saharNooby
 * @since 13:19 14.11.2019
 */
@FunctionalInterface
public interface ResultSetMapper<T> {

	/**
	 * Maps a ResultSet to a value.
	 * @param set Result set.
	 * @return Value, may br null.
	 * @throws SQLException On SQL error.
	 */
	T map(ResultSet set) throws SQLException;

}
