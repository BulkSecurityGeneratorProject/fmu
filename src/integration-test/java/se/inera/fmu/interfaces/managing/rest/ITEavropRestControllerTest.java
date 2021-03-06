package se.inera.fmu.interfaces.managing.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;

import se.inera.fmu.Application;
import se.inera.fmu.application.CurrentUserService;
import se.inera.fmu.application.FmuOrderingService;
import se.inera.fmu.domain.model.authentication.Role;
import se.inera.fmu.domain.model.eavrop.booking.BookingStatusType;
import se.inera.fmu.domain.model.eavrop.booking.BookingType;
import se.inera.fmu.domain.model.eavrop.booking.interpreter.InterpreterBookingStatusType;
import se.inera.fmu.facade.FmuFacade;
import se.inera.fmu.infrastructure.security.FakeAuthenticationProvider;
import se.inera.fmu.interfaces.managing.rest.dto.AddNoteRequestDTO;
import se.inera.fmu.interfaces.managing.rest.dto.BookingModificationRequestDTO;
import se.inera.fmu.interfaces.managing.rest.dto.BookingRequestDTO;
import se.inera.fmu.interfaces.managing.rest.dto.TimeDTO;
import se.inera.fmu.interfaces.managing.rest.dto.TolkBookingModificationRequestDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@IntegrationTest("server.port:0")
// Randomize ports
@Validated
@Slf4j
public class ITEavropRestControllerTest {

	@Inject
	private FmuFacade fmuFacade; 
	private MockMvc restMock;

	@Inject
	private CurrentUserService currentUserService;
	
	@Inject
	private FakeAuthenticationProvider provider;

	@Before
	public void SetUp() {
		EavropResource eavropResource = new EavropResource();
		ReflectionTestUtils.setField(eavropResource, "fmuFacade", fmuFacade);

		this.restMock = MockMvcBuilders.standaloneSetup(eavropResource).build();
		TestUtil.loginWithNoActiveRole(provider);
	}
	
	@Test
	public void getEavropAsSamordnareTest() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		MvcResult result = restMock
				.perform(get("/app/rest/eavrop/1").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void loggedInasLandstingSamordnareNotAccepted() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		DateTime startDate = new DateTime(1990, 1, 1, 0, 0, 0);
		DateTime endDate = new DateTime(2990, 1, 1, 0, 0, 0);
		MvcResult result = restMock
				.perform(get("/app/rest/eavrop" + "/fromdate/"
										+ startDate.getMillis() + "/todate/"
										+ endDate.getMillis()
										+ "/status/NOT_ACCEPTED" + "/page/0"
										+ "/pagesize/10" + "/sortkey/arendeId"
										+ "/sortorder/ASC").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements", is(5)))
				.andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void loggedInasLandstingSamordnareAccepted() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		DateTime startDate = new DateTime(1990, 1, 1, 0, 0, 0);
		DateTime endDate = new DateTime(2990, 1, 1, 0, 0, 0);
		MvcResult result = restMock
				.perform(
						get(
								"/app/rest/eavrop" + "/fromdate/"
										+ startDate.getMillis() + "/todate/"
										+ endDate.getMillis()
										+ "/status/ACCEPTED" + "/page/0"
										+ "/pagesize/10" + "/sortkey/arendeId"
										+ "/sortorder/ASC").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void loggedInasLandstingSamordnareCompleted() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		DateTime startDate = new DateTime(1990, 1, 1, 0, 0, 0);
		DateTime endDate = new DateTime(2990, 1, 1, 0, 0, 0);
		MvcResult result = restMock
				.perform(get("/app/rest/eavrop" + "/fromdate/"
										+ startDate.getMillis() + "/todate/"
										+ endDate.getMillis()
										+ "/status/COMPLETED" + "/page/0"
										+ "/pagesize/10" + "/sortkey/arendeId"
										+ "/sortorder/ASC").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void loggedInasUtredareNotAccepted() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(Role.ROLE_UTREDARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		DateTime startDate = new DateTime(1990, 1, 1, 0, 0, 0);
		DateTime endDate = new DateTime(2990, 1, 1, 0, 0, 0);
		MvcResult result = restMock.perform(
						get("/app/rest/eavrop" + "/fromdate/"
										+ startDate.getMillis() + "/todate/"
										+ endDate.getMillis()
										+ "/status/NOT_ACCEPTED" + "/page/0"
										+ "/pagesize/10" + "/sortkey/arendeId"
										+ "/sortorder/ASC").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void loggedInasUtredareAccepted() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(Role.ROLE_UTREDARE);

		DateTime startDate = new DateTime(1990, 1, 1, 0, 0, 0);
		DateTime endDate = new DateTime(2990, 1, 1, 0, 0, 0);
		MvcResult result = restMock.perform(
				get("/app/rest/eavrop" + "/fromdate/"
										+ startDate.getMillis() + "/todate/"
										+ endDate.getMillis()
										+ "/status/ACCEPTED" + "/page/0"
										+ "/pagesize/10" + "/sortkey/arendeId"
										+ "/sortorder/ASC").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.totalElements", is(8)))
				.andExpect(status().isOk())
				.andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void loggedInasUtredareCompleted() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(Role.ROLE_UTREDARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		DateTime startDate = new DateTime(1990, 1, 1, 0, 0, 0);
		DateTime endDate = new DateTime(2990, 1, 1, 0, 0, 0);
		MvcResult result = restMock
				.perform(
						get("/app/rest/eavrop" + "/fromdate/"
										+ startDate.getMillis() + "/todate/"
										+ endDate.getMillis()
										+ "/status/COMPLETED" + "/page/0"
										+ "/pagesize/10" + "/sortkey/arendeId"
										+ "/sortorder/ASC").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	@Test
	public void getEavropEvents() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		MvcResult result = restMock
				.perform(
						get("/app/rest/eavrop/1/utredning").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.error(result.getResponse().getContentAsString());
	}

	@Test
	public void createBookingTest() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);
		BookingRequestDTO booking = new BookingRequestDTO();
		booking.setEavropId("3");
		booking.setBookingType(BookingType.EXAMINATION);
		booking.setBookingDate(new DateTime().getMillis());
		booking.setBookingStartTime(new TimeDTO().setHour(12).setMinute(30));
		booking.setBookingEndTime(new TimeDTO().setHour(13).setMinute(30));
		booking.setPersonName("Åsa Andersson");
		booking.setAdditionalService(true);
		booking.setPersonRole("Läkare");
		booking.setUseInterpreter(true);

		restMock.perform(
				post("/app/rest/eavrop/utredning/create/booking").contentType(
						MediaType.APPLICATION_JSON).content(
						convertObjectToJsonBytes(booking))).andExpect(
				status().isOk());

		MvcResult result = restMock
				.perform(
						get("/app/rest/eavrop/3/utredning").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.error(result.getResponse().getContentAsString());
	}

	@Test
	public void changeBookingTest() throws Exception {
		// Set login credencials
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		// Create a booking
		BookingRequestDTO booking = new BookingRequestDTO();
		booking.setEavropId("3");
		booking.setBookingType(BookingType.EXAMINATION);
		booking.setBookingDate(new DateTime().getMillis());
		booking.setBookingStartTime(new TimeDTO().setHour(12).setMinute(30));
		booking.setBookingEndTime(new TimeDTO().setHour(13).setMinute(30));
		booking.setPersonName("Åsa Andersson");
		booking.setAdditionalService(true);
		booking.setPersonRole("Läkare");
		booking.setUseInterpreter(true);

		restMock.perform(
				post("/app/rest/eavrop/utredning/create/booking").contentType(
						MediaType.APPLICATION_JSON).content(
						convertObjectToJsonBytes(booking))).andExpect(
				status().isOk());

		// Change the newly created booking
		MvcResult result = restMock.perform(get("/app/rest/eavrop/3/utredning")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		log.debug("Handelse: " + result.getResponse().getContentAsString());
		String bookingId = getJsonValue(0, "bookingId", result.getResponse().getContentAsString());
		
		TolkBookingModificationRequestDTO tolkModificationRequest = new TolkBookingModificationRequestDTO();
		tolkModificationRequest.setBookingId(bookingId)
		  .setBookingStatus(InterpreterBookingStatusType.INTERPRETER_NOT_PRESENT)
		  .setEavropId("3")
		  .setComment("This tolk is bad");

		log.debug(convertObjectToJsonBytes(tolkModificationRequest));
		restMock.perform(post("/app/rest/eavrop/utredning/modify/tolk")
				.contentType(MediaType.APPLICATION_JSON)
				.content(convertObjectToJsonBytes(tolkModificationRequest)))
						.andExpect(status().isOk());
		
		// Change tolk booking
		BookingModificationRequestDTO bookingModificationRequest = new BookingModificationRequestDTO();
		bookingModificationRequest.setBookingId(bookingId)
		  .setBookingStatus(BookingStatusType.CANCELLED_NOT_PRESENT)
		  .setEavropId("3")
		  .setComment("This booking is bad");

		log.debug(convertObjectToJsonBytes(bookingModificationRequest));
		restMock.perform(post("/app/rest/eavrop/utredning/modify/booking")
				.contentType(MediaType.APPLICATION_JSON)
				.content(convertObjectToJsonBytes(bookingModificationRequest)))
						.andExpect(status().isOk());
		// Verify
		result = restMock.perform(get("/app/rest/eavrop/3/utredning")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].tolkStatus.currentStatus.name", is("INTERPRETER_NOT_PRESENT")))
				.andExpect(jsonPath("$[0].handelseStatus.currentStatus.name", is("CANCELLED_NOT_PRESENT")))
				.andExpect(jsonPath("$[0].tolkStatus.comment", is("This tolk is bad")))
				.andExpect(jsonPath("$[0].handelseStatus.comment", is("This booking is bad")))
				.andReturn();
		log.debug(result.getResponse().getContentAsString());
	}
	
	@Test
	public void addNoteTest() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);
		AddNoteRequestDTO addRequest = new AddNoteRequestDTO();
		addRequest.setEavropId("3")
		.setText("This is the note content");

		restMock.perform(
				post("/app/rest/eavrop/note/add").contentType(
						MediaType.APPLICATION_JSON).content(
						convertObjectToJsonBytes(addRequest))).andExpect(
				status().isOk());
	}
	
	@Test
	public void removeNoteTest() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_UTREDARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);
		
		// Add note
		AddNoteRequestDTO addRequest = new AddNoteRequestDTO();
		addRequest.setEavropId("3")
		.setText("This is the note content");

		restMock.perform(
				post("/app/rest/eavrop/note/add").contentType(
						MediaType.APPLICATION_JSON).content(
						convertObjectToJsonBytes(addRequest))).andExpect(
				status().isOk());
		
		// Get the newly added note
		MvcResult result = restMock.perform(get("/app/rest/eavrop/3/notes")
				.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", hasSize(1)))
						.andExpect(jsonPath("$[0].removable", is(true)))
						.andReturn();
		
		String noteId = getJsonValue(0, "noteId", result.getResponse().getContentAsString());
		// Remove the newly created note
		restMock.perform(delete("/app/rest/eavrop/3/note/" + noteId + "/remove")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		// Validate note being removed
		restMock.perform(get("/app/rest/eavrop/3/notes")
				.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	public void getCompensationsTest() throws Exception {
		this.currentUserService.getCurrentUser().setActiveRole(
				Role.ROLE_SAMORDNARE);
		this.currentUserService.getCurrentUser().setLandstingCode(1);

		MvcResult result = restMock
				.perform(get("/app/rest/eavrop/16/compensations").accept(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		result.getResponse().setCharacterEncoding("UTF-8");
		log.debug(result.getResponse().getContentAsString());
	}

	public static String convertObjectToJsonBytes(Object object)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsString(object);
	}

	public static String getJsonValue(int index, String fieldName, String jsonString)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonobj = mapper.readTree(jsonString);
		return jsonobj.path(index).path(fieldName).toString().replace("\"", "");
	}

}
