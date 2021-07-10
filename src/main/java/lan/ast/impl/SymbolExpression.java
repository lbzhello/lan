package lan.ast.impl;

import lan.ast.Expression;
import lan.ast.Name;

import java.util.Objects;

public class SymbolExpression implements Name {
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
