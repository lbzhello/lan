package lan.ast.expression;

import lan.ast.BaseExpression;
import lan.ast.Expression;

/**
 * 函数表达式
 */
public class FnExpression extends BaseExpression {
    /**
     * 获取代码流
     * @return
     */
    public Expression[] getCode() {
        return toArray();
    }
}
