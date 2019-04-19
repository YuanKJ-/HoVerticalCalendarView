package com.ykj.calendar.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hosle.vertical_calendar.demo.R;
import com.ykj.calendar.bean.BaseTitleBean;

import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联动列表左侧title列表adapter基类
 */
public class BaseTitleAdapter<T extends BaseTitleBean, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {
    private String selectedYear;

    public BaseTitleAdapter(List<T> data) {
        super(R.layout.year_item, data);
        if (data != null && data.size() > 0) {
            selectedYear = data.get(0).year;
        }
    }

    @Override
    protected void convert(K helper, T item) {
        String text = item.year + "年";
        helper.setText(R.id.year_text, text);
        if (item.year.equals(selectedYear)) {
            //设置选中
            helper.setBackgroundColor(R.id.year_text, mContext.getResources().getColor(android.R.color.white));
        } else {
            helper.setBackgroundColor(R.id.year_text, mContext.getResources().getColor(android.R.color.transparent));
        }
    }

    public void setSelectedYear(String selectedYear) {
        this.selectedYear = selectedYear;
        notifyDataSetChanged();
    }

    public void updateSelectedPos(int pos) {
        T item = getItem(pos);
        if (item != null && !item.year.equals(selectedYear)) {
            selectedYear = item.year;
            notifyDataSetChanged();
        }
    }
}
