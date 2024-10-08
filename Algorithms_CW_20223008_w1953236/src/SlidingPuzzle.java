/*
Student Name: P. A. Yasindu Anushka Gunasekara
IIT ID: 20223008
UOW ID: w1953236
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SlidingPuzzle {
    public static void main(String[] args) {
        String filePath = "samples/benchmark_series/";
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = sc.nextLine();

        System.out.println("File Name: " + fileName);
        System.out.println();
        System.out.println("Shortest Path for this file:");
        System.out.println();

        SlidingPuzzle slidingPuzzle = new SlidingPuzzle();
        long start = System.currentTimeMillis();

        String[][] puzzleText = slidingPuzzle.readFile(filePath + fileName);
        int[] startPointIndex = slidingPuzzle.getStartPointIndex(puzzleText, puzzleText[0].length, puzzleText.length);
        int[] endPointIndex = slidingPuzzle.getEndPointIndex(puzzleText, puzzleText[0].length, puzzleText.length);

        SlidingPuzzle.PuzzleInfo[][] puzzle = slidingPuzzle.createGame(puzzleText, puzzleText[0].length, puzzleText.length);

        SlidingPuzzle.PuzzleInfo startPoint = puzzle[startPointIndex[0]][startPointIndex[1]];
        SlidingPuzzle.PuzzleInfo endPoint = puzzle[endPointIndex[0]][endPointIndex[1]];

        slidingPuzzle.dijkstra(startPoint, endPoint);
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        String timeTake = String.format("%20s %20s \r\n", fileName, elapsedTime);
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Time Taken for file(ms): ");
        System.out.print(timeTake);

    }
    //create inner class for store game data
    static class PuzzleInfo {
        int row_pos;
        int col_pos;
        int weight;
        PuzzleInfo left;
        PuzzleInfo right;
        PuzzleInfo up;
        PuzzleInfo down;

        public PuzzleInfo(int row_pos, int col_pos) {
            this.row_pos = row_pos;
            this.col_pos = col_pos;
        }
    }

    //counting the number of line in the string
    private int numberOfLines(String name) throws FileNotFoundException {
        File file = new File(name);
        Scanner reader = new Scanner(file);
        int rowCount = 0;


        while (reader.hasNextLine()) {
            reader.nextLine();
            rowCount++;
        }
        return rowCount;
    }

    // Reading the string design from the txt file and create a 2D array
    public String[][] readFile(String fileName) {

        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);

            int lineCount = numberOfLines(fileName);

            String[][] puzzleText = new String[lineCount][lineCount];
            int lineNum = 0;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] lineData = line.split("");
                puzzleText[lineNum] = lineData;
                lineNum++;

            }

            return puzzleText;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    // Find the starting position of the game
    public int[] getStartPointIndex(String[][] puzzleText, int numOfRows, int numOfCols) {
        int[] index = new int[2];
        for (int row = 0; row < numOfRows; row++) {
            for (int column = 0; column < numOfCols; column++) {
                if (puzzleText[row][column].equals("S")) {
                    puzzleText[row][column] = ".";
                    index[0] = row;
                    index[1] = column;
                    return index;
                }
            }
        }
        return null;
    }

    // Find the finish position of the game
    public int[] getEndPointIndex(String[][] puzzleText, int numOfRows, int numOfCols) {
        int[] index = new int[2];
        for (int row = 0; row < numOfRows; row++) {
            for (int column = 0; column < numOfCols; column++) {
                if (puzzleText[row][column].equals("F")) {
                    puzzleText[row][column] = ".";
                    index[0] = row;
                    index[1] = column;
                    return index;
                }
            }
        }
        return null;
    }

    // Create game using String array
    public PuzzleInfo[][] createGame(String[][] puzzleText, int numOfRows, int numOfCols) {
        PuzzleInfo[][] puzzle = new PuzzleInfo[numOfRows][numOfCols];

        for (int row = 0; row < numOfRows; row++) {
            for (int column = 0; column < numOfCols; column++) {
                if (puzzleText[row][column].equals(".")) {
                    PuzzleInfo cur_point = new PuzzleInfo(row, column);
                    if (column - 1 >= 0 && puzzleText[row][column - 1].equals(".")) {
                        cur_point.left = puzzle[row][column - 1];
                        if (puzzle[row][column - 1] != null) {
                            puzzle[row][column - 1].right = cur_point;
                        }
                    }

                    if (column + 1 < numOfCols && puzzleText[row][column + 1].equals(".")) {
                        cur_point.right = puzzle[row][column + 1];
                        if (puzzle[row][column + 1] != null) {
                            puzzle[row][column + 1].left = cur_point;
                        }
                    }

                    if (row - 1 >= 0 && puzzleText[row - 1][column].equals(".")) {
                        cur_point.up = puzzle[row - 1][column];
                        if (puzzle[row - 1][column] != null) {
                            puzzle[row - 1][column].down = cur_point;
                        }
                    }

                    if (row + 1 < numOfRows && puzzleText[row + 1][column].equals(".")) {
                        cur_point.down = puzzle[row + 1][column];
                        if (puzzle[row + 1][column] != null) {
                            puzzle[row + 1][column].up = cur_point;
                        }
                    }
                    puzzle[row][column] = cur_point;
                }
            }
        }
        return puzzle;
    }

    // Do the sliding using direction
    private PuzzleInfo slide(PuzzleInfo source_point, PuzzleInfo end_point, String direction) {

        source_point.weight = 0;
        PuzzleInfo cur_point = source_point;

        if (direction.equals("left")) {
            while (cur_point.left != null && cur_point != end_point) {
                cur_point = cur_point.left;
                source_point.weight++;
            }
        }
        if (direction.equals("right")) {
            while (cur_point.right != null && cur_point != end_point) {
                cur_point = cur_point.right;
                source_point.weight++;
            }
        }

        if (direction.equals("up")) {
            while (cur_point.up != null && cur_point != end_point) {
                cur_point = cur_point.up;
                source_point.weight++;
            }
        }
        if (direction.equals("down")) {
            while (cur_point.down != null && cur_point != end_point) {
                cur_point = cur_point.down;
                source_point.weight++;
            }
        }
        if (source_point.weight != 0) {
            return cur_point;
        } else {
            return null;
        }
    }

    // Find the shortest path
    private void findShortestPath(PuzzleInfo start, PuzzleInfo current, HashMap<PuzzleInfo, PuzzleInfo> previous) {
        ArrayList<PuzzleInfo> path = new ArrayList<>();
        path.add(current);
        while (current != start) {
            PuzzleInfo temp = previous.get(current);
            path.add(temp);
            current = temp;
        }
        int i = path.size() - 2;
        System.out.println("1. Start at (" + (start.col_pos + 1) + "," + (start.row_pos + 1) + ")");
        while (i >= 0) {
            if (path.get(i).col_pos == path.get(i + 1).col_pos) {
                if (path.get(i).row_pos < path.get(i + 1).row_pos) {
                    System.out.println((path.size() - i) + ". Move up to (" + (path.get(i).col_pos + 1) + "," + (path.get(i).row_pos + 1) + ")");
                } else {
                    System.out.println((path.size() - i) + ". Move down to (" + (path.get(i).col_pos + 1) + "," + (path.get(i).row_pos + 1) + ")");
                }
            } else {
                if (path.get(i).col_pos < path.get(i + 1).col_pos) {
                    System.out.println((path.size() - i) + ". Move left to (" + (path.get(i).col_pos + 1) + "," + (path.get(i).row_pos + 1) + ")");
                } else {
                    System.out.println((path.size() - i) + ". Move right to (" + (path.get(i).col_pos + 1) + "," + (path.get(i).row_pos + 1) + ")");
                }
            }
            i--;
        }
        System.out.println((path.size() - i) + ". Done!");
    }

    public void dijkstra(PuzzleInfo start_point, PuzzleInfo end_point) {
        HashMap<PuzzleInfo, Integer> distance = new HashMap<>();
        distance.put(start_point, 0);

        String[] directions = {"left", "right", "down", "up"};

        HashMap<PuzzleInfo, PuzzleInfo> previous = new HashMap<>();

        ArrayList<PuzzleInfo> q = new ArrayList<>();
        q.add(start_point);

        while (q.size() > 0) {
            PuzzleInfo current = q.get(0);
            q.remove(0);

            for (int i = 0; i < 4; i++) {
                PuzzleInfo temp = slide(current, end_point, directions[i]);
                if (temp != null && !distance.containsKey(temp)) {
                    distance.put(temp, distance.get(current) + current.weight);
                    previous.put(temp, current);
                    q.add(temp);
                } else if (temp != null && distance.get(current) + current.weight <= distance.get(temp)) {
                    distance.put(temp, distance.get(current) + current.weight);
                    previous.put(temp, current);
                }
            }

        }
        if (distance.containsKey(end_point)) {
            findShortestPath(start_point, end_point, previous);
        }
    }

}