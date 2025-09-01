package battleship.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int size;
    private final List<Ship> ships = new ArrayList<>();
    private final List<Coordinate> shots = new ArrayList<>();

    public Board(int size) {
        this.size = size;
    }

    public int getSize() { return size; }
    public List<Coordinate> getShots() { return shots; }


    
    public boolean placeShip(Coordinate start, int length, boolean vertical, String name) {
        // comcerta se o navio ultrapassa a borda do tabuleiro
        if (vertical) {
            if (start.getRow() + length > size) return false;
        } else {
            if (start.getCol() + length > size) return false;
        }

        Ship ship = new Ship(name, start, length, vertical);

        for (Coordinate pos : ship.getPositions()) {
            if (pos.getRow() < 0 || pos.getRow() >= size || pos.getCol() < 0 || pos.getCol() >= size)
                return false;

            for (Ship s : ships) {
                if (s.occupies(pos)) return false;
            }

            // Para cada parte do novo navio, checa se seus "vizinhos" estão livres.
            if (!isCellSpacedCorrectly(pos)) {
                return false;
            }
        }
        
        ships.add(ship);
        return true;
    }

    private boolean isCellSpacedCorrectly(Coordinate c) {
        // Matriz com as 8 direções adjacentes (incluindo diagonais)
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},// linha superior
            { 0, -1},          { 0, 1},// lados
            { 1, -1}, { 1, 0}, { 1, 1}// linha inferior
        };

        for (int[] dir : directions) {
            int newRow = c.getRow() + dir[0];
            int newCol = c.getCol() + dir[1];
            Coordinate neighbor = new Coordinate(newRow, newCol);

            // Verifica se a coordenada vizinha está dentro do tabuleiro
            if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                // Verifica se algum navio já posicionado ocupa esta célula vizinha
                for (Ship existingShip : this.ships) {
                    if (existingShip.occupies(neighbor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public AttackResult attack(Coordinate c) {
        if (shots.contains(c)) return new AttackResult(false, null, false);
        shots.add(c);
        for (Ship s : ships) {
            if (s.registerHit(c)) {
                return new AttackResult(
                        true,
                        s.isSunk() ? s.getName() : null,
                        allSunk()
                );
            }
        }
        return new AttackResult(false, null, false);
    }

    public boolean allSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    
    public static class AttackResult {
        private final boolean hit;
        private final String sunkShip;
        private final boolean gameOver;

        public AttackResult(boolean hit, String sunkShip, boolean gameOver) {
            this.hit = hit;
            this.sunkShip = sunkShip;
            this.gameOver = gameOver;
        }

        public boolean isHit() { return hit; }
        public String getSunkShip() { return sunkShip; }
        public boolean isGameOver() { return gameOver; }
    }
}