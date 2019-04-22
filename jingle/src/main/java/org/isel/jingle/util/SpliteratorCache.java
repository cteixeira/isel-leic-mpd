package org.isel.jingle.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class SpliteratorCache<T> extends Spliterators.AbstractSpliterator<T> {

    private final Iterator<T> src;
    private LinkedList<T> cache;
    int pos;

    public SpliteratorCache(Iterator<T> src, LinkedList<T> list) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.src = src;
        this.cache = list;
        pos = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if(cache.size() > pos) {
            action.accept(cache.get(pos++));
            return true;
        }
        else if(src.hasNext()) {
            cache.add(src.next());
            action.accept(cache.get(pos++));
            return true;
        }
        return false;
    }
}

