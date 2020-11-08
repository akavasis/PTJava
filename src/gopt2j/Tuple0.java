/*
 * The MIT License
 *
 * Copyright 2020 akava.
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
package gopt2j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author alex.collins - https://github.com/alexec/tuples
 */
public final class Tuple0 implements Tuple<Tuple0> {

    private static final Tuple0 INSTANCE = new Tuple0();
    private static final long serialVersionUID = 1015700180699730675L;

    protected Tuple0() {
    }

    public static Tuple0 valueOf() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "()";
    }

    @Override
    public Object apply(int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return 0;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    }
}
