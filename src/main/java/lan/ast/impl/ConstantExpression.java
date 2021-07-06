package lan.ast.impl;

import lan.ast.Expression;

/**
 * 常量表达式，代表一个值，字符串，数字，符号
 */
public interface ConstantExpression extends Expression {

    default Expression eval() {
        return this;
    }
}
