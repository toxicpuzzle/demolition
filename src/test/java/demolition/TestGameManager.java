package demolition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
public class TestGameManager extends AppTester {
    //Test to next level
    private GameManager manager;

    @BeforeEach
    public void getManager(){
        List<Level> levels = Loader.loadAllFiles(app, "src/test/resources/config.json");
        this.manager = new GameManager(levels);
    }

    @Test
    public void notNull(){
        assertNotNull(this.manager);
    }

    // Test win and gameover conditions

    @Test
    // Check manager goes to gameover if the player runs out of time
    public void testGameOverOutOfTime(){
        Level level = manager.getCurrentLevel();
        for (int i = 0; i < 100*App.FPS; i++){
            level.tick();
            manager.tick();
        }
        assertTrue(manager.hasGameOver());
    }

    @Test
    // Check that the gamemanager is running at the correct pace
    public void testGameTimerCorrectTime(){
        Level level = manager.getCurrentLevel();
        for (int i = 0; i < 50*App.FPS; i++){
            level.tick();
            manager.tick();
        }
        assertEquals(50, level.getTimeLeft());
    }

    @Test
    // Check manager goes to gameover if the player runs out of lives
    public void testGameOverOutOfLives(){
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        player.setLives(1);
        // Get player to collide with enemy
        player.xPos = 4*32;
        player.yPos = 11*32;
        player.moveRight();
        manager.tick();
        assertEquals(0, player.getLives());
        assertEquals(true, manager.hasGameOver());
    }

    @Test
    // Test player restarts level if they touch an explosion
    public void testRestartAfterTouchingExplosion(){
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        player.moveRight();
        Explosion explosion = new Explosion(3*32, 3*32, level, app);
        explosion.addAllExpTiles();
        manager.tick();
        assertEquals(1*32, player.xPos);    
        assertEquals(3*32, player.yPos);
        assertEquals(2, player.getLives());
    }

    // Test going to next level

    // Test player goes to the next level if they step on the goal tile (spawn player on goal tile)
    @Test
    public void testStepGoalTile(){
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        assertEquals(0, manager.getCurrentLevelIndex());
        player.xPos = 12*32;
        player.yPos = 13*32;
        player.moveRight();
        manager.tick();
        assertEquals(1, manager.getCurrentLevelIndex());
    }


    // Test player moves goes to the win screen if they step on the goal tile in the last level.
    @Test
    public void testStepFinalGoalTile(){
        manager.toNextLevel(); //Moves the player to the last level
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        assertEquals(1, manager.getCurrentLevelIndex());
        player.xPos = 12*32;
        player.yPos = 8*32;
        player.moveRight();
        manager.tick();
        assertEquals(1, manager.getCurrentLevelIndex());
        assertEquals(true, manager.hasWonAll());
    }

    // Player walking into goal tile whilst hitting enemy
    @Test
    public void testPlayerWalkingIntoGoalHittingEnemy(){
        // Setup enemy next to player when player is about to hit goal tile
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        player.xPos = 12*32;
        player.yPos = 13*32;
        Enemy enemy = SpriteFactory.makeEnemyRed(13*32, 13*32, app);
        level.addObject(enemy);
        enemy.setCurrentLevel(level);
        player.moveRight();
        this.manager.tick();

        // Check that player has lost a life and player is on the same level
        assertEquals(0, manager.getCurrentLevelIndex());
        assertEquals(2, player.getLives());
    }

    // Player walking into goal tile whilst hitting explosion
    @Test
    public void testPlayerWalkingIntoGoalHittingExplosion(){
        // Setup enemy next to player when player is about to hit goal tile
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        player.xPos = 12*32;
        player.yPos = 13*32;
        ExplosionTile explosion = SpriteFactory.makeExplosionCentre(13*32, 13*32, app);
        level.addObject(explosion);
        player.moveRight();
        this.manager.tick();

        // Check that player has lost a life and player is on the same level
        assertEquals(0, manager.getCurrentLevelIndex());
        assertEquals(2, player.getLives());
    }

    //Test player loses life if they collide with the enemy -> create enemy and player in the same spot
    @Test
    public void testStepOnEnemyResetLevel(){
        Level level = manager.getCurrentLevel();
        Player player = level.getPlayer();
        assertEquals(0, manager.getCurrentLevelIndex());
        player.xPos = 4*32;
        player.yPos = 11*32;
        player.moveRight();
        manager.tick();
        assertEquals(0, manager.getCurrentLevelIndex());
        assertEquals(2, player.getLives());
        assertEquals(player.xPos, 32);
        assertEquals(player.yPos, 3*32);
    }

    
    @Test
    // Test correct number of brokenwalls are removed after explosion
    public void testTileBrokenAfterExplosion(){
        Level level = manager.getCurrentLevel();
        Explosion explosion = new Explosion(5*32, 3*32, level, app);
        explosion.addAllExpTiles();    

        int wallsRemoved = manager.removeBrokenWalls();
        assertEquals(1, wallsRemoved);
    }

    @Test
    // Test that no broken walls are set to removed status in level (not removed) after level resets
    public void testTileBrokenRestoredAfterReset(){
        Level level = manager.getCurrentLevel();
        Explosion explosion = new Explosion(5*32, 3*32, level, app);
        explosion.addAllExpTiles();    
        int wallsRemoved = manager.removeBrokenWalls();
        level.reset();
        List<BrokenWall> walls = level.getBrokenWalls();
        for(BrokenWall b: walls){
            assertEquals(false, b.isRemoved);
        }
    }

}
