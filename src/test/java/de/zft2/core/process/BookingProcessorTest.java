package de.zft2.core.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.zft2.core.dto.Account;
import de.zft2.core.dto.Booking;
import de.zft2.core.dto.Booking.Typ;
import de.zft2.core.exception.ConfigurationException;
import de.zft2.core.testsupport.TestAccount;
import de.zft2.core.testsupport.TestBooking;

class BookingProcessorTest {

	private static final String CHECKING_IBAN = "DE11111111111111111111";
	private static final String SAVINGS_IBAN = "DE22222222222222222222";

	private TestBookingProcessor processor;

	@BeforeEach
	void setUp() throws ConfigurationException {
		AccountProcessor.propsTransfer = null;
		AccountProcessor.propsAccount = null;
		AccountProcessor.propsSkip = null;
		AccountProcessor.accountNumbersMap.clear();
		processor = new TestBookingProcessor();
	}

	@Test
	void addBookingTypesClassifiesRebookingsInterestTaxAndUnknownAmounts() {
		TestBooking rebooking = booking("-50.00", "Transfer to savings")
				.withAccountName("Checking")
				.withCrossAccountIBAN(SAVINGS_IBAN);
		TestBooking interest = booking("4.25", "ZINSEN credited").withAccountName("Checking");
		TestBooking tax = booking("-1.10", "KAPST charged").withAccountName("Checking");
		TestBooking unknownAmount = booking(null, "missing amount").withAccountName("Checking");
		TestBooking depositOrRemoval = booking("100.00", "Salary").withAccountName("Checking");
		TestAccount account = new TestAccount("Checking", "Checking")
				.withIban(CHECKING_IBAN)
				.withBookings(rebooking, interest, tax, unknownAmount, depositOrRemoval);

		processor.addBookingTypes(account.getBookings(), account);

		assertEquals(Typ.REBOOKING_OUT, rebooking.getTyp());
		assertEquals("Savings", rebooking.getCrossAccountName());
		assertEquals(Typ.INTEREST, interest.getTyp());
		assertEquals(Typ.TAX, tax.getTyp());
		assertEquals(Typ.UNKNOWN, unknownAmount.getTyp());
		assertNull(depositOrRemoval.getTyp());
	}

	@Test
	void getAccountBalanceSumsAllBookingAmounts() {
		TestAccount account = new TestAccount("Checking", "Checking")
				.withBookings(
						booking("10.50", "first"),
						booking("-3.50", "second"),
						booking("1.00", "third"));

		assertEquals("8.00", processor.getAccountBalance(account));
	}

	@Test
	void countAccountBookingsCountsTypedAndUnclassifiedBookings() {
		TestBooking rebooking = booking("10.00", "rebooking").withTyp(Typ.REBOOKING_IN);
		TestBooking unknown = booking("3.00", "unknown").withTyp(Typ.UNKNOWN);
		TestBooking unclassified = booking("4.00", "unclassified");
		TestBooking transferBooking = booking("5.00", "transfer")
				.withTyp(Typ.REBOOKING_OUT)
				.withAccountName("--TRANSFER--");
		TestAccount account = new TestAccount("Checking", "Checking")
				.withBookings(rebooking, unknown, unclassified, transferBooking);

		assertEquals(1, processor.countAccountBookings(account, Typ.REBOOKING_IN));
		assertEquals(2, processor.countAccountBookings(account, null));
	}

	@Test
	void findMissingRebookingsReturnsOnlyUnpairedRebookings() {
		LocalDate date = LocalDate.of(2026, 5, 20);
		TestBooking pairedOut = booking("-100.00", "paired out")
				.withDate(date)
				.withTyp(Typ.REBOOKING_OUT)
				.withAccountName("Checking")
				.withCrossAccountName("Savings");
		TestBooking pairedIn = booking("100.00", "paired in")
				.withDate(date)
				.withTyp(Typ.REBOOKING_IN)
				.withAccountName("Savings")
				.withCrossAccountName("Checking");
		TestBooking missing = booking("-25.00", "missing")
				.withDate(date)
				.withTyp(Typ.REBOOKING_OUT)
				.withAccountName("Checking")
				.withCrossAccountName("Broker");

		List<Booking> result = processor.findMissingRebookings(List.of(pairedOut, pairedIn, missing));

		assertEquals(List.of(missing), result);
	}

	@Test
	void generateCrossBookingsLinksSameDayRebookingsWithoutTransferAccount() {
		LocalDate date = LocalDate.of(2026, 5, 20);
		TestBooking checkingOut = booking("-100.00", "paired out")
				.withDate(date)
				.withTyp(Typ.REBOOKING_OUT)
				.withAccountName("Checking")
				.withCrossAccountName("Savings");
		TestBooking savingsIn = booking("100.00", "paired in")
				.withDate(date)
				.withTyp(Typ.REBOOKING_IN)
				.withAccountName("Savings")
				.withCrossAccountName("Checking");
		TestAccount checking = new TestAccount("Checking", "Checking").withBookings(checkingOut);
		TestAccount savings = new TestAccount("Savings", "Savings").withBookings(savingsIn);

		processor.generateCrossBookings(List.of(checking, savings), 3, null);

		assertSame(savingsIn, checkingOut.getCrossBooking());
	}

	@Test
	void revertCancellationRebookingsAppliesDefaultCancelPatternsToAllAccounts() {
		LocalDate date = LocalDate.of(2026, 5, 20);
		TestBooking original = booking("-12.00", "Invoice 42")
				.withDate(date);
		TestBooking cancellation = booking("12.00", "Cancel Invoice 42")
				.withDate(date.plusDays(1));
		TestAccount account = new TestAccount("Other Account", "Other Account")
				.withBookings(original, cancellation);

		processor.revertCancellationRebookings(List.of(account));

		assertEquals(Typ.CANCEL, original.getTyp());
		assertEquals(Typ.CANCEL, cancellation.getTyp());
	}

	private static TestBooking booking(String amount, String purpose) {
		return new TestBooking()
				.withDate(LocalDate.of(2026, 5, 20))
				.withAmount(amount)
				.withPurpose(purpose);
	}

	private static class TestBookingProcessor extends BookingProcessor<TestBooking, TestAccount> {

		TestBookingProcessor() throws ConfigurationException {
			super();
		}

		@Override
		protected void generateAndLinkCrossBookingsOnTransferAccount(Account<TestBooking> accountTansfer,
				Booking booking, Booking crossBookingToTransfer) {
			booking.setCrossBooking(crossBookingToTransfer);
			crossBookingToTransfer.setCrossBooking(booking);
		}
	}
}
