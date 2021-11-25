package lan.ast.java;

import lan.ast.Expression;

/**
 * java 中的方法引用
 */
public interface MethodInfo extends Expression {
    /**
     * 绑定方法接收者/调用者
     * 此方法会返回一个新的对象
     * @return
     */
    MethodInfo bindTo(Object caller);

    /**
     * 调用方法
     * @param args
     * @return
     */
    Expression invoke(Object... args);
}
