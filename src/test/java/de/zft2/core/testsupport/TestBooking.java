package de.zft2.core.testsupport;

import java.math.BigDecimal;
import java.time.LocalDate;

import de.zft2.core.dto.Booking;

public class TestBooking implements Booking {

	private LocalDate date;
	private BigDecimal amount;
	private String amountStr;
	private String purpose = "";
	private Typ typ;
	private String accountNamePP;
	private String crossAccountIBAN;
	private String crossAccountBIC;
	private String crossAccountNamePP;
	private Booking crossBooking;

	public TestBooking withDate(LocalDate date) {
		this.date = date;
		return this;
	}

	public TestBooking withAmount(String amount) {
		this.amount = amount == null ? null : new BigDecimal(amount);
		this.amountStr = amount;
		return this;
	}

	public TestBooking withPurpose(String purpose) {
		this.purpose = purpose;
		return this;
	}

	public TestBooking withTyp(Typ typ) {
		this.typ = typ;
		return this;
	}

	public TestBooking withAccountNamePP(String accountNamePP) {
		this.accountNamePP = accountNamePP;
		return this;
	}

	public TestBooking withCrossAccountIBAN(String crossAccountIBAN) {
		this.crossAccountIBAN = crossAccountIBAN;
		return this;
	}

	public TestBooking withCrossAccountBIC(String crossAccountBIC) {
		this.crossAccountBIC = crossAccountBIC;
		return this;
	}

	public TestBooking withCrossAccountNamePP(String crossAccountNamePP) {
		this.crossAccountNamePP = crossAccountNamePP;
		return this;
	}

	@Override
	public LocalDate getDate() {
		return date;
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public String getAmountStr() {
		return amountStr;
	}

	@Override
	public String getPurpose() {
		return purpose;
	}

	@Override
	public Typ getTyp() {
		return typ;
	}

	@Override
	public String getAccountNamePP() {
		return accountNamePP;
	}

	@Override
	public String getCrossAccountIBAN() {
		return crossAccountIBAN;
	}

	@Override
	public String getCrossAccountBIC() {
		return crossAccountBIC;
	}

	@Override
	public String getCrossAccountNamePP() {
		return crossAccountNamePP;
	}

	@Override
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		this.amountStr = amount == null ? null : amount.toString();
	}

	@Override
	public void setTyp(Typ typ) {
		this.typ = typ;
	}

	@Override
	public void setCrossAccountIBAN(String crossAccountIBAN) {
		this.crossAccountIBAN = crossAccountIBAN;
	}

	@Override
	public void setCrossAccountBIC(String crossAccountBIC) {
		this.crossAccountBIC = crossAccountBIC;
	}

	@Override
	public void setCrossAccountNamePP(String accountNamePP) {
		this.crossAccountNamePP = accountNamePP;
	}

	@Override
	public void setCrossBooking(Booking crossBookingToTransfer) {
		this.crossBooking = crossBookingToTransfer;
	}

	public Booking getCrossBooking() {
		return crossBooking;
	}
}
