package com.symantec.searchactivity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

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


//public class SearchActivity extends ActionBarActivity {

public class SearchActivity extends AppCompatActivity {
    //EditText etQuery;
    SearchView etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<ImageResult> imageResults;
    ImageResultArrayAdapter imageAdapter;

    private int startAt;
    private int currentPage;
    private static int MAX_PAGINATION = 8;
    private static int R_SIZE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        setupTabs();
        resetSearch();
        setupViews();
    }

    private void setupTabs() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }


    public void setSettings() {
        SharedPreferences settings = getSharedPreferences("ImageSearchSettings", 0);
        String size = settings.getString("size", null);
        SharedPreferences.Editor editor = settings.edit();

        if (size == null ) {
            // First time saving settings
        }
        else {
            // User saved settings
            editor.putString("size", "all");
            editor.putString("color", "all");
            editor.commit();
        }
    }

    public void setupViews() {
        //etQuery = (EditText) findViewById(R.id.etQuery);
        etQuery = (SearchView) findViewById(R.id.action_search);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        imageResults = new ArrayList<>();
        imageAdapter = new ImageResultArrayAdapter(this, imageResults);

        gvResults.setAdapter(imageAdapter);

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (page < 8) {
                    customLoadMoreDataFromApi(page);
                } else {
                    // No more results to show
                }
            }
        });

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Create an intent to display the full screen image
                Intent intent = new Intent(getApplicationContext(), ImageDisplayActivity.class);

                // Get the image to display
                ImageResult imageResult = imageResults.get(position);

                // Pass image result to the intent
                intent.putExtra("result", imageResult);

                // Launch the activity
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {



        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        // Retrieve the action-view from menu
        View v = MenuItemCompat.getActionView(actionViewItem);
        // Find the button within action-view
        Button b = (Button) v.findViewById(R.id.btnSearch);
        // Handle button click here
        Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: perform query here
                Toast.makeText(getApplicationContext(), "Entered text in action bar", Toast.LENGTH_SHORT).show();
                resetSearch();
                setupViews();
                retrieveImages();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
        //return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(this, "Item selected" + item.toString(), Toast.LENGTH_SHORT).show();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String constructQueryString() {
        String search_url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0";
        String var_size = "&rsz=" + R_SIZE;
        String query = etQuery.getQuery().toString();
        //String query = etQuery.getText().toString();
        String var_query = "&q=" + query;
        String var_start = "&start=" + (startAt);

        return search_url + var_size + var_query + var_start;
    }

    private boolean increasePage() {

        if (currentPage < MAX_PAGINATION) {
            currentPage++;
            startAt = startAt + R_SIZE;
            return true;
        } else
        {
            // reset
            resetSearch();
            return false;
        }
    }


    private void fetchImages() {

        AsyncHttpClient client = new AsyncHttpClient();

        // Get Request
        client.get(constructQueryString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageJsonResults;


                try {
                    imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");

                    if (currentPage == 0 || currentPage > MAX_PAGINATION) {
                        imageAdapter.clear();
                    }
                    imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                    imageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error after Success", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Exception onFailure", Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void onImageSearch(View view) {
        resetSearch();
        setupViews();
        retrieveImages();
    }

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void customLoadMoreDataFromApi(int page) {
        retrieveImages();
    }

    private void retrieveImages() {
        if (isNetworkAvailable()) {
            if (increasePage()) {
                fetchImages();
            }
//            else
//            {
//                // TODO: Handle user requested more than allowed retrieval
//            }
        }
        else {
            Toast.makeText(this, "No network is available", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetSearch() {
        currentPage = 0;
        startAt = 0;
    }

}