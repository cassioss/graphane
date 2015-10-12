package basics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Basic (abstract) parser.
 *
 * @author Cassio dos Santos Sousa
 * @version 1.0
 */
public class BasicParser {

    /**
     * Raw parsing of lines of a file into a list of lines (strings).
     *
     * @param filePath the file path.
     * @return a list of strings.
     */
    public static List<String> parsedLines(String filePath) {

        File etotFile = new File(filePath);
        List<String> fileLines = new ArrayList<>();

        try {
            Scanner sc = new Scanner(etotFile);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.trim().isEmpty())
                    fileLines.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileLines;
    }

    /**
     * Parses a file into a double array of strings.
     *
     * @param filePath the file path.
     * @return a double array of strings with each value of the file.
     */
    public static String[][] initialParsing(String filePath) {
        List<String> fileLines = parsedLines(filePath);
        int lines = fileLines.size();
        String[][] initialParsing = new String[lines][];
        for (int i = 0; i < lines; i++)
            initialParsing[i] = fileLines.get(i).split("\\s+");
        return initialParsing;
    }

}
