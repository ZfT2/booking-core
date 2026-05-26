package de.zft2.core.dto;

import java.math.BigDecimal;
import java.util.List;

public interface Account<B extends Booking> {

	String getIban();

	String getBic();

	String getNumber();

	String getBlz();

	String getBankName();

	String getAccountName();

	String getParentAccount();

	BigDecimal getBalance();

	String getNamePP();

	List<B> getBookings();

	void setAccountName(String accountName);

	void setIban(String string);

	void setBic(String string);

	void setNumber(String string);

	void setBlz(String blz);

	void setBankName(String bankName);

	void setNamePP(String namePP);

	void setParentAccount(String parentAccountName);

	void setBalance(BigDecimal balance);

	void setBookings(List<B> bookings);

}
