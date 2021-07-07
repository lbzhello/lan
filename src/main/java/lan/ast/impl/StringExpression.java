package lan.ast.impl;

import lan.ast.LeafExpression;
import lan.ast.NameExpression;

import java.util.Objects;

/**
 * 字符串
 */
public class StringExpression implements LeafExpression, NameExpression {
    private String value;

    public StringExpression(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return Objects.isNull(value) ? "" : "\"" + value + "\"";
    }
}