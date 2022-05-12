package objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;
import helper.Const;

/**
 * Abstract class for items regarding money
 * The items objectives are to hold a value, be picked up by the player, then either disappear or
 * change appearance
 */
public abstract class MoneyItems {
    protected int value;
    protected boolean collected;
    protected GameScreen gameScreen;
    protected Texture texture;
    protected float x, y;
    protected float width, height;
    protected Body body;

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

        //Assign value its value in subclass
        value = 0;
        collected = false;

        this.texture = new Texture("maps/safepicture.png");
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
        changeTexture();
    }

    /**
     * Changing the texture is overridden by subclasses
     */
    public void changeTexture() {}

    public void render(SpriteBatch batch) {
        batch.draw(texture, x * Const.PPM - width/2, y * Const.PPM - height/2, width, height);
    }
}
