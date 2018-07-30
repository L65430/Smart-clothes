package com.example.l.l_clothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.l_clothers.Info.TreatInfo;
import com.example.l.l_clothers.R;
import com.example.l.l_clothers.Utils.ImageUtil;

import java.net.ConnectException;
import java.util.List;

/**
 * Created by L on 2017/6/5.
 */

public class TreatListAdapter extends BaseAdapter {
    final static class ViewHolder{
        ImageView TreatImage;
        TextView  Treatinfo;
    }

    private Context mcontext;
    private List<TreatInfo> list;

    public TreatListAdapter(Context context,List<TreatInfo>list)
    {
        this.mcontext=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        final  TreatInfo atreatinfo=list.get(position);
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(mcontext).inflate(R.layout.treat_item,null);
            viewHolder.TreatImage=(ImageView)convertView.findViewById(R.id.treatimage);
            viewHolder.Treatinfo=(TextView)convertView.findViewById(R.id.treatinfo);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.TreatImage.setImageResource(atreatinfo.getTreatimage());
        viewHolder.Treatinfo.setText(atreatinfo.getTreatinfo());
        return convertView;
    }
    public void updateListView(List<TreatInfo>list)
    {
        this.list=list;
        notifyDataSetChanged();
    }


}
