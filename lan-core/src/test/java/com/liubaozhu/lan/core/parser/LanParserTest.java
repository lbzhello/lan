package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.exception.ErrorCode;
import com.liubaozhu.lan.core.test.LanTestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class LanParserTest {
    private static final Logger logger = LogManager.getLogger(LanParserTest.class);

    @Test
    public void fnCallTest() {
        LanTestUtil.parseFile("lan/expression/fn-call.lan")
                .printResult();
    }

    @Test
    public void assignTest() {
        LanTestUtil.parseFile("lan/expression/assign.lan")
                .printResult();
    }

    @Test
    public void listTest() {
        LanTestUtil.parseFile("lan/expression/list.lan")
                .printResult();
    }

    @Test
    public void tupleTest() {
        logger.debug("tuple test");
        LanTestUtil.parseFile("lan/expression/tuple.lan")
                .printResult();
    }

    @Test
    public void tupleErrorTest() {
        LanTestUtil.parseText("(cmd, 3 5, 6)")
                .matchErrorCode(ErrorCode.PARSE_TUPLE_FAILED);

        LanTestUtil.parseText("(cmd a, b 3 8)")
                .matchErrorCode(ErrorCode.PARSE_LISP_FAILED);
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
