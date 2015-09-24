package nano.at.udacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sindri on 16/08/15.
 */
public class ImageAdapter extends ArrayAdapter<MovieData>{

    Context context = null;

    public ImageAdapter(Activity activity, List<MovieData> movieData) {
        super(activity, 0, movieData);

        context = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView imageView;
        final MovieData movieData = getItem(position);


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
                movieData.posterPath = bitmap;
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("tag", "Picasso loading failed");
            }
        };
        imageView.setTag(target);
        Picasso.with(context).load(movieData.Url).into(new WeakReference<>(target).get());

        return imageView;


    }
}
