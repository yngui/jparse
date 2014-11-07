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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static com.github.jparse.ParseResult.failure;
import static java.util.Objects.requireNonNull;

final class MemoParser<T, U> extends FluentParser<T, U> {

    private static final int DETECTED = 1;
    private static final int SKIP = 2;
    private static final Memo<Map<Sequence<?>, SequenceEntry>> SEQUENCE_ENTRIES = new Memo<Map<Sequence<?>,
            SequenceEntry>>() {
        @Override
        protected Map<Sequence<?>, SequenceEntry> initialValue() {
            return new IdentityHashMap<>();
        }
    };
    private final Parser<T, ? extends U> parser;

    MemoParser(Parser<T, ? extends U> parser) {
        this.parser = requireNonNull(parser);
    }

    @Override
    public ParseResult<T, ? extends U> parse(Sequence<T> sequence) {
        Map<Sequence<?>, SequenceEntry> sequenceEntries = SEQUENCE_ENTRIES.get(sequence);
        SequenceEntry sequenceEntry = sequenceEntries.get(sequence);
        if (sequenceEntry == null) {
            sequenceEntry = new SequenceEntry();
            sequenceEntries.put(sequence, sequenceEntry);
            return setup(sequence, sequenceEntry);
        } else {
            ParserEntry parserEntry = sequenceEntry.parserEntries.get(parser);
            if (parserEntry == null) {
                return setup(sequence, sequenceEntry);
            } else {
                return recall(sequence, sequenceEntry, parserEntry);
            }
        }
    }

    private ParseResult<T, ? extends U> setup(Sequence<T> sequence, SequenceEntry sequenceEntry) {
        ParserEntry parserEntry = new ParserEntry();
        sequenceEntry.parserEntries.put(parser, parserEntry);
        sequenceEntry.stack = new StackEntry(parserEntry, sequenceEntry.stack);
        ParseResult<T, ? extends U> result = parser.parse(sequence);
        sequenceEntry.stack = sequenceEntry.stack.next;
        if (parserEntry.state == SKIP) {
            return result;
        } else if (parserEntry.state == DETECTED && result.isSuccess()) {
            ParseResult<T, ? extends U> oldResult;
            do {
                oldResult = result;
                parserEntry.result = oldResult;
                result = parser.parse(sequence);
            } while (result.isSuccess() && result.getRest().length() < oldResult.getRest().length());
            if (result.isError()) {
                parserEntry.result = result;
                return result;
            } else {
                return oldResult;
            }
        } else {
            parserEntry.result = result;
            return result;
        }
    }

    private ParseResult<T, ? extends U> recall(Sequence<T> sequence, SequenceEntry sequenceEntry,
            ParserEntry parserEntry) {
        if (parserEntry.state == SKIP) {
            return parser.parse(sequence);
        } else {
            @SuppressWarnings("unchecked")
            ParseResult<T, ? extends U> result = (ParseResult<T, ? extends U>) parserEntry.result;
            if (result == null) {
                StackEntry stack = sequenceEntry.stack;
                while (stack.parserEntry != parserEntry) {
                    stack.parserEntry.state = SKIP;
                    stack = stack.next;
                }
                parserEntry.state = DETECTED;
                return failure("infinite left recursion detected", sequence);
            } else {
                return result;
            }
        }
    }

    private static final class SequenceEntry {

        final Map<Parser<?, ?>, ParserEntry> parserEntries = new HashMap<>();
        StackEntry stack;
    }

    private static final class ParserEntry {

        ParseResult<?, ?> result;
        int state;
    }

    private static final class StackEntry {

        final ParserEntry parserEntry;
        final StackEntry next;

        private StackEntry(ParserEntry parserEntry, StackEntry next) {
            this.parserEntry = parserEntry;
            this.next = next;
        }
    }
}
