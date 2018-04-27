import java.util.Random;

public class Main{
  public static void main(String []args) {
    Board board = new Board(11,5,5);
    board.print();
  }
}

class Board {
  int mine_count;
  int found_mines = 0;
  int rows;
  int cols;
  public Board(int mines, int h, int w){
    //mine_count = mines;
    rows = h;
    cols = w;
    boolean[][] real_grid = new boolean[rows][cols];
    //now we fill the real_grid with 0s
    for (int i = 0; i < rows; i++){
      for (int j = 0; j < cols; j++){
        real_grid[i][j] = false;
      }
    }
    int[][] mine_coords = new int[mines][2];
    Random rand = new Random();
    int row = 0; int col = 0;
    for (int i = 0; i < mines; i++){
      boolean used = true;
      while (used){//the purpose of this loop is to keep pickig coordinate pairs until we find one that hasn't been used
        row = rand.nextInt(rows-1);
        col = rand.nextInt(cols-1);
        used = false;
        for (int j = 0; j < i; j++){
          if (mine_coords[j][0] == row && mine_coords[j][1] == col){
            used = true;
          }
        }
      }// end while
      real_grid[row][col] = true;
    }// end for

    String[][] pub_grid = new String[rows][cols];
    //now we need to fill it in with @ symbols which represent unguessed tiles
    for (int i = 0; i < rows; i++){
      for (int j = 0; j < cols; j++){
        pub_grid[i][j] = "@";
      }
    }

  }
  public void print(){
    System.out.println(mine_count);
  }
}
