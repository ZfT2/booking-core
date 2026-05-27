package de.zft2.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

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
				if (Objects.equals(x.translation, strValue))
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

	Booking getCrossBooking();

	Typ getTyp();

	String getAccountName();

	Counterpart getCounterpart();

	String getCrossAccountName();

	void setDate(LocalDate date);

	void setAmount(BigDecimal amount);

	void setPurpose(String purpose);

	void setTyp(Typ typ);

	void setAccountName(String accountName);

	void setCounterpart(Counterpart counterpart);

	void setCrossAccountName(String accountName);

	void setCrossBooking(Booking crossBookingToTransfer);

	public static int compareBookingByAccountThenDate(Booking b1, Booking b2) {
		int value1 = b1.getCrossAccountName().compareTo(b2.getCrossAccountName());
		if (value1 == 0) {
			int value2 = b1.getAccountName().compareTo(b2.getAccountName());
			if (value2 == 0) {
				return b1.getDate().compareTo(b2.getDate());
			} else {
				return value2;
			}
		}
		return value1;
	}

}
