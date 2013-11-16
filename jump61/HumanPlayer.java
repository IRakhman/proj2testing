package jump61;

/** A Player that gets its moves from manual input.
 *  @author Iskander Rakhmanberdiyev
 */
class HumanPlayer extends Player {

    /** Moves to be played. */
    private final int[] _moves;

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Color color) {
        super(game, color);
        _moves = new int[2];
    }

    @Override
    void makeMove() {
        Game game = getGame();
        Board constantBoard = getBoard();
        //MutableBoard board = new MutableBoard(constantBoard);

        if (game.getMove(_moves)) {
            game.makeMove(_moves[0], _moves[1]);
        }
    }

}
