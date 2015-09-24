package nano.at.udacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Sindri on 20/09/15.
 */
public class BackPosterAdpater extends BaseAdapter {

    Context context = null;
    MovieData movieData;

    public BackPosterAdpater(Activity activity, MovieData movieData) {


        this.movieData = movieData;
        context = activity;
    }


    @Override
    public int getCount() {
        return movieData.size();
    }

    @Override
    public Object getItem(int position) {
        return movieData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView imageView;

        if(convertView == null){
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);

        }else{
            imageView = (ImageView)convertView;
        }

        final Target target = new Target() {
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                movieData.backdropPath = bitmap;
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("tag", "Picasso loading failed");
            }
        };
        imageView.setTag(target);
        Picasso.with(context).load(movieData.Url2).into(new WeakReference<>(target).get());

        return imageView;

    }

}