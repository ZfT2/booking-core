package de.zft2.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class CoreBookingUtil {

	private static DateTimeFormatter dateFormatterShort = DateTimeFormatter.ofPattern("dd.MM.uu");
	private static DateTimeFormatter dateFormatterLong = DateTimeFormatter.ofPattern("dd.MM.uuuu");

	protected LocalDate asLocalDate(String dateStr) {
		if (dateStr != null) {
			return LocalDate.parse(dateStr, (dateStr.length() == 8 ? dateFormatterShort : dateFormatterLong));
		} else {
			return null;
		}
	}

	protected String fromLocalDate(LocalDate date) {
		if (date != null)
			return date.format(dateFormatterShort);
		return null;
	}
}
