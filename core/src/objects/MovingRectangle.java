package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;

import java.util.ArrayList;

/**
 * Abstract class used for player and enemies
 */
public abstract class MovingRectangle {

    protected Body body;
    protected GameScreen gameScreen;

    protected float startX, startY;
    protected float x, y, directionX, directionY, speedLevel, speed;
    protected float width, height;

    protected int jumpCounter;
    protected int lives;

    protected Texture texture;

    private boolean isDead;

    /**
     * Constructor for moving rectangle
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */
    public MovingRectangle(float width, float height, Body body, GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        //this.startX = body.getPosition().x;
        //this.startY = body.getPosition().y;
        this.startX = body.getPosition().x * Const.PPM - (width / 2);
        this.startY = body.getPosition().y * Const.PPM - (height / 2);

        this.x = body.getPosition().x;
        this.y = body.getPosition().y;

        this.speed = 10;
        this.directionX = 0;
        this.directionY = 0;
        this.speedLevel = 1;

        this.texture = new Texture("badlogic.jpg");
        this.width = width;
        this.height = height;

        this.jumpCounter = 0;
        this.isDead = false;

        this.body = body;
        this.lives = 1;
    }

    public void addSensor() {
        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2 - 2) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(ContactType.SENSOR);
    }

    /**
     * Pause game for one second
     * Sets the players position to its start position
     */
    public void reset(){
        this.body.setTransform(startX / Const.PPM, startY / Const.PPM, 0);
    }

    public void update() {

        //If the player has died or fallen below y = -300
        if(isDead || y < -300) {
            handleDeath();
        }

        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        // Reset directionX, when user stops moving the player instantly stops
        // Removing this will result in player "gliding"
        //directionX = 0;

        manageUserInput();
    }


    public void manageUserInput() {
        //Reset
        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDirectionX() { return directionX; }

    public float getSpeed() { return speed; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getLives() { return lives; }
    public void lowerLives() { lives--; }

    public void rectangleFallen() {
        if(lives <= 0) {
            handleDeath();
        }
    }

    /**
     * Object has "died"
     * Lower lives by one, reset position to start.
     */
    public void handleDeath() {
        lives--;
        reset();
        isDead = false;
    }

    protected void generateCoin() {
        //Create the bullets body
        Body body = BodyHelper.createBody(
                //x+width/2+directionX*(width/2),
                x+width/2,
                y+height/2,
                64,
                64,
                false,
                99999999,
                gameScreen.getWorld(),
                ContactType.COIN
        );
        gameScreen.addMoneyItem(new Coin(64, 64, body, gameScreen));
    }

    public void setIsDead(boolean isDead) { this.isDead = isDead; }

    public Body getBody() {
        return body;
    }

    public void resetJumpCounter() {
        this.jumpCounter = 0;
    }
}
