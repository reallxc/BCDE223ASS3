package nz.ac.arastudent.xil0393.bcde223helloworld;

public class ComputerPlayer extends Player {
    PathModel successPath;
    public ComputerPlayer(BoardModel board) {
        super(board);
        this.successPath = new PathModel(this.myBoard.getHeight(), this.myBoard.getWidth());
    }

    //Path finding
    void tryNextMove(int maxStep){
        for (int i = 1;i<5;i++) {
            if (this.movePlayer(i)) {
                if (myBoard.isGoalPosition(this.currentPosition)&&this.myPath.getSteps()<=(maxStep)) {
                    if (this.successPath.getSteps()==0||this.myPath.getSteps()<this.successPath.getSteps()) {
                        this.successPath.overwritePath(this.myPath);
                    }
                } else {
                    this.tryNextMove(maxStep);
                }
                this.currentPosition = this.myPath.goBackward();
            }
        }
    }
}
