package project.finalterm.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.FolderDetailActivity;
import project.finalterm.quizapp.Data.Folder;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.FolderViewModel;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleFolderItem);
            description = itemView.findViewById(R.id.descriptionFolderItem);
        }
    }
    private Context context;
    private ArrayList<Folder> folders;
    private String userId;
    private FolderViewModel folderViewModel;
    public FolderAdapter(Context context, ArrayList<Folder> folders, String userId, FolderViewModel folderViewModel) {
        this.context = context;
        this.folders = folders;
        this.userId = userId;
        this.folderViewModel = folderViewModel;
    }

    @NonNull
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View topicView = inflater.inflate(R.layout.folder_item, parent, false);
        FolderAdapter.ViewHolder viewHolder = new FolderAdapter.ViewHolder(topicView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.ViewHolder holder, int position) {
        Folder folder = folders.get(position);

        holder.title.setText(folder.getTitle());
        holder.description.setText(folder.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Log.d("FOLDERCHECK", userId + " - " + folder.getId());
            Intent intent = new Intent(context, FolderDetailActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("folderId", folder.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.folders_context_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.editFolder) {
                    Log.d("FOLDERCHECK", userId + " - " + folder.getId());
                    Intent intent = new Intent(context, FolderDetailActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("folderId", folder.getId());
                    context.startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.deleteFolder) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialAlertDialog);
                    builder.setTitle("Confirm Delete");
                    builder.setMessage("Are you sure you want to delete this folder?");

                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        String topicId = folders.get(position).getId();

                        folderViewModel.deleteFolder(userId, topicId, (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                Toast.makeText(context, "Successfully deleted folder", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                        folders.remove(folder);
                        notifyDataSetChanged();
                    });

                    builder.setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    builder.show();

                    return true;
                } else {
                    return false;
                }
            });
            popup.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void setFolders(ArrayList<Folder> newFolders) {
        if (newFolders != null) {
            folders.clear();
            folders.addAll(newFolders);
            notifyDataSetChanged();
        }
    }
}
