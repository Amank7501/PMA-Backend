package com.rasp.app.config;

public class SuperAdminStrategy extends RoleStrategy {

    @Override
    public boolean hasAccess(String role, String action, String resourceType, String resourceId, String userId) throws Exception {
        if(resourceType.equalsIgnoreCase("add-client-role") ||resourceType.equalsIgnoreCase("role_resource_permission")){
           return true;
        }
        if(resourceType.equalsIgnoreCase("project")|| resourceType.equalsIgnoreCase("comments")|| resourceType.equalsIgnoreCase("issue")||resourceType.equalsIgnoreCase("list_res")||resourceType.equalsIgnoreCase("list")||resourceType.equalsIgnoreCase("users")||resourceType.equalsIgnoreCase("label_issue_map")||resourceType.equalsIgnoreCase("label")||resourceType.equalsIgnoreCase("issue_user_map")||resourceType.equalsIgnoreCase("user_project_map")){
            if(action.equalsIgnoreCase("GET_BY_ID") || action.equalsIgnoreCase("GET_ALL")|| action.equalsIgnoreCase("add")|| action.equalsIgnoreCase("GET_LABEL_BY_ISSUE_ID")|| action.equalsIgnoreCase("GET_USER_BY_ISSUE_ID")|| action.equalsIgnoreCase("GET_USER_BY_PROJECT_ID")|| action.equalsIgnoreCase("MODIFY")|| action.equalsIgnoreCase("GET_PROJECT_BY_USER_ID")){
                return true;
            }
        }
        return false;
    }
}
