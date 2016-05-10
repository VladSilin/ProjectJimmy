package com.pig.jimmy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.pig.jimmy.model.Sound;
import com.pig.jimmy.projectjimmy.R;
import com.pig.jimmy.request.Requestor;

import java.util.List;

/**
 * Created by Vlad Silin on 19/03/16.
 *
 * This is a RecyclerView adapter for displaying a list of sounds
 */
public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.SoundViewHolder> {
    private List<Sound> mSoundList;

    public SoundListAdapter(List<Sound> soundList) {
        mSoundList = soundList;
    }

    @Override
    public int getItemCount() {
        return mSoundList.size();
    }

    public Object getItem(int position) {
        return mSoundList.get(position);
    }

    @Override
    public SoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_list_item, parent, false);

        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SoundViewHolder holder, int position) {
        final Sound currentSound = mSoundList.get(position);
        holder.bindSound(currentSound);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Sound> getSoundList() {
        return mSoundList;
    }

    public void swapItems(List<Sound> soundList) {
        this.mSoundList = soundList;
        notifyDataSetChanged();
    }

    /**
     * A holder for RecyclerView items
     */
    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        private TextView mSoundNameTextView;
        private Switch mIsSoundEnabledSwitch;

        private Sound mCurrentSound;

        private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Requestor.makeEnableSoundRequest(mCurrentSound, isChecked);
            }
        };

        public SoundViewHolder(View itemView) {
            super(itemView);

            mSoundNameTextView = (TextView) itemView.findViewById(R.id.text_view_sound_name);
            mIsSoundEnabledSwitch = (Switch) itemView.findViewById(R.id.switch_is_sound_enabled);

            mIsSoundEnabledSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }

        public void bindSound(Sound currentSound) {
            mCurrentSound = currentSound;

            mSoundNameTextView.setText(mCurrentSound.getSoundName());

            mIsSoundEnabledSwitch.setOnCheckedChangeListener(null);
            mIsSoundEnabledSwitch.setChecked(isSoundPlayable());
            mIsSoundEnabledSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }

        private boolean isSoundPlayable() {
            return mCurrentSound.getPlayable() == 1;
        }
    }
}
