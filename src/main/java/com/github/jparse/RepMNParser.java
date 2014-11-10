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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.jparse.ParseResult.success;
import static java.util.Objects.requireNonNull;

final class RepMNParser<T, U> extends FluentParser<T, List<U>> {

    private final Parser<T, ? extends U> parser;
    private final int m;
    private final int n;

    RepMNParser(Parser<T, ? extends U> parser, int m) {
        if (m < 0) {
            throw new IllegalArgumentException();
        }
        this.parser = requireNonNull(parser);
        this.m = m;
        n = -1;
    }

    RepMNParser(Parser<T, ? extends U> parser, int m, int n) {
        if (m < 0) {
            throw new IllegalArgumentException();
        }
        if (n < m) {
            throw new IllegalArgumentException();
        }
        this.parser = requireNonNull(parser);
        this.m = m;
        this.n = n;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ParseResult<T, List<U>> parse(Sequence<T> sequence) {
        if (n == 0) {
            return success(Collections.<U>emptyList(), sequence);
        }
        List<U> list = new ArrayList<>();
        Sequence<T> rest = sequence;
        for (int i = 0; i < m; i++) {
            ParseResult<T, ? extends U> result = parser.parse(rest);
            if (!result.isSuccess()) {
                return (ParseResult<T, List<U>>) result;
            }
            list.add(result.getResult());
            rest = result.getRest();
        }
        if (n == -1) {
            return parseToInfinity(rest, list);
        } else {
            return parseToN(rest, list);
        }
    }

    @SuppressWarnings("unchecked")
    private ParseResult<T, List<U>> parseToInfinity(Sequence<T> sequence, List<U> list) {
        Sequence<T> rest = sequence;
        for (int i = m; i < n; i++) {
            ParseResult<T, ? extends U> result = parser.parse(rest);
            if (result.isFailure()) {
                break;
            }
            if (result.isError()) {
                return (ParseResult<T, List<U>>) result;
            }
            list.add(result.getResult());
            rest = result.getRest();
        }
        return success(Collections.unmodifiableList(list), rest);
    }

    @SuppressWarnings("unchecked")
    private ParseResult<T, List<U>> parseToN(Sequence<T> sequence, List<U> list) {
        Sequence<T> rest = sequence;
        while (true) {
            ParseResult<T, ? extends U> result = parser.parse(rest);
            if (result.isFailure()) {
                return success(Collections.unmodifiableList(list), rest);
            }
            if (result.isError()) {
                return (ParseResult<T, List<U>>) result;
            }
            list.add(result.getResult());
            rest = result.getRest();
        }
    }
}