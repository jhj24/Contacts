package com.jhj.contacts.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhj.contacts.R;
import com.jhj.contacts.bean.InfoBean;

import java.util.List;

/**
 * 自定义分割线的通讯录
 * Created by jianhaojie on 2017/5/9.
 */

public class SecondListAdapter extends RecyclerView.Adapter<SecondListAdapter.ItemViewHolder> {

    public List<InfoBean.Data> dataList;
    private LayoutInflater inflater;

    SecondListAdapter(Context context, List<InfoBean.Data> dataList) {
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_second_contact, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        InfoBean.Data data = dataList.get(position);
        holder.name.setText(data.name);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 根据首字母ASCII的值获取第一次出现该字母的位置
     */
    int getPositionFromSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            char firstChar = dataList.get(i).alpha.charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

}
