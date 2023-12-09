import java.util.concurrent.atomic.AtomicInteger;

public class MyThread extends  Thread{

    public int getId2() {
        return id;
    }

    private final int id;
    static int global_id=0;

    public MyThread(int waitTimeInput)
    {
        id=global_id;
        global_id++;
        this.waitTime=waitTimeInput;
    }
    int waitTime;
    private static int counter=0;
    @Override
    public void run()
    {

        try{
            synchronized (MyThread.class)
            {
                Thread.sleep(waitTime);
                var dataValue= new AtomicInteger(DataMover.getDataValue(id));
                doCurrentindexTask(dataValue);
                doNextIndexTask(dataValue);
            }

        }catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        counter++;
    }


     private void doCurrentindexTask(AtomicInteger dataValue)
    {
        System.out.println("#" + id + ": data " + id + " == " + dataValue);
        DataMover.decreaseDataValueWith(getId2());
        dataValue=new AtomicInteger(DataMover.getDataValue(id));
        System.out.println("##" + id + ": data " + id + " == " + dataValue);
    }

    private  void doNextIndexTask(AtomicInteger dataValue)
    {
        var nextindex=DataMover.getNextIndex(getId2());
        dataValue=new AtomicInteger(DataMover.getDataValue(nextindex));
        System.out.println("#" + id + ": data " + nextindex + " -> " + dataValue);
        DataMover.increaseDataValueWith(getId2());
        dataValue=new AtomicInteger(DataMover.getDataValue(nextindex));
        System.out.println("#" + id + ": data " + nextindex + " -> " + dataValue);
        System.out.println("---------------------------------");
    }
    public void setWaitTime(int input)
    {
        waitTime = input;
    }
}
