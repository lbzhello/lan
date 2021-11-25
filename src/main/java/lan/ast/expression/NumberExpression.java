package lan.ast.expression;

import lan.ast.Expression;
import lan.ast.Value;
import lan.ast.java.MethodHandleAdapter;
import lan.ast.java.MethodInfo;
import lan.base.impl.LanDefinition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数字
 */
public class NumberExpression extends ClassExpression implements Value {

    // 系统提供的方法 + - * / 等
    private static Map<String, MethodInfo> methods = new HashMap<>();
    static {
        methods.put(LanDefinition.PLUS, new MethodHandleAdapter(NumberExpression.class, "plus", NumberExpression.class, NumberExpression.class));
        methods.put(LanDefinition.MINUS, new MethodHandleAdapter(NumberExpression.class, "minus", NumberExpression.class, NumberExpression.class));
        methods.put(LanDefinition.MUL, new MethodHandleAdapter(NumberExpression.class, "multiply", NumberExpression.class, NumberExpression.class));
        methods.put(LanDefinition.DIV, new MethodHandleAdapter(NumberExpression.class, "divide", NumberExpression.class, NumberExpression.class));
    }

    private BigDecimal value = BigDecimal.ZERO;

    public NumberExpression(String val) {
        this.value = new BigDecimal(val);
    }

    public NumberExpression(BigDecimal val) {
        this.value = val;
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

    //******** 运算符实现 *********//

    /**
     * +
     * @param that
     * @return
     */
    public NumberExpression plus(NumberExpression that) {
        BigDecimal rst = this.value.add(that.value);
        return new NumberExpression(rst);
    }

    /**
     * -
     * @param that
     * @return
     */
    public NumberExpression minus(NumberExpression that) {
        BigDecimal rst = this.value.subtract(that.value);
        return new NumberExpression(rst);
    }

    public NumberExpression multiply(NumberExpression that) {
        return new NumberExpression(this.value.multiply(that.value));
    }

    public NumberExpression divide(NumberExpression that) {
        return new NumberExpression(this.value.divide(that.value, 9, RoundingMode.HALF_EVEN));
    }

}
