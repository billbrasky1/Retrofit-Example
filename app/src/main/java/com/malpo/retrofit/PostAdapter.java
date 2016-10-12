package com.malpo.retrofit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> posts;
    private PostClickListener postClickListener;

    public PostAdapter(PostClickListener postClickListener) {
        this.postClickListener = postClickListener;
    }

    /**
     * Set the list of posts to display in the recyclerview
     *
     * @param posts
     */
    public void setPosts(List<Post> posts) {
        this.posts = posts;

        //let's the adapter know that it needs to refresh its dataset.
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_cell, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Retrieve the post from the given position in the list
        final Post thisPost = posts.get(position);

        //Set the title / body of the view to be the title / body of the post
        holder.title.setText(thisPost.getTitle());
        holder.body.setText(thisPost.getBody());

        //Callback to RetrofitActivity to perform an action when a cell is selected.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postClickListener.onPostClicked(thisPost);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView title;
        TextView body;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.post_title);
            body = (TextView) itemView.findViewById(R.id.post_body);
        }
    }

    public interface PostClickListener {
        void onPostClicked(Post post);
    }
}
