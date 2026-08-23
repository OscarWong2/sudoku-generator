import java.util.Scanner;

public class SudokuMain
{
    public static void print(String msg)
    {
        System.out.print(msg);
    }

    public static void main(String[] args)
    {
        Sudoku puzzle;
        int tries = 3;

        Scanner keyboard = new Scanner(System.in);
        String input;

        print("Sudoku\n");
        print("Enter a difficulty between 1 - 4: ");
        input = keyboard.nextLine();
        while (!input.equals("1")
                && !input.equals("2")
                && !input.equals("3")
                && !input.equals("4"))
        {
            print("Invalid input. Please enter a number between 1 - 4: ");
        }
        print("\n");

        puzzle = new Sudoku(Integer.parseInt(input));
        puzzle.genGrid();

        puzzle.printGrid(true, null);
        print("\n");
        while (!puzzle.checkHiddenPosRemain() && tries > 0)
        {
            int x = -1, y = -1, num = 0;

            print("Tries: " + tries + "\n");

            print("Enter a coordinate in \"x,y\" format: ");
            while (x == -1 && y == -1)
            {
                input = keyboard.nextLine();
                if (input.length() != 3 || input.charAt(1) != ','
                    || input.charAt(0) < 49 || input.charAt(0) > 57
                    || input.charAt(2) < 49 || input.charAt(2) > 57)
                {
                    print("Invalid input. Please enter a coordinate in \"x,y\" format: ");
                    continue;
                }
                x = input.charAt(0) - '0' - 1;
                y = input.charAt(2) - '0' - 1;
                if (!puzzle.findHiddenPos(x, y))
                {
                    x = y = -1;
                    print("Invalid input. Please enter a coordinate in \"x,y\" format: ");
                }
            }

            print("Enter a number to place in the entered coordinate from 1 - 9: ");
            while (num == 0)
            {
                input = keyboard.nextLine();
                if (input.length() != 1
                    || input.charAt(0) < 49 || input.charAt(0) > 57)
                {
                    print("Invalid input. Please enter a number between 1 - 9: ");
                    continue;
                }
                num = input.charAt(0) - '0';
            }

            print("\n");

            if (puzzle.retrievePos(x, y) == num)
            {
                puzzle.removeHiddenPos(x, y);
                puzzle.printGrid(true, new Pos(x, y));
                print("\n\u001B[33mCorrect!\u001B[0m\n\n");
            }
            else
            {
                puzzle.printGrid(true, null);
                print("\n");
                tries--;
            }
        }

        if (tries == 0)
            print("Game over, no tries left.\n");
        else
            print("Puzzle solved!\n");
    }
}