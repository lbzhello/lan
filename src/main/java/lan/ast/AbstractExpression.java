package lan.ast;

import lan.util.AbstractContainer;

/**
 * 抽线表达式，提供容器功能
 */
public abstract class AbstractExpression extends AbstractContainer implements Expression {
    public AbstractExpression(Expression... expressions) {
        super(expressions);
    }

    @Override
    public Expression eval() {
        return this;
    }
}
