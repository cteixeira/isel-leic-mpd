/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2019, Miguel Gamboa (gamboa.pt)
 *
 *   All rights granted under this License are granted for the term of
 * copyright on the Program, and are irrevocable provided the stated
 * conditions are met.  This License explicitly affirms your unlimited
 * permission to run the unmodified Program.  The output from running a
 * covered work is covered by this License only if the output, given its
 * content, constitutes a covered work.  This License acknowledges your
 * rights of fair use or other equivalent, as provided by copyright law.
 *
 *   You may make, run and propagate covered works that you do not
 * convey, without conditions so long as your license otherwise remains
 * in force.  You may convey covered works to others for the sole purpose
 * of having them make modifications exclusively for you, or provide you
 * with facilities for running those works, provided that you comply with
 * the terms of this License in conveying all material for which you do
 * not control copyright.  Those thus making or running the covered works
 * for you must do so exclusively on your behalf, under your direction
 * and control, on terms that prohibit them from making any copies of
 * your copyrighted material outside their relationship with you.
 *
 *   Conveying under any other circumstances is permitted solely under
 * the conditions stated below.  Sublicensing is not allowed; section 10
 * makes it unnecessary.
 *
 */

package org.isel.jingle.util.iterators;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class IteratorCache<T> implements Iterator<T> {

    private Iterator<T> src;
    private LinkedList<T> cache;
    int pos;

    public IteratorCache(Iterable<T> iter, LinkedList<T> list) {
        this.src = iter.iterator();
        this.cache = list;
        pos = 0;
    }

    @Override
    public boolean hasNext() {
        if(cache.size()>pos)
            return true;
        else if(src.hasNext()) {
            cache.add(src.next());
            return true;
        }
        pos = 0;
        return false;
    }

    @Override
    public T next() {
        return cache.get(pos++);
    }

    /*@Override
    public boolean hasNext() {
        return src.hasNext();
    }

    @Override
    public T next() {
        T next;
        if(cache.size() > pos)
            next = cache.get(pos);
        else
            cache.add(next = src.next());
        pos++;
        return next;
    }*/
}