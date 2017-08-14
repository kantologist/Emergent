package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.femi.emergent.R;

import java.util.List;

import Models.News;

/**
 * Created by femi on 5/14/17.
 */

public class NewsAdapter extends RecyclerView.Adapter <NewsAdapter.NewsAdapterDataObjectHolder> {

    private Context context;
    private List<News> newses;
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter(Context context, List<News> newses) {
        this.context = context;
        this.newses = newses;
    }


    @Override
    public NewsAdapterDataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_cardview, parent, false);
        NewsAdapterDataObjectHolder dataObjectHolder = new NewsAdapterDataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapterDataObjectHolder holder, int position) {
        News current = newses.get(position);
        Glide.with(context)
                .load(current.getUrlToImage())
                .centerCrop()
                .into(holder.image);
        holder.title.setText(current.getTitle());
        holder.author.setText(current.getAuthor());
    }

    @Override
    public int getItemCount() {
        return newses.size();
    }

    public class NewsAdapterDataObjectHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView author;

        public NewsAdapterDataObjectHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.news_thumbnail);
            title = (TextView) itemView.findViewById(R.id.news_title);
            author = (TextView) itemView.findViewById(R.id.news_author);
        }
    }

    public interface ClickListener{
        void onClick(View view, int position) throws ClassNotFoundException;
        void onLongClick(View view, int position);
    }

    public static class  RecycleTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private NewsAdapter.ClickListener clickListener;

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public RecycleTouchListener(Context context, final RecyclerView recyclerView, final NewsAdapter.ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
               @Override
                public boolean onSingleTapUp(MotionEvent e){
                   return true;
               }

               @Override
                public void onLongPress(MotionEvent e) {
                   View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                   if(child != null && clickListener != null) {
                       clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                   }
               }


            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                try {
                    clickListener.onClick(child, rv.getChildPosition(child));
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView v, MotionEvent event) {

        }
    }
}
