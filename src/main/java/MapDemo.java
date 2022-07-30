import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://www.geeksforgeeks.org/concurrenthashmap-in-java/#:~:text=ConcurrentHashMap%20class%20is%20thread%2Dsafe,is%20not%20there%20in%20HashMap.
 * https://openclassrooms.com/en/courses/5684021-scale-up-your-code-with-java-concurrency/6671491-simplify-map-sharing-using-concurrenthashmap
 * https://javarevisited.blogspot.com/2013/02/concurrenthashmap-in-java-example-tutorial-working.html#axzz7aVW9laCv
 */
public class MapDemo {
    static Map<Integer, Integer> hashMap = new HashMap<>();
    static Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();
    public static void main(String[] args) throws  InterruptedException {
        try{
            cocncurrentReadWrite();
        } catch (Exception e){
            e.printStackTrace();
        }

        cocncurrentReadWriteSolve();
        resizeIssueHashMap();
        resizeSolvedConcurrentHashMap();

    }
    public static void resizeIssueHashMap() throws InterruptedException {
        Thread[] threads = new Thread[4];
        for (int i=0;i<threads.length;i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<1000;i++){
                        Random random = new Random();
                        int value = random.nextInt();
                        hashMap.put(i ,value); // Putting keys in multi threaded environment
                    }

                }
            });
            threads[i].start();
        }
        for (Thread t:
                threads) {
            t.join();
        }
        /**
         * Reason for this is during put resize has happened and at the same time
         * Other thread also put similar key and did resize.
         * Now during resize of hashmap it might happen then it goes into looping.
         * Why : https://www.quora.com/What%E2%80%99s-wrong-with-using-HashMap-in-a-multithreaded-environment-when-the-get-method-goes-to-an-infinite-loop
         */
        System.out.println("Size:"+ hashMap.size()); // Notice size of map as well.
    }
    public static void resizeSolvedConcurrentHashMap() throws InterruptedException {
        Thread[] threads = new Thread[4];
        for (int i=0;i<threads.length;i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<1000;i++){
                        Random random = new Random();
                        int value = random.nextInt();
                        concurrentMap.put(i ,value); // Putting keys in multi threaded environment ,
                        // as soon as it selects same bucket it has acquired lock on it. So if in any case same key is
                        // coming for put. It will see lock and wait to complete that operation.

                    }

                }
            });
            threads[i].start();
        }
        for (Thread t:
                threads) {
            t.join();
        }

        System.out.println("Concurrent map Size:"+ concurrentMap.size()); // Notice size of map as well.
    }
    public static void cocncurrentReadWrite() {
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0;i<1000;i++) {
            map.put(i, i);
        }
        /**
         * Below code is doing read and write together
         * Example where concurrent modification exception.
         */
       for (int i = 0;i<1000;i++){
           Iterator<Map.Entry<Integer, Integer>> entryIterator = map.entrySet().iterator();
           while (entryIterator.hasNext()){
               int key = entryIterator.next().getKey();
               if(key%2 == 0){
                   map.remove(key);
               }
           }
       }
    }

    public static void cocncurrentReadWriteSolve() {
        Map<Integer,Integer> map = new ConcurrentHashMap<>();
        for (int i = 0;i<1000;i++) {
            map.put(i, i);
        }
        /**
         * Below code is doing read and write together without issue.
         */
        for (int i = 0;i<1000;i++){
            Iterator<Map.Entry<Integer, Integer>> entryIterator = map.entrySet().iterator();
            while (entryIterator.hasNext()){
                int key = entryIterator.next().getKey();
                if(key%2 == 0){
                    map.remove(key);
                }
            }
        }
        System.out.println("Concurrent Map : "+ map);
    }

}
