package xyz.niflheim.stockfish;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.game.GameMode;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;
import xyz.niflheim.stockfish.ui.board.BoardPanel;
import xyz.niflheim.stockfish.util.GameDTO;
import xyz.niflheim.stockfish.util.Preference;

import java.util.ArrayList;
import java.util.List;

public class MoveHistoryLogicTest {

    public static void main(String[] args) throws StockfishInitException {
        Preference preference = new Preference("UserName");
        preference.setGameMode(GameMode.HUMAN_VS_HUMAN);
        GameDTO gameDTO = new GameDTO(preference);

        BoardPanel boardPanel = new BoardPanel(gameDTO);

        Board board = boardPanel.getBoard();
        MoveList moveHistory = gameDTO.getMoveHistory();

        List<Move> moveList = new ArrayList<>(List.of(new Move(Square.D2,Square.D4),new Move(Square.G8,Square.F6),new Move(Square.C2,Square.C4),
        new Move(Square.E7,Square.E6),new Move(Square.G1,Square.G3)));

        for(Move move : moveList) {
            boolean isMoveValid = board.doMove(move, true);
            if(!isMoveValid) {
                throw new RuntimeException("기물 이동 오류");
            }
            System.out.println("------------------------------------");
        }
        String[] fanArray = moveHistory.toFanArray();
        System.out.println("기물 히스토리 출력부 =>");
        for(String fan : fanArray) {
            System.out.printf("%s ",fan);
        }
    }
}
