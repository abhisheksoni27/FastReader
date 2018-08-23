package Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import Activities.ShowReader;
import androidx.recyclerview.widget.RecyclerView;
import dreamnyc.myapplication.Book;
import dreamnyc.myapplication.R;


public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {

    private static final String TAG = "MyListCursorAdapter";

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Book myListItem = Book.fromCursor(cursor);
        viewHolder.titleTextView.setText(myListItem.getTitle());
        viewHolder.authorTextView.setText(myListItem.getAuthor());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public TextView titleTextView;
        public Button readButton;

        public ViewHolder(final View view) {
            super(view);

            readButton = view.findViewById(R.id.readButton);
            titleTextView = view.findViewById(R.id.titleTextView);
            authorTextView = view.findViewById(R.id.authorTextView);

            readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToReader = new Intent(view.getContext(), ShowReader.class);
                    goToReader.putExtra("BOOK_NAME", titleTextView.getText());
                    view.getContext().startActivity(goToReader);
                }
            });

        }

    }
}
