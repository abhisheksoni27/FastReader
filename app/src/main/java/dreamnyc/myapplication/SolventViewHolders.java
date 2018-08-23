package dreamnyc.myapplication;


import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by abhishek on 13/3/16.
 */
public class SolventViewHolders extends RecyclerView.ViewHolder {

    public TextView chapterName;
    public CardView cv;
    public View seperatorChapter;

    public SolventViewHolders(View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.view2);
        chapterName = itemView.findViewById(R.id.chapterListItem);
        seperatorChapter = itemView.findViewById(R.id.separatorChapter);
    }

}

