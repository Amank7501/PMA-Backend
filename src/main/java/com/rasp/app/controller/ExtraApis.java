package com.rasp.app.controller;

import com.rasp.app.helper.UserProjectMapHelper;
import com.rasp.app.helper.UsersHelper;
import com.rasp.app.resource.Project;
import com.rasp.app.resource.UserProjectMap;
import com.rasp.app.resource.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import platform.db.Expression;
import platform.db.REL_OP;
import platform.resource.BaseResource;
import platform.util.ApplicationException;
import platform.util.ExceptionSeverity;
import platform.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api")
public class ExtraApis {
//    @GetMapping ("/extra_apis")
//    public ResponseEntity<List<Map<String, Object>>> getUserByProj(@RequestParam String pId ,@RequestParam String queryId){
//        Expression e = new Expression(UserProjectMap.FIELD_PROJECT_ID, REL_OP.EQ, pId);
//        BaseResource[] user_project = UserProjectMapHelper.getInstance().getByExpression(e);
//
//        List<Map<String,Object>> UserLists =new ArrayList<>();
//        for (BaseResource pr : user_project) {
//
//            UserProjectMap user_project_map = (UserProjectMap) pr;
//           String role= user_project_map.getProject_role();
//            Expression e2 = new Expression(Users.FIELD_ID, REL_OP.EQ, user_project_map.getUser_id());
//            BaseResource[] user_helper = UsersHelper.getInstance().getByExpression(e2);
//
//            if (user_helper != null && user_helper.length > 0) {
//                for (BaseResource u : user_helper) {
//                  Users users=  (Users) u;
//
//
//                    Map<String,Object> UserList = new HashMap<String,Object>();
//                    UserList.put("user_name", users.getUser_name());
//                    UserList.put("user_id",  users.getId());
//                    UserList.put("user_email", users.getUser_email());
//                    UserList.put("user_role",role);
//                    UserLists.add(UserList);
//                }
//            }
//        }
//        return ResponseEntity.ok(UserLists) ;
//
//    }

}
