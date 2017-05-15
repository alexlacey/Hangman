
/*
 * Hangman.java
 * 
 *   A program that allows the user to play Hangman, a simple word guessing game. In this game, the user 
 *   provides a list of words to the program. The program randomly selects one of the words to be guessed from 
 *   this list. The player then guesses letters in an attempt to figure out what the hidden word might be. 
 *   The number of guesses that the user takes are tracked and reported at the end of the game.
 *   
 * 
 * @author Alex Lacey
 * 
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Hangman {

	public static void main(String[] args) throws MalformedURLException, IOException {

		/* Declare and initialize the needed objects for the entire program */
		ArrayList<String> wordList = importWordList(); // load the list of the 1000 most common English words
		boolean keepPlaying = true;
		Scanner in = new Scanner(System.in);

		/* Loop through multiple games until the user is done playing */
		while (keepPlaying == true) {

			/* Declare and initialize the needed objects for each specific game */
			String solution = getRandomWord(wordList); // picks a word from wordList
			ArrayList<Character> charList = getCharactersFromWord(solution); // makes a list of the characters in the word
			String currentInfo = starWord(solution); // converts the word into stars, like a password
			ArrayList<String> previousGuessList = new ArrayList<String>();
			char charGuess = ' ';
			boolean unsolved = true;

			/* Loop through multiple iterations of guessing until the user discovers the correct answer */
			while (unsolved == true) {
				printCurrentState(charGuess, solution, currentInfo, previousGuessList); // prints the current information in the game
				charGuess = getCharacterGuess(in); // prompts and obtains a character guess from the user
				previousGuessList.add(Character.toString(charGuess)); // add the character to the list of guessed characters
				printNumOccurances(charGuess, solution); // print the number of times that the guessed character appears in the word
				if (checkInList(charGuess, charList) == true) { // if the character occurs in the solution
					currentInfo = modifyInfo(charGuess, solution, currentInfo); // update the known information about the word
					String wordGuess = getWordGuess(in, currentInfo); // prompts and obtains a word guess from the user
					unsolved = checkWordGuess(wordGuess, solution); // check whether the user's guess is correct
					printWordGuessResponse(unsolved); // informs the user of whether or not he or she guessed correctly
				}
			}

			/* Conclusion of the game */
			System.out.println("You achieved the correct answer in " + previousGuessList.size() + " guesses.");
			keepPlaying = askForRematch(in); // ask the user if he or she would like to play again

		}

		/* Goodbye message */
		System.out.println("\nThanks for playing!  Goodbye!");

	}

	/* Import a list the 1000 most common English words, created by David Norman */
	private static ArrayList<String> importWordList() throws MalformedURLException, IOException {
		URL url = new URL("https://gist.githubusercontent.com/deekayen/4148741/raw/01c6252ccc5b5fb307c1bb899c95989a8a284616/1-1000.txt");
		Scanner inFile = new Scanner(url.openStream());
		ArrayList<String> list = getList(inFile); // creates an ArrayList of the 1000 words in the file
		System.out.println("Read " + list.size() + " words from the file\n");
		return list;
	}

	/* Choose a random word from the list to be the solution */
	private static String getRandomWord(ArrayList<String> inList) {
		int randomNumber = (int) (Math.random() * inList.size());
		String randomWord = inList.get(randomNumber);
		return randomWord;
	}

	/* Create an ArrayList containing all of the characters in the solution word */
	private static ArrayList<Character> getCharactersFromWord(String word) {
		ArrayList<Character> characterList = new ArrayList<Character>();
		for (int i = 0; i < word.length(); i++) {
			characterList.add(word.charAt(i));
		}
		return characterList;
	}

	/* Convert the word to all stars, like a password */
	private static String starWord(String inWord) {
		String output = "";
		for (int i = 0; i < inWord.length(); i++) {
			output += "*";
		}
		return output;
	}

	/* Inform the user of the current state of the game */
	private static void printCurrentState(char charGuess, String solution, String currentInfo, ArrayList<String> previousGuessList) {
		System.out.println();
		System.out.println("The word to guess is: " + modifyInfo(charGuess, solution, currentInfo)); // present the user with what they know
																										// about the word so far
		System.out.print("Previous characters guessed: ");
		printList(previousGuessList); // prints previously-guessed characters
		System.out.println();
	}

	/* Allow the user to guess a character that might be in the solution */
	private static char getCharacterGuess(Scanner in) {
		while (true) {
			System.out.print("Enter a character to guess: ");
			String input = in.next();
			input = input.toUpperCase();
			if (input.length() == 1) {
				return input.charAt(0); // serves as a break statement
			} else {
				System.out.println("Error! Please enter a single character.");
			}
		}
	}

	/* Print the number of times that a given character appears in the solution word */
	private static void printNumOccurances(char charGuess, String solution) {
		int count = 0;
		for (int i = 0; i < solution.length(); i++) {
			if (solution.charAt(i) == charGuess) {
				count++;
			}
		}
		System.out.print("The character occurs " + count + " times.");
	}

	/* Check whether or not a character is present in the ArrayList (and thus the solution word) */
	private static boolean checkInList(char inChar, ArrayList<Character> inList) {
		for (int i = 0; i < inList.size(); i++) {
			if (inList.get(i).equals(inChar)) {
				return true;
			}
		}
		return false;
	}

	/* Add to the currently available information about the solution word based on the user's character guess */
	private static String modifyInfo(char inChar, String word, String currentInfo) {
		String output = "";
		for (int i = 0; i < currentInfo.length(); i++) {
			if (currentInfo.charAt(i) == '*') {
				if (word.charAt(i) == inChar) {
					output += word.charAt(i);
				} else {
					output += '*';
				}
			} else {
				output += word.charAt(i);
			}
		}
		return output;
	}

	/* Allow the user to guess the solution word */
	private static String getWordGuess(Scanner in, String currentInfo) {
		System.out.println("The word to guess is: " + currentInfo);
		System.out.print("Enter your guess: ");
		String wordGuess = in.next();
		return wordGuess;
	}

	/* Check whether the user's word guess is correct */
	private static boolean checkWordGuess(String wordGuess, String solution) {
		boolean isCorrect;
		if (wordGuess.equals(solution)) {
			isCorrect = true;
		} else {
			isCorrect = false;
		}
		boolean unSolved = !(isCorrect);
		return unSolved;
	}

	/* Inform the user about whether or not their word guess was correct */
	private static void printWordGuessResponse(boolean unsolved) {
		boolean isCorrect = !(unsolved);
		if (isCorrect == true) {
			System.out.println("That is correct!");
		} else {
			System.out.println("That is not correct.");
		}
	}

	/* Ask the user whether or not he or she would like to play another game */
	private static boolean askForRematch(Scanner in) {
		System.out.print("Would you like a rematch [y/n]? ");
		String rematch = in.next().toUpperCase();
		boolean anotherGame;
		if (rematch.equals("Y")) {
			anotherGame = true;
		} else {
			anotherGame = false;
		}
		return anotherGame;
	}

	/* Read a list of words */
	private static ArrayList<String> getList(Scanner in) {
		ArrayList<String> wordList = new ArrayList<String>();
		while (in.hasNext()) {
			String word = in.nextLine().toUpperCase();
			wordList.add(word);
		}
		return wordList;
	}

	/* Print every item in a list */
	private static void printList(ArrayList<String> list) {
		if (list.size() != 0) {
			for (int i = 0; i < list.size() - 1; i++) {
				System.out.print(list.get(i));
				System.out.print(", ");
			}
			System.out.print(list.get(list.size() - 1));
		}
	}

}
