package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.ast.Expression;

/**
 * 语法结构解析器。用于特定语法结构的解析，比如循环语句 for，判断语句 if 等
 */
public interface SyntaxParser {
    Expression parse();
}
