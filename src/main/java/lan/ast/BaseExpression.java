package lan.ast;

import lan.util.AbstractContainer;

import java.util.List;

/**
 * 表达式基类，提供基本容器功能，存放解析的代码流
 * e.g. expr1 expr2 expr3...
 */
public abstract class BaseExpression extends AbstractContainer implements Expression {
    @Override
    public Expression eval() {
        return this;
    }

    public void addAll(List<Expression> expressionList) {
        addAll(expressionList.toArray(new Expression[0]));
    }
}
