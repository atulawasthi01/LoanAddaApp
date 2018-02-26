package com.addventure.loanadda.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.addventure.loanadda.R;
import com.addventure.loanadda.BaseActivity;
import com.addventure.loanadda.MainActivity;
import com.addventure.loanadda.Model.Loantitle;


import java.util.List;

/**
 * Created by User on 12/11/2017.
 */

public class CustomloanAdapter extends BaseAdapter {
    Context context;
    List<Loantitle> loantitleList;
    ViewHolder viewHolder;
    LayoutInflater inflater;

    public CustomloanAdapter(Context context, List<Loantitle> loantitleList) {
        this.context=context;
        this.loantitleList=loantitleList;
    }

    @Override
    public int getCount() {
        return loantitleList.size();
    }

    @Override
    public Object getItem(int i) {
        return loantitleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        viewHolder=new ViewHolder();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view==null) {
            view=inflater.inflate(R.layout.grid_row_layout,viewGroup,false);
            viewHolder.loantitleTv = (TextView)view.findViewById(R.id.titletxt);
            viewHolder.loantitleIv = (ImageView)view.findViewById(R.id.icon);
            Loantitle loantitle=loantitleList.get(i);

            viewHolder.loantitleTv.setText(loantitle.getLoanTitle());
            viewHolder.loantitleIv.setImageResource(loantitle.getLoantitleImg());

        }
        return view;
    }

    public static class ViewHolder {
        TextView loantitleTv;
        ImageView loantitleIv;
    }
}
