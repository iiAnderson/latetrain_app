package com.github.iianderson.latetrain_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aigestudio.wheelpicker.WheelPicker;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SingleDateAndTimePickerDialog.Builder doubleBuilder;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE d MMM, HH:mm", Locale.getDefault());
    JourneyDetails outgoingJourney = new JourneyDetails();
    JourneyDetails returnJourney = new JourneyDetails();


    private final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);

                if(view.getId() == i){
                    checkJourneyDetailPanelsDisplayed(view.getText().toString());
                }
            }

        }
    };
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.backgroundSeconary)));


        ((RadioGroup) findViewById(R.id.journeyTypeToggleGroup)).setOnCheckedChangeListener(ToggleListener);

        setupJourneyDetails(R.id.departingType, R.id.departingDateTime, outgoingJourney);
    }

    public void checkJourneyDetailPanelsDisplayed(String buttonSelected){
        if("Return".equals(buttonSelected)){

            Animation inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
//            this.overridePendingTransition(R.anim.slide_in_left,
//                    R.anim.slide_out_left);
            findViewById(R.id.returnLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.returnLayout).setVisibility(View.VISIBLE);

            findViewById(R.id.returnLabel).startAnimation(inAnimation);
            findViewById(R.id.returnLayout).startAnimation(inAnimation);

            setupJourneyDetails(R.id.returnType, R.id.returnDateTime, returnJourney);
        } else {
            Animation inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

            inAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.returnLabel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.returnLayout).setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            findViewById(R.id.returnLabel).startAnimation(inAnimation);
            findViewById(R.id.returnLayout).startAnimation(inAnimation);

        }
    }

    public void setupJourneyDetails(final int journeyType, int journeyDate, final JourneyDetails details){
        final TextView journeyTypeView = findViewById(journeyType);
        final TextView departureDateTime = findViewById(journeyDate);

        journeyTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new BottomSheetDialog(new ContextThemeWrapper(MainActivity.this,
                        R.style.DialogSlideAnim));

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final WheelPicker journeyTypeSelector = dialog.findViewById(R.id.journeyType);

                        dialog.findViewById(R.id.journeyType).setBackground(new ColorDrawable(getResources().getColor(R.color.backgroundMain)));
                        dialog.findViewById(R.id.typeDismisser).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Object data = journeyTypeSelector.getData()
                                        .get(journeyTypeSelector.getCurrentItemPosition());
                                journeyTypeView.setText(data.toString());
                                details.type = data.toString();

                                if("Last".equals(details.getType()) || "First".equals(details.getType())){
                                    departureDateTime.setText(((String) departureDateTime.getText()).split(",")[0]);
                                } else {
                                    departureDateTime.setText(simpleDateFormat.format(details.getDate()));
                                }

                                dialog.dismiss();
                            }
                        });

                        journeyTypeSelector.setData(Arrays.asList(
                                new String[] {"Departing", "Arriving", "Last", "First"}));

                        journeyTypeSelector.setCurved(true);

                        if(details.getType() != null){
                            for(int i = 0; i < journeyTypeSelector.getData().size(); i++){
                                Object data = journeyTypeSelector.getData().get(i);
                                if(details.getType().equals(data)){
                                    journeyTypeSelector.setSelectedItemPosition(i);
                                }
                            }
                        }

                    }
                });
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.spinner_dialogue);


                dialog.show();
            }
        });

        departureDateTime.setText(simpleDateFormat.format(new Date()));
        departureDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Date now = new Date();

                doubleBuilder = new SingleDateAndTimePickerDialog.Builder(MainActivity.this)
                        .bottomSheet()
                        .curved()

                        .backgroundColor(getResources().getColor(R.color.backgroundMain))
                        .mainColor(getResources().getColor(R.color.buttonPrimary))
                        .minutesStep(5)
                        .mustBeOnFuture()
                        .defaultDate(details.getDate() == null? now : details.date)

                        .title("")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                if("Last".equals(details.getType()) || "First".equals(details.getType())){
                                    departureDateTime.setText(simpleDateFormat.format(date).split(",")[0]);
                                }
                                departureDateTime.setText(simpleDateFormat.format(date));
                                details.date = date;

                            }
                        });
                doubleBuilder.display();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onToggle(View view){
        ((RadioGroup)view.getParent()).check(view.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class JourneyDetails {

        public Date date = new Date();
        public String type = "Departing";

        public Date getDate() {
            return date;
        }

        public String getType() {
            return type;
        }
    }
}
