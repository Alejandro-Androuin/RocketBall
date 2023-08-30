import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Names {

    private static List<String> names = new ArrayList<>();

    public static void generateNames() throws FileNotFoundException {

        names = new ArrayList<>();

        File f = new File("Names.txt");
        Scanner in = new Scanner(f);

        while(in.hasNext())
        {
            names.add(in.next());
        }
    }

    public static String getRandomName()
    {
        return names.get((int)(Math.random()*names.size()));
    }

    public static List<String> getUniqueNames(int size)
    {
        List<String> n = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        while(n.size()!=size)
        {
            int rNum = (int)(Math.random()*names.size());
            if(!indexes.contains(rNum))
            {
                indexes.add(rNum);
                n.add(names.get(rNum));
            }
            else
            {
                continue;
            }
        }

        return n;
    }
}
