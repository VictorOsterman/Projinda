package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.Const;
import helper.BodyHelper;
import helper.ContactType;

/**
 * Player class
 *
 */
public class Player {
    private Body body;
    private GameScreen gameScreen;

    private float x, y, velX, velY, speed;
    private int width, height;

    private boolean onGround;

    private Texture texture;

    public Player(GameScreen gameScreen, float x, float y) {
        this.x = x;
        this.y = y;
        this.gameScreen = gameScreen;

        this.speed = 10;
        this.velX = 0;
        this.velY = 0;

        this.texture = new Texture("badlogic.jpg");
        this.width = 40;
        this.height = 60;

        this.body = BodyHelper.createBody(x, y, width, height, false, 0, this.gameScreen.getWorld(), ContactType.PLAYER);

        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2 - 2) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(ContactType.SENSOR);
    }

    public void reset() {
        this.body.setTransform(Boot.INSTANCE.getScreenHeight() / 2 / Const.PPM, Boot.INSTANCE.getScreenHeight() / 2 / Const.PPM, 0);
    }

    public void update() {
        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        // Reset velX, when user stops moving the player instantly stops
        // Removing this will result in player "gliding"
        velX = 0;

        // Apply gravity
        body.applyForceToCenter(0, -50, true);

        //Walk right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            velX = 1;

        //Walk left
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            velX = -1;

        //Jump up
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && onGround == true)
            body.applyForceToCenter(0, 1500, true);

        //Dash down
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            body.applyForceToCenter(0, -1500, true);

        //Reset
        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();

        //Run
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
            velX *= 1.5;

        //Dash to side
        if(Gdx.input.isKeyJustPressed(Input.Keys.D))
            velX *= 8;

        body.setLinearVelocity(velX*speed, body.getLinearVelocity().y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
