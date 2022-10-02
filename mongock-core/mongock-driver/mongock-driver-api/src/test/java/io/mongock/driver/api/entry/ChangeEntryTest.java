package io.mongock.driver.api.entry;


import io.mongock.utils.field.FieldInstance;
import io.mongock.utils.field.FieldUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeEntryTest {

	@Test
	public void fields() throws NoSuchMethodException {
		// given
		Map<String, Object> fieldExpectation = getExpectation();
		ChangeEntry entry = getChangeEntry();

		// when
		List<FieldInstance> instances = FieldUtil.getAllFields(entry.getClass())
				.stream()
				.map(field -> new FieldInstance(field, entry))
				.collect(Collectors.toList());
		// then
		assertEquals(fieldExpectation.size(), instances.size());
		assertTrue(instances.stream()
				.allMatch(field ->
						"timestamp".equals(field.getName()) || fieldExpectation.get(field.getName()).equals(field.getValue())));
	}

	private ChangeEntry getChangeEntry() throws NoSuchMethodException {

		Map<String, String> metadata = new HashMap<>();
		metadata.put("field", "value");
		return ChangeEntry.failedInstance(
				"migrationExecutionId",
				"changeAuthor",
				ChangeState.FAILED,
				ChangeType.EXECUTION,
				"changeId",
				ChangeEntryTest.class.getName(),
				"changeSetMethod",
				333,
				"localhost",
				metadata,
                                "ERROR_TRACE",
                                true
		);
	}

	private Map<String, Object> getExpectation() {
		Map<String, String> metadata = new HashMap<>();
		metadata.put("field", "value");
		Map<String, Object> fieldExpectation = new HashMap<>();
		fieldExpectation.put("executionId", "migrationExecutionId");
		fieldExpectation.put("changeId", "changeId");
		fieldExpectation.put("author", "changeAuthor");
		fieldExpectation.put("timestamp", null);
		fieldExpectation.put("state", "FAILED");
                fieldExpectation.put("type", "EXECUTION");
		fieldExpectation.put("changeLogClass", ChangeEntryTest.class.getName());
		fieldExpectation.put("changeSetMethod", "changeSetMethod");
		fieldExpectation.put("metadata", metadata);
		fieldExpectation.put("executionMillis", 333L);
		fieldExpectation.put("executionHostname", "localhost");
                fieldExpectation.put("errorTrace", "ERROR_TRACE");
                fieldExpectation.put("systemChange", true);
		return fieldExpectation;
	}

	public void changeSetMethod() {
	}

}
