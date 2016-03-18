package dreamnyc.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder>{

    private static final String TAG = "MyListCursorAdapter";

    public MyListCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booklist, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        Book myListItem = Book.fromCursor(cursor);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(myListItem.getPathOfCover(), options);
        viewHolder.titleTextView.setText(myListItem.getTitle());
        viewHolder.mImageView.setImageBitmap(bitmap);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView titleTextView;
        public ImageView mImageView;
        public CardView cv;
        public ViewHolder(final View view) {
            super(view);

            titleTextView =(TextView)view.findViewById(R.id.titleTextView);
            mImageView = (ImageView) view.findViewById(R.id.mImageView);
            cv = (CardView)view.findViewById(R.id.view);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToReader = new Intent(view.getContext(),ShowReader.class);
                    goToReader.putExtra("BOOK_NAME",titleTextView.getText());
                    view.getContext().startActivity(goToReader);
                }
            });

        }
    }
}
