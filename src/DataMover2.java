import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataMover2 {
    private static int n;
    static Random rand=new Random();
    public static AtomicInteger arrivedCount = new AtomicInteger(0);
    public static AtomicInteger totalSent = new AtomicInteger(0);
    public static AtomicInteger totalArrived = new AtomicInteger(0);
    public static ArrayList<Integer>discard=new ArrayList<>();
    public static ArrayList<Future<DataMover2Result>>moverResults=new ArrayList<>();
    public static ArrayList<BlockingDeque<Integer>>
        queues=new ArrayList<>();
    public static ExecutorService pool;
    public static ArrayList<Integer>bemenetek;

    private static Semaphore producerSemaphore = new Semaphore(1);
    private final static Semaphore consumerSemaphore = new Semaphore(0);


    public static void main(String[] args) {

        bemenetek = args.length>0?
                Utility.stringToIntegerList.apply(args)
                : Utility.stringToIntegerList.apply
                        (new String[]{"23", "111", "256", "404"}
          );

        n=bemenetek.size();

        for (int i = 0; i < n; i++) {
            queues.add(new LinkedBlockingDeque<>());
        }

        pool=Executors.newFixedThreadPool(100);

        for (int i = 0; i < n; i++) {
            var currentQueue=queues.get(i);
            AtomicInteger currentIndex=new AtomicInteger(i);

            Callable<DataMover2Result> resultCallable=
                    createCallable(currentQueue,arrivedCount,currentIndex);

            Future<DataMover2Result> future = pool.submit(resultCallable);
            moverResults.add(future);
        };

        Utility2.shutDownPool(pool);
        Utility2.setRequiredData(moverResults,queues,totalArrived,discard);
        Utility2.testResult(totalSent.get(),totalArrived.get(),discard);

    }

    private static Callable<DataMover2Result> createCallable(
            BlockingDeque<Integer> currentQueue,
            AtomicInteger arrivedCount,
            AtomicInteger currentIndex
    ) {
        return () -> {
            var result = new DataMover2Result();

            Runnable queueRunnable =
                    createQueueRunnable
                            (currentQueue, arrivedCount, currentIndex);
            Runnable dequeueRunnable =
                    createDequeueRunnable
                            (currentQueue, arrivedCount, currentIndex, result);

            Thread queueThread = new Thread(queueRunnable);
            Thread dequeueThread = new Thread(dequeueRunnable);

            queueThread.start();
            dequeueThread.start();

            return result;
        };
    }

    private static Runnable
    createQueueRunnable(
                    BlockingDeque<Integer> currentQueue
                    , AtomicInteger arrivedCount
                    , AtomicInteger currentIndex
         ) {
            return () -> {

                while (arrivedCount.get() < 5 * n)
                {
                    try {
                        producerSemaphore.acquire();
                        int value= ThreadLocalRandom
                                .current().nextInt(0, 10000);

                        currentQueue.put(value);
                        totalSent.addAndGet(value);

                        System.out.println(
                                Utility2.getSablonString(arrivedCount, currentIndex,n)
                                        + "  sends  " + value
                        );

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt(); // Preserve interruption status

                    } finally {
                        consumerSemaphore.release();
                        Utility2.wait1(currentIndex,bemenetek);
                    }

                }//whileEnd

            };//runnableEnd
    }

    private static Runnable
    createDequeueRunnable(
            BlockingQueue<Integer> currentQueue
            ,AtomicInteger arrivedCount
            ,AtomicInteger currentIndex
            ,DataMover2Result result
    ) {
        return () -> {

            while (arrivedCount.get() < 5 * n )
            {
                try
                {
                    consumerSemaphore.acquire();
                    var timeoutLimit =
                            rand.nextInt(300, 1000);
                    Integer value =
                            currentQueue.poll(timeoutLimit, TimeUnit.MILLISECONDS);

                    String sablonOutput=
                            Utility2.getSablonString(arrivedCount, currentIndex,n);;
                    if (value != null)
                    {

                        boolean conditionTrue = value % n == currentIndex.get();

                        if (conditionTrue) {
                            arrivedCount.incrementAndGet();
                            result.count += 1;
                            result.data += value;

                            sablonOutput=
                                    Utility2.getSablonString(arrivedCount, currentIndex,n);

                            System.out.println(sablonOutput+ " got  " + value);
                        } else {
                            result.forwarded++;
                            Utility2.forwardValue(currentIndex,value,n,queues);
                            var nextIndex=((currentIndex.get() + 1)%n);
                            System.out.println(
                                          sablonOutput  + " forward  " + value + " (" + nextIndex + ")"
                            );

                        }//if2end

                    } else {
                        System.out.println(sablonOutput+ "nothing...");
                    }// if1end

                }//tryend
                catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Preserve interruption status

                }finally {
                    producerSemaphore.release();
                }// finallyend

            }// whileEnd

        };//runnableEnd
    }//voidEnd
}
