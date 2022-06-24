package nz.ac.arastudent.xil0393.bcde223ass3;

public class PathModel {
    private int[][] playerPathMap;
    private int stepCount;
    public PathModel(int h, int w) {
        this.playerPathMap = new int[h][w];
        this.clearPath();
    }
    boolean pushStep(int[] pos){
        if (pos[0]>=0&&pos[0]<this.playerPathMap.length&&pos[1]>=0&&pos[1]<this.playerPathMap[0].length) {
            if (this.playerPathMap[pos[0]][pos[1]] == -1) {
                this.stepCount ++;
                this.playerPathMap[pos[0]][pos[1]] = this.stepCount;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    int getSteps() {
        return this.stepCount;
    }
    int[][] getStepMap() {
        return this.playerPathMap;
    }
    void clearPath() {
        for (int i=0;i<this.playerPathMap.length;i++) {
            for (int j=0;j<this.playerPathMap[0].length;j++) {
                this.playerPathMap[i][j] = -1;

            }
        }
        this.stepCount = 0;
    }
    int[] goBackward() {
        int[] newPos = new int[2];
        for (int i=0;i<this.playerPathMap.length;i++) {
            for (int j=0;j<this.playerPathMap[0].length;j++) {
                if (this.playerPathMap[i][j]==stepCount) {
                    this.playerPathMap[i][j] = -1;
                }
                if (this.playerPathMap[i][j]==stepCount-1) {
                    newPos[0] = i;
                    newPos[1] = j;
                }
            }
        }
        stepCount--;
        return newPos;
    }
    void overwritePath(PathModel newPath) {
        this.playerPathMap = newPath.playerPathMap;
        this.stepCount = newPath.stepCount;
    }
}
