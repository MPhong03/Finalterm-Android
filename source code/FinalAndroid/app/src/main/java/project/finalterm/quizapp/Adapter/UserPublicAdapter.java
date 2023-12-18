package project.finalterm.quizapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import project.finalterm.quizapp.Activity.PublicProfileActivity;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Dto.TopicPublic;
import project.finalterm.quizapp.Dto.UserPublic;
import project.finalterm.quizapp.R;

public class UserPublicAdapter extends RecyclerView.Adapter<UserPublicAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private CircleImageView avt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.nameUserPublicItem);
            avt = itemView.findViewById(R.id.authorAvatarUserPublicItem);
        }
    }
    private Context context;
    private ArrayList<UserPublic> users;

    public UserPublicAdapter(Context context, ArrayList<UserPublic> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserPublicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.user_public_item, parent, false);
        UserPublicAdapter.ViewHolder viewHolder = new UserPublicAdapter.ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserPublicAdapter.ViewHolder holder, int position) {
        UserPublic user = users.get(position);

        holder.username.setText(user.getUserName());
        Glide.with(context)
                .load(Uri.parse(user.getAvt()))
                .placeholder(R.drawable.handle_profile)
                .into(holder.avt);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PublicProfileActivity.class);
            intent.putExtra("userId", user.getUserId());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(ArrayList<UserPublic> newUsers) {
        if (newUsers != null) {
            users.clear();
            users.addAll(newUsers);
            notifyDataSetChanged();
        }
    }
}
