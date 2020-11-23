/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * 增加字符过滤，修改数量限制
 * @author lind
 */
public class Parameter06Util {
    
    public static int bankistMocMax = 100000;//交通部白名单
    public static int blacklistMocMax = 100000;//交通部黑名单
    public static int blacklistOctMax = 20000;//公交
    public static int blacklistMetroMax = 20000;//地铁
    public static int blacklistSecMax = 20;
    public static int blacklistLength = 20;
    public static String patternReg = "^[0-9]*$";
    public static String patternRegMtr = "^00004100.[0-9]*$";
    public static String sqlStr = "select  config_name, config_value from "+FrameDBConstant.COM_COMMU_P+"CM_CFG_SYS";
    
    private static Logger logger = Logger.getLogger(Parameter06Util.class.getName());
    
    /*
    * 获取配置
    */
   public static void getLimit() {
           DbHelper dbHelper = null;
           boolean result;

           try {
                   dbHelper = new DbHelper("", FrameDBConstant.CM_DBCPHELPER.getConnection());
                   result = dbHelper.getFirstDocument(sqlStr);
                   while (result) {
                       if(dbHelper.getItemValue("config_name").trim().equals("banklist.moc.max")){
                           bankistMocMax = dbHelper.getItemIntValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("blacklist.moc.max")){
                           blacklistMocMax = dbHelper.getItemIntValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("blacklist.oct.max")){
                           blacklistOctMax = dbHelper.getItemIntValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("blacklist.metro.max")){
                           blacklistMetroMax = dbHelper.getItemIntValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("blacklist.section.max")){
                           blacklistSecMax = dbHelper.getItemIntValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("blacklist.length")){
                           blacklistLength = dbHelper.getItemIntValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("pattern.reg")){
                           patternReg = dbHelper.getItemValue("config_value");
                       }
                       if(dbHelper.getItemValue("config_name").trim().equals("pattern.reg.mtr")){
                           patternRegMtr = dbHelper.getItemValue("config_value");
                       }
                       result = dbHelper.getNextDocument();
                   }

           } catch (SQLException e) {
                   logger.error("获取黑名单限制数错误 ", e);
           } finally {
                   PubUtil.finalProcess(dbHelper);
           }
   }

   public static Vector<String[]> getRecordNotOverLimit(Vector<String[]> v, int limit) {
           Vector<String[]> vLimit = new Vector<String[]>();
           int size = v.size();
           int randomIndex;
           int randomNum;
           // 集合数超过限定数
           if (size > limit) {
                   randomIndex = (int) (Math.random() * size);// 随机数作为集合索引
                   randomNum = size - randomIndex;// 随机数索引包含的数量
                   logger.warn("randomIndex=" + randomIndex);
                   if (randomNum >= limit) {// 随机数索引包含的数量大于等于限定数
                           vLimit.addAll(v.subList(randomIndex, randomIndex + limit));// 取从随机数索引开始的limit条数量

                   } else {// 随机数索引包含的数量小于限定数
                           vLimit.addAll(v.subList(0, limit - randomNum));// 缺少的数从头开始取
                           vLimit.addAll(v.subList(randomIndex, size));// 取从随机数索引开始
                   }

           } else// 集合数不超过限定数
           {
                   vLimit.addAll(v);
           }
           return vLimit;
   }
   
   /*
    * 正则表达式匹配
    */
   public static boolean matchs(String str, String reg){
       return Pattern.compile(reg).matcher(str).find();
   }

   /*
    * 数组正则表达式匹配,长度判断
    */
   public static boolean patternMacher(String field) {
       return matchs(field, patternReg) && field.length()>=blacklistLength;
   }
   
   /*
    * 数组正则表达式匹配,地铁卡
    */
   public static boolean matchMtr(String field) {
       return matchs(field, patternRegMtr) && field.length()>=blacklistLength;
   }

   /*
    * 插入含非法字符黑名单
    */
   public static void inErrBlackDown(Vector<String[]> errV, String type) {
       if(errV == null || errV.size()==0){
           return;
       }
       DbHelper dbHelper = null;
       String sql = "insert into "+FrameDBConstant.COM_COMMU_P+"CM_ERR_PRM_BLACK_DOWN values (?,?,?,sysdate)";

       try {
               dbHelper = new DbHelper("", FrameDBConstant.CM_DBCPHELPER.getConnection());
               
               dbHelper.setAutoCommit(false);
               for(int i=0; i<errV.size(); i++){
                   String[] fieldTypes = {errV.get(i)[0].toString(),type,errV.get(i)[2].toString()};
                   dbHelper.executeUpdate(sql,fieldTypes);
                   dbHelper.close();
               }

               dbHelper.commitTran();
               dbHelper.setAutoCommit(true);
               logger.info("非法黑名单"+errV.size()+"插入错误表！");
       } catch (SQLException e) {
               logger.error("插入非法黑名单错误 ", e);
       } finally {
               PubUtil.finalProcess(dbHelper);
       }
   }
   
   
   /*
    * 插入含非法字符黑名单段
    */
   public static void inErrBlackDownSEC(Vector<String[]> errV, String type) {
       if(errV == null || errV.size()==0){
           return;
       }
       DbHelper dbHelper = null;
       String sql = "insert into "+FrameDBConstant.COM_COMMU_P+"CM_ERR_PRM_BLACK_SEC_DOWN values (?,?,?,?,sysdate)";

       try {
               dbHelper = new DbHelper("", FrameDBConstant.CM_DBCPHELPER.getConnection());
               
               dbHelper.setAutoCommit(false);
               for(int i=0; i<errV.size(); i++){
                   String[] fieldTypes = {errV.get(i)[0].toString(),errV.get(i)[1].toString(),type,errV.get(i)[3].toString()};
                   dbHelper.executeUpdate(sql,fieldTypes);
               }

               dbHelper.commitTran();
               dbHelper.setAutoCommit(true);
               logger.info("非法黑名单段"+errV.size()+"插入错误表！");
       } catch (SQLException e) {
               logger.error("插入非法黑名单段错误 ", e);
       } finally {
               PubUtil.finalProcess(dbHelper);
       }
   }
    
}
