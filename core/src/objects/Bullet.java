package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;

/**
 * Class for bullets
 * Can be shot by player and enemies
 * Depending on who shot the bullet it has different speedLevels and texture
 * Is removed when either hitting something or is out of frame
 *
 * @author Erik Sidén
 * @version 2022-05-20
 */
public class Bullet extends MovingRectangle{

    private float lastX;
    private boolean outOfSight;
    private final String shotBy;

    /**
     * Constructor for bullet
     * Uses the superclass' constructor
     * Sets the texture to correct texture
     * Sets the directionX to correct value
     *
     * @param body body of the bullet
     * @param gameScreen gameScreen of the bullet
     * @param directionX direction of the bullet
     * @param shotBy string representation of who shot the bullet
     */
    public Bullet(Body body, GameScreen gameScreen, float directionX, String shotBy) {
        super(20, 10, body, gameScreen);

        this.texture = directionX == 1 ? new Texture("bulletr.png") : new Texture("bulletl.png");

        this.directionX = directionX;
        this.body.setGravityScale(0);    //Remove gravity from floating Bullet
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

    /**
     * Updates the bullets position
     * The bullet is removed if:
     *      The bullet has no lives (has hit something),
     *      The bullets x position did not change during last update
     *      The bullet is out of sight
     *
     *      and it is:
     *      It is not already destroyed
     *
     * Otherwise it updates its position and checks if it is now out of sight
     *
     * @param dt time since last update
     */
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


    /**
     * Check if the bullet is out of sight
     *
     * @return true if out of sight, false if not in sight
     */
    private boolean bulletOutOfSight() {
        // If the bullet is outside of sight to the right or left of a normal camera (player not on the edge)
        if(x > gameScreen.getPlayer().getX() + (Boot.INSTANCE.getScreenWidth()/2) || x < gameScreen.getPlayer().getX() - (Boot.INSTANCE.getScreenWidth()/2)) {
            // If the player is standing on the edge, the camera is not centered around the player
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

    /**
     * Creates a new bullet and adds it to the game screens arraylist of moving rectangles
     * @param x coordinate of shooter
     * @param y coordinate of shooter
     * @param width of shooter
     * @param height of shooter
     * @param gameScreen gameScreen the shooter and bullet belongs to
     * @param directionX direction the bullet is supposed to go in
     * @param shotBy who shot the bullet
     */
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
