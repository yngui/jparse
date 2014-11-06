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

import static java.util.Objects.requireNonNull;

final class ThenRightParser<T, U> extends FluentParser<T, U> {

    private final Parser<T, ?> parser1;
    private final Parser<T, ? extends U> parser2;

    ThenRightParser(Parser<T, ?> parser1, Parser<T, ? extends U> parser2) {
        this.parser1 = requireNonNull(parser1);
        this.parser2 = requireNonNull(parser2);
    }

    @Override
    public ParseResult<T, U> parse(Sequence<T> sequence) {
        ParseResult<T, ?> result1 = parser1.parse(sequence);
        if (result1.isSuccess()) {
            return parser2.parse(result1.getRest()).cast();
        } else {
            return result1.cast();
        }
    }
}
