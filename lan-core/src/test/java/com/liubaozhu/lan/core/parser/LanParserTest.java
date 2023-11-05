package com.liubaozhu.lan.core.parser;

import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LanParserTest {
    private static final Logger logger = LoggerFactory.getLogger(LanParserTest.class);

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
    public void listTest() {
        LanShell.printExpr("[cmd + 2 , 3 + 2, 5]");
        LanShell.printExpr("[cmd (3 + 2) 5]");
    }

    @Test
    public void tupleTest() {
        LanShell.printExpr("(cmd, 3, 5)");
        LanShell.printExpr("(cmd, 3 5, 6)");
    }

    @Test
    public void lispTest() {
        LanShell.printFile("lan/expression/lisp.lan");
    }

    @Test
    public void commandTest() {
        logger.debug("命令表达式测试");
        LanShell.printFile("lan/expression/cmd.lan");
    }

    @Test
    public void operatorTest() {
        logger.debug("运算符表达式测试");
        LanShell.printFile("lan/expression/operator.lan");
    }

    @Test
    public void wordTest() {
        logger.debug("单词表达式测试");
        LanShell.printFile("lan/expression/word.lan");
    }
}
