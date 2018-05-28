package hermes.mju.captdesign.hermesar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mju4 on 2018-05-28.
 */

public class SearchAdapter extends BaseAdapter {
    private ArrayList<SearchItem> arrayList = new ArrayList<>();

    public SearchAdapter() {}

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_pop_up, viewGroup, false);
        }

        TextView tvName = (TextView)view.findViewById(R.id.tvName);
        TextView tvDesc = (TextView)view.findViewById(R.id.tvDesc);

        SearchItem item = arrayList.get(i);

        tvName.setText(item.name);
        tvDesc.setText(item.desc);

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    public void addItem(String name, String desc) {
        SearchItem item = new SearchItem();
        item.name = name;
        item.desc = desc;

        arrayList.add(item);
    }

    public void clear() {
        arrayList.clear();
    }
}
