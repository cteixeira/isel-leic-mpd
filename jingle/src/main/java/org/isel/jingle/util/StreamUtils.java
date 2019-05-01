package org.isel.jingle.util;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
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

    public static <T,U,R> Stream<R> merge(Stream<T> seq1, Stream<U> seq2, BiPredicate<T,U> pred, BiFunction<T,U,R> transf, U defaultVal) {
        Supplier<Stream<U>> seq2Cached = cache(seq2);
        return seq1.map(s1 -> {
            Optional<U> u = seq2Cached.get().filter(s2 -> pred.test(s1, s2)).findFirst();
            return u.isPresent() ? transf.apply(s1, u.get()) : transf.apply(s1, defaultVal);
        });
    }
}
