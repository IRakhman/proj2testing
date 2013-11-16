
package jump61;


/** Represents one square on a board.
 *  @author 
 */
public class Square {

    /** Color of the square. */
    private Color _color;

    /** Number of spots in the square. */
    private int _numSpots;

    /** Default constructor that creates a white square with 1 spot. */
    public Square() {
        _color = Color.WHITE;
        _numSpots = 0;//should be "1", used to be "0"
    }

    /** Creates a square with color COLOR and NUMSPOTS spots. */
    public Square(Color color, int numSpots) {
        _color = color;
        _numSpots = numSpots;
    }

    /** Changes the color of this square to COLOR. */
    protected void setColor(Color color) {
        if (color == Color.RED || color == Color.BLUE) {
            _color = color;
        }
    }

    /** Returns _color. */
    protected Color getColor() {
        return _color;
    }

    /** Returns _numSpots. */
    protected int getNumSpots() {
        return _numSpots;
    }

    /** Sets the number of spots of this square to VALUE. */
    protected void setNumSpots(int value) {
        _numSpots = value;
    }

    /** Removes one spot from this square. */
    protected void removeSpot() {
        _numSpots -= 1;
    }

    /** Adds one spot to this square. */
    protected void addSpot() {
        _numSpots += 1;
    }
}