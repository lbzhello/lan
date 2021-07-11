package lan.base.impl;

import lan.ast.Expression;
import lan.ast.Name;
import lan.ast.Value;
import lan.base.Definition;
import lan.base.OperatorEnum;

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
        operatorPrecedence.put(OperatorEnum.COLON.getName(), -10);

        operatorPrecedence.put(OperatorEnum.ARROW.getName(), -1);

        operatorPrecedence.put(OperatorEnum.ASSIGN.getName(), 0);

        operatorPrecedence.put(OperatorEnum.OR.getName(), 11);
        operatorPrecedence.put(OperatorEnum.AND.getName(), 12);

        operatorPrecedence.put(OperatorEnum.EQUAL.getName(), 21);
        operatorPrecedence.put(OperatorEnum.NOT_EQUAL.getName(), 21);
        operatorPrecedence.put(OperatorEnum.GT.getName(), 21);
        operatorPrecedence.put(OperatorEnum.GE.getName(), 21);
        operatorPrecedence.put(OperatorEnum.LT.getName(), 21);
        operatorPrecedence.put(OperatorEnum.LE.getName(), 21);

        operatorPrecedence.put(OperatorEnum.PLUS.getName(), 31);
        operatorPrecedence.put(OperatorEnum.MINUS.getName(), 31);
        operatorPrecedence.put(OperatorEnum.MUL.getName(), 41);
        operatorPrecedence.put(OperatorEnum.DIV.getName(), 41);

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
