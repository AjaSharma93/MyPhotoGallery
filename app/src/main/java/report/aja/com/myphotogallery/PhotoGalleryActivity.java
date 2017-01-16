package report.aja.com.myphotogallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhotoGalleryActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
