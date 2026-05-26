package de.zft2.core.process;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.zft2.core.config.ImportProperties;
import de.zft2.core.dto.Account;
import de.zft2.core.dto.Booking;
import de.zft2.core.dto.Booking.Typ;
import de.zft2.core.exception.AccountException;
import de.zft2.core.exception.ConfigurationException;

public abstract class BookingProcessor<B extends Booking, A extends Account<B>> extends AccountProcessor<A> {

	private static Logger log = LogManager.getLogger(BookingProcessor.class);

	private static final String NAME_ACCOUNT_TRANSFER = "--TRANSFER--";

	private static ImportProperties propsBookingTypes;
	private static ImportProperties propsCancel;

	protected BookingProcessor() throws ConfigurationException {
		super();
		init();
	}

	private static void init() throws ConfigurationException {
		if (propsBookingTypes == null && propsCancel == null) {
			propsBookingTypes = ImportProperties.getInstance("bookings.properties", false);
			propsCancel = ImportProperties.getInstance("cancel.properties", true);
		}
	}

	public void addBookingTypesToAccountBookings(Collection<A> accountList) {
		for (Account<B> account : accountList) {
			addBookingTypes(account.getBookings(), account);
		}
	}
	
	public void addBookingTypes(Collection<B> records, Account<B> account) {

		for (Booking booking : records) {
			if (booking.getCrossAccountIBAN() != null) {
				booking.setCrossAccountNamePP(findAccountNamePP(booking.getCrossAccountIBAN()));
				if (booking.getCrossAccountNamePP() == null && booking.getCrossAccountIBAN().length() >= 15) {
					booking.setCrossAccountNamePP(
							findAccountNamePP(booking.getCrossAccountIBAN().substring(12).replaceFirst("^0+(?!$)", "")));
				}
			}
			determineBookingTyp(booking, account);
		}
	}

	private void determineBookingTyp(Booking booking, Account<B> account) {
		if (booking.getAmount() == null) {
			booking.setTyp(Typ.UNKNOWN);
		} else if (isNotEmpty(booking.getCrossAccountNamePP()) && !isCrossBookingOnSameAccount(account, booking.getCrossAccountIBAN())) {
			booking.setTyp(booking.getAmount().compareTo(BigDecimal.ZERO) <= 0 ? Typ.REBOOKING_OUT : Typ.REBOOKING_IN);
		} else if (matchesBookingType(booking.getPurpose(), propsBookingTypes.getProp("INTEREST").split(";"), false)
				|| matchesBookingType(booking.getPurpose(), propsBookingTypes.getProp("INTEREST_WHOLE_WORD").split(";"), true)) {
			booking.setTyp(booking.getAmount().compareTo(BigDecimal.ZERO) <= 0 ? Typ.INTEREST_CHARGE : Typ.INTEREST);
		} else if (matchesBookingType(booking.getPurpose(), propsBookingTypes.getProp("TAX").split(";"), false)) {
			booking.setTyp(booking.getAmount().compareTo(BigDecimal.ZERO) <= 0 ? Typ.TAX : Typ.TAX_REFUND);
/**		} else if (matchesBookingType(booking.getPurpose(), propsBookingTypes.getProp("DIVIDENDS").split(";"), false)) {
			booking.setTyp(Typ.DIVIDENDS); **/
		} else {
			/* booking.setTyp(Typ.UNKNOWN); */
			booking.setTyp(null); /* must be Deposit or Removal */
		}
	}

	private boolean isNotEmpty(String value) {
		return (value != null && !"".equalsIgnoreCase(value));
	}

	
	private boolean matchesBookingType(String verwendungszweck, String[] musterList, boolean wholeWord) {
		for (String muster : musterList) {
			if (!wholeWord && verwendungszweck.contains(muster) || (wholeWord && verwendungszweck.trim().equals(muster))) {
				return true;
			}
		}
		return false;
	}
	
	public Collection<A> generateCrossBookings(Collection<A> accountList, int daysRebooking, A accountTansfer) {
		
		if (setupTransferProperties(accountList)) {
			for (Account<B> account : accountList) {
				if (account.getNamePP() == null) {
					account.setNamePP(account.getAccountName());
				}
				addBookingTypes(account.getBookings(), account);
			}
		}

		if (accountTansfer != null) {
			accountTansfer.setIban("DE00000000");
			accountTansfer.setBic("BIC00");
			accountTansfer.setNumber("0000");
			accountTansfer.setNamePP(NAME_ACCOUNT_TRANSFER);
			accountTansfer.setBookings(new ArrayList<>());
		}

		for (A account : accountList) {

			if (account.getNamePP() == null) {
				continue;
			}

			Collection<A> kontenListToCompare = new ArrayList<>(accountList);
			kontenListToCompare.remove(account);

			generateCrossTransferBooking(accountTansfer, account, kontenListToCompare, accountTansfer != null, daysRebooking);
		}

		if (accountTansfer != null && !accountTansfer.getBookings().isEmpty()) {
			accountList.add(accountTansfer);
		}

		return accountList;
	}

	private void generateCrossTransferBooking(A accountTansfer, A account, Collection<A> kontenListToCompare,
			boolean withTransferAccount, int daysRebooking) {
		for (Booking booking : account.getBookings()) {
			if (!NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(booking.getCrossAccountNamePP())
					&& !account.getNamePP().equalsIgnoreCase(booking.getCrossAccountNamePP())
					&& (booking.getTyp() == Typ.REBOOKING_IN || booking.getTyp() == Typ.REBOOKING_OUT)) {

				Booking crossBookingToTransfer = null;
				try {
					Account<B> kontoCmp = getKontoByNamePP(kontenListToCompare, booking.getCrossAccountNamePP());
					crossBookingToTransfer = findCrossBooking(kontoCmp, booking, daysRebooking, withTransferAccount);
				} catch (AccountException ae) {
					log.error(
							"Booking (Account: {}) {} / {} / {} has cross reference to Account<Booking> {}, "
									+ "but this Account<Booking> does not exist in File!",
							account.getNamePP(), booking.getDate(), booking.getPurpose(), booking.getCrossAccountIBAN(),
							booking.getCrossAccountNamePP(), ae);
					continue;
				} catch (Exception e) {
					log.error("Error finding CMP Account: {}!", booking.getCrossAccountNamePP(), e);
				}

				if (crossBookingToTransfer != null) {
					generateAndLinkCrossBookingsOnTransferAccount(accountTansfer, booking, crossBookingToTransfer);
				}
			}
		}
	}

	protected abstract void generateAndLinkCrossBookingsOnTransferAccount(Account<B> accountTansfer, Booking booking, Booking crossBookingToTransfer);
	
	private Booking findCrossBooking(Account<B> kontoCmp, Booking baseBooking, int maxTimeBetween, boolean withTransferAccount) {
		Booking rebookingCandidate = null;
		int days = maxTimeBetween;
		for (Booking bookingCmp : kontoCmp.getBookings()) {
			if (isCorrespondingRebooking(baseBooking.getTyp(), bookingCmp.getTyp())
					&& compareCorrespondingAmount(baseBooking.getAmount(), bookingCmp.getAmount())
					&& compareCrossAccount(baseBooking.getCrossAccountNamePP(), bookingCmp)
					&& compareBaseAccount(baseBooking.getAccountNamePP(), bookingCmp)) {				
				int daysBetweenFound = compareTransactionDates(baseBooking.getDate(), bookingCmp.getDate(), bookingCmp.getTyp(), maxTimeBetween);

				if (daysBetweenFound == 0) {
					baseBooking.setCrossBooking(bookingCmp);
					return null;
				} else if (daysBetweenFound > 0 && isDaysInRange(daysBetweenFound, maxTimeBetween) && daysBetweenFound < days) {
					rebookingCandidate = bookingCmp;
					days = daysBetweenFound;
				}
			}
		}

		if (withTransferAccount) {
			return rebookingCandidate;
		}
		baseBooking.setCrossBooking(rebookingCandidate);
		return null;
	}

	private boolean isUmbuchung(Typ bookingType) {
		return (bookingType != null && bookingType == Typ.REBOOKING_IN || bookingType == Typ.REBOOKING_OUT);
	}

	private boolean isCorrespondingRebooking(Typ bookingTypeBase, Typ bookingTypeCmp) {
		return (bookingTypeBase != null && bookingTypeCmp != null
				&& (bookingTypeBase == Typ.REBOOKING_IN && bookingTypeCmp == Typ.REBOOKING_OUT
						|| bookingTypeBase == Typ.REBOOKING_OUT && bookingTypeCmp == Typ.REBOOKING_IN));
	}

	private boolean compareAmount(BigDecimal amountBasebooking, BigDecimal amountCmpKonto) {
		return (amountBasebooking.abs().compareTo(amountCmpKonto.abs()) == 0);
	}

	private boolean compareCorrespondingAmount(BigDecimal amountBasebooking, BigDecimal amountCmpKonto) {
		return (amountBasebooking.multiply(BigDecimal.valueOf(-1L)).compareTo(amountCmpKonto) == 0);
	}
	
	private int compareTransactionDates(LocalDate dateBasebooking, LocalDate dateCmpKonto, Typ typ, int timeBetween) {

		if (dateBasebooking.isEqual(dateCmpKonto) && timeBetween > 0) {
			return 0; // direct Rebooking without transfer account
		} else if (typ == Typ.REBOOKING_IN) {
			return Math.abs((int) ChronoUnit.DAYS.between(dateCmpKonto, dateBasebooking));
		} else if (typ == Typ.REBOOKING_OUT) {
			return Math.abs((int) ChronoUnit.DAYS.between(dateCmpKonto, dateBasebooking));
		} else {
			return -1;
		}
	}

	private boolean compareCrossAccount(String crossAccountNamePP, Booking possibleReBooking) {
		return possibleReBooking.getAccountNamePP() != null
				&& possibleReBooking.getAccountNamePP().equals(crossAccountNamePP);
	}

	private boolean compareBaseAccount(String baseAccountNamePP, Booking possibleReBooking) {
		return possibleReBooking.getAccountNamePP() != null
				&& possibleReBooking.getCrossAccountNamePP().equals(baseAccountNamePP);
	}

	private Account<B> getKontoByNamePP(Collection<A> kontenList, String kontoNamePP) throws AccountException {
		for (Account<B> konto : kontenList) {
			if (kontoNamePP.equalsIgnoreCase(konto.getNamePP()) || konto.getNamePP().contains(kontoNamePP)) {
				return konto;
			}
		}
		throw new AccountException("Konto " + kontoNamePP + " not found!");
	}

	private boolean isDaysInRange(long value, int timeBetween) {
		return (value >= -timeBetween && value <= timeBetween);
	}

	public void revertCancellationRebookings(Collection<A> accountList) {

		Map<String, String[]> cancelBookingsMap = new HashMap<>();
		for (final String name : propsCancel.stringPropertyNames()) {
			cancelBookingsMap.put(name, propsCancel.getProperty(name).split(";"));
		}

		int cancellationBookingsCount = 0;
		for (Account<B> account : accountList) {
			String[] cancelPatterns = cancelBookingsMap.get(account.getNamePP());
			if (cancelPatterns == null) {
				continue;
			}
			for (String cancelPatternStr : cancelPatterns) {
				Pattern pattern = Pattern.compile(cancelPatternStr);
				for (Booking bookingCancel : account.getBookings()) {
					Matcher matcher = pattern.matcher(bookingCancel.getPurpose());
					if (matcher.find()) {
						String purposeToSearch = matcher.group(1);
						cancellationBookingsCount = searchAccountforCancelBooking(account, bookingCancel, purposeToSearch, cancellationBookingsCount);
					}
				}
			}
		}
		log.warn("Found Cancellation Bookings: {}\n", cancellationBookingsCount);
	}

	private int searchAccountforCancelBooking(Account<B> account, Booking bookingCancel, String purposeToSearch, int cancellationBookingsCount) {
		for (Booking bookingOriginal : account.getBookings()) {
			if (bookingOriginal.getPurpose().contains(purposeToSearch) 
					&& isDaysInRange(ChronoUnit.DAYS.between(bookingCancel.getDate(), bookingOriginal.getDate()), 3)
					&& compareCorrespondingAmount(bookingCancel.getAmount(), bookingOriginal.getAmount())) {
				log.warn("Cancellation-Booking to revert found: (Account: {} ) {} / {} / {} / {} / {} \n,"
								+ "corresponding booking: {} / {}/ {} / {} / {} ",
						account.getNamePP(), bookingCancel.getDate(), bookingCancel.getAmountStr(),
						bookingCancel.getPurpose(), bookingCancel.getCrossAccountIBAN(),
						bookingCancel.getCrossAccountNamePP(), bookingOriginal.getDate(),
						bookingOriginal.getAmountStr(), bookingOriginal.getPurpose(),
						bookingOriginal.getCrossAccountIBAN(),
						bookingOriginal.getCrossAccountNamePP());

				bookingCancel.setTyp(Typ.UNKNOWN);
				bookingCancel.setCrossAccountNamePP(null);
				bookingOriginal.setTyp(Typ.UNKNOWN);
				bookingOriginal.setCrossAccountNamePP(null);

				cancellationBookingsCount++;

				break;
			}
		}
		return cancellationBookingsCount;
	}

	public String getAccountBalance(A account) {
		BigDecimal balanceAccount = BigDecimal.ZERO;
		for (Booking booking : account.getBookings()) {
			balanceAccount = balanceAccount.add(booking.getAmount());
		}
		return balanceAccount.toString();
	}

	public int countAccountBookings(A account, Typ typ) {
		int reBookings = 0;
		for (Booking booking : account.getBookings()) {
			if (!NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(booking.getAccountNamePP())
					&& !NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(booking.getCrossAccountNamePP())
					&& typ == booking.getTyp()
					|| (typ == null && (booking.getTyp() == null || booking.getTyp() == Typ.UNKNOWN))) {
				reBookings++;
			}
		}
		return reBookings;
	}

	public void removeDoubleSameDayRebookings(Collection<A> accountList) throws AccountException {
		int removeCount = 0;
		for (Account<B> account : accountList) {
			if (NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(account.getNamePP())) {
				continue;
			}
			for (Booking possibleReBooking : account.getBookings()) {
				if (isUmbuchung(possibleReBooking.getTyp())
						&& !NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(possibleReBooking.getCrossAccountNamePP())) {
					removeCount = removeBookingsFromCrossAccount(accountList, possibleReBooking, removeCount);
				}
			}
		}

		log.warn("Removed total {} cross Bookings", removeCount);
	}

	private int removeBookingsFromCrossAccount(Collection<A> accountList, Booking possibleReBooking, int removeCount)
			throws AccountException {
		Collection<B> crossAccountBookings = getKontoByNamePP(accountList, possibleReBooking.getCrossAccountNamePP()).getBookings();
		for (Booking bookingCrossAccount : crossAccountBookings) {
			if (isCorrespondingRebooking(possibleReBooking.getTyp(), bookingCrossAccount.getTyp())
					&& compareCrossAccount(bookingCrossAccount.getCrossAccountNamePP(), possibleReBooking)
					&& possibleReBooking.getDate().equals(bookingCrossAccount.getDate())
					&& compareCorrespondingAmount(possibleReBooking.getAmount(), bookingCrossAccount.getAmount())) {
				if (crossAccountBookings.remove(bookingCrossAccount)) {
					removeCount++;
				}
				break;
			}
		}
		return removeCount;
	}
	
	public void removeTransferAccountBookings(Collection<A> accountList) {
		for (A account : accountList) {
			if (NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(account.getNamePP())) {
				accountList.remove(account);
			}
		}
	}

	public List<Booking> findMissingRebookings(Collection<B> bookingListWithoutTransfer) {

		int rebookingsFoundCount = 0;
		int rebookingsMissingCount = 0;

		List<Booking> missingReBookings = new ArrayList<>();

		for (Booking baseBooking : bookingListWithoutTransfer) {
			if (!isUmbuchung(baseBooking.getTyp())
					|| NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(baseBooking.getCrossAccountNamePP())) {
				continue;
			}
			boolean foundRebooking = false;
			for (Booking possibleReBooking : bookingListWithoutTransfer) {
				if (!possibleReBooking.equals(baseBooking)
						&& compareTransactionDates(baseBooking.getDate(), possibleReBooking.getDate(),
								possibleReBooking.getTyp(), 0) == 0
						&& compareAmount(baseBooking.getAmount(), possibleReBooking.getAmount())
						&& compareCrossAccount(baseBooking.getCrossAccountNamePP(), possibleReBooking)) {
					foundRebooking = true;
					rebookingsFoundCount++;
					break;
				}
			}
			if (!foundRebooking) {
				rebookingsMissingCount++;

				missingReBookings.add(baseBooking);
			}
		}

		log.warn("Missing Rebookings: {}", rebookingsMissingCount);
		log.warn("Found Rebookings: {}", rebookingsFoundCount);

		return missingReBookings;
	}

	public List<Booking> findReBookings(Collection<B> bookingListWithoutTransfer) {
		List<Booking> reBookingsList = new ArrayList<>();
		for (Booking possibleReBooking : bookingListWithoutTransfer) {
			if (!NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(possibleReBooking.getAccountNamePP())
					&& !NAME_ACCOUNT_TRANSFER.equalsIgnoreCase(possibleReBooking.getCrossAccountNamePP())
					&& isUmbuchung(possibleReBooking.getTyp())) {
				reBookingsList.add(possibleReBooking);
			}
		}
		return reBookingsList;
	}
	
	public void printMissingDoubleSameDayRebookings(List<B> missingBookings) {
		
		missingBookings.sort(Booking::compareBookingByAccountThenDate);

		for (Booking baseBooking : missingBookings) {
			log.printf(Level.WARN,
					"Re-Booking missing for (BaseAccount): %-25s / %8s / %9s / %-160s / %23s / %11s / %25s",
					baseBooking.getAccountNamePP(), baseBooking.getDate(), baseBooking.getAmountStr(),
					baseBooking.getPurpose().substring(0,
							baseBooking.getPurpose().length() > 160 ? 160 : baseBooking.getPurpose().length()),
					baseBooking.getCrossAccountIBAN(), baseBooking.getCrossAccountBIC(),
					baseBooking.getCrossAccountNamePP());
		}

	}
}
