package demolition;

import processing.core.PImage;


public class Goal extends GameObject {
    public Goal(int x, int y, PImage startingImage){ //TODO: Is it necssary to have this class if we the only thing it does is handles the sold setting? we should handle image loading here too.
        super(x, y, false, startingImage);
    } 

    @Override
    public void tick(int currentTime) {
        // Do nothing since solid walls do not move
    }
}