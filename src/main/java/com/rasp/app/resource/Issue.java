/*
 * Copyright 2010-2020 M16, Inc. All rights reserved.
 * This software and documentation contain valuable trade
 * secrets and proprietary property belonging to M16, Inc.
 * None of this software and documentation may be copied,
 * duplicated or disclosed without the express
 * written permission of M16, Inc.
 */

package com.rasp.app.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import platform.exception.ExceptionEnum;
 import platform.resource.BaseResource;
import platform.util.*;
import org.springframework.stereotype.Component;
import platform.db.*;
import java.util.*;
import com.rasp.app.message.*;
import com.rasp.app.helper.*;
import com.rasp.app.service.*;

/*
 ********** This is a generated class Don't modify it.Extend this file for additional functionality **********
 * 
 */
@Component
 public class Issue extends BaseResource {
	private String id = null;
	private String g_created_by_id = null;
	private String g_created_by_name = null;
	private String g_modified_by_id = null;
	private String g_modified_by_name = null;
	private Long g_creation_time = null;
	private Long g_modify_time = null;
	private String g_soft_delete = null;
	private String g_status = null;
	private String archived = null;
	private Long archived_time = null;
	private String owner_id = null;
	private String project_id = null;
	private String issue_title = null;
	private String description = null;
	private String attachment = null;
	private Date due_date = null;
	private String priority = null;
	private String status = null;
	private String list_id = null;
	private Map<String, Object> extra_data = null;

	public static String FIELD_ID = "id";
	public static String FIELD_G_CREATED_BY_ID = "g_created_by_id";
	public static String FIELD_G_CREATED_BY_NAME = "g_created_by_name";
	public static String FIELD_G_MODIFIED_BY_ID = "g_modified_by_id";
	public static String FIELD_G_MODIFIED_BY_NAME = "g_modified_by_name";
	public static String FIELD_G_CREATION_TIME = "g_creation_time";
	public static String FIELD_G_MODIFY_TIME = "g_modify_time";
	public static String FIELD_G_SOFT_DELETE = "g_soft_delete";
	public static String FIELD_G_STATUS = "g_status";
	public static String FIELD_ARCHIVED = "archived";
	public static String FIELD_ARCHIVED_TIME = "archived_time";
	public static String FIELD_OWNER_ID = "owner_id";
	public static String FIELD_PROJECT_ID = "project_id";
	public static String FIELD_ISSUE_TITLE = "issue_title";
	public static String FIELD_DESCRIPTION = "description";
	public static String FIELD_ATTACHMENT = "attachment";
	public static String FIELD_DUE_DATE = "due_date";
	public static String FIELD_PRIORITY = "priority";
	public static String FIELD_STATUS = "status";
	public static String FIELD_LIST_ID = "list_id";
	public static String FIELD_EXTRA_DATA = "extra_data";

	private static final long serialVersionUID = 1L;
	public final static ResourceMetaData metaData = new ResourceMetaData("issue");

	static {
		metaData.setCheckBeforeAdd(false);
		metaData.setCheckBeforeUpdate(false);

		metaData.setAllow_duplicate_name(false);
		Field idField = new Field("id", "String");
		idField.setRequired(true);
		metaData.addField(idField);

		Field g_created_by_idField = new Field("g_created_by_id", "String");
		g_created_by_idField.setLength(128);
		metaData.addField(g_created_by_idField);

		Field g_created_by_nameField = new Field("g_created_by_name", "String");
		g_created_by_nameField.setLength(128);
		metaData.addField(g_created_by_nameField);

		Field g_modified_by_idField = new Field("g_modified_by_id", "String");
		g_modified_by_idField.setLength(128);
		metaData.addField(g_modified_by_idField);

		Field g_modified_by_nameField = new Field("g_modified_by_name", "String");
		g_modified_by_nameField.setLength(128);
		metaData.addField(g_modified_by_nameField);

		Field g_creation_timeField = new Field("g_creation_time", "long");
		metaData.addField(g_creation_timeField);

		Field g_modify_timeField = new Field("g_modify_time", "long");
		metaData.addField(g_modify_timeField);

		Field g_soft_deleteField = new Field("g_soft_delete", "String");
		g_soft_deleteField.setDefaultValue("N");
		g_soft_deleteField.setLength(1);
		metaData.addField(g_soft_deleteField);

		Field g_statusField = new Field("g_status", "String");
		g_statusField.setIndexed(true);
		g_statusField.setLength(32);
		metaData.addField(g_statusField);

		Field archivedField = new Field("archived", "String");
		archivedField.setIndexed(true);
		archivedField.setDefaultValue("N");
		archivedField.setLength(1);
		metaData.addField(archivedField);

		Field archived_timeField = new Field("archived_time", "long");
		metaData.addField(archived_timeField);

		Field owner_idField = new Field("owner_id", "String");
		owner_idField.setRequired(true);
		owner_idField.setForeign(new Foreign("Users"));
		metaData.addField(owner_idField);

		Field project_idField = new Field("project_id", "String");
		project_idField.setRequired(true);
		project_idField.setForeign(new Foreign("Project"));
		metaData.addField(project_idField);

		Field issue_titleField = new Field("issue_title", "String");
		issue_titleField.setRequired(true);
		metaData.addField(issue_titleField);

		Field descriptionField = new Field("description", "String");
		metaData.addField(descriptionField);

		Field attachmentField = new Field("attachment", "String");
		attachmentField.setFile(true);
		metaData.addField(attachmentField);

		Field due_dateField = new Field("due_date", "Date");
		metaData.addField(due_dateField);

		Field priorityField = new Field("priority", "String");
		metaData.addField(priorityField);

		Field statusField = new Field("status", "String");
		metaData.addField(statusField);

		Field list_idField = new Field("list_id", "String");
		list_idField.setRequired(true);
		list_idField.setForeign(new Foreign("List_res"));
		metaData.addField(list_idField);

		Field extra_dataField = new Field("extra_data", "Map");
		extra_dataField.setValueType("Object");
		metaData.addField(extra_dataField);


		metaData.setTableName("issue");

		metaData.setCluster("rasp_db");
	}

	public Issue() {this.setId(Util.getUniqueId());}
	public Issue(String id) {this.setId(id);}

	public Issue(Issue obj) {
		this.id = obj.id;
		this.g_created_by_id = obj.g_created_by_id;
		this.g_created_by_name = obj.g_created_by_name;
		this.g_modified_by_id = obj.g_modified_by_id;
		this.g_modified_by_name = obj.g_modified_by_name;
		this.g_creation_time = obj.g_creation_time;
		this.g_modify_time = obj.g_modify_time;
		this.g_soft_delete = obj.g_soft_delete;
		this.g_status = obj.g_status;
		this.archived = obj.archived;
		this.archived_time = obj.archived_time;
		this.owner_id = obj.owner_id;
		this.project_id = obj.project_id;
		this.issue_title = obj.issue_title;
		this.description = obj.description;
		this.attachment = obj.attachment;
		this.due_date = obj.due_date;
		this.priority = obj.priority;
		this.status = obj.status;
		this.list_id = obj.list_id;
		this.extra_data = obj.extra_data;
	}

	public ResourceMetaData getMetaData() {
		return metaData;
	}

	private void setDefaultValues() {
		if(g_soft_delete == null)
			g_soft_delete = "N";
		if(archived == null)
			archived = "N";
	}

	public Map<String, Object> convertResourceToMap(HashMap<String, Object> map) {
		if(id != null)
			map.put("id", id);
		if(g_created_by_id != null)
			map.put("g_created_by_id", g_created_by_id);
		if(g_created_by_name != null)
			map.put("g_created_by_name", g_created_by_name);
		if(g_modified_by_id != null)
			map.put("g_modified_by_id", g_modified_by_id);
		if(g_modified_by_name != null)
			map.put("g_modified_by_name", g_modified_by_name);
		if(g_creation_time != null)
			map.put("g_creation_time", g_creation_time);
		if(g_modify_time != null)
			map.put("g_modify_time", g_modify_time);
		if(g_soft_delete != null)
			map.put("g_soft_delete", g_soft_delete);
		if(g_status != null)
			map.put("g_status", g_status);
		if(archived != null)
			map.put("archived", archived);
		if(archived_time != null)
			map.put("archived_time", archived_time);
		if(owner_id != null)
			map.put("owner_id", owner_id);
		if(project_id != null)
			map.put("project_id", project_id);
		if(issue_title != null)
			map.put("issue_title", issue_title);
		if(description != null)
			map.put("description", description);
		if(attachment != null)
			map.put("attachment", attachment);
		if(due_date != null)
			map.put("due_date", due_date);
		if(priority != null)
			map.put("priority", priority);
		if(status != null)
			map.put("status", status);
		if(list_id != null)
			map.put("list_id", list_id);
		if(extra_data != null)
			map.put("extra_data", extra_data);
		return map;
	}

	public Map<String, Object> validateAndConvertResourceToMap(HashMap<String,Object> map,boolean add) throws ApplicationException {
		if(validateId(add))
			map.put("id", id);
		if(g_created_by_id != null)
			map.put("g_created_by_id", g_created_by_id);
		if(g_created_by_name != null)
			map.put("g_created_by_name", g_created_by_name);
		if(g_modified_by_id != null)
			map.put("g_modified_by_id", g_modified_by_id);
		if(g_modified_by_name != null)
			map.put("g_modified_by_name", g_modified_by_name);
		if(g_creation_time != null)
			map.put("g_creation_time", g_creation_time);
		if(g_modify_time != null)
			map.put("g_modify_time", g_modify_time);
		if(g_soft_delete != null)
			map.put("g_soft_delete", g_soft_delete);
		if(g_status != null)
			map.put("g_status", g_status);
		if(archived != null)
			map.put("archived", archived);
		if(archived_time != null)
			map.put("archived_time", archived_time);
		if(validateOwner_id(add))
			map.put("owner_id", owner_id);
		if(validateProject_id(add))
			map.put("project_id", project_id);
		if(validateIssue_title(add))
			map.put("issue_title", issue_title);
		if(description != null)
			map.put("description", description);
		if(attachment != null)
			map.put("attachment", attachment);
		if(due_date != null)
			map.put("due_date", due_date);
		if(priority != null)
			map.put("priority", priority);
		if(status != null)
			map.put("status", status);
		if(validateList_id(add))
			map.put("list_id", list_id);
		if(extra_data != null)
			map.put("extra_data", extra_data);
		return map;
	}

	public Map<String, Object> convertResourceToPrimaryMap(HashMap<String, Object> map) {
		return map;
	}

	@SuppressWarnings("unchecked")
	public void convertMapToResource(Map<String, Object> map) {
		id = (String) map.get("id");
		g_created_by_id = (String) map.get("g_created_by_id");
		g_created_by_name = (String) map.get("g_created_by_name");
		g_modified_by_id = (String) map.get("g_modified_by_id");
		g_modified_by_name = (String) map.get("g_modified_by_name");
		g_creation_time = (map.get("g_creation_time") == null ? null : ((Number) map.get("g_creation_time")).longValue());
		g_modify_time = (map.get("g_modify_time") == null ? null : ((Number) map.get("g_modify_time")).longValue());
		g_soft_delete = (String) map.get("g_soft_delete");
		g_status = (String) map.get("g_status");
		archived = (String) map.get("archived");
		archived_time = (map.get("archived_time") == null ? null : ((Number) map.get("archived_time")).longValue());
		owner_id = (String) map.get("owner_id");
		project_id = (String) map.get("project_id");
		issue_title = (String) map.get("issue_title");
		description = (String) map.get("description");
		attachment = (String) map.get("attachment");
		due_date = (Date) map.get("due_date");
		priority = (String) map.get("priority");
		status = (String) map.get("status");
		list_id = (String) map.get("list_id");
		extra_data = (Map<String, Object>) map.get("extra_data");
	}

	@SuppressWarnings("unchecked")
	public void convertTypeUnsafeMapToResource(Map<String, Object> map) {
		Object idObj = map.get("id");
		if(idObj != null)
			id = idObj.toString();

		Object g_created_by_idObj = map.get("g_created_by_id");
		if(g_created_by_idObj != null)
			g_created_by_id = g_created_by_idObj.toString();

		Object g_created_by_nameObj = map.get("g_created_by_name");
		if(g_created_by_nameObj != null)
			g_created_by_name = g_created_by_nameObj.toString();

		Object g_modified_by_idObj = map.get("g_modified_by_id");
		if(g_modified_by_idObj != null)
			g_modified_by_id = g_modified_by_idObj.toString();

		Object g_modified_by_nameObj = map.get("g_modified_by_name");
		if(g_modified_by_nameObj != null)
			g_modified_by_name = g_modified_by_nameObj.toString();

		Object g_creation_timeObj = map.get("g_creation_time");
		if(g_creation_timeObj != null)
			g_creation_time = new Long(g_creation_timeObj.toString());

		Object g_modify_timeObj = map.get("g_modify_time");
		if(g_modify_timeObj != null)
			g_modify_time = new Long(g_modify_timeObj.toString());

		Object g_soft_deleteObj = map.get("g_soft_delete");
		if(g_soft_deleteObj != null)
			g_soft_delete = g_soft_deleteObj.toString();

		Object g_statusObj = map.get("g_status");
		if(g_statusObj != null)
			g_status = g_statusObj.toString();

		Object archivedObj = map.get("archived");
		if(archivedObj != null)
			archived = archivedObj.toString();

		Object archived_timeObj = map.get("archived_time");
		if(archived_timeObj != null)
			archived_time = new Long(archived_timeObj.toString());

		Object owner_idObj = map.get("owner_id");
		if(owner_idObj != null)
			owner_id = owner_idObj.toString();

		Object project_idObj = map.get("project_id");
		if(project_idObj != null)
			project_id = project_idObj.toString();

		Object issue_titleObj = map.get("issue_title");
		if(issue_titleObj != null)
			issue_title = issue_titleObj.toString();

		Object descriptionObj = map.get("description");
		if(descriptionObj != null)
			description = descriptionObj.toString();

		Object attachmentObj = map.get("attachment");
		if(attachmentObj != null)
			attachment = attachmentObj.toString();

		Object due_dateObj = map.get("due_date");
		if(due_dateObj != null)
			due_date = new Date(due_dateObj.toString());

		Object priorityObj = map.get("priority");
		if(priorityObj != null)
			priority = priorityObj.toString();

		Object statusObj = map.get("status");
		if(statusObj != null)
			status = statusObj.toString();

		Object list_idObj = map.get("list_id");
		if(list_idObj != null)
			list_id = list_idObj.toString();

		extra_data = (Map<String, Object>) map.get("extra_data");
	}

	public void convertPrimaryMapToResource(Map<String, Object> map) {
	}

	public void convertTypeUnsafePrimaryMapToResource(Map<String, Object> map) {
	}

	public String getId() {
		return id;
	}

	public String getIdEx() {
		return id != null ? id : "";
	}

	public void setId(String id) {
		this.id = id;
	}

	public void unSetId() {
		this.id = null;
	}

	public boolean validateId(boolean add) throws ApplicationException {
		if(add && id == null)
			throw new ApplicationException(ExceptionSeverity.ERROR, "Requierd validation Failed[id]");
		return id != null;
	}

	public String getG_created_by_id() {
		return g_created_by_id;
	}

	public String getG_created_by_idEx() {
		return g_created_by_id != null ? g_created_by_id : "";
	}

	public void setG_created_by_id(String g_created_by_id) {
		this.g_created_by_id = g_created_by_id;
	}

	public void unSetG_created_by_id() {
		this.g_created_by_id = null;
	}

	public String getG_created_by_name() {
		return g_created_by_name;
	}

	public String getG_created_by_nameEx() {
		return g_created_by_name != null ? g_created_by_name : "";
	}

	public void setG_created_by_name(String g_created_by_name) {
		this.g_created_by_name = g_created_by_name;
	}

	public void unSetG_created_by_name() {
		this.g_created_by_name = null;
	}

	public String getG_modified_by_id() {
		return g_modified_by_id;
	}

	public String getG_modified_by_idEx() {
		return g_modified_by_id != null ? g_modified_by_id : "";
	}

	public void setG_modified_by_id(String g_modified_by_id) {
		this.g_modified_by_id = g_modified_by_id;
	}

	public void unSetG_modified_by_id() {
		this.g_modified_by_id = null;
	}

	public String getG_modified_by_name() {
		return g_modified_by_name;
	}

	public String getG_modified_by_nameEx() {
		return g_modified_by_name != null ? g_modified_by_name : "";
	}

	public void setG_modified_by_name(String g_modified_by_name) {
		this.g_modified_by_name = g_modified_by_name;
	}

	public void unSetG_modified_by_name() {
		this.g_modified_by_name = null;
	}

	public Long getG_creation_time() {
		return g_creation_time;
	}

	public long getG_creation_timeEx() {
		return g_creation_time != null ? g_creation_time : 0L;
	}

	public void setG_creation_time(long g_creation_time) {
		this.g_creation_time = g_creation_time;
	}

	@JsonIgnore
	public void setG_creation_time(Long g_creation_time) {
		this.g_creation_time = g_creation_time;
	}

	public void unSetG_creation_time() {
		this.g_creation_time = null;
	}

	public Long getG_modify_time() {
		return g_modify_time;
	}

	public long getG_modify_timeEx() {
		return g_modify_time != null ? g_modify_time : 0L;
	}

	public void setG_modify_time(long g_modify_time) {
		this.g_modify_time = g_modify_time;
	}

	@JsonIgnore
	public void setG_modify_time(Long g_modify_time) {
		this.g_modify_time = g_modify_time;
	}

	public void unSetG_modify_time() {
		this.g_modify_time = null;
	}

	public String getG_soft_delete() {
		return g_soft_delete != null ? g_soft_delete : "N";
	}

	public void setG_soft_delete(String g_soft_delete) {
		this.g_soft_delete = g_soft_delete;
	}

	public void unSetG_soft_delete() {
		this.g_soft_delete = "N";
	}

	public String getG_status() {
		return g_status;
	}

	public String getG_statusEx() {
		return g_status != null ? g_status : "";
	}

	public void setG_status(String g_status) {
		this.g_status = g_status;
	}

	public void unSetG_status() {
		this.g_status = null;
	}

	public String getArchived() {
		return archived != null ? archived : "N";
	}

	public void setArchived(String archived) {
		this.archived = archived;
	}

	public void unSetArchived() {
		this.archived = "N";
	}

	public Long getArchived_time() {
		return archived_time;
	}

	public long getArchived_timeEx() {
		return archived_time != null ? archived_time : 0L;
	}

	public void setArchived_time(long archived_time) {
		this.archived_time = archived_time;
	}

	@JsonIgnore
	public void setArchived_time(Long archived_time) {
		this.archived_time = archived_time;
	}

	public void unSetArchived_time() {
		this.archived_time = null;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public String getOwner_idEx() {
		return owner_id != null ? owner_id : "";
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public void unSetOwner_id() {
		this.owner_id = null;
	}

	public boolean validateOwner_id(boolean add) throws ApplicationException {
		if(add && owner_id == null)
			throw new ApplicationException(ExceptionSeverity.ERROR, "Requierd validation Failed[owner_id]");
		return owner_id != null;
	}

	public String getProject_id() {
		return project_id;
	}

	public String getProject_idEx() {
		return project_id != null ? project_id : "";
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public void unSetProject_id() {
		this.project_id = null;
	}

	public boolean validateProject_id(boolean add) throws ApplicationException {
		if(add && project_id == null)
			throw new ApplicationException(ExceptionSeverity.ERROR, "Requierd validation Failed[project_id]");
		return project_id != null;
	}

	public String getIssue_title() {
		return issue_title;
	}

	public String getIssue_titleEx() {
		return issue_title != null ? issue_title : "";
	}

	public void setIssue_title(String issue_title) {
		this.issue_title = issue_title;
	}

	public void unSetIssue_title() {
		this.issue_title = null;
	}

	public boolean validateIssue_title(boolean add) throws ApplicationException {
		if(add && issue_title == null)
			throw new ApplicationException(ExceptionSeverity.ERROR, "Requierd validation Failed[issue_title]");
		return issue_title != null;
	}

	public String getDescription() {
		return description;
	}

	public String getDescriptionEx() {
		return description != null ? description : "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void unSetDescription() {
		this.description = null;
	}

	public String getAttachment() {
		return attachment;
	}

	public String getAttachmentEx() {
		return attachment != null ? attachment : "";
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public void unSetAttachment() {
		this.attachment = null;
	}

	public Date getDue_date() {
		return due_date;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public void unSetDue_date() {
		this.due_date = null;
	}

	public String getPriority() {
		return priority;
	}

	public String getPriorityEx() {
		return priority != null ? priority : "";
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void unSetPriority() {
		this.priority = null;
	}

	public String getStatus() {
		return status;
	}

	public String getStatusEx() {
		return status != null ? status : "";
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void unSetStatus() {
		this.status = null;
	}

	public String getList_id() {
		return list_id;
	}

	public String getList_idEx() {
		return list_id != null ? list_id : "";
	}

	public void setList_id(String list_id) {
		this.list_id = list_id;
	}

	public void unSetList_id() {
		this.list_id = null;
	}

	public boolean validateList_id(boolean add) throws ApplicationException {
		if(add && list_id == null)
			throw new ApplicationException(ExceptionSeverity.ERROR, "Requierd validation Failed[list_id]");
		return list_id != null;
	}

	public Map<String, Object> getExtra_data() {
		return extra_data;
	}

	public Object getExtra_data(String key) {
		return extra_data == null ? null : extra_data.get(key);
	}

	public void setExtra_data(Map<String, Object> extra_data) {
		this.extra_data = extra_data;
	}

	public void setExtra_data(String key, Object value) {
		if(extra_data == null)
			extra_data = new HashMap<String, Object>();
		extra_data.put(key, value);
	}

	public void unSetExtra_data() {
		this.extra_data = null;
	}
	public String getCluster() {
		return "rasp_db";
	}
	public String getClusterType() {
		return "REPLICATED";
	}
	public  Class<?> getResultClass() {return IssueResult.class;};
	public  Class<?> getMessageClass() {return IssueMessage.class;};
	public  Class<?> getHelperClass() {return IssueHelper.class;};
	public  Class<?> getServiceClass() {return IssueService.class;};
}