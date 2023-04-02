package com.liubaozhu.lan.core.ast;

/**
 * 表达式提供者，实现此接口会调用 {@link #get()} 方法生成一个 expression
 * 通常用来创建一个新的 expression
 */
public interface ExpressionSupplier extends Expression {
    Expression get();
}
