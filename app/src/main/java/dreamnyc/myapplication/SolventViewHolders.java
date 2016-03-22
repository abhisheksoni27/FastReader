package dreamnyc.myapplication;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by abhishek on 13/3/16.
 */
public class SolventViewHolders extends RecyclerView.ViewHolder{

    public TextView chapterName;
    public CardView cv;
    public View seperatorChapter;


    public SolventViewHolders(View itemView) {
        super(itemView);
        cv=(CardView)itemView.findViewById(R.id.view2);
        chapterName = (TextView) itemView.findViewById(R.id.chapterListItem);
seperatorChapter =itemView.findViewById(R.id.separatorChapter);
    }

}

