package de.zft2.core.testsupport;

import java.math.BigDecimal;
import java.time.LocalDate;

import de.zft2.core.dto.Booking;
import de.zft2.core.dto.BookingDetails;
import de.zft2.core.dto.Counterpart;
import de.zft2.core.dto.DefaultCounterpart;

public class TestBooking implements BookingDetails {

	private LocalDate date;
	private LocalDate dateBooking;
	private LocalDate dateValue;
	private BigDecimal amount;
	private String amountStr;
	private String purpose = "";
	private Typ typ;
	private String accountName;
	private String crossAccountIBAN;
	private String crossAccountBIC;
	private String crossAccountName;
	private String crossReceiverName;
	private String crossBankName;
	private String crossAccountNumber;
	private String crossBlz;
	private String sepaCustomerRef;
	private String sepaCreditorId;
	private String sepaEndToEnd;
	private String sepaMandate;
	private String sepaPersonId;
	private String sepaPurpose;
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

	public TestBooking withAccountName(String accountName) {
		this.accountName = accountName;
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

	public TestBooking withCrossAccountName(String crossAccountName) {
		this.crossAccountName = crossAccountName;
		return this;
	}

	@Override
	public LocalDate getDate() {
		return date;
	}

	@Override
	public LocalDate getDateBooking() {
		return dateBooking;
	}

	@Override
	public LocalDate getDateValue() {
		return dateValue;
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
	public Booking getCrossBooking() {
		return crossBooking;
	}

	@Override
	public Typ getTyp() {
		return typ;
	}

	@Override
	public String getAccountName() {
		return accountName;
	}

	@Override
	public Counterpart getCounterpart() {
		return DefaultCounterpart.ofNullable(crossReceiverName, crossAccountIBAN, crossAccountBIC, crossAccountNumber, crossBlz, crossBankName);
	}

	@Override
	public void setCounterpart(Counterpart counterpart) {
		crossReceiverName = counterpart != null ? counterpart.getName() : null;
		crossAccountIBAN = counterpart != null ? counterpart.getIban() : null;
		crossAccountBIC = counterpart != null ? counterpart.getBic() : null;
		crossAccountNumber = counterpart != null ? counterpart.getAccountNumber() : null;
		crossBlz = counterpart != null ? counterpart.getBlz() : null;
		crossBankName = counterpart != null ? counterpart.getBankName() : null;
	}

	public String getCrossAccountIBAN() {
		return crossAccountIBAN;
	}

	public String getCrossAccountBIC() {
		return crossAccountBIC;
	}

	@Override
	public String getCrossAccountName() {
		return crossAccountName;
	}

	public String getCrossReceiverName() {
		return crossReceiverName;
	}

	public String getCrossBankName() {
		return crossBankName;
	}

	public String getCrossAccountNumber() {
		return crossAccountNumber;
	}

	public String getCrossBlz() {
		return crossBlz;
	}

	@Override
	public String getSepaCustomerRef() {
		return sepaCustomerRef;
	}

	@Override
	public String getSepaCreditorId() {
		return sepaCreditorId;
	}

	@Override
	public String getSepaEndToEnd() {
		return sepaEndToEnd;
	}

	@Override
	public String getSepaMandate() {
		return sepaMandate;
	}

	@Override
	public String getSepaPersonId() {
		return sepaPersonId;
	}

	@Override
	public String getSepaPurpose() {
		return sepaPurpose;
	}

	@Override
	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public void setDateBooking(LocalDate dateBooking) {
		this.dateBooking = dateBooking;
	}

	@Override
	public void setDateValue(LocalDate dateValue) {
		this.dateValue = dateValue;
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
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@Override
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setCrossAccountIBAN(String crossAccountIBAN) {
		this.crossAccountIBAN = crossAccountIBAN;
	}

	public void setCrossAccountBIC(String crossAccountBIC) {
		this.crossAccountBIC = crossAccountBIC;
	}

	@Override
	public void setCrossAccountName(String accountName) {
		this.crossAccountName = accountName;
	}

	@Override
	public void setCrossBooking(Booking crossBookingToTransfer) {
		this.crossBooking = crossBookingToTransfer;
	}

	public void setCrossReceiverName(String crossReceiverName) {
		this.crossReceiverName = crossReceiverName;
	}

	public void setCrossBankName(String crossBankName) {
		this.crossBankName = crossBankName;
	}

	public void setCrossAccountNumber(String crossAccountNumber) {
		this.crossAccountNumber = crossAccountNumber;
	}

	public void setCrossBlz(String crossBlz) {
		this.crossBlz = crossBlz;
	}

	@Override
	public void setSepaCustomerRef(String sepaCustomerRef) {
		this.sepaCustomerRef = sepaCustomerRef;
	}

	@Override
	public void setSepaCreditorId(String sepaCreditorId) {
		this.sepaCreditorId = sepaCreditorId;
	}

	@Override
	public void setSepaEndToEnd(String sepaEndToEnd) {
		this.sepaEndToEnd = sepaEndToEnd;
	}

	@Override
	public void setSepaMandate(String sepaMandate) {
		this.sepaMandate = sepaMandate;
	}

	@Override
	public void setSepaPersonId(String sepaPersonId) {
		this.sepaPersonId = sepaPersonId;
	}

	@Override
	public void setSepaPurpose(String sepaPurpose) {
		this.sepaPurpose = sepaPurpose;
	}
}
