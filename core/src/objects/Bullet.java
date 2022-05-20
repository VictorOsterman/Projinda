package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
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
    private String shotBy;
    public Bullet(Body body, GameScreen gameScreen, float directionX, String shotBy) {
        super(20, 10, body, gameScreen);

        this.texture = directionX == 1 ? new Texture("bulletr.png") : new Texture("bulletl.png");

        this.directionX = directionX;
        this.body.setGravityScale(0);    //Remove gravity from floating Bullet
        this.remove = false;
        this.className = "Bullet";
        this.shotBy = shotBy;
        this.speedLevel = 3;
        this.lastX = -1;
        this.outOfSight = false;

        if(shotBy.equals("enemy")) {
            this.speedLevel = 1.5f;
            this.texture = new Texture("laser.png");
        }
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

    public String getShotBy() { return shotBy; }

    public static void shootBullet(float x, float y, float width, float height, GameScreen gameScreen, float directionX, String shotBy) {
        ContactType ct;
        short mBits;
        short gIndex;
        if(shotBy.equals("player")) {
            ct = ContactType.PLAYERBULLET;
            mBits = (short) (Const.PLAYER_BIT | Const.ENEMY_BIT | Const.PLATFORM_BIT | Const.SAFE_BIT);
            gIndex = (short) 0;
        }
        else {
            Gdx.app.log("Shot by enemy", "");
            ct = ContactType.ENEMYBULLET;
            mBits = (short) (Const.PLAYER_BIT | Const.ENEMY_BIT | Const.PLATFORM_BIT | Const.SAFE_BIT);
            gIndex = (short) -1;
        }

        //Create the bullets body
        Body body = BodyHelper.createBody(
                x+width/2+directionX*(width),
                y+height/2,
                20,
                10,
                false,
                0,
                gameScreen.getWorld(),
                ct,
                Const.BULLET_BIT,
                mBits,
                gIndex
                //(short) (Const.PLAYER_BIT | Const.ENEMY_BIT | Const.PLATFORM_BIT | Const.SAFE_BIT),
                //(short) 0
        );
        gameScreen.addMovingRectangle(new Bullet(body, gameScreen, directionX, shotBy));
    }
}
