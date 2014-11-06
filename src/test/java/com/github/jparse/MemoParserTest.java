/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Igor Konev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.jparse;

import org.junit.Before;
import org.junit.Test;

import static com.github.jparse.CharParsers.literal;
import static com.github.jparse.CharParsers.pattern;
import static com.github.jparse.Sequences.fromCharSequence;
import static com.github.jparse.Sequences.withMemo;
import static org.junit.Assert.assertEquals;

public class MemoParserTest {

    private static final Parser<Character, String> plus = literal("+");
    private static final Parser<Character, String> num = pattern("\\d+");
    private static final String SEQUENCE = "1+2+3";
    private static final Pair<Pair<Pair<Pair<String, String>, String>, String>, String> RESULT = Pair.create(
            Pair.create(Pair.create(Pair.create("1", "+"), "2"), "+"), "3");
    private FluentParser<Character, Object> exprRef;
    private FluentParser<Character, Object> xRef;
    private FluentParser<Character, Pair<Object, Object>> yRef;
    private MemoParser<Character, Object> expr;
    private MemoParser<Character, Object> x;
    private MemoParser<Character, Pair<Object, Object>> y;
    private Sequence<Character> sequence;

    @Before
    public void setUp() throws Exception {
        exprRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence) {
                return expr.parse(sequence);
            }
        };
        xRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence) {
                return x.parse(sequence);
            }
        };
        yRef = new FluentParser<Character, Pair<Object, Object>>() {
            @Override
            public ParseResult<Character, Pair<Object, Object>> parse(Sequence<Character> sequence) {
                return y.parse(sequence);
            }
        };
        sequence = withMemo(fromCharSequence(SEQUENCE));
    }

    @Test
    public void test1() {
        expr = new MemoParser<>(exprRef.then(plus).then(num).orelse(num));
        ParseResult<Character, Object> result = expr.phrase().parse(sequence);
        assertEquals(RESULT, result.getResult());
    }

    @Test
    public void test2() {
        expr = new MemoParser<>(exprRef.then(plus).then(num).orelse(exprRef.then(plus).then(num)).orelse(num));
        ParseResult<Character, Object> result = expr.phrase().parse(sequence);
        assertEquals(RESULT, result.getResult());
    }

    @Test
    public void test3() {
        expr = new MemoParser<>(xRef.then(plus).then(num).orelse(num));
        x = new MemoParser<>(exprRef);
        ParseResult<Character, Object> result = expr.phrase().parse(sequence);
        assertEquals(RESULT, result.getResult());
    }

    @Test
    public void test4() {
        expr = new MemoParser<>(exprRef);
        ParseResult<Character, Object> result = expr.phrase().parse(withMemo(fromCharSequence("dummy")));
        assertEquals("infinite left recursion detected", result.getMessage());
    }

    @Test
    public void test5() {
        x = new MemoParser<>(yRef.orelse(literal("a")));
        y = new MemoParser<>(xRef.then(xRef.orelse(yRef)));
        ParseResult<Character, Object> result = x.phrase().parse(withMemo(fromCharSequence("a a")));
        assertEquals(Pair.create("a", "a"), result.getResult());
    }
}
