package de.cronn.jira.sync.mapping;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.cronn.jira.sync.domain.JiraComment;
import de.cronn.jira.sync.domain.JiraIssue;
import de.cronn.jira.sync.service.JiraService;

@Component
public class DefaultCommentMapper implements CommentMapper {

	private static final String TAB_PANEL_PAGE_ID = "com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel";

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);
	private static final String THIS_COMMENT_WAS_ADDED_BEHIND_TIME = "This comment was added behind time";

	private Clock clock;

	private UsernameReplacer usernameReplacer;

	private TicketReferenceReplacer ticketReferenceReplacer;

	@Autowired
	public void setClock(Clock clock) {
		this.clock = clock;
	}

	@Autowired
	public void setUsernameReplacer(UsernameReplacer usernameReplacer) {
		this.usernameReplacer = usernameReplacer;
	}

	@Autowired
	public void setTicketReferenceReplacer(TicketReferenceReplacer ticketReferenceReplacer) {
		this.ticketReferenceReplacer = ticketReferenceReplacer;
	}

	@Override
	public String map(JiraIssue sourceIssue, JiraComment comment, JiraService jiraSource, boolean behindTime) {
		String originalCommentId = getOriginalCommentId(comment);
		String author = getAuthorDisplayName(comment);
		String dateString = getDateString(comment);
		String sourceKey = getIssueKey(sourceIssue);
		String commentText = usernameReplacer.replaceUsernames(comment.getBody(), jiraSource);
		commentText = ticketReferenceReplacer.replaceTicketReferences(commentText, jiraSource);
		return "{panel:title=" + author + " - " + dateString + "|" + getPanelColors(behindTime) + "}\n" +
			commentText + "\n" +
			"~??[comment " + originalCommentId + "|" + buildCommentLink(jiraSource, originalCommentId, sourceKey) + "]??~\n" +
			(behindTime ? "~(!) " + THIS_COMMENT_WAS_ADDED_BEHIND_TIME + ". The order of comments might not represent the real order.~\n" : "") +
			"{panel}";
	}

	@Override
	public boolean wasAddedBehindTime(JiraComment comment) {
		return comment.getBody().contains(THIS_COMMENT_WAS_ADDED_BEHIND_TIME);
	}

	private String getPanelColors(boolean outOfOrder) {
		if (outOfOrder) {
			return "titleBGColor=#CCC|bgColor=#DDD";
		} else {
			return "titleBGColor=#DDD|bgColor=#EEE";
		}
	}

	@Override
	public boolean isMapped(JiraComment commentInSource, String commentTextInTarget) {
		String sourceCommentId = commentInSource.getId();
		Assert.hasText(sourceCommentId);
		return commentTextInTarget.startsWith("{panel:title=") && commentTextInTarget.contains("focusedCommentId=" + sourceCommentId + "&");
	}

	private String buildCommentLink(JiraService jiraSource, String originalCommentId, String sourceKey) {
		String baseUrl = getBaseUrl(jiraSource);
		return baseUrl + "browse/" + sourceKey + "?focusedCommentId=" + originalCommentId + "&page=" + TAB_PANEL_PAGE_ID + "#comment-" + originalCommentId;
	}

	private String getBaseUrl(JiraService jiraSource) {
		String baseUrl = jiraSource.getServerInfo().getBaseUrl();
		return baseUrl + (baseUrl.endsWith("/") ? "" : "/");
	}

	private String getIssueKey(JiraIssue issue) {
		String key = issue.getKey();
		Assert.notNull(key);
		return key;
	}

	private String getDateString(JiraComment comment) {
		ZonedDateTime created = comment.getCreated();
		String createdDate = formatDate(created);
		if (comment.getUpdated().isAfter(created)) {
			return createdDate + " (Updated: " + formatDate(comment.getUpdated()) + ")";
		} else {
			return createdDate;
		}
	}

	private String formatDate(ZonedDateTime zonedDateTime) {
		Assert.notNull(zonedDateTime);
		return DATE_TIME_FORMATTER.format(zonedDateTime.withZoneSameInstant(clock.getZone()));
	}

	private String getOriginalCommentId(JiraComment comment) {
		String originalCommentId = comment.getId();
		Assert.notNull(originalCommentId);
		return originalCommentId;
	}

	private String getAuthorDisplayName(JiraComment comment) {
		String author = comment.getAuthor().getDisplayName();
		Assert.notNull(author);
		return author;
	}

}
