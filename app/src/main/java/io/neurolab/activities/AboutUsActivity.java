package io.neurolab.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
                .create();
        setContentView(aboutPage);
    }

}
