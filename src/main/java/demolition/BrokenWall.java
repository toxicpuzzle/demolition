package demolition;

import processing.core.PImage;

public class BrokenWall extends GameObject {

    public BrokenWall(int x, int y, PImage startingImage){ //TODO: Is it necssary to have this class if we the only thing it does is handles the sold setting? we should handle image loading here too.
        super(x, y, true, startingImage);
    } 

    @Override
    public void tick(int currentTime) {
        // TODO Auto-generated method stub
        
    }
}