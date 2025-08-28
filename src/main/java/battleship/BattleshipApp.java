package battleship;

import static spark.Spark.*;
import com.google.gson.Gson;
import battleship.service.GameService;
import java.util.Map;

public class BattleshipApp {
    public static void main(String[] args) {
        port(8080);

        // serve tudo que estiver em src/main/resources/public na raiz
        staticFiles.location("/public");

        // redireciona "/" para index.html
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        GameService service = new GameService();
        Gson gson = new Gson();

        // inicializa tabuleiro e posiciona navios do computador
        get("/api/init", (req, res) -> {
            int size = Integer.parseInt(req.queryParams("size"));
            service.init(size);
            res.type("application/json");
            return Map.of("size", size);
        }, gson::toJson);

        // posiciona um navio do jogador
        // posiciona um navio do jogador
        post("/api/place", (req, res) -> {
            Map<?,?> body = gson.fromJson(req.body(), Map.class);
            boolean ok = service.placePlayerShip(
                    ((Double) body.get("row")).intValue(),
                    ((Double) body.get("col")).intValue(),
                    ((Double) body.get("length")).intValue(),
                    (Boolean) body.get("vertical"),
                    (String) body.get("name")
            );
            res.type("application/json");
            return Map.of("ok", ok);
        }, gson::toJson);


        // jogador ataca computador
        post("/api/attack", (req, res) -> {
            Map<?,?> body = gson.fromJson(req.body(), Map.class);
            return service.playerAttack(
                    ((Double) body.get("row")).intValue(),
                    ((Double) body.get("col")).intValue()
            );
        }, gson::toJson);

        // computador ataca jogador
        get("/api/attack/computer", (req, res) -> {
            return service.computerAttack();
        }, gson::toJson);
    }
}