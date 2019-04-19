package org.isel.jingle.utils;

import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

    public static <T> Supplier<Stream<T>> cache(Stream<T> src){
        LinkedList<T> list = new LinkedList();
        SpliteratorCache<T> res = new SpliteratorCache<T>(src.iterator(), list);
        Supplier<Stream<T>> supplier = () -> {
            res.pos = 0;
            return StreamSupport.stream(res, false);
        };
        return supplier;
    }
}
