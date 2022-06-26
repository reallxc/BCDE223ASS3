package nz.ac.arastudent.xil0393.bcde223ass3;

public class GameModel {
    BoardModel gameBoard;
    HumanPlayer humanPlayer;
    ComputerPlayer cPlayer;
    boolean solvableBoard = false;
    int difficulty,width,height,stepLowerLimit,stepUpperLimit;
    public GameModel(){
        this.setDifficulty(4);
        init();
    }
    public GameModel(int difficulty){
        this.setDifficulty(difficulty);
        init();
    }
    private void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        if (difficulty>=1&&difficulty<=3) {
            this.width = 4;
            this.height = 4;
            this.stepLowerLimit = difficulty * 3;
            this.stepUpperLimit = difficulty * 3 + 3;
//            1:3-5 steps  2:6-8 steps  3:9-11 steps
        } else if (difficulty>=4&&difficulty<=6) {
            this.width = 6;
            this.height = 6;
            this.stepLowerLimit = difficulty * 2 + difficulty - 4;
            this.stepUpperLimit = difficulty * 2 + difficulty - 1;
//            4:8-10 steps  5:11-13 steps  6:14-16 steps
        } else if (difficulty>=7&&difficulty<=9) {
            this.width = 8;
            this.height = 8;
            this.stepLowerLimit = difficulty * 2 + difficulty - 7;
            this.stepUpperLimit = difficulty * 2 + difficulty - 1;
//            7:14-16 steps  8:17-19 steps  9:20-22 steps
        }
    }
    public static int getMazeSize(int difficulty) {
        if (difficulty>=1&&difficulty<=3) {
            return 4;
        } else if (difficulty>=4&&difficulty<=6) {
            return 6;
        } else if (difficulty>=7&&difficulty<=9) {
            return 8;
        } else {
            return 0;
        }
    }
    private void init() {
        this.gameBoard = new BoardModel(this.height,this.width);
        this.cPlayer = new ComputerPlayer(this.gameBoard);
        this.humanPlayer = new HumanPlayer(this.gameBoard);
        prepareBoard();
    }
    private void prepareBoard() {
        int count = 1;
        do {
            this.cPlayer.myPath.clearPath();
            this.cPlayer.successPath.clearPath();
            this.gameBoard.generateBoard();
            this.cPlayer.resetPosition();
            this.cPlayer.tryNextMove(stepUpperLimit);
            //check if board is too easy
            if (this.cPlayer.successPath.getSteps()!=0) {
                if (this.cPlayer.successPath.getSteps()>=stepLowerLimit)
                {
                    this.solvableBoard = true;
                }
            }
            count ++;
        } while (!this.solvableBoard);
        System.out.println("Got solvable maze after "+ count+" tries.");
        System.out.println("Best solution step: "+ this.cPlayer.successPath.getSteps());
    }
    public boolean checkBestPath() {
        return this.humanPlayer.myPath.getSteps() <= this.cPlayer.successPath.getSteps();
    }
    public void restartGame() {
        this.humanPlayer.myPath.clearPath();
        this.humanPlayer.resetPosition();
    }
}