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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import helper.Const;
import helper.ContactType;
import helper.TiledMapHelper;
import objects.*;
import scenes.ScoreBoard;

import java.util.ArrayList;

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
        Gdx.app.log("Now updating ", "");
        Gdx.app.log(String.valueOf(movingRectangles.size()), "size of moving rectangles");
        world.step(1/60f, 6, 2);
        Gdx.app.log("world stepped ", "");
        player.update();
        Gdx.app.log("player updated ", "");
        scoreBoard.update(dt, player.getScore(), player.getLives());
        Gdx.app.log("scoreboard updated ", "");

        Gdx.app.log("Now updating moving rectangles", "");
        for (int i = 0; i < movingRectangles.size(); i++) {
            Gdx.app.log("update", "");
            movingRectangles.get(i).update();
        }
        Gdx.app.log("Done with moving rectangles", "");


        Gdx.app.log("Now updating money items", "");
        for (int i = 0; i < moneyItems.size(); i++) {
            if(!moneyItems.get(i).getIsStatic())
                moneyItems.get(i).update();
        }
        Gdx.app.log("Done with money items" , "");
        updateCamera();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();

        Gdx.app.log("Update finished", "");
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
        Gdx.app.log("Now rendering moving rectangles",String.valueOf(movingRectangles.size()));
        for (int i = 0; i < movingRectangles.size(); i++) {
            Gdx.app.log(String.valueOf(movingRectangles.get(i)), String.valueOf(i));
            movingRectangles.get(i).render(batch);
            Gdx.app.log("rendered moving rectangle", "");
        }
        Gdx.app.log("Now rendering moneyitems, amount: ", String.valueOf(moneyItems.size()));
        for (int i = 0; i < moneyItems.size(); i++) {
            Gdx.app.log(String.valueOf(moneyItems.get(i)), String.valueOf(i));
            moneyItems.get(i).render(batch);
        }
        Gdx.app.log("Now rendered moneyitems", "");

        batch.end();
        Gdx.app.log("batch ended", "");
        box2DDebugRenderer.render(world, camera.combined.scl(Const.PPM));
        Gdx.app.log("box2ddebugrendered", "");
        batch.setProjectionMatrix(scoreBoard.stage.getCamera().combined);
        Gdx.app.log("set projection matrix ", "");
        scoreBoard.stage.draw();
        Gdx.app.log("Drawn stage", "");
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
        Gdx.app.log(String.valueOf(x), String.valueOf(y));
        for (MoneyItems moneyItem: moneyItems) {
            Gdx.app.log(String.valueOf(moneyItem.getX()), String.valueOf(moneyItem.getY()));
            if (x * Const.PPM == moneyItem.getX() + moneyItem.getWidth() / 2 && y * Const.PPM == moneyItem.getY() + moneyItem.getHeight() / 2) {
                return moneyItem;
            }
            else if(x == moneyItem.getX() && y == moneyItem.getY())
                return moneyItem;
        }
        Gdx.app.log("No matching money item found", "");
        return null;
    }
    
    /**
     * Find which rectangle a player has collided with by finding rectangle with correct position
     * @param x x-coordinate of fixture collided with
     * @param y y-coordinate of fixture collided with
     * @return movingRectangle collided with
     */
    public MovingRectangle getMatchingRectangle(float x, float y) {
        Gdx.app.log("---------------------", "");
        Gdx.app.log(String.valueOf(x*Const.PPM), String.valueOf(y*Const.PPM));
        for (MovingRectangle movingRectangle: movingRectangles) {
            Gdx.app.log(String.valueOf(movingRectangle.getX() + movingRectangle.getWidth()/2), String.valueOf(movingRectangle.getY() + movingRectangle.getHeight() / 2));
            if(x*Const.PPM == movingRectangle.getX() + movingRectangle.getWidth()/2 && y*Const.PPM == movingRectangle.getY() + movingRectangle.getHeight() / 2) {
                Gdx.app.log("---------------------", "");
                return movingRectangle;
            }
        }
        Gdx.app.log("No matching moving rectangle found", "");
        Gdx.app.log("---------------------", "");
        return null;
    }

    public void removeMovingRectangle(MovingRectangle movingRectangle) {
        movingRectangles.remove(movingRectangle);
    }

    public void removeMoneyItem (MoneyItems moneyItem) {
        moneyItems.remove(moneyItem);
    }
}
