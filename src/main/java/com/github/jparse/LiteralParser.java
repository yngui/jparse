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

import java.util.regex.Pattern;

import static com.github.jparse.ParseResult.failure;
import static com.github.jparse.ParseResult.success;
import static java.util.Objects.requireNonNull;

public final class LiteralParser extends CharParser<String> {

    private final String literal;

    public LiteralParser(String literal) {
        this.literal = requireNonNull(literal);
    }

    public LiteralParser(String literal, Pattern whitespacePattern) {
        super(whitespacePattern);
        this.literal = requireNonNull(literal);
    }

    @Override
    public ParseResult<Character, String> parse(Sequence<Character> sequence) {
        CharSequence charSequence = CharSequences.forSequence(sequence);
        int start = handleWhitespace(charSequence);
        int end = start + literal.length();
        if (end > charSequence.length() || !literal.contentEquals(charSequence.subSequence(start, end))) {
            return failure('\'' + literal + "' expected", sequence.subSequence(start));
        }
        return success(literal, sequence.subSequence(end));
    }
}