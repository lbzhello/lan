package com.liubaozhu.lan.core.ast;

import java.io.Serializable;

/**
 * 抽象语法树顶层类
 */
public interface Expression extends Serializable, Cloneable {
    default Expression eval() {
        return this;
    }

    /**
     * 返回字面量信息，即表达式源码表示
     * @return
     */
    default String literal() {
        return "";
    }

    Constant NIL = new Constant("nil");
    Constant TRUE = new Constant("true");
    Constant FALSE = new Constant("false");

    // 常量值
    class Constant implements Expression {
        private Object value;

        public Constant(Object value) {
            this.value = value;
        }

        @Override
        public Expression eval() {
            return null;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
