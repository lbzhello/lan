package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.ast.Expression;

/**
 * 语法结构解析器。用于特定语法结构的解析，比如循环语句 for，判断语句 if 等
 */
public interface SyntaxParser {

    /**
     * 解析完整的关键字语法结构，并返回关键字语法树
     * @return
     */
    Expression parse();

    /**
     * 基础语法解析器，提供基本表达式解析，和其他语法结构解析
     * 新的语法结构需要遵循基础的语法形式
     * @param lanParser
     */
    void setLanParser(LanParser lanParser);
}
