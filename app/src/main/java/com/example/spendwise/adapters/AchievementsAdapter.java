package com.example.spendwise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.models.AchievementModel;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {

    private final List<AchievementModel> achievementList;

    public AchievementsAdapter(List<AchievementModel> achievementList) {
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public AchievementsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementsAdapter.ViewHolder holder, int position) {
        AchievementModel model = achievementList.get(position);

        holder.icon.setImageResource(model.getIconResId());
        holder.title.setText(model.getTitle());
        holder.subtitle.setText(model.getSubtitle());
        holder.points.setText(model.getPoints() + " pts");

        if (model.isEarned()) {
            holder.status.setText("Earned");
            holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.primary_green));
            holder.date.setText(model.getDateEarned());
        } else {
            holder.status.setText("Not Earned");
            holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.gray));
            holder.date.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, points, status, date;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.achievement_icon);
            title = view.findViewById(R.id.text_achievement_title);
            subtitle = view.findViewById(R.id.text_achievement_subtitle);
            points = view.findViewById(R.id.text_achievement_points);
            status = view.findViewById(R.id.text_achievement_status);
            date = view.findViewById(R.id.text_achievement_date);
        }
    }
}
