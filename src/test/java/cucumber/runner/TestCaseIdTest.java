package cucumber.runner;

import com.epam.reportportal.annotations.TestCaseId;
import com.epam.reportportal.annotations.TestCaseIdKey;
import com.epam.reportportal.cucumber.RunningContextExtension;
import com.epam.reportportal.cucumber.StepReporter;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.ReportPortal;
import com.epam.reportportal.service.ReportPortalClient;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRS;
import io.cucumber.core.internal.gherkin.ast.Location;

import io.cucumber.plugin.event.*;
import io.reactivex.Maybe;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import rp.com.google.common.collect.Lists;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:ivan_budayeu@epam.com">Ivan Budayeu</a>
 */
public class TestCaseIdTest {

	private static final URI FEATURE_URI = URI.create(TestCaseIdTest.class.getClassLoader().getResource("test/belly.feature").toString());

	private StepReporterExtension stepReporter;

	private static ArrayList<String> parameterValues = Lists.newArrayList("1", "parameter");
	private static ArrayList<String> parameterNames = Lists.newArrayList("count", "item");

	public static class StepReporterExtension extends StepReporter {

		static final ThreadLocal<Step> STEP = new ThreadLocal<>();

		static final ThreadLocal<TestStep> TEST_STEP = new ThreadLocal<>();

		static final ThreadLocal<RunningContextExtension.FeatureContextExtension> FEATURE_CONTEXT = new ThreadLocal<>();
		static final ThreadLocal<RunningContextExtension.ScenarioContextExtension> SCENARIO_CONTEXT = new ThreadLocal<>();

		static final ThreadLocal<ReportPortal> REPORT_PORTAL = new ThreadLocal<>();

		static final ThreadLocal<Launch> LAUNCH = new ThreadLocal<>();

		static final ThreadLocal<ReportPortalClient> REPORT_PORTAL_CLIENT = new ThreadLocal<>();

		static final ThreadLocal<ListenerParameters> LISTENER_PARAMETERS = new ThreadLocal<>();

		public StepReporterExtension() {
			STEP.set(getStep(1, "Given", "some test step"));

			RunningContextExtension.FeatureContextExtension featureContext = mock(RunningContextExtension.FeatureContextExtension.class);
			FEATURE_CONTEXT.set(featureContext);

			RunningContextExtension.ScenarioContextExtension scenarioContext = mock(RunningContextExtension.ScenarioContextExtension.class);
			when(scenarioContext.getId()).thenReturn(Maybe.create(emitter -> {
				emitter.onSuccess("scenarioId");
				emitter.onComplete();
			}));

			when(scenarioContext.getStepPrefix()).thenReturn("");
			SCENARIO_CONTEXT.set(scenarioContext);

			ReportPortalClient reportPortalClient = mock(ReportPortalClient.class);
			when(reportPortalClient.startLaunch(any(StartLaunchRQ.class))).then(t -> Maybe.create(emitter -> {
				StartLaunchRS rs = new StartLaunchRS();
				rs.setId("launchId");
				emitter.onSuccess(rs);
				emitter.onComplete();
			}).blockingGet());
			REPORT_PORTAL_CLIENT.set(reportPortalClient);

			ListenerParameters listenerParameters = mock(ListenerParameters.class);
			when(listenerParameters.getEnable()).thenReturn(true);
			when(listenerParameters.getBaseUrl()).thenReturn("http://example.com");
			when(listenerParameters.getIoPoolSize()).thenReturn(10);
			when(listenerParameters.getBatchLogsSize()).thenReturn(5);
			LISTENER_PARAMETERS.set(listenerParameters);

			Launch launch = mock(Launch.class);
			when(launch.start()).thenReturn(Maybe.create(emitter -> {
				emitter.onSuccess("launchId");
				emitter.onComplete();
			}));
			LAUNCH.set(launch);

			ReportPortal reportPortal = mock(ReportPortal.class);
			when(reportPortal.getClient()).thenReturn(REPORT_PORTAL_CLIENT.get());
			when(reportPortal.newLaunch(any())).thenReturn(LAUNCH.get());
			when(reportPortal.getParameters()).thenReturn(LISTENER_PARAMETERS.get());
			REPORT_PORTAL.set(reportPortal);
		}

		@Override
		public void beforeStep(TestStep testStep) {
			super.beforeStep(testStep);
		}

		@Override
		public void beforeLaunch() {
			super.beforeLaunch();
		}

		@Override
		protected ReportPortal buildReportPortal() {
			return REPORT_PORTAL.get();
		}

		@Override
		protected RunningContextExtension.ScenarioContextExtension getCurrentScenarioContext() {
			return SCENARIO_CONTEXT.get();
		}
	}

	@Before
	public void initLaunch() {
		stepReporter = new StepReporterExtension();
	}

	@Test
	public void shouldSendCaseIdWhenNotParametrized() throws NoSuchMethodException {

		stepReporter.beforeLaunch();

		PickleStepTestStep testStep = getCustomTestStep(new StepDefinition(getClass().getDeclaredMethod("testCaseIdMethod").getName(), ""));

		StepReporterExtension.TEST_STEP.set(testStep);

		when(StepReporterExtension.SCENARIO_CONTEXT.get()
				.getStep(StepReporterExtension.TEST_STEP.get())).thenReturn(new io.cucumber.core.internal.gherkin.ast.Step(new Location(1, 1),
				"keyword",
				String.format("tesst with parameters <%s>", String.join("> <", parameterNames)),
				null
		));

		stepReporter.beforeStep(StepReporterExtension.TEST_STEP.get());

		ArgumentCaptor<StartTestItemRQ> startTestItemRQArgumentCaptor = ArgumentCaptor.forClass(StartTestItemRQ.class);
		verify(StepReporterExtension.LAUNCH.get(), times(1)).startTestItem(any(), startTestItemRQArgumentCaptor.capture());

		StartTestItemRQ request = startTestItemRQArgumentCaptor.getValue();
		assertNotNull(request);
		assertNotNull(request.getTestCaseId());
		assertEquals("SimpleCaseId", request.getTestCaseId());
	}

	@TestCaseId(value = "SimpleCaseId")
	public void testCaseIdMethod() {

	}

	@Test
	public void shouldSendCaseIdWhenParametrized() throws NoSuchMethodException {

		stepReporter.beforeLaunch();

		PickleStepTestStep testStep = getCustomTestStep(new StepDefinition(getClass().getDeclaredMethod("testCaseIdParametrizedMethod",
				Integer.class,
				String.class
		).getName(), ""));

		StepReporterExtension.TEST_STEP.set(testStep);

		when(StepReporterExtension.SCENARIO_CONTEXT.get()
				.getStep(StepReporterExtension.TEST_STEP.get())).thenReturn(new io.cucumber.core.internal.gherkin.ast.Step(new Location(1, 1),
				"keyword",
				String.format("tesst with parameters <%s>", String.join("> <", parameterNames)),
				null
		));

		stepReporter.beforeStep(StepReporterExtension.TEST_STEP.get());

		ArgumentCaptor<StartTestItemRQ> startTestItemRQArgumentCaptor = ArgumentCaptor.forClass(StartTestItemRQ.class);
		verify(StepReporterExtension.LAUNCH.get(), times(1)).startTestItem(any(), startTestItemRQArgumentCaptor.capture());

		StartTestItemRQ request = startTestItemRQArgumentCaptor.getValue();
		assertNotNull(request);
		assertNotNull(request.getTestCaseId());
		assertEquals("parameter", request.getTestCaseId());
	}

	@TestCaseId(parametrized = true)
	public void testCaseIdParametrizedMethod(Integer index, @TestCaseIdKey String caseIdKey) {

	}

	@Test
	public void shouldSendCaseIdWhenParametrizedWithoutKey() throws NoSuchMethodException {

		stepReporter.beforeLaunch();

		PickleStepTestStep testStep = getCustomTestStep(new StepDefinition(getClass().getDeclaredMethod(
				"testCaseIdParametrizedMethodWithoutKey",
				Integer.class,
				String.class
		).getName(), ""));

		StepReporterExtension.TEST_STEP.set(testStep);

		when(StepReporterExtension.SCENARIO_CONTEXT.get()
				.getStep(StepReporterExtension.TEST_STEP.get())).thenReturn(new io.cucumber.core.internal.gherkin.ast.Step(new Location(1, 1),
				"keyword",
				String.format("tesst with parameters <%s>", String.join("> <", parameterNames)),
				null
		));

		stepReporter.beforeStep(StepReporterExtension.TEST_STEP.get());

		ArgumentCaptor<StartTestItemRQ> startTestItemRQArgumentCaptor = ArgumentCaptor.forClass(StartTestItemRQ.class);
		verify(StepReporterExtension.LAUNCH.get(), times(1)).startTestItem(any(), startTestItemRQArgumentCaptor.capture());

		StartTestItemRQ request = startTestItemRQArgumentCaptor.getValue();
		assertNotNull(request);
		assertNotNull(request.getTestCaseId());
		assertEquals("com.test.Parametrized[1,parameter]", request.getTestCaseId());
	}

	@TestCaseId(parametrized = true)
	public void testCaseIdParametrizedMethodWithoutKey(Integer index, String caseIdKey) {

	}

	private PickleStepTestStep getCustomTestStep(StepDefinition stepDefinition) {
		Step pickleStep = getStep(1, "Given", "text");
		PickleStepTestStep result = mock(PickleStepTestStep.class);
		when(result.getUri()).thenReturn(FEATURE_URI);
		when(result.getDefinitionArgument()).thenReturn(Collections.emptyList());
		when(result.getPattern()).thenReturn(stepDefinition.getPattern());
		when(result.getCodeLocation()).thenReturn(stepDefinition.getLocation());
		when(result.getStep()).thenReturn(pickleStep);
		return result;
	}

	private static Step getStep(final int line, final String keyword, final String text) {
		Step result = mock(Step.class);
		when(result.getLine()).thenReturn(line);
		when(result.getArgument()).thenReturn(mock(StepArgument.class));
		when(result.getKeyWord()).thenReturn(keyword);
		when(result.getText()).thenReturn(text);
		return result;
	}
}


