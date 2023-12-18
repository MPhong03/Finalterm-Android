package project.finalterm.quizapp.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import project.finalterm.quizapp.Data.Information;
import project.finalterm.quizapp.Data.UserRank;
import project.finalterm.quizapp.R;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rank;
        private TextView name;
        private TextView score;
        private CircleImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.userRank);
            name = itemView.findViewById(R.id.userNameRank);
            score = itemView.findViewById(R.id.userScoreRank);
            photo = itemView.findViewById(R.id.userPhotoRank);
        }
    }
    private Context context;
    private ArrayList<UserRank> users;

    public RankAdapter(Context context, ArrayList<UserRank> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_rank_item, parent, false);
        RankAdapter.ViewHolder viewHolder = new RankAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int position) {
        UserRank user = users.get(position);

        holder.rank.setText(user.getRank());
        holder.name.setText(user.getName());
        holder.score.setText(user.getScore());

        Glide.with(context)
                .load(Uri.parse(user.getPhoto()))
                .placeholder(R.drawable.handle_profile)
                .into(holder.photo);

        if (position == 0) {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gold)));
        } else if (position == 1) {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.silver)));
        } else if (position == 2) {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bronze)));
        } else {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
