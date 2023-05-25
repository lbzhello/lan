package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.Expression;

import java.util.ArrayList;
import java.util.List;

public class LambdaExpression implements Expression {
    // 返回值类型
    private Expression returnType;
    // 参数列表
    private List<Expression> params = new ArrayList<>();
    // 代码
    private List<Expression> codes = new ArrayList<>();

    public void setReturnType(Expression returnType) {
        this.returnType = returnType;
    }

    /**
     * 增加参数
     * @param param
     */
    public void addParams(Expression param) {
        this.params.add(param);
    }

    /**
     * 增加语句表达式
     * @param code
     */
    public void addCode(Expression code) {
        this.codes.add(code);
    }
}
