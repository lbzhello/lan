package com.liubaozhu.lan.core.test;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.exception.LanEexception;
import com.liubaozhu.lan.core.parser.LanShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LanTestUtil {
    public static final Logger logger = LoggerFactory.getLogger(LanTestUtil.class);

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

    public static ExpressionSpec parseText(String text) {

        try {
            return new ExpressionSpec(LanShell.interpret(text));
        } catch (LanEexception e) {
            return new ExpressionSpec(e);
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
                Expression rst = expr.eval();
                logger.debug("{}: {} => {}", i + 1, expr, rst);
            }
        }

        public void matchErrorCode(String errorCode) {
            if (Objects.isNull(ex)) {
                throw new TestException("缺少异常信息: null");
            }

            if (!Objects.equals(errorCode, ex.getCode())) {
                throw new TestException("断言失败", ex);
            }

            logger.info("matchErrorCode success");
        }
    }
}
