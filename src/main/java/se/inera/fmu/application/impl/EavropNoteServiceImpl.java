package se.inera.fmu.application.impl;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import se.inera.fmu.application.EavropNoteService;
import se.inera.fmu.application.impl.command.AddNoteCommand;
import se.inera.fmu.application.impl.command.RemoveNoteCommand;
import se.inera.fmu.application.util.StringUtils;
import se.inera.fmu.domain.model.eavrop.Eavrop;
import se.inera.fmu.domain.model.eavrop.EavropId;
import se.inera.fmu.domain.model.eavrop.EavropRepository;
import se.inera.fmu.domain.model.eavrop.note.Note;
import se.inera.fmu.domain.model.eavrop.note.NoteType;
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
public class EavropNoteServiceImpl implements EavropNoteService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final EavropRepository eavropRepository;
    

    /**
     * Constructor
     * @param eavropRepository
     * @param domainEventPublisher
     */
	@Inject
	public EavropNoteServiceImpl(EavropRepository eavropRepository) {
		this.eavropRepository = eavropRepository;
	}

	@Override
	public void addNote(AddNoteCommand aCommand) {
		Eavrop eavrop = getEavropByEavropId(aCommand.getEavropId());
		HoSPerson person = null;
		if(!StringUtils.isBlankOrNull(aCommand.getPersonName())){
			person  = new HoSPerson(aCommand.getPersonName(),  aCommand.getPersonRole(), aCommand.getPersonOrganisation(), aCommand.getPersonUnit());
		}
		
		Note note = new Note(NoteType.EAVROP, aCommand.getText(), person);
		
		eavrop.addNote(note);
		
		if(log.isDebugEnabled()){
        	log.debug(String.format("Note %s added to eavrop:: %s", note.getNoteId(), eavrop.getEavropId().toString()));
        }

	}

	@Override
	public void removeNote(RemoveNoteCommand noteCommand) {
		throw new IllegalArgumentException("removeNote(RemoveNoteCommand noteCommand) method not yet implemented");
	}

	private Eavrop getEavropByEavropId(EavropId eavropId) throws EntityNotFoundException{
		Eavrop eavrop = this.eavropRepository.findByEavropId(eavropId);
		if(eavrop==null){
			throw new EntityNotFoundException(String.format("Eavrop %s not found", eavropId.toString()));
		}
		return eavrop;
	}
}