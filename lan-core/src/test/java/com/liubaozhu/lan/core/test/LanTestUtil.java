package com.liubaozhu.lan.core.test;

import cn.hutool.core.io.resource.ResourceUtil;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.exception.LanEexception;
import com.liubaozhu.lan.core.parser.LanShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LanTestUtil {
    public static final Logger logger = LogManager.getLogger(LanTestUtil.class);

    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:");
            System.out.println("hello");
            return "hello";
        }).thenApply(str -> {
            System.out.println("thenApply:");
            if (true) {
                throw new RuntimeException();
            }
            return str.length();
        }).thenApply(it -> {
            System.out.println("thenAccept:");
            System.out.println(it);
            if (true) {
                throw new RuntimeException();
            }
            return "test apply";
        }).exceptionally(ex -> {
            System.out.println("exceptionally");
            System.out.println("end");
            return null;
        });
        System.out.println();
    }

    /**
     * 解析语法数
     * @param text
     * @return
     */
    public static ExpressionSpec parseText(String text) {

        try {
            return new ExpressionSpec(LanShell.interpret(text));
        } catch (LanEexception e) {
            return new ExpressionSpec(e);
        }
    }

    public static ExpressionSpec parseFile(String file) {
        try {
            return parseText(Files.readString(Path.of(ResourceUtil.getResource(file).toURI())));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ExpressionSpec {
        private List<Expression> exprs;
        private LanEexception ex;

        public ExpressionSpec(List<Expression> exprs) {
            this.exprs = exprs;
        }

        public ExpressionSpec(LanEexception ex) {
            this.ex = ex;
        }

        /**
         * 打印解析的语法树
         * @return
         */
        public ExpressionSpec print() {
            return this;
        }

        public ExpressionSpec eval() {
            return this;
        }

        /**
         * 打印解析结果
         */
        public void printResult() {
            if (ex != null) {
                throw ex;
            }

            for (int i = 0; i < exprs.size(); i++) {
                Expression expr = exprs.get(i);
                logger.info("{}: {}", i + 1, expr);
            }
        }

        public void matchErrorCode(String errorCode) {
            if (Objects.isNull(ex)) {
                throw new TestException("无异常信息");
            }

            if (!Objects.equals(errorCode, ex.getCode())) {
                throw new TestException("断言失败", ex);
            }

            logger.info("matchErrorCode success");
        }
    }
}
