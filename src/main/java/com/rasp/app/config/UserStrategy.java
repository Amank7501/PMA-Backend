package com.rasp.app.config;

public class UserStrategy extends RoleStrategy {


    @Override
    public boolean hasAccess(String role, String action, String resourceType, String resourceId, String userId) throws Exception {
        if(action.equalsIgnoreCase("add") && resourceType.equalsIgnoreCase("project")){
            return true;
        }
        return false;
    }
}
