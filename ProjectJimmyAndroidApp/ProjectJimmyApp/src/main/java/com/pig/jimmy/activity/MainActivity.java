package com.pig.jimmy.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pig.jimmy.main.ProjectJimmyApplication;
import com.pig.jimmy.model.Interval;
import com.pig.jimmy.projectjimmy.R;
import com.pig.jimmy.request.Requestor;
import com.pig.jimmy.util.DateTimeUtils;

public class MainActivity extends AppCompatActivity {
    private static final String INTERVAL_TIME_PICKER_FRAGMENT_ID = "intervalTimePickerFragment";

    private static final String SAVED_MIN_INTERVAL_TAG = "mMinInterval";
    private static final String SAVED_MAX_INTERVAL_TAG = "mMaxInterval";

    private FloatingActionButton mToggleAutoplayButton;
    private TextView mMinIntervalTextView;
    private TextView mMaxIntervalTextView;

    private boolean mIsAutoPlaying;
    private static int mMinInterval;
    private static int mMaxInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        registerRequestReceivers();
        setUpPlayButton();
        setUpIntervalDisplay(savedInstanceState);
    }

    private void setUpToolbar() {
        setTitle(getResources().getString(R.string.app_name));
        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
    }

    private void setUpPlayButton() {
        mToggleAutoplayButton = (FloatingActionButton) findViewById(R.id.play_button);
        mToggleAutoplayButton.hide();

        Requestor.makeIsPlayingRequest();

        mToggleAutoplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Requestor.makeToggleAutoPlayRequest();
            }
        });
    }

    private void registerRequestReceivers() {
        LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                .registerReceiver(new AutoplayBroadcastReceiver(),
                        new IntentFilter(Requestor.AUTO_PLAY_TOGGLE_REPSONSE));

        LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                .registerReceiver(new AutoplayBroadcastReceiver(),
                        new IntentFilter(Requestor.IS_PLAYING_RESPONSE));

        LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                .registerReceiver(new GetIntervalBroadcastReceiver(),
                        new IntentFilter(Requestor.GET_INTERVAL_REPSONSE));
    }

    private void setUpIntervalDisplay(Bundle savedInstanceState) {
        mMinIntervalTextView = (TextView) findViewById(R.id.min_value);
        mMaxIntervalTextView = (TextView) findViewById(R.id.max_interval_value_textview);

        mMinIntervalTextView.setVisibility(View.INVISIBLE);
        mMaxIntervalTextView.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null) {
            restoreIntervalsFromSavedInstanceState(savedInstanceState);
        } else {
            Requestor.makeIntervalGetRequest(Requestor.INTERVAL_TYPE_MIN);
            Requestor.makeIntervalGetRequest(Requestor.INTERVAL_TYPE_MAX);
        }

        mMinIntervalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(getString(R.string.set_min_interval_title), R.id.min_value);
            }
        });

        mMaxIntervalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(getString(R.string.set_max_interval_title), R.id.max_interval_value_textview);
            }
        });
    }

    private void restoreIntervalsFromSavedInstanceState(Bundle savedInstanceState) {
        int minInterval = savedInstanceState.getInt(SAVED_MIN_INTERVAL_TAG, 0);
        int maxInterval = savedInstanceState.getInt(SAVED_MAX_INTERVAL_TAG, 0);

        int minHours = minInterval / 3600;
        int minMinutes = (minInterval % 3600) / 60;

        int maxHours = maxInterval / 3600;
        int maxMinutes = (maxInterval % 3600) / 60;

        mMinIntervalTextView.setText(String.format(getResources().getString(R.string.time_display),
                minHours, minMinutes));

        mMaxIntervalTextView.setText(String.format(getResources().getString(R.string.time_display),
                maxHours, maxMinutes));

        mMinIntervalTextView.setVisibility(View.VISIBLE);
        mMaxIntervalTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        savedState.putInt(SAVED_MIN_INTERVAL_TAG, mMinInterval);
        savedState.putInt(SAVED_MAX_INTERVAL_TAG, mMaxInterval);
    }

    private class AutoplayBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIsAutoPlaying = intent.getBooleanExtra(Requestor.IS_PLAYING_INTENT_KEY, false);

            mToggleAutoplayButton.setImageResource(mIsAutoPlaying ?
                    R.drawable.stop_button : R.drawable.start_button);
            mToggleAutoplayButton.show();
        }
    }

    private class GetIntervalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Interval interval = intent.getParcelableExtra(Requestor.INTERVAL_INTENT_KEY);

            int hours = interval.getHours();
            int minutes = interval.getMinutes();

            String intervalType = intent.getStringExtra(Requestor.INTERVAL_TYPE_INTENT_KEY);

            if (Requestor.INTERVAL_TYPE_MIN.equals(intervalType)) {
                mMinInterval = DateTimeUtils.getSeconds(hours, minutes);
                mMinIntervalTextView.setText(
                        String.format(getString(R.string.time_display),
                                hours, minutes));
            } else if (Requestor.INTERVAL_TYPE_MAX.equals(intervalType)) {
                mMaxInterval = DateTimeUtils.getSeconds(hours, minutes);
                mMaxIntervalTextView.setText(
                        String.format(getString(R.string.time_display),
                                hours, minutes));
            }

            mMinIntervalTextView.setVisibility(View.VISIBLE);
            mMaxIntervalTextView.setVisibility(View.VISIBLE);
        }
    }

    public void showTimePickerDialog(String title, int textViewId) {
        DialogFragment newFragment = new IntervalTimePickerFragment();

        Bundle args = new Bundle();
        args.putString(IntervalTimePickerFragment.INTERVAL_TIME_PICKER_DIALOG_TITLE_KEY, title);
        args.putInt(IntervalTimePickerFragment.INTERVAL_TIME_PICKER_TEXTVIEW_ID_KEY, textViewId);
        newFragment.setArguments(args);

        newFragment.show(getSupportFragmentManager(), INTERVAL_TIME_PICKER_FRAGMENT_ID);
    }

    public static class IntervalTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private static final String INTERVAL_TIME_PICKER_DIALOG_TITLE_KEY = "timePickerDialogTitle";
        private static final String INTERVAL_TIME_PICKER_TEXTVIEW_ID_KEY = "timePickerTextViewID";

        private String mDialogTitle;
        private int mTextViewID;

        @Override
        public void setArguments(Bundle args) {
            mDialogTitle = args.getString(INTERVAL_TIME_PICKER_DIALOG_TITLE_KEY);
            mTextViewID = args.getInt(INTERVAL_TIME_PICKER_TEXTVIEW_ID_KEY);
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int currentTime = 0;
            if (mTextViewID == R.id.min_value) {
                currentTime = mMinInterval;
            } else if (mTextViewID == R.id.max_interval_value_textview) {
                currentTime = mMaxInterval;
            }

            int hours = currentTime / 3600;
            int minutes = (currentTime % 3600) / 60;

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(getActivity(), this, hours, minutes, true);

            View dialogTitleView = getActivity().getLayoutInflater()
                    .inflate(R.layout.time_picker_title, null);
            TextView dialogTitle = (TextView) dialogTitleView.findViewById(R.id.time_picker_title_textview);
            if (mDialogTitle != null) {
                dialogTitle.setText(mDialogTitle);
            }
            timePickerDialog.setCustomTitle(dialogTitleView);

            return timePickerDialog;
        }

        @Override
        public void onTimeSet(TimePicker view, int hours, int minutes) {
            TextView timeTextView = (TextView) getActivity().findViewById(mTextViewID);

            Interval newInterval = new Interval(hours, minutes);
            int newIntervalInSeconds = DateTimeUtils.getSeconds(hours, minutes);

            if (mTextViewID == R.id.min_value) {
                if (newIntervalInSeconds > mMaxInterval) {
                    Toast toast = Toast.makeText(getActivity(),
                            getString(R.string.min_interval_error),
                            Toast.LENGTH_SHORT);
                    toast.show();

                    return;
                }

                if (newIntervalInSeconds != mMinInterval) {
                    mMinInterval = newIntervalInSeconds;
                    Requestor.makeIntervalSetRequest(newInterval, Requestor.INTERVAL_TYPE_MIN);
                }
            } else if (mTextViewID == R.id.max_interval_value_textview) {
                if (newIntervalInSeconds < mMinInterval) {
                    Toast toast = Toast.makeText(getActivity(),
                            getString(R.string.max_interval_error),
                            Toast.LENGTH_SHORT);
                    toast.show();

                    return;
                }

                if (newIntervalInSeconds != mMaxInterval) {
                    mMaxInterval = newIntervalInSeconds;
                    Requestor.makeIntervalSetRequest(newInterval, Requestor.INTERVAL_TYPE_MAX);
                }
            }

            timeTextView.setText(String.format(getResources().getString(R.string.time_display),
                    hours, minutes));
        }
    }
}
