package de.zft2.core.process;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.zft2.core.config.ImportProperties;
import de.zft2.core.dto.Account;
import de.zft2.core.dto.Booking;
import de.zft2.core.exception.ConfigurationException;

public class AccountProcessor<A extends Account<? extends Booking>> {
	
	private static Logger log = LogManager.getLogger(AccountProcessor.class);

	protected static Properties propsTransfer;
	protected static Properties propsAccount;
	protected static Properties propsSkip;
	
	protected static Map<String,Collection<String>> accountNumbersMap = new HashMap<>();

	public AccountProcessor() throws ConfigurationException {
		initProperties();
		setAccountNumbersMap();
	}

	private static void initProperties() throws ConfigurationException {
		if (propsTransfer == null)
			propsTransfer = ImportProperties.getInstance("accountTransfer.properties", true);
		if (propsAccount == null)
			propsAccount = ImportProperties.getInstance("account.properties", true);
		if (propsSkip == null)
			propsSkip = ImportProperties.getInstance("accountSkip.properties", false);
	}
	
	private void setAccountNumbersMap() {
		for (Map.Entry<Object, Object> property : propsTransfer.entrySet()) {

			String[] possibleIdentifiers = ((String) property.getValue()).split(";");
			String accountAliasName = ((String) property.getKey());

			accountNumbersMap.put(accountAliasName, Arrays.asList(possibleIdentifiers));
		}
	}

	protected String findAccountNamePP(String accountIdentifier) {

		for (Map.Entry<Object, Object> property : propsTransfer.entrySet()) {

			String[] possibleIdentifiers = ((String) property.getValue()).split(";");
			String accountAliasName = ((String) property.getKey());

			for (String identifier : possibleIdentifiers) {
				if (identifier.equalsIgnoreCase(accountIdentifier)) {
					return accountAliasName;
				}
			}
		}
		return null;
	}
	
	protected boolean setupTransferProperties(Collection<A> accountList) {
		
		boolean result = false;
		
		if (!propsTransfer.isEmpty()) {
			log.info("Transfer properties found, size: {}", propsTransfer.size());
		}
		log.info("creating default transfer properties...");
		for (Account<?> account : accountList) {
			String accountDescription = account.getAccountName();
			if (propsTransfer.get(accountDescription) != null) {
				log.info("Found already transfer property for account {} in file, so skipping default.", accountDescription);
				continue;
			}
			Set<String> identifiersSet = new HashSet<>();
			if (account.getIban() != null) {
				identifiersSet.add(account.getIban());
				if (account.getIban().length() > 10) {
					identifiersSet.add(account.getIban().substring(account.getIban().length() - 10));
				}
			}
			if (account.getNumber() != null) {
				identifiersSet.add(account.getNumber());
				identifiersSet.add(account.getNumber().replaceFirst("^0+(?!$)", ""));
			}
			
			Set<String> allAccountIdentifiersFromProp = accountNumbersMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
			if (!identifiersSet.isEmpty() && identifiersSet.stream().noneMatch(allAccountIdentifiersFromProp::contains)) {
				propsTransfer.put(accountDescription, String.join(";", identifiersSet));
				result = true;
			}
		}

		for (Map.Entry<Object, Object> property : propsAccount.entrySet()) {
			String possibleIdentifiers = ((String) property.getValue());
			String accountAliasName = ((String) property.getKey());
			propsTransfer.put(accountAliasName, possibleIdentifiers);
		}

		log.info("created/added default transfer properties with size: {}", propsTransfer.size());
		return result;
	}

	public boolean isCrossBookingOnSameAccount(Account<?> account, String crossIdentifier) {
		if (crossIdentifier == null) {
			return false;
		}

		if (accountNumbersMap.get(account.getNamePP()) != null && accountNumbersMap.get(account.getNamePP()).contains(crossIdentifier)) {
			return true;
		}

		String accountIdentifier = account.getIban() != null ? account.getIban() : account.getNumber();
		if (accountIdentifier != null) {
			if (accountIdentifier.equalsIgnoreCase(crossIdentifier)) {
				return true;
			}

			String numberWithZeros = accountIdentifier.length() > 12 ? accountIdentifier.substring(12) : accountIdentifier;

			numberWithZeros = numberWithZeros.length() < 10 ? String.format("%010d", Integer.parseInt(numberWithZeros)) : numberWithZeros;
			crossIdentifier = crossIdentifier.length() < 10 ? String.format("%010d", Integer.parseInt(crossIdentifier)) : crossIdentifier;

			if (numberWithZeros.endsWith(crossIdentifier)) {
				return true;
			}
		}
		return false;
	}

	public void addParentAccounts(Collection<A> accountList) {
		for (Entry<String, Collection<String>> entry : accountNumbersMap.entrySet()) {
			Account<?> accountFound = null;
			for (Account<?> account : accountList) {
				for (String identifier : entry.getValue()) {
					if (identifier.equalsIgnoreCase(account.getIban()) || identifier.equalsIgnoreCase(account.getNumber())) {
						if (accountFound != null) {
							accountFound.setParentAccount(entry.getKey());
							account.setParentAccount(entry.getKey());
						} else {
							accountFound = account;
						}
					}
				}
			}
		}
	}

}
