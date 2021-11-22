package lan.ast.operator;

import cn.hutool.core.util.ArrayUtil;
import lan.ast.BaseExpression;

/**
 * 运算符抽象类，提供 add 方法实现，重写 toString
 */
public abstract class AbstractOperator extends BaseExpression {
    @Override
    public String toString() {
        return "(" + ArrayUtil.join(toArray(), " ") + ")";
    }
}
