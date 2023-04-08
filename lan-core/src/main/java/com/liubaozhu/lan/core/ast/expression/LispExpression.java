package com.liubaozhu.lan.core.ast.expression;

import cn.hutool.core.util.ArrayUtil;
import com.liubaozhu.lan.core.ast.Expression;

/**
 * lisp 格式表达式
 * e.g. (max 2 3) || max(2, 3)
 */
public class LispExpression extends EvalExpression {

    public LispExpression(Expression... exprs) {
        super(exprs);
    }

    @Override
    public String toString() {
        return "(" + ArrayUtil.join(toArray(), " ") + ")";
    }
}
