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

	LocalDate getDateBooking();

	LocalDate getDateValue();

	BigDecimal getAmount();

	String getAmountStr();

	String getPurpose();

	Booking getCrossBooking();

	Typ getTyp();

	String getAccountName();

	String getCrossAccountIBAN();

	String getCrossAccountBIC();

	String getCrossAccountName();

	String getCrossReceiverName();

	String getCrossBankName();

	String getCrossAccountNumber();

	String getCrossBlz();

	String getSepaCustomerRef();

	String getSepaCreditorId();

	String getSepaEndToEnd();

	String getSepaMandate();

	String getSepaPersonId();

	String getSepaPurpose();

	void setDate(LocalDate date);

	void setDateBooking(LocalDate dateBooking);

	void setDateValue(LocalDate dateValue);

	void setAmount(BigDecimal amount);

	void setPurpose(String purpose);

	void setTyp(Typ typ);

	void setAccountName(String accountName);

	void setCrossAccountIBAN(String crossAccountIBAN);

	void setCrossAccountBIC(String crossAccountBIC);

	void setCrossAccountName(String accountName);

	void setCrossBooking(Booking crossBookingToTransfer);

	void setCrossReceiverName(String crossReceiverName);

	void setCrossBankName(String crossBankName);

	void setCrossAccountNumber(String crossAccountNumber);

	void setCrossBlz(String crossBlz);

	void setSepaCustomerRef(String sepaCustomerRef);

	void setSepaCreditorId(String sepaCreditorId);

	void setSepaEndToEnd(String sepaEndToEnd);

	void setSepaMandate(String sepaMandate);

	void setSepaPersonId(String sepaPersonId);

	void setSepaPurpose(String sepaPurpose);

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
