package objects;

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
    public Bullet(Body body, GameScreen gameScreen, float directionX) {
        super(20, 10, body, gameScreen);
        this.texture = new Texture("steel.png");
        this.directionX = directionX;
    }

    @Override
    public void update(){
        super.update();
        body.setLinearVelocity(directionX*speedLevel*speed, 0);
    }
}
