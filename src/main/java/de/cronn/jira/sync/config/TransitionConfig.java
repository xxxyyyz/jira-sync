package de.cronn.jira.sync.config;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TransitionConfig {

	private Set<String> sourceStatusIn = new LinkedHashSet<>();
	private Set<String> targetStatusIn = new LinkedHashSet<>();
	private String sourceStatusToSet;
	private boolean copyResolutionToSource = false;
	private boolean copyFixVersionsToSource = false;
	private boolean onlyIfAssignedInTarget = false;
	private boolean assignToMyselfInSource = false;
	private boolean triggerIfIssueWasMovedBetweenProjects = false;

	public TransitionConfig() {
	}

	public TransitionConfig(Collection<String> sourceStatusIn, Collection<String> targetStatusIn, String sourceStatusToSet) {
		this.sourceStatusIn = new LinkedHashSet<>(sourceStatusIn);
		this.targetStatusIn = new LinkedHashSet<>(targetStatusIn);
		this.sourceStatusToSet = sourceStatusToSet;
	}

	public Set<String> getSourceStatusIn() {
		return sourceStatusIn;
	}

	public void setSourceStatusIn(Set<String> sourceStatusIn) {
		this.sourceStatusIn = sourceStatusIn;
	}

	public void setTargetStatusIn(Set<String> targetStatusIn) {
		this.targetStatusIn = targetStatusIn;
	}

	public String getSourceStatusToSet() {
		return sourceStatusToSet;
	}

	public void setSourceStatusToSet(String sourceStatusToSet) {
		this.sourceStatusToSet = sourceStatusToSet;
	}

	public boolean isCopyResolutionToSource() {
		return copyResolutionToSource;
	}

	public void setCopyResolutionToSource(boolean copyResolutionToSource) {
		this.copyResolutionToSource = copyResolutionToSource;
	}

	public boolean isCopyFixVersionsToSource() {
		return copyFixVersionsToSource;
	}

	public void setCopyFixVersionsToSource(boolean copyFixVersionsToSource) {
		this.copyFixVersionsToSource = copyFixVersionsToSource;
	}

	public void setOnlyIfAssignedInTarget(boolean onlyIfAssignedInTarget) {
		this.onlyIfAssignedInTarget = onlyIfAssignedInTarget;
	}

	public boolean isOnlyIfAssignedInTarget() {
		return onlyIfAssignedInTarget;
	}

	public void setAssignToMyselfInSource(boolean assignToMyselfInSource) {
		this.assignToMyselfInSource = assignToMyselfInSource;
	}

	public boolean isAssignToMyselfInSource() {
		return assignToMyselfInSource;
	}

	public Set<String> getTargetStatusIn() {
		return targetStatusIn;
	}

	public boolean isTriggerIfIssueWasMovedBetweenProjects() {
		return triggerIfIssueWasMovedBetweenProjects;
	}

	public void setTriggerIfIssueWasMovedBetweenProjects(boolean triggerIfIssueWasMovedBetweenProjects) {
		this.triggerIfIssueWasMovedBetweenProjects = triggerIfIssueWasMovedBetweenProjects;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("sourceStatusIn", sourceStatusIn)
			.append("targetStatusIn", targetStatusIn)
			.append("sourceStatusToSet", sourceStatusToSet)
			.toString();
	}
}
