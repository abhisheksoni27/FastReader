package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import Activities.FastRead;
import dreamnyc.myapplication.R;
import Activities.ShowReader;
import dreamnyc.myapplication.SolventViewHolders;

/**
 * Created by abhishek on 13/3/16.
 */
public class chapterListViewAdapter extends  RecyclerView.ViewHolder {
    private List<String> itemList;
    private Context context;

    public chapterListViewAdapter(Context context, List<String> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_list, null);
        SolventViewHolders rcv = new SolventViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final SolventViewHolders holder, final int position) {
        holder.chapterName.setText(itemList.get(position).toString());
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] gotBackFromActivity = ((ShowReader) holder.cv.getContext()).onClickCalled(position);
                Intent read = new Intent(holder.cv.getContext(), FastRead.class);
                read.putExtra("CHAPTERURL", gotBackFromActivity[1]);
                read.putExtra("BOOKOBJECT", gotBackFromActivity[0]);
                read.putExtra("CHAPTERNAME", holder.chapterName.getText());
                holder.cv.getContext().startActivity(read);
            }
        });
        holder.chapterName.measure(0, 0);       //must call measure!

        holder.chapterName.getMeasuredWidth();
        holder.seperatorChapter.getLayoutParams().width = holder.chapterName.getMeasuredWidth() + 24;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}