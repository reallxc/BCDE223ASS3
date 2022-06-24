package nz.ac.arastudent.xil0393.bcde223ass3;

public abstract class Player {
    int[] currentPosition = new int[2];
    BoardModel myBoard;
    PathModel myPath;
    public Player(BoardModel board){
        this.currentPosition[0] = 0;
        this.currentPosition[1] = 0;
        this.myBoard = board;
        this.myPath = new PathModel(board.getHeight(), board.getWidth());
    }
    void resetPosition() {
        this.currentPosition[0] = 0;
        this.currentPosition[1] = 0;
    }
    boolean movePlayer(int direction){
        //Use direction to move
        //direction 1 up 2 down 3 left 4 right
        int step = this.myBoard.getPositionNumber(this.currentPosition);
        switch(direction){
            case 1:
                if (step<this.currentPosition[0]) {
                    this.currentPosition[0] = this.currentPosition[0] - step;
                    if (this.myPath.pushStep(this.currentPosition)) {
                        return true;
                    } else {
                        this.currentPosition[0] = this.currentPosition[0] + step;
                    }
                }
                break;
            case 2:
                if (step<(this.myBoard.getHeight()-this.currentPosition[0])) {
                    this.currentPosition[0] = this.currentPosition[0] + step;
                    if (this.myPath.pushStep(this.currentPosition)) {
                        return true;
                    } else {
                        this.currentPosition[0] = this.currentPosition[0] - step;
                    }
                }
                break;
            case 3:
                if (step<this.currentPosition[1]) {
                    this.currentPosition[1] = this.currentPosition[1] - step;
                    if (this.myPath.pushStep(this.currentPosition)) {
                        return true;
                    } else {
                        this.currentPosition[1] = this.currentPosition[1] + step;
                    }
                }
                break;
            case 4:
                if (step<(this.myBoard.getWidth()-this.currentPosition[1])) {
                    this.currentPosition[1] = this.currentPosition[1] + step;
                    if (this.myPath.pushStep(this.currentPosition)) {
                        return true;
                    } else {
                        this.currentPosition[1] = this.currentPosition[1] - step;
                    }
                }
                break;
        }
        return false;
    }
}
