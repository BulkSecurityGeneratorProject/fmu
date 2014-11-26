package se.inera.fmu.application.impl;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import se.inera.fmu.application.DomainEventPublisher;
import se.inera.fmu.application.EavropAssignmentService;
import se.inera.fmu.application.impl.command.AcceptEavropAssignmentCommand;
import se.inera.fmu.application.impl.command.AssignEavropCommand;
import se.inera.fmu.application.impl.command.RejectEavropAssignmentCommand;
import se.inera.fmu.application.util.StringUtils;
import se.inera.fmu.domain.model.eavrop.Eavrop;
import se.inera.fmu.domain.model.eavrop.EavropId;
import se.inera.fmu.domain.model.eavrop.EavropRepository;
import se.inera.fmu.domain.model.eavrop.assignment.EavropAcceptedByVardgivarenhetEvent;
import se.inera.fmu.domain.model.eavrop.assignment.EavropAssignedToVardgivarenhetEvent;
import se.inera.fmu.domain.model.eavrop.assignment.EavropRejectedByVardgivarenhetEvent;
import se.inera.fmu.domain.model.hos.hsa.HsaId;
import se.inera.fmu.domain.model.hos.vardgivare.Vardgivarenhet;
import se.inera.fmu.domain.model.hos.vardgivare.VardgivarenhetRepository;
import se.inera.fmu.domain.model.person.HoSPerson;

/**
 * Service for handling eavrop assignments
 * 
 * @author 
 *
 */
@Service
@Validated
@Transactional
public class EavropAssignmentServiceImpl implements EavropAssignmentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final EavropRepository eavropRepository;
    private final VardgivarenhetRepository vardgivarenhetRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * Constructor
     * @param eavropRepository
     * @param domainEventPublisher
     */
	@Inject
	public EavropAssignmentServiceImpl(EavropRepository eavropRepository, VardgivarenhetRepository vardgivarenhetRepository, DomainEventPublisher domainEventPublisher) {
		this.eavropRepository = eavropRepository;
		this.vardgivarenhetRepository = vardgivarenhetRepository;
		this.domainEventPublisher = domainEventPublisher;
	}
	
	@Override
	public void assignEavropToVardgivarenhet(AssignEavropCommand aCommand) throws EntityNotFoundException, IllegalArgumentException{
		Eavrop eavrop = getEavropByEavropId(aCommand.getEavropId());
		Vardgivarenhet vardgivarenhet = getVardgivarenhetByHsaId(aCommand.getVardgivarenhetHsaId());

		if(eavrop.getCurrentAssignedVardgivarenhet()!=null){
			throw new IllegalArgumentException(String.format("Eavrop %s already assigned to vardgivarenhet %s", aCommand.getEavropId().toString(), eavrop.getCurrentAssignedVardgivarenhet().getHsaId().toString() ));
		}

		HoSPerson assigningPerson = null;
		if(!StringUtils.isBlankOrNull(aCommand.getPersonName()) || aCommand.getPersonHsaId() != null){
			assigningPerson  = new HoSPerson(aCommand.getPersonHsaId(), aCommand.getPersonName(), aCommand.getPersonRole(), aCommand.getPersonOrganisation(), aCommand.getPersonUnit());
		}
		eavrop.assignEavropToVardgivarenhet(vardgivarenhet, assigningPerson);
		log.debug(String.format("Eavrop %s assigned to :: %s", aCommand.getEavropId().toString(), aCommand.getVardgivarenhetHsaId().toString()));
		
		handleEavropAssigned(aCommand.getEavropId(), aCommand.getVardgivarenhetHsaId());
	}

	@Override
	public void acceptEavropAssignment(AcceptEavropAssignmentCommand aCommand) throws EntityNotFoundException, IllegalArgumentException{
		Eavrop eavrop = getEavropByEavropId(aCommand.getEavropId());
		Vardgivarenhet vardgivarenhet = getVardgivarenhetByHsaId(aCommand.getVardgivarenhetHsaId());

		if(!vardgivarenhet.equals(eavrop.getCurrentAssignedVardgivarenhet())){
			throw new IllegalArgumentException(String.format("Eavrop %s not assigned to vardgivarenhet %s", aCommand.getEavropId().toString(), aCommand.getVardgivarenhetHsaId().toString() ));
		}
		HoSPerson acceptingPerson = null;
		if(!StringUtils.isBlankOrNull(aCommand.getPersonName()) || aCommand.getPersonHsaId() != null){
			acceptingPerson  = new HoSPerson(aCommand.getPersonHsaId(), aCommand.getPersonName(), aCommand.getPersonRole(), aCommand.getPersonOrganisation(), aCommand.getPersonUnit());
		}
		
		eavrop.acceptEavropAssignment(acceptingPerson);
		log.debug(String.format("Eavrop %s accepted  by :: %s", aCommand.getEavropId().toString(), aCommand.getVardgivarenhetHsaId().toString()));
		
		handleEavropAccepted(aCommand.getEavropId(), aCommand.getVardgivarenhetHsaId());
	}

	@Override
	public void rejectEavropAssignment(RejectEavropAssignmentCommand aCommand)  throws EntityNotFoundException, IllegalArgumentException{
		Eavrop eavrop = getEavropByEavropId(aCommand.getEavropId());
		Vardgivarenhet vardgivarenhet = getVardgivarenhetByHsaId(aCommand.getVardgivarenhetHsaId());

		if(!vardgivarenhet.equals(eavrop.getCurrentAssignedVardgivarenhet())){
			throw new IllegalArgumentException(String.format("Eavrop %s not assigned to vardgivarenhet %s", aCommand.getEavropId().toString(), aCommand.getVardgivarenhetHsaId().toString()));
		}
		
		HoSPerson rejectingPerson = null;
		if(!StringUtils.isBlankOrNull(aCommand.getPersonName()) || aCommand.getPersonHsaId() != null){
			rejectingPerson = new HoSPerson(aCommand.getPersonHsaId(), aCommand.getPersonName(), aCommand.getPersonRole(), aCommand.getPersonOrganisation(), aCommand.getPersonUnit());
		}

		eavrop.rejectEavropAssignment(rejectingPerson, null);
		log.debug(String.format("Eavrop %s rejected  by :: %s", aCommand.getEavropId().toString(), aCommand.getVardgivarenhetHsaId().toString()));
		
		handleEavropRejected(aCommand.getEavropId(), aCommand.getVardgivarenhetHsaId());
	}

	private Eavrop getEavropByEavropId(EavropId eavropId) throws EntityNotFoundException{
		Eavrop eavrop = this.eavropRepository.findByEavropId(eavropId);
		if(eavrop==null){
			throw new EntityNotFoundException(String.format("Eavrop %s not found", eavropId.toString()));
		}
		return eavrop;
	}
	
	private Vardgivarenhet getVardgivarenhetByHsaId(HsaId hsaIdVardgivarenhet) throws EntityNotFoundException{
		Vardgivarenhet vardgivarenhet = this.vardgivarenhetRepository.findByHsaId(hsaIdVardgivarenhet);
		if(vardgivarenhet==null){
			throw new EntityNotFoundException(String.format("Vardgivarenhet %s not found", hsaIdVardgivarenhet.toString()));
		}
		return vardgivarenhet;
	}

	private DomainEventPublisher getDomainEventPublisher(){
		return this.domainEventPublisher;
	}
	
	private void handleEavropAssigned(EavropId eavropId, HsaId vardgivarenhetId){
		EavropAssignedToVardgivarenhetEvent event = new EavropAssignedToVardgivarenhetEvent(eavropId, vardgivarenhetId);
        if(log.isDebugEnabled()){
        	log.debug(String.format("EavropAssignedToVardgivarenhetEvent created :: %s", event.toString()));
        }
		getDomainEventPublisher().post(event);
	}

	private void handleEavropAccepted(EavropId eavropId, HsaId vardgivarenhetId){
		EavropAcceptedByVardgivarenhetEvent event = new EavropAcceptedByVardgivarenhetEvent(eavropId, vardgivarenhetId);
        if(log.isDebugEnabled()){
        	log.debug(String.format("EavropAcceptedByVardgivarenhetEvent created :: %s", event.toString()));
        }
		getDomainEventPublisher().post(event);
	}

	private void handleEavropRejected(EavropId eavropId, HsaId vardgivarenhetId){
		EavropRejectedByVardgivarenhetEvent event = new EavropRejectedByVardgivarenhetEvent(eavropId, vardgivarenhetId);
        if(log.isDebugEnabled()){
        	log.debug(String.format("EavropRejectedByVardgivarenhetEvent created :: %s", event.toString()));
        }
		getDomainEventPublisher().post(event);
	}
	
	
}
