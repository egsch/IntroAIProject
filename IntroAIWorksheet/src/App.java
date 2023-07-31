//import java.util.Random;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;

public class App {
    public int n;
    public int m;
    public int penalty;
    int[][] cost;
    int[][] time;
    int[] limits;

    public static void main(String[] args) throws IOException {
        // int[] solution2 = {5, 3, 2, 5, 5, 3, 1, 1, 3, 4, 3, 1, 2, 4, 2};
        App a = new App();
        //a.processSolution(a.TimeCostHeuristic());
        int[] optSolution = a.thresholdSearch(a.TimeGreedyHeuristic());
        System.out.println("Solution:" + Arrays.toString(optSolution) + ", cost: " + a.processSolution(optSolution));
    }

    public App() throws FileNotFoundException {
        // size for constraints
        n = 15;
        m = 5;
        penalty = 1000;

        // get data
        cost = new int[n][m];
        Scanner in = new Scanner(new File("C:\\Users\\egsch\\IntroAIProject\\IntroAIWorksheet\\src\\input.txt"));
        for (int i = 0; i < n; i++) {
            for (int q = 0; q < m; q++) {
                cost[i][q] = in.nextInt();
            }
        }

        in.nextLine();

        time = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int q = 0; q < m; q++) {
                time[i][q] = in.nextInt();
            }
        }

        in.nextLine();

        limits = new int[m];
        for (int i = 0; i < m; i++) {
            limits[i] = in.nextInt();
        }
    }

    /** DO NOT USE **/
    public int[] TimeCostHeuristic() {
        int[] solution = new int[n];
        int[] remainingTime = limits.clone(); // used to store remaining time for each worker
        for (int i = 0; i < n; i++) {
            int j = 0;
            int weight = 2;

            // make sure limits are not exceeded on first few workers
            while (remainingTime[j] < time[i][j]) {
                j++;
            }

            int minTimeCost = time[i][j] + weight*cost[i][j];
            int minIndex = j;
            int timeThreshold = (int) Arrays.stream(time[i]).average().orElse(Double.NaN);

            // find minimum value and index, while also checking feasibility
            while (j < m) {
                if (time[i][j] + weight*cost[i][j] < minTimeCost && time[i][j] < timeThreshold && remainingTime[j] >= time[i][j]) {
                    minTimeCost = time[i][j] + weight*cost[i][j];
                    minIndex = j;
                }
                j++;
            }
            solution[i] = minIndex + 1;
            remainingTime[minIndex] -= time[i][minIndex];

        }

        return solution;
    }

    public int[] TimeGreedyHeuristic() {
        int[] solution = new int[n];
        int[] remainingTime = limits.clone(); // used to store remaining time for each worker
        for (int i = 0; i < n; i++) {
            int j = 0;

            // make sure limits are not exceeded on first few workers
            while (remainingTime[j] < time[i][j]) {
                j++;
            }

            int minTime = time[i][j];
            int minIndex = j;

            // find minimum value and index, while also checking feasibility
            while (j < m) {
                if (time[i][j] < minTime && remainingTime[j] >= time[i][j]) {
                    minTime = time[i][j];
                    minIndex = j;
                }
                j++;
            }
            solution[i] = minIndex + 1;
            remainingTime[minIndex] -= time[i][minIndex];

        }

        return solution;
    }

    private int processSolution(int[] solution) {
        // process solution
        int sCost = 0;
        int[] sTimes = new int[m];
        for (int j = 0; j < n; j++) {
            sCost += cost[j][solution[j] - 1];
            sTimes[solution[j] - 1] += time[j][solution[j] - 1];
        }

        // check for feasibility
        int feasible = 1;
        for (int j = 0; j < m; j++) {
            if (sTimes[j] > limits[j] && feasible == 1) {
                feasible = 0;
                sCost += penalty;
            }
        }

//        // print results
//        if (feasible == 1) {
//            // System.out.println("Solution: " + Arrays.toString(solution) + " is feasible, cost: " + sCost);
//        } else {
//            // System.out.println("Solution: " + Arrays.toString(solution) + " is infeasible, cost: " + sCost);
//        }
        return sCost;
    }

    private void generateRandom() {
        // randomly generate & check 100
        for (int i = 0; i < 10000000; i++) {
            int[] solution = new int[n];

            // generate solution
            for (int j = 0; j < n; j++) {
                solution[j] = ThreadLocalRandom.current().nextInt(1, 6);
            }

            this.processSolution(solution);
        }
    }

    private int[] variationOperator(int[] solution){
        // pick random, swap
        int[] newSolution = solution.clone();
        int switch1 = ThreadLocalRandom.current().nextInt( 15);
        int switch2 = ThreadLocalRandom.current().nextInt(15);
        while(switch1 == switch2){
            switch1 = ThreadLocalRandom.current().nextInt( 15);
        }

        newSolution[switch2] = solution[switch1];
        newSolution[switch1] = solution[switch2];

        return newSolution;

    }

    private int[] thresholdSearch(int[] initial) {
        int threshold = 5;
        int[] solution = initial;
        int[] bestSolution = initial;
        int solutionCost = this.processSolution(initial);
        int bestSolutionCost = this.processSolution(initial);
        int count = 0;

        while(bestSolutionCost > 269) {
            count++;
            System.out.println("Solution:" + Arrays.toString(solution) + ", cost: " + solutionCost);
            int[] perturbed = variationOperator(solution);
            int cost = processSolution(perturbed);

            if ((cost - threshold) < solutionCost) {
                solution = perturbed;
                solutionCost = processSolution(perturbed);
            }

            if (cost < bestSolutionCost) {
                bestSolution = perturbed;
                bestSolutionCost = processSolution(perturbed);
            }
        }
        System.out.println(Arrays.toString(bestSolution));
        System.out.println(count);
        return bestSolution;
    }
}
