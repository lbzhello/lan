package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.Value;

import java.util.Objects;

public class SymbolExpression implements Value {
    private String value;

    public SymbolExpression(String value) {
        this.value = value;
    }

    @Override
    public Expression eval() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.nonNull(value) ? value : "";
    }
}
