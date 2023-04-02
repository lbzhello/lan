package com.liubaozhu.lan.core.interpreter;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanInterpreterTest {
    private static final Logger logger = LoggerFactory.getLogger(LanInterpreterTest.class);

    @Test
    public void squareBracketTest() {
        logger.debug("列表测试");
        LanShell.printExpr("[foo, bar hello + world 3 + 2 5]");
    }

    @Test
    public void roundBracketTest() {
        logger.debug("括号表达式测试");
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
        logger.debug("赋值表达式测试");
        LanShell.printExpr("a = 3 + 2");
        LanShell.printExpr("a = b = c");
    }

    @Test
    public void commandTest() {
        logger.debug("命令表达式测试");
        LanShell.printExpr("cmd a 2 + 3 c = 4 + 5 hello");
        LanShell.printExpr("cmd a");
        LanShell.printExpr("cmd a b, \n 3 + 2, d");
    }

    @Test
    public void operatorTest() {
        logger.debug("运算符测试");
        LanShell.printResult("\"hello\" + \"world\" + \n 2 + 5");

        LanShell.printResult("123 + 456 + 789");
    }

}
