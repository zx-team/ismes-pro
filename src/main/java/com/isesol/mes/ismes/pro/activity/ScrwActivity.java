package com.isesol.mes.ismes.pro.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;
import com.isesol.mes.ismes.pro.constant.BatchPlanStatus;
import com.isesol.mes.ismes.pro.constant.BatchStatus;
import com.isesol.mes.ismes.pro.constant.ProduceTaskImportStatus;
import com.isesol.mes.ismes.pro.constant.TableConstant;
import com.isesol.mes.ismes.pro.constant.TaskStatus;

import net.sf.json.JSONArray;

public class ScrwActivity {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	DecimalFormat fnum = new DecimalFormat("##0.00"); 
	/**
	 * 查询生产任务
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_scrw(Parameters parameters, Bundle bundle) {
		return "pro_scrw";
	}
	
	/**插入/修改生产任务
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String update_scrw(Parameters parameters, Bundle bundle) throws Exception {
		
		List<Map<String,Object>> scrw_inlist = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> scrw_uplist = new ArrayList<Map<String,Object>>();
		List<Object[]>  objlist= new ArrayList<Object[]>();
		JSONArray jsonarray = JSONArray.fromObject(parameters.get("data_list"));  
		for (int i = 0; i < jsonarray.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>(); 
			Map<String, Object> map_in = new HashMap<String, Object>(); 
			map = jsonarray.getJSONObject(i);
			map_in.put("jgksrq", sdf.parse(map.get("jgksrq").toString()));
			map_in.put("jgwcrq", sdf.parse(map.get("jgwcrq").toString()));
			if(StringUtils.isNotBlank(map.get("addSign").toString()))
			{
				map_in.put("ljid", map.get("ljid"));
				map_in.put("wlid", map.get("wlid"));
//				map_in.put("sjwlkcid", map.get("sjwlkcid"));
				map_in.put("mplh", map.get("mplh"));
				map_in.put("scph", map.get("scph"));
				map_in.put("ljbh", map.get("ljbh"));
				map_in.put("ljxh", map.get("ljxh"));
				map_in.put("jgsl", map.get("jgsl"));
//				String jgsl = (String) map.get("jgsl");
//				if(jgsl.contains(".")){
//					map_in.put("jgsl", jgsl.substring(0, jgsl.indexOf(".")));
//				}else{
//					map_in.put("jgsl", jgsl);
//				}
				map_in.put("yxwc", map.get("yxwc"));
				String jhsl = (String) map.get("jhsl");
				if(jhsl.contains(".")){
					map_in.put("jhsl", jhsl.substring(0, jhsl.indexOf(".")));
				}else{
					map_in.put("jhsl", jhsl);
				}
//				map_in.put("jhsl", map.get("jhsl"));
				map_in.put("jgzt", map.get("jgzt"));
				map_in.put("scrwfpztdm", "10");
				map_in.put("scrwlydm", "30");
				map_in.put("scrwztdm", "10");
				map_in.put("scrwlrsj", new Date());
				String scrwbh = map.get("ljbh").toString().replace(" ", "")+(sdf6.format(new Date())).substring(2);
				String  num = ""+Sys.getNextSequence("scrw"+scrwbh.replace("-", ""));
				if(num.length()==1)
				{
					num = "00"+num;
				}else if(num.length()==2){
					num = "0"+num;
				}
				map_in.put("scrwbh",scrwbh+num);
				scrw_inlist.add(map_in); 
				
			}else{
				objlist.add(new Object[]{map.get("scrwid")});
				map_in.put("ljid", map.get("ljid"));
				map_in.put("wlid", map.get("wlid"));
//				map_in.put("sjwlkcid", map.get("sjwlkcid"));
				map_in.put("mplh", map.get("mplh"));
				map_in.put("ljxh", map.get("ljxh"));
				map_in.put("scph", map.get("scph"));
				map_in.put("jgsl", map.get("jgsl"));
				map_in.put("yxwc", map.get("yxwc"));
				String jhsl = (String) map.get("jhsl");
				if(jhsl.contains(".")){
					map_in.put("jhsl", jhsl.substring(0, jhsl.indexOf(".")));
				}else{
					map_in.put("jhsl", jhsl);
				}
//				map_in.put("jhsl", map.get("jhsl"));
				map_in.put("jgzt", map.get("jgzt"));
				scrw_uplist.add(map_in); 
			}
		}  
		if (scrw_inlist.size()>0) {
			try {
				int i = Sys.insert(TableConstant.生产任务, scrw_inlist);
				System.out.println("插入数量"+i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (scrw_uplist.size()>0) {
			try {
				int i = Sys.update(TableConstant.生产任务,scrw_uplist,"scrwid=?",objlist);
				System.out.println("更新数量"+i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "json:";
	}
	
	
	/**删除生产任务
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String del_scrw(Parameters parameters, Bundle bundle) {
		if (StringUtils.isBlank(parameters.getString("data_list"))) {
			return "json:";
		}
		List<Object[]>  objlist= new ArrayList<Object[]>();
		objlist.add(new Object[]{Integer.parseInt(parameters.getString("data_list"))});
		try {
			int i = Sys.delete(TableConstant.生产任务,"scrwid=?",objlist);
			System.out.println("删除数量"+i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "json:";
	}

	/**生产任务列表分页查询请用table_scrw  此方法作废
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String table(Parameters parameters, Bundle bundle) {
		Dataset datasetScrw = Sys.query(TableConstant.生产任务,"scrwid,scrwbh,ljid,jgsl,dw,jgksrq,jgwcrq,bz,scrwlrsj,scrwztdm,scrwlydm,scrwfpztdm", null, null, 0, 50,new Object[]{});
		List<Map<String, Object>> sc_scrw = datasetScrw.getList();
		bundle.put("rows", sc_scrw);
		bundle.put("total", 3);
		bundle.put("page", 1);
		bundle.put("records", 1000);
		return "json:";
	}
	
	/**查询生产任务
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String table_scrw(Parameters parameters, Bundle bundle) throws Exception {
		
		//查询零件信息
		Bundle b_ljxx = Sys.callModuleService("pm", "pmservice_ljxxbybhxh", parameters);
		if (null==b_ljxx) {
			return "json:";
		}
		List<Map<String, Object>> ljxx = (List<Map<String, Object>>) b_ljxx.get("ljxx");
		if (ljxx.size()<=0) {
			return "json:";
		}
		String val_lj = "(";
		for (int i = 0; i < ljxx.size(); i++) {
			if(i!=0)
			{
				val_lj = val_lj +",";
			}
			val_lj += "'" +ljxx.get(i).get("ljid")+"'";
		} 
		val_lj = val_lj +")";
		parameters.set("val_lj", val_lj);
		
		
		
		// 查询生产任务
		String query_jgksstart = parameters.getString("query_jgksstart"); // 加工开始日期         
		String query_jgksend = parameters.getString("query_jgksend"); // 加工开始日期
		String query_jgjsstart = parameters.getString("query_jgjsstart"); // 加工结束日期
		String query_jgjsend = parameters.getString("query_jgjsend"); // 加工结束日期
		String query_rwlrstart = parameters.getString("query_rwlrstart"); // 任务录入日期
		String query_rwlrend = parameters.getString("query_rwlrend"); // 任务录入日期
		String query_rwzt = parameters.getString("query_rwzt"); // 任务状态
		String query_rwly = parameters.getString("query_rwly"); // 任务来源
		String query_fpzt = parameters.getString("query_fpzt"); // 分批状态
		String sortName = parameters.getString("sortName");// 排序字段
		String sortOrder = parameters.getString("sortOrder");// 排序方式 asc  desc
		
		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();
		if(StringUtils.isNotBlank(val_lj)) 
		{
			con = con + " and ljid in "+ val_lj ;
		}
		if(StringUtils.isNotBlank(query_jgksstart))
		{
			con = con + " and jgksrq >=?  ";
			val.add(sdf_time.parse(query_jgksstart+" 00:00:00"));
		}
		if(StringUtils.isNotBlank(query_jgksend))
		{
			con = con + " and jgksrq <=? ";
			val.add(sdf_time.parse(query_jgksend+" 23:59:59"));
		}
		if(StringUtils.isNotBlank(query_jgjsstart))
		{
			con = con + " and jgwcrq >=? ";
			val.add(sdf_time.parse(query_jgjsstart+" 00:00:00"));
		}
		if(StringUtils.isNotBlank(query_jgjsend))
		{
			con = con + " and jgwcrq <=? ";
			val.add(sdf_time.parse(query_jgjsend+" 23:59:59"));
		}
		if(StringUtils.isNotBlank(query_rwlrstart))
		{
			con = con + " and scrwlrsj >=? ";
			val.add(sdf_time.parse(query_rwlrstart+" 00:00:00"));
		}
		if(StringUtils.isNotBlank(query_rwlrend))
		{
			con = con + " and scrwlrsj <=? ";
			val.add(sdf_time.parse(query_rwlrend+" 23:59:59"));
		}
		if(StringUtils.isNotBlank(query_rwzt))
		{
			con = con + " and scrwztdm = ? ";
			val.add(query_rwzt);
		} else {
			con = con + " and (scrwztdm = ? or scrwztdm = ?) ";
			val.add(TaskStatus.未执行);
			val.add(TaskStatus.执行中);
		}
		if(StringUtils.isNotBlank(query_rwly))
		{
			con = con + " and scrwlydm = ? ";
			val.add(query_rwly);
		}
		if(StringUtils.isNotBlank(query_fpzt))
		{
			con = con + " and scrwfpztdm = ? ";
			val.add(query_fpzt);
		}
		if(StringUtils.isNotBlank(sortName))
		{
			sortOrder = sortName + " "+sortOrder+" ";
		}else {
			sortOrder = "scrwlrsj desc";
		}
			
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetScrw = Sys.query(TableConstant.生产任务,"scrwid,scrwbh,ljid,jgsl,jgksrq,jgwcrq,bz,scrwlrsj,scrwztdm,scrwlydm,scrwfpztdm,scph,wlid,sjwlkcid,jgzt,jhsl,yxwc,mplh,mplh mplhxf,ljxh ljxhxf", con, sortOrder, (page-1)*pageSize, pageSize,val.toArray());
		List<Map<String, Object>> scrw = datasetScrw.getList();
		//增加毛坯规格字段--------------------start---------------
		//遍历物料id
//		String val_wl = "(";
		String val_wl = "";
		
		for (int i = 0; i < scrw.size(); i++) {
		if(null!=scrw.get(i).get("wlid")){
			if(i!=0)
			{
				val_wl = val_wl +",";
			}
			val_wl += "'" +scrw.get(i).get("wlid")+"'";
		} 
		}
//		val_wl = val_wl +")";
		parameters.set("wlid", val_wl);
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_query_wlxx_by_wlid", parameters);
		if (null==b_wlxx) {
			return "json:";
		}
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		if (wlxx.size()<=0) {
			return "json:";
		}
		//--------------------end---------------
		//增加毛坯炉号字段--------------------start---------------
		//遍历物料id
//		String val_kc = "(";
//		for (int i = 0; i < scrw.size(); i++) {
//			if(null!=scrw.get(i).get("sjwlkcid")){
//			if(i!=0)
//			{
//				val_kc = val_kc +",";
//			}
//			val_kc += "'" +scrw.get(i).get("sjwlkcid")+"'";
//			}
//		} 
//		val_kc = val_kc +")";
//		parameters.set("sjwlkcid", val_kc);
//		Bundle b_kcxx = Sys.callModuleService("wm", "wmService_query_kcxx_by_sjwlkcid", parameters);
//		if (null==b_kcxx) {
//			return "json:";
//		}
//		List<Map<String, Object>> kcxx = (List<Map<String, Object>>) b_kcxx.get("kcxx");
//		if (kcxx.size()<=0) {
//			return "json:";
//		}
		//--------------------end---------------
		for (int i = 0; i < scrw.size(); i++) {
			for (int j = 0; j < ljxx.size(); j++) {
				if (scrw.get(i).get("ljid").toString().equals(ljxx.get(j).get("ljid").toString())) {
					scrw.get(i).put("ljbh", ljxx.get(j).get("ljbh"));
					scrw.get(i).put("ljmc", ljxx.get(j).get("ljmc"));
					scrw.get(i).put("ljms", ljxx.get(j).get("ljms"));
					break;
				}
			}
			for (int k = 0; k < wlxx.size(); k++) {
				if(null!=scrw.get(i).get("wlid")){
				if (scrw.get(i).get("wlid").toString().equals(wlxx.get(k).get("wlid").toString())) {
					scrw.get(i).put("wlid", wlxx.get(k).get("wlid"));
					scrw.get(i).put("wlgg", wlxx.get(k).get("wlgg"));
					break;
				}
				}
			}
			for(int l=0;l<scrw.size();l++){
				if(null==scrw.get(l).get("yxwc")){
					scrw.get(l).put("yxwc", "");
				}
			}
//			for (int k = 0; k < kcxx.size(); k++) {
//				if(null!=scrw.get(i).get("sjwlkcid")){
//				if (scrw.get(i).get("sjwlkcid").toString().equals(kcxx.get(k).get("sjwlkcid").toString())) {
//					scrw.get(i).put("sjwlkcid", kcxx.get(k).get("mplh"));
//					scrw.get(i).put("mplh", kcxx.get(k).get("mplh"));
//					break;
//				}
//				}
//			}
		}
		
		bundle.put("rows", scrw);
		int totalPage = datasetScrw.getTotal()%pageSize==0?datasetScrw.getTotal()/pageSize:datasetScrw.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetScrw.getTotal());
		return "json:";
	}
	
	/**
	 * 导入刷新按钮
	 */
	public String importAndRefresh(Parameters parameters, Bundle bundle){
		//从中间表读取数据
		Dataset dataSet = Sys.query("sc_scrwjkb", "scrwjkid,"/*生产任务接口表序号*/
				+"scrwlydm,"/*生产任务来源代码*/+"ljid,"/*零件编号*/
				+"jgsl,"/*加工数量*/+"jgksrq,"/*加工开始日期*/
				+"jgwcrq,"/*加工完成日期*/+"bz,"/*备注*/ + "scrwbh,"//生产任务编号
				+"lrrq"/*录入日期*/
				, "rwzt = ?", null,null, ProduceTaskImportStatus.未导入);
		List<Map<String, Object>> list =  dataSet.getList();
		//若两张表的字段相同，实际可以使用同一个list，但是为了规范，以及可能出现的业务操作以及可能出现的不同字段，new list
		List<Map<String, Object>> insertList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> updateList = new ArrayList<Map<String,Object>>();
		List<Object[]> xhList = new ArrayList<Object[]>();
		for(Map<String, Object> map : list){
			Map<String, Object> newMap = new HashMap<String, Object>();
			//生产任务接口表序号
			if(map.get("scrwjkid") == null){
				continue;
			}
			xhList.add(new Object[]{map.get("scrwjkid").toString()});
			//任务来源
			if(map.get("scrwlydm") != null){
				newMap.put("scrwlydm", map.get("scrwlydm"));
			}
			//零件编号
			if(map.get("ljid") != null){
				newMap.put("ljid", map.get("ljid"));
			}
			//加工数量
			if(map.get("jgsl") != null){
				newMap.put("jgsl", map.get("jgsl"));
			}
			//加工开始日期
			if(map.get("jgksrq") != null){
				newMap.put("jgksrq", map.get("jgksrq"));
			}
			//加工完成日期
			if(map.get("jgwcrq") != null){
				newMap.put("jgwcrq", map.get("jgwcrq"));
			}
			//备注
			if(map.get("bz") != null){
				newMap.put("bz", map.get("bz"));
			}
			//录入日期
			if(map.get("lrrq") != null){
				//newMap.put("scrwlrsj", map.get("lrrq"));
				newMap.put("scrwlrsj", new Date());
			}
			//录入日期
			if(map.get("scrwbh") != null){
				newMap.put("scrwbh", map.get("scrwbh"));
			}
			
			//生产任务状态  为未执行
			newMap.put("scrwztdm", TaskStatus.未执行);
			//生产任务分批状态
			newMap.put("scrwfpztdm", BatchStatus.未分批);
			insertList.add(newMap);
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("rwzt", ProduceTaskImportStatus.已导入);
			updateList.add(updateMap);
		}
		int i = 0;
		if(CollectionUtils.isNotEmpty(insertList)){
			i = Sys.insert(TableConstant.生产任务, insertList);
		}
		if(CollectionUtils.isNotEmpty(xhList)){
			Sys.update(TableConstant.生产任务中间接口表, updateList, "scrwjkid = ?", xhList);
		}
		bundle.put("importNum", i);
		return "json:importNum";
	}
	
	/**
	 * 终止任务
	 * @param parameters
	 * @param bundle
	 */
	public void terminateTask(Parameters parameters, Bundle bundle){
		String scrwid = (String) parameters.get("scrwid");
		Map<String,Object> updateMap = new HashMap<String, Object>();
		//修改任务状态
		updateMap.put("scrwztdm", TaskStatus.已终止);
		Sys.update(TableConstant.生产任务, updateMap, "scrwid = ?", scrwid);
		//修改批次状态
		updateMap.clear();
		updateMap.put("pcjhztdm", BatchPlanStatus.已终止);
		Sys.update(TableConstant.生产任务批次, updateMap, "scrwid = ?", scrwid);
		//修改工单状态
		//查询有哪些批次
		Dataset dataSet = Sys.query(TableConstant.生产任务批次, "pcbh,scrwpcid",  "scrwid = ?", null , new Object[]{scrwid});
		List<Map<String, Object>> list = dataSet.getList();
		parameters.set("pclist", list);
		parameters.set("gdzt", "60");
		Sys.callModuleService("pl", "updateGdStatusService", parameters);
		
		Parameters jdCondition = new Parameters();
		jdCondition.set("query_scrwbh",  parameters.get("scrwbh"));
		jdCondition.set("query_ljbh",  parameters.get("ljbh"));
		jdCondition.set("query_sign", "query");
		List<Map<String, Object>> scrw = caculateProgress(jdCondition,bundle);
		
		String activityType = "0"; //动态任务
		String[] roles = new String[] { "PLAN_MANAGEMENT_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };//关注该动态的角色
		String templateId = "scrwzz_tp";
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("scph", parameters.get("scph"));//生产批号
		data.put("ljbh", parameters.get("ljbh"));//零件id
		data.put("ljmc", parameters.get("ljmc"));//零件名称
		if(scrw.size()>0)
		{
			data.put("ywcsl", scrw.get(0).get("wxwcsl"));//已完成数量
			data.put("jgsl", scrw.get(0).get("jgsl"));//
			//查询零件图片
			parameters.set("ljid", scrw.get(0).get("ljid"));
			Bundle resultLjUrl = Sys.callModuleService("pm", "partsInfoService", parameters);
			Object ljtp = ((Map)resultLjUrl.get("partsInfo")).get("url");
			data.put("ljtp", ljtp);//零件图片
		}
		//查询任务完成进度
		Parameters progressCondition = new Parameters();
		progressCondition.set("scrwbh", parameters.get("scrwbh"));
		Bundle resultProgress = Sys.callModuleService("pc", "pcservice_caculateProgress", progressCondition);
		Object scrwjd = resultProgress.get("scrwjd");
		data.put("scrwjd", scrwjd);//生产任务进度
		data.put("userid", Sys.getUserIdentifier());//操作人
		data.put("username", Sys.getUserName());//操作人
		
		sendActivity(activityType, templateId, true, roles, null, null, data);
	}
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> caculateProgress(Parameters parameters, Bundle bundle) {
		parameters.set("page", 1);
		parameters.set("pageSize", 10);
		List<Map<String, Object>> scrwl = new ArrayList<Map<String,Object>>();
		//查询零件信息
		Bundle b_ljxx = Sys.callModuleService("pm", "pmservice_ljxxbybhmc", parameters);
		if (null==b_ljxx) {
			return scrwl;
		}
		List<Map<String, Object>> ljxx = (List<Map<String, Object>>) b_ljxx.get("ljxx");
		if (ljxx.size()<=0) {
			return scrwl;
		}
		String val_lj = "(";
		for (int i = 0; i < ljxx.size(); i++) {
			if(i!=0)
			{
				val_lj = val_lj +",";
			}
			val_lj += "'" +ljxx.get(i).get("ljid")+"'";
		} 
		val_lj = val_lj +")";
		parameters.set("val_lj", val_lj);
		
		//查询生产任务
		Bundle b_scrw = Sys.callModuleService("pro", "proService_scrw", parameters);
		List<Map<String, Object>> scrw = (List<Map<String, Object>>) b_scrw.get("rows");
		if (scrw.size()<=0) {
			return scrwl;
		}
		String val_scrw = "(";
		for (int i = 0; i < scrw.size(); i++) {
			for (int j = 0; j < ljxx.size(); j++) {
				if (scrw.get(i).get("ljid").toString().equals(ljxx.get(j).get("ljid").toString())) {
					scrw.get(i).put("ljbh", ljxx.get(j).get("ljbh"));
					scrw.get(i).put("ljmc", ljxx.get(j).get("ljmc"));
					break;
				}
			}
			//初始化完成数量
			scrw.get(i).put("ywcsl", 0);
			scrw.get(i).put("wxwcsl", 0);
			scrw.get(i).put("wcjd", 0.0);
			scrw.get(i).put("wxwcjd", 0.0);
			
			if(i!=0)
			{
				val_scrw = val_scrw +",";
			}
			val_scrw += scrw.get(i).get("scrwid");
		}
		val_scrw = val_scrw +")";
		parameters.set("val_scrw", val_scrw);
		
		//根据生产任务ID，查询生产任务批次信息
		Bundle b_pcxx = Sys.callModuleService("pro", "proService_pcxx", parameters);
		List<Map<String, Object>> pcxx = (List<Map<String, Object>>) b_pcxx.get("pcxx");
		if (pcxx.size()>0) {
			for (int n = 0; n < pcxx.size(); n++) {
				//初始化完成数量
				pcxx.get(n).put("ywcsl", 0);
				pcxx.get(n).put("wcjd",0);
				pcxx.get(n).put("wxwcsl", 0.0);
				pcxx.get(n).put("wxwcjd", 0.0);
				String val_pc = "('"+pcxx.get(n).get("scrwpcid")+"')";
				parameters.set("val_pc", val_pc);
				Bundle b_gdxx = Sys.callModuleService("pl", "plservice_gxslByPcid", parameters);
				if(null != b_gdxx)
				{
					List<Map<String, Object>> gdxx = (List<Map<String, Object>>) b_gdxx.get("gdxx");
					if (gdxx.size()>0) {
						
						String val_gx = "(";
						for (int i = 0; i < gdxx.size(); i++) {
							if(i!=0)
							{
								val_gx = val_gx +",";
							}
							val_gx += "'" +gdxx.get(i).get("gxid")+"'";
						}
						val_gx = val_gx +")";
						parameters.set("val_gx", val_gx);
						Bundle b_gxxx = Sys.callModuleService("pm", "pmservice_query_gygx", parameters);
						if (null!= b_gxxx) {
							List<Map<String, Object>> gxxx = (List<Map<String, Object>>) b_gxxx.get("gxxx");
							for (int i = 0; i < gxxx.size(); i++) {
								for (int j = 0; j < gdxx.size(); j++) {
									if (gxxx.get(i).get("gxid").toString().equals(gdxx.get(j).get("gxid").toString())) {
										gxxx.get(i).put("jgsl",gdxx.get(j).get("jgsl"));
										gxxx.get(i).put("wcsl",gdxx.get(j).get("wcsl"));
									}
								}
							}
							Map<String, Object> gxxxMap = new HashMap<String, Object>();
							for (int i = 0; i < gxxx.size(); i++) {
								gxxx.get(i).put("zzpsl", 0);
								gxxx.get(i).put("ydcl", 0);
								if(null==gxxx.get(i).get("qxid")||StringUtils.isBlank(gxxx.get(i).get("qxid").toString()))
								{
									gxxx.get(i).put("qxjgsj", Integer.parseInt(gxxx.get(i).get("jgfs").toString()));
									gxxxMap = gx_ydcl(gxxx,gxxx.get(i));
									if("1".equals(gxxxMap.get("wxbz")))
									{
										gxxx.get(i).put("zzpsl", Integer.parseInt(gxxx.get(i).get("wcsl").toString()));
										pcxx.get(n).put("wxwcsl", gxxx.get(i).get("wcsl"));
										pcxx.get(n).put("ywcsl",fnum.format(Float.parseFloat(gxxxMap.get("ydcl").toString())+Float.parseFloat(gxxx.get(i).get("wcsl").toString())));
										pcxx.get(n).put("wcjd",""+(Math.round((Float.parseFloat(pcxx.get(n).get("ywcsl").toString())*10000)/Float.parseFloat(pcxx.get(n).get("pcsl").toString()))/100.0));
										pcxx.get(n).put("wxwcjd",""+(Math.round((Float.parseFloat(pcxx.get(n).get("wxwcsl").toString())*10000)/Float.parseFloat(pcxx.get(n).get("pcsl").toString()))/100.0));
									}else{
										gxxx.get(i).put("zzpsl", Float.parseFloat(gxxx.get(i).get("jgsl").toString())- Float.parseFloat(gxxx.get(i).get("wcsl").toString()));
										if(0!=Integer.parseInt(gxxx.get(i).get("jgsl").toString()))
										{
											pcxx.get(n).put("ywcsl",  fnum.format(Float.parseFloat(gxxxMap.get("ydcl").toString())+((Float.parseFloat(gxxx.get(i).get("zzpsl").toString())*(Float.parseFloat(gxxx.get(i).get("wcsl").toString())/Float.parseFloat(gxxx.get(i).get("jgsl").toString()))*(Float.parseFloat(gxxx.get(i).get("jgfs").toString())*100/Float.parseFloat(gxxx.get(i).get("zjgsj").toString())))/100.0)));
											pcxx.get(n).put("wxwcsl", gxxxMap.get("wxwcsl"));
											pcxx.get(n).put("wcjd",""+(Math.round((Float.parseFloat(pcxx.get(n).get("ywcsl").toString())*10000)/Float.parseFloat(pcxx.get(n).get("pcsl").toString()))/100.0));
											pcxx.get(n).put("wxwcjd",""+(Math.round((Float.parseFloat(pcxx.get(n).get("wxwcsl").toString())*10000)/Float.parseFloat(pcxx.get(n).get("pcsl").toString()))/100.0));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//计算生产任务已完成数量
		for (int i = 0; i < scrw.size(); i++) {
			for (int j = 0; j < pcxx.size(); j++) {
				if (scrw.get(i).get("scrwid").toString().equals(pcxx.get(j).get("scrwid").toString())) {
					scrw.get(i).put("ywcsl",Float.parseFloat(scrw.get(i).get("ywcsl").toString())+ Float.parseFloat(pcxx.get(j).get("ywcsl").toString()));
					scrw.get(i).put("wxwcsl",Float.parseFloat(scrw.get(i).get("wxwcsl").toString())+ Float.parseFloat(pcxx.get(j).get("wxwcsl").toString()));
				}
			}
		}
		if(0<scrw.size())
		{
			return scrw;
		}else{
			return scrwl;
		}
		
	}
	
	public Map<String, Object> gx_ydcl(List<Map<String, Object>> gxxx ,Map<String, Object> gxMap) {
		Map<String, Object> gxxxMap = new HashMap<String, Object>();
		gxxxMap.put("ydcl", 0);//约当产量
		gxxxMap.put("wxbz", "1");//尾序标识
		for (int i = 0; i < gxxx.size(); i++) {
			if(gxMap.get("gxid").equals(gxxx.get(i).get("qxid")))
			{
				
				gxxx.get(i).put("qxjgsj", Integer.parseInt(gxxx.get(i).get("jgfs").toString()) + Integer.parseInt(gxMap.get("qxjgsj").toString()));
				gxxxMap = gx_ydcl(gxxx,gxxx.get(i));   
				
				if("1".equals(gxxxMap.get("wxbz")))
				{
					gxxxMap.put("wxwcsl", gxxx.get(i).get("wcsl"));
					gxxx.get(i).put("zzpsl", Integer.parseInt(gxMap.get("wcsl").toString()));
				}else{
					gxxxMap.put("wxwcsl", gxxxMap.get("wxwcsl"));
					gxxx.get(i).put("zzpsl", Integer.parseInt(gxMap.get("wcsl").toString()) - Integer.parseInt(gxxx.get(i).get("wcsl").toString()));
				}
				if(0!=Integer.parseInt(gxxx.get(i).get("jgsl").toString()))
				{
					float zzpsl = Float.parseFloat(gxxx.get(i).get("zzpsl").toString());
					float wcsl = Float.parseFloat(gxxx.get(i).get("wcsl").toString());
					float jgsl = Float.parseFloat(gxxx.get(i).get("jgsl").toString());
					float jgfs = Float.parseFloat(gxxx.get(i).get("jgfs").toString());
					float zjgsj = Float.parseFloat(gxxx.get(i).get("zjgsj").toString());
					float qxjgsj = Float.parseFloat(gxMap.get("qxjgsj").toString());
					gxxxMap.put("ydcl", Float.parseFloat(gxxxMap.get("ydcl").toString())+(zzpsl*((wcsl*100/jgsl)*jgfs+qxjgsj*100)/zjgsj)/100.0);
				}
				gxxxMap.put("wxbz", "0");
				break;
			}
		}
		
		return gxxxMap;
	}
	
	/**
	 * 根据零件编号信息模糊查询
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String ljEdit(Parameters parameters, Bundle bundle){
//		String param = (String) parameters.get("query");
//		parameters.set("query_ljbh", param);
		Bundle b = Sys.callModuleService("pm", "queryLjxxByLjbh", parameters);
		if (null != b) {
			List<Map<String,Object>> list = (List<Map<String, Object>>) b.get("ljxx");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("label", list.get(i).get("ljbh"));
				list.get(i).put("value", list.get(i).get("ljid"));
				list.get(i).put("title", list.get(i).get("ljbh"));
			}
			
			bundle.put("data", list);
		}

		return "json:data";
	}
	
	/**
	 * 根据零件项号信息模糊查询
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String ljxhEdit(Parameters parameters, Bundle bundle){
//		String param = (String) parameters.get("query");
//		parameters.set("query_ljbh", param);
		Bundle b = Sys.callModuleService("pm", "queryLjxxByLjxh", parameters);
		if (null != b) {
			List<Map<String,Object>> list = (List<Map<String, Object>>) b.get("ljxx");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("label", list.get(i).get("ljxh"));
				list.get(i).put("value", list.get(i).get("ljxh"));
				list.get(i).put("title", list.get(i).get("ljxh"));
			}
			
			bundle.put("data", list);
		}

		return "json:data";
	}
	
	/**
	 * 根据物料规格模糊查询
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String wlEdit(Parameters parameters, Bundle bundle){
		String param = (String) parameters.get("query");
		parameters.set("query_wlgg", param);
		Bundle b = Sys.callModuleService("mm", "mmservice_query_wlxx_by_mpgg", parameters);
		if (null != b) {
			List<Map<String,Object>> list = (List<Map<String, Object>>) b.get("wlxx");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("label", list.get(i).get("wlgg"));
				list.get(i).put("value", list.get(i).get("wlid"));
				list.get(i).put("title", list.get(i).get("wlgg"));
			}
			
			bundle.put("data", list);
		}

		return "json:data";
	}
	/**
	 * 根据物料炉号信息模糊查询
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String lhEdit(Parameters parameters, Bundle bundle){
//		String mpgg = (String) parameters.get("mpgg");
		
		//20161110 add by maww 查询炉号ID
		String wlid = "";
		if(null!=parameters.get("wlid")&&!"".equals(parameters.get("wlid"))){
			Bundle b = Sys.callModuleService("mm", "mmservice_query_wlggid_bywlggxx", parameters);
			if (null==b) {
				return "json:";
			}
			List<Map<String,Object>> list = (List<Map<String,Object>>)b.get("wlgg");
			if (list.size() > 0){
				wlid = list.get(0).get("wlid").toString();
				parameters.set("wlid", wlid);
			}
		}
		
		Bundle b = Sys.callModuleService("wm", "wmService_query_kcxx_by_mplh", parameters);
		if (null != b) {
			List<Map<String,Object>> list = (List<Map<String, Object>>) b.get("kcxx");
			//去重复
			for (int i = 0; i < list.size() - 1; i++) {
				for (int j = list.size() - 1; j > i; j--) {
					if (null != list.get(j).get("mplh") && null != list.get(i).get("mplh")) {
						if (list.get(j).get("mplh").equals(list.get(i).get("mplh"))) {
							list.remove(j);
						}
					}
				}
			}
			if(null!=list){
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("label", list.get(i).get("mplh"));
				list.get(i).put("value", list.get(i).get("mplh"));
				list.get(i).put("title", list.get(i).get("mplh"));
			}
			bundle.put("data", list);
			}else{
				bundle.put("data", new ArrayList());
			}

		}

		return "json:data";
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
	 * 
	 * @param parameters
	 * @param bundle
	 */
	public void scrwByScrwid(Parameters parameters, Bundle bundle) {
		String scrwid = parameters.getString("scrwid");
		Dataset datasetScrw = Sys.query(TableConstant.生产任务, "scrwbh,scph",  "scrwid = ?", null , null , new Object[]{scrwid});
		Map<String, Object> scrw = datasetScrw.getMap();
		bundle.put("scrw", scrw);
	}
}
