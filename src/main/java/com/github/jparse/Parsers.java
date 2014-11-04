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

public final class Parsers {

    private Parsers() {
    }

    public static <T> ElemParser<T> elem(T elem) {
        return new ElemParser<>(elem);
    }

    public static <T, U> SuccessParser<T, U> success(U result) {
        return new SuccessParser<>(result);
    }

    public static <T, U> FailureParser<T, U> failure(String message) {
        return new FailureParser<>(message);
    }

    public static <T, U> ErrorParser<T, U> error(String message) {
        return new ErrorParser<>(message);
    }

    public static <T, U, V> ThenParser<T, U, V> then(Parser<T, U> parser1, Parser<T, V> parser2) {
        return new ThenParser<>(parser1, parser2);
    }

    public static <T, U> ThenLeftParser<T, U> thenLeft(Parser<T, U> parser1, Parser<T, ?> parser2) {
        return new ThenLeftParser<>(parser1, parser2);
    }

    public static <T, U> ThenRightParser<T, U> thenRight(Parser<T, ?> parser1, Parser<T, U> parser2) {
        return new ThenRightParser<>(parser1, parser2);
    }

    public static <T, U> OrelseParser<T, U> orelse(Parser<T, ? extends U> parser1, Parser<T, ? extends U> parser2) {
        return new OrelseParser<>(parser1, parser2);
    }

    public static <T, U> OptParser<T, U> opt(Parser<T, U> parser) {
        return new OptParser<>(parser);
    }

    public static <T, U> RepParser<T, U> rep(Parser<T, U> parser) {
        return new RepParser<>(parser);
    }

    public static <T, U> Rep1Parser<T, U> rep1(Parser<T, U> parser) {
        return new Rep1Parser<>(parser);
    }

    public static <T, U, V> MapParser<T, U, V> map(Parser<T, U> parser, Function<? super U, ? extends V> function) {
        return new MapParser<>(parser, function);
    }

    public static <T, U> MemoParser<T, U> memo(Parser<T, U> parser, MemoParser.Context<T> context) {
        return new MemoParser<>(parser, context);
    }

    public static <T, U> PhraseParser<T, U> phrase(Parser<T, U> parser) {
        return new PhraseParser<>(parser);
    }

    public static <T, U> WithFailureMessageParser<T, U> withFailureMessage(Parser<T, U> parser, String message) {
        return new WithFailureMessageParser<>(parser, message);
    }

    public static <T, U> WithErrorMessageParser<T, U> withErrorMessage(Parser<T, U> parser, String message) {
        return new WithErrorMessageParser<>(parser, message);
    }

    public static <T, U> AsFailureParser<T, U> asFailure(Parser<T, U> parser) {
        return new AsFailureParser<>(parser);
    }

    public static <T, U> AsErrorParser<T, U> asError(Parser<T, U> parser) {
        return new AsErrorParser<>(parser);
    }

    public static <T, U> LogParser<T, U> log(Parser<T, U> parser, String name, LogParser.Context context) {
        return new LogParser<>(parser, name, context);
    }
}
