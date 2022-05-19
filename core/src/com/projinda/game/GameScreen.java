package com.projinda.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import helper.BodyHelper;
import helper.Const;
import helper.ContactType;
import helper.TiledMapHelper;
import objects.*;
import scenes.ScoreBoard;

import java.util.ArrayList;
import java.util.Random;

/**
 * The game's game logical machine.
 */
public class GameScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Boot boot;
    private World world;
    private GameContactListener gameContactListener;

    //Game objects
    private Player player;
    private ArrayList<MovingRectangle> movingRectangles;
    private ArrayList<MoneyItems> moneyItems;

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TiledMapHelper tiledMapHelper;
    private ScoreBoard scoreBoard;


    /**
     * The game screen constructor, called when a new object of type {@code GameScreen} is needed
     * @param camera an orthographic camera, used to follow the player
     * @param boot being able to start new screen objects
     */
    public GameScreen(OrthographicCamera camera, Boot boot) {

        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-25),false);
        this.boot = boot;

        gameContactListener = new GameContactListener(this);
        world.setContactListener(gameContactListener);

        this.camera.position.set(new Vector3(Boot.INSTANCE.getScreenWidth()/2,Boot.INSTANCE.getScreenHeight()/2, 0));

        movingRectangles = new ArrayList<>();
        moneyItems = new ArrayList<>();
        this.tiledMapHelper = new TiledMapHelper(this);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap();

        scoreBoard = new ScoreBoard(batch);
    }

    /**
     * Called when wanting the camera to follow the players position.
     */
    private void updateCamera(){
        if(player.getY() - tiledMapHelper.getTileSize().y*5 < 0){
            if(player.getX() - Boot.INSTANCE.getScreenWidth() / 2 < 0){
                camera.position.set(new Vector3(Boot.INSTANCE.getScreenWidth() / 2, tiledMapHelper.getTileSize().y*5,0));
            }else if (player.getX() + Boot.INSTANCE.getScreenWidth() / 2 > tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x ) {
                camera.position.set(new Vector3(tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x - Boot.INSTANCE.getScreenWidth()/2, tiledMapHelper.getTileSize().y*5,0));

            }else{
                camera.position.set(new Vector3(player.getX(),tiledMapHelper.getTileSize().y*5,0));
            }
        }else if(player.getY() + tiledMapHelper.getTileSize().y*5 > tiledMapHelper.getMapSize().y*tiledMapHelper.getTileSize().y){
            float yPos = tiledMapHelper.getMapSize().y*tiledMapHelper.getTileSize().y - tiledMapHelper.getTileSize().y*5;

            if(player.getX() - Boot.INSTANCE.getScreenWidth() / 2 < 0){
                camera.position.set(new Vector3(Boot.INSTANCE.getScreenWidth() / 2, yPos,0));
            }else if (player.getX() + Boot.INSTANCE.getScreenWidth() / 2 > tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x ) {
                camera.position.set(new Vector3(tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x - Boot.INSTANCE.getScreenWidth()/2, yPos,0));

            }else{
                camera.position.set(new Vector3(player.getX(),yPos,0));
            }
        }else{
            if(player.getX() - Boot.INSTANCE.getScreenWidth() / 2 < 0){
                camera.position.set(new Vector3(Boot.INSTANCE.getScreenWidth() / 2, player.getY(),0));
            }else if (player.getX() + Boot.INSTANCE.getScreenWidth() / 2 > tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x ) {
                camera.position.set(new Vector3(tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x - Boot.INSTANCE.getScreenWidth()/2, player.getY(),0));

            }else{
                camera.position.set(new Vector3(player.getX(),player.getY(),0));
            }
        }
        camera.update();
    }

    /**
     * Called when the screen should update into a new frame
     * @param dt difference in time
     */
    public void update(float dt){
        world.step(1/60f, 6, 2);

        //Gdx.app.log("world stepped ", "");
        player.update(dt);
        scoreBoard.update(dt, player.getScore(), player.getLives());

        if(getScoreBoard().getWorldTimer() <= 0){
            boot.setGameOverScreen(getScoreBoard().getScore(), false);
            return;
        }else if(getScoreBoard().getLives() == 0){
            boot.setGameOverScreen(getScoreBoard().getScore(), true);
            return;
        }

        for (int i = 0; i < movingRectangles.size(); i++) {
            movingRectangles.get(i).update(dt);
        }


        for (int i = 0; i < moneyItems.size(); i++) {
            if(!moneyItems.get(i).getIsStatic())
                moneyItems.get(i).update();
        }
        updateCamera();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            boot.setMainMenuScreen();
            return;
        }

        if(scoreBoard.isNewSecond() && scoreBoard.getWorldTimer() % 10 == 0) {
            spawnEnemy();
        }
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta){
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthogonalTiledMapRenderer.render();

        batch.begin();

        player.render(batch);

        for (int i = 0; i < movingRectangles.size(); i++) {
            movingRectangles.get(i).render(batch);
        }
        for (int i = 0; i < moneyItems.size(); i++) {
            moneyItems.get(i).render(batch);
        }

        batch.end();

        batch.setProjectionMatrix(scoreBoard.stage.getCamera().combined);
        scoreBoard.stage.draw();
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void addMovingRectangle(MovingRectangle movingRectangle) {
        movingRectangles.add(movingRectangle);
    }

    public void addMoneyItem(MoneyItems moneyItem) {
        moneyItems.add(moneyItem);
    }

    /**
     * Finds moneyItem corresponding to fixture's coordinates
     * @param x coordinate of fixture
     * @param y coordinate of fixture
     * @return corresponding money item
     */
    public MoneyItems getMatchingMoneyItem(float x, float y) {
        for (MoneyItems moneyItem: moneyItems) {
            if (x * Const.PPM == moneyItem.getX() + moneyItem.getWidth() / 2 && y * Const.PPM == moneyItem.getY() + moneyItem.getHeight() / 2) {
                return moneyItem;
            }
            else if(x == moneyItem.getX() && y == moneyItem.getY())
                return moneyItem;
        }
        return null;
    }
    
    /**
     * Find which rectangle a player has collided with by finding rectangle with correct position
     * @param x x-coordinate of fixture collided with
     * @param y y-coordinate of fixture collided with
     * @return movingRectangle collided with
     */
    public MovingRectangle getMatchingRectangle(float x, float y) {

        for (MovingRectangle movingRectangle: movingRectangles) {
            if(x*Const.PPM == movingRectangle.getX() + movingRectangle.getWidth()/2 && y*Const.PPM == movingRectangle.getY() + movingRectangle.getHeight() / 2) {
                return movingRectangle;
            }
        }
        return null;
    }

    /**
     * Remove a moving rectangle
     * @param movingRectangle the rectangle to be removed
     */
    public void removeMovingRectangle(MovingRectangle movingRectangle) {
        movingRectangles.remove(movingRectangle);
    }

    /**
     * Remove the item money
     *
     * @param moneyItem the item to be removed
     */
    public void removeMoneyItem (MoneyItems moneyItem) {
        moneyItems.remove(moneyItem);
    }

    /**
     * Checks if the bullet is in motion
     * @return true if it does, false otherwise
     */
    public boolean bulletInMotion () {
        for (MovingRectangle movingRectangle :
                movingRectangles) {
            if (movingRectangle.getClassName().equals("Bullet"))
                return true;
        }
        return false;
    }

    /**
     *
     * @return a TiledMapHelper object
     */
    public TiledMapHelper getTiledMapHelper() {
        return tiledMapHelper;
    }

    /**
     *
     * @return a ScoreBoard object
     */
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    /**
     * Spawns an enemy to the game
     * If the player is on the left third, the enemy is placed on the right third and vice versa
     * If the player is in the middle, the enemy is randomly placed either on the left or right side
     */
    public void spawnEnemy() {
        // If max amount of enemies already exist, do not spawn another
        int enemyCounter = 0;
        for (MovingRectangle movingRectangle :
                movingRectangles) {
            if (movingRectangle.getClassName().equals("Enemy"))
                enemyCounter++;
        }
        if(enemyCounter >= 10)
            return;

        // Find coordinates for the new enemy to be spawned at
        int x = 0;
        int y = 128;
        Random rng = new Random();

        // Player in the left third of game
        if(player.getX() < (tiledMapHelper.getMapSize().x*tiledMapHelper.getTileSize().x)/3) {
            x = 2800;
        }

        // Player in the right third of game
        else if(player.getX() > (2*(tiledMapHelper.getMapSize().x*tiledMapHelper.getTileSize().x/3))) {
            x = 100;
        }

        // Player in the middle
        else {
            x = rng.nextInt(2) == 1 ? 100 : 2800;
        }

        //Create a new body for the enemy
        Body body = BodyHelper.createBody(
                x,
                y,
                64,
                64,
                false,
                99999999,
                world,
                ContactType.ENEMY
        );
        addMovingRectangle(new Enemy(64, 64, body, this));
    }
}
