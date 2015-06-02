package com.topicos.kitfisica;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

/**
 * @author Matim Development
 * @version 1.0.0
 * <br><br>
 * https://sites.google.com/site/matimdevelopment/
 */
public class SceneManager extends BaseGameActivity
{
	private final int CAMERA_WIDTH = 480;
	private final int CAMERA_HEIGHT = 320;
	
	private Camera camera;
	private Scene splashScene;
	private Scene mainScene;
	
    private BuildableBitmapTextureAtlas splashTextureAtlas;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;
    private TiledTextureRegion mHelicopterTextureRegion;
	private TiledTextureRegion mBananaTextureRegion;
	
	private enum SceneType
	{
		SPLASH,
		MAIN,
		OPTIONS,
		WORLD_SELECTION,
		LEVEL_SELECTION,
		CONTROLLER
	}
	
	private SceneType currentScene = SceneType.SPLASH;
	
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mHelicopterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.splashTextureAtlas, this, "helicopter_tiled.png", 2, 2);
		this.mBananaTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.splashTextureAtlas, this, "banana_tiled.png", 4, 2);
		
        splashTextureAtlas.load();
       
        pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		initSplashScene();
        pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                loadResources();
                loadScenes();         
                splash.detachSelf();
                mEngine.setScene(mainScene);
                currentScene = SceneType.MAIN;
            }
		}));
  
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
	    {	    	
	    	switch (currentScene)
	    	{
	    		case SPLASH:
	    			break;
	    		case MAIN:
	    			System.exit(0);
	    			break;
	    	}
	    }
	    return false; 
	}
	
	public void loadResources() 
	{
		// Load your game resources here!
	}
	
	private void loadScenes()
	{
		// load your game here, you scenes
		mainScene = new Scene();
		mainScene.setBackground(new Background(50, 50, 50));
	}
	
	// ===========================================================
	// INITIALIZIE  
	// ===========================================================
	
	private void initSplashScene()
	{
    	splashScene = new Scene();
    	AnimatedSprite helicopter = new AnimatedSprite(320, 50, mHelicopterTextureRegion, getVertexBufferObjectManager());
		helicopter.animate(new long[] { 100, 100 }, 1, 2, true);
		//helicopter.registerEntityModifier(new MoveModifier(30, 0, CAMERA_WIDTH - helicopter.getWidth(), 0, CAMERA_HEIGHT - helicopter.getHeight()));
		splashScene.attachChild(helicopter);
		
		AnimatedSprite banana = new AnimatedSprite(400, 220, mBananaTextureRegion, getVertexBufferObjectManager());
		banana.animate(100);
		splashScene.attachChild(banana);
    	//splashScene.attachChild(splash);
	}
}
