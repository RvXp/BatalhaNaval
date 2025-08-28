import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import battleship.service.GameService;
import battleship.model.Board;
import battleship.model.Board.AttackResult;
import battleship.model.Coordinate;

public class GameServiceTest {
    private GameService gameService;
    private Board computerBoard;
    private Board playerBoard;

    @BeforeEach
    public void GameServiceSetup() {
        gameService = new GameService();
        gameService.init(10);
        
        playerBoard = new Board(10);
        playerBoard.placeShip(new Coordinate(5, 7), 3, true, "teste");
        
        computerBoard = new Board(10);
        computerBoard.placeShip(new Coordinate(5, 7), 3, true, "teste");
    }
    
    @Test
    public void testPlacePlayerShipsTrue() {
        Assertions.assertTrue(gameService.placePlayerShip(5, 7, 3, true, "teste"));
    }
    @Test
    public void testPlacePlayerShipsFalse() {
        Assertions.assertFalse(gameService.placePlayerShip(11, 7, 3, true, "teste"));
    }
     
    @Test
    public void testPlayerAttackHit() {
        AttackResult res = computerBoard.attack(new Coordinate(5, 7));
        Assertions.assertTrue(res.isHit());
    }
        
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
        Assertions.assertEquals("teste", m.get("sunk"));
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
        Assertions.assertEquals("teste", m.get("sunk"));
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
    @Test
    public void testAddNeighborsFollowDirection() {
    Coordinate firstHit = new Coordinate(5, 5);
    playerBoard.getShots().add(firstHit);
    gameService.getLastShot().setCoordinate(firstHit);

    Coordinate secondHit = new Coordinate(5, 6);
    playerBoard.getShots().add(secondHit);
    gameService.getSecondHit().setCoordinate(secondHit);
    gameService.getLastShot().setCoordinate(secondHit); 

    gameService.addNeighbors(secondHit, 10, true);

    Assertions.assertEquals(1, gameService.getPotentialShots().size());
    }

    @Test
    public void testIsValidPosition(){
        Coordinate c = new Coordinate(5, 7);
        Assertions.assertTrue(gameService.isValidPosition(c, 10));
    }
    @Test 
    public void testIsNotValidPosition(){
        Coordinate c = new Coordinate(12, 7);
        Assertions.assertFalse(gameService.isValidPosition(c, 10));
    }
}
