package l.s.common.vfs.spi;

import java.util.Enumeration;
import java.util.Iterator;

class EnumerationIterator<T> implements Iterator<T> {

    private final Enumeration<T> entries;

    EnumerationIterator(Enumeration<T> entries) {
        this.entries = entries;
    }

    public boolean hasNext() {
        return entries.hasMoreElements();
    }

    public T next() {
        return entries.nextElement();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
