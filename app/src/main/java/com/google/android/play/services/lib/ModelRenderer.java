package com.google.android.play.services.lib;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.google.android.play.services.lib.engine.animation.Animator;
import com.google.android.play.services.lib.engine.drawer.DrawerFactory;
import com.google.android.play.services.lib.engine.model.Camera;
import com.google.android.play.services.lib.engine.model.AnimatedModel;
import com.google.android.play.services.lib.engine.model.Object3D;
import com.google.android.play.services.lib.engine.services.Object3DBuilder;
import com.google.android.play.services.lib.engine.model.Object3DData;
import com.google.android.play.services.lib.engine.drawer.Object3DImpl;
import com.google.android.play.services.lib.util.android.GLUtil;
//import com.google.android.play.services.ServiceAlert;
import com.project.tigad.MainActivity;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelRenderer implements GLSurfaceView.Renderer {

	private final static String TAG = "AsDfGhJkL";
	private MainActivity main;
	private int width;
	private int height;
	private static final float near = 1f;
	private static final float far = 100f;
	private DrawerFactory drawer;
	private Map<Object3DData, Object3DData> wireframes = new HashMap<Object3DData, Object3DData>();
	private Map<byte[], Integer> textures = new HashMap<byte[], Integer>();
	private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<Object3DData, Object3DData>();
	private Map<Object3DData, Object3DData> normals = new HashMap<Object3DData, Object3DData>();
	private Map<Object3DData, Object3DData> skeleton = new HashMap<>();
	private final float[] modelProjectionMatrix = new float[16];
	private final float[] modelViewMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] lightPosInEyeSpace = new float[4];
	private boolean infoLogged = false;
	private Animator animator = new Animator();

	public ModelRenderer() {
		// NOTHING
	}

	public ModelRenderer(MainActivity main) {
		this.main = main;
	}

	public float getNear() {
		return near;
	}

	public float getFar() {
		return far;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0, 0, 0, 0);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		drawer = new DrawerFactory();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;

		GLES20.glViewport(0, 0, width, height);

		SceneLoader scene = main.getScene();
		Camera camera = scene.getCamera();
		Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
				camera.zView, camera.xUp, camera.yUp, camera.zUp);

		// the projection matrix is the 3D virtual space (cube) that we want to project
		float ratio = (float) width / height;
		Log.d(TAG, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
		Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
	}

	@Override
	public void onDrawFrame(GL10 unused) {

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		SceneLoader scene = main.getScene();
		if (scene == null) {
			// scene not ready
			return;
		}

		// recalculate mvp matrix according to where we are looking at now
		Camera camera = scene.getCamera();

		camera.translateCamera(main.animeCameraX, main.animeCameraY);
		if (main.zoomRotasi) {
			//camera.MoveCameraZ(main.animeZoom);
			//camera.Rotate(main.animeRotasi);

			Log.i(TAG, "zzzzzzzzzz");
			main.zoomRotasi = false;
		}

		if (camera.hasChanged()) {
			Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
					camera.zView, camera.xUp, camera.yUp, camera.zUp);
			// Log.d("Camera", "Changed! :"+camera.ToStringVector());
			Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
			camera.setChanged(false);
		}

		List<Object3DData> objects = scene.getObjects();
		for (int i=0; i<objects.size(); i++) {
			Object3DData objData = null;
			try {
				objData = objects.get(i);
				boolean changed = objData.isChanged();

				Object3D drawerObject = drawer.getDrawer(objData, scene.isDrawTextures(), scene.isDrawLighting(), scene.isDrawAnimation());

				if (main.posisi) {
					objData.setPosition(new float[] { main.posisiX, main.posisiY, main.posisiZ });
					main.posisi = false;
				}

				if (!infoLogged) {
					Log.i("ModelRenderer","Using drawer "+drawerObject.getClass());
					infoLogged = true;
				}

				Integer textureId = textures.get(objData.getTextureData());
				if (textureId == null && objData.getTextureData() != null) {
					Log.i("ModelRenderer","Loading GL Texture...");
					ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
					textureId = GLUtil.loadTexture(textureIs);
					textureIs.close();
					textures.put(objData.getTextureData(), textureId);
				}

				if (objData.getDrawMode() == GLES20.GL_POINTS){
					Object3DImpl lightBulbDrawer = (Object3DImpl) drawer.getPointDrawer();
					lightBulbDrawer.draw(objData,modelProjectionMatrix, modelViewMatrix, GLES20.GL_POINTS,lightPosInEyeSpace);
				} else if (scene.isAnaglyph()){
				// TODO: implement anaglyph
				} else if (scene.isDrawWireframe() && objData.getDrawMode() != GLES20.GL_POINTS
						&& objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP
						&& objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
					// Log.d("ModelRenderer","Drawing wireframe model...");
					try{
						// Only draw wireframes for objects having faces (triangles)
						Object3DData wireframe = wireframes.get(objData);
						if (wireframe == null || changed) {
							Log.i("ModelRenderer","Generating wireframe model...");
							wireframe = Object3DBuilder.buildWireframe(objData);
							wireframes.put(objData, wireframe);
						}
						drawerObject.draw(wireframe,modelProjectionMatrix,modelViewMatrix,wireframe.getDrawMode(),
								wireframe.getDrawSize(),textureId != null? textureId:-1, lightPosInEyeSpace);
					}catch(Error e){
						Log.e("ModelRenderer",e.getMessage(),e);
					}
				} else if (scene.isDrawPoints() || objData.getFaces() == null || !objData.getFaces().loaded()){
					drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix
							,GLES20.GL_POINTS, objData.getDrawSize(),
							textureId != null ? textureId : -1, lightPosInEyeSpace);
				} else if (scene.isDrawSkeleton() && objData instanceof AnimatedModel && ((AnimatedModel) objData)
						.getAnimation() != null){
					Object3DData skeleton = this.skeleton.get(objData);
					if (skeleton == null){
						skeleton = Object3DBuilder.buildSkeleton((AnimatedModel) objData);
						this.skeleton.put(objData, skeleton);
					}
					animator.update(skeleton);
					drawerObject = drawer.getDrawer(skeleton, false, scene.isDrawLighting(), scene
                            .isDrawAnimation());
					drawerObject.draw(skeleton, modelProjectionMatrix, modelViewMatrix,-1, lightPosInEyeSpace);
				} else {
					drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix,
							textureId != null ? textureId : -1, lightPosInEyeSpace);
				}

				// Draw bounding box
				if (scene.isDrawBoundingBox() || scene.getSelectedObject() == objData) {
					Object3DData boundingBoxData = boundingBoxes.get(objData);
					if (boundingBoxData == null || changed) {
						boundingBoxData = Object3DBuilder.buildBoundingBox(objData);
						boundingBoxes.put(objData, boundingBoxData);
					}
					Object3D boundingBoxDrawer = drawer.getBoundingBoxDrawer();
					boundingBoxDrawer.draw(boundingBoxData, modelProjectionMatrix, modelViewMatrix, -1, null);
				}

				// Draw normals
				if (scene.isDrawNormals()) {
					Object3DData normalData = normals.get(objData);
					if (normalData == null || changed) {
						normalData = Object3DBuilder.buildFaceNormals(objData);
						if (normalData != null) {
							// it can be null if object isnt made of triangles
							normals.put(objData, normalData);
						}
					}
					if (normalData != null) {
						Object3D normalsDrawer = drawer.getFaceNormalsDrawer();
						normalsDrawer.draw(normalData, modelProjectionMatrix, modelViewMatrix, -1, null);
					}
				}
				// TODO: enable this only when user wants it
				// obj3D.drawVectorNormals(result, modelViewMatrix);
			} catch (Exception ex) {
				Log.e("ModelRenderer","There was a problem rendering the object '"+objData.getId()+"':"+ex.getMessage(),ex);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float[] getModelProjectionMatrix() {
		return modelProjectionMatrix;
	}

	public float[] getModelViewMatrix() {
		return modelViewMatrix;
	}
}
