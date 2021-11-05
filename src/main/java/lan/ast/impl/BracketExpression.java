package lan.ast.impl;

import lan.ast.Expression;
import lan.util.AbstractContainer;

public class BracketExpression extends AbstractContainer implements Expression {
    public BracketExpression(Expression... expressions) {
        super(expressions);
    }

    @Override
    public Expression eval() {
        return this;
    }
}
