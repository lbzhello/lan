package lan.ast.impl;

import lan.ast.LeafExpression;
import lan.ast.NameExpression;

import java.math.BigDecimal;

/**
 * 数字
 */
public class NumberExpression extends BigDecimal implements LeafExpression, NameExpression {
    public NumberExpression(String val) {
        super(val);
    }

    public NumberExpression(double val) {
        super(val);
    }

}
