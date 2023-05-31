package com.liubaozhu.lan.core.parser.impl;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.expression.*;
import com.liubaozhu.lan.core.exception.ParseException;
import com.liubaozhu.lan.core.parser.AbstractSyntaxParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 函数语法结构解析
 */
public class LambdaParser extends AbstractSyntaxParser {

    /**
     * lambda 表达式
     * {p1::Int p2:Int -> Int | return p1 + p2}
     * 1. 返回值 -> 可以省略；
     * 2. 如果 | 位于行尾，可以省略；
     * 3. 如果有参数，-> 和 | 只能省略一个；
     * 5. 如果无参数，-> 和 | 都可以省略；
     * @return
     */
    @Override
    public LambdaExpression parse() {
        LambdaExpression lambda = new LambdaExpression();

        lanLexer.next(); // eat '{'
        lanLexer.skipBlank('\n');

        // 不含参数
        if (lanLexer.currentIs('|') // { | ...
                || lanLexer.currentIs(';')) { // { ; ...
            lanLexer.skipBlank('\n', '|', ';');

            // { | ; }
            if (lanLexer.currentIs('}')) {
                lanLexer.next(); // eat '}'
                return lambda;
            }
            parseLambdaBody(lambda);
            return lambda;
        }

        // { -> ... 参数为空时可以省略，只有返回值形式
        if (lanLexer.prefetchNextChars(2, "->")) {
            parseLambdaReturnType(lambda);
        }

        if (lanLexer.currentIs('}')) { // {} 空lambda
            lanLexer.next();
            return lambda;
        }

        // 解析 lambda 参数
        parseLambdaParams(lambda);
        return lambda;
    }

    /**
     *
     * 解析并检查是否为空格形式的参数；
     * 栗子：
     *   { p1 p2
     *     p3
     *     p4 p5 -> ...
     *
     * 如果不是空格形式参数，会尝试当成 请求体解析
     * 判断 statement 是否是 lambda 参数
     * @param statement
     * @return true 空格分割的参数形式
     *         false 非空格，后续尝试当作请求体解析，请求体遇到 | 或 -> 则抛出异常
     */
    private boolean checkLambdaSpaceParams(Expression statement, List<Expression> lambdaParamCache) {
        lanLexer.skipBlank('\n');

        // { p1; ... 确定是语句，lambda 不含参数
        if (lanLexer.currentIs(';')) {
            lanLexer.skipBlank('\n', ';');
            return false;
        }

        // { p1 p2, ... 空格形式参数带有 ','
        if (lanLexer.currentIs(',')) {
            throw new ParseException("lambda 解析错误：空格分割的参数不能加上 ','，或者参数全部采用 ',' 分割", lanLexer);
        }

        // 如果是命令式参数，则 CommandExpression 列表中的值是变量声明类型
        // 如上面 p1 p2
        if (statement instanceof CommandExpression command) {
            for (Expression elem : command.toArray()) {
                if (!(elem instanceof VariableExpression)
                        && !(elem instanceof SymbolExpression)) {
                    return false;
                }
            }
            lambdaParamCache.addAll(List.of(command.toArray()));
            return true;
        }

        // 变量声明类参数
        // 可能只有一个变量，如上面 p3
        if (statement instanceof VariableExpression
                || statement instanceof SymbolExpression
                || statement instanceof LambdaExpression) {
            lambdaParamCache.add(statement);
            return true;
        }

        return false;
    }

    /**
     * 完成 lambda 参数后续解析，如 '|'  '->' '}'
     * @param lambda
     * @param lambdaParamCache
     * @param lambdaBodyCache
     * @return true 完成 lambda 参数后续解析
     *         false 未完成解析，可能是不含参数形式
     */
    private boolean parseLambdaParamsComplete(LambdaExpression lambda,
                                              List<Expression> lambdaParamCache,
                                              List<Expression> lambdaBodyCache) {
        // {p1, p2 -> ...
        if (lanLexer.prefetchNextChars(2, "->")) {
            lambda.addParams(lambdaParamCache);
            parseLambdaReturnType(lambda);
            return true;
        }

        // {p1, p2 | ...
        if (lanLexer.currentIs('|')) {
            lanLexer.next();
            lambda.addParams(lambdaParamCache);
            parseLambdaBody(lambda);
            return true;
        }

        // {p1...} 请求体全是变量声明式语句
        if (lanLexer.currentIs('}')) {
            lanLexer.next();
            lambda.addCode(lambdaBodyCache);
            return true;
        }

        return false;
    }

    /**
     * 解析 lambda 参数
     * 括号 {(p1, p2, p3) -> p4 | ...}
     * 逗号分割 {p1, p2, p3 -> p4 | ...}
     * 空格分割 {p1 p2 p3 -> p4 | ...}
     * @param lambda
     * @return true 有参数，false 无参数
     */
    private void parseLambdaParams(LambdaExpression lambda) {
        boolean containsComma = true;

        List<Expression> lambdaBodyCache = new ArrayList<>();
        List<Expression> lambdaParamCache = new ArrayList<>();

        Expression first = lanParser.statement();
        lambdaBodyCache.add(first);

        // 括号形式参数
        // {(p1, p2, p3) -> ...
        if (first instanceof TupleExpression tuple) {
            lambdaParamCache.addAll(List.of(tuple.toArray()));
            if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) {
                return;
            }
        }

        // 逗号分割的参数形式
        if (first instanceof VariableExpression // { p1::Int, ...
                || first instanceof SymbolExpression // { p1, ...
                || first instanceof LambdaExpression // { {Int -> Int}, ...
        ) {
            lambdaParamCache.add(first);
            // {p1 -> ... 单参数形式
            if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) {
                return;
            }

            // {p1, ... 逗号分割的参数
            if (lanLexer.currentIs(',')) {
                Expression statement = first;
                while (statement instanceof VariableExpression // { p1::Int, ...
                        || statement instanceof SymbolExpression // { p1, ...
                        || statement instanceof LambdaExpression) { // { {Int | Int}, ...
                    if (lanLexer.currentIs(',')) {
                        lanLexer.next();
                        lanLexer.skipBlank('\n');
                        // {p1, p2, -> ... 最后一个参数可以是 ','
                        if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) {
                            return;
                        }
                    } else if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) { // p1, p2 -> 解析完成
                        return;
                    } else {
                        // { p1, p2 p3 ...
                        throw new ParseException("lambda 参数缺少 ','", lanLexer);
                    }

                    statement = lanParser.statement();
                    lambdaBodyCache.add(statement);
                    lambdaParamCache.add(statement);
                }

                // {p1, p2 + p3 ...
                throw new ParseException("lambda 解析错误，参数类型不支持", lanLexer);
            } else { // 空格分割的形式
                // 空格分割的参数形式后面会重新解析
                lambdaParamCache.clear();
            }
        }

        // 空格分割的参数形式
        // {p1 p2
        //     p3::Str p4::Int -> ...
        Expression statement = first;
        while (checkLambdaSpaceParams(statement, lambdaParamCache)) {
            if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) {
                return;
            }
            // 继续解析下一个语句
            statement = lanParser.statement();
            lambdaBodyCache.add(statement);
        }

        /**
         * 到这里表示无参数形式的 lambda，
         * 如果出现 '->' '|' 等参数分割符则表示错误
         *
         * {
         *   a + b;
         *   c -> // 这里错误
         *   ret 3;
         * }
         */
        lambda.addCode(lambdaBodyCache);
        parseLambdaBody(lambda);
    }

    private void parseLambdaReturnType(LambdaExpression lambda) {
        Expression retType = lanParser.statement(); // 可能解析错误

        if (retType instanceof VariableExpression // { p1 -> a::Int ...
                || retType instanceof SymbolExpression  // { p1 -> Int ...
                || retType instanceof TupleExpression // { p1 -> (Int, Str) ...
                || retType instanceof LambdaExpression // { p1 -> {...} ...
        ) {
            lambda.setReturnType(retType);
        } else {
            throw new ParseException("lambda 解析错误：返回值类型不正确", lanLexer);
        }

        lanLexer.skipBlank();
        // -> 后面必须由分割符 '|', '\n'
        if (lanLexer.currentIs('|') // { p1 -> retType | ...
                || lanLexer.currentIs('\n') // { p1 -> retType
                || lanLexer.currentIs(';') // { p1 -> retType; ... 返回值支持 ';' 结尾
                || lanLexer.currentIs('}') // { p1 -> retType }
        ) {
            lanLexer.skipBlank('|', '\n', ';');
            if (lanLexer.currentIs('}')) {
                lanLexer.next();
                return;
            }
        } else {
            throw new ParseException("lambda 解析错误：返回值后是否缺少换行符或分割符 '|'？", lanLexer);
        }

        parseLambdaBody(lambda);
    }

    // 解析 lambda 方法体；
    private void parseLambdaBody(LambdaExpression lambda) {
        while (lanLexer.currentNot('}') && lanLexer.hasNext()) {
            Expression statement = lanParser.statement();
            lambda.addCode(statement);
            lanLexer.skipBlank('\n', ';');

            // 请求体不能包含 '|' 或 '->'
            // { p1 p2 + p3 -> ...
            if (lanLexer.currentIs('|') || lanLexer.prefetchNextChars(2, "->")) {
                throw new ParseException("lambda解析错误：请求体不能包含 '|' 或 '->'，是否参数格式错误？", lanLexer);
            }
        }

        if (lanLexer.currentNot('}')) {
            throw new ParseException("lambda 解析错误：是否缺少关闭符 '}' ？", lanLexer);
        }

        lanLexer.next(); // eat '}'
    }
}
