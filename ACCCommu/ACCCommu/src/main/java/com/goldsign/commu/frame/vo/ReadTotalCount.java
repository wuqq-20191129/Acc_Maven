/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class ReadTotalCount {

	private int count = 0;

	public ReadTotalCount() {
	}

	public void add() {
		count++;
	}

	public int get() {
		return count;
	}
}
