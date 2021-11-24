package lan.ast.expression;

import lan.ast.Value;

import java.util.Objects;

/**
 * 字符串
 */
public class StringExpression extends ClassExpression implements Value {
    private String value;

    public StringExpression(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return Objects.isNull(value) ? "" : "\"" + value + "\"";
    }
}
