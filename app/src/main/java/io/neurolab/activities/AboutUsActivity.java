package io.neurolab.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
                .setDescription(getResources().getString(R.string.about_us_content))
                .addGroup(getResources().getString(R.string.connect_with_us))
                .addWebsite("https://fossasia.org/")
                .addFacebook("fossasia")
                .addTwitter("fossasia")
                .addYoutube("UCQprMsG-raCIMlBudm20iLQ")
                .addInstagram("fossasia")
                .addGitHub("fossasia")
                .addGroup("Others")
                .addItem(addVersion())
                .addItem(addBugReport())
                .addItem(addLicense())
                .create();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params);
        frameLayout.addView(aboutPage);
        setContentView(frameLayout);
    }

    private Element addVersion() {
        Element versionElement = new Element();
        versionElement.setTitle(getString(R.string.app_version))
                .setIconDrawable(R.drawable.ic_update);
        return versionElement;
    }

    private Element addBugReport() {
        Element bugReport = new Element();
        bugReport.setTitle(getString(R.string.report_bug))
                .setIconDrawable(R.drawable.ic_bug_report_black_24dp)
                .setOnClickListener(v -> {
                    String report_link = getString(R.string.bug_report);
                    startIntent(report_link);
                });
        return bugReport;
    }

    private Element addLicense() {
        Element license = new Element();
        license.setTitle(getString(R.string.app_license))
                .setIconDrawable(R.drawable.ic_insert_drive_file_black_24dp)
                .setOnClickListener(v -> {
                    String lic_link = getString(R.string.license_link);
                    startIntent(lic_link);
                });
        return license;
    }

    private void startIntent(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
