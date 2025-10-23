package com.heyu.timeline;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Timeline项目测试套件
 */
@Suite
@SelectClasses({
    BothTimeLineTypesExampleTest.class,
    CompleteTimeLineExampleTest.class,
    EvictionStrategyTest.class,
    TimeLineExampleTest.class,
    TimeLinePoolExampleTest.class,
    TimeLineStrategyExampleTest.class,
    TimeLineUsageExampleTest.class,
    TimelineStructureExampleTest.class
})
public class TestSuite {
    // 测试套件，运行所有测试类
}