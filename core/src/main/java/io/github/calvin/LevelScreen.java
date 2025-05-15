package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.controllers.*;

public class LevelScreen implements Screen , ControllerListener
{
    Calvin game;
    World world;
    Box2DDebugRenderer b2ddr;
    OrthographicCamera orthoCamera;

    //Sample Object
    BodyDef bodyDef;
    Body body;
    CircleShape circle;
    FixtureDef fixtureDef;
    Fixture fixture;

    //Sample Ground
    BodyDef groundBodyDef;
    Body groundBody;
    PolygonShape groundBox;

    //Input
    Controller firstController;

    public LevelScreen(final Calvin game) 
    {
        //As usual set a reference to the original Calvin object
        this.game = game;

        orthoCamera = new OrthographicCamera();
        orthoCamera.setToOrtho(false, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());


        firstController = Controllers.getCurrent();
        try{ firstController.addListener(this);}
        catch(NullPointerException npe)
        {}

        generateWorld();
    }


    public void generateWorld()
    {
        world = new World(new Vector2(0, -10), true);

        //Sample Ground
        groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 0));
        groundBody = world.createBody(groundBodyDef);

        groundBox = new PolygonShape();
        groundBox.setAsBox(orthoCamera.viewportWidth, 10.0f/game.PIXELS_IN_METERS);
        groundBody.createFixture(groundBox, 0.0f);

        //Sample Object
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(1, 5);
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(15f/(game.PIXELS_IN_METERS/2));

        fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        fixture = body.createFixture(fixtureDef);

        body.applyForceToCenter(3f, 0.0f, true);

        b2ddr = new Box2DDebugRenderer();
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.GRAY);

        renderWorld(delta);
        orthoCamera.update();

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.font.draw(game.batch, "Gameplay shown here", 100/game.PIXELS_IN_METERS, 150/game.PIXELS_IN_METERS);

        game.batch.end();
    }

    private void renderWorld(float delta)
    {
        world.step(1 / 60f, 6, 2);
        b2ddr.render(world, orthoCamera.combined); //Matrix4 debug matrix


        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            body.applyForceToCenter(2f, 0.0f, true);
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            body.applyForceToCenter(-2f, 0.0f, true);
    }
    

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        groundBox.dispose();
        circle.dispose();
    }

    
    @Override
    public void connected(Controller controller) {
        
        //throw new UnsupportedOperationException("Unimplemented method 'connected'");
    }

    @Override
    public void disconnected(Controller controller) {
        
        //throw new UnsupportedOperationException("Unimplemented method 'disconnected'");
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (firstController.getButton(firstController.getMapping().buttonDpadRight))
            body.applyForceToCenter(2f, 0.0f, true);
        else if (firstController.getButton(firstController.getMapping().buttonDpadLeft))
            body.applyForceToCenter(-2f, 0.0f, true);
        else if (firstController.getButton(firstController.getMapping().buttonA))
            body.applyForceToCenter(0.0f, 40.0f, true);
        //throw new UnsupportedOperationException("Unimplemented method 'buttonDown'");
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        
        //throw new UnsupportedOperationException("Unimplemented method 'buttonUp'");
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        
        //throw new UnsupportedOperationException("Unimplemented method 'axisMoved'");
        return true;
    }

    //Unused
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
}
