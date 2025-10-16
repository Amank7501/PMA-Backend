package com.rasp.app.resource;

import java.util.List;
import java.util.Map;

public class UpdateUserRequest {
    private Map<String, Object> updatedFields;   // fields to update (firstName, email, etc.)
    private List<String> assignRoles;            // roles to assign
    private List<String> removeRoles;            // roles to remove

    // getters and setters
    public Map<String, Object> getUpdatedFields() {
        return updatedFields;
    }
    public void setUpdatedFields(Map<String, Object> updatedFields) {
        this.updatedFields = updatedFields;
    }

    public List<String> getAssignRoles() {
        return assignRoles;
    }
    public void setAssignRoles(List<String> assignRoles) {
        this.assignRoles = assignRoles;
    }

    public List<String> getRemoveRoles() {
        return removeRoles;
    }
    public void setRemoveRoles(List<String> removeRoles) {
        this.removeRoles = removeRoles;
    }
}
