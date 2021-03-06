package de.cronn.jira.sync.mapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.cronn.jira.sync.JiraSyncException;
import de.cronn.jira.sync.config.JiraSyncConfig;
import de.cronn.jira.sync.domain.JiraField;
import de.cronn.jira.sync.domain.JiraIssue;
import de.cronn.jira.sync.service.JiraService;

@Component
public class DefaultCustomFieldMapper implements CustomFieldMapper {

	private JiraSyncConfig jiraSyncConfig;

	@Autowired
	public void setJiraSyncConfig(JiraSyncConfig jiraSyncConfig) {
		this.jiraSyncConfig = jiraSyncConfig;
	}

	@Override
	public Map<String, Object> map(JiraIssue fromIssue, JiraService fromJira, JiraService toJira) {
		Map<String, Object> fields = new LinkedHashMap<>();
		for (Entry<String, String> entry : jiraSyncConfig.getFieldMapping().entrySet()) {
			String fromFieldId = findCustomFieldId(fromJira, entry.getKey());
			String toFieldId = findCustomFieldId(toJira, entry.getValue());

			Map<String, Object> fromFields = fromIssue.getOrCreateFields().getOther();
			Object sourceValue = fromFields.get(fromFieldId);
			if (sourceValue != null) {
				fields.put(toFieldId, sourceValue);
			}
		}
		return fields;
	}

	private String findCustomFieldId(JiraService jiraService, String fieldName) {
		List<JiraField> fields = jiraService.getFields();
		for (JiraField field : fields) {
			if (field.getName().equals(fieldName)) {
				return field.getId();
			}
		}
		throw new JiraSyncException("Field '" + fieldName + "' not found in " + jiraService);
	}

}
