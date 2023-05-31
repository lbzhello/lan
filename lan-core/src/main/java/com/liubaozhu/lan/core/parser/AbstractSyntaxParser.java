package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.lexer.LanLexer;

/**
 * 语法结构解析器抽象类
 * 提供词法解析器 LanLexer 和 语法解析器 LanParser 引用
 */
public abstract class AbstractSyntaxParser implements SyntaxParser {
    protected LanLexer lanLexer;
    protected LanParser lanParser;

    @Override
    public void setLanParser(LanParser lanParser) {
        this.lanParser = lanParser;
        this.lanLexer = lanParser.getLanLexer();
    }
}
