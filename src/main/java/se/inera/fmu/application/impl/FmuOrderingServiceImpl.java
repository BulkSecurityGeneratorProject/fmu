package se.inera.fmu.application.impl;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import se.inera.fmu.application.FmuOrderingService;
import se.inera.fmu.application.impl.command.CreateEavropCommand;
import se.inera.fmu.domain.model.eavrop.ArendeId;
import se.inera.fmu.domain.model.eavrop.Eavrop;
import se.inera.fmu.domain.model.eavrop.EavropBuilder;
import se.inera.fmu.domain.model.eavrop.EavropRepository;
import se.inera.fmu.domain.model.eavrop.invanare.InvanareRepository;
import se.inera.fmu.domain.model.eavrop.properties.EavropProperties;
import se.inera.fmu.domain.model.landsting.Landsting;
import se.inera.fmu.domain.model.landsting.LandstingRepository;
import se.inera.fmu.domain.model.systemparameter.Configuration;

/**
 * Created by Rasheed on 7/7/14.
 *
 * Application Service for managing FMU process
 */
@SuppressWarnings("all")
@Service
@Validated
@Slf4j
public class FmuOrderingServiceImpl implements FmuOrderingService {

	private final EavropRepository eavropRepository;
	private final InvanareRepository invanareRepository;
	private final Configuration configuration;
	private final LandstingRepository landstingRepository;

	/**
	 *
	 * @param eavropRepository
	 * @param invanareRepository
	 * @param configuration
	 * @param landstingRepository
	 */
	@Inject
	public FmuOrderingServiceImpl(final EavropRepository eavropRepository,
								  final InvanareRepository invanareRepository, 
								  final Configuration configuration,
								  final LandstingRepository landstingRepository) {
		this.eavropRepository = eavropRepository;
		this.invanareRepository = invanareRepository;
		this.configuration = configuration;
		this.landstingRepository = landstingRepository;
	}

	/**
	 * Creates a an eavrop.
	 *
	 * @param aCommand  CreateEavropCommand
	 * @return arendeId
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public ArendeId createEavrop(CreateEavropCommand aCommand) {
		EavropProperties props = getEavropProperties();

		Landsting landsting = landstingRepository.findByLandstingCode(aCommand.getLandstingCode());
		if (landsting == null) {
			throw new IllegalArgumentException(String.format("Landsting with id %s does not exist",	aCommand.getArendeId()));
		}

		if (eavropRepository.findByArendeId(aCommand.getArendeId()) != null) {
			throw new IllegalArgumentException(String.format(
					"Eavrop with arendeId %s already exist", aCommand.getArendeId()));
		}

		Eavrop eavrop = EavropBuilder.eavrop().withArendeId(aCommand.getArendeId())
				.withUtredningType(aCommand.getUtredningType())
				.withInvanare(aCommand.getInvanare()).withLandsting(landsting)
				.withBestallaradministrator(aCommand.getBestallaradministrator())
				.withInterpreter(aCommand.getInterpreter()).withEavropProperties(props)
				.withDescription(aCommand.getDescription())
				.withUtredningFocus(aCommand.getUtredningFocus())
				.withAdditionalInformation(aCommand.getAdditionalInformation())
				.withPriorMedicalExamination(aCommand.getPriorMedicalExamination()).build();

		eavrop = eavropRepository.save(eavrop);
		log.debug(String.format("eavrop with arendeId  %s created", eavrop.getArendeId()));
		
		return eavrop.getArendeId();
	}

	private EavropProperties getEavropProperties() {
		int startDateOffset = getConfiguration().getInteger(
				Configuration.KEY_EAVROP_START_DATE_OFFSET, 3);
		int acceptanceValidLength = getConfiguration().getInteger(
				Configuration.KEY_EAVROP_ACCEPTANCE_VALID_LENGTH, 5);
		int assessmentValidLength = getConfiguration().getInteger(
				Configuration.KEY_EAVROP_ASSESSMENT_VALID_LENGTH, 25);
		int completionValidLength = getConfiguration().getInteger(
				Configuration.KEY_EAVROP_COMPLETION_VALID_LENGTH, 10);

		return new EavropProperties(startDateOffset, acceptanceValidLength, assessmentValidLength,
				completionValidLength);
	}

	private Configuration getConfiguration() {
		return this.configuration;
	}



}
