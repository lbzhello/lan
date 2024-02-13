package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.exception.ErrorCode;
import com.liubaozhu.lan.core.test.LanTestUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanParserTest {
    private static final Logger logger = LoggerFactory.getLogger(LanParserTest.class);

    @Test
    public void test() {
        LanTestUtil.parseText("cmd a, b")
                .matchErrorCode(ErrorCode.PARSE_COMMAND_FAILED);
    }

    @Test
    public void squareBracketTest() {
        logger.debug("列表测试");
        LanShell.printExpr("[foo, bar hello + world 3 + 2 5]");
    }

    @Test
    public void roundBracketTest() {
        LanShell.printExpr("foo(p1, p2, p3)");
        LanShell.printExpr("foo(p1 p2 p3)");
        LanShell.printExpr("(foo, bar = 3 + 2\n, 5)");

        LanShell.printExpr("(cmd p1 = 5 + \n6 9 + 8 10)");

        LanShell.printExpr("(a, b\n, ,\n,c, d)");
        LanShell.printExpr("(,)");
        LanShell.printExpr("(a,)");
        LanShell.printExpr("(,a)");
    }

    @Test
    public void assignTest() {
        LanShell.printFile("lan/expression/assign.lan");
    }

    @Test
    public void listTest() {
        LanShell.printFile("lan/expression/list.lan");
    }

    @Test
    public void tupleTest() {
        logger.debug("tuple test");
        LanShell.printFile("lan/expression/tuple.lan");
    }

    @Test
    public void tupleErrorTest() {
        LanTestUtil.parseText("(cmd, 3 5, 6)")
                .printResult();

        LanTestUtil.parseText("(cmd a, b 3 8)")
                .matchErrorCode(ErrorCode.PARSE_COMMAND_FAILED);
    }

    @Test
    public void lispTest() {
        logger.debug("lisp test");
        LanTestUtil.parseFile("lan/expression/lisp.lan")
                .printResult();
    }

    @Test
    public void commandTest() {
        logger.debug("command test");
        LanTestUtil.parseFile("lan/expression/cmd.lan")
                .printResult();
    }

    @Test
    public void commandErrorTest() {
        LanTestUtil.parseText("cmd a, b")
                .matchErrorCode(ErrorCode.PARSE_COMMAND_FAILED);
    }

    @Test
    public void operatorTest() {
        logger.debug("operator test");
        LanTestUtil.parseFile("lan/expression/operator.lan")
                .printResult();
    }

    @Test
    public void wordTest() {
        logger.debug("word test");
        LanTestUtil.parseFile("lan/expression/word.lan")
                .printResult();
    }
}
