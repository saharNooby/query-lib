package me.saharnooby.lib.query.set;

import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A wrapper allowing useful operations like mapping.
 * If closed, underlying result set will be closed.
 * @author saharNooby
 * @since 13:16 14.11.2019
 */
public final class ResultSetWrapper implements AutoCloseable {

	private final ResultSet set;

	/**
	 * Constructs a new wrapper.
	 * @param set Result set.
	 */
	public ResultSetWrapper(@NonNull ResultSet set) {
		this.set = set;
	}

	/**
	 * @return Underlying ResultSet.
	 */
	public ResultSet set() {
		return this.set;
	}

	/**
	 * Closes the underlying ResultSet.
	 * @throws SQLException On SQL error.
	 */
	@Override
	public void close() throws SQLException {
		this.set.close();
	}

	/**
	 * Maps all rows in the result set using specified mapper and collects the results to a list.
	 * Underlying result set will be closed after this method returns.
	 * @param mapper Mapper.
	 * @param <T> Mapped element type.
	 * @return List of mapped rows.
	 * @throws SQLException On SQL error.
	 */
	public <T> List<T> mapAll(@NonNull ResultSetMapper<T> mapper) throws SQLException {
		List<T> list = new ArrayList<>();

		try (ResultSet set = set()) {
			while (set.next()) {
				list.add(mapper.map(set));
			}
		}

		return list;
	}

	/**
	 * Maps the first row in the result set, if it exists, using specified mapper and returns an optional value.
	 * Underlying result set will be closed after this method returns.
	 * The optional will be empty if the set contains no rows or if mapper have return null.
	 * @param mapper Mapper.
	 * @param <T> Mapped element type.
	 * @return Optional value containing the mapped row.
	 * @throws SQLException On SQL error.
	 */
	public <T> Optional<T> map(@NonNull ResultSetMapper<T> mapper) throws SQLException {
		try (ResultSet set = set()) {
			return set.next() ? Optional.ofNullable(mapper.map(set)) : Optional.empty();
		}
	}

}
