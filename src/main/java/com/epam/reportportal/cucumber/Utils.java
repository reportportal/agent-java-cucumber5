/*
 * Copyright 2020 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epam.reportportal.cucumber;

import com.epam.reportportal.listeners.ItemStatus;
import com.epam.reportportal.utils.reflect.Accessible;
import io.cucumber.plugin.event.Argument;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestStep;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author Vadzim Hushchanskou
 */
public class Utils {
	private static final String EMPTY = "";

	private static final String DEFINITION_MATCH_FIELD_NAME = "definitionMatch";
	private static final String STEP_DEFINITION_FIELD_NAME = "stepDefinition";
	private static final String METHOD_FIELD_NAME = "method";

	private Utils() {
		throw new AssertionError("No instances should exist for the class!");
	}

	//@formatter:off
    public static final Map<Status, ItemStatus> STATUS_MAPPING = Collections.unmodifiableMap(new HashMap<Status, ItemStatus>(){{
            put(Status.PASSED, ItemStatus.PASSED);
            put(Status.FAILED, ItemStatus.FAILED);
            put(Status.SKIPPED, ItemStatus.SKIPPED);
            put(Status.PENDING, ItemStatus.SKIPPED);
            put(Status.AMBIGUOUS, ItemStatus.SKIPPED);
            put(Status.UNDEFINED, ItemStatus.SKIPPED);
            put(Status.UNUSED, ItemStatus.SKIPPED);
	}});

	public static final Map<Status, String> LOG_LEVEL_MAPPING = Collections.unmodifiableMap(new HashMap<Status, String>() {{
			put(Status.PASSED, "INFO");
			put(Status.FAILED, "ERROR");
			put(Status.SKIPPED, "WARN");
			put(Status.PENDING, "WARN");
			put(Status.AMBIGUOUS, "WARN");
			put(Status.UNDEFINED, "WARN");
			put(Status.UNUSED, "WARN");
		}});
    //@formatter:on

	/**
	 * Generate name representation
	 *
	 * @param prefix   - substring to be prepended at the beginning (optional)
	 * @param infix    - substring to be inserted between keyword and name
	 * @param argument - main text to process
	 * @return transformed string
	 */
	public static String buildName(String prefix, String infix, String argument) {
		return (prefix == null ? EMPTY : prefix) + infix + argument;
	}

	public static Method retrieveMethod(Object stepDefinitionMatch) throws IllegalAccessException, NoSuchFieldException {
		Object javaStepDefinition = Accessible.on(stepDefinitionMatch).field(STEP_DEFINITION_FIELD_NAME).getValue();
		Method method = null;
		if (javaStepDefinition != null) {
			method = (Method) Accessible.on(javaStepDefinition).field(METHOD_FIELD_NAME).getValue();
		}
		return method;
	}

	public static final java.util.function.Function<List<Argument>, List<?>> ARGUMENTS_TRANSFORM = arguments -> ofNullable(arguments).map(
			args -> args.stream().map(Argument::getValue).collect(Collectors.toList())).orElse(null);

	public static Object getDefinitionMatch(TestStep testStep) {
		try {
			return Accessible.on(testStep).field(DEFINITION_MATCH_FIELD_NAME).getValue();
		} catch (NoSuchFieldException e) {
			return null;
		}
	}
}
