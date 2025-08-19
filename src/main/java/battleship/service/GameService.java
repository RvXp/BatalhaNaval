package battleship.service;


import battleship.model.Board;
import battleship.model.Coordinate;
import battleship.model.Board.AttackResult;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameService {
    private Board playerBoard;
    private Board computerBoard;
    private final Random random = new Random();

    public void init(int size) {
        playerBoard = new Board(size);
        computerBoard = new Board(size);
        int[] lengths = {2,3,3,4,5};
        String[] names = {"destroyer", "submarine", "cruiser", "battleship", "carrier"};
        for (int i = 0; i < lengths.length; i++) {
            int len = lengths[i];
            String name = names[i]; // Atribui nome baseado no índice
            boolean placed = false;
            while (!placed) {
                int r = random.nextInt(size), c = random.nextInt(size);
                boolean v = random.nextBoolean();
                placed = computerBoard.placeShip(
                        new Coordinate(r, c), len, v, name // Passe o nome para placeShip
                );
            }
        }
    }

    public boolean placePlayerShip(int row, int col, int length, boolean vertical, String name) {
        return playerBoard.placeShip(
                new Coordinate(row, col), length, vertical, name
        );
    }

    public Map<String,Object> playerAttack(int row, int col) {
        AttackResult res = computerBoard.attack(
                new Coordinate(row, col)
        );
        Map<String,Object> m = new HashMap<>();
        m.put("hit", res.isHit());
        m.put("sunk", res.getSunkShip());
        m.put("gameOver", res.isGameOver());
        return m;
    }

    public Map<String,Object> computerAttack() {
        int size = playerBoard.getSize();
        Coordinate shot;
        do {
            shot = new Coordinate(
                    random.nextInt(size),
                    random.nextInt(size)
            );
        } while (playerBoard.getShots().contains(shot));
        AttackResult res = playerBoard.attack(shot);
        Map<String,Object> m = new HashMap<>();
        m.put("row", shot.getRow());
        m.put("col", shot.getCol());
        m.put("hit", res.isHit());
        m.put("sunk", res.getSunkShip());
        m.put("gameOver", res.isGameOver());
        return m;
    }
}