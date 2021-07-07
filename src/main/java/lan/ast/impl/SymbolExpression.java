package lan.ast.impl;

import lan.ast.LeafExpression;
import lan.ast.NameExpression;

import java.util.Objects;

public class SymbolExpression implements LeafExpression, NameExpression {
    private String value;

    public SymbolExpression(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Objects.nonNull(value) ? value : "";
    }
}
