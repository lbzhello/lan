package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.ExpressionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表达式
 */
public class ClassExpression extends ExpressionContext implements Expression {
    // 类定义代码
    private List<Expression> codes = new ArrayList<>();

    public void add(Expression expression) {
        codes.add(expression);
    }
}
