package com.projinda.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class GameScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private GameContactListener gameContactListener;

    //Game objects
    private Player player;
    private ArrayList<MovingRectangle> movingRectangles;
    private ArrayList<MoneyItems> moneyItems;

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TiledMapHelper tiledMapHelper;
    private Box2DDebugRenderer box2DDebugRenderer;

    private ScoreBoard scoreBoard;


    public GameScreen(OrthographicCamera camera) {

        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-25),false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();

        gameContactListener = new GameContactListener(this);
        world.setContactListener(gameContactListener);

        this.camera.position.set(new Vector3(Boot.INSTANCE.getScreenWidth()/2,Boot.INSTANCE.getScreenHeight()/2, 0));

        movingRectangles = new ArrayList<>();
        moneyItems = new ArrayList<>();
        this.tiledMapHelper = new TiledMapHelper(this);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap();

        scoreBoard = new ScoreBoard(batch);
    }

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

    public void update(float dt){
        //Gdx.app.log("Now updating ", "");
        world.step(1/60f, 6, 2);
        //Gdx.app.log("world stepped ", "");
        player.update();
        scoreBoard.update(dt, player.getScore(), player.getLives());

        for (int i = 0; i < movingRectangles.size(); i++) {
            movingRectangles.get(i).update();
        }


        for (int i = 0; i < moneyItems.size(); i++) {
            if(!moneyItems.get(i).getIsStatic())
                moneyItems.get(i).update();
        }
        updateCamera();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();

        if(scoreBoard.isNewSecond() && scoreBoard.getWorldTimer() % 10 == 0) {
            spawnEnemy();
        }
    }

    @Override
    public void render(float delta){
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthogonalTiledMapRenderer.render();
        batch.begin();
        player.render(batch);
        /*for (MovingRectangle movingRectangle :
                movingRectangles) {
            movingRectangle.render(batch);
        }*/
        for (int i = 0; i < movingRectangles.size(); i++) {
            movingRectangles.get(i).render(batch);
        }
        for (int i = 0; i < moneyItems.size(); i++) {
            moneyItems.get(i).render(batch);
        }

        batch.end();
        box2DDebugRenderer.render(world, camera.combined.scl(Const.PPM));
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
        //Gdx.app.log(String.valueOf(x), String.valueOf(y));
        for (MoneyItems moneyItem: moneyItems) {
            //Gdx.app.log(String.valueOf(moneyItem.getX()), String.valueOf(moneyItem.getY()));
            if (x * Const.PPM == moneyItem.getX() + moneyItem.getWidth() / 2 && y * Const.PPM == moneyItem.getY() + moneyItem.getHeight() / 2) {
                return moneyItem;
            }
            else if(x == moneyItem.getX() && y == moneyItem.getY())
                return moneyItem;
        }
        //Gdx.app.log("No matching money item found", "");
        return null;
    }
    
    /**
     * Find which rectangle a player has collided with by finding rectangle with correct position
     * @param x x-coordinate of fixture collided with
     * @param y y-coordinate of fixture collided with
     * @return movingRectangle collided with
     */
    public MovingRectangle getMatchingRectangle(float x, float y) {
        //Gdx.app.log("---------------------", "");
        //Gdx.app.log(String.valueOf(x*Const.PPM), String.valueOf(y*Const.PPM));
        for (MovingRectangle movingRectangle: movingRectangles) {
            //Gdx.app.log(String.valueOf(movingRectangle.getX() + movingRectangle.getWidth()/2), String.valueOf(movingRectangle.getY() + movingRectangle.getHeight() / 2));
            if(x*Const.PPM == movingRectangle.getX() + movingRectangle.getWidth()/2 && y*Const.PPM == movingRectangle.getY() + movingRectangle.getHeight() / 2) {
                //Gdx.app.log("---------------------", "");
                return movingRectangle;
            }
        }
        //Gdx.app.log("No matching moving rectangle found", "");
        //Gdx.app.log("---------------------", "");
        return null;
    }

    public void removeMovingRectangle(MovingRectangle movingRectangle) {
        movingRectangles.remove(movingRectangle);
    }

    public void removeMoneyItem (MoneyItems moneyItem) {
        moneyItems.remove(moneyItem);
    }

    public boolean bulletInMotion () {
        for (MovingRectangle movingRectangle :
                movingRectangles) {
            if (movingRectangle.getClassName().equals("Bullet"))
                return true;
        }
        return false;
    }

    public TiledMapHelper getTiledMapHelper() {
        return tiledMapHelper;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public SpriteBatch getBatch() {
        return batch;
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
