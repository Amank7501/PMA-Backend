package com.rasp.app.decorator;




import com.rasp.app.helper.*;
import com.rasp.app.resource.*;
import platform.db.Expression;
import platform.db.REL_OP;
import platform.decorator.BaseDecorator;

import platform.defined.helper.UserHelper;
import platform.defined.resource.User;
import platform.resource.BaseResource;
import platform.util.ApplicationException;
import platform.util.ExceptionSeverity;
import platform.util.Util;
import platform.webservice.BaseService;
import platform.webservice.ServletContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


public class LabelIssueDecorator extends BaseDecorator {
    public LabelIssueDecorator() {
        super(new LabelIssueMap());
    }

    //    @Override
//    public void preAddDecorator(ServletContext ctx, BaseResource _resource) throws ApplicationException {
//        Course course = (Course) _resource;
//        BaseResource[] baseCourses = CourseHelper.getInstance().getAll();
//        for(BaseResource baseRes : baseCourses){
//            Course c = (Course) baseRes;
//            if(Objects.equals(course.getCourse_code(), c.getCourse_code()) && Objects.equals(course.getCourse_name(), c.getCourse_name())){
//                throw new ApplicationException(ExceptionSeverity.ERROR, "Course Already Present");
//            }
//        }
//    }
    @Override
    public BaseResource[] getQuery(ServletContext ctx, String queryId, Map<String, Object> map, BaseService service) throws ApplicationException {
     if("GET_ISSUE_BY_LABEL_ID".equalsIgnoreCase(queryId)){
            String label_id = (String) map.get(Label.FIELD_ID);
            if (Util.isEmpty(label_id)) {
                throw new ApplicationException(ExceptionSeverity.ERROR, "User id not found!!!");
            }
            Expression e1 = new  Expression(LabelIssueMap.FIELD_LABEL_ID, REL_OP.EQ, label_id);
            BaseResource[] label_issue = LabelIssueMapHelper.getInstance().getByExpression(e1);

            ArrayList<BaseResource> issueList = new ArrayList<>();
            for (BaseResource li : label_issue) {
                LabelIssueMap labelissuemap = (LabelIssueMap) li;
                Expression e2 = new Expression(Issue.FIELD_ID, REL_OP.EQ, labelissuemap.getIssue_id());
                BaseResource[] issueHelper = IssueHelper.getInstance().getByExpression(e2);

                if (issueHelper != null && issueHelper.length > 0) {
                    for (BaseResource u : issueHelper) {
                        issueList.add(u);
                    }
                }
            }
            return issueList.toArray(new BaseResource[0]);
        }
        else if("GET_LABEL_BY_ISSUE_ID".equalsIgnoreCase(queryId)){
            String issue_id = (String) map.get(Issue.FIELD_ID);
            if (Util.isEmpty(issue_id)) {
                throw new ApplicationException(ExceptionSeverity.ERROR, "User id not found!!!");
            }
            Expression e1 = new  Expression(LabelIssueMap.FIELD_ISSUE_ID, REL_OP.EQ, issue_id);
            BaseResource[] label_issue = LabelIssueMapHelper.getInstance().getByExpression(e1);
            ArrayList<BaseResource> labellist = new ArrayList<>();
            for (BaseResource li : label_issue) {
                LabelIssueMap labelissuemap = (LabelIssueMap) li;
                Expression e2 = new Expression(Label.FIELD_ID, REL_OP.EQ, labelissuemap.getLabel_id());
                BaseResource[] labelhelper = LabelHelper.getInstance().getByExpression(e2);

                if (labelhelper != null && labelhelper.length > 0) {
                    for (BaseResource u : labelhelper) {
                        labellist.add(u);
                    }
                }
            }
            return labellist.toArray(new BaseResource[0]);
        }
//        else if("GET_ISSUE_BY_LABEL_ID".equalsIgnoreCase(queryId)){
//            String label_id = (String) map.get(Label.FIELD_ID);
//            if (Util.isEmpty(label_id)) {
//                throw new ApplicationException(ExceptionSeverity.ERROR, "User id not found!!!");
//            }
//            Expression e1 = new  Expression(LabelIssueMap.FIELD_LABEL_ID, REL_OP.EQ, label_id);
//            BaseResource[] label_issue = LabelIssueMapHelper.getInstance().getByExpression(e1);
//
//            ArrayList<BaseResource> issueList = new ArrayList<>();
//            for (BaseResource li : label_issue) {
//                LabelIssueMap labelissuemap = (LabelIssueMap) li;
//                Expression e2 = new Expression(Issue.FIELD_ID, REL_OP.EQ, labelissuemap.getIssue_id());
//                BaseResource[] issueHelper = IssueHelper.getInstance().getByExpression(e2);
//
//                if (issueHelper != null && issueHelper.length > 0) {
//                    for (BaseResource u : issueHelper) {
//                        issueList.add(u);
//                    }
//                }
//            }
//            return issueList.toArray(new BaseResource[0]);
//        }
//        else if("GET_LABEL_BY_ISSUE_ID".equalsIgnoreCase(queryId)){
//            String issue_id = (String) map.get(Issue.FIELD_ID);
//            if (Util.isEmpty(issue_id)) {
//                throw new ApplicationException(ExceptionSeverity.ERROR, "User id not found!!!");
//            }
//            Expression e1 = new  Expression(LabelIssueMap.FIELD_ISSUE_ID, REL_OP.EQ, issue_id);
//            BaseResource[] label_issue = LabelIssueMapHelper.getInstance().getByExpression(e1);
//
//            ArrayList<BaseResource> labellist = new ArrayList<>();
//            for (BaseResource li : label_issue) {
//                LabelIssueMap labelissuemap = (LabelIssueMap) li;
//                Expression e2 = new Expression(Label.FIELD_ID, REL_OP.EQ, labelissuemap.getLabel_id());
//                BaseResource[] labelhelper = IssueHelper.getInstance().getByExpression(e2);
//
//                if (labelhelper != null && labelhelper.length > 0) {
//                    for (BaseResource u : labelhelper) {
//                        labellist.add(u);
//                    }
//                }
//            }
//            return labellist.toArray(new BaseResource[0]);
//        }
//        else if ("GET_COURSES_BY_INSTRUCTOR".equalsIgnoreCase(queryId)) {
//            String instructorId = (String) map.get(Course.FIELD_INSTRUCTOR_ID);
//            if (Util.isEmpty(instructorId)) {
//                throw new ApplicationException(ExceptionSeverity.ERROR, "Instructor id not found!!!");
//            }
//            Expression e1 = new Expression(Course.FIELD_INSTRUCTOR_ID, REL_OP.EQ, instructorId);
//            return CourseHelper.getInstance().getByExpression(e1);
//        }
        return super.getQuery(ctx, queryId, map, service);
    }
}