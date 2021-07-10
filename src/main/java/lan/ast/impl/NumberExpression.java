package lan.ast.impl;

import lan.ast.Value;
import lan.ast.Name;

import java.math.BigDecimal;

/**
 * 数字
 */
public class NumberExpression extends BigDecimal implements Value, Name {
    public NumberExpression(String val) {
        super(val);
    }

    public NumberExpression(double val) {
        super(val);
    }

}
