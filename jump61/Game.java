package jump61;

import static jump61.GameException.error;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Random;
import java.util.Scanner;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Iskander Rakhmanberdiyev
 */
class Game {

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _readonlyBoard = new ConstantBoard(_board);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)\\p{Blank}*$|^\\p{Blank}*|\\p{Blank}+");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
        _player1 = new HumanPlayer(this, Color.RED);
        _player2 = new HumanPlayer(this, Color.BLUE);
        _inGame = false;
    }

    /** Returns a read-only view of the game board. This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _readonlyBoard;
    }

    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        _out.flush();
        _inGame = true;

        while (_inGame) {
            if (_playing) {
                _player1.makeMove();
                checkForWin();
                _player2.makeMove();
                checkForWin();
            } else if (promptForNext()) {
                readExecuteCommand();
            }
        }
        return 0;
    }

    /** Get a move from my input and place its row and column in
     *  MOVE.  Returns true if this is successful, false if game stops
     *  or ends first. */
    boolean getMove(int[] move) {
        while (_playing && _move[0] == 0 && promptForNext()) {
            readExecuteCommand();
        }
        if (_move[0] > 0) {
        	//System.out.println(_move[0]);
        	//System.out.println(_move[1]);
            move[0] = _move[0];
            move[1] = _move[1];
            _move[0] = 0;
            _move[1] = 0;
            //System.out.println(_move[0]);
        	//System.out.println(_move[1]);
            return true;
        } else {
            return false;
        }
        
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        Color player = _board.whoseMove();
        if (_board.isLegal(player, r, c)) {
            _board.addSpot(player, r, c);
            _board.setMoves(_board.numMoves() + 1);
        }
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        makeMove(_board.row(n), _board.col(n));
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        if (_playing) {
            if (_board.getWinner() != null) {
                announceWinner();
                _playing = false;
            }
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        String winner = _board.getWinner().toCapitalizedString();
        _out.println(winner + " wins.");
    }

    /** Make PLAYER an AI for subsequent moves. */
    private void setAuto(Color player) {
        if (_player1.getColor() == player) {
            _player1 = new AI(this, player);
        } else if (_player2.getColor() == player) {
            _player2 = new AI(this, player);
        }
    }

    /** Make PLAYER take manual input from the user for subsequent moves. */
    private void setManual(Color player) {
        if (_player1.getColor() == player) {
            _player1 = new HumanPlayer(this, player);
        } else if (_player2.getColor() == player) {
            _player2 = new HumanPlayer(this, player);
        }
    }

    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        _playing = false;
        _board.clear(_board.size());
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board);
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        _playing = false;
        _board.setMoves(n);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots, String color) {
        if (spots >= _board.neighbors(r, c)) {
            GameException.error("ERROR");
        } else {
            if (spots == 0) {
                _board.set(r, c, Color.WHITE);
            } else {
                if (color.equals("r")) {
                    _board.set(r, c, Color.RED);
                } else if (color.equals("b")) {
                    _board.set(r, c, Color.BLUE);
                } else {
                    GameException.error("Error: Incorrect color.");
                }
            }
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(int n) {
        _playing = false;
        _board.clear(n);
    }

    /** Begin accepting moves for game. If the game is won,
     *  immediately print a win message and end the game. */
    private void restartGame() {
        _playing = true;
        checkForWin();
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        if (!_board.exists(r, c)) {
            throw error("move %d %d out of bounds", r, c);
        }
        _move[0] = r;
        _move[1] = c;
    }

    /** Returns a color (player) name from _inp: either RED or BLUE.
     *  Throws an exception if not present. */
    private Color readColor() {
        return Color.parseColor(_inp.next("[rR][eE][dD]|[Bb][Ll][Uu][Ee]"));
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        if (_playing && _inp.hasNextInt()) {
            saveMove(Integer.parseInt(_inp.next()), Integer.parseInt(_inp.next()));
        } else {
            executeCommand(_inp.next());
        }
        if (_inp.hasNextLine()) {
        	_inp.nextLine();
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. */
    private void executeCommand(String cmnd) {
        switch (cmnd) {
        case "\n": case "\r\n":
            return;
        case "#":
            break;
        case "clear":
            clear();
            break;
        case "start":
            restartGame();
            break;
        case "quit":
            _inGame = false;
            System.exit(0);
            break;
        case "auto":
            if (_inp.hasNext()) {
                setAuto(Color.parseColor(_inp.next()));
            } else {
                throw error("Error: invalid arguments.");
            }
            break;
        case "manual":
            if (_inp.hasNext()) {
                setManual(Color.parseColor(_inp.next()));
            } else {
                throw error("Error: invalid arguments.");
            }
            break;
        case "size":
            if (_inp.hasNext()) {
                setSize(Integer.parseInt(_inp.next()));
            } else {
                throw error("Error: invalid arguments.");
            }
            break;
        case "move":
            if (_inp.hasNext()) {
                setMoveNumber(Integer.parseInt(_inp.next()));
            } else {
                throw error("Error: invalid argument.");
            }
            break;
        case "set":
            int row, column, spots;
            row = column = spots = 0;
            String color = "";
            for (int i = 0; i < 4; i++) {
                if (_inp.hasNext()) {
                    if (i == 0) {
                        row = Integer.parseInt(_inp.next());
                    } else if (i == 1) {
                        column = Integer.parseInt(_inp.next());
                    } else if (i == 2) {
                        spots = Integer.parseInt(_inp.next());
                    } else if (i == 3) {
                        color = _inp.next();
                        setSpots(row, column, spots, color);
                    }
                } else {
                    throw error("Error: invalid arguments.");
                }
            }
            break;
        case "dump":
            dump();
            break;
        case "seed":
            if (_inp.hasNext()) {
                setSeed(Long.parseLong(_inp.next()));
            } else {
                throw error("Error: invalid arguments.");
            }
            break;
        case "help":
            help();
            break;
        default:
            throw error("bad command: '%s'", cmnd);
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token. */
    private boolean promptForNext() {
        if (_playing) {
            _out.print(_board.whoseMove().toString() + "> ");
        } else {
            _out.print("> ");
        }
        _out.flush();

        return _inp.hasNext();
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
    }

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;

    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;

    /** Outlet for responses to the user. */
    private final PrintWriter _out;

    /** Outlet for error responses to the user. */
    private final PrintWriter _err;

    /** The board on which I record all moves. */
    private final Board _board;
    /** A readonly view of _board. */
    private final Board _readonlyBoard;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

    /** True iff the game is being played. */
    private boolean _inGame;

    /** Player 1. */
    private Player _player1;

    /** Player 2. */
    private Player _player2;

   /** Used to return a move entered from the console.  Allocated
     *  here to avoid allocations. */
    private final int[] _move = new int[2];
}
