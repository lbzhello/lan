package lan.ast.impl;

import lan.ast.LeafExpression;
import lan.ast.Name;

import java.util.Objects;

/**
 * 字符串
 */
public class StringExpression implements LeafExpression, Name {
    private String value;

    public StringExpression(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return Objects.isNull(value) ? "" : "\"" + value + "\"";
    }
}
