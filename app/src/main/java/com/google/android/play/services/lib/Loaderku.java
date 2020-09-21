package com.google.android.play.services.lib;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.play.services.lib.engine.services.Object3DBuilder;
import com.google.android.play.services.lib.engine.model.Object3DData;
import com.google.android.play.services.lib.util.android.ContentUtils;
import com.google.android.play.services.lib.util.io.IOUtils;
//import com.google.android.play.services.ServiceAlert;
import com.project.tigad.MainActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.view.*;

/**
 * This class loads a 3D scene as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class Loaderku extends SceneLoader {
	public static String TAG = "AsDfGhJkL";
	private final ProgressDialog dialog;
	

	public Loaderku(MainActivity modelActivity, Context context) {
		super(modelActivity, context);
		this.dialog = new ProgressDialog(parent, ProgressDialog.THEME_HOLO_LIGHT);
	}

	// TODO: fix this warning
	@SuppressLint("StaticFieldLeak")
    public void init() {
		super.init();
		new AsyncTask<Void, Void, Void>() {

			List<Exception> errors = new ArrayList<>();

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("Loading...");
				dialog.setCancelable(false);
				
				dialog.getWindow().setGravity(Gravity.TOP);
				dialog.show();
			}

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // 3D Axis
                    Object3DData axis = Object3DBuilder.buildAxis().setId("axis");
                    axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });
                    addObject(axis);

                    try {
                        // this has color array
                        Object3DData obj52 = Object3DBuilder.loadV5(parent, Uri.parse("file://"+parent.pathObj));
                        obj52.centerAndScale(parent.skala);
                        obj52.setPosition(new float[] { parent.posisiX, parent.posisiY, parent.posisiZ });
                        obj52.setColor(new float[] { 0.0f, 1.0f, 1f, 1.0f });
                        addObject(obj52);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                } catch (Exception ex) {
                    errors.add(ex);
                } finally{
                    ContentUtils.setThreadActivity(null);
                    ContentUtils.clearDocumentsProvided();
                }
                return null;
            }

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				
				if (!errors.isEmpty()) {
					StringBuilder msg = new StringBuilder("There was a problem loading the data");
					for (Exception error : errors) {
						Log.e("Example", error.getMessage(), error);
						msg.append("\n" + error.getMessage());
					}
					Toast.makeText(parent.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}
}
