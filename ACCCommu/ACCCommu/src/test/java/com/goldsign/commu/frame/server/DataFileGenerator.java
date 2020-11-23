package com.goldsign.commu.frame.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-05-13
 * @Time: 11:07
 */
public class DataFileGenerator {
    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<String>();
//        File file = new File("D:\\工作_总目录\\My\\My Work\\#999 数据交换系统\\乌市\\需求\\#6 TCC相关开发\\模拟数据\\ACC_TCC_KYL_LINE_05MIN.csv");
        File file = new File("D:\\JDK\\FLOWMONITOR\\GBK");
        if (file.isFile() && file.exists())
        { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), "utf-8");// 考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null)
            {
                list.add(lineTxt);
            }
            bufferedReader.close();
            read.close();
           for(String vo:list){
               System.out.println("vo = " + vo);
           }
        }
    }
}
