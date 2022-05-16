package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.ContactType;

public class Bullet extends MovingRectangle{
    /**
     * Constructor for moving rectangle
     *
     * @param body       body to be used by player
     * @param gameScreen
     */
    protected boolean remove;
    private float lastX;
    public Bullet(Body body, GameScreen gameScreen, float directionX) {
        super(20, 10, body, gameScreen);
        this.texture = new Texture("steel.png");
        this.directionX = directionX;
        this.body.setGravityScale(0);    //Remove gravity from floating Bullet
        this.remove = false;
        lastX = -1;
    }

    @Override
    public void update(){
        lastX = x;
        super.update();
        if(lastX == x) {
            remove = true;
        }
        Gdx.app.log(String.valueOf(remove), "");
        body.setLinearVelocity(directionX*speedLevel*speed, directionY*speedLevel*speed);
    }


}
