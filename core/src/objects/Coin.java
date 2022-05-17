package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;
import helper.Const;

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


        this.speed = 10;
        rng = new Random();
        if(rng.nextInt(2) == 0)
            this.directionX = 1;
        else
            this.directionX = -1;
        thrown = false;

        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        body.applyLinearImpulse(new Vector2(0, 10+rng.nextInt(10)), body.getPosition(), true);
    }

    @Override
    public void update() {
        // If the coin is collected
        if(collected) {
            removeMoneyItem();
            gameScreen.getWorld().destroyBody(this.body);
            return;
        }
        super.update();
        if(!thrown) {
            body.setLinearVelocity(body.getLinearVelocity().x, 10);
            thrown = true;
        }
        speed *= 0.99;

        body.setLinearVelocity(directionX*speed, body.getLinearVelocity().y);
    }

}
