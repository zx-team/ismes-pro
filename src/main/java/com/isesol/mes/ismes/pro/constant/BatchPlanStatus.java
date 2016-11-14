package com.isesol.mes.ismes.pro.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 批次计划状态
 */
public class BatchPlanStatus {

	/** 分批状态 */
	public static final String 未下发 = "10";
	public static final String 已下发 = "20";
	public static final String 计划未制作 = "20";
	public static final String 计划制作中 = "30";
	public static final String 工单已生成 = "40";
	public static final String 工单已下发 = "50";
	// 去除工单全部下发状态
	//public static final String 工单全部下发 = "60";
	public static final String 加工中 = "70";
	public static final String 加工完成 = "80";
	public static final String 已终止 = "90";
	
	/** 生产任务状态l */
	public static final String undo = "10";  //未执行
	public static final String doing = "20"; //执行中
	public static final String done = "30"; //已完成
	public static final String stop = "40"; //已终止
	
	public static List<String> statusList = new ArrayList<String>();
	static{
		statusList.add(未下发);
		statusList.add(已下发);
		statusList.add(计划制作中);
		statusList.add(工单已生成);
		statusList.add(工单已下发);
		/** 去除工单全部下发状态 */
		//statusList.add(工单全部下发);
		statusList.add(加工中);
		statusList.add(加工完成);
		statusList.add(已终止);
	}
	
	public static List<String> scrwStateList = new ArrayList<String>();
	static{
		scrwStateList.add(undo);
		scrwStateList.add(doing);
		scrwStateList.add(done);
		scrwStateList.add(stop);
	}
}
