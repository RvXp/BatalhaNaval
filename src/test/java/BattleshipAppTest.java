import battleship.service.GameService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BattleshipAppTest {
    
    private final GameService service = new GameService();
    private final Gson gson = new Gson();

    @Test
    public void testInitGame() {
        service.init(10);
        assertNotNull(service);
    }

    @Test
    public void testPlacePlayerShip() {
        service.init(10);
        boolean result = service.placePlayerShip(5, 7, 3, true, "teste");
        assertTrue(result);
    }

    @Test
    public void testPlayerAttack() {
        service.init(10);
        Map<String, Object> result = service.playerAttack(0, 0);
        assertNotNull(result.get("hit"));
    }
    @Test
    public void testJsonConversion() {
        Map<String, String> testMap = Map.of("test", "value");
        String json = gson.toJson(testMap);
        assertTrue(json.contains("test"));
        assertTrue(json.contains("value"));
    }
}