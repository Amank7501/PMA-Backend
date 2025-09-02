//package com.rasp.app.decorator;
//
//import com.rasp.app.helper.IssueUserMapHelper;
//import com.rasp.app.resource.IssueUserMap;
//import platform.db.Expression;
//import platform.db.REL_OP;
//import platform.decorator.BaseDecorator;
//import platform.resource.BaseResource;
//import platform.util.ApplicationException;
//import platform.util.ExceptionSeverity;
//import platform.util.Util;
//import platform.webservice.BaseService;
//import platform.webservice.ServletContext;
//
//import java.util.*;
//
//public class IssueUserMapDec extends BaseDecorator {
//    public IssueUserMapDec() {super(new IssueUserMap());}
//
//    @Override
//    public BaseResource[] getQuery(ServletContext ctx, String queryId, Map<String, Object> map, BaseService service) throws ApplicationException {
//
////        if ("getIssue".equalsIgnoreCase(queryId)) {
////
////            String issueId = (String) map.get(IssueUserMap.FIELD_ISSUE_ID);
////            if (Util.isEmpty(issueId)) {
////                throw new ApplicationException(ExceptionSeverity.ERROR, "Roll Number Not Found!!!");
////            }
////
////            Expression e = new Expression(IssueUserMap.FIELD_ISSUE_ID, REL_OP.EQ, issueId);
////            return IssueUserMapHelper.getInstance().getByExpression(e);
////
////
////
////        }
//
//
//
//        return super.getQuery(ctx, queryId, map, service);
//    }
//}
