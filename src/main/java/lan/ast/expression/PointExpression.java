package lan.ast.expression;

import lan.ast.BaseExpression;

/**
 * 类中的方法，字段，运算符等引用
 */
public class PointExpression extends BaseExpression {
    @Override
    public String toString() {
        return get(0) + "." + get(1);
    }
}
