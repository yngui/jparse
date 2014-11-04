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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.jparse.ParseResult.success;
import static java.util.Objects.requireNonNull;

final class RepParser<T, U> extends FluentParser<T, List<U>> {

    private final Parser<T, ? extends U> parser;

    RepParser(Parser<T, ? extends U> parser) {
        this.parser = requireNonNull(parser);
    }

    @Override
    public ParseResult<T, List<U>> parse(Sequence<T> sequence, ParseContext context) {
        ParseResult<T, ? extends U> result = parser.parse(sequence, context);
        if (!result.isSuccess()) {
            return success(Collections.<U>emptyList(), sequence);
        }
        List<U> list = new ArrayList<>();
        Sequence<T> rest;
        do {
            list.add(result.getResult());
            rest = result.getRest();
            result = parser.parse(rest, context);
            if (result.isError()) {
                return result.cast();
            }
        } while (result.isSuccess());
        return success(Collections.unmodifiableList(list), rest);
    }
}
