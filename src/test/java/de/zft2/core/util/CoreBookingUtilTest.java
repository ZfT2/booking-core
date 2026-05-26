package de.zft2.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class CoreBookingUtilTest {

	private final TestUtil util = new TestUtil();

	@Test
	void asLocalDateParsesShortAndLongGermanDates() {
		LocalDate expected = LocalDate.of(2026, 5, 22);

		assertEquals(expected, util.parse("22.05.26"));
		assertEquals(expected, util.parse("22.05.2026"));
		assertNull(util.parse(null));
	}

	@Test
	void fromLocalDateFormatsShortGermanDate() {
		assertEquals("22.05.26", util.format(LocalDate.of(2026, 5, 22)));
		assertNull(util.format(null));
	}

	private static class TestUtil extends CoreBookingUtil {

		LocalDate parse(String dateStr) {
			return asLocalDate(dateStr);
		}

		String format(LocalDate date) {
			return fromLocalDate(date);
		}
	}
}
