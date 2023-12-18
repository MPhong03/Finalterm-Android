package project.finalterm.quizapp.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import java.util.Locale;

import project.finalterm.quizapp.Interface.SpeechCompletionListener;

public class TextSpeechUtil {
    private TextToSpeech textToSpeech;
    private SpeechCompletionListener listener;
    public TextSpeechUtil(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set a default language or handle initialization success
            } else {
                Log.e("TextToSpeech", "Initialization failed");
            }
        });
    }
    public void setSpeechCompletionListener(SpeechCompletionListener listener) {
        this.listener = listener;
    }
    public void readText(String textToRead) {
        FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        languageIdentifier.identifyLanguage(textToRead)
                .addOnSuccessListener(detectedLanguage -> {
                    if (!detectedLanguage.equals("und")) { // "und" indicates undetermined language
                        speakText(textToRead, detectedLanguage);
                    } else {
                        Log.e("LanguageIdentification", "Could not determine language");
                        speakText(textToRead, "en");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void speakText(String textToRead, String detectedLanguage) {
        int result = textToSpeech.setLanguage(new Locale(detectedLanguage));
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TextToSpeech", "Language not supported");
        } else {
            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        textToSpeech.setOnUtteranceCompletedListener(utteranceId -> {
            if (listener != null) {
                listener.onSpeechCompleted();
            }
        });
    }

    public void stopSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
