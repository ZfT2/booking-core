package de.zft2.core.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.zft2.core.dto.Booking.Typ;
import de.zft2.core.testsupport.TestBooking;

class BookingTest {

	@Test
	void forStringMapsTranslationsAndUnknownValues() {
		assertEquals(Typ.REBOOKING_IN, Typ.forString("Umbuchung (Eingang)"));
		assertEquals(Typ.UNKNOWN, Typ.forString(null));
		assertNull(Typ.forString("not configured"));
	}

	@Test
	void compareBookingByAccountThenDateSortsByCrossAccountBaseAccountAndDate() {
		TestBooking first = booking("Broker", "Checking", LocalDate.of(2026, 5, 20));
		TestBooking second = booking("Savings", "Checking", LocalDate.of(2026, 5, 19));
		TestBooking third = booking("Savings", "Checking", LocalDate.of(2026, 5, 21));
		TestBooking fourth = booking("Savings", "Depot", LocalDate.of(2026, 5, 18));

		List<TestBooking> bookings = new ArrayList<>(List.of(third, fourth, second, first));

		bookings.sort(Booking::compareBookingByAccountThenDate);

		assertEquals(List.of(first, second, third, fourth), bookings);
	}

	private static TestBooking booking(String crossAccountName, String accountName, LocalDate date) {
		return new TestBooking()
				.withCrossAccountName(crossAccountName)
				.withAccountName(accountName)
				.withDate(date);
	}
}
