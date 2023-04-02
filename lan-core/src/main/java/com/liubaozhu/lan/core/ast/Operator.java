package com.liubaozhu.lan.core.ast;

/**
 * 基础运算符，系统（语言运行时）提供的运算符
 * todo 运算符通过函数实现
 */
public interface Operator extends Expression {
    void add(Expression operand);
}
