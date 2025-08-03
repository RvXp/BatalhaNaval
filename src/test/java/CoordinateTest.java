import battleship.model.Coordinate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
public class CoordinateTest {
    Coordinate c = new Coordinate(5, 7);
    @Test
    public void testCoordinateConstructor() {
        Assertions.assertNotNull(c);
    }
    @Test
    public void testCoordinateGetRow() {
        Assertions.assertEquals(5, c.getRow());
    }
    @Test
    public void testCoordinateGetCol() {
        Assertions.assertEquals(7, c.getCol());
    }
    @Test
    public void testCoordinateEqualsTrue() {
        Coordinate c2 = new Coordinate(5, 7);
        Assertions.assertTrue(c.equals(c2));
    }
    @Test
    public void testCoordinateEqualsFalse() {
        Coordinate c2 = new Coordinate(4, 7);
        Assertions.assertFalse(c.equals(c2));
    }
    @Test
    public void testCoordinateHashCode() {
        Coordinate c2 = new Coordinate(5, 7);
        Assertions.assertEquals(c.hashCode(), c2.hashCode());
    }
}
