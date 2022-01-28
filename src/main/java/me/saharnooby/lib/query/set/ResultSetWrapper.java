package me.saharnooby.lib.query.set;

import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link ResultSet} wrapper allowing useful operations like mapping.
 * If closed, underlying result set will be closed.
 * @author saharNooby
 * @since 13:16 14.11.2019
 */
public final class ResultSetWrapper implements AutoCloseable {

	private final ResultSet set;
	private final PreparedStatement parentStatement;

	/**
	 * Constructs a new wrapper.
     * @param set A result set.
	 */
	public ResultSetWrapper(@NonNull ResultSet set) {
		this(set, null);
	}

	/**
	 * Constructs a new wrapper. Parent statement, if specified, will be closed after this set is closed.
	 * @param set A result set.
	 */
	public ResultSetWrapper(@NonNull ResultSet set, PreparedStatement parentStatement) {
		this.set = set;
		this.parentStatement = parentStatement;
	}

	/**
	 * @return Underlying {@link ResultSet}.
	 */
	public ResultSet set() {
		return this.set;
	}

	/**
	 * Closes the underlying {@link ResultSet}.
	 * @throws SQLException On SQL error.
	 */
	@Override
	public void close() throws SQLException {
		try {
			this.set.close();
		} finally {
			if (this.parentStatement != null) {
				this.parentStatement.close();
			}
		}
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
	 * The optional will be empty if the set contains no rows or if the mapper returned null.
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
