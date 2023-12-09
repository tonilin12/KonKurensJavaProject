import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Utility2 {
    public static String getSablonString(AtomicInteger arrivedCount
            , AtomicInteger currentIndex, int n)
    {
        return "total"+"  "+arrivedCount+"/"+(n*5)+" | "
                +"# "+currentIndex+" ";
    }

    public static void testResult(
            int totalSentValue
            , int totalArrivedValue
            , List<Integer> discard
    ) {
        int discardedSum = discard.stream().mapToInt(Integer::intValue).sum();

        System.out.println("discarded " + discard + " = " + discardedSum);

        boolean checkResult = totalSentValue == (totalArrivedValue + discardedSum);

        if (checkResult) {
            System.out.println(
                    " | sent " + totalSentValue + " === got " + (totalArrivedValue + discardedSum) +
                            " = " + totalArrivedValue + " + discarded " + discardedSum);

        } else {
            System.out.println(
                    "WRONG sent " + totalSentValue + " !== got " +
                            ((totalArrivedValue + discardedSum))
                            + " = " + totalArrivedValue + " + discarded " + discardedSum + ""
            );
        }
        System.exit(0);
    }

    public static void setRequiredData(
            List<Future<DataMover2Result>> moverResults
            ,List<BlockingDeque<Integer>> queues
            , AtomicInteger totalArrived
            , List<Integer> discard
    ) {
        for (Future<DataMover2Result> futureResult : moverResults) {
            try {
                DataMover2Result result = futureResult.get();
                totalArrived.addAndGet(result.data + result.forwarded);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        for (BlockingDeque<Integer> queue : queues) {
            int sum = 0;
            Integer value;
            while ((value = queue.poll()) != null) {
                sum += value;
            }
            discard.add(sum);
        }
    }
    public static void shutDownPool(ExecutorService pool) {
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void forwardValue(
            AtomicInteger currentIndex
            , int value, int n
            , List<BlockingDeque<Integer>> queues
    ) {
        int nextIndex = (currentIndex.get() + 1) % n;
        BlockingDeque<Integer> nextQueue = queues.get(nextIndex);
        if (nextQueue != null) {
            try {
                nextQueue.put(value - 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void wait1
            (AtomicInteger currentIndex
                    , ArrayList<Integer> bemenetek
            ) {
        try {
            Thread.sleep(bemenetek.get(currentIndex.get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
