package dreamnyc.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {

    private static final String TAG = "MyListCursorAdapter";

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
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
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 380, 680, true);
        }
        viewHolder.titleTextView.setText(myListItem.getTitle());

        viewHolder.authorTextView.setText(myListItem.getAuthor());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public TextView titleTextView;
        public LinearLayout insideCard;
        public Button readButton;
        public View seperator;
        public CardView cv;

        public ViewHolder(final View view) {
            super(view);
            WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics DM=new DisplayMetrics();
            display.getMetrics(DM);
            readButton=(Button)view.findViewById(R.id.readButton);
            seperator=view.findViewById(R.id.separator);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            insideCard=(LinearLayout)view.findViewById(R.id.insideCard);
            insideCard.getLayoutParams().width=(DM.widthPixels/2)-24;
            authorTextView=(TextView)view.findViewById(R.id.authorTextView);
            cv = (CardView) view.findViewById(R.id.view);
            Typeface light = Typeface.createFromAsset(titleTextView.getResources().getAssets(), "fonts/light.ttf");
            titleTextView.setTypeface(light);
            Typeface regular = Typeface.createFromAsset(authorTextView.getResources().getAssets(),"fonts/thinItalic.ttf");
            authorTextView.setTypeface(regular);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToReader = new Intent(view.getContext(), ShowReader.class);
                    goToReader.putExtra("BOOK_NAME", titleTextView.getText());
                    view.getContext().startActivity(goToReader);
                }
            });

setWidth((DM.widthPixels/2)-24);
        }
        public void setWidth(int a){
            a=a-8*4;
            readButton.getLayoutParams().width=a;
            seperator.getLayoutParams().width=a;

        }
    }



    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


}
