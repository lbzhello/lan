package lan.ast.expression;

import cn.hutool.core.util.ArrayUtil;
import lan.ast.BaseExpression;
import lan.ast.Expression;
import lan.ast.java.MethodInfo;

/**
 * 求值表达式，函数调用，命令调用等
 * e.g. max 2 3 || (max 2 3) || max(2, 3)
 */
public class EvalExpression extends BaseExpression {

    public EvalExpression(Expression... exprs) {
        for (Expression expr : exprs) {
            add(expr);
        }
    }

    @Override
    public Expression eval() {
        if (isEmpty()) {
            return Expression.NIL;
        }

        Expression first = get(0).eval();
        // java 反射调用
        if (first instanceof MethodInfo) {
            MethodInfo method = (MethodInfo) first;
            Object[] params = slice(1);
            Expression rst = method.invoke(params);
            return rst;
        }
        return Expression.NIL;
    }

    @Override
    public String toString() {
        return "(" + ArrayUtil.join(toArray(), " ") + ")";
    }
}
