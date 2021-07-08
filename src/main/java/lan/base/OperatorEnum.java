package lan.base;

import lan.ast.Expression;
import lan.ast.Name;
import lan.ast.Operator;

/**
 * 运算符
 */
public enum OperatorEnum implements Operator, Name {
    NIL("nil"),
    TRUE("true"),
    FALSE("false"),

    DEFINE("define"),

    PLUS("+"),
    ;

    // 运算符名称
    private String name;

    OperatorEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Expression eval() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
