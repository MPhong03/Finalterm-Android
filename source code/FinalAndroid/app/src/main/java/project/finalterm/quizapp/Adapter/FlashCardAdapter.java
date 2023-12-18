package project.finalterm.quizapp.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import project.finalterm.quizapp.R;
import project.finalterm.quizapp.Utils.TextSpeechUtil;

public class FlashCardAdapter extends RecyclerView.Adapter<FlashCardAdapter.CardViewHolder> {
    private Context context;
    private List<Map.Entry<String, String>> dataList;
    private Map<Integer, CardViewHolder> viewHolderMap = new HashMap<>();
    public FlashCardAdapter(Context context, Map<String, String> dataMap) {
        this.context = context;
        this.dataList = new ArrayList<>(dataMap.entrySet());
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item, parent, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(dataList.get(position));
        viewHolderMap.put(position, holder);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public Map.Entry<String, String> getItem(int position) {
        if (position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        }
        return null;
    }

    public CardViewHolder getViewHolderAtPosition(int position) {
        return viewHolderMap.get(position);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private final View frontLayout;
        private final View backLayout;
        private final TextView frontTextView;
        private final TextView backTextView;
        private final ImageView audioButtonFront;
        private final ImageView audioButtonBack;
        private boolean isFrontVisible = true;
        private TextSpeechUtil util;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            frontLayout = itemView.findViewById(R.id.frontLayout);
            backLayout = itemView.findViewById(R.id.backLayout);
            frontTextView = itemView.findViewById(R.id.frontTextView);
            backTextView = itemView.findViewById(R.id.backTextView);
            audioButtonFront = itemView.findViewById(R.id.audioButtonFront);
            audioButtonBack = itemView.findViewById(R.id.audioButtonBack);

            util = new TextSpeechUtil(itemView.getContext());

            audioButtonFront.setOnClickListener(v -> util.readText(frontTextView.getText().toString()));
            audioButtonBack.setOnClickListener(v -> util.readText(backTextView.getText().toString()));

            itemView.setOnClickListener(v -> flipCard());
        }

        void bind(Map.Entry<String, String> data) {
            frontTextView.setText(data.getKey());
            backTextView.setText(data.getValue());

            frontLayout.setVisibility(View.VISIBLE);
            backLayout.setVisibility(View.GONE);
            isFrontVisible = true;
        }

        public void flipCard() {
            ViewFlipper flipper = itemView.findViewById(R.id.flipper);
            if (isFrontVisible) {
                flipper.setInAnimation(itemView.getContext(), R.anim.card_flip_in);
                flipper.setOutAnimation(itemView.getContext(), R.anim.card_flip_out);
                flipper.showNext();
            } else {
                flipper.setInAnimation(itemView.getContext(), R.anim.card_flip_in);
                flipper.setOutAnimation(itemView.getContext(), R.anim.card_flip_out);
                flipper.showPrevious();
            }
            isFrontVisible = !isFrontVisible;
        }

        public TextView getFrontTextView() {
            return frontTextView;
        }

        public TextView getBackTextView() {
            return backTextView;
        }

        public boolean isFrontVisible() {
            return isFrontVisible;
        }
    }
}
