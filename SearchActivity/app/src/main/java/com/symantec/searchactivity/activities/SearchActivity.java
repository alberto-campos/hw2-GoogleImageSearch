package com.symantec.searchactivity.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.symantec.searchactivity.adapters.ImageResultArrayAdapter;
import com.symantec.searchactivity.models.ImageResult;
import com.symantec.searchactivity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class SearchActivity extends ActionBarActivity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<ImageResult> imageResults;
    ImageResultArrayAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        imageResults = new ArrayList<>();
        imageAdapter = new ImageResultArrayAdapter(this, imageResults);
        gvResults.setAdapter(imageAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Create an intent to display the full screen image
                Intent intent = new Intent(getApplicationContext(), ImageDisplayActivity.class);

                // Get the image to display
                ImageResult imageResult = imageResults.get(position);

                // Pass image result to the intent
               // intent.putExtra("url", imageResult.getFullUrl());
                intent.putExtra("result", imageResult);

                // Launch the activity
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onImageSearch(View view) {
        String query = etQuery.getText().toString();
        // Toast.makeText(this, "Serching for... " + query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();


        // Get https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
        // Get Request
        client.get("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&q=" +query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageJsonResults;
                try {
                    imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
                    imageAdapter.clear();
                    imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                  //  Log.d("DEBUG", imageJsonResults.toString());
                    imageAdapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                // super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }
}
