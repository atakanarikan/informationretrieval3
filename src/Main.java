import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;


public class Main {
    static final double TPRATE = 0.15;
    static int verticeCount;
    static HashMap<Integer, String> people = new HashMap<Integer, String>();

    static double[][] transitionMatrix;
    static double[] x_t;
    static Matrix transition;
    static Matrix x;
    static Matrix xP;
    public static void main(String[] args) throws IOException, InterruptedException {
        readInput(args[0]);
        addTeleportationRate();
        randomWalk((int) (Math.random()*verticeCount)); // start from a random index
        int[] x = max50(xP.getColumnPackedCopy());
        for(int i = 0; i < x.length; i++) {
            System.out.println(i+1 + ". " + people.get(x[i]+1));
        }

    }
    /*
    reads the input from data.txt and fills people and adjacencyList containers.
     */
    public static void readInput(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        try {
            String line = br.readLine(); // *Vertices <number>
            verticeCount = Integer.parseInt(line.substring(10));
            transitionMatrix = new double[verticeCount][verticeCount];
            for(int i = 0; i < verticeCount; i++){
                line = br.readLine();
                String[] temp = line.split(" ");
                temp[1] = temp[1].substring(1, temp[1].length()-1); // get rid of quote marks
                people.put(Integer.parseInt(temp[0]), temp[1]);
            }
            line = br.readLine();
            line = br.readLine();
            while (line != null) {
                String[] temp = line.split(" ");
                int source = Integer.parseInt(temp[0]);
                int destination = Integer.parseInt(temp[1]);
                transitionMatrix[source-1][destination-1]++;
                transitionMatrix[destination-1][source-1]++;
                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }

    public static void addTeleportationRate() {
        for (int i = 0; i < transitionMatrix.length; i++) {
            double[] normalizedRow = normalize(transitionMatrix[i]);
            for(int j = 0; j < normalizedRow.length; j++) {
                transitionMatrix[i][j] = TPRATE/verticeCount + ((1-TPRATE)*normalizedRow[j]);
            }
        }
    }


    public static int[] max50(double[] array){
        int[] index = new int[50];
        boolean[] isTaken = new boolean[array.length];
        Arrays.fill(index,-1);
        Arrays.fill(isTaken,false);
        for (int j = 0; j <50 ; j++) {
            double max = Double.MAX_VALUE *-1;
            int correctIndex= 0;
            for (int i = 0; i < array.length ; i++) {
                if(array[i] >  max && !isTaken[i]){
                    max = array[i];
                    correctIndex = i;
                }
            }
            index[j]= correctIndex;
            isTaken[correctIndex] = true;
        }
        return index;
    }
    public static void randomWalk(int startIndex){
        transition = new Matrix(transitionMatrix);
        x_t = new double[verticeCount];
        x_t[startIndex] = 1;
        x = new Matrix(x_t, 1);
        xP = x.times(transition);
        while(!isStopped(x.getRowPackedCopy(), xP.getRowPackedCopy())){
            x = xP.timesEquals(1);
            xP = x.times(transition);
        }
    }

    public static double[] normalize(double[] row) {
        double total = 0;
        for(int i = 0; i < row.length; i++) {
            total += row[i];
        }
        for(int i = 0; i < row.length; i++) {
            row[i] = row[i]/total;
        }
        return row;
    }
    public static boolean isStopped(double[] row1, double[] row2){
        double total = 0;
        for(int i = 0; i < row1.length; i++) {
            total += Math.abs(row1[i] - row2[i]);
        }
        return total < 0.0000000000001;
    }


}
