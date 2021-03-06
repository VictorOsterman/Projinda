package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;

/**
 * MovingPlatform class.
 * Holds a floating platform.
 * Can be set to go up and down / to the sides / both
 *      This is decided by the name of the rectangle when first rendered in TiledMapHelper
 *
 * Moves up or down or both by its width*2 size
 *
 * @author Erik Sidén
 * @version 2022-05-13
 */
public class MovingPlatform extends MovingRectangle{

    private int range;

    /**
     * Construct the moving platform.
     * Set the directionX and Y to correct values
     * @param width of the platform
     * @param height of the platform
     * @param body of the platform
     * @param gameScreen of the platform
     * @param direction of the platform
     */
    public MovingPlatform(float width, float height, Body body, GameScreen gameScreen, int direction) {
        super(width, height, body, gameScreen);
        this.speedLevel = 0.3F;
        // Set direction of platform
        if (direction == 0)
            this.directionX = 1;
        else if (direction == 1)
            this.directionY = 1;
        else {
            this.directionX = 1;
            this.directionY = 1;
        }

        this.body.setGravityScale(0);    //Remove gravity from floating Platform
        this.texture = new Texture("steel.png");
        this.range = 200;
    }

    /**
     * Uses the superclass' update method
     * Update the platform's direction
     * @param dt time since last update
     */
    @Override
    public void update(float dt){
        super.update(dt);
        updatePlatformDirection();
        body.setLinearVelocity(directionX*speedLevel*speed, directionY*speedLevel*speed);
    }

    /**
     * Check if the platform has reached end of scope, if so -> turn around
     */
    public void updatePlatformDirection() {
        if(x > startX + range) {
            directionX = -1;
        } else if(x < startX - range) {
            directionX = 1;
        }
        if(y > startY + range) {
            directionY = -1;
        } else if(y < startY - range) {
            directionY = 1;
        }
    }
}
