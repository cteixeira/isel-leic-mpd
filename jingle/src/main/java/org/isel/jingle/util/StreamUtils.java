package org.isel.jingle.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

    public static <T> Supplier<Stream<T>> cache(Supplier<Stream<T>> src){
        LinkedList<T> list = new LinkedList();
        Supplier<Stream<T>> supplier = () -> {
            SpliteratorCache<T> res = new SpliteratorCache<T>(src.get().iterator(), list);
            return StreamSupport.stream(res, false);
        };
        return supplier;
    }

    public static <T,U,R> Stream<R> merge(Stream<T> seq1, Stream<U> seq2, BiPredicate<T,U> pred, BiFunction<T,U,R> transf, U defaultVal) {
        List<U> uList = seq2.collect(Collectors.toList());
        return seq1.map(s1 -> {
            Optional<U> u = uList.stream().filter(s2 -> pred.test(s1, s2)).findFirst();
            return u.isPresent() ? transf.apply(s1, u.get()) : transf.apply(s1, defaultVal);
        });
    }
}
