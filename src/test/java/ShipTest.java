import battleship.model.Ship;
import battleship.model.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.List;
public class ShipTest {
    Coordinate c = new Coordinate(5, 7);
    Coordinate c2 = new Coordinate(4, 7);
    Ship ship = new Ship("Submarine", c, 3, true);
    @Test
    public void testShipConstructor() {
        Assertions.assertNotNull(ship);
    }
    @Test
    public void testShipGetName() {
        Assertions.assertEquals("Submarine", ship.getName());
    }
    @Test
    public void testShipOccupiesTrue() {
        Assertions.assertTrue(ship.occupies(c));
    }
    @Test
    public void testShipOccupiesFalse() {
        Assertions.assertFalse(ship.occupies(c2));
    }
    @Test
    public void testRegisterHitTrue() {
        Assertions.assertTrue(ship.registerHit(c));
    }
    @Test
    public void testRegisterHitFalse(){
        Assertions.assertFalse(ship.registerHit(c2));
    }
    @Test
    public void testisHitAtTrue() {
        ship.registerHit(c);
        Assertions.assertTrue(ship.isHitAt(c));
    }
    @Test
    public void testisHitAtFalse() {
        ship.registerHit(c);
        Assertions.assertFalse(ship.isHitAt(c2));
    }
    @Test
    public void testIsSunkFalse() {
        ship.registerHit(new Coordinate(6, 7));
        Assertions.assertFalse(ship.isSunk());
    }

    @Test
    public void testIsSunkTrue() {  
        ship.registerHit(new Coordinate(5, 7));
        ship.registerHit(new Coordinate(6, 7));
        ship.registerHit(new Coordinate(7, 7));
        Assertions.assertTrue(ship.isSunk());
    }
    
    @Test
    public void testGetPositionsOne() {
        List<Coordinate> positions = ship.getPositions();
        Assertions.assertEquals(3, positions.size());
    }
    @Test
    public void testGetPositionsTwo() {
        List<Coordinate> positions = ship.getPositions();
        Assertions.assertTrue(positions.contains(c));
    }
    @Test
    public void testGetPositionsThree() {
        List<Coordinate> positions = ship.getPositions();
        Assertions.assertTrue(positions.contains(new Coordinate(6, 7)));
    }
}