package de.zft2.core.dto;

public interface Counterpart {

	String getName();

	void setName(String name);

	String getIban();

	void setIban(String iban);

	String getBic();

	void setBic(String bic);

	String getAccountNumber();

	void setAccountNumber(String accountNumber);

	String getBlz();

	void setBlz(String blz);

	String getBankName();

	void setBankName(String bankName);

	static boolean hasData(Counterpart counterpart) {
		return counterpart != null && hasData(counterpart.getName(), counterpart.getIban(), counterpart.getBic(),
				counterpart.getAccountNumber(), counterpart.getBlz(), counterpart.getBankName());
	}

	static boolean hasData(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return true;
			}
		}
		return false;
	}

	static String ibanOf(Counterpart counterpart) {
		return counterpart != null ? counterpart.getIban() : null;
	}

	static String bicOf(Counterpart counterpart) {
		return counterpart != null ? counterpart.getBic() : null;
	}

	static void copy(Counterpart source, Counterpart target) {
		target.setName(source != null ? source.getName() : null);
		target.setIban(source != null ? source.getIban() : null);
		target.setBic(source != null ? source.getBic() : null);
		target.setAccountNumber(source != null ? source.getAccountNumber() : null);
		target.setBlz(source != null ? source.getBlz() : null);
		target.setBankName(source != null ? source.getBankName() : null);
	}

}
