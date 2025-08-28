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
        }

        ships.add(ship);
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