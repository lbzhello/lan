package lan.base.impl;

import lan.ast.ExpressionFactory;
import lan.base.Definition;
import lan.ast.Operator;
import lan.base.OperatorEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * lan 语言定义
 */
public class LanDefinition implements Definition {
    // 值每次获取时新创建一个对象
    private Map<String, Supplier<Operator>> operatorSupplier = new HashMap<>();

    private void init() {
        // 基本值唯一，不需要新建
        operatorSupplier.put(OperatorEnum.NIL.getName(), () -> OperatorEnum.NIL);
        operatorSupplier.put(OperatorEnum.TRUE.getName(), () -> OperatorEnum.TRUE);
        operatorSupplier.put(OperatorEnum.FALSE.getName(), () -> OperatorEnum.FALSE);

        operatorSupplier.put(OperatorEnum.PLUS.getName(), () -> ExpressionFactory.plus());
    }

    /**
     * 每次获取新创建一个对象
     * @param op 运算符，如 + - * / def let 等
     * @return 对应的运算符表达式，可以执行响应的计算
     */
    @Override
    public Operator getOperator(String op) {
        return operatorSupplier.get(op).get();
    }
}
