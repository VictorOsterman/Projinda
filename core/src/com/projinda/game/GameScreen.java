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
import helper.TiledMapHelper;
import objects.Enemy;
import objects.Player;
import scenes.ScoreBoard;

import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private GameContactListener gameContactListener;

    //Game objects
    private Player player;
    private ArrayList<Enemy> enemies;

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

        enemies = new ArrayList<>();
        this.tiledMapHelper = new TiledMapHelper(this);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap();

        scoreBoard = new ScoreBoard(batch);
    }

    private void updateCamera(){
        if(player.getX() - Boot.INSTANCE.getScreenWidth() / 2 < 0){
            camera.position.set(new Vector3(Boot.INSTANCE.getScreenWidth() / 2, player.getY(),0));
        }else if (player.getX() + Boot.INSTANCE.getScreenWidth() / 2 > tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x ) {
            camera.position.set(new Vector3(tiledMapHelper.getMapSize().x * tiledMapHelper.getTileSize().x - Boot.INSTANCE.getScreenWidth()/2, player.getY(),0));

        }else{
            camera.position.set(new Vector3(player.getX(),player.getY(),0));
        }

        camera.update();
    }

    public void update(float dt){
        world.step(1/60f, 6, 2);
        player.update();
        scoreBoard.update(dt);
        for (Enemy enemy :
                enemies) {
            enemy.update();
        }
        updateCamera();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();

    }

    @Override
    public void render(float delta){
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthogonalTiledMapRenderer.render();
        batch.begin();
        player.render(batch);
        for (Enemy enemy :
                enemies) {
            enemy.render(batch);
        }

        batch.end();
        //box2DDebugRenderer.render(world, camera.combined.scl(Const.PPM));
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

    public void addEnemy(Enemy enemy) {
        if(enemy != null) {
            enemies.add(enemy);
        }
    }
}
