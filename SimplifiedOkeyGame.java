import java.util.Random;

public class SimplifiedOkeyGame {

    Player[] players;
    Tile[] tiles;
    int tileCount;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;
    int topTileIndex;

    public static final int TILE_RELEVANCE_CONSTANT = 2; 
    //Tiles are related if the difference is equal or less than this constant

    public SimplifiedOkeyGame() {
        players = new Player[4];
        createTiles();
        shuffleTiles();
        distributeTilesToPlayers();
    }

    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // four copies of each value, no jokers
        for (int i = 1; i <= 26; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[currentTile++] = new Tile(i);
            }
        }

        tileCount = 104;
    }

    /*
     * TODO: distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles, this method assumes the tiles are already shuffled
     */
    public void distributeTilesToPlayers() {
        for(int i = 0; i < 14; i++){
            for(int j = 0; j < 4; j++){
                players[j].addTile(tiles[(j * 14) + i]);
            }
        }
        players[0].addTile(tiles[56]);
        tileCount -= 57;

        topTileIndex = 57;
    }

    /*
     * TODO: get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {   
        players[currentPlayerIndex].addTile(lastDiscardedTile);
        return lastDiscardedTile.toString();
    }

    /*
     * TODO: get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * and it will be given to the current player
     * returns the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        Tile topTile = tiles[topTileIndex];
        players[currentPlayerIndex].addTile(topTile);
        topTileIndex++;
        return topTile.toString();
    }

    /*
     * TODO: should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        Random rand = new Random();
        Tile[] tilesOrdered = new Tile[104];

        // Copying the values of tiles into tilesOrdered array
        for(int i = 0; i < 104; i++){
            tilesOrdered[i] = tiles[i];
        }

        // Checking for the random index of the tilesOrdered array is 0, if it's 0 it means that random index has already been used
        boolean keepShuffling = true;
        int index = 0;
        while(keepShuffling){
            int randIndex = rand.nextInt(104);
            if(tilesOrdered[randIndex].value != 0){
                tiles[index] = tilesOrdered[randIndex];
                tilesOrdered[randIndex].value = 0;
                index++;
            }
            if(index == 104){
                keepShuffling = false;
            }
        }
    }

    /*
     * TODO: check if game still continues, should return true if current player
     * finished the game. use checkWinning method of the player class to determine
     */
    public boolean didGameFinish() {
        return players[currentPlayerIndex].checkWinning();
    }

    /* TODO: finds the player who has the highest number for the longest chain
     * if multiple players have the same length may return multiple players
     */
    public Player[] getPlayerWithHighestLongestChain() {
        int[] playersLongestChain = new int[4];
        int longestChain = 0;
        for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
            int currentPlayerLongestChain = players[playerIndex].findLongestChain();
            playersLongestChain[playerIndex] = currentPlayerLongestChain;

            if (currentPlayerLongestChain > longestChain) {
                longestChain = currentPlayerLongestChain;
            }
        }

        int noOfWinners = 0;
        for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
            if (longestChain ==  playersLongestChain[playerIndex]) {
                noOfWinners++;
            }
        }

        Player[] winners = new Player[noOfWinners];
        int winnersIndex = 0;
        for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
            if (longestChain ==  playersLongestChain[playerIndex]) {
                winners[winnersIndex] = players[playerIndex];
                winnersIndex++;
            }
        }

        return winners;
    }
    
    /*
     * checks if there are more tiles on the stack to continue the game
     */
    public boolean hasMoreTileInStack() {
        return tileCount != 0;
    }

    /*
     * TODO: pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * you should check if getting the discarded tile is useful for the computer
     * by checking if it increases the longest chain length, if not get the top tile
     */
    public void pickTileForComputer() {
        boolean sameTileExists = false;
        boolean tileIsRelatedToLongestChain = false;
        //true if the discarded tile can be added to the longest chain or
        //near the chain with 1 point of difference

        boolean tileIsUseful = false;
        Player currentPlayer = players[currentPlayerIndex];
        int chain = currentPlayer.findLongestChain();
        int chainEndingIndex = currentPlayer.getLongestChainEndingIndex();
        int chainBeginningIndex = chainEndingIndex - chain + 1;
        Tile[] tileSet = currentPlayer.getTiles();
        
        for (int tile = 0; tile < 14; tile++) {
            if (tileSet[tile].getValue() == lastDiscardedTile.getValue()) {
                sameTileExists = true;
            }

            int tileRelevanceBeginning; //if less than relevance constant, tile is related to the chain
            int tileRelevanceEnding;

            tileRelevanceBeginning = tileSet[chainBeginningIndex].getValue() - lastDiscardedTile.getValue();
            tileRelevanceEnding = tileSet[chainEndingIndex].getValue() - lastDiscardedTile.getValue();

            if (Math.abs(tileRelevanceBeginning) <= TILE_RELEVANCE_CONSTANT ||
                Math.abs(tileRelevanceEnding) <= TILE_RELEVANCE_CONSTANT) {
                    tileIsRelatedToLongestChain = true;
            }
        }

        if (!sameTileExists && tileIsRelatedToLongestChain) {
            tileIsUseful = true;
        }

        if (tileIsUseful) {
            getLastDiscardedTile();
        }
        else {
            getTopTile();
        }
    }

    /*
     * TODO: Current computer player will discard the least useful tile.
     * you may choose based on how useful each tile is
     */
    public void discardTileForComputer() {
        //If there is a repeating tile, it would be discarded.
        //Else, furthest tile to the longest chain would be discarded.
        int furthestTileToChainIndex = -1;
        int repeatingTileIndex = -1;

        Player currentPlayer = players[currentPlayerIndex];
        int chain = currentPlayer.findLongestChain();
        int chainEndingIndex = currentPlayer.getLongestChainEndingIndex();
        int chainBeginningIndex = chainEndingIndex - chain + 1;
        Tile[] tileSet = currentPlayer.getTiles();

        for (int tile = 0; tile < 14; tile++) {
            if (tile != 0) {
                if (tileSet[tile].getValue() == tileSet[tile - 1].getValue()) {
                    repeatingTileIndex = tile;
                }
            }
        }

        int maxDifference1 = tileSet[chainBeginningIndex].getValue() - tileSet[0].getValue();
        int maxDifference2 = tileSet[13].getValue() - tileSet[chainEndingIndex].getValue();

        if (maxDifference1 > maxDifference2) {
            furthestTileToChainIndex = 0;
        }
        else {
            furthestTileToChainIndex = 13;
        }

        if (repeatingTileIndex != -1) {
            discardTile(repeatingTileIndex);
        }
        else {
            discardTile(furthestTileToChainIndex);
        }
    }

    /*
     * TODO: discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        lastDiscardedTile = players[currentPlayerIndex].getAndRemoveTile(tileIndex);
    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }

}
