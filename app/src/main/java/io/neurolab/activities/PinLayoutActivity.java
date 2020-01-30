package io.neurolab.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.neurolab.R;
import io.neurolab.utilities.PinDetails;

public class PinLayoutActivity extends AppCompatActivity implements View.OnTouchListener {

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private ImageView colorMap;
    private ArrayList<PinDetails> pinDetails = new ArrayList<>();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean frontSide = getIntent().getBooleanExtra("layout", true);
        ImageView pinImageView = findViewById(R.id.pin_lay_img_view);
        colorMap = findViewById(R.id.img_neurolab_color_map);
        colorMap.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                frontSide ? R.drawable.neurolab_front_colormap : R.drawable.neurolab_back_colormap, null));
        pinImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), frontSide ? R.drawable.front_pin_layout : R.drawable.back_pin_layout, null));

        pinImageView.setOnTouchListener(this);

        populatePinDetails();
    }

    private void populatePinDetails() {
        pinDetails.add(new PinDetails(getString(R.string.ado), getString(R.string.analog_input_0), Color.parseColor("#406743"), Color.parseColor("#226a0c")));
        pinDetails.add(new PinDetails(getString(R.string.ad1), getString(R.string.analog_input_1), Color.parseColor("#406743"), Color.parseColor("#d28080")));
        pinDetails.add(new PinDetails(getString(R.string.ad2), getString(R.string.analog_input_2), Color.parseColor("#406743"), Color.parseColor("#aa44aa")));
        pinDetails.add(new PinDetails(getString(R.string.ad3), getString(R.string.analog_input_3), Color.parseColor("#406743"), Color.parseColor("#baaa22")));
        pinDetails.add(new PinDetails(getString(R.string.clk), getString(R.string.clockpin_description), Color.parseColor("#6b40a9"), Color.parseColor("#dc1616")));
        pinDetails.add(new PinDetails(getString(R.string.rx), getString(R.string.recieverpin_description), Color.parseColor("#4372a2"), Color.parseColor("#1616dc")));
        pinDetails.add(new PinDetails(getString(R.string.tx), getString(R.string.transmitterpin_description), Color.parseColor("#4372a2"), Color.parseColor("#37dc16")));
        pinDetails.add(new PinDetails(getString(R.string.gnd), getString(R.string.groundpin), Color.parseColor("#ff4040"), Color.parseColor("#16dcda")));
        pinDetails.add(new PinDetails(getString(R.string.miso), getString(R.string.master_in_slave_out), Color.parseColor("#ffe040"), Color.parseColor("#b01498")));
        pinDetails.add(new PinDetails(getString(R.string.mosi), getString(R.string.master_out_slave_in), Color.parseColor("#7d5840"), Color.parseColor("#890b41")));
        pinDetails.add(new PinDetails(getString(R.string.scl), getString(R.string.serial_clock_pin), Color.parseColor("#4372a2"), Color.parseColor("#6e4472")));
        pinDetails.add(new PinDetails(getString(R.string.cs), getString(R.string.chip_select_pin), Color.parseColor("#7d5840"), Color.parseColor("#ff0b41")));
        pinDetails.add(new PinDetails(getString(R.string.usb), getString(R.string.usb_micro_b), Color.parseColor("#6b40a9"), Color.parseColor("#ff2b4f")));
        pinDetails.add(new PinDetails(getString(R.string.vcc), getString(R.string.voltage_supply_pin), Color.parseColor("#ff4040"), Color.parseColor("#5c3c14")));
        pinDetails.add(new PinDetails(getString(R.string.led), getString(R.string.led_attachment_pin), Color.parseColor("#406743"), Color.parseColor("#226aba")));
        pinDetails.add(new PinDetails("+5V", "Test pin +5.0 V for power supplying", Color.parseColor("#ff4040"), Color.parseColor("#765f40")));
        pinDetails.add(new PinDetails(getString(R.string.i1_plus), getString(R.string.current_measuring_pin), Color.parseColor("#406743"), Color.parseColor("#e7a41a")));
        pinDetails.add(new PinDetails(getString(R.string.i1_minus), getString(R.string.current_measuring_pin), Color.parseColor("#406743"), Color.parseColor("#e7a4ff")));
        pinDetails.add(new PinDetails(getString(R.string.i2_plus), getString(R.string.current_measuring_pin), Color.parseColor("#406743"), Color.parseColor("#ad2d00")));
        pinDetails.add(new PinDetails(getString(R.string.i2_minus), getString(R.string.current_measuring_pin), Color.parseColor("#406743"), Color.parseColor("#0053ad")));
        pinDetails.add(new PinDetails("+5.5V", "Test pin +5.5 V for power supplying", Color.parseColor("#ff4040"), Color.parseColor("#6b6500")));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        colorMap.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP:
                colorMap.setDrawingCacheEnabled(true);
                Bitmap clickSpot = Bitmap.createBitmap(colorMap.getDrawingCache());
                colorMap.setDrawingCacheEnabled(false);
                try {
                    int pixel = clickSpot.getPixel((int) event.getX(), (int) event.getY());
                    for (PinDetails pin : pinDetails) {
                        if (pin.getColorID() == Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel))) {
                            displayPinDescription(pin);
                        }
                    }
                } catch (IllegalArgumentException e) {/**/}
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

            default:
                break;
        }
        view.setImageMatrix(matrix);
        colorMap.setImageMatrix(matrix);

        return true;
    }

    private void displayPinDescription(PinDetails pin) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.pin_description_dialog, null);
        builder.setView(view);

        ImageView pinColor = view.findViewById(R.id.pin_category_color);
        pinColor.setBackgroundColor(pin.getCategoryColor());
        TextView pinTitle = view.findViewById(R.id.pin_description_title);
        pinTitle.setText(pin.getName());
        TextView pinDescription = view.findViewById(R.id.pin_description);
        pinDescription.setText(pin.getDescription());
        Button dialogButton = view.findViewById(R.id.pin_description_dismiss);

        builder.create();
        final AlertDialog dialog = builder.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private float spacing(MotionEvent event) {
        float x = 0;
        float y = 0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (Exception e) {/**/}
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = 0;
        float y = 0;
        try {
            x = event.getX(0) + event.getX(1);
            y = event.getY(0) + event.getY(1);
        } catch (Exception e) {/**/}
        point.set(x / 2, y / 2);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
