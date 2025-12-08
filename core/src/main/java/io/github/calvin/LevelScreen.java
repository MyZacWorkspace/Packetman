package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Iterator;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
//TILED MAP Stuff
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class LevelScreen implements Screen, ControllerListener, ContactListener
 {
    Calvin game;
    World world;
    Box2DDebugRenderer b2ddr;
    OrthographicCamera orthoCamera;
    float totalElapsedTime = 0.0f;
	
	float fallHeight = -10.0f;

    //Sprites
    PlayerSprite player;
    long score;
	
	AnimatedSprite homeNetwork;
	
	Array<EnemySprite> enemies;

    //Sample Player World-Object
    BodyDef playerBodyDef;
    Body playerBody;
    PolygonShape playerShape;
    FixtureDef playerFixtureDef;
    Fixture playerFixture;

    //Sample Enemy
    BodyDef enemyBodyDef;
    Array<Body> enemyBodies;
    PolygonShape enemyShape;
    FixtureDef enemyFixtureDef;
    Fixture enemyFixture;

    //Sample Game World-Objects (Physics)
    Array<BodyDef> gameObjectBodyDefs;
    Array<Body> gameBodies;
    Array<PolygonShape> gameShapes;

    //Input
    Controller firstController;

    //Tiled Map
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    float MAP_SCALE_MODIFIER = 60.0f;

    float tiledMapScale;

    Sprite hitBox;
    Sprite hitBox2;
    Sprite hurtBox;

    //Game Completion
    static int livesLeft;

    AnimatedSprite wwweb;

    public LevelScreen(final Calvin game, Controller control) {
        //As usual set a reference to the original Calvin object
        this.game = game;

        orthoCamera = new OrthographicCamera();
        orthoCamera.setToOrtho(false, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());


        tiledMapScale = game.PIXELS_IN_METERS - MAP_SCALE_MODIFIER;

        tiledMap = new TmxMapLoader().load("packetmandemo.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap,
                1.0f / (tiledMapScale));
				
		
		if(control != null)
		{
			
			firstController = control;
			try 
			{ firstController.addListener(this);} 
			catch (NullPointerException npe) {}
		}
        else
        {
            Controllers.addListener(this);
            firstController = Controllers.getCurrent();
            try
            { firstController.addListener(this);}
            catch (NullPointerException npe) {
                System.err.println();
            }
        }
		
        generateSprites();
        generateWorld();
    }

    public void generateSprites() 
    {
        player = new PlayerSprite(5.0f, 5.0f);
        wwweb = new AnimatedSprite("sprites/worldwideweb.atlas", 0.0f, 0.0f, 0.2f, 12.0f, 0.0f, 5.0f);
		homeNetwork = new AnimatedSprite("sprites/homenetwork.atlas", 0.0f, 0.0f, 0.2f, 12.0f, 47.363f, 3.427f);

        enemies = new Array<EnemySprite>();
		EnemySprite enemy1 = new EnemySprite("sprites/malware.atlas", 9.655f, 3.421f);
        //EnemySprite enemy2 = new EnemySprite("sprites/malware.atlas", 23.3f, 6.621f);
        EnemySprite enemy3 = new EnemySprite("sprites/malware.atlas", 28.0619f, 1.45f);
        //EnemySprite enemy4 = new EnemySprite("sprites/malware.atlas", 35.780f, 3.421f);
        enemies.add(enemy1, enemy3);
    }

    public void generateWorld() 
    {
        world = new World(new Vector2(0, -10), true);

        MapProperties globalMapProperties = tiledMap.getProperties();

        //Layer for Physics Collisions
        MapLayer objectLayer = tiledMap.getLayers().get("Object Layer 1");
        MapObjects gameMapObjects = objectLayer.getObjects();
        MapObject currentObject;
        MapProperties currentObjectProps;
    
        gameObjectBodyDefs = new Array<BodyDef>();
        gameBodies = new Array<Body>();
        gameShapes = new Array<PolygonShape>();
        //CONSTRUCT ALL RECTANGULAR OBJECTS from the OBJECT LAYER
        //Because Tiled Map is like a big sprite, divide by 2 for proper adjustment
        for(int o = 0; o < gameMapObjects.getCount() ; o++)
        {
            currentObject = gameMapObjects.get(o);
            currentObjectProps = currentObject.getProperties();

            Iterator iteration = currentObjectProps.getValues();
            while (iteration.hasNext())
            {
              System.out.println(iteration.next());
            }
            gameObjectBodyDefs.add(new BodyDef());
            gameObjectBodyDefs.get(o).position.set(
                    (float) currentObjectProps.get("x") / (tiledMapScale) + ( (float) currentObjectProps.get("width") / 2/ (tiledMapScale)),
                    (float) currentObjectProps.get("y") / (tiledMapScale) + ((float) currentObjectProps.get("height")/ 2 / (tiledMapScale)));
            gameBodies.add(world.createBody(gameObjectBodyDefs.get(o)));

            gameShapes.add(new PolygonShape());

            gameShapes.get(o).setAsBox(
                    ((float) currentObjectProps.get("width") /2) / (tiledMapScale),
                    ((float) currentObjectProps.get("height") /2) / (tiledMapScale));
            gameBodies.get(o).createFixture(gameShapes.get(o), 0.0f);    
        }

        //Sample Player World-Object
        playerBodyDef = new BodyDef();
        playerBodyDef.type = BodyType.DynamicBody;
        playerBodyDef.position.set(player.getX(), player.getY());
        playerBody = world.createBody(playerBodyDef);
        playerBody.setFixedRotation(true);

        playerShape = new PolygonShape();
        //This just works -- dividing by half the in game PIXELS-to-METERS Ratio
        playerShape.setAsBox(player.getWidth() / 2 / (game.PIXELS_IN_METERS / 2),
                player.getHeight() / (game.PIXELS_IN_METERS / 2 ));

        playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = playerShape;
        playerFixtureDef.density = 0.33f;
        playerFixtureDef.friction = 0.4f;
        playerFixtureDef.restitution = 0.0f;
        playerFixture = playerBody.createFixture(playerFixtureDef);
        playerShape.dispose();
        
        //Enemy Shape
        enemyBodies = new Array<Body>();
        for(int i = 0 ; i < enemies.size ; i++)
        {
            enemyBodyDef = new BodyDef();
            enemyBodyDef.type = BodyType.DynamicBody;
            enemyBodyDef.position.set(enemies.get(i).getX() , enemies.get(i).getY());
            enemyBodies.add(world.createBody(enemyBodyDef));
            enemyBodies.get(i).setFixedRotation(true);

            enemyShape = new PolygonShape();
            enemyShape.setAsBox(enemies.get(i).getWidth()  / 2, (enemies.get(i).getHeight() / 2) - 0.1f);
            enemyFixtureDef = new FixtureDef();
            enemyFixtureDef.shape = enemyShape;
            enemyFixtureDef.density = 0.77f;
            enemyFixtureDef.friction = 0.5f;
            enemyFixtureDef.restitution = 0.0f;
            enemyFixture = enemyBodies.get(i).createFixture(enemyFixtureDef);
            enemyShape.dispose();
        }
       

        b2ddr = new Box2DDebugRenderer();
    }

  
    @Override
    public void render(float delta) {
        totalElapsedTime += delta;

        ScreenUtils.clear(Color.BLACK);

        renderWorld(delta);

        scrollCamera();
        orthoCamera.update();
        tiledMapRenderer.setView(orthoCamera);
        tiledMapRenderer.render();

        game.batch.begin();

        game.viewport.apply();

        game.batch.setProjectionMatrix(orthoCamera.combined);

        player.draw(game.batch);
		homeNetwork.draw(game.batch);
        wwweb.draw(game.batch);

        for(EnemySprite es : enemies)
        {
            es.draw(game.batch);
        }

        game.hud_viewport.apply();
        game.batch.setProjectionMatrix(game.hud_viewport.getCamera().combined);
        game.font.draw(game.batch, "Lives Left: " + LevelScreen.livesLeft,
                0.25f, 7.5f);
        game.font.draw(game.batch, "Time: " + totalElapsedTime, 8.0f, 7.5f);
        
        game.batch.end();
	  

        //System.out.println("Player Y-Velocity: " + playerBody.getLinearVelocity().y);
        updateEntities(totalElapsedTime, delta);
    }

    private void renderWorld(float delta) 
    {
        world.step(1 / 60f, 6, 2);

        player.setPosition(playerBody.getPosition().x - player.getWidth() / 2 / 25,
                (playerBody.getPosition().y - player.getHeight() / 2 / 25));

        for(int i = 0 ; i < enemies.size ; i++)
        {
            enemies.get(i).setPosition(enemyBodies.get(i).getPosition().x - enemies.get(i).getWidth()/2,
                (enemyBodies.get(i).getPosition().y - enemies.get(i).getHeight() / 2) + 0.1f);
        }
        
        //Hide box2d rendering
        b2ddr.render(world, orthoCamera.combined); // Matrix4 debug matrix
    }


    private void scrollCamera()
    {
        float horizontalDisplacement = 0.0f;
        float verticalDisplacement = 0.0f;
        //On first frame

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            orthoCamera.translate(0.0f, -0.1f, 0.0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            orthoCamera.translate(0.0f, 0.1f, 0.0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            orthoCamera.translate(0.1f, 0.0f, 0.0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            orthoCamera.translate(-0.1f, 0.0f, 0.0f);
        }
        
        if(player.initialPosition == null)
        {
            player.setInitialPosition(new Vector2(playerBody.getPosition().x, playerBody.getPosition().y));
        }
        else
        {
            player.setRecentPosition(new Vector2(playerBody.getPosition().x, playerBody.getPosition().y));
            //orthoCamera.translate(horizontalDisplacement, 0.0f, 0.0f);
            
            horizontalDisplacement = player.getRecentPosition().x - player.getInitialPosition().x;
            verticalDisplacement = player.getRecentPosition().y - player.getInitialPosition().y;
           
            orthoCamera.translate(horizontalDisplacement, verticalDisplacement, 0.0f);
            player.setInitialPosition(player.getRecentPosition());
            player.setRecentPosition(null);
        }
    }

    private void updateEntities(float totalElapsedTime, float delta) 
    {
        boolean isPlayerToTheRightOfEnemy;
        //End conditions
        Rectangle playerRect = player.getBoundingRectangle();
        Rectangle victoryRect = homeNetwork.getBoundingRectangle();
        
        
		if(playerRect.overlaps(victoryRect))
		{
			firstController.removeListener(this);
		    Controllers.removeListener(this);
            //game.dispose();
            game.setScreen(new VictoryScreen(game, firstController, totalElapsedTime));
		}
	    if(player.getY() <= fallHeight)
        {
            LevelScreen.livesLeft--;
            if (livesLeft > 0)
            {
                //FIXME Defeat animation
                firstController.removeListener(this);
                game.setScreen(new LevelScreen(game, firstController));
            }
            else
		        endGame();
	    }
        
        for(EnemySprite enemy : enemies)
        {

            if(player.getBoundingRectangle().overlaps(enemy.getBoundingRectangle()))
            {
                LevelScreen.livesLeft--;
                if (livesLeft > 0)
                {
                //FIXME Defeat animation
                    firstController.removeListener(this);
                    game.setScreen(new LevelScreen(game, firstController));
                }
                else
		            endGame();
            }
        }
        wwweb.update(totalElapsedTime);
        homeNetwork.update(totalElapsedTime);
        //Run through Automata
        //FIXME -- Be careful of comparing floating points
        //And rounding with doubles
        //Only run through the automata if the final state has not been reached
        if (player.jumpRecog != JumpStates.TWO_F)
        {
            float currentPlayerY_Velocity = playerBody.getLinearVelocity().y;
            if (currentPlayerY_Velocity != 0.0f) {
                player.jumpRecog = JumpStates.NONE;
            } else if (Math.abs(currentPlayerY_Velocity) < 0.01) //Zero y_velocity found
            {
                if (player.jumpRecog == JumpStates.NONE)
                    player.jumpRecog = JumpStates.ONCE;
                else if (player.jumpRecog == JumpStates.ONCE)
                {
                    player.jumpRecog = JumpStates.TWO_F; // The final state
                    player.isAllowedToJump = true;
                }
            }
        }

        if (player.jumpRecog == JumpStates.TWO_F) //This means you are grounded
        { // You will always remain in the NONE state in the air!
            if (player.isInputRight) 
            {
                playerBody.setLinearVelocity(2.0f, playerBody.getLinearVelocity().y);
            } else if (player.isInputLeft) 
            {
                playerBody.setLinearVelocity(-2.0f, playerBody.getLinearVelocity().y);
            }
        }

        //System.out.println("Is the player running?: " + player.isRunning);
        //Now do speed
        /*
        if(player.isRunning)
        {
            if(player.isFacingRight)
                playerBody.applyForceToCenter(player.speedAccel, 0.0f,false);
            else
                playerBody.applyForceToCenter(-player.speedAccel, 0.0f,false);
        }
        */
           

        player.update(totalElapsedTime, delta);

        for(int i = 0; i < enemies.size ; i++)
        {
            enemies.get(i).setDistanceFromPlayer(player.getPositionV2().dst(enemies.get(i).getPositionV2()));
            if (enemies.get(i).getDistanceFromPlayer() < 2.0f)
            {
                if (playerBody.getPosition().x < enemyBodies.get(i).getPosition().x) {
                    isPlayerToTheRightOfEnemy = false;
                    enemyBodies.get(i).setLinearVelocity(-1.0f, enemyBodies.get(i).getLinearVelocity().y);
                } else {
                    isPlayerToTheRightOfEnemy = false;
                    enemyBodies.get(i).setLinearVelocity(1.0f, enemyBodies.get(i).getLinearVelocity().y);
                }
            }

            enemies.get(i).update(totalElapsedTime, playerBody.getWorldCenter());
        }
    }
	
	//End Game
	private void endGame()
	{
		firstController.removeListener(this);
		Controllers.removeListener(this);
        game.dispose();
		game.create();
	}
	
	
	
    //Controller Support
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        controller = firstController;

        if(buttonCode == controller.getMapping().buttonL1)
        { 
            System.out.println("Location: " + "X: " + playerBody.getPosition().x + " Y: " + playerBody.getPosition().y);
        }

        //Jump Button
        if (controller.getButton(controller.getMapping().buttonA) && player.isAllowedToJump)
        {
            playerBody.applyForceToCenter(0.0f, 125.0f, false);
            player.isAllowedToJump = false;
            player.jumpRecog = JumpStates.NONE; //Look for the pattern once more
            player.isJumpAnimationActive = true;
        }

        
        if(controller.getButton(controller.getMapping().buttonDpadRight))
        {
            player.isInputRight = true;
        }
        else if (controller.getButton(controller.getMapping().buttonDpadLeft)) {
            player.isInputLeft = true;
        }

        //FIXME Ask Libgdx community, why was this giving so much trouble!

        /*
        if(player.jumpRecog == JumpStates.TWO_F && (player.isInputRight || player.isInputLeft) && buttonCode == controller.getMapping().buttonY ||
            buttonCode == controller.getMapping().buttonX)
        {
            player.isRunning = true;
        }
        */


        return true;
    }
	

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {

        controller = firstController;

        if (buttonCode == controller.getMapping().buttonDpadRight) {
            player.isInputRight = false;
        } else if (buttonCode == controller.getMapping().buttonDpadLeft) {
            player.isInputLeft = false;
        }

        /*
        if(buttonCode == controller.getMapping().buttonY ||
             buttonCode == controller.getMapping().buttonX)
        {
            player.isRunning = false;
        }
        */

        return false;
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        game.hud_viewport.update(width, height, true);
    }

    @Override
    public void connected(Controller controller) {
		System.out.println("Connection ESTABLISHED!");
		try {
            controller.addListener(this);
        } catch (NullPointerException npe) {
        }
		firstController = controller;
    }

    @Override
    public void disconnected(Controller controller) {

		System.out.println("DISCONNECTED");
        //throw new UnsupportedOperationException("Unimplemented method 'disconnected'");
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {

        //throw new UnsupportedOperationException("Unimplemented method 'axisMoved'");
        return true;
    }

	//FIXME memory optimization needed
	//Throw out all resources and objects when necessary
	@Override
    public void dispose() {
        for (int d = 0; d < gameShapes.size; d += 0)
        {
            gameShapes.get(d).dispose();
        }
    }
	
    //Unimplemented from Screen
    @Override
    public void show() 
	{}
    @Override
    public void hide() 
	{}
    @Override
    public void pause() 
	{}
    @Override
    public void resume() 
	{}

    @Override
    public void beginContact(Contact contact) {
        
        if(contact.getFixtureA().equals(playerFixture))
        {

        }
        throw new UnsupportedOperationException("Unimplemented method 'beginContact'");
    }

    @Override
    public void endContact(Contact contact) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'endContact'");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'preSolve'");
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'postSolve'");
    }
}
