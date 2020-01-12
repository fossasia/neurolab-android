package io.neurolab.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.neurolab.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.app_logo)
                .addItem(new Element(getString(R.string.version), R.drawable.ic_update))
                .setDescription(getResources().getString(R.string.about_us_content))
                .addGroup(getResources().getString(R.string.connect_with_us))
                .addWebsite(getString(R.string.website_fossasia))
                .addYoutube(getResources().getString(R.string.fossaisa))
                .addFacebook(getResources().getString(R.string.fossaisa))
                .addTwitter(getResources().getString(R.string.fossaisa))
                .addYoutube(getResources().getString(R.string.aboutus_youtube))
                .addInstagram(getResources().getString(R.string.fossaisa))
                .addGitHub(getResources().getString(R.string.fossaisa))
                .create();
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params);
        frameLayout.addView(aboutPage);
        setContentView(frameLayout);
    }

}
