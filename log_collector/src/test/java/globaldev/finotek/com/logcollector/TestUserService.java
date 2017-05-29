package globaldev.finotek.com.logcollector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import globaldev.finotek.com.logcollector.api.user.UserService;

/**
 * Created by magyeong-ug on 27/04/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestUserService {

	@Mock
	UserService userService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		userService = Mockito.mock(UserService.class);
	}

	@Test
	public void testRegisterUser() throws Exception {
	}

}
