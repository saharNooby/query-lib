package me.saharnooby.lib.query.batch;

import lombok.NonNull;
import me.saharnooby.lib.query.query.AbstractQuery;
import me.saharnooby.lib.query.query.impl.Raw;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows executing a query with multiple parameter lists in a single batch using {@link PreparedStatement#executeBatch()} method.
 * @author saharNooby
 * @since 19:34 28.03.2020
 */
public final class BatchBuilder {

	private final List<AbstractQuery> queries = new ArrayList<>();

	/**
	 * Adds a query to the batch. If this builder is not empty, the SQL text of the query must be exactly equal to the first added query.
	 * @param query Query to be added.
	 * @return This object.
	 */
	public BatchBuilder add(@NonNull AbstractQuery query) {
		AbstractQuery raw = new Raw(query.getSQL(), query.getParams());

		if (!this.queries.isEmpty()) {
			String first = this.queries.get(0).getSQL();

			if (!raw.getSQL().equals(first)) {
				throw new IllegalArgumentException("Can't add a query '" + raw.getSQL() + "' to a batch. Expected the query to be '" + first + "'");
			}
		}

		this.queries.add(raw);

		return this;
	}

	/**
	 * Executes all added queries as a batch.
	 * @param con Connection.
	 * @return An array of update counts for each executed statement.
	 * @throws SQLException On SQL error.
	 */
	public int[] execute(@NonNull Connection con) throws SQLException {
		if (this.queries.isEmpty()) {
			return new int[0];
		}

		try (PreparedStatement s = con.prepareStatement(this.queries.get(0).getSQL())) {
			for (AbstractQuery query : this.queries) {
				List<Object> params = query.getParams();

				for (int i = 0; i < params.size(); i++) {
					s.setObject(i + 1, params.get(i));
				}

				s.addBatch();
			}

			return s.executeBatch();
		}
	}

	/**
	 * Obtains a connection from the source, executes all added queries as a batch and closes the connection.
	 * @param source Data source.
	 * @return An array of update counts for each executed statement.
	 * @throws SQLException On SQL error.
	 */
	public int[] execute(@NonNull DataSource source) throws SQLException {
		try (Connection con = source.getConnection()) {
			return execute(con);
		}
	}

}
