package lan.ast.impl;

import cn.hutool.core.util.ArrayUtil;
import lan.ast.BaseExpression;

/**
 * 列表/数组表达式
 * e.g. [1 2 3 4]
 */
public class ListExpression extends BaseExpression {
    @Override
    public String toString() {
        return "[" + ArrayUtil.join(toArray(), " ") + "]";
    }
}
