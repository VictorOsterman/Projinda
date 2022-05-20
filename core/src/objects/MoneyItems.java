package objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;
import helper.Const;

import java.util.Random;

/**
 * Abstract class for items regarding money
 * The items objectives are to hold a value, be picked up by the player, then either disappear or
 * change appearance
 */
public abstract class MoneyItems {
    protected int value;
    protected boolean collected;
    protected boolean generateCoins;
    protected GameScreen gameScreen;
    protected Texture texture;
    protected float x, y;
    protected float width, height;
    protected Body body;
    protected boolean isStatic;
    protected boolean destroyed;
    protected float removeCounter;

    //For non-static moneyItems:
    protected float directionX, speed;

    /**
     * Constructor of MoneyItems
     * @param width width of the item
     * @param height height of the item
     * @param body body of the item
     * @param gameScreen gameScreen the item is added to
     */
    public MoneyItems(float width, float height, Body body, GameScreen gameScreen) {
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.width = width;
        this.height = height;
        this.gameScreen = gameScreen;
        this.body = body;
        this.removeCounter = 0;

        //Assign value its value in subclass
        value = 0;
        collected = false;
        generateCoins = false;

        this.texture = new Texture("maps/safepicture.png");
        this.isStatic = true;
        this.destroyed = false;
    }

    public void update() {
        if(collected)
            return;

        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        // Reset directionX, when user stops moving the player instantly stops
        // Removing this will result in player "gliding"
        //directionX = 0;

    }

    /**
     * Method where the player collects the items money
     * If it already has been collected nothing happens.
     * After collecting the texture changes.
     */
    public void collect() {
        if(collected) return;
        gameScreen.getPlayer().increaseScore(value);
        collected = true;
        generateCoins = true;
        changeTexture();
    }

    /**
     * Changing the texture is overridden by subclasses
     */
    public void changeTexture() {}

    public void removeMoneyItem() {
        gameScreen.removeMoneyItem(this);
        gameScreen.getWorld().destroyBody(this.body);
        body.setUserData(null);
        body = null;
        destroyed = true;
    }

    public void removeBody() {
        gameScreen.getWorld().destroyBody(this.body);
        body.setUserData(null);
        body = null;
        destroyed = true;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getValue() {
        return value;
    }

    public boolean getIsStatic() { return isStatic; }

    public Body getBody() {
        return body;
    }

    public void render(SpriteBatch batch, float dt) {
        if(isStatic) {
            batch.draw(texture, x * Const.PPM - width / 2, y * Const.PPM - height / 2, width, height);
        }
        else if(!isStatic) {
            batch.draw(texture, x, y, width, height);
        }
    }
}
