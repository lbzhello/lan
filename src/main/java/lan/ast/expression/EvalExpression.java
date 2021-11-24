package lan.ast.expression;

import cn.hutool.core.util.ArrayUtil;
import lan.ast.BaseExpression;

/**
 * 求值表达式，函数调用，命令调用等
 * e.g. max 2 3 || (max 2 3) || max(2, 3)
 */
public class EvalExpression extends BaseExpression {
    @Override
    public String toString() {
        return "(" + ArrayUtil.join(toArray(), " ") + ")";
    }
}
