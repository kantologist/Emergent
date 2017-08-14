package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.femi.emergent.R;

import java.util.List;
import java.util.Random;

import Models.Contact;

/**
 * Created by femi on 4/14/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactAdapterDataObjectHolder> {

    private Context context;
    private List<Contact> contacts;

    public ContactAdapter(Context context, List<Contact> contacts){
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public ContactAdapterDataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_cardview, parent, false);
        ContactAdapterDataObjectHolder dataObjectHolder = new ContactAdapterDataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ContactAdapterDataObjectHolder holder, int position) {
        Contact current = contacts.get(position);
        holder.name.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactAdapterDataObjectHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public ContactAdapterDataObjectHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.person_photo);
            name = (TextView) itemView.findViewById(R.id.person_name);
        }
    }

    public interface ClickListener{
        void onClick(View view, int position) throws ClassNotFoundException;
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ContactAdapter.ClickListener clickListener;

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public  RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ContactAdapter.ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e){
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e){
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child != null && clickListener != null)
                    {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }


         @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
                try{
                    clickListener.onClick(child,  rv.getChildPosition(child));
                } catch (ClassNotFoundException e1){
                    e1.printStackTrace();;
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
    }
}
