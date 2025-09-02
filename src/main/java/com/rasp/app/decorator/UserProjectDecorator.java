package com.rasp.app.decorator;

import com.rasp.app.helper.IssueHelper;
import com.rasp.app.helper.UserProjectMapHelper;
import com.rasp.app.resource.*;
import platform.db.Expression;
import platform.db.REL_OP;
import platform.decorator.BaseDecorator;
import platform.resource.BaseResource;
import platform.util.ApplicationException;
import platform.util.ExceptionSeverity;
import platform.util.Util;
import platform.webservice.BaseService;
import platform.webservice.ServletContext;

import java.util.ArrayList;
import java.util.Map;


public class UserProjectDecorator extends BaseDecorator {
    public UserProjectDecorator() {super(new UserProjectMap());}

    @Override
    public BaseResource[] getQuery(ServletContext ctx, String queryId, Map<String, Object> map, BaseService service) throws ApplicationException {

        if ("GET_PROJECT_BY_USER_ID".equalsIgnoreCase(queryId)) {

            String user_id = (String) map.get(Users.FIELD_ID);
            if (Util.isEmpty(user_id)) {
                throw new ApplicationException(ExceptionSeverity.ERROR, "user Not Found!!!");
            }

            Expression e = new Expression(UserProjectMap.FIELD_USER_ID, REL_OP.EQ, user_id);
            BaseResource[] user_project = UserProjectMapHelper.getInstance().getByExpression(e);
            ArrayList<BaseResource> projectList = new ArrayList<>();
            for (BaseResource pr : user_project) {
                UserProjectMap user_project_map = (UserProjectMap) pr;
                Expression e2 = new Expression(Project.FIELD_ID, REL_OP.EQ, user_project_map.getProject_id());
                BaseResource[] user_project_helper = UserProjectMapHelper.getInstance().getByExpression(e2);

                if (user_project_helper != null && user_project_helper.length > 0) {
                    for (BaseResource u : user_project_helper) {
                        projectList.add(u);
                    }
                }
            }
            return projectList.toArray(new BaseResource[0]);


        }
        else if ("GET_USER_BY_PROJECT_ID".equalsIgnoreCase(queryId)) {

            String project_id = (String) map.get(Project.FIELD_ID);
            if (Util.isEmpty(project_id)) {
                throw new ApplicationException(ExceptionSeverity.ERROR, "project Not Found!!!");
            }

            Expression e = new Expression(UserProjectMap.FIELD_PROJECT_ID, REL_OP.EQ, project_id);
            BaseResource[] user_project = UserProjectMapHelper.getInstance().getByExpression(e);
            ArrayList<BaseResource> UserList = new ArrayList<>();
            for (BaseResource pr : user_project) {
                UserProjectMap user_project_map = (UserProjectMap) pr;
                Expression e2 = new Expression(Users.FIELD_ID, REL_OP.EQ, user_project_map.getUser_id());
                BaseResource[] user_project_helper = UserProjectMapHelper.getInstance().getByExpression(e2);

                if (user_project_helper != null && user_project_helper.length > 0) {
                    for (BaseResource u : user_project_helper) {
                        UserList.add(u);
                    }
                }
            }
            return UserList.toArray(new BaseResource[0]);


        }



        return super.getQuery(ctx, queryId, map, service);
    }
}
