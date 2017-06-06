package com.jhj.contacts.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhj.contacts.R;
import com.jhj.contacts.bean.InfoBean;

import java.util.List;

/**
 * Created by jianhaojie on 2017/5/9.
 */

public class FirstListAdapter extends RecyclerView.Adapter<FirstListAdapter.ItemViewHolder> {

    private final List<InfoBean.Data> bean;
    private LayoutInflater inflater;

    public FirstListAdapter(Context context, List<InfoBean.Data> bean) {
        this.bean = bean;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return bean.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_constacts_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        InfoBean.Data data = bean.get(position);

        holder.name.setText(data.name);
        holder.alpha.setText(data.alpha);

        int section = getSectionFromPosition(position);
        if (position == getPositionFromSection(section)) {
            if ("#".equals(getAlpha(data.alpha))) {
                holder.alpha.setText("#");
            }
            holder.layout.setVisibility(View.VISIBLE);
        } else {
            holder.layout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取position位置的首字母的ASCII值
     */
    private int getSectionFromPosition(int position) {
        return getAlpha(bean.get(position).alpha).charAt(0);
    }

    /**
     * 根据首字母ASCII的值获取第一次出现该字母的位置
     */
    public int getPositionFromSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            char firstChar = getAlpha(bean.get(i).alpha).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 对于首字母不[A-Z]的用"#"代替
     */
    private String getAlpha(String string) {
        String alpha = string.toUpperCase();
        if (alpha.matches("[A-Z]")) {
            return alpha;
        } else {
            return "#";
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView alpha;
        private LinearLayout layout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            alpha = (TextView) itemView.findViewById(R.id.tv_alpha);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_alpha);
        }
    }
}
