/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

import com.goldsign.lib.db.util.DbcpHelper;

/**
 * 
 * @author hejj
 */
public class FrameDBConstant {

	public static DbcpHelper OP_DBCPHELPER;
	public static DbcpHelper CM_DBCPHELPER;
	public static DbcpHelper TK_DBCPHELPER;
//	public static DbcpHelper EMERGENT_DBCPHELPER;
	public static int CPListenerThreadSleepTime = 1000;
	public static String TestSqlForOP = "";
	public static String TestSqlForCM = "";
	public static String TestSqlForTK = "";
	public static String TestSqlForEmergent = "";
	public static int SqlMsgHandleSleepTime = 100;
	public static int FLAG_OP = 0;
	public static int FLAG_CM = 1;
	public static int FLAG_TK = 2;
	public static int FLAG_EMERGENT = 3;
	public static boolean isWriteEmergentTraffic = false;// 控制是否写应急指挥中心客流

	public static boolean isWriteEmergentTrafficForDb = true;
        
        public static String TABLE_PREFIX = "w_";
        public static String COM_COMMU_P = "w_acc_cm."+TABLE_PREFIX;
        public static String COM_ST_P = "w_acc_st."+TABLE_PREFIX;
        public static String COM_TK_P = "w_acc_tk."+TABLE_PREFIX;
        public static String COM_COMMU = "w_acc_cm.";
        public static String COM_ST = "w_acc_st.";
        public static String COM_TK = "w_acc_tk.";
}
