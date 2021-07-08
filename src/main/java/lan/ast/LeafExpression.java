package lan.ast;

/**
 * 叶子节点，代表一个值，求值 {@link #eval()} 时不会再进行计算
 * 直接返回自身
 * 如字符串，数字，符号等
 */
public interface LeafExpression extends Expression {
    default Expression eval() {
        return this;
    }
}
