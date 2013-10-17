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

public final class Sequences {

    private Sequences() {
    }

    @SuppressWarnings("unchecked")
    public static Sequence<Character> forCharSequence(CharSequence sequence) {
        if (sequence instanceof Sequence) {
            return (Sequence<Character>) sequence;
        }
        return new CharSequenceAdapter(sequence, 0, sequence.length());
    }

    private static final class CharSequenceAdapter implements Sequence<Character>, CharSequence {

        private final CharSequence sequence;
        private final int index;
        private final int length;

        private CharSequenceAdapter(CharSequence sequence, int index, int length) {
            this.sequence = sequence;
            this.index = index;
            this.length = length;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public Character at(int index) {
            return charAt(index);
        }

        @Override
        public char charAt(int index) {
            return sequence.charAt(this.index + index);
        }

        @Override
        public CharSequenceAdapter subSequence(int start) {
            if (start < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (start > length) {
                throw new IndexOutOfBoundsException();
            }
            if (start == 0) {
                return this;
            }
            return new CharSequenceAdapter(sequence, index + start, length - start);
        }

        @Override
        public CharSequenceAdapter subSequence(int start, int end) {
            if (start < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (end > length) {
                throw new IndexOutOfBoundsException();
            }
            if (start > end) {
                throw new IndexOutOfBoundsException();
            }
            if (start == 0 && end == length) {
                return this;
            }
            return new CharSequenceAdapter(sequence, index + start, end - start);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CharSequenceAdapter)) {
                return false;
            }
            CharSequenceAdapter other = (CharSequenceAdapter) obj;
            return sequence.equals(other.sequence) && index == other.index && length == other.length;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + sequence.hashCode();
            result = 31 * result + index;
            result = 31 * result + length;
            return result;
        }

        @Override
        public String toString() {
            return sequence.subSequence(index, index + length).toString();
        }
    }
}
