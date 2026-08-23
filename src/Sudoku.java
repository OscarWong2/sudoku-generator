import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sudoku
{
    private final int matrix[][];
    private final int diff;
    private final ArrayList<Pos> hiddenPos;

    public Sudoku(int d)
    {
        matrix = new int[9][9]; // All values initialized to 0
        diff = d;
        hiddenPos = new ArrayList<>();
    }

    // Retrieve number from position
    public int retrievePos(int x, int y)
    {
        return matrix[y][x];
    }
    // Generate matrix
    public void genGrid()
    {
        genThreeSubGrid();
        fillGrid(0, 0, getFixedNum());
        genHiddenPos();
    }
    // Print matrix
    public void printGrid(boolean hidePos, Pos highlightPos)
    {
        System.out.println("x 1 2 3 4 5 6 7 8 9\n");
        for (int i = 0; i < 9; i++)
        {
            System.out.print("  ");
            for (int j = 0; j < 9; j++)
            {
                if (matrix[i][j] == 0 || (hidePos && hiddenPos.contains(new Pos(j, i))))
                    System.out.print("-");
                else
                {
                    if (highlightPos != null
                        && highlightPos.x() == j
                        && highlightPos.y() == i)
                        System.out.printf("\u001B[33m%d\u001B[0m", matrix[i][j]);
                    else System.out.print(matrix[i][j]);
                }
                if ((j + 1) % 3 == 0 && j != 8)
                    System.out.print("|");
                else
                    System.out.print(" ");
            }
            System.out.println("  " + (i + 1));
            if ((i + 1) % 3 == 0 && i != 8)
                System.out.println("  -----+-----+-----");
        }
        System.out.println("                      y");
    }
    // Add to the list of hidden positions/numbers
    public void addHiddenPos(int x, int y)
    {
        if (!hiddenPos.contains(new Pos(x, y)))
            hiddenPos.add(new Pos(x, y));
    }
    // Delete from the list of hidden positions/numbers
    public void removeHiddenPos(int x, int y)
    {
        if (hiddenPos.contains(new Pos(x, y)))
            hiddenPos.remove(new Pos(x, y));
    }
    // Check if a hidden position exists
    public boolean findHiddenPos(int x, int y)
    {
        return hiddenPos.contains(new Pos(x, y));
    }
    // Check if any hidden positions/numbers remain
    public boolean checkHiddenPosRemain()
    {
        return hiddenPos.isEmpty();
    }

    // Helper functions
    // Find number in sub-matrix
    private Pos findInSubGrid(int x, int y, int findNum)
    {
        int xLower = (x - (x % 3));
        int xUpper = xLower + 2;
        int yLower = (y - (y % 3));
        int yUpper = yLower + 2;
        for (int i = yLower; i <= yUpper; i++)
        {
            for (int j = xLower; j <= xUpper; j++)
            {
                if (j == x && i == y) continue; // Don't check x and y itself
                if (matrix[i][j] == findNum) return new Pos(j, i);
            }
        }
        return null;
    }
    // Find number in row
    private Pos findInRow(int x, int y, int findNum)
    {
        for (int i = 0; i < 9; i++)
        {
            if (i == y) continue; // Don't check x and y itself
            if (matrix[i][x] == findNum) return new Pos(x, i);
        }
        return null;
    }
    // Find number in column
    private Pos findInColumn(int x, int y, int findNum)
    {
        for (int i = 0; i < 9; i++)
        {
            if (i == x) continue; // Don't check x and y itself
            if (matrix[y][i] == findNum) return new Pos(i, y);
        }
        return null;
    }
    // Get numbers already in matrix (treat as fixed numbers)
    private ArrayList<Pos> getFixedNum()
    {
        ArrayList<Pos> fixedNum = new ArrayList<>();
        for (int y = 0; y < 9; y++)
        {
            for (int x = 0; x < 9; x++)
                if (matrix[y][x] != 0) fixedNum.add(new Pos(x, y));
        }
        return fixedNum;
    }
    // Create and return array containing numbers 1 - 9 in either randomized or sorted order
    private ArrayList<Integer> genNumList(boolean randomize)
    {
        ArrayList<Integer> num = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toCollection(ArrayList::new));
        if (randomize) Collections.shuffle(num);
        return num;
    }
    // Generate numbers for upper-left, center, and lower-right matrices
    private void genThreeSubGrid()
    {
        int xBound = 0, yBound = 0;
        for (int i = 1; i <= 3; i++)
        {
            ArrayList<Integer> num = genNumList(true);
            for (int y = yBound; y <= yBound + 2; y++)
            {
                for (int x = xBound; x <= xBound + 2; x++)
                {
                    matrix[y][x] = num.getFirst();
                    num.removeFirst();
                }
            }
            yBound = xBound += 3;
        }
    }
    // Recursive DFS-like function to fill matrix with numbers
    private boolean fillGrid(int x, int y, ArrayList<Pos> fixedNum)
    {
        if (y == 9) return true;

        int xNext, yNext;
        if (x == 8)
        {
            xNext = 0;
            yNext = y + 1;
        }
        else
        {
            xNext = x + 1;
            yNext = y;
        }

        if (fixedNum.contains(new Pos(x, y)))
            return fillGrid(xNext, yNext, fixedNum);

        ArrayList<Integer> num = genNumList(false);
        for (int i : num)
        {
            if (findInRow(x, y, i) == null
                && findInColumn(x, y, i) == null
                && findInSubGrid(x, y, i) == null)
            {
                matrix[y][x] = i;
                if (fillGrid(xNext, yNext, fixedNum)) return true;
            }
        }
        matrix[y][x] = 0;
        return false;
    }
    // Generate positions/numbers to hide in matrix based on difficulty
    private void genHiddenPos()
    {
        int posToKeep;
        switch (diff)
        {
            case 1 -> posToKeep = 40;
            case 2 -> posToKeep = 30;
            case 3 -> posToKeep = 25;
            default -> posToKeep = 20;
        }
        for (int y = 0; y < 9; y++)
        {
            for (int x = 0; x < 9; x++)
                hiddenPos.add(new Pos(x, y));
        }
        Collections.shuffle(hiddenPos);
        for (int i = 1; i <= posToKeep; i++)
            hiddenPos.removeLast();
    }
}