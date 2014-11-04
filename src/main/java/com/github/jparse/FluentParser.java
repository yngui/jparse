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

import static java.util.Objects.requireNonNull;

public abstract class FluentParser<T, U> implements Parser<T, U> {

    private final Parser<T, U> parser;

    protected FluentParser() {
        parser = this;
    }

    FluentParser(Parser<T, U> parser) {
        this.parser = requireNonNull(parser);
    }

    public static <T, U> FluentParser<T, U> from(final Parser<T, U> parser) {
        if (parser instanceof FluentParser) {
            return (FluentParser<T, U>) parser;
        } else {
            return new FluentParser<T, U>(parser) {
                @Override
                public ParseResult<T, U> parse(Sequence<T> sequence) {
                    return parser.parse(sequence);
                }
            };
        }
    }

    @SuppressWarnings("unchecked")
    public final <V> FluentParser<T, V> cast() {
        return (FluentParser<T, V>) this;
    }

    public final <V> ThenParser<T, U, V> then(Parser<T, V> parser) {
        return Parsers.then(this.parser, parser);
    }

    public final ThenLeftParser<T, U> thenLeft(Parser<T, ?> parser) {
        return Parsers.thenLeft(this.parser, parser);
    }

    public final <V> ThenRightParser<T, V> thenRight(Parser<T, V> parser) {
        return Parsers.thenRight(this.parser, parser);
    }

    public final OrelseParser<T, Object> orelse(Parser<T, ?> parser) {
        return Parsers.<T, Object>orelse(this.parser, parser);
    }

    public final OptParser<T, U> opt() {
        return Parsers.opt(parser);
    }

    public final RepParser<T, U> rep() {
        return Parsers.rep(parser);
    }

    public final Rep1Parser<T, U> rep1() {
        return Parsers.rep1(parser);
    }

    public final <V> MapParser<T, U, V> map(Function<? super U, ? extends V> function) {
        return Parsers.map(parser, function);
    }

    public final MemoParser<T, U> memo(MemoParser.Context<T> context) {
        return Parsers.memo(parser, context);
    }

    public final PhraseParser<T, U> phrase() {
        return Parsers.phrase(parser);
    }

    public final WithFailureMessageParser<T, U> withFailureMessage(String message) {
        return Parsers.withFailureMessage(parser, message);
    }

    public final WithErrorMessageParser<T, U> withErrorMessage(String message) {
        return Parsers.withErrorMessage(parser, message);
    }

    public final AsFailureParser<T, U> asFailure() {
        return Parsers.asFailure(parser);
    }

    public final AsErrorParser<T, U> asError() {
        return Parsers.asError(parser);
    }

    public final LogParser<T, U> log(String name, LogParser.Context context) {
        return Parsers.log(parser, name, context);
    }
}
