package lan.base;

import lan.ast.Expression;

/**
 * 关键字
 */
public enum OperatorEnum implements Operator {
    NIL("nil", 0)
    ;

    // 预算付名称
    private String name;
    // 参数个数
    private int paramsNum;
    private int priority;

    OperatorEnum(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public Expression eval() {
        return this;
    }
}
