import java.util.ArrayList;
import java.util.Random;

public class Main {

  public static ArrayList<MyThread> movers;
  public static Random rand=new Random();

    public static Integer[] data;
    public static int getDataValue(int index) {
        synchronized (Main.class) {
            return data[index];
        }
    }
    public static void decrementValue(int index) {
        synchronized (Main.class) {
            data[index] -= index;
        }
    }
    public static void incrementValue(int index) {
        synchronized (Main.class) {
            var nextIndex=Main.getNextIndex(index);
            data[nextIndex] += index;
        }
    }
    public static int getNextIndex(int index) {
        synchronized (Main.class) {
            var nextIndex=index+1;
            if (nextIndex>=data.length)
            {
                nextIndex=0;
            }
            return nextIndex;
        }
    }



    public static void main(String[] args) {

    }
}