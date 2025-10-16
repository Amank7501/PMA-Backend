package com.rasp.app.controller;

import com.rasp.app.resource.UpdateUserRequest;
import com.rasp.app.resource.UserResource;
import com.rasp.app.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import platform.resource.BaseResource;
import platform.util.ApplicationException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class IamController {
     @Autowired
     IamService iamService;

    @PostMapping("/add-client-role")//add_role
    public ResponseEntity<?> addClientRole(@RequestParam String roleName) {
        return iamService.addClientRole(roleName);
    }


    @PostMapping("/add_user")
    public ResponseEntity<?> registerUser(@RequestBody UserResource userResource) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ApplicationException {

        return iamService.addUser(userResource);

    }

//    @PostMapping("/user_resource_role")
//    public ResponseEntity<?> userResourceRole(@RequestParam String role,@RequestParam String userName,@RequestParam String resourceType,@RequestParam String resourceId){
//        return iamService.addUserResourceRole(role,userName,resourceType,resourceId);
//    }
@PostMapping("/user_resource_role")
public ResponseEntity<?> userResourceRole(@RequestBody Map<String, String> map){
    return iamService.addUserResourceRole(map);
}
//    @PostMapping("/user_role_mapping")
//    public ResponseEntity<?> userRoleMapping(@RequestParam String role,@RequestParam String userName){
//        return iamService.addUserRoleMapping(role,userName);
//    }
@PostMapping("/user_role_mapping")
public ResponseEntity<?> userRoleMapping(@RequestBody Map<String,Object> map){
    return iamService.addUserRoleMapping(map);
}
@GetMapping("/role")
public ResponseEntity<?> getAllRole() {
    return iamService.getAllRole();
}

    @GetMapping("/{roleName}/users")
    public ResponseEntity<?> getUsersByRole(@PathVariable String roleName) {
        try {
            return iamService.getUsersByRole(roleName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users for role '" + roleName + "': " + e.getMessage());
        }
    }

    @GetMapping("/user_resource_role")
    public ResponseEntity<?> userResourceRole(String projectId){
        return iamService.getUserRoleResource(projectId);
    }

    @GetMapping("/users-with-roles")
    public ResponseEntity<?> getAllUsersWithRoles() {
        return iamService.getAllUsersWithRoles();
    }

    // ✅ Update user details + roles
    @PutMapping("update-user/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest request
    ) {
        return iamService.updateUser(
                userId,
                request.getUpdatedFields(),
                request.getAssignRoles(),
                request.getRemoveRoles()
        );
    }

}
