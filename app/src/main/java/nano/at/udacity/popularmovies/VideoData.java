package nano.at.udacity.popularmovies;

import java.util.ArrayList;

/**
 * Created by Sindri on 19/09/15.
 */
public class VideoData extends ArrayList<String> {

    public    String id;
    public    String key;
    public    String size;

    public VideoData (String id, String key, String size){
        this.id = id;
        this.key = key;
        this.size = size;
    }
}
