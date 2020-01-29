package io.neurolab.activities;

import android.content.Intent;
import android.net.Uri;
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
                .addWebsite("https://fossasia.org/")
                .addFacebook("fossasia")
                .addTwitter("fossasia")
                .addYoutube("UCQprMsG-raCIMlBudm20iLQ")
                .addInstagram("fossasia")
                .addGitHub("fossasia")
                .addItem(addDevelopers())
                .create();
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params);
        frameLayout.addView(aboutPage);
        setContentView(frameLayout);
    }

    private Element addDevelopers() {
        Element developersElement = new Element();
        developersElement.setTitle(getString(R.string.developers));
        developersElement.setOnClickListener(v -> {
            String url = getString(R.string.github_developers_link);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
        return developersElement;
    }

}
