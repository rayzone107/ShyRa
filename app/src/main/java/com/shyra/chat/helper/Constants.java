package com.shyra.chat.helper;

import android.os.Environment;

/**
 * A class that contains all the constants across the app.
 * Created by Rachit Goyal for ShyRa on 10/1/16.
 */

public class Constants {
    public interface DIMENSIONS {
        int TIMELINE_RV_TOP_SPACING = 48;
    }

    public interface LOCAL_STORAGE_PATHS {
        String EVENT_IMAGE_PATH = Environment.getExternalStorageDirectory()
                .getPath() + "/ShyRa/eventImages";
    }

    public interface SERVER_STORAGE_HIERARCHY {
        interface EVENT_PATH {
            String EVENT = "event";
            String IMAGE = "image";
            String BACKDROP = "backdrop";
        }
    }

    public interface EXTRA {
        String TIMELINE_EVENT = "timeline_event";
    }
}
