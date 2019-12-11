package com.frozencode.placeguessr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.frozencode.placeguessr.MultiplayerActivity;
import com.frozencode.placeguessr.R;
import com.frozencode.placeguessr.model.PlayerName;

import java.util.ArrayList;
import java.util.List;

public class PlayersJoinedRoomAdapter extends RecyclerView.Adapter<PlayersJoinedRoomAdapter.ViewHolder> {

    private List<PlayerName> mPlayersNames;
    private Activity activity;

    public PlayersJoinedRoomAdapter(List<PlayerName> mPlayersNames, Activity activity) {
        this.mPlayersNames = mPlayersNames;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_joined, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String text;
        text = "Player " + (mPlayersNames.get(position).getId() + 1) + ": " + mPlayersNames.get(position).getPlayerName();
        holder.text.setText(text);
        if (position < mPlayersNames.size() - 1) {
            holder.space.setVisibility(View.VISIBLE);
        } else {
            holder.space.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mPlayersNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView, space;
        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            text = mView.findViewById(R.id.text);
            space = mView.findViewById(R.id.space);

        }
    }

}
