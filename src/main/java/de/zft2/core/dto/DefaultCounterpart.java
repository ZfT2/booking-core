package de.zft2.core.dto;

public class DefaultCounterpart implements Counterpart {

	private String name;
	private String iban;
	private String bic;
	private String accountNumber;
	private String blz;
	private String bankName;

	public DefaultCounterpart() {
	}

	public DefaultCounterpart(Counterpart counterpart) {
		Counterpart.copy(counterpart, this);
	}

	public DefaultCounterpart(String name, String iban, String bic, String accountNumber, String blz, String bankName) {
		this.name = name;
		this.iban = iban;
		this.bic = bic;
		this.accountNumber = accountNumber;
		this.blz = blz;
		this.bankName = bankName;
	}

	public static DefaultCounterpart ofNullable(String name, String iban, String bic, String accountNumber, String blz, String bankName) {
		return Counterpart.hasData(name, iban, bic, accountNumber, blz, bankName)
				? new DefaultCounterpart(name, iban, bic, accountNumber, blz, bankName)
				: null;
	}

	public static DefaultCounterpart copyOf(Counterpart counterpart) {
		return Counterpart.hasData(counterpart) ? new DefaultCounterpart(counterpart) : null;
	}

	public static DefaultCounterpart withAccount(Counterpart counterpart, String iban, String bic) {
		DefaultCounterpart target = new DefaultCounterpart(counterpart);
		target.setIban(iban);
		target.setBic(bic);
		return Counterpart.hasData(target) ? target : null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getIban() {
		return iban;
	}

	@Override
	public void setIban(String iban) {
		this.iban = iban;
	}

	@Override
	public String getBic() {
		return bic;
	}

	@Override
	public void setBic(String bic) {
		this.bic = bic;
	}

	@Override
	public String getAccountNumber() {
		return accountNumber;
	}

	@Override
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Override
	public String getBlz() {
		return blz;
	}

	@Override
	public void setBlz(String blz) {
		this.blz = blz;
	}

	@Override
	public String getBankName() {
		return bankName;
	}

	@Override
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
}
