package exp.com.sobot;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import exp.com.sobot.Models.Word;

public class SearchDictionaryActivity extends AppCompatActivity {

    private CoordinatorLayout rootView;
    private AppCompatTextView tvWord;
    private AppCompatTextView tvDef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dictionary);
        rootView = (CoordinatorLayout) findViewById(R.id.rootView);
        tvWord = (AppCompatTextView) findViewById(R.id.tvWord);
        tvDef = (AppCompatTextView) findViewById(R.id.tvDefinition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            if (!TextUtils.isEmpty(query)) {
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(query);
                showResults(query);
            }
        }
    }

    /**
     * Searches the dictionary and displays results for the given query.
     *
     * @param query The search query
     */
    private void showResults(final String query) {

        Cursor cursor = managedQuery(DictionaryProvider.CONTENT_URI, null, null,
                new String[]{query}, null);

        if (cursor == null) {
            // There are no results
            Snackbar.make(rootView, R.string.error_no_result_found, Snackbar.LENGTH_LONG).show();
            if (!isNetworkAvailable()) {
                Snackbar.make(rootView, R.string.network_error_message, Snackbar
                        .LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        fetchFromInternet(query);
                    }
                });
            } else {
                fetchFromInternet(query);
            }
        } else {

            cursor.moveToFirst();
            int word = cursor.getColumnIndex(DictionaryDatabase.KEY_WORD);
            int definition = cursor.getColumnIndex(DictionaryDatabase.KEY_DEFINITION);
            String words = cursor.getString(word);
            String def = cursor.getString(definition);

            if (!TextUtils.isEmpty(words)) tvWord.setText(words);
            if (!TextUtils.isEmpty(def)) tvDef.setText(def);

        }
    }

    private void fetchFromInternet(final String querys) {
        final String[] query = querys.split(" ");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://glosbe" +
                ".com/gapi/translate?from=eng&dest=eng&format=json&phrase=" + query[0] + "&pretty=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Word word = gson.fromJson(response, Word.class);
                if (word != null && word.getTuc() != null) {
                    Word.tuc[] tuc = word.getTuc();
                    if (tuc.length != 0) {
                        Word.tuc.meanings[] meaningses = tuc[0].getMeanings();
                        Word.tuc.meanings meanings = meaningses[0];
                        if (!TextUtils.isEmpty(meanings.getText())) {
                            tvDef.setText(meanings.getText());
                            tvWord.setText(query[0]);
                        } else {
                            tvWord.setText("Oops...");
                            Snackbar.make(rootView, "No Result found for " + query[0] + " in Online " +
                                    "Dictionary", Snackbar
                                    .LENGTH_INDEFINITE).show();
                        }

                    } else {
                        tvWord.setText("Oops...");
                        Snackbar.make(rootView, "No Result found for " + query[0] + " in Online " +
                                "Dictionary", Snackbar
                                .LENGTH_INDEFINITE).show();
                    }
                } else {
                    tvWord.setText("Oops...");
                    Snackbar.make(rootView, "No Result found for " + query[0] + " in Online " +
                            "Dictionary", Snackbar
                            .LENGTH_INDEFINITE).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("API Response Error", error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    /**
     * Function to check network state
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
