package nz.ac.arastudent.xil0393.bcde223ass3;

public class GameModel {
    BoardModel gameBoard;
    HumanPlayer humanPlayer;
    ComputerPlayer cPlayer;
    boolean solvableBoard = false;
    int difficulty,width,height,stepLowerLimit;
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
        if (this.difficulty>=1&&this.difficulty<=3) {
            this.width = 4;
            this.height = 4;
            this.stepLowerLimit = 5 + difficulty;
        } else if (this.difficulty>=4&&this.difficulty<=6) {
            this.width = 6;
            this.height = 6;
            this.stepLowerLimit = 1 + difficulty * 2;
        } else if (this.difficulty>=7&&this.difficulty<=9) {
            this.width = 8;
            this.height = 8;
            this.stepLowerLimit = this.difficulty * 2;
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
            this.cPlayer.tryNextMove(stepLowerLimit+2);
            //check if board is too easy
            if (this.cPlayer.successPath.getSteps()!=0) {
                if (this.cPlayer.successPath.getSteps()>=(stepLowerLimit-2))
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