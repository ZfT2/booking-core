package de.zft2.core.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.zft2.core.exception.ConfigurationException;
import de.zft2.core.testsupport.TestAccount;

class AccountProcessorTest {

	private static final String CHECKING_IBAN = "DE11111111111111111111";
	private static final String SAVINGS_IBAN = "DE22222222222222222222";

	@BeforeEach
	void resetProcessorState() {
		AccountProcessor.propsTransfer = null;
		AccountProcessor.propsAccount = null;
		AccountProcessor.propsSkip = null;
		AccountProcessor.accountNumbersMap.clear();
	}

	@Test
	void isCrossBookingOnSameAccountRecognizesConfiguredAndDerivedIdentifiers() throws ConfigurationException {
		AccountProcessor<TestAccount> processor = new AccountProcessor<>();
		TestAccount checking = new TestAccount("Checking", "Checking")
				.withIban(CHECKING_IBAN)
				.withNumber("1111111111");

		assertTrue(processor.isCrossBookingOnSameAccount(checking, CHECKING_IBAN));
		assertTrue(processor.isCrossBookingOnSameAccount(checking, "1111111111"));
		assertFalse(processor.isCrossBookingOnSameAccount(checking, SAVINGS_IBAN));
	}

	@Test
	void addParentAccountsMarksAccountsThatShareAConfiguredParent() throws ConfigurationException {
		AccountProcessor<TestAccount> processor = new AccountProcessor<>();
		TestAccount firstChild = new TestAccount("Joint One", "Joint One").withIban("DE33333333333333333333");
		TestAccount secondChild = new TestAccount("Joint Two", "Joint Two").withIban("DE44444444444444444444");
		TestAccount unrelated = new TestAccount("Unrelated", "Unrelated").withIban(SAVINGS_IBAN);

		processor.addParentAccounts(List.of(firstChild, secondChild, unrelated));

		assertEquals("Joint", firstChild.getParentAccount());
		assertEquals("Joint", secondChild.getParentAccount());
		assertEquals(null, unrelated.getParentAccount());
	}
}
