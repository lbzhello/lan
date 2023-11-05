package com.liubaozhu.lan.core.parser;

import cn.hutool.core.io.resource.ResourceUtil;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.base.impl.LanDefinition;
import com.liubaozhu.lan.core.lexer.LanLexer;
import com.liubaozhu.lan.core.parser.impl.FnSyntaxParser;
import com.liubaozhu.lan.core.parser.impl.LambdaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 实时解析引擎
 */
public final class LanShell {
    private static final Logger logger = LoggerFactory.getLogger(LanShell.class);

    public static List<Expression> interpret(String text) {
        LanLexer lexer = LanLexer.text(text);
        LanParser lanParser = LanParser.builder()
                .lexer(lexer)
                .definition(new LanDefinition())
                .syntaxParser(LanDefinition.LAMBDA, new LambdaParser())
                .syntaxParser(LanDefinition.FUNCTION, new FnSyntaxParser())
                .build();


        List<Expression> exprs = new ArrayList<>();
        while (!lanParser.isEnd()) {
            exprs.add(lanParser.statement());
        }
        return exprs;
    }

    /**
     * 打印文本解析
     * @return
     */
    public static void printExpr(String text) {
        logger.debug("script:\n{}" , text);
        logger.debug("expressions:");
        List<Expression> interpret = interpret(text);
        for (int i = 0; i < interpret.size(); i++) {
            Expression expr = interpret.get(i);
            logger.debug("{}: {}", i + 1, expr);
        }
    }

    /**
     * 打印文本解析执行
     * @return
     */
    public static void printResult(String text) {
        logger.debug("script: {}", text);
        List<Expression> exprs = interpret(text);
        for (int i = 0; i < exprs.size(); i++) {
            Expression expr = exprs.get(i);
            Expression rst = expr.eval();
            logger.debug("{}: {} => {}", i + 1, expr, rst);
        }
    }

    /**
     * 打印解析的 lan 脚本文件，相对路径
     */
    public static void printFile(String file) {
        try {
            printExpr(Files.readString(Path.of(ResourceUtil.getResource(file).toURI())));
        } catch (Exception e) {
            logger.error("printLanFile failed");
            throw new RuntimeException(e);
        }
    }
}
