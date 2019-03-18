package example.com.w5_prove;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // These are the constant values we'll be using for keys throughout the application
    // The're put here for simplicity, but in RealLife™ we'd store them as string resources
    // See: https://developer.android.com/guide/topics/resources/string-resource.html
    public static final String BOOK_PART = "BOOK_PART";
    public static final String CHAPTER_PART = "CHAPTER_PART";
    public static final String VERSE_PART = "VERSE_PART";

    public static final String APP_PREFS = "APPLICATION_PREFERENCES";

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //
    public static final String TAG = "MainActivity.FL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Favorite Scripture");
    }

    // Some of you might have used OnClickListener rather than binding the click handler
    // directly in the XML layout. See this SO post for an explanation of the differences:
    // http://stackoverflow.com/questions/21319996/android-onclick-in-xml-vs-onclicklistener
    public void onDisplayScripture(View theButton) {

        EditText txtBook = (EditText) findViewById(R.id.txtBook);
        EditText txtChapter = (EditText) findViewById(R.id.txtChapter);
        EditText txtVerse = (EditText) findViewById(R.id.txtVerse);

        String book = txtBook.getText().toString();
        String chapter = txtChapter.getText().toString();
        String verse = txtVerse.getText().toString();

        String scripture = String.format("%s %s:%s", book, chapter, verse);
        Log.d(getClass().getName(), String.format("About to create intent with %s", scripture));

        // Some of you might have used a bundle rather than putExtra(). You can see a discussion
        // of the differences here:
        // http://stackoverflow.com/questions/15243798/advantages-of-using-bundle-instead-of-direct-intent-putextra-in-android
        Intent scriptureIntent = new Intent(this, DisplayActivity.class);
        scriptureIntent.putExtra(BOOK_PART, book);
        scriptureIntent.putExtra(CHAPTER_PART, chapter);
        scriptureIntent.putExtra(VERSE_PART, verse);

        startActivity(scriptureIntent);
    }

    public void onLoadScripture(View theButton) {

        // See https://developer.android.com/training/basics/data-storage/shared-preferences.html
        SharedPreferences sharedPref = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);

        // Get the book, or return null if it isn't there.
        String book = sharedPref.getString(BOOK_PART, null);
        String chapter = sharedPref.getString(CHAPTER_PART, null);
        String verse = sharedPref.getString(VERSE_PART, null);

        if(book == null) {
            Toast.makeText(this, "No saved scripture found.", Toast.LENGTH_SHORT).show();
        }
        else {
            EditText txtBook = (EditText) findViewById(R.id.txtBook);
            EditText txtChapter = (EditText) findViewById(R.id.txtChapter);
            EditText txtVerse = (EditText) findViewById(R.id.txtVerse);

            txtBook.setText(book);
            txtChapter.setText(chapter);
            txtVerse.setText(verse);

            // See https://developer.android.com/guide/topics/ui/notifiers/toasts.html
            Toast.makeText(this, "Loaded scripture.", Toast.LENGTH_SHORT).show();
        }

        //
        db.collection("scriptures")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }
}