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
 * Abstract class used for player and enemies
 */
public abstract class MovingRectangle extends Sprite {

    public enum State {FALL, JUMP, WALK, RUN, SHOOT, STAND};
    public State currentState;
    public State previousState;

    protected TextureRegion currentFrame;
    private TextureRegion shooting;
    private Animation<TextureRegion> walking;
    private Animation<TextureRegion> running;
    private TextureRegion jumping;
    private TextureRegion falling;

    private float stateTimer;
    private boolean runningRight;



    protected Body body;
    protected GameScreen gameScreen;

    protected float startX, startY;
    protected float x, y, directionX, directionY, speedLevel, speed, startSpeedLevel;
    protected float width, height;

    protected int jumpCounter;
    protected int lives;



    private boolean isDead;
    protected String className;
    protected boolean destroyed;
    protected boolean setToDestroy;

    protected Texture texture;
    protected TextureRegion[] animationFrames;
    protected TextureRegion rectStand;
    protected Animation<TextureRegion> animation;
    protected float elapsedTime;

    /**
     * Constructor for moving rectangle
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */
    public MovingRectangle(float width, float height, Body body, GameScreen gameScreen) {
        //super(gameScreen.getAtlas().findRegion("badlogic"));

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
        this.startSpeedLevel = speedLevel;

        //this.texture = new Texture("badlogic.jpg");
        this.width = width;
        this.height = height;

        this.jumpCounter = 0;
        this.isDead = false;

        this.body = body;


        /*
        this.texture = new Texture("temp/playermovements.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 64, 64);
        animationFrames = new TextureRegion[7];
        int index = 0;
        for(int i = 0; i < 7; i++) {
            animationFrames[1] = tmpFrames[0][i];
        }

        animation = new Animation<TextureRegion>(0.1f, animationFrames);
        */

        TextureAtlas charset = new TextureAtlas(Gdx.files.internal("Tjuv.pack"));
        /*
        TextureAtlas charset = new TextureAtlas(Gdx.files.internal("Tjuv.pack"));
        animation = new Animation<TextureRegion>(0.1f, charset.findRegion("playermovements"));
        animation.setFrameDuration(0.1f);
        elapsedTime = 0f;
         */

        //TextureRegion[][] tmpFrames = TextureRegion.split


        currentState = State.STAND;
        previousState = State.STAND;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(charset.findRegion("playermovements"), i*64, 0, 64, 64));
        }
        walking = new Animation<TextureRegion>(0.15f, frames);

        frames.clear();

        for(int i = 5; i < 7; i++) {
            frames.add(new TextureRegion(charset.findRegion("playermovements"), i*64, 0, 64, 64));
        }
        running = new Animation<TextureRegion>(0.15f, frames);

        shooting = new TextureRegion(charset.findRegion("playermovements"), 4*64, 0, 64, 64);
        jumping = new TextureRegion(charset.findRegion("playermovements"), 7*64, 0, 64, 64);
        falling = new TextureRegion(charset.findRegion("playermovements"), 8*64, 0, 64, 64);

        currentFrame = shooting;

        //this.texture = rectStand.getTexture();



        this.lives = 1;

        this.className = "MovingRectangle";

        this.body.setFixedRotation(true);   //Disables rotating on bodies

        this.destroyed = false;
        this.setToDestroy = false;
    }

    public void render(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();

        if(className.equals("Player")) {
            batch.draw(currentFrame, x, y);
        }else {
            batch.draw(texture, x, y, width, height);
        }
    }

    public void update(float dt) {
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
        currentFrame = getFrame(dt);
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case JUMP:
                region = jumping;
                break;
            case RUN:
                region = running.getKeyFrame(stateTimer, true);
                break;
            case FALL:
                region = falling;
                break;
            case WALK:
                region = walking.getKeyFrame(stateTimer, true);
                break;
            case SHOOT:
            default:
                region = shooting;
                break;
        }
        // region.isFlipX() returns true if flipped over
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

    public State getState() {
        if(body.getLinearVelocity().y > 0)
            return State.JUMP;
        else if(body.getLinearVelocity().y < 0)
            return State.FALL;
        else if(body.getLinearVelocity().x != 0) {
            if(speedLevel == startSpeedLevel)
                return State.WALK;
            else
                return State.RUN;
        }
        else
            return State.SHOOT;
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

    public String getClassName() {
        return className;
    }

    public void resetJumpCounter() {
        this.jumpCounter = 0;
    }

    public void hitByBullet() {
        lowerLives();
    }

    public void removeMovingRectangle() {
        gameScreen.removeMovingRectangle(this);
        gameScreen.getWorld().destroyBody(this.body);
        body.setUserData(null);
        body = null;
        destroyed = true;
    }
}
