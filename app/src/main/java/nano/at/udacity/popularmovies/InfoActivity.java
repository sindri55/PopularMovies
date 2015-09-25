package nano.at.udacity.popularmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sindri on 20/08/15.
 */
public class InfoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    public ImageView imageView;
    public static MovieData movie;
    public YouTubePlayer youTubePlayer;
    private String VideoKey = null;
    public static final int RQS_ErrorDialog = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        Bundle bundle = getIntent().getBundleExtra("Bundle");
        if (bundle != null) {
            movie = bundle.getParcelable("MovieItem");
        }

        TextView date, vote, cnt, dec, title;
        ImageView postImg = (ImageView) findViewById(R.id.postImg);
        date = (TextView) findViewById(R.id.date2);
        vote = (TextView) findViewById(R.id.voteAve2);
        cnt = (TextView) findViewById(R.id.voteCnt2);
        dec = (TextView) findViewById(R.id.description);
        title = (TextView) findViewById(R.id.title2);

        postImg.setImageBitmap(movie.posterPath);
        date.setText("Release date: " + movie.releaseDate);
        vote.setText("Ratings: " + movie.voteAverage);
        cnt.setText("From " + movie.voteCount + " users");
        dec.setText(movie.overView);
        title.setText(movie.title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String id = movie.id;
        new DownloadVideo().execute(id);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.MINIMAL;
        player.setPlayerStyle(style);
        youTubePlayer = player;
        if (!b) {
            // loadVideo() //will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.cueVideo(VideoKey);

            // Hiding player controls
            // player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this, RQS_ErrorDialog).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class DownloadVideo extends AsyncTask<String, Void, String> {
        InputStream is = null;
        BufferedReader reader = null;
        JSONArray jsonArray = null;

        final String JSON_BASE_URL_VIDEO = "http://api.themoviedb.org/3/movie/";
        final String JSON_BASE2_URL_VIDEO = "/videos?api_key=";
        final String JSON_MY_MOVIE_KEY = "2733c0a0178777342548a83fa5aefec3";

        private static final String JSON_KEY = "key";


        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(JSON_BASE_URL_VIDEO + params[0] + JSON_BASE2_URL_VIDEO + JSON_MY_MOVIE_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.connect();

                is = conn.getInputStream();

                // Read the input stream into a String
                InputStream inputStream = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));


                // Newline-ing JSON for better debugging - Will not affect parsins.
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                try {
                    jsonArray = new JSONObject(buffer.toString()).getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        VideoKey =
                                jsonArray.getJSONObject(i).getString(JSON_KEY);
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
            return VideoKey;
        }


        @Override
        protected void onPostExecute(String result) {
            YouTubePlayerView  youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            if(result != null){
                youTubePlayerView.initialize(Config.DEVELOPER_KEY, InfoActivity.this);
            }
        }

    }
}