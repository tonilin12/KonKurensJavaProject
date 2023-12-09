import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataMover {

    public static ArrayList<MyThread> movers;
    public static Random rand = new Random();

    public static Integer[] data;

    public static int getDataValue(int index) {
        synchronized (DataMover.class) {
            return data[index];
        }
    }

    public static void decreaseDataValueWith(int index) {
        synchronized (DataMover.class) {
            data[index] -= index;
        }
    }

    public static void increaseDataValueWith(int index) {
        synchronized (DataMover.class) {
            var nextIndex = DataMover.getNextIndex(index);
            data[nextIndex] += index;
        }
    }

    public static int getNextIndex(int index) {
        synchronized (DataMover.class) {
            var nextIndex = index + 1;
            if (nextIndex >= data.length) {
                nextIndex = 0;
            }
            return nextIndex;
        }
    }

    public static void main(String[] args) {

        var bemenetek = args.length > 0 ?
                Utility.stringToIntegerList.apply(args) :
                Utility.stringToIntegerList.apply(
                        new String[]{"23", "111", "256", "404"}
                );

        int defaultWaitTime = bemenetek.get(0);
        List<Integer> threadWaitTimeList =
                bemenetek.stream().skip(1)
                        .collect(Collectors.toList());

        data = Utility.generateIntArray.apply(threadWaitTimeList.size());

        movers = Stream.generate(() -> new MyThread(defaultWaitTime))
                .limit(threadWaitTimeList.size())
                .collect(Collectors.toCollection(ArrayList::new));

        Utility.setAllWaitTime(threadWaitTimeList, movers);

        int threadTaskCount = 10;

        ConcurrentHashMap<Integer, AtomicInteger>
                map = Utility.generateMap(threadTaskCount, movers);

        var executerService
                = Executors.newFixedThreadPool(movers.size());

        AtomicInteger k = new AtomicInteger(0);
        while (map.size() > 0) {
            var randomkey = Utility.getRandomKey(map);
            executerService.submit(movers.get(randomkey));
            map.get(randomkey).getAndDecrement();
            if (map.get(randomkey).get() == -1) {
                map.remove(randomkey);
            }
        }


        executerService.shutdown();
        try {
            // Wait for all threads to finish or until timeout (e.g., 1 hour)
            if (!executerService.awaitTermination(3, TimeUnit.MINUTES)) {
                System.out.println("Threads did not finish within the timeout.");
            }
        } catch (InterruptedException e) {
            System.err.println("Thread pool await termination interrupted: " + e.getMessage());
        }
        System.out.println("eredmeny:" + Arrays.toString(data));
    }
}
