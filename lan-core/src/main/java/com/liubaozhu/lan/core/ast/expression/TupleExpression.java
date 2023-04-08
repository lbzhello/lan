package com.liubaozhu.lan.core.ast.expression;

import cn.hutool.core.util.ArrayUtil;
import com.liubaozhu.lan.core.ast.BaseExpression;
import com.liubaozhu.lan.core.ast.Expression;

/**
 * 元组表达式表达式
 * e.g. [1 2 3 4]
 */
public class TupleExpression extends ListExpression {
    public TupleExpression(Expression... expressions) {
        addAll(expressions);
    }

    @Override
    public String toString() {
        return "(" + ArrayUtil.join(toArray(), ", ") + ")";
    }
}
