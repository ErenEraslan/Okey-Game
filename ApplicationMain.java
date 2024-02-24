import java.util.Scanner;

public class ApplicationMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SimplifiedOkeyGame game = new SimplifiedOkeyGame();

        System.out.print("Please enter your name: ");
        String playerName = sc.next();

        game.setPlayerName(0, playerName);
        game.setPlayerName(1, "John");
        game.setPlayerName(2, "Jane");
        game.setPlayerName(3, "Ted");

        game.createTiles();
        game.shuffleTiles();
        game.distributeTilesToPlayers();

        // developer mode is used for seeing the computer players hands, to be used for
        // debugging
        boolean devChosen = false;
        boolean devModeOn = false;
        while (!devChosen) {
            System.out.print("\nPlay in developer's mode with other player's tiles visible? (Y/N): ");
            char devMode = sc.next().charAt(0);
            if (devMode == 'Y' || devMode == 'y') {
                devModeOn = true;
                devChosen = true;
            } else if (devMode == 'N' || devMode == 'n') {
                devModeOn = false;
                devChosen = true;
            } else {
                System.out.println("Enter a valid choice!");
            }
        }
        boolean firstTurn = true;
        boolean gameContinues = true;
        int playerChoice = -1;
        while (gameContinues) {

            int currentPlayer = game.getCurrentPlayerIndex();
            System.out.println("\n" + game.getCurrentPlayerName() + "'s turn.");
            if (currentPlayer == 0) {
                // this is the human player's turn
                game.displayRemainingTiles();
                game.displayDiscardInformation();
                game.displayCurrentPlayersTiles();
                System.out.println("\nWhat will you do?");

                if (!firstTurn) {
                    // after the first turn, player may pick from tile stack or last player's
                    // discard
                    System.out.println("1. Pick From Tiles");
                    System.out.println("2. Pick From Discard");
                } else if (firstTurn) {
                    // on first turn the starting player does not pick up new tile
                    System.out.println("1. Discard Tile");
                }

                boolean valid = false;
                // after the first turn we can pick up
                if (!firstTurn) {
                    while (!valid) {
                        System.out.print("Your choice: ");
                        if (sc.hasNextInt()) {
                            playerChoice = sc.nextInt();
                            if (playerChoice == 1) {
                                System.out.println("You picked up: " + game.getTopTile());
                                valid = true;
                                firstTurn = false;
                            } else if (playerChoice == 2) {
                                System.out.println("You picked up: " + game.getLastDiscardedTile());
                                valid = true;
                            } else {
                                System.out.println("Enter a valid choice!");
                                System.out.println("1. Pick From Tiles");
                                System.out.println("2. Pick From Discard");
                            }
                        } else {
                            System.out.println("Enter a valid choice!");
                            System.out.println("1. Pick From Tiles");
                            System.out.println("2. Pick From Discard");
                            sc.next();
                        }
                    }
                    // display the hand after picking up new tile
                    game.displayCurrentPlayersTiles();
                } else {
                    // after first turn it is no longer the first turn
                    firstTurn = false;
                }

                gameContinues = !game.didGameFinish() && game.hasMoreTileInStack();
                boolean discarded = false;
                if (gameContinues) {
                    // if game continues we need to discard a tile using the given
                    // index by the player
                    System.out.println("\nWhich tile you will discard?");
                    while (!discarded) {
                        System.out.print("Discard the tile in index: ");
                        if (sc.hasNextInt()) {
                            playerChoice = sc.nextInt();
                            // make sure the given index is correct, should be 0 <= index <= 14
                            if (playerChoice < 0 || playerChoice > 14) {
                                System.out.println("Invalid index. Please enter a number between 0 and 14.");
                            } else {
                                discarded = true;
                            }
                        } else {
                            System.out.println("Invalid index. Please enter a number between 0 and 14.");
                            sc.next();
                        }
                    }
                    game.discardTile(playerChoice);
                    game.displayCurrentPlayersTiles();
                    game.passTurnToNextPlayer();
                } else {

                    // the game ended with no more tiles in the stack
                    // determine the winner based on longest chain lengths of the players
                    // use getPlayerWithHighestLongestChain method of game for this task
                    Player[] winners = game.getPlayerWithHighestLongestChain();
                    Player[] allPlayers = game.players;
                    if(!game.hasMoreTileInStack()){
                        System.out.println("\nNo tiles left in the stack");
                        for (Player  player : allPlayers) {
                            player.displayLongestChain();
                        }
                        System.out.println("\n" + winners[0].getName() + " wins!");

                    }else if (winners.length == 1) {
                        System.out.println(winners[0].getName() + " wins! Longest Chain:");
                        winners[0].displayLongestChain();
                    } else {
                        System.out.println("It's a tie between the following players:");
                        for (Player winner : winners) {
                            System.out.println(winner.getName() + "'s longest chain:");
                            winner.displayLongestChain();
                        }
                    }

                }
            } else {
                // this is the computer player's turn

                // computer picks a tile from tile stack or other player's discard
                game.pickTileForComputer();

                gameContinues = !game.didGameFinish() && game.hasMoreTileInStack();

                if (gameContinues) {
                    // if game did not end computer should discard
                    game.displayDiscardInformation();
                    game.discardTileForComputer();
                    if (devModeOn) {
                        game.displayCurrentPlayersTiles();
                    }
                    game.passTurnToNextPlayer();
                } else {

                    // the game ended with no more tiles in the stack
                    // determine the winner based on longest chain lengths of the players
                    // use getPlayerWithHighestLongestChain method of game for this task
                    Player[] winners = game.getPlayerWithHighestLongestChain();
                    Player[] allPlayers = game.players;
                    if(!game.hasMoreTileInStack()){
                        System.out.println("\nNo tiles left in the stack");
                        for (Player  player : allPlayers) {
                            player.displayLongestChain();
                        }
                        System.out.println("\n" + winners[0].getName() + " wins!");

                    }else if (winners.length == 1) {
                        System.out.println(winners[0].getName() + " wins! Longest Chain:");
                        winners[0].displayLongestChain();
                    } else {
                        System.out.println("It's a tie between the following players:");
                        for (Player winner : winners) {
                            System.out.println(winner.getName() + "'s longest chain:");
                            winner.displayLongestChain();
                        }
                    }
                }
            }
        }

        sc.close();
    }
}