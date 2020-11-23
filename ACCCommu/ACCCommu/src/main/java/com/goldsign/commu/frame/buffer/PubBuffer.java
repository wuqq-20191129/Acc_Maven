/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.buffer;

import java.util.HashMap;
import java.util.TreeMap;

import com.goldsign.commu.app.vo.FlowHourMinFive;

/**
 * 
 * @author hejj
 */
public class PubBuffer {
	// 进站客流缓存
	public static TreeMap bufferFlowEntry = new TreeMap();
	// 出站客流缓存
	public static TreeMap bufferFlowExit = new TreeMap();
	// 5分钟进站客流缓存 :主键为日期，
	// 值为所有车站的所有5分钟客流，使用hashMap(对象封装，主键为车站票卡，值为车站所有5分钟客流，使用HourMinFive对象封装)
	public static TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlowMinFiveEntry = new TreeMap<String, HashMap<String, FlowHourMinFive>>();
	// 5分钟出站客流缓存:主键为日期，
	// 值为所有车站的所有5分钟客流，使用hashMap(对象封装，主键为车站票卡，值为车站所有5分钟客流，使用HourMinFive对象封装)
	public static TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlowMinFiveExit = new TreeMap<String, HashMap<String, FlowHourMinFive>>();

}
