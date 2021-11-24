package lan.ast.expression;

import lan.ast.Expression;
import lan.ast.Value;

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
