import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * This class is given a game.log file, it then parses it to obtain two files:
 * a file with all of my moves and a file with all of the opponent's moves.
 * For this the program has to be called with three inputs:
 * 	- The filename of the game log file to parse.
 * 	- what your player number is
 * 	- game name
 * Outputs:
 * 	<game_name>_mymoves.txt
 * 	<game_name>_oppmoves.txt
 */
public class GameLogParser {
	Scanner sc;
	String outputname;
	int myturn;

	public GameLogParser(String gamelogname, int turn, String gamename) {
		myturn = turn;
		outputname = gamename;
		try {
			sc = new Scanner(new File(gamelogname));
		} catch(FileNotFoundException e) {
			System.out.println(e.toString());
		}
	}

	public void parse() {
		ArrayList<String> stringMoves = new ArrayList<String>();
		String line;
		boolean ignoreInitialLines = true;
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (ignoreInitialLines && line.length() != 0 && line.charAt(0) == '#') {
				ignoreInitialLines = false;
			}
			if (line.length() == 0 || line.charAt(0) == '#' || ignoreInitialLines) {
				continue;
			}
			stringMoves.add(line);
		}
		sc.close();

		String file1 = "tournamentgames/" + outputname + "_mymoves.txt";
		String file2 = "tournamentgames/" + outputname + "_oppmoves.txt";

		try {
			BufferedWriter outMyMoves = new BufferedWriter(new FileWriter(file1));
			outMyMoves.write(getAllMoveStrings(myturn, stringMoves));
			outMyMoves.close();

			BufferedWriter outOppMoves = new BufferedWriter(new FileWriter(file2));
			outOppMoves.write(getAllMoveStrings(3-myturn, stringMoves));
			outOppMoves.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		System.out.println("Wrote your moves to " + file1);
		System.out.println("Wrote opponent's moves to " + file2);
	}

	private String getAllMoveStrings(int turn, ArrayList<String> stringMoves) {
		int i = 1;
		StringBuilder sb = new StringBuilder("");
		for (String s : stringMoves) {
			if (i == turn) {
				sb.append(s);
				sb.append('\n');
			}
			i = 3-i;
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		int myturn = 0;
		if (args.length < 3) {
			System.out.println("Too few arguments. Arguments must be <game log filename>, <your player number>, <game name>");
			System.exit(0);
		} else if (args.length > 3) {
			System.out.println("Too many arguments. Arguments must be <game log filename> <your player number> <game name>");
			System.exit(0);
		} else {
			myturn = Integer.parseInt(args[1]);
			if (myturn < 1 || myturn > 2) {
				System.out.println("Invalid player number. Can only be either 1 or 2.");
				System.exit(0);
			}
		}

		GameLogParser gameParser = new GameLogParser(args[0], myturn, args[2]);
		gameParser.parse();
	}
}
