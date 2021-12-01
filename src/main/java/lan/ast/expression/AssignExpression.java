package lan.ast.expression;


import lan.ast.BaseExpression;
import lan.ast.Expression;

public class AssignExpression extends BaseExpression {
    @Override
    public String toString() {
        Expression left = get(0);
        Expression right = get(1);
        return get(0) + " = " + get(1);
    }
}
