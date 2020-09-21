package com.project.tigad;

import android.app.*;
import android.os.*;
import android.net.*;
import com.google.android.play.services.lib.*;
import android.widget.*;
import android.widget.ActionMenuView.LayoutParams;
import android.graphics.*;
import android.view.*;
import android.content.*;

public class MainActivity extends Activity 
{
	public static String pathObj = "";
    public static float animeCameraX = 0;
    public static float animeCameraY = 0;
    public static float animeZoom = 0;
    public static float animeRotasi = 0;
    public static float posisiX = 0;
    public static float posisiY = 0;
    public static float posisiZ = 0;
    public static float skala = 0;
    public static boolean zoomRotasi = false;
    public static boolean posisi = false;

	private static final int REQUEST_CODE_LOAD_TEXTURE = 1000;
    private int paramType;
    private Uri paramUri;
    private boolean immersiveMode = true;
    public ModelSurfaceView gLView;
    private Loaderku scene;
    private Handler handler;
	
	private LinearLayout layoutView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		zoomRotasi = true;
		posisi = true;
		setPath("/sdcard/teapot.obj");
		skala = 4;
		setCamera(0.0023f, 0.001f);
		setCameraZoom(8);
		setCameraRotasi(7);
		setPosisi(0, 0, 0);
		
		LinearLayout.LayoutParams params3d = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		handler = new Handler(getMainLooper());
		scene = new Loaderku(this, this);
		scene.init();
		gLView = new ModelSurfaceView(this);

		layoutView = new LinearLayout(this);
		layoutView.setBackgroundColor(Color.TRANSPARENT);
		layoutView.addView(gLView, params3d);
		
        setContentView(layoutView);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
			case R.id.model_load_objects:
                alertObject();
                break;
            case R.id.model_toggle_wireframe:
                scene.toggleWireframe();
                break;
            case R.id.model_toggle_boundingbox:
                scene.toggleBoundingBox();
                break;
            case R.id.model_toggle_textures:
                scene.toggleTextures();
                break;
            case R.id.model_toggle_animation:
                scene.toggleAnimation();
                break;
            case R.id.model_toggle_smooth:
				Toast.makeText(this, "update later...", Toast.LENGTH_LONG).show();
                //scene.toggleSmooth();
                break;
            case R.id.model_toggle_collision:
                scene.toggleCollision();
                break;
            case R.id.model_toggle_lights:
                scene.toggleLighting();
                break;
            case R.id.model_toggle_stereoscopic:
                //scene.toggleStereoscopic();
				Toast.makeText(this, "update later...", Toast.LENGTH_LONG).show();
				
                break;
            case R.id.model_toggle_blending:
                //scene.toggleBlending();
				Toast.makeText(this, "update later...", Toast.LENGTH_LONG).show();
				
                break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void alertObject() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        builder1.setTitle("Object manager");
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_object, null);

        final EditText edtCamera = (EditText)layout.findViewById(R.id.edtCamera);
        final EditText edtCameraZoom = (EditText)layout.findViewById(R.id.edtCameraZoom);
        final EditText edtCameraRotasi = (EditText)layout.findViewById(R.id.edtCameraRotasi);
        final EditText edtPosisi = (EditText)layout.findViewById(R.id.edtPosisi);
        final EditText edtSkala = (EditText)layout.findViewById(R.id.edtSkala);
        final Button btnObject = (Button)layout.findViewById(R.id.btnPathObject);
        
        btnObject.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);

					builderIndex.setTitle("Metode");
					builderIndex.create().show();
				}
			});

        builder1.setPositiveButton("Exsekusi", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					setCameraZoom(Float.parseFloat(edtCameraZoom.getText().toString()));
					Toast.makeText(MainActivity.this, "ekese", Toast.LENGTH_LONG).show();
				}
			});

        builder1.setView(layout);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
	
	public void setCamera(float dx, float dy) {
        this.animeCameraX = dx;
        this.animeCameraY = dy;
    }
    public void setCameraZoom(float vector) {
        this.animeZoom = vector;
    }
    public void setCameraRotasi(float pi) {
        this.animeRotasi = pi;
    }
    public void setPosisi(float x, float y, float z) {
        this.posisiX = x;
        this.posisiY = y;
        this.posisiZ = z;
    }
    public void setPath(String path) {
        this.pathObj = path;
    }


	public Uri getParamUri() {
        return paramUri;
    }

    public int getParamType() {
        return paramType;
    }

    public Loaderku getScene() {
        return scene;
    }

    public ModelSurfaceView getGLView() {
        return gLView;
    }
	
}

