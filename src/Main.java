import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final int TEXT_COUNT = 10000;
    private static final int TEXT_LENGTH = 100000;
    private static final int QUEUE_CAPACITY = 100;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {
        Thread producer = new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = generateText("abc", TEXT_LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumerA = new Thread(() -> processQueue(queueA, 'a'));
        Thread consumerB = new Thread(() -> processQueue(queueB, 'b'));
        Thread consumerC = new Thread(() -> processQueue(queueC, 'c'));

        producer.start();
        consumerA.start();
        consumerB.start();
        consumerC.start();

        try {
            producer.join();
            consumerA.join();
            consumerB.join();
            consumerC.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static void processQueue(BlockingQueue<String> queue, char targetChar) {
        int maxCount = 0;
        String maxText = null;

        for (int i = 0; i < TEXT_COUNT; i++) {
            try {
                String text = queue.take();
                int count = countChar(text, targetChar);
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Max count of '" + targetChar + "': " + maxCount);
    }

    private static int countChar(String text, char targetChar) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == targetChar) {
                count++;
            }
        }
        return count;
    }
}