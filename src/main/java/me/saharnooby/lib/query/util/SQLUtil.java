package me.saharnooby.lib.query.util;

import lombok.NonNull;

/**
 * Internal class, do not use directly.
 * @author saharNooby
 * @since 12:40 16.11.2019
 */
public final class SQLUtil {

	/**
	 * Validates that specified string is not empty and does not contain <code>`</code> characters.
	 * @param s String to be validated.
	 */
	public static void validateIdentifier(@NonNull String s) {
		if (s.isEmpty()) {
			throw new IllegalArgumentException("An empty string can't be an identifier");
		}

		if (s.contains("`")) {
			throw new IllegalArgumentException("Invalid identifier \"" + s + "\"");
		}
	}

	/**
	 * Validates that count of <code>?</code> characters in the string is equal to the value array length.
	 * @param expr String to be validated.
	 * @param values Value array.
	 */
	public static void validatePlaceholderCount(@NonNull String expr, @NonNull Object[] values) {
		int count = placeholderCount(expr);

		if (count != values.length) {
			throw new IllegalArgumentException("Expected " + values.length + " placeholders, got " + count + " in expression \"" + expr + "\"");
		}
	}

	private static int placeholderCount(@NonNull String expr) {
		int count = 0;

		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == '?') {
				count++;
			}
		}

		return count;
	}

}
