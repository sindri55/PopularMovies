package nano.at.udacity.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY_GRID_INDEX = "index";
    private int mIndex = 0;

    private static final ArrayList<MovieData> movielist = new ArrayList<>();

    private String API_URL = "http://api.themoviedb.org/3/discover/movie?";
    private String POPULAR_NOW_URL = "sort_by=popularity.desc&";
    private String ACTION_BEST_URL = "certification_country=US&certification=R&sort_by=vote_average.desc&vote_count.gte=1000";
    private String COMEDY_BEST_URL = "with_genres=35&sort_by=vote_average.desc&vote_count.gte=1000";
    private String DRAMA_BEST_URL = "with_genres=18&sort_by=vote_average.desc&vote_count.gte=1000";
    private String BEST_OFF_ALL_TIME = "?primary_release_date.gte=1990-09-15&primary_release_date.lte=2016-10-22&sort_by=vote_average.desc&vote_count.gte=1000";
    private String PAGE = "page=";
    private String API_KEY = "&api_key=###";
    private ImageAdapter adapter = null;

    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dont know why i need this but i do for the actionbar to work
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }


        //Getting 3 pages of popular movies in a get request
        for(int i = 3; i >= 1; i--){
            new DownloadTask().execute(API_URL + POPULAR_NOW_URL + PAGE + i + API_KEY);

        }

        adapter = new ImageAdapter(this, movielist);

        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                MovieData movie = movielist.get(position);

                Intent intent;

                Bundle b = new Bundle();
                b.putParcelable("MovieItem",  movie);
                intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("Bundle", b);
                startActivity(intent);
                }
        });

        //TODO Passa copy
        if((savedInstanceState != null) && ((savedInstanceState.containsKey(BUNDLE_KEY_GRID_INDEX)))) {
            mIndex = savedInstanceState.getInt(BUNDLE_KEY_GRID_INDEX);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        movielist.clear();

        switch (item.getItemId()){
            case (R.id.popularNow):

                for(int i = 3; i >= 1; i--){
                    new DownloadTask().execute(API_URL + POPULAR_NOW_URL + PAGE + i + API_KEY);
                }
                break;

            case (R.id.bestAction):

                    new DownloadTask().execute(API_URL + ACTION_BEST_URL + API_KEY);
                break;

            case(R.id.bestComedy):

                    new DownloadTask().execute(API_URL + COMEDY_BEST_URL + API_KEY);
                break;

            case(R.id.bestDrama):

                    new DownloadTask().execute(API_URL + DRAMA_BEST_URL + API_KEY);
                break;

            case(R.id.bestOfAllTime):

                    new DownloadTask().execute(API_URL + BEST_OFF_ALL_TIME + API_KEY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class DownloadTask extends AsyncTask<String, Void, Void> {
        InputStream is = null;
        BufferedReader reader = null;
        JSONArray jsonArray = null;

        final String JSON_BASE_URL_IMAGE = "http://image.tmdb.org/t/p/";
        final String JSON_SIZE_FRONT_IMG = "w185";
        final String JSON_SIZE_BACK_IMG = "w500";

        // json data.
        private static final String ID = "id";
        private static final String JSON_TITLE = "title";
        final String JSON_POSTER_PATH = "poster_path";
        final String JSON_BACKDROP_PATH = "backdrop_path";
        final String JSON_OVERVIEW = "overview";
        final String JSON_VOTE_AVERAGE = "vote_average";
        final String JSON_VOTE_COUNT = "vote_count";
        final String JSON_RELEASE_DATE = "release_date";
        final String JSON_LANGUAGE = "original_language";


        @Override
        protected Void doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                is = conn.getInputStream();

                InputStream inputStream = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));


                String line;
                // Add a new line so it will be easyer to read.
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }



                try {
                    jsonArray = new JSONObject(buffer.toString()).getJSONArray("results");

                    // Fetching data fram the array into custom movielist array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        movielist.add(i, new MovieData(
                                jsonArray.getJSONObject(i).getString(ID),
                                jsonArray.getJSONObject(i).getString(JSON_LANGUAGE),
                                jsonArray.getJSONObject(i).getString(JSON_TITLE),
                                (new URL(
                                        Uri.parse(JSON_BASE_URL_IMAGE)
                                                .buildUpon()
                                                .appendPath(JSON_SIZE_FRONT_IMG)
                                                .appendEncodedPath(
                                                        jsonArray.getJSONObject(i).getString(JSON_POSTER_PATH))
                                                .toString()

                                )).toString(),
                                (new URL(
                                        Uri.parse(JSON_BASE_URL_IMAGE)
                                                .buildUpon()
                                                .appendPath(JSON_SIZE_BACK_IMG)
                                                .appendEncodedPath(
                                                        jsonArray.getJSONObject(i).getString(JSON_BACKDROP_PATH))
                                                .toString()

                                )).toString(),
                                null,
                                null,
                                jsonArray.getJSONObject(i).getString(JSON_OVERVIEW),
                                jsonArray.getJSONObject(i).getString(JSON_VOTE_AVERAGE),
                                jsonArray.getJSONObject(i).getString(JSON_VOTE_COUNT),
                                jsonArray.getJSONObject(i).getString(JSON_RELEASE_DATE)
                        ));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }




        @Override
        protected void onPostExecute(Void results) {


            adapter.notifyDataSetChanged();
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    gridView.setSelection(mIndex);
                }
            });



        }

    }
}