package com.malpo.retrofit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity implements PostAdapter.PostClickListener {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    //Create retrofit instance
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private RetrofitService service = retrofit.create(RetrofitService.class);

    private RecyclerView rv;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set view for activity
        setContentView(R.layout.activity_retrofit);

        //Set toolbar as support action bar (standard practice since material design)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Recyclerview setup
        rv = (RecyclerView) findViewById(R.id.retrofit_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        //Recyclerview adapter setup
        postAdapter = new PostAdapter(this);
        rv.setAdapter(postAdapter);

        //Retrieve posts from API using Retrofit.
        getPosts();
    }

    /**
     * Fetch posts from API using Retrofit
     */
    private void getPosts() {
        //Enqueue makes our call asynchronous
        service.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                //Populate our recyclerview with the results from the API call.
                populateRecyclerView(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

                //If API call fails, show snackbar, so user knows something went wrong.
                failed("Error retrieving posts");
            }
        });
    }

    /**
     * Populate the recyclerview with the list of posts returned from the API
     * @param posts the list of posts
     */
    private void populateRecyclerView(List<Post> posts) {
        postAdapter.setPosts(posts);
    }

    @Override
    public void onPostClicked(Post post) {
        //Show loading dialog
        final ProgressDialog progressDialog = showLoadingDialog();

        //Retrieve the first comment for the post using Retrofit.
        service.getComments(post.getId()).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                //cancel loading dialog since we now have a successful response.
                progressDialog.cancel();
                showCommentDialog(response.body().get(0));
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

                //cancel loading dialog, but show a snackbar to let the user
                //know something went wrong
                progressDialog.cancel();
                failed("Error retrieving comments");
            }
        });
    }

    /**
     * Shows a snackbar with a given message
     * @param message the message to show in the snackbar
     */
    private void failed(String message) {
        Snackbar.make(rv, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Create and show a loading dialog
     * @return the created dialog
     */
    private ProgressDialog showLoadingDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Loading comment...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    /**
     * Create and show an AlertDialog with info from a given {@link Comment}
     * @param comment the {@link Comment} to pass into the dialog.
     */
    private void showCommentDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(comment.getName());
        builder.setMessage(comment.getBody());

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
