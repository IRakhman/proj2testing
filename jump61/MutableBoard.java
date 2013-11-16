package jump61;

import java.util.ArrayList;
/** MutableBoard is the current board being used
 *  @author Iskander Rakhmanberdiyev
 */
class MutableBoard extends Board {

    /** Total combined number of moves. */
    protected int _moves;

    /** Size of board: squares along one edge. */
    private int _N;

    /** 2 dimensional array for all of the squares. */
    private final Square[][] _squareArray;

    /** ArrayList for storing squares. */
    private final ArrayList<Square> _squareList = new ArrayList<Square>();//This might be referring to "history"

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _N = N;
        _moves = 0;
        _squareArray = new Square[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                _squareArray[i][j] = new Square();
            }
        }
        for (Square[] outer : _squareArray) {//This part might cause an
        	//error due to creation of layers
            for (Square square : outer) {
                square = new Square();
                _squareList.add(square);
            }
        }
    }

    /** A board whose initial contents are copied from BOARD0. Clears the
     *  undo history. */
    MutableBoard(Board board0) {
        _N = board0.size();
        _squareArray = new Square[_N][_N];

        copy(board0);
    }

    @Override
    void clear(int N) {
        for (Square[] outer : _squareArray) {
            for (Square square : outer) {
                square = new Square();
                _squareList.add(square);//I think I MUST NOT add these elements into the array. Beforehand, I HAVE TO CLEAR IT FIRST.
            }
        }
        _N = N;
        _moves = 0;
    }

    @Override
    void copy(Board board) {
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                _squareArray[i][j] = new Square(board.color(i + 1, j + 1),
                    board.spots(i + 1, j + 1));
                _squareList.add(_squareArray[i][j]);
            }
        }
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        return _squareArray[r - 1][c - 1].getNumSpots();
    }

    @Override
    int spots(int n) {
        return _squareList.get(n).getNumSpots();
    }

    @Override
    Color color(int r, int c) {
        return _squareArray[r - 1][c - 1].getColor();//I think this should be _squareArray[c-1][r-1], but it looks like many implemented it this way
    }

    @Override
    Color color(int n) {
    	//or //return _squareArray[row(n - 1)][col(n - 1)];
        return _squareList.get(n).getColor();//This might be the place where the error is occurring because _SQUARELIST is being added to even during CLEAR(), but here
        //color takes in the element from the existing _SQUARELIST, where N might not be relating to the right index
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    int numOfColor(Color color) {
        int result = 0;
        for (Square square : _squareList) {
            if (square.getColor() == color) {
                result += 1;
            }
        }
        return result;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        //r = r -1;
        //c = c - 1;
        if (color(r, c) == Color.WHITE) {
            _squareArray[r - 1][c - 1].setColor(player);
        }
        _squareArray[r - 1][c - 1].addSpot();
        if (spots(r, c) > neighbors(r, c)) {
            jump(sqNum(r, c));
        }
    }

    @Override
    void addSpot(Color player, int n) {
        addSpot(player, row(n), col(n));
    }

    @Override
    void set(int r, int c, int num, Color player) {
        _squareArray[r][c].setColor(player);
        _squareArray[r][c].setNumSpots(num);
    }

    @Override
    void set(int n, int num, Color player) {
        _squareList.get(n).setColor(player);
        _squareList.get(n).setNumSpots(num);
    }

    @Override
    void setMoves(int num) {
        assert num > 0;
        _moves = num;
    }

    @Override
    void undo() {
        /*
        if (_oldBoard == null) {
            GameException.error("Error, no old board exists.");
        }
        copy(_oldBoard);
        */
    }

    /** Do all jumping on this board, assuming that initially, N is the only
     *  square that might be over-full. */
    private void jump(int N) {
        int row = row(N);
        int col = col(N);
        Color color = _squareArray[row][col].getColor();

        if (row != 1) {
            _squareArray[row][col].removeSpot();
            _squareArray[row - 1][col].setColor(color);
            _squareArray[row - 1][col].addSpot();
        }
        if (row != _N) {
            System.out.print("here");
            _squareArray[row][col].removeSpot();
            _squareArray[row + 1][col].setColor(color);
            _squareArray[row + 1][col].addSpot();
        }
        if (col != 1) {
            _squareArray[row][col].removeSpot();
            _squareArray[row][col - 1].setColor(color);
            _squareArray[row][col - 1].addSpot();
        }
        System.out.println(col + " : " + _N);
        if (col != _N) {
            System.out.println("here too");
            _squareArray[row][col].removeSpot();
            _squareArray[row][col + 1].setColor(color);
            _squareArray[row][col + 1].addSpot();
        }
    }

}
