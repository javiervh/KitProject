package com.topicos.kitfisica;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

/**
 * @author Matim Development
 * @version 1.0.0
 * <br><br>
 * https://sites.google.com/site/matimdevelopment/
 */
public class SplashTemplate extends BaseGameActivity
{
	private final int CAMERA_WIDTH = 720;
	private final int CAMERA_HEIGHT = 480;
	
	private Camera camera;
	private Scene splashScene;
	private Scene mainScene;
	
    private BitmapTextureAtlas splashTextureAtlas;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;
    
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
        splashTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, this, "splash.png", 0, 0);
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
    	splash = new Sprite(0, 0, splashTextureRegion, mEngine.getVertexBufferObjectManager())
    	{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
    	};
    	
    	splash.setScale(1.5f);
    	splash.setPosition((CAMERA_WIDTH - splash.getWidth()) * 0.5f, (CAMERA_HEIGHT - splash.getHeight()) * 0.5f);
    	splashScene.attachChild(splash);
	}
}
