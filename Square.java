import java.util.HashSet;

public class Square {

    private final char value; // char held in this square
    private Square[] perimeter; // surrounding squares
    private HashSet<String> usedIn; // words that use this square

    public Square(char value) {
        this.value = value;
        this.perimeter = new Square[8];
        this.usedIn = new HashSet<String>();
    }

    /**
     * Returns the surrounding square in a specified direction.
     * 
     * @param d is the direction to take from.
     * @return the square in that direction.
     */
    public Square getPerimeter(Direction d) {
        return perimeter[d.getId()];
    }

    /**
     * Sets the surrounding square in the specified direction in order to build the grid internally.
     * 
     * @param s is the bordering square.
     * @param d is the direction to set in.
     */
    public void setPerimeter(Square s, Direction d) {
        perimeter[d.getId()] = s;

        return;
    }

    /**
     * Adds a word that this square was used to form.
     * 
     * @param w is the formed word.
     */
    public void addUsedIn(String w) {
        usedIn.add(w);

        return;
    }

    /**
     * Checks whether this square was used to form a specified word.
     * 
     * @param w is the word to check.
     * @return true if it is used in w, false otherwise.
     */
    public boolean isUsedIn(String w) {
        return usedIn.contains(w);
    }

    /**
     * @return the held character in this square.
     */
    public char getValue() {
        return value;
    }

    /**
     * Simplifies the use of specific numbers as directions; makes things more readable in the long-run.
     */
    public enum Direction {
        UP_LEFT(0), UP(1), UP_RIGHT(2), RIGHT(3), DOWN_RIGHT(4), DOWN(5), DOWN_LEFT(6), LEFT(7);

        private final int id;

        Direction(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

}