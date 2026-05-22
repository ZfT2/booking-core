package de.zft2.core.dto;

import java.util.List;

public interface Account<B extends Booking> {

	String getIban();

	String getBic();

	String getNumber();

	String getAccountName();

	String getNamePP();

	List<B> getBookings();

	void setIban(String string);

	void setBic(String string);

	void setNumber(String string);

	void setNamePP(String namePP);

	void setParentAccount(String parentAccountName);

	void setBookings(List<B> bookings);

}
