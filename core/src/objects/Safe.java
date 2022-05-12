package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;

import java.util.Random;

/**
 * Class for the safe item
 * Extends moneyItems
 * Changes appearance after being cracked
 */
public class Safe extends MoneyItems {


    /**
     * Constructor
     * @param width width of the safe
     * @param height height of the safe
     * @param body body of the safe
     * @param gameScreen gameScreen to which the safe is added
     */
    public Safe(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
        Random rng = new Random();
        // Random value from 100 to 200
        value = 100 + rng.nextInt(101);
    }

    /**
     * When collected change texture
     */
    @Override
    public void changeTexture() {
        texture = new Texture("maps/crackedsafe.png");
    }
}
