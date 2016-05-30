package recommender.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Miguel on 29-05-2016.
 */
public class MapUtils
{
    // Sorts a Map by Value in decreasing order
    public static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> orderByValueDecreasing(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries,
                (e1, e2) -> e2.getValue().compareTo(e1.getValue())
        );

        return sortedEntries;
    }

    public static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> getTopK(List<Map.Entry<K, V>> listEntries, Integer numberOfKeys)
    {
        return listEntries
                .stream()
                .limit(numberOfKeys)
                .collect(Collectors.toList());

        /* List<K> keys = new ArrayList<>();

        for (Map.Entry<K,V> entry: listEntries)
            keys.add(entry.getKey());


        return keys.stream()
                .limit(numberOfKeys)
                .collect(Collectors.toList()); */
    }




}