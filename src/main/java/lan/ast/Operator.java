package lan.ast;

/**
 * 基础运算符，系统（语言运行时）提供的运算符
 */
public interface Operator extends Expression {
    default void add(Expression operand) {

    }
}
