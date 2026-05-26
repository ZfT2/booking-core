package de.zft2.core.testsupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.zft2.core.dto.Account;

public class TestAccount implements Account<TestBooking> {

	private String iban;
	private String bic;
	private String number;
	private String blz;
	private String bankName;
	private String accountName;
	private String namePP;
	private BigDecimal balance;
	private String parentAccount;
	private List<TestBooking> bookings = new ArrayList<>();

	public TestAccount(String accountName, String namePP) {
		this.accountName = accountName;
		this.namePP = namePP;
	}

	public TestAccount withIban(String iban) {
		this.iban = iban;
		return this;
	}

	public TestAccount withBic(String bic) {
		this.bic = bic;
		return this;
	}

	public TestAccount withNumber(String number) {
		this.number = number;
		return this;
	}

	public TestAccount withBlz(String blz) {
		this.blz = blz;
		return this;
	}

	public TestAccount withBankName(String bankName) {
		this.bankName = bankName;
		return this;
	}

	public TestAccount withBookings(TestBooking... bookings) {
		this.bookings = new ArrayList<>(Arrays.asList(bookings));
		return this;
	}

	@Override
	public String getIban() {
		return iban;
	}

	@Override
	public String getBic() {
		return bic;
	}

	@Override
	public String getNumber() {
		return number;
	}

	@Override
	public String getBlz() {
		return blz;
	}

	@Override
	public String getBankName() {
		return bankName;
	}

	@Override
	public String getAccountName() {
		return accountName;
	}

	@Override
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Override
	public String getNamePP() {
		return namePP;
	}

	@Override
	public List<TestBooking> getBookings() {
		return bookings;
	}

	@Override
	public void setIban(String string) {
		this.iban = string;
	}

	@Override
	public void setBic(String string) {
		this.bic = string;
	}

	@Override
	public void setNumber(String string) {
		this.number = string;
	}

	@Override
	public void setBlz(String blz) {
		this.blz = blz;
	}

	@Override
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Override
	public void setNamePP(String namePP) {
		this.namePP = namePP;
	}

	@Override
	public void setParentAccount(String parentAccountName) {
		this.parentAccount = parentAccountName;
	}

	@Override
	public void setBookings(List<TestBooking> bookings) {
		this.bookings = bookings;
	}

	public String getParentAccount() {
		return parentAccount;
	}

	@Override
	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
