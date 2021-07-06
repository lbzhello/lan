package lan.ast.impl;

import lan.ast.Expression;

import java.math.BigDecimal;

/**
 * 数字
 */
public class NumberExpression extends BigDecimal implements ConstantExpression {
    public NumberExpression(String val) {
        super(val);
    }

    public NumberExpression(double val) {
        super(val);
    }

    @Override
    public Expression eval() {
        return this;
    }
}
