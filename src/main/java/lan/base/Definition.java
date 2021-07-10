package lan.base;

import lan.ast.Operator;

/**
 * 语言定义
 */
public interface Definition {

    /**
     * 每次获取新创建一个对象
     * @param op 运算符，如 + - * / def let 等
     * @return 对应的运算符表达式，可以执行响应的计算
     */
    Operator getOperator(String op);
}
