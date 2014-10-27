package se.inera.fmu.domain.model.eavrop;

/**
 * The end state of the Eavrop
 */
public class ClosedEavropState extends AbstractEavropState {
	@Override
	public EavropStateType getEavropStateType() {
		return EavropStateType.CLOSED;
	}
}