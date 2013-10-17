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

import static com.github.jparse.Objects.requireNonNull;

public abstract class ParseResult<T, U> {

    protected final Sequence<T> rest;

    private ParseResult(Sequence<T> rest) {
        this.rest = requireNonNull(rest);
    }

    public static <T, U> ParseResult<T, U> success(U result, Sequence<T> rest) {
        return new Success<T, U>(result, rest);
    }

    public static <T, U> ParseResult<T, U> failure(String message, Sequence<T> rest) {
        return new Failure<T, U>(message, rest);
    }

    public static <T, U> ParseResult<T, U> error(String message, Sequence<T> rest) {
        return new Error<T, U>(message, rest);
    }

    @SuppressWarnings("unchecked")
    public final <V> ParseResult<T, V> cast() {
        return (ParseResult<T, V>) this;
    }

    public final Sequence<T> getRest() {
        return rest;
    }

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract boolean isError();

    public abstract U getResult();

    public abstract String getMessage();

    public abstract <V> ParseResult<T, V> map(Function<? super U, ? extends V> function);

    public abstract ParseResult<T, U> withFailureMessage(String message);

    public abstract ParseResult<T, U> withErrorMessage(String message);

    public abstract ParseResult<T, U> asFailure();

    public abstract ParseResult<T, U> asError();

    private static final class Success<T, U> extends ParseResult<T, U> {

        private final U result;

        private Success(U result, Sequence<T> rest) {
            super(rest);
            this.result = result;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public U getResult() {
            return result;
        }

        @Override
        public String getMessage() {
            throw new IllegalStateException();
        }

        @Override
        public <V> ParseResult<T, V> map(Function<? super U, ? extends V> function) {
            return new Success<T, V>(function.apply(result), rest);
        }

        @Override
        public ParseResult<T, U> withFailureMessage(String message) {
            return this;
        }

        @Override
        public ParseResult<T, U> withErrorMessage(String message) {
            return this;
        }

        @Override
        public ParseResult<T, U> asFailure() {
            return this;
        }

        @Override
        public ParseResult<T, U> asError() {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Success)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Success<T, U> other = (Success<T, U>) obj;
            return (result != null ? result.equals(other.result) : other.result == null) && rest.equals(other.rest);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + (this.result != null ? this.result.hashCode() : 0);
            result = 31 * result + rest.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Success{result=" + result + ", rest=" + rest + '}';
        }
    }

    private abstract static class NoSuccess<T, U> extends ParseResult<T, U> {

        protected final String message;

        private NoSuccess(String message, Sequence<T> rest) {
            super(rest);
            this.message = message;
        }

        @Override
        public final boolean isSuccess() {
            return false;
        }

        @Override
        public final U getResult() {
            throw new IllegalStateException();
        }

        @Override
        public final String getMessage() {
            return message;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <V> ParseResult<T, V> map(Function<? super U, ? extends V> function) {
            return (ParseResult<T, V>) this;
        }
    }

    private static final class Failure<T, U> extends NoSuccess<T, U> {

        private Failure(String message, Sequence<T> rest) {
            super(message, rest);
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public ParseResult<T, U> withFailureMessage(String message) {
            return new Failure<T, U>(message, rest);
        }

        @Override
        public ParseResult<T, U> withErrorMessage(String message) {
            return this;
        }

        @Override
        public ParseResult<T, U> asFailure() {
            return this;
        }

        @Override
        public ParseResult<T, U> asError() {
            return new Error<T, U>(message, rest);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Failure)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Failure<T, U> other = (Failure<T, U>) obj;
            return (message != null ? message.equals(other.message) : other.message == null) && rest.equals(other.rest);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + (message != null ? message.hashCode() : 0);
            result = 31 * result + rest.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Failure{message=" + message + ", rest=" + rest + '}';
        }
    }

    private static final class Error<T, U> extends NoSuccess<T, U> {

        private Error(String message, Sequence<T> rest) {
            super(message, rest);
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public ParseResult<T, U> withFailureMessage(String message) {
            return this;
        }

        @Override
        public ParseResult<T, U> withErrorMessage(String message) {
            return new Error<T, U>(message, rest);
        }

        @Override
        public ParseResult<T, U> asFailure() {
            return new Failure<T, U>(message, rest);
        }

        @Override
        public ParseResult<T, U> asError() {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Error)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Error<T, U> other = (Error<T, U>) obj;
            return (message != null ? message.equals(other.message) : other.message == null) && rest.equals(other.rest);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + (message != null ? message.hashCode() : 0);
            result = 31 * result + rest.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Error{message=" + message + ", rest=" + rest + '}';
        }
    }
}
