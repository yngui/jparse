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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class CharParser<T> extends FluentParser<Character, T> {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private final Pattern whitespacePattern;

    protected CharParser() {
        this(WHITESPACE_PATTERN);
    }

    protected CharParser(Pattern whitespacePattern) {
        this.whitespacePattern = whitespacePattern;
    }

    protected final int handleWhitespace(CharSequence sequence) {
        if (whitespacePattern != null) {
            Matcher matcher = whitespacePattern.matcher(sequence);
            if (matcher.lookingAt()) {
                return matcher.end();
            }
        }
        return 0;
    }
}
