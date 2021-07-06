package lan.ast;

/**
 * 抽象语法树顶层类
 */
public interface Expression {
    Expression eval();
}
