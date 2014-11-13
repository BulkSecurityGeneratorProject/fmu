package se.inera.fmu.domain.model.eavrop;

import java.util.Arrays;


public enum EavropEventDTOType {
	BOOKING_EXAMINATION,
	BOOKING_BREIFING_WITH_CITIZEN,
	BOOKING_INTERNAL_WORK,
	BOOKING_DEVIATION_RESPONSE_STOP,
	BOOKING_DEVIATION_RESPONSE_RESTART,
	INTYG_APPROVED,
	INTYG_COMPLEMENT_REQUEST,
	INTYG_SIGNED,
	EAVROP_APPROVED,
	EAVROP_COMPENSATION_APPROVED,
	UNKNOWN;
	
	private static final EavropEventDTOType[] BOOKING_TYPES = {
		BOOKING_EXAMINATION,
		BOOKING_BREIFING_WITH_CITIZEN,
		BOOKING_INTERNAL_WORK};
	
	
	public boolean isBooking(){
		if(Arrays.asList(BOOKING_TYPES).contains(this)){
			return true;
		}
		return false;
	}

}
