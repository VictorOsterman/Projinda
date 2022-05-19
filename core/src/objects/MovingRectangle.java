package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Abstract class used for moving rectangles such as player, enemy, bullet
 * Has support for animations, these must be implemented in subclasses.
 *
 * @author Erik Sidén
 * @version 2022-05-18
 */
public abstract class MovingRectangle extends Sprite {

    public enum State {FALL, JUMP, WALK, RUN, SHOOT, STAND};
    public State currentState;
    public State previousState;

    protected TextureRegion currentFrame;
    protected TextureRegion shooting;
    protected Animation<TextureRegion> walking;
    protected Animation<TextureRegion> running;
    protected Animation<TextureRegion> jumping;
    protected Animation<TextureRegion> falling;

    protected boolean hasAnimations;

    private float stateTimer;
    private boolean runningRight;
    protected boolean onRectangle;
    protected boolean downDash;



    protected Body body;
    protected GameScreen gameScreen;

    protected float startX, startY;
    protected float x, y, directionX, directionY, speedLevel, speed, startSpeedLevel;
    protected int moving; //Used to make player stop when not moving, allows directionX to still be 1 or -1 in order to create bullets

    protected float width, height;

    protected int jumpCounter;
    protected boolean onGround;
    protected int lives;
    protected boolean immortal;
    protected float immortalCounter;
    protected boolean showTransparentImage;
    protected int showTransparentCounter;

    protected boolean isDead;
    protected String className;
    protected boolean destroyed;
    protected boolean setToDestroy;

    protected Texture texture;
    protected float elapsedTime;

    /**
     * Constructs the basics for the moving rectangle
     *
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */
    public MovingRectangle(float width, float height, Body body, GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        this.startX = body.getPosition().x * Const.PPM - (width / 2);
        this.startY = body.getPosition().y * Const.PPM - (height / 2);

        this.x = body.getPosition().x;
        this.y = body.getPosition().y;

        this.speed = 10;
        this.directionX = 0;
        this.directionY = 0;
        this.speedLevel = 1;
        this.startSpeedLevel = speedLevel;

        this.width = width;
        this.height = height;

        this.downDash = false;

        this.jumpCounter = 0;
        this.onGround = true;
        this.isDead = false;
        this.onRectangle = false;
        this.showTransparentImage = false;
        this.showTransparentCounter = 0;

        this.body = body;

        this.hasAnimations = false;

        this.stateTimer = 0;
        this.runningRight = true;

        this.lives = 1;
        this.immortal = false;
        this.immortalCounter = 0;

        this.className = "MovingRectangle";

        this.body.setFixedRotation(true);   //Disables rotating on bodies

        this.destroyed = false;
        this.setToDestroy = false;
        this.texture = new Texture("white.png");    // Add other texture in subclasses
        this.moving = 0;
    }

    /**
     * Adds animations to the moving rectangle
     * Expects a row of seven animation squares in the following order:
     * walk 1, walk 2, stand still, jump 1, jump 2, fall 1, fall 2
     *
     * @param filename file with the animations
     * @param sf scaling factor, if sf = 1 the width and height of rectangle and animations are 64
     */
    public void addAnimations(String filename, int sf) {
        Texture animations = new Texture(filename);

        currentState = State.STAND;
        previousState = State.STAND;

        Array<TextureRegion> frames = new Array<>();
        shooting = new TextureRegion(animations, 2*64*sf, 0, 64*sf, 64*sf);
        //shooting = new TextureRegion(charset.findRegion("playermovements"), 0, 0, 64, 64);

        for(int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(animations, i*64*sf, 0, 64*sf, 64*sf));
        }
        walking = new Animation<TextureRegion>(0.15f, frames);
        running = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(int i = 3; i < 5; i++) {
            frames.add(new TextureRegion(animations, i*64*sf, 0, 64*sf, 64*sf));
        }
        jumping = new Animation<TextureRegion>(0.15f, frames);
        frames.clear();

        for(int i = 5; i < 7; i++) {
            frames.add(new TextureRegion(animations, i*64*sf, 0, 64*sf, 64*sf));
        }
        falling = new Animation<TextureRegion>(0.15f, frames);
        frames.clear();

        currentFrame = shooting;
        this.hasAnimations = true;
    }

    /**
     * Renders the moving rectangle
     * If the rectangle uses animations the correct frame is drawn
     * @param batch
     */
    public void render(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        if(hasAnimations) {
            batch.draw(currentFrame, x, y, width, height);
        }else {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Updates the moving rectangle's position
     * Checks for user input
     * If the rectangle uses animations the frame is updated
     *
     * @param dt
     */
    public void update(float dt) {
        // If the rectangle is immortal, increase the counter of how long the rectangle has been immortal
        // With a lagom interval set that the rectangle should display a transparent image to show that it is immortal
        if(immortal) {
            immortalCounter += dt;
            showTransparentCounter ++;
            if(showTransparentCounter % 15 == 1)
                showTransparentImage = !showTransparentImage;
        }

        // If the rectangle has been immortal long enough, set it to mortal again
        if(immortalCounter >= 4) {
            immortal = false;
            showTransparentImage = false;
            immortalCounter = 0;
        }
        //If the rectangle has died or fallen below y = -300 it is dead
        if(isDead || y < -300) {
            handleDeath();
        }

        if((body.getLinearVelocity().y == 0 && onGround) || onRectangle) {
            downDash = false;
            resetJumpCounter();
        }

        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        manageUserInput();

        if(hasAnimations && !showTransparentImage)
            currentFrame = getFrame(dt);
        else if (showTransparentImage)
            currentFrame = new TextureRegion(new Texture("pictures/transparent.png"), 0, 0, 64, 64);

    }

    /**
     * Returns the next frame for the animation
     * Checks current state with getState method
     * Depending on the state the method gets the correct frame
     * @param dt
     * @return next frame to be rendered for moving rectangle
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case JUMP:
                region = jumping.getKeyFrame(stateTimer, true);
                break;
            case RUN:
                region = running.getKeyFrame(stateTimer, true);
                break;
            case FALL:
                region = falling.getKeyFrame(stateTimer, true);
                break;
            case WALK:
                region = walking.getKeyFrame(stateTimer, true);
                break;
            case SHOOT:
            default:
                region = shooting;
                break;
        }
        // region.isFlipX() returns true if the region is flipped over
        if((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    /**
     * Checks and returns which state the moving rectangle is in
     *
     * @return current state
     */
    public State getState() {
        if(body.getLinearVelocity().y > 0 && !onRectangle)
            return State.JUMP;
        else if(body.getLinearVelocity().y < -0.01f && !onRectangle)
            return State.FALL;
        else if(body.getLinearVelocity().x < -0.1 || body.getLinearVelocity().x > 0.1) {
            // Check if the rectangle is standing on another rectangle
            if(onRectangle) {
                if(moving != 1)
                    return State.SHOOT;
            }
            if(speedLevel == startSpeedLevel)
                return State.WALK;
            else
                return State.RUN;
        }
        else
            return State.SHOOT;
    }

    /**
     * Creates a sensor and adds it to the body
     */
    public void addSensor(String placement) {
        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;

        if(placement.equals("foot")) {
            shape.setAsBox((width / 2 - 2) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);
        } else if (placement.equals("head")) {
            shape.setAsBox((width / 2 - 2) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, (height/2) / Const.PPM), 0);
        }
        this.body.createFixture(fixtureDef).setUserData(ContactType.SENSOR);
    }

    /**
     * Pause game for one second
     * Sets the players position to its start position
     */
    public void reset(){
        this.body.setTransform(startX / Const.PPM, startY / Const.PPM, 0);
    }

    public void manageUserInput() {
        //Reset
        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();
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

    /**
     * Object has "died"
     * Lower lives by one, reset position to start.
     */
    public void handleDeath() {
        lives--;
        reset();
        isDead = false;
    }

    /**
     * Generates a coin in the moving rectangle's position
     * Used when enemies die
     */
    protected void generateCoin() {
        // Generate two coins for a bigger movingRectangle
        int numCoins = width > 100 ? 2 : 1;

        for (int i = 0; i < numCoins; i++) {
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
                    ContactType.COIN,
                    Const.COIN_BIT,
                    (short) (Const.SAFE_BIT | Const.PLATFORM_BIT | Const.PLAYER_BIT),
                    (short) -1
            );
            gameScreen.addMoneyItem(new Coin(64, 64, body, gameScreen));
        }
    }

    public void setIsDead(boolean isDead) { this.isDead = isDead; }

    public Body getBody() {
        return body;
    }

    public String getClassName() {
        return className;
    }

    public void resetJumpCounter() {
        this.jumpCounter = 0;
    }

    public void hitByBullet() {
        lowerLives();
    }

    /**
     * Fully removes a moving rectangle
     * Removes it from GameScreen and cleans up the body
     */
    public void removeMovingRectangle() {
        gameScreen.removeMovingRectangle(this);
        gameScreen.getWorld().destroyBody(this.body);
        body.setUserData(null);
        body = null;
        destroyed = true;
    }

    /**
     * Set that the moving rectangle is either standing or not standing on ground
     * @param onGround boolean whether the rectangle is or is not on ground
     */
    public void setOnGround (boolean onGround) { this.onGround = onGround; }

    public boolean getDownDash() { return downDash; }

    /**
     * When a moving rectangle is hit by an enemy and is supposed to take damage this method is called
     * Lowers the moving rectangle's lives and sets it to immortal
     */
    public void hitByEnemy() {
        if(immortal)
            return;
        lives--;
        immortal = true;
    }
}
