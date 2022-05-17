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
        this.value = 10;
        this.isStatic = false;
    }


}
