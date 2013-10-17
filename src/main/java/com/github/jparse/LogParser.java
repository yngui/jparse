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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.jparse.Objects.requireNonNull;

final class LogParser<T, U> extends FluentParser<T, U> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogParser.class);
    private static final Object KEY = new Object();
    private static final String INDENT = "  ";

    private final Parser<T, ? extends U> parser;
    private final String name;

    LogParser(Parser<T, ? extends U> parser, String name) {
        this.parser = requireNonNull(parser);
        this.name = requireNonNull(name);
    }

    @Override
    public ParseResult<T, U> parse(Sequence<T> sequence, ParseContext context) {
        if (!LOGGER.isDebugEnabled()) {
            return parser.parse(sequence, context).cast();
        }
        Indent indent = context.get(KEY);
        if (indent == null) {
            indent = new Indent();
            context.put(KEY, indent);
        }
        int value = indent.value;
        StringBuilder sb = new StringBuilder(value);
        for (int i = 0; i < value; i++) {
            sb.append(INDENT);
        }
        LOGGER.debug("{}{} <-- {}", sb, name, sequence);
        indent.value++;
        ParseResult<T, U> result = parser.parse(sequence, context).cast();
        indent.value--;
        LOGGER.debug("{}{} --> {}", sb, name, result);
        return result;
    }

    private static final class Indent {

        private int value;
    }
}
