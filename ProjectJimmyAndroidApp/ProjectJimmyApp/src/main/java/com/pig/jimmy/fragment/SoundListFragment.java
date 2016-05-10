package com.pig.jimmy.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pig.jimmy.adapter.SoundListAdapter;
import com.pig.jimmy.main.ProjectJimmyApplication;
import com.pig.jimmy.model.Sound;
import com.pig.jimmy.projectjimmy.R;
import com.pig.jimmy.request.Requestor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad Silin on 19/03/16.
 *
 * This is the fragment containing the sound list view
 */
public class SoundListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String SAVED_SOUND_LIST_TAG = "soundList";

    private RecyclerView mSoundRecyclerView;
    private SoundListAdapter mSoundListAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sound_list_fragment, container, false);

        setUpSwipeRefreshLayout(view);
        setUpSoundRecyclerView(view, savedInstanceState);

        return view;
    }

    private void setUpSoundRecyclerView(View view, Bundle savedInstanceState) {
        mSoundRecyclerView = (RecyclerView) view.findViewById(R.id.sound_recycler_view);
        mSoundRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setSoundRecyclerViewAdapter(savedInstanceState);
    }

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    private void setSoundRecyclerViewAdapter(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            List<Sound> sounds = savedInstanceState.getParcelableArrayList(SAVED_SOUND_LIST_TAG);
            if (sounds != null) {
                mSoundListAdapter = new SoundListAdapter(sounds);
            }
        } else {
            mSoundListAdapter = new SoundListAdapter(new ArrayList<Sound>());

            mSwipeRefreshLayout.post(new Runnable() {
                                         @Override
                                         public void run() {
                                             mSwipeRefreshLayout.setRefreshing(true);
                                             Requestor.makeSoundListRequest();
                                         }
                                     }
            );
            Requestor.makeSoundListRequest();
        }
        mSoundRecyclerView.setAdapter(mSoundListAdapter);
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                .registerReceiver(new SoundListBroadcastReceiver(),
                        new IntentFilter(Requestor.SOUND_LIST_REPSONSE));
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        ArrayList<Sound> values = new ArrayList<>(mSoundListAdapter.getSoundList());
        savedState.putParcelableArrayList(SAVED_SOUND_LIST_TAG, values);
    }

    @Override
    public void onRefresh() {
        Requestor.makeSoundListRequest();

        Requestor.makeIsPlayingRequest();

        Requestor.makeIntervalGetRequest(Requestor.INTERVAL_TYPE_MIN);
        Requestor.makeIntervalGetRequest(Requestor.INTERVAL_TYPE_MAX);
    }

    private class SoundListBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<Sound> soundList = intent.getParcelableArrayListExtra("soundList");

            if (soundList != null) {
                mSoundListAdapter.swapItems(soundList);
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
