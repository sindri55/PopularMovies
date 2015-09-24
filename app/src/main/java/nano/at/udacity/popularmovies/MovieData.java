package nano.at.udacity.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Sindri on 18/08/15.
 */
public class MovieData extends ArrayList<String> implements Parcelable {

    //Parameter for the data
    String id;
    String language;
    String title;
    String Url;
    String Url2;
    Bitmap posterPath;
    Bitmap backdropPath;
    String overView;
    String voteAverage;
    String voteCount;
    String releaseDate;
    private Parcel dest;
    private int flags;

    public MovieData(String id, String language, String title, String Url, String Url2, Bitmap posterPath, Bitmap backdropPath, String overView,
                     String voteAverage, String voteCount, String releaseDate){

        this.id = id;
        this.language = language;
        this.title = title;
        this.Url = Url;
        this.Url2 = Url2;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overView = overView;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.releaseDate = releaseDate;

    }

    public MovieData(Parcel in){
        id = in.readString();
        title = in.readString();
        language = in.readString();
        Url = in.readString();
        Url2 = in.readString();
        posterPath = in.readParcelable(Bitmap.class.getClassLoader());
        backdropPath = in.readParcelable(Bitmap.class.getClassLoader());
        overView = in.readString();
        voteAverage = in.readString();
        voteCount = in.readString();
        releaseDate = in.readString();

    }





    public static final Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(language);
        dest.writeString(Url);
        dest.writeString(Url2);
        dest.writeParcelable(posterPath, flags);
        dest.writeParcelable(backdropPath, flags);
        dest.writeString(overView);
        dest.writeString(voteAverage);
        dest.writeString(voteCount);
        dest.writeString(releaseDate);
    }
}


