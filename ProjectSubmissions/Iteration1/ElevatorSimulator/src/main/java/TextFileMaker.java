import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TextFileMaker {
    private LocalDateTime currentTime;
    private final int upperBound; // in seconds
    private int floorCount;
    private int lineCount;
    private Random rand;
    private DateTimeFormatter formatter1;
    private DateTimeFormatter formatter2;

    public TextFileMaker(int timeVariance, int floorCount, int lineCount) {
        this.currentTime = LocalDateTime.now();
        this.upperBound = timeVariance;
        this.floorCount = floorCount;
        this.lineCount = lineCount;
        this.rand = new Random();
        this.formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.formatter2 = DateTimeFormatter.ofPattern("HHmmss");
    }

    private void increaseTime() {
        int int_random = rand.nextInt(upperBound) + 1;
        currentTime = currentTime.plusSeconds(int_random);
    }

    public void mkf() {
        String filename = currentTime.format(formatter2) + ".txt";
        try {
            FileWriter myWriter = new FileWriter(filename);
            for (int i = 0; i < lineCount; i++) {
                String dateTimeString = currentTime.format(formatter1);
                int currentFloor = rand.nextInt(floorCount) + 1;
                int nextFloor = rand.nextInt(floorCount) + 1;
                while (nextFloor == currentFloor) { nextFloor = rand.nextInt(floorCount) + 1; }
                String dir = nextFloor > currentFloor ? "up" : "down";

                String out = String.format("%s %d %s %d\n", dateTimeString, currentFloor, dir, nextFloor);

                myWriter.write(out);
                increaseTime();
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        TextFileMaker t = new TextFileMaker(30, 5, 10);
        t.mkf();
    }
}