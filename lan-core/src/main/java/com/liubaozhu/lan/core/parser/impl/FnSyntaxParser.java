package com.liubaozhu.lan.core.parser.impl;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.lexer.LanLexer;
import com.liubaozhu.lan.core.parser.LanParser;
import com.liubaozhu.lan.core.parser.SyntaxParser;

public class FnSyntaxParser implements SyntaxParser {
    private LanLexer lexer;
    private LanParser lanParser;

    public FnSyntaxParser(LanLexer lexer, LanParser lanParser) {
        this.lexer = lexer;
        this.lanParser = lanParser;
    }

    @Override
    public Expression parse() {
        return null;
    }
}
