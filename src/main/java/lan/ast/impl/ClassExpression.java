package lan.ast.impl;

import lan.ast.BaseExpression;
import lan.ast.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * 类表达式
 */
public class ClassExpression extends BaseExpression {
    // 查找表，存放字段，函数，运算符等
    private Map<String, Expression> table = new HashMap<>();

    public Expression lookup(String name) {
        return table.get(name);
    }
}
