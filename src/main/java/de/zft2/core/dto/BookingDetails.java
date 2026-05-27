package de.zft2.core.dto;

import java.time.LocalDate;

public interface BookingDetails extends Booking {

	LocalDate getDateBooking();

	LocalDate getDateValue();

	String getSepaCustomerRef();

	String getSepaCreditorId();

	String getSepaEndToEnd();

	String getSepaMandate();

	String getSepaPersonId();

	String getSepaPurpose();

	void setDateBooking(LocalDate dateBooking);

	void setDateValue(LocalDate dateValue);

	void setSepaCustomerRef(String sepaCustomerRef);

	void setSepaCreditorId(String sepaCreditorId);

	void setSepaEndToEnd(String sepaEndToEnd);

	void setSepaMandate(String sepaMandate);

	void setSepaPersonId(String sepaPersonId);

	void setSepaPurpose(String sepaPurpose);

}
