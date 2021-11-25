package lan.ast.expression;

import lan.ast.Expression;
import lan.ast.Value;
import lan.ast.java.JavaMethod;
import lan.ast.java.MethodHandleAdapter;
import lan.ast.operator.Plus;
import lan.ast.operator.PlusOperator;
import lan.base.Definition;
import lan.base.impl.LanDefinition;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数字
 */
public class NumberExpression extends ClassExpression implements Value, Plus {

    // 系统提供的方法 + - * / 等
    private static Map<String, MethodHandleAdapter> methods = new HashMap<>();
    static {
        methods.put(LanDefinition.PLUS, new MethodHandleAdapter(NumberExpression.class, "plus", NumberExpression.class, Expression.class));
    }

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

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Expression lookup(String key) {
        Expression lookup = super.lookup(key);
        if (Objects.isNull(lookup)) {
            return methods.get(key);
        }
        return lookup;
    }
}
