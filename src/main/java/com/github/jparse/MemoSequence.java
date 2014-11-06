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

import java.util.Map;
import java.util.WeakHashMap;

import static java.util.Objects.requireNonNull;

final class MemoSequence<T> implements Sequence<T> {

    private final Sequence<T> sequence;
    final Map<Object, Object> values;

    MemoSequence(Sequence<T> sequence) {
        this(requireNonNull(sequence), new WeakHashMap<>());
    }

    private MemoSequence(Sequence<T> sequence, Map<Object, Object> values) {
        this.sequence = sequence;
        this.values = values;
    }

    @Override
    public int length() {
        return sequence.length();
    }

    @Override
    public T at(int index) {
        return sequence.at(index);
    }

    @Override
    public MemoSequence<T> subSequence(int start) {
        if (start == 0) {
            return this;
        }
        return new MemoSequence<>(sequence.subSequence(start), values);
    }

    @Override
    public MemoSequence<T> subSequence(int start, int end) {
        if (start == 0 && end == sequence.length()) {
            return this;
        }
        return new MemoSequence<>(sequence.subSequence(start, end), values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MemoSequence)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        MemoSequence<T> other = (MemoSequence<T>) obj;
        return sequence.equals(other.sequence);
    }

    @Override
    public int hashCode() {
        return sequence.hashCode();
    }

    @Override
    public String toString() {
        return sequence.toString();
    }
}
