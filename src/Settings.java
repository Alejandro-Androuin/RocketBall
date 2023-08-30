import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {

    //Bot Training Settings (All values here are considered default)
    public static int teamSize = 2;

    public static int maxWins = 8;
    public static float percentElite = (float).1;
    public static int proWins = 5;

    public static int numSpecies = 20;
    private static float killPercent = (float)0.5;
    private static float threshold = 20;
    private static float thresholdIncrement = (float)0.1;
    private static int numGenerationsPurge = 20;

    public static float incluenceOnFitness = (float)0.2; //fitness = wins*(1-val)+(wins-avgtm8Wins)(val)+(avgoppWins-wins)(val)

    public static void writeDefaultSettings() throws IOException {
        String path = "saves\\DefaultSettings.txt";
        File f = new File(path);
        f.delete();
        f.createNewFile();

        FileWriter fw = new FileWriter(path);
        fw.write("//Bot Training Settings");
        fw.close();
    }

}
