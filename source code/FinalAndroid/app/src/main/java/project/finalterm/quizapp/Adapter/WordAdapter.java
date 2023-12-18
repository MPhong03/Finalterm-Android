package project.finalterm.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.R;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText title;
        private TextInputEditText subtitle;
        private TextInputEditText description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleNewWord);
            subtitle = itemView.findViewById(R.id.subtitleNewWord);
            description = itemView.findViewById(R.id.descriptionNewWord);
        }
    }
    private Context context;
    private ArrayList<Word> words;
    private String userId;
    public WordAdapter(Context context, ArrayList<Word> words, String userId) {
        this.context = context;
        this.words = words;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View wordView = inflater.inflate(R.layout.new_word_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(wordView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = words.get(position);

        holder.title.setText(word.getTitle());
        holder.subtitle.setText(word.getSubtitle());
        holder.description.setText(word.getDescription());

        holder.itemView.setOnLongClickListener(v -> {
            showContextMenu(holder.itemView, position);
            return true;
        });

    }

    private void showContextMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.edit_remove_word_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.edit_option) {
                showEditWordDialog(position);
                return true;
            } else if (itemId == R.id.remove_option) {
                showRemoveWordDialog(position);
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void showEditWordDialog(int position) {
        Word wordToEdit = words.get(position);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialAlertDialog);
        builder.setTitle("Edit Word");

        View editWordView = LayoutInflater.from(context).inflate(R.layout.edit_word_dialog, null);

        EditText editTitle = editWordView.findViewById(R.id.editTitle);
        EditText editSubtitle = editWordView.findViewById(R.id.editSubtitle);
        EditText editDescription = editWordView.findViewById(R.id.editDescription);

        editTitle.setText(wordToEdit.getTitle());
        editSubtitle.setText(wordToEdit.getSubtitle());
        editDescription.setText(wordToEdit.getDescription());

        builder.setView(editWordView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedTitle = editTitle.getText().toString();
            String updatedSubtitle = editSubtitle.getText().toString();
            String updatedDescription = editDescription.getText().toString();

            Word updatedWord = new Word(updatedTitle, updatedSubtitle, updatedDescription, userId);

            words.set(position, updatedWord);
            notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void showRemoveWordDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialAlertDialog);
        builder.setTitle("Remove Word");
        builder.setMessage("Are you sure you want to remove this word?");

        builder.setPositiveButton("Remove", (dialog, which) -> {
            removeWord(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void removeWord(int position) {
        if (position >= 0 && position < words.size()) {
            words.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void addWord(Word word) {
        words.add(word);
        notifyItemInserted(words.size() - 1);
    }

    public void setWords(ArrayList<Word> words) {
        if (words != null) {
            this.words.clear();
            this.words.addAll(words);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Word> getWords() {
        return words;
    }
}
