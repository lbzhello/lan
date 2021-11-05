package lan.base;

import lan.ast.Expression;
import lan.ast.Keyword;
import lan.ast.Operator;

/**
 * 语言定义
 */
public interface Definition {
    boolean isOperator(Expression op);

    /**
     * 判断标识符是否是运算符
     * @param op
     * @return
     */
    boolean isOperator(String op);

    default boolean isOperator(char op) {
        return isOperator(String.valueOf(op));
    }
    
    /**
     * 获取运算符表达式
     * @param op 运算符，如 + - * / 等
     * @return 对应的运算符表达式，可以执行响应的计算
     */
    Operator createOperator(String op);

    /**
     * 判断标识符是否是关键字
     * @param keyword
     * @return
     */
    boolean isKeyWord(String keyword);

    /**
     * 获取关键字表达式
     * @param keyword
     * @return
     */
    Keyword createKeyword(String keyword);

    /**
     * 获取运算符优先级
     * @param operatorName
     * @return
     */
    Integer getPrecedence(String operatorName);
}
