package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.Const;
import helper.BodyHelper;
import helper.ContactType;
import org.w3c.dom.Text;

import java.util.ArrayList;

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


    private int score;
    private MovingRectangle movingRectangle;


    public Player(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);

        this.score = 0;
        addSensor();
        this.lives = 3;
        this.moving = 0;
        this.directionX = 1;
        this.speedLevel = 0.5f;
        this.startSpeedLevel = speedLevel;

        className = "Player";
        addAnimations("Player.png", 1);
    }

    @Override
    public void addSensor() {
        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();
        //Narrower width than movingRectangle's sensor
        shape.setAsBox((width / 2 - 10) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(ContactType.PLAYERSENSOR);
    }

    /**
     * Update method of player
     * Updates x and y positions
     * Listens to user input
     */
    @Override
    public void update(float dt){
        moving = 0;
        super.update(dt);
        float velocityX = moving*directionX*speedLevel;
        // If the player is standing on a platform, the platform's velocity is the player's "base velocity"
        if(onRectangle) {
            //Gdx.app.log("Player on rectangle", "update method in player");
            if(this.movingRectangle != null) {
                velocityX = moving * directionX * speedLevel + movingRectangle.directionX * movingRectangle.speedLevel;
            }
        }
        body.setLinearVelocity(velocityX*speed, body.getLinearVelocity().y);
        //setRegion(getFrame(dt));
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
        speedLevel = startSpeedLevel;
        super.manageUserInput();
        //Walk right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            directionX = 1;
            moving = 1;
        }

        //Walk left
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            directionX = -1;
            moving = 1;
        }

        //Jump up
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCounter < 2) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, 13), body.getPosition(), true);
            jumpCounter ++;
        }
        //Dash down
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            body.applyForceToCenter(0, -1500, true);
        //Run
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
            speedLevel *= 1.5;

        //Shoot bullet
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if(!gameScreen.bulletInMotion()) {
                //Create the bullets body
                Body body = BodyHelper.createBody(
                        x+width/2+directionX*(width),
                        y+height/2,
                        20,
                        10,
                        false,
                        0,
                        gameScreen.getWorld(),
                        ContactType.PLAYERBULLET
                );
                gameScreen.addMovingRectangle(new Bullet(body, gameScreen, directionX));
            }
        }

        // Hack for pro players, unlimited coins $$$
        if(Gdx.input.isKeyPressed(Input.Keys.U)) {
            //Create the coins body
            Body body = BodyHelper.createBody(
                    x+width/2,
                    y-height/2,
                    64,
                    64,
                    false,
                    99999999,
                    gameScreen.getWorld(),
                    ContactType.COIN
            );
            gameScreen.addMoneyItem(new Coin(64, 64, body, gameScreen));
        }
    }

    public int getScore() { return score; }
    public void increaseScore(int newScore) { score += newScore; }

    /**
     * Set that the player is standing on a rectangle
     * @param movingRectangle rectangle which the player is standing on
     */
    public void setOnRectangle(MovingRectangle movingRectangle) {
        this.movingRectangle = movingRectangle;
        this.onRectangle = true;
    }

    public void setOnRectangle(boolean onRectangle) {
        this.onRectangle = onRectangle;
    }

    public boolean isOnRectangle() {
        return onRectangle;
    }

    @Override
    public void handleDeath() {
        lives--;
        isDead = false;
    }

}
