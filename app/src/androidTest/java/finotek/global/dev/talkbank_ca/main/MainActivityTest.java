package finotek.global.dev.talkbank_ca.main;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Singleton;

import dagger.Component;
import finotek.global.dev.talkbank_ca.MainActivity;
import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by magyeong-ug on 21/03/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

	@Rule
	public ActivityTestRule<MainActivity> menuActivityTestRule =
			new ActivityTestRule<>(MainActivity.class, true, true);

	@Before
	public void setUp() {
		Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
		MyApplication app = (MyApplication) instrumentation.getTargetContext().getApplicationContext();
		/*
		TestMainComponent component =
				(TestMainComponent) DaggerMainComponent.builder()
						.activityModule(new ActivityModule(menuActivityTestRule.getActivity()))
						.appComponent(app.getAppComponent()).build();

		component.inject(this);
		*/
	}

	@Test
	public void testBtnText() {
		onView(withId(R.id.main_button)).check(matches(withText("시작")));
	}

	@Test
	public void testMoveNextActivity() {
		onView(withId(R.id.main_button)).perform(click());
		//verify()
	}

	@Singleton
	@Component(dependencies = AppModule.class, modules = ActivityModule.class)
	public interface TestMainComponent extends MainComponent {
		void inject(MainActivityTest mainActivityTest);
	}

}
