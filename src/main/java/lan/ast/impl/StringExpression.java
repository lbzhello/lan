package lan.ast.impl;

/**
 * 字符串
 */
public class StringExpression implements ConstantExpression {
    private String value;

    public StringExpression(String value){
        this.value = value;
    }
}
