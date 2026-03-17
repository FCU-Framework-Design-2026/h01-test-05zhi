package org.example;

import java.util.*;

// ===== AbstractGame =====
abstract class AbstractGame {
    abstract void setPlayers(Player p1, Player p2);
    abstract boolean gameOver();
    abstract String move(int from, int to);
}

// ===== Player =====
class Player {
    String name;
    int side;

    public Player(String name, int side) {
        this.name = name;
        this.side = side;
    }
}

// ===== Chess =====
class Chess {
    String name;
    int weight;
    int side;
    int loc;
    boolean revealed = false;
    boolean captured = false;

    public Chess(String name, int weight, int side, int loc) {
        this.name = name;
        this.weight = weight;
        this.side = side;
        this.loc = loc;
    }

    public String toString() {
        if (captured) return "Ｘ";
        if (!revealed) return "＿";
        return name;
    }
}

// ===== ChessGame =====
class ChessGame extends AbstractGame {
    Chess[] board = new Chess[32];
    boolean[] XGrid = new boolean[32];
    Player p1, p2;
    Player currentPlayer;
    Scanner sc = new Scanner(System.in);

    String[] names = {
            "將","士","士","象","象","車","車","馬","馬","包","包","兵","兵","兵","兵","兵",
            "將","士","士","象","象","車","車","馬","馬","包","包","兵","兵","兵","兵","兵"
    };

    int[] weights = {
            7,6,6,5,5,4,4,3,3,2,2,1,1,1,1,1,
            7,6,6,5,5,4,4,3,3,2,2,1,1,1,1,1
    };

    public void setPlayers(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.currentPlayer = p1;
    }

    public void generateChess() {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < 32; i++) positions.add(i);
        Collections.shuffle(positions);

        for (int i = 0; i < 32; i++) {
            board[i] = new Chess(names[i], weights[i], i < 16 ? 0 : 1, positions.get(i));
        }
    }

    public void showAllChess() {
        String[] grid = new String[32];
        Arrays.fill(grid, "＿");

        for (Chess c : board) {
            if (c.loc >= 0) grid[c.loc] = c.toString();
        }

        for (int i = 0; i < 32; i++) {
            if (XGrid[i]) grid[i] = "Ｘ";
        }

        System.out.println("\n目前玩家：" + currentPlayer.name);
        System.out.println("   1   2   3  4   5  6   7   8");
        for (int r = 0; r < 4; r++) {
            char row = (char) ('A' + r);
            System.out.print(row + "  ");
            for (int c = 0; c < 8; c++) {
                System.out.print(grid[r * 8 + c] + "  ");
            }
            System.out.println();
        }
    }

    private int parsePos(String input) {
        input = input.toUpperCase();
        int row = input.charAt(0) - 'A';
        int col = input.charAt(1) - '1';
        return row * 8 + col;
    }

    // ===== move =====
    public String move(int from, int to) {
        Chess c1 = null, c2 = null;
        for (Chess c : board) {
            if (c.loc == from) c1 = c;
            if (c.loc == to) c2 = c;
        }

        if (c1 == null) return "來源格沒有棋子！";
        if (!c1.revealed) return "來源棋子尚未翻開！";

        if (c2 == null) return "目的格沒有棋子！";
        if (!c2.revealed) return "目的棋子尚未翻開！";

        if (c1.side == c2.side) return "不能吃自己的棋子！";

        if (c1.weight < c2.weight) return "棋子太輕，無法吃掉目標棋子！";

        // 標記原格 X
        XGrid[from] = true;

        // 吃掉目的格
        c2.captured = true;
        c2.loc = -1;

        // 移動棋子
        c1.loc = to;

        return ""; // 成功
    }

    public boolean gameOver() {
        int side0 = 0, side1 = 0;
        for (Chess c : board) {
            if (!c.captured) {
                if (c.side == 0) side0++;
                else side1++;
            }
        }
        return side0 == 0 || side1 == 0;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == p1) ? p2 : p1;
    }

    public void play() {
        while (!gameOver()) {
            showAllChess();
            System.out.print("輸入位置：");
            String input = sc.next();
            int pos = parsePos(input);

            Chess target = null;
            for (Chess c : board) {
                if (c.loc == pos) target = c;
            }

            if (target == null) {
                System.out.println("格子沒有棋子，請重新輸入！");
                continue;
            }

            if (!target.revealed) {
                target.revealed = true;
                switchPlayer();
            } else {
                System.out.print("輸入目的位置：");
                String input2 = sc.next();
                int to = parsePos(input2);

                String result = move(pos, to);
                if (result.isEmpty()) {
                    switchPlayer();
                } else {
                    System.out.println("移動失敗！原因：" + result);
                }
            }
        }
        showAllChess();
        System.out.println("Game Over!");
    }
}

// ===== Main =====
public class Main {
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.setPlayers(new Player("玩家1", 0), new Player("玩家2", 1));
        game.generateChess();
        game.play();
    }
}