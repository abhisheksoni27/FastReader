package dreamnyc.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by abhishek on 13/3/16.
 */
public class chapterListViewAdapter extends RecyclerView.Adapter<SolventViewHolders> {
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
    public void onBindViewHolder(SolventViewHolders holder, final int position) {
        holder.chapterName.setText(itemList.get(position).toString());

    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
