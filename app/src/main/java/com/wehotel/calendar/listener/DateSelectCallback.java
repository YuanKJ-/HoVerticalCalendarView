package com.wehotel.calendar.listener;

/**
 * Created by kejie.yuan
 * Date: 2019/3/21
 * Description: 日期选择回调
 */
public interface DateSelectCallback {

    /**
     * 选中日期回调
     * @param beginDate 开始时间,毫秒级
     * @param endDate 结束时间,毫秒级
     * @param type 选中日期类型, {@link com.wehotel.calendar.enums.TimeTypeEnum}
     * @param beginFormat 选中日期format数据
     *                    根据type区分显示
     *                    DAY: 2019-03-30  (选中2019年3月30日)
     *                    WEEK: 2019-1(~52)  (选中2019年第1周~第52周)
     *                    MONTH: 2019-1(~12)  (选中2019年1月~12月)
     *                    SEASON: 2018-1(~4)  (选中2018年第1季度~第4季度)
     * @param endFormat 选中日期format数据 (数据暂时跟beginFormat相等)
     */
    void onDateSelect(long beginDate, long endDate, String type, String beginFormat, String endFormat);
}
