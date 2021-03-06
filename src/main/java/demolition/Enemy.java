package demolition;

import java.util.HashMap;
import java.util.List;
import processing.core.PApplet;
import java.util.ArrayList;

/**Abstract enemy object that depends on movement strategies 
 * concretely implemented by EnemyYellow and EnemyRed */
public abstract class Enemy extends MovingObject implements Movable {

    protected static int yHeadOffset = 16;
    protected int yStarting;
    protected int xStarting;
    protected int walkTimer;
    protected List<Direction> directionsTried;

     /**Default constructor for enemyObjects, creates animations
     * @param x x coord to create the object
     * @param y y coord to create the object
     * @param animations animations corresponding to each direction the directional object is moving
    */
    public Enemy(int x, int y, HashMap<Direction, Animation> animations){
        super(x, y, animations);
        this.yStarting = y;
        this.xStarting = x;
        this.width = 32; // WARNING: Hard code
        this.height = 32;
        this.directionsTried = new ArrayList<Direction>();
        // Need to find a way to load all of the animations in
    }

    /**Draws the enemy's current frame on screen
     * @param app the app object associated with the enemy
     */
    @Override
    public void draw(PApplet app) {
        app.image(currentFrame, xPos, yPos-yHeadOffset);
    }

    /**Moves the enemy up */
    @Override
    public void moveUp() {
        this.yPos -= 32;
    }

    /**Moves the enemy down */
    @Override
    public void moveDown() {
        this.yPos += 32;
    }
    
    /**Moves the enemy right */
    @Override
    public void moveRight() {
        this.xPos += 32;
    }

    /**Moves the enemy left */
    @Override
    public void moveLeft() {
        this.xPos -= 32;
    }

    /**Automatically walks the enemy in the appropriate direction/changes its coords and 
     * updates its direction when it hits a wall based on the specific enemy's concrete 
     * implementation of the abstract movementstrategy method */
    protected void walk(){
        // keep trying random direction until clear path opens up and animation updates
        int oldX = this.xPos;
        int oldY = this.yPos;
        Direction oldDirection = this.direction;

        switch(direction){
            case DOWN: 
                moveDown();
                break;
            case UP: 
                moveUp();
                break;
            case LEFT: 
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break; 
        }

        if (collideWithSolid()){

            // Add the current direction tried to the failed list
            if (!directionsTried.contains(oldDirection)){ directionsTried.add(direction);} //Won't need .equals() for enums since ther is only one instance
            resetPosition(oldX, oldY, oldDirection);
            
            if (directionsTried.size() >= 4){
                this.direction = this.getCurrentAnimation().getDirection();
                justChangedDirection = false;
                directionsTried.clear();
                return;
            }

            // Ensures the new direction is one that has not yet tried
            Direction newDirection = getDirectionStrategy();
            while (directionsTried.contains(newDirection)){
                newDirection = getDirectionStrategy();
            }

            // Set the object's direction to the new direction chosen and try walking in that direction;
            this.direction = newDirection; //NB: Direction attribute will still change but animation is 
            justChangedDirection = true;
            walk();
        } else {
            directionsTried.clear();
            updateCurrentAnimation();
            return;
        }
    }

    /**@return the direction for the enemy to move in when it is facing a solid object */
    public abstract Direction getDirectionStrategy();

    /**Updates the state of the enemy based on if has touched an explosion or if it has changed direction/walked */
    @Override
    public void tick() {
        walkTimer++;
        
        if (collideWithExplosion()){
            this.isRemoved = true;
        }

        float secondsBetweenFrames = (float)currentAnimation.getFrameDuration()/1000;
        if (this.walkTimer >= secondsBetweenFrames*App.FPS*4){
            walk();
            if (justChangedDirection){
                this.currentFrame = this.currentAnimation.getFrameAtIndex(0); 
            }
            // ERROR: Moves before updating frame -> Fixed
            walkTimer = 0;
        }

        super.tick();
    }
    
}
