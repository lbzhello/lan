package lan.ast.impl;

import lan.ast.Expression;
import lan.ast.Name;
import lan.ast.Value;
import lan.ast.operator.Plus;

import java.math.BigDecimal;

/**
 * 数字
 */
public class NumberExpression implements Value, Name, Plus {
    private BigDecimal value = BigDecimal.ZERO;

    public NumberExpression(String val) {
        this.value = new BigDecimal(val);
    }

    public NumberExpression(BigDecimal val) {
        this.value = val;
    }

    @Override
    public NumberExpression plus(Expression operand) {
        NumberExpression that = (NumberExpression) operand;
        BigDecimal rst = value.add(that.getValue());
        return new NumberExpression(rst);
    }

    public BigDecimal getValue() {
        return this.value;
    }
}
