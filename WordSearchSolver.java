import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class WordSearchSolver {

    private static ArrayList<Square[]> rows = new ArrayList<Square[]>();
    private static int rowSize = 0;
    private static Trie words = new Trie();
    private static HashMap<String, Boolean> missingWords = new HashMap<String, Boolean>();

    private static String foundWord = "";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("File name was not provided. Use: java WordSearchSolver <FileName>.txt");
            return;
        } else if (args.length > 1) {
            System.out.println("Arguments not recognized. Use: java WordSearchSolver <FileName>.txt");
            return;
        }

        initRows(args[0]);
        setPerimeters();
        getWords();
        wordSearch();
        printToFile(args[0]);

        return;
    }

    private static void initRows(String fileName) {
        Scanner txt;
        try {
            txt = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File with name " + fileName + 
                    " not found. Make sure it exists and is in the directory.");
            return;
        }

        while (txt.hasNextLine()) {
            String line = txt.nextLine();
            Square[] row;

            if (rowSize == 0) {
                rowSize = line.length();
            } else if (rowSize != line.length()) {
                System.out.println("Format error. Grid must have equally sized rows.");
                return;
            }

            row = new Square[rowSize];
            rows.add(row);
            for (int i = 0; i < rowSize; i++) {
                char c = line.charAt(i);

                if (c < 'A' || c > 'Z') {
                    if (c < 'a' || c > 'z') {
                        System.out.println("Format error. File must have a single grid of only letters.");
                        return;
                    }

                    c += 'A' - 'a';
                }
                
                row[i] = new Square(c);
            }
        }

        txt.close();
        return;
    }

    private static void setPerimeters() {
        for (int r = 0; r < rows.size(); r++) {
            for (int c = 0; c < rowSize; c++) {
                Square curr = rows.get(r)[c];

                if (r > 0 && c > 0) { // up-left
                    curr.setPerimeter(rows.get(r-1)[c-1], Square.Direction.UP_LEFT);
                }
                if (r > 0) { // up
                    curr.setPerimeter(rows.get(r-1)[c], Square.Direction.UP);
                }
                if (r > 0 && c < rowSize-1) { // up-right
                    curr.setPerimeter(rows.get(r-1)[c+1], Square.Direction.UP_RIGHT);
                }
                if (c < rowSize-1) { // right
                    curr.setPerimeter(rows.get(r)[c+1], Square.Direction.RIGHT);
                }
                if (r < rows.size()-1 && c < rowSize-1) { // down-right
                    curr.setPerimeter(rows.get(r+1)[c+1], Square.Direction.DOWN_RIGHT);
                }
                if (r < rows.size()-1) { // down
                    curr.setPerimeter(rows.get(r+1)[c], Square.Direction.DOWN);
                }
                if (r < rows.size()-1 && c > 0) { // down-left
                    curr.setPerimeter(rows.get(r+1)[c-1], Square.Direction.DOWN_LEFT);
                }
                if (c > 0) { // left
                    curr.setPerimeter(rows.get(r)[c-1], Square.Direction.LEFT);
                }
            }
        }

        return;
    }

    private static void getWords() {
        Scanner usr = new Scanner(System.in);

        System.out.println("What words must be found?");
        while (true) {
            String w = usr.nextLine().replaceAll(" ", "").toUpperCase();
            if (w == "") {
                break;
            }
            words.addWord(w);
            missingWords.put(w, true);
        }

        usr.close();
        return;
    }

    private static void wordSearch() {
        Trie.TrieIterator it = words.iterator();

        for (Square[] r : rows) {
            for (Square s : r) {
                moveInLine(s, it.reset(), Square.Direction.UP_LEFT);
                moveInLine(s, it.reset(), Square.Direction.UP);
                moveInLine(s, it.reset(), Square.Direction.UP_RIGHT);
                moveInLine(s, it.reset(), Square.Direction.RIGHT);
                moveInLine(s, it.reset(), Square.Direction.DOWN_RIGHT);
                moveInLine(s, it.reset(), Square.Direction.DOWN);
                moveInLine(s, it.reset(), Square.Direction.DOWN_LEFT);
                moveInLine(s, it.reset(), Square.Direction.LEFT);
            }
        }

        return;
    }

    private static boolean moveInLine(Square s, Trie.TrieIterator it, Square.Direction d) {
        if (s == null || !it.proceed(s.getValue())) {
            return false;
        }

        boolean wordEnd = it.isWordEnd();
        String currWord = it.getWord();

        if (moveInLine(s.getPerimeter(d), it, d)) {
            if (wordEnd) {
                missingWords.remove(currWord);
            }

            s.addUsedIn(foundWord);
            return true;
        } else if (wordEnd) {
            foundWord = currWord;

            s.addUsedIn(foundWord);
            missingWords.put(foundWord, false);
            return true;
        }

        return false;
    }

    private static void printToFile(String fileName) {
        for (int i = fileName.length()-1; i >= 0; i--) {
            if (fileName.charAt(i) == '.') {
                fileName = fileName.substring(0, i) + "_solution" + fileName.substring(i);
                break;
            }
        }

        FileWriter solution;
        StringBuffer footer = new StringBuffer();
        String newLine = System.lineSeparator();

        try {
            solution = new FileWriter(fileName);

            for (Map.Entry<String, Boolean> m : missingWords.entrySet()) {
                if (m.getValue()) {
                    footer.append(newLine + "*" + m.getKey());
                } else {
                    solution.append(m.getKey() + ":" + newLine);
                    for (Square[] row : rows) {
                        for (Square s : row) {
                            solution.append((s.isUsedIn(m.getKey()) ? String.valueOf(s.getValue()) : "-"));
                        }
                        solution.append(newLine);
                    }
                    solution.append(newLine);
                }
            }

            if (!footer.isEmpty()) {
                solution.append("Words that were not found:" + footer.toString());
            }

            solution.close();
        } catch (Exception e) {
            System.out.println("Could not successfully create/write a solution file:");
            e.printStackTrace();
            return;
        }

        return;
    }

}