package com.liubaozhu.lan.core;

import com.liubaozhu.lan.core.parser.CharIterator;
import com.liubaozhu.lan.core.parser.LanParser;
import org.junit.jupiter.api.Test;

public class LanStartTest {
    @Test
    public void keywordTest() {

    }

    @Test
    public void stringIteratorTest() {
        CharIterator iterator = LanParser.text("def sql do hello end");
        System.out.println(iterator.current());
    }
}
