package com.goldsign.commu.app.vo;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.util.MessageUtil;

import java.util.TreeMap;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Vector;

import org.apache.log4j.Logger;

public class FlowHourMinFive {

    private static Logger logger = Logger.getLogger(FlowHourMinFive.class
            .getName());
    /**
     * 键对应的是小时分钟，值为5分钟总客流
     */
    private TreeMap<String, FlowHourMinFiveUnit> flowHourMinFiveTotal = new TreeMap<String, FlowHourMinFiveUnit>();
    /**
     * 键对应的是小时分钟，值为5分钟客流
     */
    private TreeMap<String, FlowHourMinFiveUnit> flowHourMinFive = new TreeMap<String, FlowHourMinFiveUnit>();

    public TreeMap<String, FlowHourMinFiveUnit> getFlowHourMinFive() {
        return flowHourMinFive;
    }

    public void setFlowHourMinFive(
            TreeMap<String, FlowHourMinFiveUnit> flowHourMinFive) {
        this.flowHourMinFive = flowHourMinFive;
    }

    public TreeMap<String, FlowHourMinFiveUnit> getFlowHourMinFiveTotal() {
        return flowHourMinFiveTotal;
    }

    public void setFlowHourMinFiveTotal(
            TreeMap<String, FlowHourMinFiveUnit> flowHourMinFiveTotal) {
        this.flowHourMinFiveTotal = flowHourMinFiveTotal;
    }

    /**
     * 初始化
     */
    public FlowHourMinFive() {
        init();

    }

    public FlowHourMinFive(boolean isInit) {
    }

    /**
     * 初始化
     */
    private void init() {

        String key = null;
        //初始化小时分钟客流消息：键为0000-2355的所有5分钟倍数的小时分钟值.为自然日中的小时分钟.不是运营日的小时分钟.
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < FrameCodeConstant.FIVE_HOUR_MINS.length; j++) {
                key = getKey(i, FrameCodeConstant.FIVE_HOUR_MINS[j]);
                this.flowHourMinFive.put(key, new FlowHourMinFiveUnit());
                this.flowHourMinFiveTotal.put(key, new FlowHourMinFiveUnit());
            }

        }

    }

    /**
     * 返回小时分钟格式
     *
     * @param hour
     * @param minFive
     * @return 小时分钟格式：hhMM
     */
    private String getKey(int hour, String minFive) {
        if (hour < 10) {
            return "0" + new Integer(hour).toString() + minFive;
        }
        return new Integer(hour).toString() + minFive;
    }

    public void calulateFlowHourMinFiveTotal() {
        FlowHourMinFiveUnit fhu;
        FlowHourMinFiveUnit fhu1;
        int total;

        Set set = this.flowHourMinFive.keySet();
        Iterator it = set.iterator();
        String key;

        while (it.hasNext()) {
            key = (String) it.next();// 时分
            fhu = (FlowHourMinFiveUnit) this.flowHourMinFive.get(key);
            if (!this.isLegalTraffic(fhu.getTraffic())) {
                continue;
            }
            total = this.getFlowHourMinFiveTotal(key);
            fhu1 = (FlowHourMinFiveUnit) this.flowHourMinFiveTotal.get(key);
            fhu1.setTraffic(total);

            // logger.error("calulateFlowHourTotal i="+i+" total="+total);

        }

    }

    private int getFlowHourMinFiveTotal(String hourMinFive) {
        int total = 0;
        FlowHourMinFiveUnit fhu;
        Set set = this.flowHourMinFive.keySet();
        Iterator it = set.iterator();
        String key;
        // 当前分钟得总客流等于当前分钟之前（包括当前分钟）得所有分钟客流之和
        while (it.hasNext()) {
            key = (String) it.next();
            if (key.compareTo(hourMinFive) > 0) {
                break;
            }
            fhu = (FlowHourMinFiveUnit) this.flowHourMinFive.get(key);
            total += fhu.getTraffic();

        }

        return total;

    }

    public void setFlowHourMinFive(FlowHourOrg vo) {
        String datetime = vo.getTrafficDatetime();
        String hourMinFive = datetime.substring(8);

        int hTraffic = vo.getTraffic();
        logger.debug("-----hourMinFive:" + hourMinFive);
        // 设置5分钟客流
        FlowHourMinFiveUnit fhu = this.flowHourMinFive.get(hourMinFive);
        fhu.setTraffic(hTraffic);

        // logger.error("ihh="+ihh+" hTraffic="+hTraffic);

    }

    public void setFlowHourMinFiveTotal(FlowHourOrg vo) {
        String datetime = vo.getTrafficDatetime();
        String hourMinFive = datetime.substring(8);

        int hTraffic = vo.getTraffic();
        // 设置5分钟总客流
        FlowHourMinFiveUnit fhu = this.flowHourMinFiveTotal.get(hourMinFive);
        fhu.setTraffic(hTraffic);

        // logger.error("ihh="+ihh+" hTraffic="+hTraffic);

    }

    public FlowResult calculateFlowHourMinFive(String minFive, int hTotalTraffic) {
        FlowResult result = new FlowResult();
        // 如果当前小时客流非法不做处理，计算5分钟客流，计算结果保存在flowHourMinFive对象中.
        if (!this.setFlowHourMinFiveUnit(minFive, hTotalTraffic)) {
            return result;
        }
        int hTraffic = ((FlowHourMinFiveUnit) this.flowHourMinFive.get(minFive))
                .getTraffic();

        if (this.isLegalTraffic(hTraffic)) {
            result.setHTraffic(hTraffic);

            result.setIsValid(true);
        }
        return result;

    }

    public FlowResult calculateFlowHourMinFive(String minFive,
                                               int hTotalTraffic, FlowHourMinFive flowHourMinFivePre) {
//
//        logger.info("计算5分钟客流开始");

        FlowResult result = new FlowResult();

        // 如果当前分钟客流非法不做处理，计算5分钟客流，计算结果保存在flowHourMinFive对象中.
        // 按运营日计算有2个特殊时间点，自然日的第一个5分钟（分钟客流＝当前总客流－上一自然日的最后的分钟2355总客流），
        // 第一个最接近运营时间的5分钟（分钟客流＝当前总客流－0）
        if (!this.setFlowHourMinFiveUnit(minFive, hTotalTraffic,
                flowHourMinFivePre)) {
            return result;
        }

        int hTraffic = ((FlowHourMinFiveUnit) this.flowHourMinFive.get(minFive))
                .getTraffic();

//        logger.info(minFive + "客流值为：" + hTraffic);

        if (this.isLegalTraffic(hTraffic)) {
            result.setHTraffic(hTraffic);
            result.setIsValid(true);
        }
        return result;

    }

    private boolean setFlowHourMinFiveUnit(String minFive, int hTotalTraffic) {
        FlowHourMinFiveUnit fhu = (FlowHourMinFiveUnit) this.flowHourMinFiveTotal
                .get(minFive);
        fhu.setTraffic(hTotalTraffic);
        // 当前分钟客流=当前总客流－上一个总客流
        int hTraffic = this.getHourMinFiveTraffic(minFive, hTotalTraffic);
        if (this.isLegalTraffic(hTraffic)) {
            FlowHourMinFiveUnit fhu1 = (FlowHourMinFiveUnit) this.flowHourMinFive
                    .get(minFive);
            fhu1.setTraffic(hTraffic);
            return true;
        }
        return false;

    }

    // 求出与运营时间最接近的5分钟时间，如运营日是0200则结果应是0200 ;运营日是0216则结果应是0220;运营日是0256则结果应是0300
    public String getMinFiveFirstInSquadTime() {
        String squadHour = FrameCodeConstant.SQUAD_TIME.substring(0, 2);
        String squadMin = FrameCodeConstant.SQUAD_TIME.substring(2);
        String min;
        String minFirst = null;

        String minFiveFirst;

        for (int i = 0; i < FrameCodeConstant.FIVE_HOUR_MINS.length; i++) {
            min = FrameCodeConstant.FIVE_HOUR_MINS[i];
            if (min.compareTo(squadMin) == 0) {
                minFirst = min;
                break;
            }
            if (min.compareTo(squadMin) > 0) {
                if (i == 0) {
                    minFirst = FrameCodeConstant.FIVE_HOUR_MINS[0];
                } else {
                    minFirst = FrameCodeConstant.FIVE_HOUR_MINS[i];
                }
                break;

            }

        }
        // 在5分钟数组中没有找到
        if (minFirst == null) {
            int n = Integer.parseInt(squadHour) + 1;// 小时数加1
            String s;
            if (n < 10) {
                s = "0" + n;// 补0
            } else {
                s = "" + n;
            }
            minFiveFirst = s + "00";
        } else {
            minFiveFirst = squadHour + minFirst;
        }
        return minFiveFirst;

    }

    public static void main(String[] arg) {
        /*
         * FlowHourMinFive m = new FlowHourMinFive(); String hm =
         * m.getMinFiveFirstInSquadTime(); logger.info(hm);
         */
        TreeMap<String, FlowHourMinFiveUnit> fhmfTotal = new TreeMap<String, FlowHourMinFiveUnit>();
        FlowHourMinFiveUnit u;
        String key;
        key = "2350";
        u = new FlowHourMinFiveUnit();
        u.setTraffic(100);

        fhmfTotal.put(key, u);

        key = "2340";
        u = new FlowHourMinFiveUnit();
        u.setTraffic(0);

        key = "2240";
        u = new FlowHourMinFiveUnit();
        u.setTraffic(0);

        key = "2230";
        u = new FlowHourMinFiveUnit();
        u.setTraffic(66);

        fhmfTotal.put(key, u);

        String minFivePreEnd = "2351";

        FlowHourMinFive m = new FlowHourMinFive(false);
        int total = m.getFlowHourMinFiveTotalPre(fhmfTotal, minFivePreEnd);
        logger.info("total=" + total);

    }

    // private boolean isFirstInSquadTime(String minFive) {
    // String first = this.getMinFiveFirstInSquadTime();
    // if (minFive.equals(first)) {
    // return true;
    // }
    // return false;
    // }
    private boolean setFlowHourMinFiveUnit(String minFive, int hTotalTraffic,
                                           FlowHourMinFive fhmfPre) {
//        logger.info("获取当前分钟对应的总客流对象");
        // 获取当前分钟对应的总客流对象
        FlowHourMinFiveUnit fhu = (FlowHourMinFiveUnit) this.flowHourMinFiveTotal
                .get(minFive);
//        logger.info("开始设置当前分钟对应的总客流：" + hTotalTraffic);
        // 设置当前分钟对应的总客流
        fhu.setTraffic(hTotalTraffic);
//        logger.info("成功设置当前分钟对应的总客流：" + hTotalTraffic);
        // 当前分钟客流=当前总客流－上一个总客流
        // 改为当前分钟客流=当前总客流－上一个最近不为0的总客流
        //(不比较客流总数)
        //modify by zhongzq 20190623 动态控制使用计算版本
        if (FrameCodeConstant.MODE_STANDARD.equals(FrameCodeConstant.USER_MODE)) {
            int hTraffic = this.getHourMinFiveTraffic(minFive, hTotalTraffic,
                    fhmfPre);
            if (this.isLegalTraffic(hTraffic)) {
                FlowHourMinFiveUnit fhu1 = (FlowHourMinFiveUnit) this.flowHourMinFive
                        .get(minFive);
                fhu1.setTraffic(hTraffic);
                return true;
            }
        } else {
            //modify by zhongzq 20190617
            if (this.isLegalTraffic(hTotalTraffic)) {
                FlowHourMinFiveUnit fhu1 = (FlowHourMinFiveUnit) this.flowHourMinFive
                        .get(minFive);
                fhu1.setTraffic(hTotalTraffic);
                return true;
            }
        }
        return false;

    }

    /**
     * @param fhmfTotal
     * @param minFivePreEnd 截止小时分钟
     * @return 最接近minFivePreEnd，客流不为0的上一个5分钟得总客流
     */
    public int getFlowHourMinFiveTotalPre(TreeMap<String, FlowHourMinFiveUnit> fhmfTotal, String minFivePreEnd) {
        //
        SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHead = fhmfTotal.headMap(minFivePreEnd);
        Collection<FlowHourMinFiveUnit> c = fhmfTotalHead.values();
        Vector<FlowHourMinFiveUnit> v = new Vector<FlowHourMinFiveUnit>(c);
        int size = v.size();
        FlowHourMinFiveUnit flowHourMinFiveUnit;
        int total = 0;
        // 最接近minFivePreEnd
        for (int i = size - 1; i >= 0; i--) {
            flowHourMinFiveUnit = v.get(i);
            total = flowHourMinFiveUnit.getTraffic();
            // 客流不为0
            if (total != 0) {
                return total;
            }

        }
        return total;

    }

    public int getFlowHourMinFiveTotalPreForAllDate(
            TreeMap<String, FlowHourMinFiveUnit> fhmfTotal,
            TreeMap<String, FlowHourMinFiveUnit> fhmfTotalPre,
            String minFivePreEnd) {


        SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHead = null;

        int traffic = 0;

        // 查找当天缓存0230-2355
        // 如是0235,上一个分钟汇总数据作0处理
        if (minFivePreEnd.equals(FrameCodeConstant.SQUAD_TIME)) {
            return 0;
        }

        /*
         * 00-0230分钟客流：当前分钟总客流－最近不为0的分钟客流.
         * 最近分钟客流查找：当前自然日小于当前分钟的客流＋上一自然日0230－2355的分钟客流
         * 0230-2355分钟客流：当前分钟总客流－最近不为0的分钟客流.
         * 最近分钟客流查找：当前自然日大于0230小于当前分钟的客流,如果是0230，则为0
         */
        fhmfTotalHead = getFlowHourMinFiveTotalForCurrentDate(fhmfTotal,
                minFivePreEnd);

        traffic = this.getFlowHourMinFiveTotalPreForDate(minFivePreEnd,
                fhmfTotalHead);

        // 当天缓存没有找到，如是00-0230分钟客流再找上一天缓存，上一天缓存从0230开始
        if (traffic == 0) {
            if (MessageUtil.isTimeBetween00And0230(minFivePreEnd)) {
                fhmfTotalHead = getFlowHourMinFiveTotalForPreDate(fhmfTotalPre,
                        minFivePreEnd);
                traffic = this.getFlowHourMinFiveTotalPreForDate(minFivePreEnd,
                        fhmfTotalHead);
            }
        }
        return traffic;

    }

    public SortedMap<String, FlowHourMinFiveUnit> getFlowHourMinFiveTotalForCurrentDate(
            TreeMap<String, FlowHourMinFiveUnit> fhmfTotal, String minFivePreEnd) {

        SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHeadTemp = fhmfTotal
                .headMap(minFivePreEnd);

        // 00-0230分钟客流,查找当前自然日小于当前分钟的客流
        if (MessageUtil.isTimeBetween00And0230(minFivePreEnd)) {
            return fhmfTotalHeadTemp;
        }

        SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHead = null;

        // 0230-2355分钟客流,查找当前自然日大于0230小于当前分钟的客流
        if (fhmfTotalHeadTemp != null) {
            fhmfTotalHead = fhmfTotalHeadTemp
                    .tailMap(FrameCodeConstant.SQUAD_TIME);
        }

        return fhmfTotalHead;

    }

    public SortedMap<String, FlowHourMinFiveUnit> getFlowHourMinFiveTotalForPreDate(
            TreeMap<String, FlowHourMinFiveUnit> fhmfTotal, String minFivePreEnd) {
        SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHead = fhmfTotal
                .tailMap(FrameCodeConstant.SQUAD_TIME);

        return fhmfTotalHead;

    }

    public int getFlowHourMinFiveTotalPreForDate(String minFivePreEnd,
                                                 SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHead) {

        Collection<FlowHourMinFiveUnit> c = fhmfTotalHead.values();
        Vector<FlowHourMinFiveUnit> v = new Vector<FlowHourMinFiveUnit>(c);
        int size = v.size();
        FlowHourMinFiveUnit flowHourMinFiveUnit;
        int total = 0;
        for (int i = size - 1; i >= 0; i--) {
            flowHourMinFiveUnit = (FlowHourMinFiveUnit) v.get(i);
//            logger.info(flowHourMinFiveUnit.toString());
            total = flowHourMinFiveUnit.getTraffic();
            if (total != 0) {
                return total;
            }

        }
        return total;

    }

    private int getHourMinFiveTraffic(String minFive, int hTotalTraffic,
                                      FlowHourMinFive fhmfPre) {
        int hTraffic;
        int hPreTotalTraffic;
        String minFivePre;
        // if (this.isFirstInSquadTime(minFive))// 运营时间第一个分钟客流与总客流相同
        // hTraffic = hTotalTraffic;
        // else {
        if (minFive.equals("0000")) {// 自然日的第一个分钟客流需使用上一个自然日的5分钟客流计算
            minFivePre = "2400";
            // 获取上一个自然日最近客流不为0的上一个5分钟得总客流
            hPreTotalTraffic = this.getFlowHourMinFiveTotalPre(
                    fhmfPre.flowHourMinFiveTotal, minFivePre);
            /*
             * minFivePre="2355"; // 获取上一个自然日上一个5分钟得总客流 hPreTotalTraffic =
             * ((FlowHourMinFiveUnit
             * )fhmfPre.flowHourMinFiveTotal.get(minFivePre)).getTraffic();
             */
            // 当前5分钟客流＝当前总客流－上一个5分钟客流
            hTraffic = hTotalTraffic - hPreTotalTraffic;

        } else {
            // 获取上一个最近客流不为0的5分钟得总客流，如果分钟在00-0230，查找除当天缓存外，还需查找上一天的缓存从0230开始
            // 查找当天缓存
            hPreTotalTraffic = this.getFlowHourMinFiveTotalPreForAllDate(
                    this.flowHourMinFiveTotal, fhmfPre.flowHourMinFiveTotal,
                    minFive);
            /*
             * //获取上一个5分钟时间 minFivePre = this.getMinFivePreOne(minFive);
             * //获取上一个5分钟得总客流 hPreTotalTraffic =
             * ((FlowHourMinFiveUnit)this.flowHourMinFiveTotal
             * .get(minFivePre)).getTraffic();
             */
            // 当前5分钟客流＝当前总客流－上一个5分钟客流
            hTraffic = hTotalTraffic - hPreTotalTraffic;
        }
        // }
        return hTraffic;

    }

    private int getHourMinFiveTraffic(String minFive, int hTotalTraffic) {
        int hTraffic;
        int hPreTotalTraffic;
        String minFivePre;
        if (minFive.equals("0000"))// 运营时间第一个分钟客流与总客流相同
        {
            hTraffic = hTotalTraffic;
        } else {

            // 获取上一个5分钟时间
            minFivePre = this.getMinFivePreOne(minFive);
            // 获取上一个5分钟得总客流
            hPreTotalTraffic = ((FlowHourMinFiveUnit) this.flowHourMinFiveTotal
                    .get(minFivePre)).getTraffic();
            // 当前5分钟客流＝当前总客流－上一个5分钟客流
            hTraffic = hTotalTraffic - hPreTotalTraffic;

        }
        return hTraffic;

    }

    private String getMinFivePreOne(String minFive) {
        String temp;
        String hour = minFive.substring(0, 2);
        String min = minFive.substring(2);
        int len = FrameCodeConstant.FIVE_HOUR_MINS.length;
        for (int i = 0; i < len; i++) {
            temp = FrameCodeConstant.FIVE_HOUR_MINS[i];
            if (temp.equals(min)) {
                if (i == 0) {// 0900返回0855
                    hour = this.getHourPre(hour);
                    return hour + FrameCodeConstant.FIVE_HOUR_MINS[len - 1];
                } else {
                    return hour + FrameCodeConstant.FIVE_HOUR_MINS[i - 1];
                }
            }
        }
        return "9999";
    }

    private String getHourPre(String hour) {
        int hPre = new Integer(hour).intValue() - 1;
        if (hPre < 10) {
            return "0" + hPre;
        } else {
            return "" + hPre;
        }
    }

    /**
     * 是否是合法的流量
     *
     * @param traffic
     * @return traffic大于0返回
     */
    private boolean isLegalTraffic(int traffic) {
        return traffic > 0;
    }
}
