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

package com.github.jparse.benchmark;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.github.jparse.FluentParser;
import com.github.jparse.ParseContext;
import com.github.jparse.ParseResult;
import com.github.jparse.Parser;
import com.github.jparse.Sequence;
import com.github.jparse.Sequences;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.jparse.CharParsers.literal;

public class MemoBenchmark extends AbstractBenchmark {

    private static final FluentParser<Character, Object> RR_ORIG;
    private static final FluentParser<Character, Object> RR_MOD;
    private static final FluentParser<Character, Object> LR_MOD;
    private static final Sequence<Character> SEQUENCE;

    static {
        FluentParser<Character, String> one = literal("1");

        FluentParser<Character, Object> rrOrigRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence, ParseContext context) {
                return RR_ORIG.parse(sequence, context);
            }
        };
        RR_ORIG = new MemoParser<>(one.then(rrOrigRef).orelse(one));

        FluentParser<Character, Object> rrModRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence, ParseContext context) {
                return RR_MOD.parse(sequence, context);
            }
        };
        RR_MOD = one.then(rrModRef).orelse(one).memo();

        FluentParser<Character, Object> lrModRef = new FluentParser<Character, Object>() {
            @Override
            public ParseResult<Character, Object> parse(Sequence<Character> sequence, ParseContext context) {
                return LR_MOD.parse(sequence, context);
            }
        };
        LR_MOD = lrModRef.then(one).orelse(one).memo();

        StringBuilder sb = new StringBuilder(10000);
        for (int i = 0; i < 10000; i++) {
            sb.append('1');
        }
        SEQUENCE = Sequences.forCharSequence(sb.toString());
    }

    @Test
    public void rrOrig() {
        RR_ORIG.phrase().parse(SEQUENCE, new ParseContext());
    }

    @Test
    public void rrMod() {
        RR_MOD.phrase().parse(SEQUENCE, new ParseContext());
    }

    @Test
    public void lrMod() {
        LR_MOD.phrase().parse(SEQUENCE, new ParseContext());
    }

    private static final class MemoParser<T, U> extends FluentParser<T, U> {

        private static final Object KEY = new Object();

        private final Parser<T, U> parser;

        private MemoParser(Parser<T, U> parser) {
            this.parser = parser;
        }

        @Override
        public ParseResult<T, U> parse(Sequence<T> sequence, ParseContext context) {
            Map<Key<T, U>, ParseResult<T, U>> results = context.get(KEY);
            if (results == null) {
                results = new HashMap<>();
                context.put(KEY, results);
            }
            Key<T, U> key = new Key<>(parser, sequence);
            ParseResult<T, U> result = results.get(key);
            if (result == null) {
                result = parser.parse(sequence, context);
                results.put(key, result);
            }
            return result;
        }

        private static final class Key<T, V> {

            private final Parser<T, V> parser;
            private final Sequence<T> sequence;

            private Key(Parser<T, V> parser, Sequence<T> sequence) {
                this.parser = parser;
                this.sequence = sequence;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Key)) {
                    return false;
                }
                @SuppressWarnings("unchecked")
                Key<T, V> other = (Key<T, V>) obj;
                return parser.equals(other.parser) && sequence.equals(other.sequence);
            }

            @Override
            public int hashCode() {
                int result = 17;
                result = 31 * result + parser.hashCode();
                result = 31 * result + sequence.hashCode();
                return result;
            }
        }
    }
}
