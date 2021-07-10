package lan.base.impl;

import lan.ast.*;
import lan.base.Definition;
import lan.base.OperateName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * lan 语言定义
 */
public class LanDefinition implements Definition {
    public static final Expression NIL = ExpressionFactory.value("nil");
    public static final Expression TRUE = ExpressionFactory.value("true");
    public static final Expression FALSE = ExpressionFactory.value("false");

    private final Supplier<Operator> defaultSupplier = () -> null;

    // 值每次获取时新创建一个对象
    private final Map<String, Supplier<Operator>> operatorSupplier = new HashMap<>();

    // 运算符优先级
    private final Map<String, Integer> operatorPrecedence = new HashMap<>();



    private void init() {
        // 获取 keywords

        // 运算符
        Arrays.stream(OperateName.values())
                .forEach(operateName -> {
                    operatorSupplier.put(operateName.getValue(), operateName.getOperatorSupplier());
                });

        // 运算符优先级
        operatorPrecedence.put(OperateName.COLON.getValue(), -10);

        operatorPrecedence.put(OperateName.ARROW.getValue(), -1);

        operatorPrecedence.put(OperateName.ASSIGN.getValue(), 0);

        operatorPrecedence.put(OperateName.OR.getValue(), 11);
        operatorPrecedence.put(OperateName.AND.getValue(), 12);

        operatorPrecedence.put(OperateName.EQUAL.getValue(), 21);
        operatorPrecedence.put(OperateName.NOT_EQUAL.getValue(), 21);
        operatorPrecedence.put(OperateName.GT.getValue(), 21);
        operatorPrecedence.put(OperateName.GE.getValue(), 21);
        operatorPrecedence.put(OperateName.LT.getValue(), 21);
        operatorPrecedence.put(OperateName.LE.getValue(), 21);

        operatorPrecedence.put(OperateName.PLUS.getValue(), 31);
        operatorPrecedence.put(OperateName.MINUS.getValue(), 31);
        operatorPrecedence.put(OperateName.MUL.getValue(), 41);
        operatorPrecedence.put(OperateName.DIV.getValue(), 41);

        operatorPrecedence.put(OperateName.ELSE.getValue(), 1314);
    }

    /**
     * 每次获取新创建一个对象
     * @param op 运算符，如 + - * / def let 等
     * @return 对应的运算符表达式，可以执行响应的计算
     */
    @Override
    public Operator getOperator(String op) {
        return operatorSupplier.getOrDefault(op, defaultSupplier).get();
    }

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
    public Integer getPrecedence(Name operatorName) {
        return null;
    }
}
