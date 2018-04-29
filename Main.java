import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class Main{

  int rows = 10;
  int cols = 10;
  Board board = new Board(20, rows, cols);

  private Frame window;
  private Label header;
  private Panel[] grid = new Panel[rows];
  private Button[][] button_array;

  boolean mode = true;

  public Main(){
    prepareGUI();
    fillGrid();
  }

  public static void main(String []args) {
    Main main = new Main();
  }

  private void prepareGUI(){
    window = new Frame("Minesweeper");
    window.setSize(50*cols,50*rows+100);
    window.setLayout(new GridLayout(rows+2,cols));
    window.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent windowEvent){
        System.exit(0);
      }
    });
    header = new Label();
    header.setAlignment(Label.CENTER);
    header.setText("Minesweeper");

    for (int i  = 0; i < rows; i++){
      grid[i] = new Panel();
      grid[i].setLayout(new GridLayout());
      grid[i].setSize(50*cols,50);
    }
    window.add(header);

    Panel options = new Panel();
    options.setLayout(new FlowLayout());

    Button guess_mode = new Button("Guess Mode");
    guess_mode.setActionCommand("guess");
    guess_mode.addActionListener(new ModeSwitchListener());
    options.add(guess_mode);

    Button mark_mode = new Button("Mark Mode");
    mark_mode.setActionCommand("mark");
    mark_mode.addActionListener(new ModeSwitchListener());
    options.add(mark_mode);

    window.add(options);
    for (int i = 0; i < rows; i++){
      window.add(grid[i]);
    }
    window.setVisible(true);
  }

  //fills the grid with the grid in the board
  private void fillGrid(){

    //first we need to empty out the grid
    for (int i = 0; i < rows; i++){
      grid[i].removeAll();
    }

    //now we fill it with new buttons

    button_array = new Button[rows][cols];//to store all the buttons

    for (int i = 0; i < rows; i++){
      for (int j = 0; j < cols; j++){
        button_array[i][j] = new Button(board.pub_grid[i][j]);
        button_array[i][j].setActionCommand(Integer.toString(i)+","+Integer.toString(j));
        button_array[i][j].addActionListener(new ButtonClickListener());
        button_array[i][j].setSize(25,25);
        grid[i].add(button_array[i][j]);
      }
    }
    window.setVisible(true);
  }

  private void changeTile(int row, int col){

    grid[row].removeAll();

    for (int j = 0; j < cols; j++){
      button_array[row][j] = new Button(board.pub_grid[row][j]);
      button_array[row][j].setActionCommand(Integer.toString(row)+","+Integer.toString(j));
      button_array[row][j].addActionListener(new ButtonClickListener());
      button_array[row][j].setSize(25,25);
      grid[row].add(button_array[row][j]);
    }

    window.setVisible(true);

  }

  private class ButtonClickListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if(mode){
        if (board.takeGuess(e.getActionCommand())){
          fillGrid();
        } else {
          changeTile(Integer.parseInt(e.getActionCommand().split(",")[0]),Integer.parseInt(e.getActionCommand().split(",")[1]));
        }
      } else {
        board.takeMark(e.getActionCommand());
        changeTile(Integer.parseInt(e.getActionCommand().split(",")[0]),Integer.parseInt(e.getActionCommand().split(",")[1]));
      }
    }
  }

  private class ModeSwitchListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      mode = (e.getActionCommand()=="guess");//when you switch mode
    }
  }

}

class Board {
  int mine_count;
  int tiles_guessed = 0;
  int rows;
  int cols;
  String[][] pub_grid;
  Boolean[][] real_grid;
  boolean won = false;
  public Board(int mines, int h, int w){
    //mine_count = mines;
    rows = h;
    cols = w;
    real_grid = new Boolean[rows][cols];
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
      mine_count++;
    }// end for

    pub_grid = new String[rows][cols];
    //now we need to fill it in with a space which represent unguessed tiles
    for (int i = 0; i < rows; i++){
      for (int j = 0; j < cols; j++){
        pub_grid[i][j] = " ";
      }
    }

  }

  public void print(){

  }

  public boolean takeGuess(String raw_guess){
    String[] raw_guess_coords = raw_guess.split(",");
    int row = Integer.parseInt(raw_guess_coords[0]);
    int col = Integer.parseInt(raw_guess_coords[1]);

    if (! (pub_grid[row][col] == " " || pub_grid[row][col] == "#")){
      return false;//this is in case you click a tile that is already guessed
    } else {
      tiles_guessed++;
      if (tiles_guessed == rows * cols - mine_count){
        won = true;
      }
    }

    if (real_grid[row][col]){
      pub_grid[row][col] = "@";
      return true;
    } else {
      pub_grid[row][col] = Integer.toString(nearTiles(row,col));
      if (nearTiles(row,col)==0){
        for (int i = -1; i < 2; i++){
          for (int j = -1; j < 2; j++){
            try{
              if (pub_grid[row+i][col+j] == " " || pub_grid[row+i][col+j] == "#"){//if it hasn't been guessed yet
                takeGuess(Integer.toString(row+i)+","+Integer.toString(col+j));
              }
            } catch (IndexOutOfBoundsException e) {}//do nothing
          }
        }
        return true;
      }
      return false;
    }

  }

  public void takeMark(String raw_mark){
    String[] raw_mark_coords = raw_mark.split(",");
    int row = Integer.parseInt(raw_mark_coords[0]);
    int col = Integer.parseInt(raw_mark_coords[1]);

    if (pub_grid[row][col]=="#"){
      pub_grid[row][col] = " ";
    } else {
      pub_grid[row][col] = "#";
    }

  }

  public int nearTiles(int row, int col){
    int near = 0;

    for (int i = row - 1; i < row + 2; i ++){
      for (int j = col - 1; j < col + 2; j++){
        try {if(real_grid[i][j]){near++;}} catch(IndexOutOfBoundsException e){}
      }
    }

    return near;

  }

}
