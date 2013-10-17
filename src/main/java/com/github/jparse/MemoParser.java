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

import java.util.HashMap;
import java.util.Map;

import static com.github.jparse.Objects.requireNonNull;
import static com.github.jparse.ParseResult.failure;

final class MemoParser<T, U> extends FluentParser<T, U> {

    private static final Object KEY = new Object();
    private static final int DETECTED = 1;
    private static final int SKIP = 2;

    private final Parser<T, ? extends U> parser;

    MemoParser(Parser<T, ? extends U> parser) {
        this.parser = requireNonNull(parser);
    }

    @Override
    public ParseResult<T, U> parse(Sequence<T> sequence, ParseContext context) {
        Map<Sequence<T>, SequenceEntry<T, U>> sequenceEntries = context.get(KEY);
        if (sequenceEntries == null) {
            sequenceEntries = new HashMap<Sequence<T>, SequenceEntry<T, U>>();
            context.put(KEY, sequenceEntries);
            SequenceEntry<T, U> sequenceEntry = new SequenceEntry<T, U>();
            sequenceEntries.put(sequence, sequenceEntry);
            return setup(sequence, context, sequenceEntry);
        }
        SequenceEntry<T, U> sequenceEntry = sequenceEntries.get(requireNonNull(sequence));
        if (sequenceEntry == null) {
            sequenceEntry = new SequenceEntry<T, U>();
            sequenceEntries.put(sequence, sequenceEntry);
            return setup(sequence, context, sequenceEntry);
        }
        ParserEntry<T, U> parserEntry = sequenceEntry.parserEntries.get(parser);
        if (parserEntry == null) {
            return setup(sequence, context, sequenceEntry);
        }
        return recall(sequence, context, sequenceEntry, parserEntry);
    }

    private ParseResult<T, U> setup(Sequence<T> sequence, ParseContext context, SequenceEntry<T, U> sequenceEntry) {
        ParserEntry<T, U> parserEntry = new ParserEntry<T, U>();
        sequenceEntry.parserEntries.put(parser, parserEntry);
        sequenceEntry.stack = new StackEntry<T, U>(parserEntry, sequenceEntry.stack);
        ParseResult<T, ? extends U> result = parser.parse(sequence, context);
        sequenceEntry.stack = sequenceEntry.stack.next;
        if (parserEntry.state == SKIP) {
            return result.cast();
        }
        if (parserEntry.state != DETECTED || !result.isSuccess()) {
            parserEntry.result = result.cast();
            return result.cast();
        }
        while (true) {
            parserEntry.result = result.cast();
            result = parser.parse(sequence, context);
            if (result.isError()) {
                return result.cast();
            }
            if (result.isFailure() || result.getRest().length() >= parserEntry.result.getRest().length()) {
                return parserEntry.result;
            }
        }
    }

    private ParseResult<T, U> recall(Sequence<T> sequence, ParseContext context, SequenceEntry<T, U> sequenceEntry,
            ParserEntry<T, U> parserEntry) {
        if (parserEntry.state == SKIP) {
            return parser.parse(sequence, context).cast();
        }
        ParseResult<T, U> result = parserEntry.result;
        if (result != null) {
            return result;
        }
        StackEntry<T, U> stack = sequenceEntry.stack;
        while (stack.parserEntry != parserEntry) {
            stack.parserEntry.state = SKIP;
            stack = stack.next;
        }
        parserEntry.state = DETECTED;
        return failure("infinite left recursion detected", sequence);
    }

    private static final class SequenceEntry<T, U> {

        private final Map<Parser<T, ? extends U>, ParserEntry<T, U>> parserEntries =
                new HashMap<Parser<T, ? extends U>, ParserEntry<T, U>>();
        private StackEntry<T, U> stack;
    }

    private static final class ParserEntry<T, U> {

        private ParseResult<T, U> result;
        private int state;
    }

    private static final class StackEntry<T, U> {

        private final ParserEntry<T, U> parserEntry;
        private final StackEntry<T, U> next;

        private StackEntry(ParserEntry<T, U> parserEntry, StackEntry<T, U> next) {
            this.parserEntry = parserEntry;
            this.next = next;
        }
    }
}
