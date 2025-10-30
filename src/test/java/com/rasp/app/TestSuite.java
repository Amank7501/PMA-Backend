package com.rasp.app;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    ProjectCollabirationTest.class,
    ProjectTest.class,
    LabelTest.class,
    IssueTest.class,
    CommentsTest.class,
    CommentsConcurrentTest.class,
    IssueDecoratorTest.class
})
public class TestSuite {
    // This class is just a container for the test suite
}
