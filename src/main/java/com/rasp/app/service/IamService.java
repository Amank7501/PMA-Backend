package com.rasp.app.service;

import com.rasp.app.helper.RoleUserResInstanceHelper;
import com.rasp.app.resource.RoleUserResInstance;
import com.rasp.app.resource.UserResource;
import org.olap4j.impl.ArrayMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import platform.db.Expression;
import platform.db.REL_OP;
import platform.helper.BaseHelper;
import platform.resource.BaseResource;
import platform.util.ApplicationException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class IamService {

    String accessToken=null;

    @Value("${spring.security.oauth2.client.provider.keycloak.clientId}")
    private  String clientId;

    @Value("${spring.security.oauth2.client.provider.keycloak.clientSecret}")
    private  String clientSecret; // Replace with your actual secret

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private  String keycloakTokenUrl;

    @Value("${spring.security.oauth2.client.provider.keycloak.keycloakUrl}")
    private  String keycloakUrl;

    @Value("${authentication-type:auth-code}")
    private String authenticationType;

    @Value("${ResourcePack}")
    private String resourcePackage;

    @Value("${HelperPack}")
    private String helperPackage;


    public ResponseEntity<?> addClientRole(String roleName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 🔹 Step 1: Get Admin Access Token using Client Credentials
        MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
        tokenRequestBody.add("grant_type", "client_credentials");
        tokenRequestBody.add("client_id", clientId);
        tokenRequestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                keycloakTokenUrl,
                HttpMethod.POST,
                tokenRequest,
                Map.class
        );

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 🔹 Step 2: Get the Client ID (UUID) from Keycloak
        HttpHeaders clientHeaders = new HttpHeaders();
        clientHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> clientRequest = new HttpEntity<>(clientHeaders);

        ResponseEntity<List> clientResponse = restTemplate.exchange(
                keycloakUrl+"/clients",
                HttpMethod.GET,
                clientRequest,
                List.class
        );

        if (!clientResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(clientResponse.getStatusCode()).body("Failed to fetch clients");
        }

        List<Map<String, Object>> clients = clientResponse.getBody();
        String clientUUID = clients.stream()
                .filter(client -> clientId.equals(client.get("clientId")))
                .map(client -> (String) client.get("id"))
                .findFirst()
                .orElse(null);

        if (clientUUID == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        // 🔹 Step 3: Create New Role for Client using Client UUID
        HttpHeaders roleHeaders = new HttpHeaders();
        roleHeaders.setContentType(MediaType.APPLICATION_JSON);
        roleHeaders.setBearerAuth(accessToken);

        Map<String, Object> rolePayload = new HashMap<>();
        rolePayload.put("name", roleName);
        rolePayload.put("description", "Auto-created client role");

        HttpEntity<Map<String, Object>> roleRequest = new HttpEntity<>(rolePayload, roleHeaders);
        ResponseEntity<String> roleResponse = restTemplate.exchange(
                keycloakUrl+"/clients/" + clientUUID + "/roles",
                HttpMethod.POST,
                roleRequest,
                String.class
        );

        if (!roleResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(roleResponse.getStatusCode()).body("Failed to create client role");
        }

        return ResponseEntity.ok("Client role '" + roleName + "' added successfully to client '" + clientId + "'");

    }


public ResponseEntity<?> addUser(UserResource userResource) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, ApplicationException {
Map<String,Object> resourceMap= userResource.getResourceMap();
Map<String,Object> authMap=userResource.getAuthMap();
String resource=userResource.getResourceName();
    String myClass= resourcePackage+"."+resource;
    Class<BaseResource> clazz = (Class<BaseResource>) Class.forName(myClass);


    BaseResource baseResource= clazz.getDeclaredConstructor().newInstance();
    baseResource.convertMapToResource(resourceMap);

    String myHelper= helperPackage+"."+resource+"Helper";
    Class<BaseHelper> clazz2=(Class<BaseHelper>) Class.forName(myHelper) ;
    BaseHelper baseHelper=clazz2.getDeclaredConstructor().newInstance();
    baseHelper.add(baseResource);


    System.out.println( baseResource.getId()+"1111111111111111222222222222222");

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // Step 1: Get Admin Access Token using Client Credentials Grant
    MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
    tokenRequestBody.add("grant_type", "client_credentials");
    tokenRequestBody.add("client_id", clientId);
    tokenRequestBody.add("client_secret", clientSecret);

    HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
    ResponseEntity<Map> tokenResponse = restTemplate.exchange(
            keycloakTokenUrl,
            HttpMethod.POST,
            tokenRequest,
            Map.class
    );

    if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
    }

    String accessToken = (String) tokenResponse.getBody().get("access_token");

    // Step 2: Create New User in Keycloak
    HttpHeaders userHeaders = new HttpHeaders();
    userHeaders.setContentType(MediaType.APPLICATION_JSON);
    userHeaders.setBearerAuth(accessToken);
    String user= (String) authMap.get("userName");
    String email=  (String) authMap.get("email");
    String firstName= (String) authMap.get("firstName");
    String lastName=(String) authMap.get("lastName");
    String password=(String) authMap.get("password");

    Map<String, Object> userPayload = new HashMap<>();
    userPayload.put("username", user);
    userPayload.put("email",email );
    userPayload.put("enabled", true);
    userPayload.put("emailVerified", true);
    userPayload.put("firstName", firstName);
    userPayload.put("lastName", lastName);

    Map<String, Object> credentials = new HashMap<>();
    credentials.put("type", "password");
    credentials.put("value", password);
    credentials.put("temporary", false);

    // Custom Attributes
    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put("custom_id", List.of( baseResource.getId())); // Correct: List of Strings

    userPayload.put("attributes", attributes); // or any value you want
    userPayload.put("credentials", List.of(credentials));

    HttpEntity<Map<String, Object>> userRequest = new HttpEntity<>(userPayload, userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            keycloakUrl+"/users",
            HttpMethod.POST,
            userRequest,
            String.class
    );



    if (!userResponse.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.status(userResponse.getStatusCode()).body("Failed to create user");
    }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        if(isScoped){
//
//            BaseResource baseResource1=   ResourceRoleHelper.getInstance().getByField(ResourceRole.FIELD_RESOURCE_NAME,resource);
//            ResourceRole resRoleType=(ResourceRole)baseResource1;
//            if(resRoleType==null){
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource to role access is not there");
//            }
//            if(resRoleType!=null) {
//                addUserResourceRole(resRoleType.getRole(), newUsername, resource, baseResource.getId());
//            }
//        }
    return  ResponseEntity.ok("User created successfully");


}




    public ResponseEntity<?> addUserResourceRole(Map<String,String> map) {

       String roleName=(String) map.get("role");
        String userName=(String) map.get("userName");
        String resourceType=(String) map.get("resourceType");
        String resourceId=(String) map.get("resourceId");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 🔹 Step 1: Get Admin Access Token
        MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
        tokenRequestBody.add("grant_type", "client_credentials");
        tokenRequestBody.add("client_id", clientId);
        tokenRequestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                keycloakTokenUrl,
                HttpMethod.POST,
                tokenRequest,
                Map.class
        );

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 🔹 Step 2: Get User ID
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);

        ResponseEntity<List> userResponse = restTemplate.exchange(
                keycloakUrl+"/users?username=" + userName,
                HttpMethod.GET,
                userRequest,
                List.class
        );



        if (userResponse.getBody().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String userId = ((Map<String, Object>) userResponse.getBody().get(0)).get("id").toString();

        String raspUserId=null;
        List<Map<String, Object>> users = userResponse.getBody();
        Map<String,Object> firstUser= users.get(0);

        Map<String,Object> attributes= (Map<String, Object>) firstUser.get("attributes");
        if(attributes!=null && attributes.containsKey("custom_id")){
            List<String> raspUserIds= (List<String>) attributes.get("custom_id");
            raspUserId=raspUserIds!=null && !raspUserIds.isEmpty() ? raspUserIds.get(0) :null;
        }


        // 🔹 Step 3: Get Client ID
        ResponseEntity<List> clientResponse = restTemplate.exchange(
                keycloakUrl+"/clients?clientId=" + clientId,
                HttpMethod.GET,
                userRequest,
                List.class
        );

        if (clientResponse.getBody().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        String clientUuid = ((Map<String, Object>) clientResponse.getBody().get(0)).get("id").toString();

        // 🔹 Step 4: Get Client Role
        ResponseEntity<List> roleResponse = restTemplate.exchange(
                keycloakUrl+"/clients/" + clientUuid + "/roles",
                HttpMethod.GET,
                userRequest,
                List.class
        );

        List<Map<String, Object>> roles = roleResponse.getBody();
        Map<String, Object> role = roles.stream()
                .filter(r -> roleName.equals(r.get("name")))
                .findFirst()
                .orElse(null);

        if (role == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");

//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");

            // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
        }
        // 🔹 Step 5: Assign Role (Content-Type must be JSON)
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setBearerAuth(accessToken);
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

        // 🔹 Step 5: Assign Client Role to User
        HttpEntity<List<Map<String, Object>>> assignRoleRequest = new HttpEntity<>(List.of(role), jsonHeaders);
        ResponseEntity<String> assignRoleResponse = restTemplate.exchange(
                keycloakUrl+"/users/" + userId + "/role-mappings/clients/" + clientUuid,
                HttpMethod.POST,
                assignRoleRequest,
                String.class
        );

        if (!assignRoleResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(assignRoleResponse.getStatusCode()).body("Failed to assign role");
        }

        RoleUserResInstance roleUserResInstance=new RoleUserResInstance();

        roleUserResInstance.setUser_name(userName);
        roleUserResInstance.setKeycloak_user_id(userId);
        roleUserResInstance.setRole_name(roleName);
        roleUserResInstance.setResource_name(resourceType);
        if(resourceId!=null) {
            roleUserResInstance.setResource_id(resourceId);
        }
        roleUserResInstance.setRasp_user_id(raspUserId);
        RoleUserResInstanceHelper.getInstance().add_Nocatch(roleUserResInstance);
        if(resourceId!=null) {
            return ResponseEntity.ok("Role '" + roleName + "' assigned successfully to user '" + userName + "'"+"' and to this instance"+resourceId);
        }
        return ResponseEntity.ok("Role '" + roleName + "' assigned successfully to user '" + userName + "'");
    }


//    public ResponseEntity<?> addUserResourceRole(String roleName, String userName,String resourceType,String resourceId) {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            // 🔹 Step 1: Get Admin Access Token
//            MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
//            tokenRequestBody.add("grant_type", "client_credentials");
//            tokenRequestBody.add("client_id", clientId);
//            tokenRequestBody.add("client_secret", clientSecret);
//
//            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
//            ResponseEntity<Map> tokenResponse = restTemplate.exchange(
//                    "http://localhost:8080/realms/new/protocol/openid-connect/token",
//                    HttpMethod.POST,
//                    tokenRequest,
//                    Map.class
//            );
//
//            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
//            }
//
//            String accessToken = (String) tokenResponse.getBody().get("access_token");
//
//            // 🔹 Step 2: Get User ID
//            HttpHeaders authHeaders = new HttpHeaders();
//            authHeaders.setBearerAuth(accessToken);
//            HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);
//
//            ResponseEntity<List> userResponse = restTemplate.exchange(
//                    "http://localhost:8080/admin/realms/new/users?username=" + userName,
//                    HttpMethod.GET,
//                    userRequest,
//                    List.class
//            );
//
//
//
//            if (userResponse.getBody().isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            }
//
//            String userId = ((Map<String, Object>) userResponse.getBody().get(0)).get("id").toString();
//
//            String raspUserId=null;
//        List<Map<String, Object>> users = userResponse.getBody();
//    Map<String,Object> firstUser= users.get(0);
//
//          Map<String,Object> attributes= (Map<String, Object>) firstUser.get("attributes");
//          if(attributes!=null && attributes.containsKey("custom_id")){
//         List<String> raspUserIds= (List<String>) attributes.get("custom_id");
//              raspUserId=raspUserIds!=null && !raspUserIds.isEmpty() ? raspUserIds.get(0) :null;
//          }
//
//
//            // 🔹 Step 3: Get Client ID
//            ResponseEntity<List> clientResponse = restTemplate.exchange(
//                    "http://localhost:8080/admin/realms/new/clients?clientId=" + clientId,
//                    HttpMethod.GET,
//                    userRequest,
//                    List.class
//            );
//
//            if (clientResponse.getBody().isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
//            }
//
//            String clientUuid = ((Map<String, Object>) clientResponse.getBody().get(0)).get("id").toString();
//
//            // 🔹 Step 4: Get Client Role
//            ResponseEntity<List> roleResponse = restTemplate.exchange(
//                    "http://localhost:8080/admin/realms/new/clients/" + clientUuid + "/roles",
//                    HttpMethod.GET,
//                    userRequest,
//                    List.class
//            );
//
//            List<Map<String, Object>> roles = roleResponse.getBody();
//            Map<String, Object> role = roles.stream()
//                    .filter(r -> roleName.equals(r.get("name")))
//                    .findFirst()
//                    .orElse(null);
//
//            if (role == null) {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
//
////                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
//
//               // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
//            }
//            // 🔹 Step 5: Assign Role (Content-Type must be JSON)
//            HttpHeaders jsonHeaders = new HttpHeaders();
//            jsonHeaders.setBearerAuth(accessToken);
//            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            // 🔹 Step 5: Assign Client Role to User
//            HttpEntity<List<Map<String, Object>>> assignRoleRequest = new HttpEntity<>(List.of(role), jsonHeaders);
//            ResponseEntity<String> assignRoleResponse = restTemplate.exchange(
//                    "http://localhost:8080/admin/realms/new/users/" + userId + "/role-mappings/clients/" + clientUuid,
//                    HttpMethod.POST,
//                    assignRoleRequest,
//                    String.class
//            );
//
//            if (!assignRoleResponse.getStatusCode().is2xxSuccessful()) {
//                return ResponseEntity.status(assignRoleResponse.getStatusCode()).body("Failed to assign role");
//            }
//
//        RoleUserResInstance roleUserResInstance=new RoleUserResInstance();
//
//        roleUserResInstance.setUser_name(userName);
//        roleUserResInstance.setKeycloak_user_id(userId);
//        roleUserResInstance.setRole_name(roleName);
//        roleUserResInstance.setResource_name(resourceType);
//        if(resourceId!=null) {
//            roleUserResInstance.setResource_id(resourceId);
//        }
//        roleUserResInstance.setRasp_user_id(raspUserId);
//        RoleUserResInstanceHelper.getInstance().add_Nocatch(roleUserResInstance);
//        if(resourceId!=null) {
//            return ResponseEntity.ok("Role '" + roleName + "' assigned successfully to user '" + userName + "'"+"' and to this instance"+resourceId);
//        }
//            return ResponseEntity.ok("Role '" + roleName + "' assigned successfully to user '" + userName + "'");
//        }

//    public ResponseEntity<?> addUserRoleMapping(String roleName, String userName) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        // 🔹 Step 1: Get Admin Access Token
//        MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
//        tokenRequestBody.add("grant_type", "client_credentials");
//        tokenRequestBody.add("client_id", clientId);
//        tokenRequestBody.add("client_secret", clientSecret);
//
//        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
//        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
//                keycloakTokenUrl,
//                HttpMethod.POST,
//                tokenRequest,
//                Map.class
//        );
//
//        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
//        }
//
//        String accessToken = (String) tokenResponse.getBody().get("access_token");
//
//        // 🔹 Step 2: Get User ID
//        HttpHeaders authHeaders = new HttpHeaders();
//        authHeaders.setBearerAuth(accessToken);
//        HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);
//
//        ResponseEntity<List> userResponse = restTemplate.exchange(
//                keycloakUrl+"/users?username=" + userName,
//                HttpMethod.GET,
//                userRequest,
//                List.class
//        );
//
//
//
//        if (userResponse.getBody().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//
//        String userId = ((Map<String, Object>) userResponse.getBody().get(0)).get("id").toString();
//
//        String raspUserId=null;
//        List<Map<String, Object>> users = userResponse.getBody();
//        Map<String,Object> firstUser= users.get(0);
//
//        Map<String,Object> attributes= (Map<String, Object>) firstUser.get("attributes");
//        if(attributes!=null && attributes.containsKey("custom_id")){
//            List<String> raspUserIds= (List<String>) attributes.get("custom_id");
//            raspUserId=raspUserIds!=null && !raspUserIds.isEmpty() ? raspUserIds.get(0) :null;
//        }
//
//
//        // 🔹 Step 3: Get Client ID
//        ResponseEntity<List> clientResponse = restTemplate.exchange(
//                keycloakUrl+"/clients?clientId=" + clientId,
//                HttpMethod.GET,
//                userRequest,
//                List.class
//        );
//
//        if (clientResponse.getBody().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
//        }
//
//        String clientUuid = ((Map<String, Object>) clientResponse.getBody().get(0)).get("id").toString();
//
//        // 🔹 Step 4: Get Client Role
//        ResponseEntity<List> roleResponse = restTemplate.exchange(
//                keycloakUrl+"/clients/" + clientUuid + "/roles",
//                HttpMethod.GET,
//                userRequest,
//                List.class
//        );
//
//        List<Map<String, Object>> roles = roleResponse.getBody();
//        Map<String, Object> role = roles.stream()
//                .filter(r -> roleName.equals(r.get("name")))
//                .findFirst()
//                .orElse(null);
//
//        if (role == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
//
////                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
//
//            // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
//        }
//        // 🔹 Step 5: Assign Role (Content-Type must be JSON)
//        HttpHeaders jsonHeaders = new HttpHeaders();
//        jsonHeaders.setBearerAuth(accessToken);
//        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        // 🔹 Step 5: Assign Client Role to User
//        HttpEntity<List<Map<String, Object>>> assignRoleRequest = new HttpEntity<>(List.of(role), jsonHeaders);
//        ResponseEntity<String> assignRoleResponse = restTemplate.exchange(
//                keycloakUrl+"/users/" + userId + "/role-mappings/clients/" + clientUuid,
//                HttpMethod.POST,
//                assignRoleRequest,
//                String.class
//        );
//
//        if (!assignRoleResponse.getStatusCode().is2xxSuccessful()) {
//            return ResponseEntity.status(assignRoleResponse.getStatusCode()).body("Failed to assign role");
//        }
//
//        return ResponseEntity.ok("Role '" + roleName + "' assigned successfully to user '" + userName + "'");
//    }
public ResponseEntity<?> addUserRoleMapping(Map<String,Object> map) {
        String roleName= (String) map.get("role");
        String userName=(String) map.get("userName");
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // 🔹 Step 1: Get Admin Access Token
    MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
    tokenRequestBody.add("grant_type", "client_credentials");
    tokenRequestBody.add("client_id", clientId);
    tokenRequestBody.add("client_secret", clientSecret);

    HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
    ResponseEntity<Map> tokenResponse = restTemplate.exchange(
            keycloakTokenUrl,
            HttpMethod.POST,
            tokenRequest,
            Map.class
    );

    if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
    }

    String accessToken = (String) tokenResponse.getBody().get("access_token");

    // 🔹 Step 2: Get User ID
    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setBearerAuth(accessToken);
    HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);

    ResponseEntity<List> userResponse = restTemplate.exchange(
            keycloakUrl+"/users?username=" + userName,
            HttpMethod.GET,
            userRequest,
            List.class
    );



    if (userResponse.getBody().isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    String userId = ((Map<String, Object>) userResponse.getBody().get(0)).get("id").toString();

    String raspUserId=null;
    List<Map<String, Object>> users = userResponse.getBody();
    Map<String,Object> firstUser= users.get(0);

    Map<String,Object> attributes= (Map<String, Object>) firstUser.get("attributes");
    if(attributes!=null && attributes.containsKey("custom_id")){
        List<String> raspUserIds= (List<String>) attributes.get("custom_id");
        raspUserId=raspUserIds!=null && !raspUserIds.isEmpty() ? raspUserIds.get(0) :null;
    }


    // 🔹 Step 3: Get Client ID
    ResponseEntity<List> clientResponse = restTemplate.exchange(
            keycloakUrl+"/clients?clientId=" + clientId,
            HttpMethod.GET,
            userRequest,
            List.class
    );

    if (clientResponse.getBody().isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }

    String clientUuid = ((Map<String, Object>) clientResponse.getBody().get(0)).get("id").toString();

    // 🔹 Step 4: Get Client Role
    ResponseEntity<List> roleResponse = restTemplate.exchange(
            keycloakUrl+"/clients/" + clientUuid + "/roles",
            HttpMethod.GET,
            userRequest,
            List.class
    );

    List<Map<String, Object>> roles = roleResponse.getBody();
    Map<String, Object> role = roles.stream()
            .filter(r -> roleName.equals(r.get("name")))
            .findFirst()
            .orElse(null);

    if (role == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");

//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");

        // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
    }
    // 🔹 Step 5: Assign Role (Content-Type must be JSON)
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setBearerAuth(accessToken);
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

    // 🔹 Step 5: Assign Client Role to User
    HttpEntity<List<Map<String, Object>>> assignRoleRequest = new HttpEntity<>(List.of(role), jsonHeaders);
    ResponseEntity<String> assignRoleResponse = restTemplate.exchange(
            keycloakUrl+"/users/" + userId + "/role-mappings/clients/" + clientUuid,
            HttpMethod.POST,
            assignRoleRequest,
            String.class
    );

    if (!assignRoleResponse.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.status(assignRoleResponse.getStatusCode()).body("Failed to assign role");
    }

    return ResponseEntity.ok("Role '" + roleName + "' assigned successfully to user '" + userName + "'");
}



public ResponseEntity<?> getAllRole() {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // 🔹 Step 1: Get Admin Access Token
    MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
    tokenRequestBody.add("grant_type", "client_credentials");
    tokenRequestBody.add("client_id", clientId);
    tokenRequestBody.add("client_secret", clientSecret);
    HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
    ResponseEntity<Map> tokenResponse = restTemplate.exchange(
            keycloakTokenUrl,
            HttpMethod.POST,
            tokenRequest,
            Map.class
    );

    if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
    }

    String accessToken = (String) tokenResponse.getBody().get("access_token");

    // 🔹 Step 2: Get User ID
    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setBearerAuth(accessToken);
    HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);

    // 🔹 Step 3: Get Client ID
    ResponseEntity<List> clientResponse = restTemplate.exchange(
            keycloakUrl+"/clients?clientId=" + clientId,
            HttpMethod.GET,
            userRequest,
            List.class
    );

    if (clientResponse.getBody().isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }

    String clientUuid = ((Map<String, Object>) clientResponse.getBody().get(0)).get("id").toString();

    // 🔹 Step 4: Get Client Role
    ResponseEntity<List> roleResponse = restTemplate.exchange(
            keycloakUrl+"/clients/" + clientUuid + "/roles",
            HttpMethod.GET,
            userRequest,
            List.class
    );

    List<Map<String, Object>> roles = roleResponse.getBody();

    List<String> roleNames = roles.stream()
            .map(r -> r.get("name").toString())
            .toList();


    return   ResponseEntity.ok(roleNames);

}

public ResponseEntity<?> getUsersByRole(String roleName) {
    RestTemplate restTemplate = new RestTemplate();

    // 🔹 Step 1: Get Admin Access Token
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
    tokenRequestBody.add("grant_type", "client_credentials");
    tokenRequestBody.add("client_id", clientId);
    tokenRequestBody.add("client_secret", clientSecret);

    HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
    ResponseEntity<Map> tokenResponse = restTemplate.exchange(
            keycloakTokenUrl,
            HttpMethod.POST,
            tokenRequest,
            Map.class
    );

    if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
    }

    String accessToken = (String) tokenResponse.getBody().get("access_token");

    // 🔹 Step 2: Get Client ID
    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setBearerAuth(accessToken);
    HttpEntity<Void> clientRequest = new HttpEntity<>(authHeaders);

    ResponseEntity<List> clientResponse = restTemplate.exchange(
            keycloakUrl + "/clients?clientId=" + clientId,
            HttpMethod.GET,
            clientRequest,
            List.class
    );

    if (clientResponse.getBody().isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }

    String clientUuid = ((Map<String, Object>) clientResponse.getBody().get(0)).get("id").toString();

    // 🔹 Step 3: Get Users by Role
    ResponseEntity<List> usersResponse = restTemplate.exchange(
            keycloakUrl + "/clients/" + clientUuid + "/roles/" + roleName + "/users",
            HttpMethod.GET,
            clientRequest,
            List.class
    );

    List<Map<String, Object>> users = usersResponse.getBody();

    // Extract usernames or custom attributes
    List<Map<String, Object>> userList = users.stream()
            .map(u -> Map.of(
                    "id", u.get("id"),
                    "username", u.get("username"),
                    "email", u.get("email"),
                    "attributes", u.get("attributes")
            ))
            .toList();

    return ResponseEntity.ok(userList);
}

    public ResponseEntity<?> getUserRoleResource(String projectId) {
        Expression e = new Expression(RoleUserResInstance.FIELD_RESOURCE_ID, REL_OP.EQ, projectId);
      BaseResource[] baseResources=  RoleUserResInstanceHelper.getInstance().getByExpression(e);
        List<Map<String,Object>> maps = new ArrayList<>(List.of());
       // Map<String,Object> roleUserResInstances=new ArrayMap<String,Object>();
        for(BaseResource b:baseResources){
            RoleUserResInstance b1 = (RoleUserResInstance) b;
            Map<String,Object> roleUserResInstances=new ArrayMap<String,Object>();
         String userName= b1.getUser_name();
            String userId=  b1.getRasp_user_id();
            String role= b1.getRole_name();
            roleUserResInstances.put("userName",userName);
            roleUserResInstances.put("userId",userId);
            roleUserResInstances.put("role",role);
            maps.add(roleUserResInstances);


        }
        return ResponseEntity.ok(maps);


    }

    public ResponseEntity<?> getAllUsersWithRoles() {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Get admin token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
        tokenRequestBody.add("grant_type", "client_credentials");
        tokenRequestBody.add("client_id", clientId);
        tokenRequestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                keycloakTokenUrl,
                HttpMethod.POST,
                tokenRequest,
                Map.class
        );

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. Fetch all users
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);

        ResponseEntity<List> usersResponse = restTemplate.exchange(
                keycloakUrl + "/users",
                HttpMethod.GET,
                entity,
                List.class
        );

        if (!usersResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(usersResponse.getStatusCode()).body("Failed to fetch users");
        }

        List<Map<String, Object>> users = usersResponse.getBody();

        // 3. Fetch roles for each user (realm + client)
        for (Map<String, Object> user : users) {
            String userId = (String) user.get("id");

            ResponseEntity<Map> rolesResponse = restTemplate.exchange(
                    keycloakUrl + "/users/" + userId + "/role-mappings",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> roleMappings = rolesResponse.getBody();

            // Extract realm roles
            List<Map<String, Object>> realmRoles = roleMappings.get("realmMappings") != null
                    ? (List<Map<String, Object>>) roleMappings.get("realmMappings")
                    : List.of();

            // Extract client roles
            Map<String, Object> clientMappings = roleMappings.get("clientMappings") != null
                    ? (Map<String, Object>) roleMappings.get("clientMappings")
                    : Map.of();

            user.put("realmRoles", realmRoles);
            user.put("clientRoles", clientMappings);
        }

        return ResponseEntity.ok(users);
    }


    private List<Map<String, Object>> getRoleRepresentations(List<String> roleNames, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> allRolesResponse = restTemplate.exchange(
                keycloakUrl + "/roles",
                HttpMethod.GET,
                entity,
                List.class
        );

        List<Map<String, Object>> allRoles = allRolesResponse.getBody();
        List<Map<String, Object>> matchedRoles = new ArrayList<>();

        for (Map<String, Object> role : allRoles) {
            String roleName = (String) role.get("name");
            if (roleNames.contains(roleName)) {
                matchedRoles.add(role);
            }
        }

        return matchedRoles;
    }

    public ResponseEntity<?> updateUser(String userId, Map<String, Object> updatedFields,
                                        List<String> rolesToAssign, List<String> rolesToRemove) {
        RestTemplate restTemplate = new RestTemplate();


        // 1. Get admin token (same as in addUser)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenRequestBody = new LinkedMultiValueMap<>();
        tokenRequestBody.add("grant_type", "client_credentials");
        tokenRequestBody.add("client_id", clientId);
        tokenRequestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                keycloakTokenUrl,
                HttpMethod.POST,
                tokenRequest,
                Map.class
        );

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");


        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1️⃣ Update user basic details
        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updatedFields, headers);
        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                keycloakUrl + "/users/" + userId,
                HttpMethod.PUT,
                updateRequest,
                Void.class
        );

        if (!updateResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(updateResponse.getStatusCode())
                    .body("Failed to update user");
        }

        // 2️⃣ Handle roles
        HttpEntity<List<Map<String, Object>>> roleRequest;

        // 2a: Assign roles
        if (rolesToAssign != null && !rolesToAssign.isEmpty()) {
            List<Map<String, Object>> roleReps = getRoleRepresentations(rolesToAssign, accessToken);
            roleRequest = new HttpEntity<>(roleReps, headers);

            restTemplate.exchange(
                    keycloakUrl + "/users/" + userId + "/role-mappings/realm",
                    HttpMethod.POST,
                    roleRequest,
                    Void.class
            );
        }

        // 2b: Unassign roles
        if (rolesToRemove != null && !rolesToRemove.isEmpty()) {
            List<Map<String, Object>> roleReps = getRoleRepresentations(rolesToRemove, accessToken);
            roleRequest = new HttpEntity<>(roleReps, headers);

            restTemplate.exchange(
                    keycloakUrl + "/users/" + userId + "/role-mappings/realm",
                    HttpMethod.DELETE,
                    roleRequest,
                    Void.class
            );
        }

        return ResponseEntity.ok("User updated successfully");
    }





}
