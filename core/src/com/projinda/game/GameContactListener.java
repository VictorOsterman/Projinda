package com.projinda.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.*;
import helper.ContactType;

/**
 * The game's contact listener
 * Checks if player collides with any objects
 */
public class GameContactListener implements ContactListener {

    private GameScreen gameScreen;
    public GameContactListener(GameScreen gameScreen) { this.gameScreen = gameScreen; }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();


        if(a == null || b == null) return;
        if(a.getUserData() == null || b.getUserData() == null) return;

        // If any of the objects is the player
        if(a.getUserData() == ContactType.PLAYER || b.getUserData() == ContactType.PLAYER) {
            // If any of the objects is an enemy
            if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                // Reset the players position
                gameScreen.getPlayer().setReset(true);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
