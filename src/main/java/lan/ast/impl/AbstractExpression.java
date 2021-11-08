package lan.ast.impl;

import lan.ast.Expression;
import lan.util.AbstractContainer;

public abstract class AbstractExpression extends AbstractContainer implements Expression {
    public AbstractExpression(Expression... expressions) {
        super(expressions);
    }

    @Override
    public Expression eval() {
        return this;
    }
}
