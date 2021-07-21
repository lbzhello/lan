package lan.base.impl;

import lan.ast.Expression;
import lan.ast.Name;
import lan.ast.Value;
import lan.base.Definition;
import lan.base.OperatorConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * lan 语言定义
 */
public class LanDefinition implements Definition {

    // 运算符优先级
    private final Map<String, Integer> operatorPrecedence = new HashMap<>();

    private void init() {
        // 获取 keywords

        // 运算符优先级
        operatorPrecedence.put(OperatorConfig.COLON, -10);

        operatorPrecedence.put(OperatorConfig.ARROW, -1);

        operatorPrecedence.put(OperatorConfig.ASSIGN, 0);

        operatorPrecedence.put(OperatorConfig.OR, 11);
        operatorPrecedence.put(OperatorConfig.AND, 12);

        operatorPrecedence.put(OperatorConfig.EQUAL, 21);
        operatorPrecedence.put(OperatorConfig.NOT_EQUAL, 21);
        operatorPrecedence.put(OperatorConfig.GT, 21);
        operatorPrecedence.put(OperatorConfig.GE, 21);
        operatorPrecedence.put(OperatorConfig.LT, 21);
        operatorPrecedence.put(OperatorConfig.LE, 21);

        operatorPrecedence.put(OperatorConfig.PLUS, 31);
        operatorPrecedence.put(OperatorConfig.MINUS, 31);
        operatorPrecedence.put(OperatorConfig.MUL, 41);
        operatorPrecedence.put(OperatorConfig.DIV, 41);

    }

    /**
     * 每次获取新创建一个对象
     * @param op 运算符，如 + - * / def let 等
     * @return 对应的运算符表达式，可以执行响应的计算
     */
//    @Override
//    public Operator getOperator(String op) {
//        return operatorSupplier.getOrDefault(op, defaultSupplier).get();
//    }

    /**
     * 表达式是否是一个值
     * @param expression
     * @return
     */
    public boolean isValue(Expression expression) {
        return expression instanceof Value;
    }


    public boolean isOperator(Name name) {
        return false;
    }

    /**
     * 获取运算符优先级
     * @param operatorName
     * @return
     */
    public Integer getPrecedence(String operatorName) {
        return operatorPrecedence.get(operatorName);
    }
}
