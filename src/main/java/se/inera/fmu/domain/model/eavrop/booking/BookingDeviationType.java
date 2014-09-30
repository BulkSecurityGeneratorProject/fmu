package se.inera.fmu.domain.model.eavrop.booking;

public enum BookingDeviationType {
	INVANARE_ABSENT,
	INVANARE_CANCELLED_LT_48,
	INVANARE_CANCELLED_GT_48,
	INVANARE_CANCELLED_LT_96,
	CANCELLED_GT_96,
	INTERPRETER_CANCELED,
	INTERPRETER_ABSENT,
	INTERPRETER_NOT_UTILIZED,
	PARTY_ABSENT;
}