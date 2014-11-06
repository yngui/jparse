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

package com.github.jparse.benchmark;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.github.jparse.FluentParser;
import com.github.jparse.Pair;
import com.github.jparse.ParseResult;
import com.github.jparse.Parser;
import com.github.jparse.Sequence;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.jparse.CharParsers.literal;
import static com.github.jparse.Sequences.fromCharSequence;
import static com.github.jparse.Sequences.withMemo;

public class MemoBenchmark extends AbstractBenchmark {

    private FluentParser<Character, Object> rrOrig;
    private FluentParser<Character, Object> rrMod;
    private FluentParser<Character, Object> lrMod;
    private Sequence<Character> sequence;

    @Before
    public void setUp() throws Exception {
        FluentParser<Character, String> one = literal("1");

        Parser<Character, Object> rrOrigRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence) {
                return rrOrig.parse(sequence);
            }
        };
        rrOrig = new RrOrigMemoParser<>(one.then(rrOrigRef).orelse(one),
                new HashMap<Pair<Parser<Character, Object>, Sequence<Character>>, ParseResult<Character, Object>>());

        Parser<Character, Object> rrModRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence) {
                return rrMod.parse(sequence);
            }
        };
        rrMod = one.then(rrModRef).orelse(one).memo();

        FluentParser<Character, Object> lrModRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence) {
                return lrMod.parse(sequence);
            }
        };
        lrMod = lrModRef.then(one).orelse(one).memo();

        StringBuilder sb = new StringBuilder(10000);
        for (int i = 0; i < 10000; i++) {
            sb.append('1');
        }
        sequence = withMemo(fromCharSequence(sb.toString()));
    }

    @Test
    public void rrOrig() {
        rrOrig.phrase().parse(sequence);
    }

    @Test
    public void rrMod() {
        rrMod.phrase().parse(sequence);
    }

    @Test
    public void lrMod() {
        lrMod.phrase().parse(sequence);
    }

    private static final class RrOrigMemoParser<T, U> extends FluentParser<T, U> {

        private final Parser<T, U> parser;
        private final Map<Pair<Parser<T, U>, Sequence<T>>, ParseResult<T, U>> results;

        RrOrigMemoParser(Parser<T, U> parser, Map<Pair<Parser<T, U>, Sequence<T>>, ParseResult<T, U>> results) {
            this.parser = parser;
            this.results = results;
        }

        @Override
        public ParseResult<T, U> parse(Sequence<T> sequence) {
            Pair<Parser<T, U>, Sequence<T>> key = Pair.create(parser, sequence);
            ParseResult<T, U> result = results.get(key);
            if (result == null) {
                result = parser.parse(sequence);
                results.put(key, result);
            }
            return result.cast();
        }
    }
}
