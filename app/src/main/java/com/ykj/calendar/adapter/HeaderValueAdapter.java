package com.ykj.calendar.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.hosle.vertical_calendar.demo.R;
import com.ykj.calendar.bean.HeaderValueBean;

import java.util.List;

public abstract class HeaderValueAdapter<T extends HeaderValueBean, K extends BaseViewHolder>
        extends BaseValueAdapter<T, K> {


    public HeaderValueAdapter(@Nullable List<T> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<T>() {
            @Override
            protected int getItemType(T entity) {
                //根据你的实体类来判断布局类型
                return entity.type;
            }
        });
        getMultiTypeDelegate()
                .registerItemType(T.HEADER_TYPE, R.layout.value_header_view)
                .registerItemType(T.ITEM_TYPE, R.layout.value_item_view);
    }

    @Override
    protected void convert(K helper, T item) {
        if (item.type == T.HEADER_TYPE) {
            helper.setText(R.id.header_text, item.year + "年");
        }
    }
}
