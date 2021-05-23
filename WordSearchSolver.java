import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class WordSearchSolver {

    private static ArrayList<Square[]> rows = new ArrayList<Square[]>(); // holds rows of the grid
    private static int rowSize = 0; // size of the rows. They are all this length
    private static Trie words = new Trie(); // holds all the words to look for
    private static HashMap<String, Boolean> missingWords = new HashMap<String, Boolean>(); // word -> is it missing

    private static String foundWord = ""; // used for moveInLine()

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

    /**
     * Reads a text file containing a word search, and creates the squares-and-rows virtual representation
     * to be used within this program.
     * 
     * @param fileName is the name of the file to be read.
     */
    private static void initRows(String fileName) {
        Scanner txt; // will read the file
        try {
            txt = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File with name " + fileName + 
                    " not found. Make sure it exists and is in the directory.");
            return;
        }

        while (txt.hasNextLine()) { // reading the file
            String line = txt.nextLine();
            Square[] row; // will contain the row of squares made from line

            if (rowSize == 0) { // only first row will have this
                rowSize = line.length();
            } else if (rowSize != line.length()) {
                System.out.println("Format error. Grid must have equally sized rows.");
                return;
            }

            row = new Square[rowSize];
            rows.add(row);
            for (int i = 0; i < rowSize; i++) { // reading each character in line
                char c = line.charAt(i);

                if (c < 'A' || c > 'Z') {
                    if (c < 'a' || c > 'z') {
                        System.out.println("Format error. File must have a single grid of only letters.");
                        return;
                    }

                    c += 'A' - 'a'; // turns lowercase to uppercase
                }
                
                row[i] = new Square(c);
            }
        }

        txt.close();
        return;
    }

    /**
     * After creating all the squares and the virtual grid, this will create relationships between
     * bordering squares so that they can interact with each other for use in wordSearch().
     */
    private static void setPerimeters() {
        for (int r = 0; r < rows.size(); r++) { // loops through each row
            for (int c = 0; c < rowSize; c++) { // loops through each square
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

    /**
     * Asks the user for what words they want the program to find within the word search. This will
     * set words and missingWords with the information they need to hold for wordSearch() to function.
     */
    private static void getWords() {
        Scanner usr = new Scanner(System.in); // will read the user's inputs

        System.out.println("What words must be found?");
        while (true) { // user may enter as many words as they want
            String w = usr.nextLine().replaceAll(" ", "").toUpperCase(); // gets the word and processes
                                                                         // it into a more usable form
            if (w == "") { // double enter will start the search
                break;
            }
            words.addWord(w);
            missingWords.put(w, true); // true because the word is still missing
        }

        usr.close();
        return;
    }

    /**
     * This will search for the words given by the user in the grid in a systemic fashion of moving
     * in every direction starting from each square.
     */
    private static void wordSearch() {
        Trie.TrieIterator it = words.iterator(); // will be used by moveInLine() to verify valid words

        for (Square[] r : rows) { // iterate through the rows
            for (Square s : r) { // iterate through the squares
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

    /**
     * Create a movement-like pattern within the grid in a straight line given a direction. This
     * will recursively move in said straight line until it can no longer move (it is at the
     * grid's border or no valid words can be made with the next square).
     * 
     * @param s is the current square in the line's movement.
     * @param it is the trie iterator that is verifying the validity of a word within the line.
     * @param d is the direction of the line's movement.
     * @return true if s is used in a valid word on this path.
     */
    private static boolean moveInLine(Square s, Trie.TrieIterator it, Square.Direction d) {
        if (s == null || !it.proceed(s.getValue())) { // recursion base case
            return false;
        }

        // these are kept track of because the recursion will cause the iterator to proceed,
        // taking away the ability of getting the information about this square's letter from
        // the trie
        boolean wordEnd = it.isWordEnd();
        String currWord = it.getWord();

        if (moveInLine(s.getPerimeter(d), it, d)) { // if the end of a word is further along the path
            if (wordEnd) { // example: pine and pineapple. Pine will be ignored
                missingWords.remove(currWord);
            }

            s.addUsedIn(foundWord);
            return true;
        } else if (wordEnd) { // if this square is the end of a valid word
            foundWord = currWord; // set foundWord for previous recursive calls to know what word
                                  // has been created further along the path
            s.addUsedIn(foundWord);
            missingWords.put(foundWord, false); // false because the word is no longer missing
            return true;
        }

        return false;
    }

    /**
     * With the words already found and the squares updated on their uses, a solution text file will
     * be created showing the positions of all the words within the grid, finally completing the word
     * search puzzle.
     * 
     * @param fileName is the name of the original file that the words search was in.
     */
    private static void printToFile(String fileName) {
        for (int i = fileName.length()-1; i >= 0; i--) { // will make file called "<fileName>_solution.txt"
            if (fileName.charAt(i) == '.') {
                fileName = fileName.substring(0, i) + "_solution" + fileName.substring(i);
                break;
            }
        }

        FileWriter solution; // will write in the solution file
        StringBuffer footer = new StringBuffer(); // will have all words that were not found
        String newLine = System.lineSeparator(); // for use in printing

        try {
            solution = new FileWriter(fileName);

            for (Map.Entry<String, Boolean> m : missingWords.entrySet()) { // iterate through every word
                if (m.getValue()) { // if the current word was not found
                    footer.append(newLine + "*" + m.getKey()); // append the word to the footer
                } else { // if the current word was found
                    solution.append(m.getKey() + ":" + newLine);
                    for (Square[] row : rows) { // iterate through every row
                        for (Square s : row) { // iterate through every square
                            // if the square was used in the current word, display its character value.
                            // Otherwise, display a dash
                            solution.append((s.isUsedIn(m.getKey()) ? String.valueOf(s.getValue()) : "-"));
                        }
                        solution.append(newLine);
                    }
                    solution.append(newLine);
                }
            }

            if (!footer.isEmpty()) { // if there were missing words
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