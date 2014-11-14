/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Igor Konev
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

import org.junit.Test;

import static com.github.jparse.CharParsers.literal;
import static com.github.jparse.CharParsers.pattern;
import static com.github.jparse.Sequences.fromCharSequence;
import static com.github.jparse.StatefulSequences.withMemo;
import static org.junit.Assert.assertEquals;

public class MemoParserTest {

    private static final String SEQUENCE = "1+2+3";
    private static final Function<Pair<String, String>, String> concat = new Function<Pair<String, String>, String>() {
        @Override
        public String apply(Pair<String, String> arg) {
            return arg.getLeft() + arg.getRight();
        }
    };
    private static final Parser<Character, String> plus = literal("+");
    private static final Parser<Character, String> num = pattern("\\d+");
    private static final Sequence<Character> sequence = fromCharSequence(SEQUENCE);
    private final FluentParser<Character, String> exprRef = new FluentParser<Character, String>() {
        @Override
        public ParseResult<Character, ? extends String> parse(Sequence<Character> sequence) {
            return expr.parse(sequence);
        }
    };
    private final FluentParser<Character, String> xRef = new FluentParser<Character, String>() {
        @Override
        public ParseResult<Character, ? extends String> parse(Sequence<Character> sequence) {
            return x.parse(sequence);
        }
    };
    private final FluentParser<Character, String> yRef = new FluentParser<Character, String>() {
        @Override
        public ParseResult<Character, ? extends String> parse(Sequence<Character> sequence) {
            return y.parse(sequence);
        }
    };
    private MemoParser<Character, String> expr;
    private MemoParser<Character, String> x;
    private MemoParser<Character, String> y;

    @Test
    public void test1() {
        expr = new MemoParser<>(exprRef.then(plus).map(concat).then(num).map(concat).orelse(num));
        ParseResult<Character, ? extends String> result = expr.phrase().parse(withMemo(sequence));
        assertEquals(SEQUENCE, result.getResult());
    }

    @Test
    public void test2() {
        expr = new MemoParser<>(exprRef.then(plus)
                .map(concat)
                .then(num)
                .map(concat)
                .orelse(exprRef.then(plus).map(concat).then(num).map(concat))
                .orelse(num));
        ParseResult<Character, ? extends String> result = expr.phrase().parse(withMemo(sequence));
        assertEquals(SEQUENCE, result.getResult());
    }

    @Test
    public void test3() {
        expr = new MemoParser<>(xRef.then(plus).map(concat).then(num).map(concat).orelse(num));
        x = new MemoParser<>(exprRef);
        ParseResult<Character, ? extends String> result = expr.phrase().parse(withMemo(sequence));
        assertEquals(SEQUENCE, result.getResult());
    }

    @Test
    public void test4() {
        expr = new MemoParser<>(exprRef);
        ParseResult<Character, ?> result = expr.phrase().parse(withMemo(fromCharSequence("dummy")));
        assertEquals("infinite left recursion detected", result.getMessage());
    }

    @Test
    public void test5() {
        x = new MemoParser<>(yRef.orelse(literal("a")));
        y = new MemoParser<>(xRef.then(xRef.orelse(yRef)).map(concat));
        ParseResult<Character, ?> result = x.phrase().parse(withMemo(fromCharSequence("aa")));
        assertEquals("aa", result.getResult());
    }
}
