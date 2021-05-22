import java.util.HashSet;

public class Square {

    private final char value;
    private Square[] perimeter; // surrounding squares
    private HashSet<String> usedIn;

    public Square(char value) {
        this.value = value;
        this.perimeter = new Square[8];
        this.usedIn = new HashSet<String>();
    }

    public Square getPerimeter(Direction d) {
        return perimeter[d.getId()];
    }

    public void setPerimeter(Square s, Direction d) {
        perimeter[d.getId()] = s;

        return;
    }

    public void addUsedIn(String w) {
        usedIn.add(w);

        return;
    }

    public boolean isUsedIn(String w) {
        return usedIn.contains(w);
    }

    public char getValue() {
        return value;
    }

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