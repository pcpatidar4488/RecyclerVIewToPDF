package deep_linking.firebase.project.com.recyclerviewtopdf.new_pkg;

import android.graphics.Bitmap;

public interface BitmapCallback {
    void finished(Bitmap bitmap);

    void error(Throwable error);
}
