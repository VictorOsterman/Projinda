package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;

public class Bullet extends MovingRectangle{
    /**
     * Constructor for moving rectangle
     *
     * @param body       body to be used by player
     * @param gameScreen
     */
    private boolean remove;
    private float lastX;
    private boolean outOfSight;
    public Bullet(Body body, GameScreen gameScreen, float directionX) {
        super(20, 10, body, gameScreen);

        this.texture = directionX == 1 ? new Texture("bulletr.png") : new Texture("bulletl.png");
        this.directionX = directionX;
        this.body.setGravityScale(0);    //Remove gravity from floating Bullet
        this.remove = false;
        this.className = "Bullet";
        this.speedLevel = 3;
        this.lastX = -1;
        this.outOfSight = false;
    }

    @Override
    public void update(float dt){
        if((lives <= 0 || lastX == x || outOfSight) && !destroyed) {
            removeMovingRectangle();
            return;
        }

        else if(!destroyed) {
            lastX = x;
            super.update(dt);
            body.setLinearVelocity(directionX*speedLevel*speed, directionY*speedLevel*speed);
            outOfSight = bulletOutOfSight();
        }
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    private boolean bulletOutOfSight() {
        if(x > gameScreen.getPlayer().getX() + (Boot.INSTANCE.getScreenWidth()/2) || x < gameScreen.getPlayer().getX() - (Boot.INSTANCE.getScreenWidth()/2)) {
            // If the player is standing on the edge, the bullet is within sight longer
            if(directionX > 0 && x <= Boot.INSTANCE.getScreenWidth()) {
                return false;
            } else if (directionX < 0 && x >= gameScreen.getTiledMapHelper().getMapSize().x * gameScreen.getTiledMapHelper().getTileSize().x - Boot.INSTANCE.getScreenWidth()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
