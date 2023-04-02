package com.liubaozhu.lan.core.interpreter;

import com.liubaozhu.lan.core.parser.TextParser;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.base.impl.LanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实时解析引擎
 */
public final class ReplEngine {
    private static final Logger logger = LoggerFactory.getLogger(ReplEngine.class);

    public static Expression interpret(String text) {
        TextParser parser = TextParser.text(text);
        LanInterpreter lanInterpreter = new LanInterpreter(parser, new LanDefinition(), null);
        Expression statement = lanInterpreter.statement();
        return statement;
    }

    /**
     * 打印文本解析
     * @return
     */
    public static void printExpr(String text) {
        Expression expr = interpret(text);
        logger.debug("{} => {}", text, expr);
    }

    /**
     * 打印文本解析执行
     * @return
     */
    public static void printResult(String text) {
        Expression expr = interpret(text);
        Expression rst = expr.eval();
        logger.debug("{} => {} => {}", text, expr, rst);
    }
}
