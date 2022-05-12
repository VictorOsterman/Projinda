package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.projinda.game.GameScreen;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;

/**
 * Abstract class used for player and enemies
 */
public abstract class MovingRectangle {

    protected Body body;
    protected GameScreen gameScreen;

    protected float startX, startY;
    protected float x, y, velX, velY, speed;
    protected float width, height;

    protected int jumpCounter;

    protected Texture texture;

    /**
     * Constructor for moving rectangle
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */
    public MovingRectangle(float width, float height, Body body, GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        this.startX = body.getPosition().x;
        this.startY = body.getPosition().y;

        this.x = body.getPosition().x;
        this.y = body.getPosition().y;

        this.speed = 10;
        this.velX = 0;
        this.velY = 0;

        this.texture = new Texture("badlogic.jpg");
        this.width = width;
        this.height = height;

        this.jumpCounter = 0;

        this.body = body;

        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2 - 2) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(ContactType.SENSOR);
    }

    /**
     * Sets the rectangles position to its start position
     */
    public void reset() {
        this.body.setTransform(startX, startY, 0);
    }

    public void update() {
        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        // Reset velX, when user stops moving the player instantly stops
        // Removing this will result in player "gliding"
        velX = 0;

        manageUserInput();
    }

    public void manageUserInput() {
        //Reset
        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

}