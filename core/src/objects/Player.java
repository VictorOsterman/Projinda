package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.Const;
import helper.BodyHelper;
import helper.ContactType;

/**
 * Player class, extends MovingRectangle class
 * Creates a player object and listens to user input.
 */
public class Player extends MovingRectangle{


    /**
     * Constructor for player
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */
    public Player(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
    }

    /**
     * Update method of player
     * Updates x and y positions
     * Listens to user input
     */
    @Override
    public void update(){
        super.update();
        body.setLinearVelocity(velX*speed, body.getLinearVelocity().y);
    }

    /**
     * Listens to user input
     *
     * KEY - ACTION
     * RIGHT - WALKS RIGHT
     * LEFT - WALKS LEFT
     * UP - JUMPS UP
     * DOWN - DASHES DOWNWARDS
     * R - RESETS PLAYER
     * SPACE - RUNS
     * D - DASHES
     */
    @Override
    public void manageUserInput() {
        super.manageUserInput();
        //Walk right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            velX = 1;
        //Walk left
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            velX = -1;
        //Jump up
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCounter < 2) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, 15), body.getPosition(), true);
            jumpCounter ++;
        }

        if(body.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }
        //Dash down
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            body.applyForceToCenter(0, -1500, true);
        //Run
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
            velX *= 1.5;
        //Dash to side
        if(Gdx.input.isKeyJustPressed(Input.Keys.D))
            velX *= 8;
    }
}
