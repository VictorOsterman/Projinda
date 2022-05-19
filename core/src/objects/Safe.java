package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.Const;

import java.util.Random;

/**
 * Class for the safe item
 * Extends moneyItems
 * Changes appearance after being cracked
 */
public class Safe extends MoneyItems {

    private float respawnCounter;
    private boolean respawned;

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
        Gdx.app.log("Created safe", "");
        respawnCounter = 0;
        respawned = false;
    }

    /**
     * When collected change texture
     */
    @Override
    public void changeTexture() {
        texture = new Texture("maps/crackedsafe.png");
    }

    @Override
    public void render(SpriteBatch batch, float dt) {
        if (collected && (!destroyed || !respawned)) {
            if ((removeCounter >= 3) && !destroyed) {
                // Remove the body but don't remove the safe from game screen
                removeBody();
                return;
            }
            if(!respawned && removeCounter >= 6) {
                // If the player is far enough away spawn a new safe
                Player player = gameScreen.getPlayer();
                int hsw = Boot.INSTANCE.getScreenWidth() / 2;
                int hsh = Boot.INSTANCE.getScreenHeight() / 2;
                if(player.getX() > x*Const.PPM + hsw || player.getX() < x*Const.PPM - hsw || player.getY() > y*Const.PPM + hsh || player.getY() < y*Const.PPM - hsh) {
                    // Remove the safe from moneyItems and spawn a new safe in the safe's place
                    gameScreen.removeMoneyItem(this);
                    gameScreen.spawnSafe(x * Const.PPM, y * Const.PPM);
                    respawned = true;
                }
            }
            else {
                removeCounter += dt;
            }
        }
        if (!destroyed) {
            batch.draw(texture, x * Const.PPM - width / 2, y * Const.PPM - height / 2, width, height);
        }
    }
}
