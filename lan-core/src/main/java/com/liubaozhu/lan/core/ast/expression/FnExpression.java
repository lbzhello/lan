package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.BaseExpression;
import com.liubaozhu.lan.core.ast.Expression;

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
