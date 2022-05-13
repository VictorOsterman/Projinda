package objects;

import com.badlogic.gdx.Gdx;
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


    // Moves with its own velocity
    private float velocity;
    /**
     * Constructor for moving rectangle
     *
     * @param width      width of the players body
     * @param height     height of the players body
     * @param body       body to be used by player
     * @param gameScreen
     */
    public MovingPlatform(float width, float height, Body body, GameScreen gameScreen, int direction) {
        super(width, height, body, gameScreen);
        this.velocity = 0.3F;
        // Set direction of platform
        if (direction == 0)
            this.velX = velocity;
        else if (direction == 1)
            this.velY = velocity;
        else {
            this.velX = velocity;
            this.velY = velocity;
        }

        this.body.setGravityScale(0);    //Remove gravity from floating Platform
        this.texture = new Texture("steel.png");
    }

    @Override
    public void update(){
        super.update();
        updatePlatformVelocity();
        body.setLinearVelocity(velX*speed, velY*speed);
    }

    /**
     * Check if the platform has reached end of scope, if so - turn around
     */
    public void updatePlatformVelocity() {
        if(x > startX + width) {
            velX = -velocity;
        } else if(x < startX - width) {
            velX = velocity;
        }
        if(y > startY + width) {
            velY = -velocity;
        } else if(y < startY - width) {
            velY = velocity;
        }
    }


}
