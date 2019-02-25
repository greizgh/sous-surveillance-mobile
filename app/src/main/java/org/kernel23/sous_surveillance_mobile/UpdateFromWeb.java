package org.kernel23.sous_surveillance_mobile;

/*
 * Mise à jour via le site ...
 * 
 * #1 - Téléchargement du fichier
 * #2 - Mise à jour de la base locale
 * 
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateFromWeb extends AsyncTask<Void, Integer, Void>{

	private Context mContext;
	private ProgressBar mProgressBar;
	
	public UpdateFromWeb(Context context, ProgressBar pb) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mProgressBar = pb;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Toast.makeText(mContext, "Téléchargement et mise à jour de base de données en arrière plan.\n\nCeci peut prendre plusieurs minutes et ralentir votre mobile.",Toast.LENGTH_LONG).show();
		mProgressBar.setVisibility(View.VISIBLE);
	}

	protected Void doInBackground(Void... arg0) {

		long total = 0;
		
		String fileName = "SousSurveillance-update.txt";
		String surveillanceDir = "/sous-surveillance-update/";

		 /*
		  * Téléchargement du dernier fichier
		  */		
		
		// EN COURS DE DEV 
		/*
		try {

			URL url = new URL("http://lyon.sous-surveillance.net/spip.php?page=cameras&format=json&details=2");
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			int lengthOfFile = c.getContentLength();
			
			String PATH = Environment.getExternalStorageDirectory() + surveillanceDir ;
			File file = new File(PATH);
			file.mkdirs();


			File outputFile = new File(file , fileName);
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
				total += len1;
		        

			}
			fos.flush();
			fos.close();
			is.close();

			} catch (IOException e) {
			Log.e("UpdateFromWeb", "IOException", e);
			}

			
		
		*/
		 //File file = mContext.getFileStreamPath(Environment.getExternalStorageDirectory()+ surveillanceDir + fileName);
		 
		
		
		 /*
		 if(file.exists()){
			 try {
				File f=new File(Environment.getExternalStorageDirectory()+ surveillanceDir + fileName);
			//	inputStream = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.e("UpdateFromWeb", "IOException", e);
			}
			
		 }
		 */
		 
		 
		 /*
		  * Le fichier suivant simule le flux Web.
		  * http://lyon.sous-surveillance.net/spip.php?page=cameras&format=json
		  */

		 InputStream inputStream = mContext.getResources().openRawResource(R.raw.sous_surveillance_gps_database);
		
		
		 /*
		  * Mise à jour de la base de données
		  */
		 
		 int count=0;
		 CameraBDD cameraDB = new CameraBDD(mContext);
		 cameraDB.open();
	     
		 
	    
	     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        	
	        int in;
	        int progress;
	        String myStr = "";
	        try {
	        	in = inputStream.read();
	        	while (in != -1)
	        	{
	        		byteArrayOutputStream.write(in);
	        		in = inputStream.read();
	        	}
	        	inputStream.close();
	        	myStr = byteArrayOutputStream.toString();
	        }catch (IOException e) {
	        		Log.e("UpdateFromWeb", "IOException", e);
	        }
	        finally{
	            try {
	            	if(inputStream !=null){
	            		inputStream.close();
	                     }
	            } catch (IOException e) {
	            	Log.e("UpdateFromWeb", "Erreur lors de la fermeture du fichier de donn�es", e);
	           }
	        }
	        //
	        
	        JSONObject fullJObj;
	        JSONObject  geoJson;
	        JSONObject  propJson;
	        JSONArray coordJson; 

			try {
				fullJObj = new JSONObject(myStr);
		        JSONArray jArr = fullJObj.getJSONArray("features");
		       
		        for (int i = 0; i < jArr.length(); ++i ) {			        	

		            JSONObject jObj = jArr.getJSONObject(i);		            
		            String geometry = jObj.getString("geometry");	    	    
		    	    geoJson = new JSONObject(geometry);
		    	    coordJson = geoJson.getJSONArray("coordinates");
		    	    
		    	    String mylong = coordJson.getString(0);
		    	    String mylat  = coordJson.getString(1);
		    	    
		    	    Camera camera = new Camera();
		   	     	camera.setLatitude(mylat);
		   	     	camera.setLongitude(mylong);
		   	     	
		   	     	
		   	     	String properties = jObj.getString("properties");
		   	     	String id_camera;
		   	     	propJson = new JSONObject(properties);
		   	     	id_camera = propJson.getString("id_camera");
		   	     	camera.setSsid(id_camera);
		   	     	cameraDB.insertCamera(camera);
	
	   				
	   				progress = (count*100/jArr.length());
	   				count++;
	   				publishProgress(progress);
	   				
		        }
		  }catch (JSONException e) {
			  	Log.e("UpdateFromWeb", "JSONException", e);
		 }
		 
	     
	     cameraDB.close();
			
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress){
		mProgressBar.setProgress( progress[0]);
	}
	
	@Override
	protected void onPostExecute(Void result) {
		 Toast.makeText(this.mContext, "Mise à jour terminée.",Toast.LENGTH_LONG).show();
		 mProgressBar.setVisibility(View.GONE);
	}
}
