package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;

import java.util.Random;

/**
 * Coin class
 * Holds a dynamic coin
 * When spawned it jumps up
 * Holds a value that can be collected
 * Collected when player touches the coin
 *
 * @author Erik Sidén
 * @version 2022-05-17
 */
public class Coin extends MoneyItems {
    private boolean thrown;
    private Random rng;

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



        rng = new Random();
        if(rng.nextInt(2) == 0)
            this.directionX = 1;
        else
            this.directionX = -1;
        thrown = false;
        this.speed = 5 + rng.nextInt(5);

        body.setLinearVelocity(body.getLinearVelocity().x, 10+rng.nextInt(5));
    }

    @Override
    public void update() {
        // If the coin is collected
        if(collected && !destroyed) {
            removeMoneyItem();
            return;
        }
        else if(!destroyed) {
            super.update();
            if(!thrown) {
                //body.setLinearVelocity(body.getLinearVelocity().x, 10);
                thrown = true;
            }
            speed *= 0.99;

            body.setLinearVelocity(directionX*speed, body.getLinearVelocity().y);
        }
    }

    /**
     * Other objects call on this method to generate coins on top of themselves
     * @param x coordinate of calling object
     * @param y coordinate of calling object
     * @param width of the calling object
     * @param height of the calling object
     */
    public static void generateCoin(float x, float y, float width, float height, GameScreen gameScreen) {
        //Create the coins body
        Body newBody = BodyHelper.createBody(
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
        gameScreen.addMoneyItem(new Coin(64, 64, newBody, gameScreen));
    }

}
