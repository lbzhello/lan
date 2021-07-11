package lan.base;

import lan.ast.Value;

/**
 * lan 语言特殊值，相对于 string number 等
 */
public enum ValueEnum implements Value {
    NIL("nil"),
    TRUE("true"),
    FALSE("false"),
    ;
    private String name;

    ValueEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
