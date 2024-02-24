public class Player {
    String playerName;
    Tile[] playerTiles;
    int numberOfTiles;
    int longestChainEndingIndex;
    int longestChainRepeats;

    public Player(String name) {
        setName(name);
        longestChainEndingIndex = 0;
        longestChainRepeats = 0;
        playerTiles = new Tile[15]; // there are at most 15 tiles a player owns at any time
        numberOfTiles = 0; // currently this player owns 0 tiles, will pick tiles at the beggining of the
                           // game
    }

    /*
     * TODO: checks this player's hand to determine if this player is winning
     * the player with a complete chain of 14 consecutive numbers wins the game
     * note that the player whose turn is now draws one extra tile to have 15 tiles
     * in hand,
     * and the extra tile does not disturb the longest chain and therefore the
     * winning condition
     * check the assigment text for more details on winning condition
     */
    public boolean checkWinning() {
        return this.findLongestChain() >= 14;
    }

    /*
     * TODO: used for finding the longest chain in this player hand
     * this method should iterate over playerTiles to find the longest chain
     * of consecutive numbers, used for checking the winning condition
     * and also for determining the winner if tile stack has no tiles
     */
    public int findLongestChain() {
        boolean chainStarted = false;
        int currentChainRepeatNumber = 0;
        int longestChainRepeatNumber = 0;
        int longestChain = 0;
        int currentLongestChain = 0;
        for (int i = 0; i < numberOfTiles - 1; i++) {
            if (playerTiles[i + 1].canFormChainWith(playerTiles[i])) {
                currentLongestChain++;
                if (!chainStarted) {
                    currentLongestChain++;
                    chainStarted = true;
                }
                
            } else if (playerTiles[i + 1].getValue() != playerTiles[i].getValue()) {
                if (currentLongestChain >= longestChain) {
                    longestChain = currentLongestChain;
                    longestChainEndingIndex = i;
                    chainStarted = false;
                }

                if (currentChainRepeatNumber > longestChainRepeats) {
                    longestChainRepeats = currentChainRepeatNumber;
                }
                currentLongestChain = 0;
                currentChainRepeatNumber = 0;
            } else {
                if (chainStarted) {
                    currentChainRepeatNumber++;
                }
            }
        }

        longestChainRepeats = longestChainRepeatNumber;
        return longestChain;
    }

    public void displayLongestChain() {
        System.out.println("\n" + playerName + "'s tile:");
        int maxLength = 1;
        int currentLength = 1;
        int startOfLongestChain = 0;
    
        for (int i = 0; i < numberOfTiles - 1; i++) {
            if (playerTiles[i].canFormChainWith(playerTiles[i + 1])) {
                currentLength++;
                if (currentLength > maxLength) {
                    maxLength = currentLength;
                    startOfLongestChain = i - currentLength + 2;
                }
            } else {
                currentLength = 1;
            }
        }
    
        for (int i = startOfLongestChain; i < startOfLongestChain + maxLength; i++) {
            System.out.print(playerTiles[i].toString() + " ");
        }
    }
    

    /*
     * TODO: removes and returns the tile in given index position
     */
    public Tile getAndRemoveTile(int index) {
        Tile temp = playerTiles[index];
        for (int i = index; i < numberOfTiles - 1; i++) {
            playerTiles[i] = playerTiles[i + 1];
        }
        numberOfTiles--;
        return temp;
    }

    /*
     * TODO: adds the given tile to this player's hand keeping the ascending order
     * this requires you to loop over the existing tiles to find the correct
     * position,
     * then shift the remaining tiles to the right by one
     */
    public void addTile(Tile t) {
        int position = 0;
        boolean found = false;
        int j = 0;
        while (!found && j < numberOfTiles) {
            if (playerTiles[j].getValue() > t.getValue()) {
                position = j;
                found = true;
            }
            j++;
        }
        if (!found) {
            position = numberOfTiles;
        }
        for (int i = numberOfTiles - 1; i >= position; i--) {
            playerTiles[i + 1] = playerTiles[i];
        }
        playerTiles[position] = t;
        numberOfTiles++;
    }

    /*
     * finds the index for a given tile in this player's hand
     */
    public int findPositionOfTile(Tile t) {
        int tilePosition = -1;
        for (int i = 0; i < numberOfTiles; i++) {
            if (playerTiles[i].matchingTiles(t)) {
                tilePosition = i;
            }
        }
        return tilePosition;
    }

    /*
     * displays the tiles of this player
     */
    public void displayTiles() {
        System.out.println(playerName + "'s Tiles:");
        for (int i = 0; i < numberOfTiles; i++) {
            System.out.print(playerTiles[i].toString() + " ");
        }
        System.out.println();
    }

    public Tile[] getTiles() {
        return playerTiles;
    }

    public void setName(String name) {
        playerName = name;
    }

    public String getName() {
        return playerName;
    }

    public int getLongestChainEndingIndex() {
        return longestChainEndingIndex;
    }

    public int getLongestChainRepeats() {
        return longestChainRepeats;
    }
}
