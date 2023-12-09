import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

    public static Function<String[], ArrayList<Integer>> stringToIntegerList=(input)->{
        return Arrays.stream(input).map(x->Integer.parseInt(x))
                .collect(Collectors.toCollection(ArrayList::new));
    };
    public static Function<Integer,Integer[]>generateIntArray=(n)->{
        return  Stream.iterate(0, x -> x + 1000)
                .limit(n)
                .toArray(Integer[]::new);
    };

    public static  ConcurrentHashMap<Integer
            , AtomicInteger> generateMap
            (int  threadTaskCount,List<MyThread>movers)
    {
        ConcurrentHashMap<Integer, AtomicInteger>
                map = new ConcurrentHashMap<>();
        for (var elem:movers
        ) {
            map.put(elem.getId2(),new AtomicInteger(threadTaskCount));
        }
        return map;
    }
    public static synchronized
    <K, V> K getRandomKey(ConcurrentHashMap<K, V> map) {
        if (!map.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(map.size());
            return map.keySet().stream().toList().get(randomIndex);
        }
        return null;
    }

    public static void setAllWaitTime(List<Integer> waitTimeList,List<MyThread>threadList )
    {
        for (int i = 0; i <waitTimeList.size() ; i++) {
                threadList.get(i).setWaitTime(waitTimeList.get(i));
        }
    }
}
