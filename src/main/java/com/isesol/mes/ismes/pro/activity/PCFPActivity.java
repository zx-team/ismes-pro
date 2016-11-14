package com.isesol.mes.ismes.pro.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.model.api.exception.ModelOperationException;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;
import com.isesol.mes.ismes.pro.constant.BatchPlanStatus;
import com.isesol.mes.ismes.pro.constant.BatchStatus;
import com.isesol.mes.ismes.pro.constant.TableConstant;
import com.isesol.mes.ismes.pro.constant.TaskStatus;

/**
 * 批次分配
 */
public class PCFPActivity {
	
	private Logger log4j = Logger.getLogger(PCFPActivity.class);
	
	private Date string2Date(String timeStr){
	    if(!timeStr.contains(":")){
	    	timeStr = timeStr + " 00:00:00";
	    }
	    String format ="";
	    if(timeStr.contains("-")){
	    	format = "yyyy-MM-dd HH:mm:ss";
	    }
	    if(timeStr.contains("/")){
	    	format = "yyyy/MM/dd HH:mm:ss";
	    }
	    SimpleDateFormat formatter=new SimpleDateFormat(format);  
	    try {
			return formatter.parse(timeStr);
		} catch (ParseException e) {
			log4j.info("时间转换出现异常;;;"+timeStr);
			log4j.error(e.getMessage());
			return null;
		} 
	}
	
	/**
	 * 初始化批次分配的页面
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws ModelOperationException
	 */
	@SuppressWarnings("unchecked")
	public String index(Parameters parameters,Bundle bundle) throws ModelOperationException {
		String scrwid = (String) parameters.get("scrwid");
//		String mplh = (String) parameters.get("mplh");
		//根据id查询，只应该查询到一条生产任务对象
		Dataset datasetScrw = Sys.query(TableConstant.生产任务, "scrwid,"
				+ "ljid,"/*零件id*/+ "jgsl,"/*加工数量*/ +"jgksrq,"/*加工开始日期*/ +"jgwcrq,"/*加工完成日期*/
				+"bz,"/*备注*/ +"scrwlrsj,"/*生产任务录入时间*/ +"scrwztdm,"/*生产任务状态代码*/ 
				+"scrwlydm,"/*生产任务来源代码*/ +"scrwfpztdm,"/*生产任务分批状态代码*/
				+"scrwbh,"/*生产任务编号*/ +"scph,"+"mplh,wlid"
				,"scrwid = ? " , null, null,new Object[]{scrwid});
//				,"scrwid = ? and mplh = ? " , null, null,new Object[]{scrwid,mplh});
		Map<String, Object> scrw = datasetScrw.getMap();
		bundle.put("scrw", scrw);
		
//		//下边是模态窗口需要的数据
//		//零件信息
		parameters.set("ljid", scrw.get("ljid"));
		Bundle partsInfo_bundle = Sys.callModuleService("pm", "partsInfoService", parameters);
		String wlid = scrw.get("wlid").toString();
		if(partsInfo_bundle!=null){
			Map<String,Object> partsInfoMap = (Map<String, Object>) partsInfo_bundle.get("partsInfo");
			if(partsInfoMap != null && partsInfoMap.get("ljlbdm") != null){
				String ljlbdm = partsInfoMap.get("ljlbdm").toString() ;
				String ljlbmc = "";
				if("10".equals(ljlbdm)){
					ljlbmc = "自制件";
				}
				if("20".equals(ljlbdm)){
					ljlbmc = "采购件";
				}
				partsInfoMap.put("ljlbmc", ljlbmc);
			}
			bundle.put("partsInfo", partsInfoMap);
			
			//毛坯规格
//			if(partsInfoMap.get("clbh") != null && StringUtils.isNotBlank(partsInfoMap.get("clbh").toString())){
//				Parameters p = new Parameters();
//				p.set("query_wlgg_eq", partsInfoMap.get("clbh").toString());
//				Bundle mp_bundle = Sys.callModuleService("mm", "mmservice_wlxxkc", p);
//				if(mp_bundle != null && mp_bundle.get("wlxxMap") != null){
//					wlid = ((Map<String,Object>)mp_bundle.get("wlxxMap")).get("wlid").toString();
//				}
//			}
			
		}
		// 查询零件库存
		parameters.set("ljid", scrw.get("ljid")+"");
		Bundle lj_warehouse_bundle = Sys.callModuleService("wm", "lj_warehouseInfoByljidService", parameters);
		if(lj_warehouse_bundle!=null){
			bundle.put("ljkcsl", lj_warehouse_bundle.get("warehouseNum"));
		}
		
		
		//物料信息
		parameters.set("wlid", wlid);
//		parameters.set("mplh", scrw.get("mplh"));
		Bundle wl_bundle = Sys.callModuleService("mm", "materielInfoByWlidService", parameters);
		if(wl_bundle != null){
			Map<String,Object> wl_map = (Map<String, Object>) wl_bundle.get("materielInfo");
			bundle.put("materielInfo", wl_map);
			bundle.put("materielInfo_wlid", wl_map.get("wlid"));
		}

		//物料库存
		Bundle wl_warehouse_bundle = Sys.callModuleService("wm", "wmService_queryWlkcByWlidLh", parameters);
		if(wl_warehouse_bundle!=null){
			List<Map<String, Object>> list = (List<Map<String, Object>>) wl_warehouse_bundle.get("wlkcxx");
			bundle.put("wlkcsl", list.get(0).get("kcsl"));
		}
		return "pcfp";
	}
	
	/**
	 * 加载页面上的table数据
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws ModelOperationException
	 */
	public String table(Parameters parameters,Bundle bundle) throws ModelOperationException {
		String scrwid = (String) parameters.get("scrwid");
		//根据生产任务对象查询到多条生产批次
		Dataset datasetScpc = Sys.query(TableConstant.生产任务批次, "scrwpcid,scrwid,ljid,pcbh,"
				+ "pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq", "scrwid = ? ",  null,"pcbh desc,"
						+ " pcjhksrq desc ,pcjhzdrq ",0,1000,new Object[]{scrwid});
		
		Dataset datasetScrw = Sys.query(TableConstant.生产任务,"scrwbh,scph",
				" scrwid = ? ",null,null,new Object[]{scrwid});
		List<Map<String, Object>> scpc = datasetScpc.getList();
		bundle.put("rows", scpc);
		bundle.put("totalRecord", datasetScpc.getTotal());
		bundle.put("currentPage", 1);
		bundle.put("records", datasetScpc.getCount());
		
		return "json:";
		
	}
	
	/**
	 * 得到进度条的信息
	 * 分为实际  和  计划部分
	 * @param parameters
	 * @param bundle
	 */
	public String progressInfo(Parameters parameters,Bundle bundle){
		String scrwid = (String) parameters.get("scrwid");
		
		Dataset datasetScrw = Sys.query(TableConstant.生产任务, "jgsl"
				,"scrwid = ? " , null, null,new Object[]{scrwid});
		Map<String, Object> scrw = datasetScrw.getMap();
		//任务加工数量
		int jgsl = (Integer) scrw.get("jgsl");
		bundle.put("jgsl", jgsl);
		
//		//查询已下发和未下发的数量
		Dataset datasetYxfNum = Sys.query(TableConstant.生产任务批次, "scrwpcid,pcsl", 
				"scrwid = ?  and (pcjhztdm = '" + BatchPlanStatus.已下发 +"'"
						+ " or pcjhztdm = '" + BatchPlanStatus.计划制作中 +"'"
						+ " or pcjhztdm = '" + BatchPlanStatus.工单已生成 +"'"
						+ " or pcjhztdm = '" + BatchPlanStatus.工单已下发 +"'"
//						+ " or pcjhztdm = '" + BatchPlanStatus.工单全部下发 +"'"
						+ " or pcjhztdm = '" + BatchPlanStatus.加工中 +"'"
						+ " or pcjhztdm = '" + BatchPlanStatus.加工完成 +"')", null, null,new Object[]{scrwid});
		//当前批次实际的下发数量
		int yxfTrueNum = 0 ;
		List<Map<String,Object>> yxfList = datasetYxfNum.getList();
		if(CollectionUtils.isNotEmpty(yxfList)){
			for(Map<String,Object> map : yxfList){
				int pcsl = (Integer)map.get("pcsl");
				yxfTrueNum = yxfTrueNum + pcsl;
			}
		}
		bundle.put("yxfTrueNum", yxfTrueNum);
		//如果实际下发的数量>任务数量，将 yxfNum 设置为 任务数量，反之 ，设置为实际下发数量
		int yxfNum = yxfTrueNum > jgsl ? jgsl : yxfTrueNum;
		bundle.put("yxfNum", yxfNum);
		
		//已下发的百分比
		int yxfTruePercent = yxfTrueNum * 100 / jgsl;
		int yxfPercent = yxfNum * 100 / jgsl;
		bundle.put("yxfTruePercent", yxfTruePercent);
		bundle.put("yxfPercent", yxfPercent);
		
		
		Dataset datasetWxfNum = Sys.query(TableConstant.生产任务批次, "scrwpcid,pcsl", 
				"scrwid = ?   and pcjhztdm = '" + BatchPlanStatus.未下发 + "'", null, null,new Object[]{scrwid});
		//当前批次实际的未下发数量
		int wxfTrueNum = 0 ;
		List<Map<String,Object>> wxfList = datasetWxfNum.getList();
		if(CollectionUtils.isNotEmpty(wxfList)){
			for(Map<String,Object> map : wxfList){
				int pcsl = (Integer)map.get("pcsl");
				wxfTrueNum = wxfTrueNum + pcsl;
			}
		}
		bundle.put("wxfTrueNum", wxfTrueNum);
		int wxfNum =  (jgsl - yxfNum) > wxfTrueNum ? wxfTrueNum : jgsl - yxfNum ;
		bundle.put("wxfNum", wxfNum);
		
		//未下发的百分比
		int wxfTruePercent = wxfTrueNum * 100 / jgsl;
		int wxfPercent = wxfNum * 100 / jgsl;
		bundle.put("wxfTruePercent", wxfTruePercent);
		bundle.put("wxfPercent", wxfPercent);
		
		//未分批
		int wfpNum = jgsl - yxfTrueNum - wxfTrueNum;
		wfpNum = wfpNum < 0 ? 0 : wfpNum;
//		int wfpPercent = wfpNum * 100 / jgsl;
		int wfpPercent = 100 - yxfPercent - wxfPercent;
		bundle.put("wfpNum", wfpNum);
		bundle.put("wfpPercent", wfpPercent);
		return "json:";
	}
	
	/**
	 * 模态窗口 零件库存中的table
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws ModelOperationException
	 */
	public String ljkcTable(Parameters parameters,Bundle bundle) throws ModelOperationException {
		if(parameters.get("ljid") == null || StringUtils.isBlank(parameters.get("ljid").toString())){
			log4j.info("模态窗口 查询零件库存，零件id不能为空");
			return "json:";
		}
		String ljid = parameters.get("ljid").toString();
		parameters.set("ljid", ljid);
		Bundle lj_warehouse_bundle = Sys.callModuleService("wm", "lj_warehouseInfoByljidService", parameters);
		bundle.put("rows", lj_warehouse_bundle.get("warehouseInfo"));
		bundle.put("totalRecord", 100);
		bundle.put("currentPage", 1);
		bundle.put("records", 100);
		return "json:";
		
	}
	
	/**
	 * 模态窗口 物料库存中的table
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws ModelOperationException
	 */
	public String wlkcTable(Parameters parameters,Bundle bundle) throws ModelOperationException {
		if(parameters.get("wlid") == null || StringUtils.isBlank(parameters.get("wlid").toString())){
			log4j.info("模态窗口 查询物料件库存，物料id不能为空");
			return "json:";
		}
		String wlid = parameters.get("wlid").toString();
//		//物料库存
		parameters.set("wlid", wlid);
		Bundle b = Sys.callModuleService("wm", "wl_warehouseInfoByWlidService", parameters);
		if(b==null){
			log4j.info("根据物料id " + wlid + " 查询物料库存信息，返回结果集为空");
			bundle.put("rows", null);
		}else{
			bundle.put("rows", b.get("warehouseInfo"));
		}
		bundle.put("totalRecord", 100);
		bundle.put("currentPage", 1);
		bundle.put("records", 100);
		
		return "json:";
	}
	
	/**
	 * 分批  保存批次
	 */
	public void saveNewPc(Parameters parameters,Bundle bundle){
		//加工数量
		int dyjgsl = parameters.getInteger("dyjgsl");
		//加工批次
		int dyjgpc = parameters.getInteger("dyjgpc");
		//任务id
		String scrwid = parameters.getString("scrwid");
		//零件id
		String ljid = parameters.getString("ljid");
		
		String pcmc = parameters.getString("pcmc");
		
		//
		String pcjhksrq_str =  parameters.getString("pcjhksrq");
		Date pcjhksrq = string2Date(pcjhksrq_str);
		//
		String pcjhwcrq_str =  parameters.getString("pcjhwcrq");
		Date pcjhwcrq = string2Date(pcjhwcrq_str);
		
		
		if(dyjgsl<dyjgpc){
//			alert("加工数量不能比批次小!");
//			bundle.put(name, "加工数量不能比批次小!");
//			return "pcfp";
		}
		int num = dyjgsl/dyjgpc;
		int last = dyjgsl;
		List<Map<String,Object>> insertList = new ArrayList<Map<String,Object>>();
		for (int i=1;i<dyjgpc;i++){
			Map<String,Object> map = new HashMap<String, Object>();
			Date now = new Date();
			map.put("scrwid", scrwid);
			map.put("ljid", ljid);
			map.put("pcsl", num);
			map.put("pcjhztdm", "10");
			map.put("pcjhzdrq", now);
			map.put("pcmc", pcmc);
			
			map.put("pcjhksrq", pcjhksrq);
			map.put("pcjhwcrq", pcjhwcrq);
			
			last = last - num;
			insertList.add(map);
		}
		Map<String,Object> map = new HashMap<String, Object>();
		Date now = new Date();
		map.put("scrwid", scrwid);
		map.put("ljid", ljid);
		map.put("pcsl", last);
		map.put("pcjhztdm", "10");
		map.put("pcjhzdrq", now);
		map.put("pcjhksrq", pcjhksrq);
		map.put("pcjhwcrq", pcjhwcrq);
		map.put("pcmc", pcmc);
		last = last - num;
		insertList.add(map);
		Sys.insert(TableConstant.生产任务批次, insertList);
	}
	
	/**
	 * 删除批次
	 * @param parameters
	 * @param bundle
	 */
	public void deletePc(Parameters parameters,Bundle bundle){
		//任务id
		String scrwpcid = parameters.getString("scrwpcid");
		if(StringUtils.isBlank(scrwpcid)){
			bundle.setError("删除批次，id不能为空！");
			return;
		}
		Dataset dataset = Sys.query(TableConstant.生产任务批次, "pcjhztdm", " scrwpcid = ? ", null, new Object[]{scrwpcid});
		Map<String,Object> pcjhztMap = dataset.getMap();
		if(MapUtils.isNotEmpty(pcjhztMap) && pcjhztMap.get("pcjhztdm")!= null){
			String pcjhztdm = pcjhztMap.get("pcjhztdm").toString();
			if(!BatchPlanStatus.未下发.equals(pcjhztdm)){
				bundle.setError("批次状态不是未下发，不能删除！");
				return;
			}
		}
		
		Sys.delete(TableConstant.生产任务批次, " scrwpcid = ? ", scrwpcid);
		
		String scrwid =  parameters.getString("scrwid");
		Dataset dataset1 = Sys.query(TableConstant.生产任务批次, "scrwpcid", " scrwid = ? ", null, new Object[]{scrwid});
		if(MapUtils.isEmpty(dataset1.getMap())){
			Map<String,Object> scrwMap = new HashMap<String, Object>();
			scrwMap.put("scrwfpztdm", BatchStatus.未分批);
			Sys.update(TableConstant.生产任务, scrwMap, " scrwid = ? ", scrwid);
		}
	}
	
	/**
	 * 修改批次
	 * @param parameters
	 * @param bundle
	 */
	public void updatePc(Parameters parameters,Bundle bundle){
		String scrwpcid = parameters.getString("scrwpcid");
		Map<String,Object> map = new HashMap<String, Object>();
		//String pcmc= parameters.getString("pcmc");
		//map.put("pcmc", pcmc);
		Integer pcsl = parameters.getInteger("pcsl");
		map.put("pcsl", pcsl);
		String pcjhksrqStr = (String) parameters.get("pcjhksrq");
		if(StringUtils.isNotBlank(pcjhksrqStr)){
			Date pcjhksrq =  string2Date(pcjhksrqStr);
			map.put("pcjhksrq", pcjhksrq);
		}
		String pcjhwcrqStr = (String) parameters.get("pcjhwcrq");
		if(StringUtils.isNotBlank(pcjhwcrqStr)){
			Date pcjhwcrq =  string2Date(pcjhwcrqStr);
			map.put("pcjhwcrq", pcjhwcrq);
		}
		
		Sys.update(TableConstant.生产任务批次, map, " scrwpcid = ? ", scrwpcid);
	}
	
	/**
	 * 下发批次
	 * 修改当前批次状态、批次编号
	 * 生产任务的状态、生产任务编号
	 */
	public void xfpc(Parameters parameters,Bundle bundle){
		//得到最大的序号
		String scrwid = (String) parameters.get("scrwid");
		String scrwpcid = (String) parameters.get("scrwpcid");
		
		Dataset datasetScrw = Sys.query(TableConstant.生产任务, "scrwbh,"/*生产任务编号*/ + "scrwztdm,"/*生产任务状态代码*/
				+ "scrwfpztdm,"/*生产任务批次状态代码*/+"jgsl"/*加工数量*/ + ",jgwcrq,jgksrq",
				"scrwid = ? ", null, null, new Object[]{scrwid});
		Map<String, Object> scrw = datasetScrw.getMap();
		if(scrw.get("scrwbh") == null || StringUtils.isBlank(scrw.get("scrwbh").toString())){
			bundle.setError("当前生产任务没有生成编号，请联系系统管理员");
			return;
		}
		String scrwbh = scrw.get("scrwbh").toString();
		Map<String,Object> map = new HashMap<String, Object>();
		//生产任务编号+2位流水序号
		String lsh = String.valueOf(Sys.getNextSequence(scrwbh.replace("-", "")));
		lsh = lsh.length() == 1 ? "0" + lsh : lsh ;
		map.put("pcbh", scrwbh + lsh);//生产批次编号
		map.put("pcjhztdm", BatchPlanStatus.已下发);//生产批次状态
		//修改当前批次状态，并且给当前批次赋予编号
		Sys.update(TableConstant.生产任务批次, map, "scrwpcid = ?" , scrwpcid);
		map.clear();
		
		
		
		//修改任务的任务状态和分批状态
		//任务状态是不是执行中的话，改成执行中
		map.put("scrwztdm", TaskStatus.执行中);
		//批次状态 
		Dataset datasetScpc = Sys.query(TableConstant.生产任务批次, "pcsl", "scrwid = ? "
				+ "and pcjhztdm != '10' and pcjhztdm != '90'" ,
				null, null,new Object[]{scrwid});
		List<Map<String, Object>> scpcList = datasetScpc.getList();
		int compareNumber = 0 ;
		if(CollectionUtils.isNotEmpty(scpcList)){
			for(Map<String, Object> m : scpcList){
				compareNumber = compareNumber +(Integer) m.get("pcsl");
			}
		}
		String xfzt = compareNumber >=(Integer) scrw.get("jgsl") ? BatchStatus.全部分批 : BatchStatus.部分分批;
		map.put("scrwfpztdm", xfzt);
		Sys.update(TableConstant.生产任务, map, "scrwid = ?" , scrwid);
		
		// update by duanpeng for send activity info 20160823
		// send activity
		//查询生产任务批次信息
		Dataset pcDataset = Sys.query(TableConstant.生产任务批次, "pcbh,pcsl,pcmc,ljid,pcjhksrq,pcjhwcrq", "scrwpcid = ? " ,
				null, null,new Object[]{scrwpcid});
		List<Map<String, Object>> pcList = pcDataset.getList();
		//查询批次加工的零件信息
		parameters.set("val_lj", "("+pcList.get(0).get("ljid") + ")");
		Bundle result = Sys.callModuleService("pm", "pmservice_ljxx", parameters);
		List<Map<String,Object>> ljxx = (List<Map<String, Object>>) result.get("ljxx");
		//查询零件图片
		parameters.set("ljid", pcList.get(0).get("ljid"));
		Bundle resultLjUrl = Sys.callModuleService("pm", "partsInfoService", parameters);
		Object ljtp = ((Map)resultLjUrl.get("partsInfo")).get("url");
		//查询任务完成进度
		Parameters progressCondition = new Parameters();
		progressCondition.set("scrwbh", scrwbh);
		Bundle resultProgress = Sys.callModuleService("pc", "pcservice_caculateProgress", progressCondition);
		Object scrwjd = resultProgress.get("scrwjd");
		//查询生产任务分批情况
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> pcxxs = (List<Map<String,Object>>)resultProgress.get("pcxx");
		List<Map<String,Object>> pcData = new ArrayList<Map<String,Object>>();
		int wfpsl = (Integer)scrw.get("jgsl");
		if(pcxxs!=null){
			for(Map<String,Object> pc : pcxxs){
				if(pc.get("pcjhztdm") != null && "10".equals(pc.get("pcjhztdm").toString()))continue;
				Map<String,Object> pcjson = new HashMap<String,Object>();
				pcjson.put("value", pc.get("pcsl"));
				pcjson.put("name", pc.get("pcmc"));
				pcData.add(pcjson);
				wfpsl = wfpsl - (Integer)pc.get("pcsl");
			}
		}
		if( wfpsl < 0 ){
			wfpsl = 0;
		}
		Map<String,Object> pcjson = new HashMap<String,Object>();
		pcjson.put("value", wfpsl);
		pcjson.put("name", "未分批");
		pcData.add(pcjson);
		
		String pcxxStr = Sys.toJson(pcData);
		Object ljmc = ljxx.get(0).get("ljmc");
		Object pcmc = pcList.get(0).get("pcmc");
		Object ljid = pcList.get(0).get("ljid");
		Object ljbh = ljxx.get(0).get("ljbh");
		Object pcsl = pcList.get(0).get("pcsl");
		String pcbh = scrwbh + lsh;
		Object pcjhksrq = ((Date)pcList.get(0).get("pcjhksrq")).getTime();
		Object pcjhwcrq = ((Date)pcList.get(0).get("pcjhwcrq")).getTime();
		Object jgksrq = ((Date)scrw.get("jgksrq")).getTime();
		Object jgwcrq = ((Date)scrw.get("jgwcrq")).getTime();
		String activityType = "0"; //动态任务
		String[] roles = new String[] { "PLAN_MANAGEMENT_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };//关注该动态的角色
		String templateId = "scrwfp_tp";
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("scrwid", scrwid);//生产任务id
		data.put("scrwbh", scrwbh);//生产任务编号
		data.put("scrwjgsl", scrw.get("jgsl"));//生产任务加工数量
		data.put("scrwjgksrq", jgksrq);//生产任务开始时间
		data.put("scrwjgwcrq", jgwcrq);//生产任务完成时间
		data.put("pcid", scrwpcid);//批次id
		data.put("pcbh", pcbh);//批次编号
		data.put("pcmc", pcmc);//批次名称
		data.put("ljid", ljid);//零件id
		data.put("ljbh", ljbh);//零件id
		data.put("ljmc", ljmc);//零件名称
		data.put("pcsl", pcsl);//批次数量
		data.put("pcjhksrq", pcjhksrq);//批次计划日期
		data.put("pcjhwcrq", pcjhwcrq);//批次计划日期
		data.put("userid", Sys.getUserIdentifier());//操作人
		data.put("username", Sys.getUserName());//操作人
		data.put("ljtp", ljtp);//零件图片URL
		data.put("scrwjd", scrwjd);//生产任务进度
		data.put("pcxx", pcxxStr);//生产任务进度
		sendActivity(activityType, templateId, true, roles, null, null, data);

		// send message
		String message_type = "1";// 待办事项
		String[] message_roles = new String[] { "PLAN_MANAGEMENT_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };
		StringBuffer content = new StringBuffer();
		StringBuffer title = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		Date now = new Date();
		title.append(ljmc).append("(编号为").append(ljbh).append(") 下达新生产批次");
		content.append(sdf.format(now)).append(",");
		content.append(ljmc).append("(编号为").append(ljbh).append(")下发了新的生产批次(批次号为");
		content.append(pcbh).append("),该批次计划生产").append(pcsl).append("件，请制订该批次的生产计划。");
		String bizType = "PRO_PCFPACTIVITY_PCXF";// 生产任务批次下发
		String bizId = pcbh;
		String url = "/pl/jhpc/listSchedule?pcid=" + scrwpcid;
		sendMessage(message_type, title.toString(), null, content.toString(), "系统发送", bizType, bizId,
				"0"/* 信息优先级：0:一般，1：紧急 ， 2：非常紧急 */, url, message_roles, null, null,
				"system"/* manual:人工发送，system：系统发送，interface：外部接口 */, null/* 消息来源ID */, null, now, null, null,
				null);
	}
	
	private Bundle sendMessage(String type, String title, String abs, String content, String from, String bizType,
			String bizId, String priority, String url, String[] roles, String[] users, String[] group, String sourceType,
			String sourceId, Map<String, Object> data, Date sendTime, String[] fileUri, String[] fileNames,
			long[] filesSize) {
		String PARAMS_TYPE = "message_type";
		String PARAMS_ROLE = "receiver_role";
		String PARAMS_USER = "receiver_user";
		String PARAMS_GROUP = "receiver_group";
		String PARAMS_TITLE = "title";
		String PARAMS_ABSTRACT = "abstract";
		String PARAMS_CONTENT = "content";
		String PARAMS_FROM = "from";
		String PARAMS_DATA = "data";
		String PARAMS_PRIORITY = "priority";
		String PARAMS_SOURCE_TYPE = "source_type";
		String PARAMS_SOURCE_ID = "source_id";
		String PARAMS_URL = "url";
		String PARAMS_FILE_URI = "file_uri";
		String PARAMS_FILE_NAME = "file_name";
		String PARAMS_FILE_SIZE = "file_size";
		String PARAMS_BIZTYPE = "biz_type";
		String PARAMS_BIZID = "biz_id";
		String PARAMS_SEND_TIME = "send_time";
		String SERVICE_NAME = "message";
		String METHOD_NAME = "send";
		Parameters parameters = new Parameters();
		parameters.set(PARAMS_TITLE, title);
		parameters.set(PARAMS_ABSTRACT, abs);
		parameters.set(PARAMS_CONTENT, content);
		parameters.set(PARAMS_FROM, from);
		parameters.set(PARAMS_BIZTYPE, bizType);
		parameters.set(PARAMS_BIZID, bizId);
		parameters.set(PARAMS_TYPE, type);
		parameters.set(PARAMS_PRIORITY, priority);
		parameters.set(PARAMS_USER, users);
		parameters.set(PARAMS_GROUP, group);
		parameters.set(PARAMS_ROLE, roles);
		parameters.set(PARAMS_SOURCE_TYPE, sourceType);
		parameters.set(PARAMS_SOURCE_ID, sourceId);
		parameters.set(PARAMS_URL, url);
		parameters.set(PARAMS_FILE_URI, fileUri);
		parameters.set(PARAMS_FILE_NAME, fileNames);
		parameters.set(PARAMS_FILE_SIZE, filesSize);
		parameters.set(PARAMS_SEND_TIME, sendTime);
		parameters.set(PARAMS_DATA, data);
		return Sys.callModuleService(SERVICE_NAME, METHOD_NAME, parameters);
	}
	
	private Bundle sendActivity(String type, String templateId, boolean isPublic, String[] roles, String[] users, String[] group,
			Map<String, Object> data) {
		String PARAMS_TYPE = "type";
		String PARAMS_TEMPLATE_ID = "template_id";
		String PARAMS_PUBLIC = "public";
		String PARAMS_ROLE = "role";
		String PARAMS_USER = "user";
		String PARAMS_GROUP = "group";
		String PARAMS_DATA = "data";
		String SERVICE_NAME = "activity";
		String METHOD_NAME = "send";
		Parameters parameters = new Parameters();
		parameters.set(PARAMS_TYPE, type);
		parameters.set(PARAMS_TEMPLATE_ID, templateId);
		if (isPublic)
			parameters.set(PARAMS_PUBLIC, "1");
		if (roles != null && roles.length > 0)
			parameters.set(PARAMS_ROLE, roles);
		if (users != null && users.length > 0)
			parameters.set(PARAMS_USER, users);
		if (group != null && group.length > 0)
			parameters.set(PARAMS_GROUP, group);
		if (data != null && !data.isEmpty())
			parameters.set(PARAMS_DATA, data);
		return Sys.callModuleService(SERVICE_NAME, METHOD_NAME, parameters);
	}
	/**
	 * 批量下发任务
	 */
	public void pcfpAll(Parameters parameters,Bundle bundle){
		String scrwpcArrayids = (String) parameters.get("scrwpcids");
		for(String scrwpid : scrwpcArrayids.split(",")){
			if(StringUtils.isBlank(scrwpid)){
				continue;
			}
			parameters.set("scrwpcid", scrwpid);
			xfpc(parameters, bundle);
		}
	}
}
