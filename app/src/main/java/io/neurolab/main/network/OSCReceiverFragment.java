package io.neurolab.main.network;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.net.SocketException;
import java.util.ArrayList;

import io.neurolab.R;

public class OSCReceiverFragment extends FragmentActivity {
    private LinearLayout listenerTextList;
    private ListView messageOutputTextList;
    private FrameLayout frameLayout;
    private TextView statusLabel;
    private EditText ipField;
    private EditText portField;
    private Button forwardButton;

    private OSCReceiver oscReceiver;
    private ArrayList<String> listenerList;

    public OSCReceiverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        listenerTextList = findViewById(R.id.listenerTextList);
        messageOutputTextList = findViewById(R.id.messageOutputTextList);
        frameLayout = findViewById(R.id.frame);
        statusLabel = findViewById(R.id.statuslabel);
        ipField = findViewById(R.id.ipField);
        portField = findViewById(R.id.portField);
        forwardButton = findViewById(R.id.forwardButton);

        getItemList();

        forwardButton.setOnClickListener(v -> {
            try {
                OSCReceiver.run(bundle.getString(getResources().getString(R.string.filename_key)));
                oscReceiver = new OSCReceiver(Integer.parseInt(String.valueOf(portField.getText())), listenerList);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        });
    }

    private void getItemList() {
        listenerList = new ArrayList<>();
        for (int i = 0; i < listenerTextList.getChildCount(); i++) {
            listenerList.add(i, String.valueOf(listenerTextList.getChildAt(i)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        oscReceiver.setSenderList(listenerList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        oscReceiver.stopListening();
    }
}
