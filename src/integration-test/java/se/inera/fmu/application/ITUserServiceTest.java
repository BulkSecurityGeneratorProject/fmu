package se.inera.fmu.application;

import se.inera.fmu.Application;
import se.inera.fmu.domain.model.authentication.PersistentToken;
import se.inera.fmu.domain.model.authentication.User;
import se.inera.fmu.domain.model.authentication.PersistentTokenRepository;
import se.inera.fmu.domain.model.authentication.UserRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import se.inera.fmu.application.impl.UserService;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Test class for the UserResource REST controller.
 *
 * @see se.inera.fmu.application.impl.UserService
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class ITUserServiceTest {

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Test
    public void testRemoveOldPersistentTokens() {
    	User admin = userRepository.findOne("admin");
    	int existingCount = persistentTokenRepository.findByUser(admin).size();
        generateUserToken(admin, "1111-1111", new LocalDate());
        LocalDate now = new LocalDate();
        generateUserToken(admin, "2222-2222", now.minusDays(32));
        assertThat(persistentTokenRepository.findByUser(admin)).hasSize(existingCount + 2);
        userService.removeOldPersistentTokens();
        assertThat(persistentTokenRepository.findByUser(admin)).hasSize(existingCount + 1);
    }

    private void generateUserToken(User user, String tokenSeries, LocalDate localDate) {
        PersistentToken token = new PersistentToken();
        token.setSeries(tokenSeries);
        token.setUser(user);
        token.setTokenValue(tokenSeries + "-data");
        token.setTokenDate(localDate);
        token.setIpAddress("127.0.0.1");
        token.setUserAgent("Test agent");
        persistentTokenRepository.saveAndFlush(token);
    }
}
