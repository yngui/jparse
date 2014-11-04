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

public final class CharSequences {

    private CharSequences() {
    }

    public static CharSequence forSequence(Sequence<Character> sequence) {
        if (sequence instanceof CharSequence) {
            return (CharSequence) sequence;
        }
        return new SequenceAdapter(sequence);
    }

    private static final class SequenceAdapter implements CharSequence {

        private final Sequence<Character> sequence;

        private SequenceAdapter(Sequence<Character> sequence) {
            this.sequence = requireNonNull(sequence);
        }

        @Override
        public int length() {
            return sequence.length();
        }

        @Override
        public char charAt(int index) {
            return sequence.at(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            if (start == 0 && end == sequence.length()) {
                return this;
            }
            return new SequenceAdapter(sequence.subSequence(start, end));
        }
    }
}
