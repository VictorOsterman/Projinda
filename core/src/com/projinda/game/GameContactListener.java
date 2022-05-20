package com.projinda.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import helper.ContactType;
import objects.*;

/**
 * The game's contact listener
 * Checks for collisions and handles them correctly
 */
public class GameContactListener implements ContactListener {

    private final GameScreen gameScreen;
    public GameContactListener(GameScreen gameScreen) { this.gameScreen = gameScreen; }

    /**
     * When two objects collide this method is called
     * This class handles how this collision is dealt with
     * @param contact contact between the objects
     */
    @Override
    public void beginContact(Contact contact) {

        // Fixtures involved in the contact
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a.getUserData() == ContactType.PLAYERSENSOR || b.getUserData() == ContactType.PLAYERSENSOR) {
            boolean isDashing = gameScreen.getPlayer().getDownDash();
            // Player's sensor is touching ground, set that it is on ground
            gameScreen.getPlayer().setOnGround(true);

            Fixture notPlayerSensor = a.getUserData() == ContactType.PLAYERSENSOR ? b : a;

            if(a.getUserData() == ContactType.SAFE || b.getUserData() == ContactType.SAFE) {
                // Player is standing on safe, find safe and retrieve money if the player is dashing
                if(isDashing)
                    ((Safe) gameScreen.getMatchingMoneyItem(notPlayerSensor.getBody().getPosition().x, notPlayerSensor.getBody().getPosition().y)).collect();
            }
            else if(a.getUserData() == ContactType.MOVINGPLATFORM || b.getUserData() == ContactType.MOVINGPLATFORM) {
                // Player is standing on platform, find platform and set player's platform to this platform
                MovingPlatform movingPlatform = (MovingPlatform) gameScreen.getMatchingRectangle(notPlayerSensor.getBody().getPosition().x, notPlayerSensor.getBody().getPosition().y);
                gameScreen.getPlayer().setOnRectangle(movingPlatform);
            }
            else if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                // If the player is dashing down on the enemy, the enemy should lose lives
                // Player is standing on platform, find platform and set player's platform to this platform
                Enemy enemy = (Enemy) gameScreen.getMatchingRectangle(notPlayerSensor.getBody().getPosition().x, notPlayerSensor.getBody().getPosition().y);
                if(isDashing)
                    enemy.lowerLives();
                gameScreen.getPlayer().setOnRectangle(enemy);
            }
        }

        if(a.getUserData() == ContactType.PLAYER || b.getUserData() == ContactType.PLAYER) {
            if(a.getUserData() == ContactType.PLAYERBULLET || b.getUserData() == ContactType.PLAYERBULLET)
                return;

            Fixture notPlayer = a.getUserData() == ContactType.PLAYER ? b : a;

            if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                // Player is in contact with enemy.
                // If the player is not standing on the enemy it should die.
                // Meaning, if the players lowest y is below enemy's highest y
                // Get the matching enemy
                Enemy enemy = (Enemy) gameScreen.getMatchingRectangle(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y);

                // Fixes bug when player is below enemy outside camera's scope
                if(enemy == null)
                    return;

                //If the player's bottom y is below enemy's bottom y (+30) and the player is not dashing downards, the player is killed by the enemy
                if(gameScreen.getPlayer().getY() < enemy.getY() + 30 && gameScreen.getPlayer().getBody().getLinearVelocity().y > -100)
                    gameScreen.getPlayer().hitByEnemy();
            }
            if(a.getUserData() == ContactType.COIN || b.getUserData() == ContactType.COIN) {
                // Player is in contact with coin
                Coin coin = ((Coin) gameScreen.getMatchingMoneyItem(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y));
                if(coin != null)
                    coin.collect();
            }
        }

        if(a.getUserData() == ContactType.PLAYERBULLET || b.getUserData() == ContactType.PLAYERBULLET) {
            Fixture notBullet = b;
            Fixture bulletFix = a;
            if(b.getUserData() == ContactType.PLAYERBULLET) {
                notBullet = a;
                bulletFix = b;
            }

            // If the bullet hit an enemy, the enemy's lives should be lowered
            if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                Enemy enemy = (Enemy) gameScreen.getMatchingRectangle(notBullet.getBody().getPosition().x, notBullet.getBody().getPosition().y);
                // Fixes bug when player is below enemy outside camera's scope
                if(enemy == null)
                    return;
                enemy.hitByBullet();
            }

            // If the bullet hit the safe, the safe should crack
            if(a.getUserData() == ContactType.SAFE || b.getUserData() == ContactType.SAFE) {
                // Player is standing on safe, find safe and retrieve money.
                ((Safe) gameScreen.getMatchingMoneyItem(notBullet.getBody().getPosition().x, notBullet.getBody().getPosition().y)).collect();
            }

            //After the bullet has hit anything it should be removed, do this by lowering its lives
            Bullet bullet = (Bullet) gameScreen.getMatchingRectangle(bulletFix.getBody().getPosition().x, bulletFix.getBody().getPosition().y);
            // Fixes bug
            if(bullet == null)
                return;
            bullet.lowerLives();
        }

        if(a.getUserData() == ContactType.ENEMYBULLET || b.getUserData() == ContactType.ENEMYBULLET) {
            Fixture bulletFix = a.getUserData() == ContactType.ENEMYBULLET ? a : b;

            if(a.getUserData() == ContactType.PLAYER || b.getUserData() == ContactType.PLAYER) {
                // Player hit by enemy bullet
                gameScreen.getPlayer().hitByEnemy();
            }

            Bullet bullet = (Bullet) gameScreen.getMatchingRectangle(bulletFix.getBody().getPosition().x, bulletFix.getBody().getPosition().y);
            if(bullet == null)
                return;
            bullet.lowerLives();
        }
    }

    /**
     * Starts when contact is ended
     * @param contact contact which is ended
     */
    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a.getUserData() == ContactType.PLAYERSENSOR || b.getUserData() == ContactType.PLAYERSENSOR) {
            // Player no longer on ground or on a rectangle
            gameScreen.getPlayer().setOnGround(false);
            gameScreen.getPlayer().setOnRectangle(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
