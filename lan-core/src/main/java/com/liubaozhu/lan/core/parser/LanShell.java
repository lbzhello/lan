package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.lexer.LanLexer;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.base.impl.LanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实时解析引擎
 */
public final class LanShell {
    private static final Logger logger = LoggerFactory.getLogger(LanShell.class);

    public static Expression interpret(String text) {
        LanLexer lexer = LanLexer.text(text);
        LanParser lanParser = LanParser.builder()
                .lexer(lexer)
                .definition(new LanDefinition())
                .syntaxParser(null)
                .build();

        Expression statement = lanParser.statement();
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
