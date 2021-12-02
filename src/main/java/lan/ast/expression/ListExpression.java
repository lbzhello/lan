package lan.ast.expression;

import cn.hutool.core.util.ArrayUtil;
import lan.ast.BaseExpression;
import lan.ast.Expression;

/**
 * 列表/数组表达式
 * e.g. [1 2 3 4]
 */
public class ListExpression extends BaseExpression {
    public ListExpression(Expression... expressions) {
        addAll(expressions);
    }

    @Override
    public String toString() {
        return "[" + ArrayUtil.join(toArray(), " ") + "]";
    }
}
