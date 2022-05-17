package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;
import helper.Const;

public class Coin extends MoneyItems {

    /**
     * Constructor of MoneyItems
     *
     * @param width      width of the item
     * @param height     height of the item
     * @param body       body of the item
     * @param gameScreen gameScreen the item is added to
     */
    public Coin(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
        this.texture = new Texture("goldcoin.png");
        value = 10;
    }

    public void update() {

        //If the player has died or fallen below y = -300
        if(collected) {
            removeMoneyItem();
        }

        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        // Reset directionX, when user stops moving the player instantly stops
        // Removing this will result in player "gliding"
        //directionX = 0;
    }
}
