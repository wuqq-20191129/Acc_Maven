/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.util.Vector;

/**
 * 
 * @author hejj
 */
public class TMMonitorResult {

	private boolean existedFailureThread = false;
	private Vector<TMThreadStatusVo> vos = new Vector<TMThreadStatusVo>();

	/**
	 * @return the existedFailureThread
	 */
	public boolean isExistedFailureThread() {
		return existedFailureThread;
	}

	/**
	 * @param existedFailureThread
	 *            the existedFailureThread to set
	 */
	public void setExistedFailureThread(boolean existedFailureThread) {
		this.existedFailureThread = existedFailureThread;
	}

	/**
	 * @return the vos
	 */
	public Vector<TMThreadStatusVo> getVos() {
		return vos;
	}

	/**
	 * @param vos
	 *            the vos to set
	 */
	public void setVos(Vector<TMThreadStatusVo> vos) {
		this.vos.addAll(vos);
	}

	public void setVos(TMThreadStatusVo vo) {
		this.vos.add(vo);
	}
}
