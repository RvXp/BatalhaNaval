import battleship.model.Board;
import battleship.model.Coordinate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class BoardTest {
    Board board = new Board(10);
	@Test 
    public void testBoardConstructor(){
        Assertions.assertNotNull(board);
    }
    @Test
    public void testBoardSize(){
        Assertions.assertEquals(10, board.getSize());
    }
    @Test   
    public void testValidPlaceShip(){
        Coordinate start = new Coordinate(0, 0);
        Assertions.assertTrue(board.placeShip(start, 3, true));
    }
    @Test   
    public void testInvalidPlaceShipOne(){
        Coordinate start = new Coordinate(-1, 0);
        Assertions.assertFalse(board.placeShip(start, 3, true));
    }
    @Test   
    public void testInvalidPlaceShipTwo(){
        Coordinate start = new Coordinate(0, -1);
        Assertions.assertFalse(board.placeShip(start, 3, true));
    }
    @Test 
    public void testAttackResultHit(){
        Coordinate start = new Coordinate(0, 0);
        board.placeShip(start, 3, true);
        Coordinate attack = new Coordinate(0, 0);
        Board.AttackResult result = board.attack(attack);
        Assertions.assertTrue(result.isHit());
    }
    @Test 
    public void testAttackResultMiss(){
        Coordinate start = new Coordinate(0, 0);
        board.placeShip(start, 3, true);
        Coordinate attack = new Coordinate(0, 1);
        Board.AttackResult result = board.attack(attack);
        Assertions.assertFalse(result.isHit());
    }

}