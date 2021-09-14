/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.asynctaskloader;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.asynctaskloader.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

// COMPLETE (1) implement LoaderManager.LoaderCallbacks<String> on MainActivity
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    /* A constant to save and restore the URL that is being displayed */
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    // COMPLETED (28) Remove the key for storing the search results JSON

    // COMPLETE (2) Create a constant int to uniquely identify your loader. Call it GITHUB_SEARCH_LOADER
    private final static int GITHUB_SEARCH_LOADER = 22;

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = findViewById(R.id.et_search_box);

        mUrlDisplayTextView = findViewById(R.id.tv_url_display);
        mSearchResultsTextView = findViewById(R.id.tv_github_search_results_json);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null) {
            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);

            // COMPLETED (26) Remove the code that retrieves the JSON


            mUrlDisplayTextView.setText(queryUrl);
            // COMPLETED (25) Remove the code that displays the JSON

        }

        // COMPLETED (24) Initialize the loader with GITHUB_SEARCH_LOADER as the ID, null for the bundle, and this for the callback
        LoaderManager.getInstance(this).initLoader(GITHUB_SEARCH_LOADER, null, this);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally request that an AsyncTaskLoader performs the GET request.
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();

        // COMPLETED (17) If no search was entered, indicate that there isn't anything to search for and return
        if(TextUtils.isEmpty(githubQuery)){
            Resources resources = this.getResources();
            mUrlDisplayTextView.setText(resources.getString(R.string.search_not_entered));
            return;
        }

        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        // COMPLETED (18) Remove the call to execute the AsyncTask


        // COMPLETED (19) Create a bundle called queryBundle
        // COMPLETED (20) Use putString with SEARCH_QUERY_URL_EXTRA as the key and the String value of the URL as the value

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, githubSearchUrl.toString());

        // COMPLETED (21) Call getSupportLoaderManager and store it in a LoaderManager variable
        // COMPLETED (22) Get our Loader by calling getLoader and passing the ID we specified
        // COMPLETED (23) If the Loader was null, initialize it. Else, restart it.

        LoaderManager manager = LoaderManager.getInstance(this);
        Loader<String> asyncLoader;
        asyncLoader = manager.getLoader(GITHUB_SEARCH_LOADER);
        if(asyncLoader  == null){
            manager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else{
            manager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the JSON data is visible */
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this){


            @Override
            protected void onStartLoading() {
                if (args == null){
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                assert args != null;
                String searchQueryURL = args.getString(SEARCH_QUERY_URL_EXTRA);
                if(TextUtils.isEmpty(searchQueryURL)){
                    return null;
                }
                try {
                    URL githubURL = new URL(searchQueryURL);
                    return NetworkUtils.getResponseFromHttpUrl(githubURL);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data == null ) {
            showErrorMessage();
        } else {
            showJsonDataView();
            mSearchResultsTextView.setText(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    // COMPLETE (3) Override onCreateLoader
    // Within onCreateLoader
        // COMPLETE (4) Return a new AsyncTaskLoader<String> as an anonymous inner class with this as the constructor's parameter
            // COMPLETE (5) Override onStartLoading
                // Within onStartLoading

                // COMPLETE (6) If args is null, return.

                // COMPLETE (7) Show the loading indicator

                // COMPLETE (8) Force a load
                // END - onStartLoading

            // COMPLETE (9) Override loadInBackground

                // Within loadInBackground
                // COMPLETE (10) Get the String for our URL from the bundle passed to onCreateLoader

                // COMPLETE (11) If the URL is null or empty, return null

                // COMPLETE (12) Copy the try / catch block from the AsyncTask's doInBackground method
                // END - loadInBackground

    // COMPLETE (13) Override onLoadFinished

        // Within onLoadFinished
        // COMPLETE (14) Hide the loading indicator

        // COMPLETE (15) Use the same logic used in onPostExecute to show the data or the error message
        // END - onLoadFinished

    // COMPLETE (16) Override onLoaderReset as it is part of the interface we implement, but don't do anything in this method

    // COMPLETED (29) Delete the AsyncTask class


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String queryUrl = mUrlDisplayTextView.getText().toString();
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);

        // COMPLETED (27) Remove the code that persists the JSON
    }
}