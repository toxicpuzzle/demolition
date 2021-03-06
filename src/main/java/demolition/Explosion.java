package demolition;

import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

/**Spawns a set of explosiontiles from the location that the bomb was placed, 
 * and ensures explosions only break 1 breakable wall max and do not penetrate solidwalls*/
public class Explosion{
    private List<ExplosionTile> left;
    private List<ExplosionTile> right;
    private List<ExplosionTile> up;
    private List<ExplosionTile> down;
    private Level currentLevel;
    private ExplosionTile centre;

    // New explosion will be made by the bomb class.
    // NOTE: Explosion is a collection of explosiontiles
    // NB: Pimage is dummy var to avoid nullpointer right now
    /**@param x the x coord for the centre of the explosion (bomb x)
     * @param y the y coord for the centre of the explosion (bomb y)
     * @param level the level in which the explosion is to be placed
     * @param app the app instance that runs the entire game
     */
    public Explosion(int x, int y, Level level, PApplet app) {
        this.centre = (ExplosionTile) SpriteFactory.makeExplosionCentre(x, y, app);
        this.left = new ArrayList<ExplosionTile>();
        this.right = new ArrayList<ExplosionTile>();
        this.up = new ArrayList<ExplosionTile>();
        this.down = new ArrayList<ExplosionTile>();
        this.currentLevel = level;
        centre.setCurrentLevel(this.currentLevel);

        // Add the current level to all explosion tiles that were made
        makeInDirection(Direction.LEFT, app);
        makeInDirection(Direction.RIGHT, app);
        makeInDirection(Direction.UP, app);
        makeInDirection(Direction.DOWN, app);

    }

    // Helper function for constructor
    /**Makes the explosion tiles in the given direction
     * @param direction the direction to make explosiontiles in
     * @param app the app instance that runs the game
     */
    private void makeInDirection(Direction direction, PApplet app){
        int xCurrent = this.centre.getX();
        int yCurrent = this.centre.getY();

        for (int i = 0; i < 2; i++) {
            
            ExplosionTile currentTile = SpriteFactory.makeExplosionHorizontal(xCurrent, yCurrent, app);
            
            //Add tile depending on direction
            switch(direction){
                case UP: 
                    yCurrent -= 32;
                    currentTile = SpriteFactory.makeExplosionVertical(xCurrent, yCurrent, app);
                    this.up.add(currentTile);
                    break;
                case DOWN: 
                    yCurrent += 32;
                    currentTile = SpriteFactory.makeExplosionVertical(xCurrent, yCurrent, app);
                    this.down.add(currentTile);
                    break;
                case LEFT: 
                    xCurrent -= 32;
                    currentTile = SpriteFactory.makeExplosionHorizontal(xCurrent, yCurrent, app);
                    this.left.add(currentTile);
                    break;
                case RIGHT: 
                    xCurrent += 32;
                    currentTile = SpriteFactory.makeExplosionHorizontal(xCurrent, yCurrent, app);
                    this.right.add(currentTile);
                    break;
            }

            currentTile.setCurrentLevel(currentLevel);

            // Remove the last two if this tile collides with any solid object, and add a endie tile (replaces previous tile)
            if (currentTile.collideWithSolid()){
                currentTile.remove();
                break;
            } else if (currentTile.collideWithBroken()) {
                break;
            }
        } 
    }

    /**@return all the explosion tiles in the explosion object that have not been removed preliminarily from hitting a block*/
    public List<GameObject> getExplosionTiles(){
        List<GameObject> gameObjects = new ArrayList<GameObject>();
        gameObjects.addAll(this.left);
        gameObjects.addAll(this.right);
        gameObjects.addAll(this.up);
        gameObjects.addAll(this.down);
        gameObjects.add(this.centre);
        List<GameObject> returnList = new ArrayList<GameObject>();
        for (GameObject object: gameObjects){
            if (!object.isRemoved){
                returnList.add(object);
            }
        }
        return returnList;
    }

    /**Adds all of the explosiontiles in the explosion object to the level the explosion object was created in */
    public void addAllExpTiles(){
        List<GameObject> tiles = this.getExplosionTiles();
        for (GameObject object: tiles){
            currentLevel.addObject(object);
        }
    }
    
}
