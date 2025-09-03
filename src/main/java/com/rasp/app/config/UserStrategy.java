package com.rasp.app.config;

public class UserStrategy extends RoleStrategy {


    @Override
    public boolean hasAccess(String role, String action, String resourceType, String resourceId, String userId) throws Exception {
        if(resourceType.equalsIgnoreCase("project")||resourceType.equalsIgnoreCase("user_project_map")){
            if(action.equalsIgnoreCase("GET_BY_ID") ||  action.equalsIgnoreCase("add")|| action.equalsIgnoreCase("GET_USER_BY_PROJECT_ID")|| action.equalsIgnoreCase("GET_PROJECT_BY_USER_ID")){
                return true;
            }
        }
        return false;
    }
}
