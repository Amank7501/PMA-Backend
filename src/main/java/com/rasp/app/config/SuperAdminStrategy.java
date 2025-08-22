package com.rasp.app.config;

public class SuperAdminStrategy extends RoleStrategy {

    @Override
    public boolean hasAccess(String role, String action, String resourceType, String resourceId, String userId) throws Exception {
        if(resourceType.equalsIgnoreCase("add-client-role") ||resourceType.equalsIgnoreCase("role_resource_permission")){
           return true;
        }
        if(resourceType.equalsIgnoreCase("project")|| resourceType.equalsIgnoreCase("comments")|| resourceType.equalsIgnoreCase("issue")||resourceType.equalsIgnoreCase("project")||resourceType.equalsIgnoreCase("list")){
            if(action.equalsIgnoreCase("GET_BY_ID") || action.equalsIgnoreCase("GET_ALL")){
                return true;
            }
        }
        return false;
    }
}
