package battleship.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import battleship.model.Board;
import battleship.model.Board.AttackResult;
import battleship.model.Coordinate;

public class GameService {
    private Board playerBoard;
    private Board computerBoard;

    private final Random random = new Random();
    private LastShot lastShot;
    private LastShot secondHit;
    private List<Coordinate> allAttacks = new ArrayList<>();
    private List<Coordinate> potentialShots = new ArrayList<>();

    public LastShot getLastShot() {
        return this.lastShot;
    }  
    public LastShot getSecondHit() {
        return this.secondHit;
    }

    public List<Coordinate> getPotentialShots() {
        return this.potentialShots;
    }

    public void init(int size) {
        playerBoard = new Board(size);

        lastShot = new LastShot();
        lastShot.setCoordinate(null);
        lastShot.setHit(false);

        secondHit = new LastShot();
        secondHit.setCoordinate(null);
        secondHit.setHit(false);

        int[] lengths = {5, 4, 3, 3, 2};
        String[] names = {"carrier", "battleship", "cruiser", "submarine", "destroyer"};
        
        boolean allShipsPlaced = false;
        int boardSetupAttempts = 0;

        // Configura o tabuleiro até ter sucesso
        do {
            computerBoard = new Board(size); // Limpa e cria um novo tabuleiro a cada tentativa
            boardSetupAttempts++;
            if (boardSetupAttempts > 100) { 
                System.err.println("NÃO FOI POSSÍVEL CONFIGURAR O TABULEIRO APÓS 100 TENTATIVAS!");
                return;
            }

            boolean successThisAttempt = true;
            for (int i = 0; i < lengths.length; i++) {
                int len = lengths[i];
                String name = names[i];
                boolean placed = false;
                int placementAttempts = 0;
                
                while (!placed && placementAttempts < 2000) {
                    int r = random.nextInt(size);
                    int c = random.nextInt(size);
                    boolean v = random.nextBoolean();
                    placed = computerBoard.placeShip(new Coordinate(r, c), len, v, name);
                    placementAttempts++;
                }

                // Se um navio não pôde ser colocado, esta tentativa de montar o tabuleiro falhou
                if (!placed) {
                    successThisAttempt = false;
                    System.out.println("Falha ao posicionar '" + name + "'. Reiniciando a disposição do tabuleiro...");
                    break; // Sai do 'for' e tenta o 'do-while' novamente
                }
            }
            allShipsPlaced = successThisAttempt;

        } while (!allShipsPlaced);
        
        System.out.println("Tabuleiro do computador configurado com sucesso após " + boardSetupAttempts + " tentativa(s).");
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
        Coordinate shot = null;
        do {
        if (potentialShots.isEmpty()) {
            shot = new Coordinate(random.nextInt(size), random.nextInt(size));
            lastShot.setCoordinate(null);
            secondHit.setCoordinate(null);
        }else {
            shot = potentialShots.remove(random.nextInt(potentialShots.size()));
        }
    }while (playerBoard.getShots().contains(shot));

    AttackResult res = playerBoard.attack(shot);
    allAttacks.add(shot);

    if(res.isHit()) {
        if(lastShot.coordinate == null) {
            lastShot.setCoordinate(shot);
            addNeighbors(shot, size, false);
        }else{
            if(secondHit.coordinate == null){
                secondHit.setCoordinate(shot);
            }
            addNeighbors(shot, size, true);
        }

        if (res.getSunkShip() != null) {
            potentialShots.clear();
            lastShot.setCoordinate(null);
            secondHit.setCoordinate(null);
        }
    }else{
        if (lastShot.coordinate != null && secondHit.coordinate != null) {
            potentialShots.clear();
        }
    }
    Map<String,Object> m = new HashMap<>();
    m.put("row", shot.getRow());
    m.put("col", shot.getCol());
    m.put("hit", res.isHit());
    m.put("sunk", res.getSunkShip());
    m.put("gameOver", res.isGameOver());
    return m;
    }

   public void addNeighbors(Coordinate shot, int size, boolean followDirection) {
    if (followDirection) {
        potentialShots.clear();
        Coordinate firstHitCoord = lastShot.coordinate;
        if (firstHitCoord.getRow() == secondHit.coordinate.getRow()) {
            if (shot.getCol() > firstHitCoord.getCol()) {
                Coordinate nextShot = new Coordinate(shot.getRow(), shot.getCol() + 1);
                if (isValidPosition(nextShot, size)) {
                    potentialShots.add(nextShot);
                }
            }else {
                Coordinate nextShot = new Coordinate(shot.getRow(), shot.getCol() - 1);
                if (isValidPosition(nextShot, size)) {
                    potentialShots.add(nextShot);
                }
            }
        }else {
            if (shot.getRow() > firstHitCoord.getRow()) {
                Coordinate nextShot = new Coordinate(shot.getRow() + 1, shot.getCol());
                if (isValidPosition(nextShot, size)) {
                    potentialShots.add(nextShot);
                }
            }else {
                Coordinate nextShot = new Coordinate(shot.getRow() - 1, shot.getCol());
                if (isValidPosition(nextShot, size)) {
                    potentialShots.add(nextShot);
                }
            }
        }
    }else {
        Coordinate[] neighbors = {
            new Coordinate(shot.getRow(), shot.getCol() - 1),
            new Coordinate(shot.getRow(), shot.getCol() + 1),
            new Coordinate(shot.getRow() - 1, shot.getCol()),
            new Coordinate(shot.getRow() + 1, shot.getCol())
        };
        for (Coordinate neighbor : neighbors) {
            if(isValidPosition(neighbor, size)) {
                potentialShots.add(neighbor);
            }
        }
    }
}
    public boolean isValidPosition(Coordinate coord, int size) {
        return coord.getRow() >= 0 && coord.getRow() < size &&
           coord.getCol() >= 0 && coord.getCol() < size;
    }

    public class LastShot{
        private Coordinate coordinate;
        private boolean Hit;
        private int direction;

        public void setCoordinate(Coordinate coordinate) {
            this.coordinate = coordinate;
        }
        public void setHit(boolean Hit) {
            this.Hit = Hit;
        }
        public int getDirection() {
            return direction;
        }
        public void setDirection(int direction) {
            this.direction = direction;
        }
    }

}
