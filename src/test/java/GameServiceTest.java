import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import battleship.model.Board;
import battleship.model.Board.AttackResult;
import battleship.model.Coordinate;
import battleship.service.GameService;

public class GameServiceTest {
    private GameService gameService;
    private Board computerBoard;
    private Board playerBoard;
    @BeforeEach
    public void GameServiceSetup() {
        gameService = new GameService();
        gameService.init(10);
        playerBoard = new Board(10);
        playerBoard.placeShip(new Coordinate(5, 7), 3, true);
        computerBoard = new Board(10);
        computerBoard.placeShip(new Coordinate(5, 7), 3, true);
    }
    
    @Test
    public void testPlacePlayerShipsTrue() {
        Assertions.assertTrue(gameService.placePlayerShip(5, 7, 3, true));
    }
    @Test
    public void testPlacePlayerShipsFalse() {
        Assertions.assertFalse(gameService.placePlayerShip(11, 7, 3, true));
    }
    // @Test
    // public void testPlayerAttackHit() {
    //     Map<String,Object> m = gameService.playerAttack(11, 7);
    //     Assertions.assertTrue((boolean) m.get("hit"));
    // }
    @Test
    public void testPlayerAttackMiss() {
        Map<String,Object> m = gameService.playerAttack(0, 0);
        Assertions.assertFalse((boolean) m.get("hit"));
    }
    @Test
    public void testPlayerAttackNoSunk(){
        Map<String,Object> m = new HashMap<>();
        computerBoard.attack(new Coordinate(5, 7));
        AttackResult res = computerBoard.attack(new Coordinate(7, 7));
        m.put("sunk", res.getSunkShip());
        Assertions.assertNull(m.get("sunk"));
    }

    @Test
    public void testPlayerAttackSunk(){
       Map<String,Object> m = new HashMap<>();
        computerBoard.attack(new Coordinate(5, 7));
        computerBoard.attack(new Coordinate(6, 7));
        AttackResult res = computerBoard.attack(new Coordinate(7, 7));
        m.put("sunk", res.getSunkShip());
        Assertions.assertEquals("ship", m.get("sunk"));
    }
    @Test
    public void testPlayerAttackGameOver() {
        Map<String,Object> m = new HashMap<>();
        computerBoard.attack(new Coordinate(5, 7));
        computerBoard.attack(new Coordinate(6, 7));
        computerBoard.attack(new Coordinate(7, 7));
        m.put("gameOver", computerBoard.allSunk());
        Assertions.assertTrue((boolean) m.get("gameOver"));
    }
    @Test
    public void testComputerAttackHit(){
        Map<String,Object> m = new HashMap<>();
        AttackResult res = playerBoard.attack(new Coordinate(5, 7));
        m.put("hit", res.isHit());
        Assertions.assertTrue((boolean) m.get("hit"));
    }
    @Test
    public void testComputerAttackMiss(){  
        Map<String,Object> m = new HashMap<>();
        AttackResult res = playerBoard.attack(new Coordinate(0, 0));
        m.put("hit", res.isHit());
        Assertions.assertFalse((boolean) m.get("hit"));
    }
    @Test
    public void testComputerAttackNoSunk(){
        Map<String,Object> m = new HashMap<>();
        playerBoard.attack(new Coordinate(5, 7));
        AttackResult res = playerBoard.attack(new Coordinate(7, 7));
        m.put("sunk", res.getSunkShip());
        Assertions.assertNull(m.get("sunk"));
    }
    @Test
    public void testComputerAttackSunk(){
       Map<String,Object> m = new HashMap<>();
        playerBoard.attack(new Coordinate(5, 7));
        playerBoard.attack(new Coordinate(6, 7));
        AttackResult res = playerBoard.attack(new Coordinate(7, 7));
        m.put("sunk", res.getSunkShip());
        Assertions.assertEquals("ship", m.get("sunk"));
    }
    @Test
    public void testComputerAttackGameOver() {
        Map<String,Object> m = new HashMap<>();
        playerBoard.attack(new Coordinate(5, 7));
        playerBoard.attack(new Coordinate(6, 7));
        playerBoard.attack(new Coordinate(7, 7));
        m.put("gameOver", playerBoard.allSunk());
        Assertions.assertTrue((boolean) m.get("gameOver"));
    }
}
