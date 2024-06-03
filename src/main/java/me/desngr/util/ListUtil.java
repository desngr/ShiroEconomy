package me.desngr.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class ListUtil {

    @SafeVarargs
    public <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>();

        Collections.addAll(list, elements);

        return Collections.unmodifiableList(list);
    }

    public <T> Map<Integer, List<T>> partition(List<T> list, int pageSize) {
        return IntStream.iterate(0, i -> i + pageSize)
                .limit((list.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(Collectors.toMap(i -> i / pageSize,
                        i -> list.subList(i, Math.min(i + pageSize, list.size()))));
    }
}
