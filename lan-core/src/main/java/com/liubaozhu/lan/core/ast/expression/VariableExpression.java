package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.Expression;

public class VariableExpression implements Expression {
    // 变量名称
    private String name;
    // 类型
    private Expression type;

    public VariableExpression(String name) {
        this.name = name;
    }

    public void setType(Expression type) {
        this.type = type;
    }
}
