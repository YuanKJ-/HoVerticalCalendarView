package com.ykj.calendar.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.hosle.vertical_calendar.demo.R;
import com.ykj.calendar.bean.YearTitleBean;

import java.util.List;

public class YearTitleAdapter extends BaseTitleAdapter<YearTitleBean, BaseViewHolder> {

    private String selectedYear;

    public YearTitleAdapter(List<YearTitleBean> data) {
        super(R.layout.year_item, data);
        if (data != null && data.size() > 0) {
            selectedYear = data.get(0).year;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, YearTitleBean item) {
        String text = item.year + "年";
        helper.setText(R.id.year_text, text);
        if (item.year.equals(selectedYear)) {
            //设置选中
            helper.setBackgroundColor(R.id.year_text, mContext.getResources().getColor(android.R.color.white));
        } else {
            helper.setBackgroundColor(R.id.year_text, mContext.getResources().getColor(android.R.color.transparent));
        }
    }

    public void updateSelectedPos(int pos) {
        YearTitleBean item = getItem(pos);
        if (item != null && !item.year.equals(selectedYear)) {
            selectedYear = item.year;
            notifyDataSetChanged();
        }
    }
}
