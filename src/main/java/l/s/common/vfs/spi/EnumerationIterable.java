package l.s.common.vfs.spi;

import java.util.Enumeration;
import java.util.Iterator;

class EnumerationIterable<T> implements Iterable<T> {

    private final Enumeration<T> entries;

    EnumerationIterable(Enumeration<T> entries) {
        this.entries = entries;
    }

    public Iterator<T> iterator() {
        return new EnumerationIterator<T>(entries);
    }
}
