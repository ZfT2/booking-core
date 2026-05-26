package de.zft2.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Booking {

	public enum Typ {

		REBOOKING_IN("Umbuchung (Eingang)"),
		REBOOKING_OUT("Umbuchung (Ausgang)"),
		INTEREST("Zinsen"),
		INTEREST_CHARGE("Zinsbelastung"),
		TAX("Steuern"),
		TAX_REFUND("Steuerrückerstattung"),
		DIVIDENDS("Dividende"),
		UNKNOWN(null);

		public static Typ forString(String strValue) {
			for (Typ x : values()) {
				if (x.translation.equals(strValue))
					return x;
			}
			return null;
		}

		private final String translation;

		private Typ(String translation) {
			this.translation = translation;
		}

		@Override
		public final String toString() {
			return translation;
		}
	}

	LocalDate getDate();

	BigDecimal getAmount();

	String getAmountStr();

	String getPurpose();

	Typ getTyp();

	String getAccountNamePP();

	String getCrossAccountIBAN();

	String getCrossAccountBIC();

	String getCrossAccountNamePP();

	void setAmount(BigDecimal amount);

	void setTyp(Typ typ);

	void setCrossAccountIBAN(String crossAccountIBAN);

	void setCrossAccountBIC(String crossAccountBIC);

	void setCrossAccountNamePP(String accountNamePP);

	void setCrossBooking(Booking crossBookingToTransfer);

	public static int compareBookingByAccountThenDate(Booking b1, Booking b2) {
		int value1 = b1.getCrossAccountNamePP().compareTo(b2.getCrossAccountNamePP());
		if (value1 == 0) {
			int value2 = b1.getAccountNamePP().compareTo(b2.getAccountNamePP());
			if (value2 == 0) {
				return b1.getDate().compareTo(b2.getDate());
			} else {
				return value2;
			}
		}
		return value1;
	}

}
