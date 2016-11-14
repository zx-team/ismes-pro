package com.isesol.mes.ismes.pro.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.isesol.mes.ismes.pro.constant.TableConstant;

public class ScrwService {
	private SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 根据批次状态查询任务列表,多个状态的用逗号分割
	 * @param parameters
	 * @param bundle
	 */
	public void scrwListByPcjhzt(Parameters parameters,Bundle bundle){
		String pcjhztdm = parameters.getString("pcjhztdm");
		pcjhztdm = pcjhztdm.replace(",", "','");
		pcjhztdm = "'" + pcjhztdm + "'";
		Dataset dataSet = Sys.query(TableConstant.生产任务批次, "scrwid", "pcjhztdm in (" + pcjhztdm +")" ,null, null);
		List<Map<String,Object>> resultList = dataSet.getList();
		if(CollectionUtils.isEmpty(resultList)){
			bundle.put("scrwList", null);
			return;
		}
		StringBuffer ids =  new StringBuffer();
		for(Map<String,Object> map : resultList ){
			String scrwid =  map.get("scrwid").toString();
			ids = ids.append(scrwid).append(",");
		}
		ids = ids.deleteCharAt(ids.length() - 1);
		ids = ids.insert(0, "(").append(")");
		String groupBy = null;
		String orderBy = StringUtils.isBlank(parameters.getString("sortName")) 
				? " jgksrq " : parameters.getString("sortName");
		String asc =  StringUtils.isBlank(parameters.getString("sortOrder")) 
				? " asc " : parameters.getString("sortOrder");
		
		int page = parameters.get("page") == null ? 1 : parameters.getInteger("page");
		int pageSize = parameters.get("pageSize") == null ? 100 : parameters.getInteger("pageSize");
		Dataset datasetScrw = Sys.query(TableConstant.生产任务, "scrwid,"
				+ "ljid,"/*零件i*/+ "jgsl,"/*加工数量*/ +"jgksrq,"/*加工开始日期*/ +"jgwcrq,"/*加工完成日期*/
				+"bz,"/*备注*/ +"scrwlrsj,"/*生产任务录入时间*/ +"scrwztdm,"/*生产任务状态代码*/ 
				+"scrwlydm,"/*生产任务来源代码*/ +"scrwfpztdm,"/*生产任务分批状态代码*/
				+"scrwbh"/*生产任务编号*/
				,"scrwid in " + ids ,groupBy ,orderBy + " " + asc ,(page-1)*pageSize, pageSize,new Object[]{});
		List<Map<String, Object>> list = datasetScrw.getList();
		bundle.put("rows", list);
		int totalPage = datasetScrw.getTotal() % pageSize== 0 ? datasetScrw.getTotal()/pageSize:
			datasetScrw.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetScrw.getTotal());
		bundle.put("records", datasetScrw.getCount());
	}
	
	/**
	 * 根据批次状态查询任务列表,多个状态的用逗号分割
	 * @param parameters
	 * @param bundle
	 */
	public void scrwListByScrwzt(Parameters parameters, Bundle bundle) {
		String scrwztdm = parameters.getString("scrwztdm");
		scrwztdm = scrwztdm.replace(",", "','");
		scrwztdm = "('" + scrwztdm + "')";
		String groupBy = null;
		String orderBy = StringUtils.isBlank(parameters.getString("sortName")) ? " scrwlrsj "
				: parameters.getString("sortName");
		String asc = StringUtils.isBlank(parameters.getString("sortOrder")) ? " desc "
				: parameters.getString("sortOrder");

		int page = parameters.get("page") == null ? 1 : parameters.getInteger("page");
		int pageSize = parameters.get("pageSize") == null ? 100 : parameters.getInteger("pageSize");
		Dataset datasetScrw = Sys.query(TableConstant.生产任务,
				"scrwid," + "ljid,"/* 零件i */ + "jgsl,"/* 加工数量 */ + "jgksrq,"/* 加工开始日期 */ + "jgwcrq,"/* 加工完成日期 */
						+ "bz,"/* 备注 */ + "scrwlrsj,"/* 生产任务录入时间 */ + "scrwztdm,"/* 生产任务状态代码 */
						+ "scrwlydm,"/* 生产任务来源代码 */ + "scrwfpztdm,"/* 生产任务分批状态代码 */
						+ "scrwbh,"/* 生产任务编号 */ + "jgzt," /*加工状态*/ + "scph" /*生产批号*/
				, "scrwztdm in " + scrwztdm, groupBy, orderBy + " " + asc, (page - 1) * pageSize, pageSize,
				new Object[] {});
		List<Map<String, Object>> list = datasetScrw.getList();
		bundle.put("rows", list);
		int totalPage = datasetScrw.getTotal() % pageSize == 0 ? datasetScrw.getTotal() / pageSize
				: datasetScrw.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetScrw.getTotal());
		bundle.put("records", datasetScrw.getCount());
	}
	
	
	/**
	 * 根据任务id和批次状态查询批次列表 ,多个状态的用逗号分割
	 * @param parameters
	 * @param bundle
	 */
	public void scrwpcListByRwidAndZtdm(Parameters parameters,Bundle bundle){
		String scrwid = parameters.getString("scrwid");
		String pcjhztdm = parameters.getString("pcjhztdm");
		String sql = "";
		if(StringUtils.isNotEmpty(pcjhztdm)){
			pcjhztdm = pcjhztdm.replace(",", "','");
			pcjhztdm = "'" + pcjhztdm + "'"; 
			sql = " and pcjhztdm in("+ pcjhztdm + ")";
		}
		
		String groupBy = null;
		String orderBy = StringUtils.isBlank(parameters.getString("sortName")) 
				? " pcbh " : parameters.getString("sortName");
		String asc =  StringUtils.isBlank(parameters.getString("sortOrder")) 
				? " asc " : parameters.getString("sortOrder");
		
		int page = parameters.get("page") == null ? 1 : parameters.getInteger("page");
		int pageSize = parameters.get("pageSize") == null ? 100 : parameters.getInteger("pageSize");
		
		Dataset datasetScpc = Sys.query(TableConstant.生产任务批次, "scrwpcid,scrwid,ljid,pcbh,"
				+ "pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq", "scrwid = ? "  + sql
				, groupBy, orderBy + " " + asc, (page-1)*pageSize, pageSize,new Object[]{scrwid});
		List<Map<String, Object>> scrwpcList = datasetScpc.getList();
		bundle.put("rows", scrwpcList);
		
		int totalPage = datasetScpc.getTotal() % pageSize== 0 ? datasetScpc.getTotal()/pageSize:
			datasetScpc.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetScpc.getTotal());
		bundle.put("records", datasetScpc.getCount());
	}
	
	/**
	 * 根据生产批次id，得到生产任务和任务批次信息
	 * @param parameters
	 * @param bundle
	 */
	public void scrwAndPcInfoByPcid(Parameters parameters,Bundle bundle){
		String [] models = new String[]{TableConstant.生产任务批次,TableConstant.生产任务};
		String join = new StringBuffer().append(TableConstant.生产任务批次).append(" join ")
				.append(TableConstant.生产任务).append(" on ").append(TableConstant.生产任务批次)
				.append(".scrwid = ").append(TableConstant.生产任务).append(".scrwid").toString();
		StringBuffer fieldsSb = new StringBuffer().append(TableConstant.生产任务).append(".scrwid,")
				.append(TableConstant.生产任务).append(".ljid,")//零件编号
				.append(TableConstant.生产任务).append(".jgsl,")//加工数量
				.append(TableConstant.生产任务).append(".jgksrq,")//加工开始日期
				.append(TableConstant.生产任务).append(".jgwcrq,")//加工完成日期
				.append(TableConstant.生产任务).append(".bz,")//备注
				.append(TableConstant.生产任务).append(".scrwlrsj,")//生产任务录入时间
				.append(TableConstant.生产任务).append(".scrwztdm,")//生产任务状态代码
				.append(TableConstant.生产任务).append(".scrwlydm,")//生产任务来源代码
				.append(TableConstant.生产任务).append(".scrwfpztdm,")//生产任务分批状态代
				.append(TableConstant.生产任务).append(".scrwbh,")//生产任务编号 
				.append(TableConstant.生产任务).append(".wlid,")//物料ID
				.append(TableConstant.生产任务).append(".sjwlkcid,")//实际物料库存ID
				.append(TableConstant.生产任务).append(".mplh,")//毛坯炉号
				.append(TableConstant.生产任务).append(".scph,")//生产批号
				.append(TableConstant.生产任务批次).append(".scrwpcid,")//生产任务批次id
				.append(TableConstant.生产任务批次).append(".pcmc,")//批次名称
				.append(TableConstant.生产任务批次).append(".pcbh,")//批次编号
				.append(TableConstant.生产任务批次).append(".pcsl,")//批次数量
				.append(TableConstant.生产任务批次).append(".pcjhksrq,")//批次计划开始日期
				.append(TableConstant.生产任务批次).append(".pcjhwcrq,")//批次计划完成日期
				.append(TableConstant.生产任务批次).append(".pcjhztdm,")//批次计划状态代码
				.append(TableConstant.生产任务批次).append(".pcjhzdrq,");//批次计划制订日期
		String fields = fieldsSb.deleteCharAt(fieldsSb.length() - 1).toString();
		//String conditions  = "scrwpcid = ?";
		String orderby = null;
		StringBuffer conditionSb = new StringBuffer(" 1 = 1 ");
		List<Object> conditionValue = new ArrayList<Object>();
		String scrwpcid = parameters.getString("scrwpcid");
		if(StringUtils.isNotBlank(scrwpcid)){
			conditionSb = conditionSb.append(" and scrwpcid = ? ");
			conditionValue.add(scrwpcid);
		}
		String scrwbh = parameters.getString("scrwbh");
		if(StringUtils.isNotBlank(scrwbh)){
			conditionSb = conditionSb.append(" and scrwbh like ? ");
			conditionValue.add("%" + scrwbh.trim() + "%");
		}
		String pcbh = parameters.getString("pcbh");
		if(StringUtils.isNotBlank(pcbh)){
			conditionSb = conditionSb.append(" and pcbh like ? ");
			conditionValue.add("%" + pcbh.trim() + "%");
		}
		Dataset dataset = Sys.query(models, join, fields, conditionSb.toString(), orderby, conditionValue.toArray());
		bundle.put("scrwandpc", dataset.getMap());
		bundle.put("scrwandpcList", dataset.getList());
	}
	
	/**
	 * 修改批次状态
	 * @param parameters
	 * @param bundle
	 */
	public void updateScrwpcjhzt(Parameters parameters,Bundle bundle){
		String scrwpcid = parameters.getString("scrwpcid");
		if(StringUtils.isBlank(scrwpcid)){
			bundle.setInfo("生产任务批次id不能为空");
			return;
		}
		String pcjhztdm = parameters.getString("pcjhztdm");
		if(!BatchPlanStatus.statusList.contains(pcjhztdm)){
			bundle.setInfo("要修改的批次状态代码不合法");
			return;
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("pcjhztdm", pcjhztdm);
		Sys.update(TableConstant.生产任务批次, map, "scrwpcid = ? ", new Object[]{scrwpcid});
	}
	
	/**
	 * 修改生产任务状态
	 * @param parameters
	 * @param bundle
	 */
	public void updateScrwzt(Parameters parameters,Bundle bundle){
		String scrwid = parameters.getString("scrwid");
		if(StringUtils.isBlank(scrwid)){
			bundle.setInfo("生产任务ID不能为空");
			return;
		}
		String scrwztdm = parameters.getString("scrwztdm");
		if(!BatchPlanStatus.scrwStateList.contains(scrwztdm)){
			bundle.setInfo("要修改的生产任务状态代码不合法");
			return;
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("scrwztdm", scrwztdm);
		try{
			Sys.update(TableConstant.生产任务, map, "scrwid = ? ", new Object[]{scrwid});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**根据零件ID，加工开始日期区间，加工结束日期区间查询生产任务
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 */
	public void queryService_scrw(Parameters parameters, Bundle bundle) throws Exception {

		String query_sign = parameters.getString("query_sign");// 查询标识
		String query_scrwbh = parameters.getString("query_scrwbh");// 生产任务编号
		String scrwbh = parameters.getString("scrwbh");// 生产任务编号
		String val_lj = parameters.getString("val_lj");// 零件编号
		String query_jgksstart = parameters.getString("query_jgksstart");// 加工开始日期         
		String query_jgksend = parameters.getString("query_jgksend");// 加工开始日期
		String query_jgjsstart = parameters.getString("query_jgjsstart");// 加工结束日期
		String query_jgjsend = parameters.getString("query_jgjsend");// 加工结束日期
		String query_rwzt = parameters.getString("query_rwzt");// 任务状态
		String sortName = parameters.getString("sortName");// 排序字段
		String sortOrder = parameters.getString("sortOrder");// 排序方式 asc  desc
		
		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();
		if("query".equals(query_sign))
		{
			 if(StringUtils.isNotBlank(query_scrwbh))
				{
					con = con + " and scrwbh like ?  ";
					val.add("%"+query_scrwbh+"%");
				}
		}else if(StringUtils.isNotBlank(scrwbh))
		{
			con = con + " and scrwbh like ?  ";
			val.add("%"+scrwbh+"%");
		}
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
//		if(StringUtils.isNotBlank(query_rwzt)&&!"rwzt_all".equals(query_rwzt))
		if(StringUtils.isNotBlank(query_rwzt))
		{
			con = con + " and scrwztdm = ? ";
			val.add(query_rwzt);
		}
		if(StringUtils.isNotBlank(sortName))
		{
			sortOrder = sortName + " "+sortOrder+" ";
		}else {
			sortOrder = null;
		}
		
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetScrw = Sys.query(TableConstant.生产任务,"scrwid,scrwbh,ljid,jgsl,jgksrq,jgwcrq,bz,scrwlrsj,scrwztdm,scrwlydm,scrwfpztdm,scph", con, sortOrder, (page-1)*pageSize, pageSize,val.toArray());
		List<Map<String, Object>> sc_scrw = datasetScrw.getList();
		bundle.put("rows", sc_scrw);
		int totalPage = datasetScrw.getTotal()%pageSize==0?datasetScrw.getTotal()/pageSize:datasetScrw.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetScrw.getTotal());
	}
	
	
	/**根据生产任务ID查询批次信息
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 */
	public void queryService_pcxx(Parameters parameters, Bundle bundle) throws Exception {
		String val_scrw = parameters.getString("val_scrw");
		if (null==val_scrw) {
			return;
		}
		Dataset dataset_pcxx= Sys.query("sc_scrwpc","scrwpcid,scrwid,ljid,pcbh,pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq", "scrwid in "+val_scrw, null, new Object[]{});
		bundle.put("pcxx", dataset_pcxx.getList());
	}
	
	/**根据批次ID查询批次信息
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 */
	public void queryService_pcxxbyid(Parameters parameters, Bundle bundle) throws Exception {
		String val_pc = parameters.getString("val_pc");
		if (null==val_pc) {
			return;
		}
		Dataset dataset_pcxx= Sys.query("sc_scrwpc","scrwpcid pcid,scrwid,ljid,pcbh,pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq", "scrwpcid in "+val_pc, null, new Object[]{});
		bundle.put("pcxx", dataset_pcxx.getList());
	}
	
	public void queryService_pcxxfy(Parameters parameters, Bundle bundle) throws Exception {
		String val_scrw = parameters.getString("val_scrw");
		if (null==val_scrw) {
			return;
		}
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset dataset_pcxx= Sys.query("sc_scrwpc","scrwpcid,scrwid,ljid,pcbh,pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq", "scrwid in "+val_scrw + " and pcjhztdm in ('40','50','70','80','85','90')", "pcbh asc", (page-1)*pageSize, pageSize, new Object[]{});
		bundle.put("pcxx", dataset_pcxx.getList());
		int totalPage = dataset_pcxx.getTotal()%pageSize==0?dataset_pcxx.getTotal()/pageSize:dataset_pcxx.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", dataset_pcxx.getTotal());
	}
	
	/**
	 * 
	 * @param parameters
	 * @param bundle
	 */
	public void scrwpcListByPcid(Parameters parameters, Bundle bundle) {
		String pcid = parameters.getString("pcid");
		Dataset datasetScpc = Sys.query(TableConstant.生产任务批次, "pcbh,scrwid,pcmc",  "scrwpcid = ?", null , null , new Object[]{pcid});
		List<Map<String, Object>> scrwpcList = datasetScpc.getList();
		bundle.put("scrwpcList", scrwpcList);
	}
	
	/**
	 *	批量查询生产任务编号，根据生产任务id
	 * @param parameters where条件 例如"(1,2)"
	 * @param bundle
	 * @author Yang Fan
	 */
	public void getScrwpcbhByScrwpcid(Parameters parameters, Bundle bundle){
		String pcid = parameters.getString("pcid");
	
		Dataset datasetScrw = Sys.query(new String[] { TableConstant.生产任务批次, TableConstant.生产任务 }, TableConstant.生产任务批次 + " left join " + TableConstant.生产任务 + " on " + TableConstant.生产任务批次
				+ ".scrwid =" + TableConstant.生产任务 + ".scrwid ",  TableConstant.生产任务批次+ ".scrwpcid, "+TableConstant.生产任务批次+".scrwid, scrwbh, pcbh ", TableConstant.生产任务批次+".scrwpcid in " + pcid, null , null , new Object[]{});
		
		List<Map<String, Object>> scrwpc = datasetScrw.getList();
		bundle.put("scrwpc", scrwpc);
	}
	
	/**
	 * 查询生产任务批次id，根据批次编号或者生产任务编号
	 * 
	 * @param parameters
	 * @param bundle
	 * @author Yang Fan
	 */
	public void getScrwpcidByScrwpcbh(Parameters parameters, Bundle bundle) {
		String scrwbh = parameters.getString("scrwbh");
		String pcbh = parameters.getString("pcbh");

		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();

		if (StringUtils.isNotBlank(scrwbh)) {
			con += " and scrwbh=? ";
			val.add(scrwbh);
		}

		if (StringUtils.isNotBlank(pcbh)) {
			con += " and pcbh=? ";
			val.add(pcbh);
		}

		Dataset datasetScpc = Sys
				.query(new String[] { TableConstant.生产任务批次, TableConstant.生产任务 },
						TableConstant.生产任务批次 + " left join " + TableConstant.生产任务 + " on " + TableConstant.生产任务批次
								+ ".scrwid =" + TableConstant.生产任务 + ".scrwid ",
								TableConstant.生产任务批次+ ".scrwpcid, pcbh, "+ TableConstant.生产任务+".ljid, pcmc, pcsl", con, null, null, val.toArray());
		List<Map<String, Object>> scrwpc = datasetScpc.getList();
		bundle.put("scrwpc", scrwpc);
	}
	
	/**
	 *	根据批次编号, 模糊查询生产任务批次id
	 * 
	 * @param parameters
	 * @param bundle
	 * @author Yang Fan
	 */
	public void getScrwByScrwpcbh(Parameters parameters, Bundle bundle) {
		String scrwid = parameters.getString("scrwid");
		String pcid = parameters.getString("pcid");
		String scrwbh = parameters.getString("scrwbh");
		String pcbh = parameters.getString("pcbh");

		String con = "1 = 1 ";
		List<Object> val = new ArrayList<Object>();

		if (StringUtils.isNotBlank(pcbh)) {
			con += " and pcbh like ? ";
			val.add("%" + pcbh + "%");
		}

		if (StringUtils.isNotBlank(scrwbh)) {
			con += " and scrwbh like ? ";
			val.add("%" + scrwbh + "%");
		}
		
		if (StringUtils.isNotBlank(scrwid)) {
			con += " and sc_scrw.scrwid = ? ";
			val.add(scrwid);
		}
		
		if (StringUtils.isNotBlank(pcid)) {
			con += " and sc_scrwpc.pcid = ? ";
			val.add(pcid);
		}
		
		
		Dataset datasetScpc = Sys
				.query(new String[] { TableConstant.生产任务批次, TableConstant.生产任务 },
						TableConstant.生产任务批次 + " left join " + TableConstant.生产任务 + " on " + TableConstant.生产任务批次
								+ ".scrwid =" + TableConstant.生产任务 + ".scrwid ",
								TableConstant.生产任务批次+ ".scrwpcid, pcbh, pcmc, "+TableConstant.生产任务批次+".ljid", con, null, null, val.toArray());
		List<Map<String, Object>> scrwpc = datasetScpc.getList();
		bundle.put("scrwpc", scrwpc);
	}
	
	
	/**
	 * 查询生产任务信息和生产任务批次信息
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 * @author YangFan
	 */
	public void getScrwpcxx(Parameters parameters, Bundle bundle) throws Exception {
		
		String pcztdm = parameters.getString("pcztdm");
		String ljid = parameters.getString("ljid");
		String pcbh = parameters.getString("pcbh");
		String scrwbh = parameters.getString("scrwbh");
		
		StringBuffer con = new StringBuffer("1 = 1 ");
		List<Object> val = new ArrayList<Object>();

		if (StringUtils.isNotBlank(ljid)) {
			con.append(" and sc_scrwpc.ljid in ").append(ljid);
		}

		if (StringUtils.isNotBlank(pcbh)) {
			con.append(" and pcbh like ? ");
			val.add("%" + pcbh + "%");
		}
		
		if (StringUtils.isNotBlank(scrwbh)) {
			con.append(" and sc_scrw.scrwbh like ? ");
			val.add("%" + scrwbh + "%");
		}
		
		if (StringUtils.isNotBlank(pcztdm)) {
			con.append(" and sc_scrwpc.pcjhztdm =? ");
			val.add(pcztdm);
		}
		
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		
		Dataset dataset_pcxx = Sys
				.query(new String[] { TableConstant.生产任务批次, TableConstant.生产任务 },
						TableConstant.生产任务批次 + " left join " + TableConstant.生产任务 + " on " + TableConstant.生产任务批次
								+ ".scrwid =" + TableConstant.生产任务 + ".scrwid ",
								"sc_scrwpc.scrwpcid,sc_scrwpc.scrwid,sc_scrwpc.ljid,pcbh,pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq,sc_scrw.scrwbh",
								con.toString(),"pcbh", (page-1)*pageSize,  pageSize, val.toArray());
		
		bundle.put("pcxx", dataset_pcxx.getList());
		int totalPage = dataset_pcxx.getTotal() % pageSize == 0 ? dataset_pcxx.getTotal() / pageSize
				: dataset_pcxx.getTotal() / pageSize + 1;
		
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", dataset_pcxx.getTotal());
	}
	
	/**
	 * 
	 * 检查生产任务信息，更新批次状态信息
	 * 1. 更新生产任务批次状态【已入库】
	 * 2. 查询该批次对应生产任务下，是否有没入库的批次数据。
	 * 3. 如果都已经入库，并且批次数量的总和 大于等于 生产任务数据，则更新生产任务表的生产任务状态为【已完成】
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 * @author YangFan
	 */
	public void updateScrwpczt(Parameters parameters, Bundle bundle) throws Exception {

		String pcid = parameters.getString("pcid");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pcjhztdm", "85");// 已入库状态
		Sys.update(TableConstant.生产任务批次, map, "scrwpcid=?", pcid);

		// 查询是否存在未入库的批次，如果没有（该任务都已入库），则更新生产任务状态为：已完成。
		Dataset pcDs = Sys.query(TableConstant.生产任务批次, "scrwid", "scrwpcid=?", null, null, pcid);

		if (pcDs != null && pcDs.getMap() != null) {
			String scrwid = pcDs.getMap().get("scrwid").toString();
			if (StringUtils.isNotBlank(scrwid)) {
				Dataset scrwDs = Sys.query(new String[] { TableConstant.生产任务批次, TableConstant.生产任务 },
						TableConstant.生产任务批次 + " left join " + TableConstant.生产任务 + " on " + TableConstant.生产任务批次
								+ ".scrwid =" + TableConstant.生产任务 + ".scrwid ",
						"sc_scrw.scrwid, sc_scrw.jgsl,sc_scrwpc.pcsl, pcjhztdm", "sc_scrwpc.scrwid=?", null,null, scrwid);

				if (null != scrwDs && scrwDs.getCount() > 0) {
					List<Map<String, Object>> scrwList = scrwDs.getList();
					if (null != scrwList) {
						Map<String, Object> scrwMap = null;
						boolean isRk = true;
						int totalPcsl = 0;
						int jgsl = 0;
						for (int i = 0; i < scrwList.size(); i++) {
							scrwMap = (Map<String, Object>) scrwList.get(i);
							if (null != scrwMap) {
								String pczt = scrwMap.get("pcjhztdm").toString();
								if (isRk && null != pczt && !"85".equals(pczt)) {
									isRk = false;
								}
								totalPcsl += Integer.parseInt(scrwMap.get("pcsl").toString());
								jgsl = Integer.parseInt(scrwMap.get("jgsl").toString());
							}
						}

						// 全部都是已入库状态，同时【批次数量总和】 大于等于 【加工数量】，更新生产任务状态。
						if (isRk && totalPcsl >= jgsl) {
							Map<String, Object> scrwztMap = new HashMap<String, Object>();
							scrwztMap.put("scrwztdm", BatchPlanStatus.done);// 已完成
							Sys.update(TableConstant.生产任务, scrwztMap, "scrwid=?",  new Object[]{scrwid});
						}
					}
				}
			}
		}
	}
	
	/**
	 * 根据生产批次id，查询生产任务及批次信息
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 * @author YangFan
	 */
	public void getScrwpcxxBypcid(Parameters parameters, Bundle bundle) throws Exception {
		
		String pcid = parameters.getString("pcid");
		
		StringBuffer con = new StringBuffer();
		List<Object> val = new ArrayList<Object>();

		if (StringUtils.isNotBlank(pcid)) {
			con.append(" sc_scrwpc.pcid in ?");
			val.add(pcid);
		}
		
		Dataset dataset_pcxx = Sys
				.query(new String[] { TableConstant.生产任务批次, TableConstant.生产任务 },
						TableConstant.生产任务批次 + " left join " + TableConstant.生产任务 + " on " + TableConstant.生产任务批次
								+ ".scrwid =" + TableConstant.生产任务 + ".scrwid ",
								"sc_scrwpc.scrwpcid,sc_scrwpc.scrwid,sc_scrwpc.ljid,pcbh,pcmc,pcsl,pcjhksrq,pcjhwcrq,pcjhztdm,pcjhzdrq,sc_scrw.scrwbh,scph,mplh",
								con.toString(),"pcbh", val.toArray());
		
		bundle.put("pcxx", dataset_pcxx.getList());
	}
	
	/**通过批次ID查询生产批号
	 * @param parameters
	 * @param bundle
	 */
	public void query_scbh(Parameters parameters, Bundle bundle) {
		String val_gd = parameters.get("val_gd").toString();
		Dataset dataset_scbh = Sys.query( new String[]{"sc_scrwpc","sc_scrw"},"sc_scrwpc right join sc_scrw on sc_scrwpc.scrwid = sc_scrw.scrwid","scrwbh, scrwpcid", "scrwpcid in "+val_gd, null, new Object[]{});
		bundle.put("scbh", dataset_scbh.getList());
		
	}
	/**通过批次ID查询生产批号
	 * @param parameters
	 * @param bundle
	 */
	public void query_scph(Parameters parameters, Bundle bundle) {
		String val_gd = parameters.get("val_gd").toString();
		Dataset dataset_scbh = Sys.query( new String[]{"sc_scrwpc","sc_scrw"},"sc_scrwpc right join sc_scrw on sc_scrwpc.scrwid = sc_scrw.scrwid","scph, scrwpcid,mplh", "scrwpcid in "+val_gd, null, new Object[]{});
		bundle.put("scbh", dataset_scbh.getList());
		
	}
	/**
	 * 根据批次ID查询查询生产任务的加工状态
	 * @param parameters
	 * @param bundle
	 */
	public void queryJgztByPcid(Parameters parameters, Bundle bundle) {
		String pcid = parameters.get("pcid").toString();
		Dataset dataset = Sys.query(new String[] { "sc_scrwpc", "sc_scrw" },
				"sc_scrwpc join sc_scrw on sc_scrwpc.scrwid = sc_scrw.scrwid", "jgzt", "scrwpcid = " + pcid, null,
				new Object[] {});
		bundle.put("jgzt", dataset.getMap().get("jgzt"));
	}
	
	/**
	 * 查询加工中的生产任务
	 * @param parameters
	 * @param bundle
	 */
	public void queryProcessingScrw(Parameters parameters, Bundle bundle){
		
		String date_jgksbegin = parameters.get("date_jgksbegin")==null?"":parameters.get("date_jgksbegin").toString();
		String date_jgksend = parameters.get("date_jgksend")==null?"":parameters.get("date_jgksend").toString();
		String date_jgjsbegin = parameters.get("date_jgjsbegin")==null?"":parameters.get("date_jgjsbegin").toString();
		String date_jgjsend = parameters.get("date_jgjsend")==null?"":parameters.get("date_jgjsend").toString();
		String ljids = parameters.get("ljids")==null?"":parameters.get("ljids").toString();
		
		StringBuffer con = new StringBuffer();
		
		con.append("scrwztdm = '20'");
		
		if(!StringUtils.isEmpty(date_jgksbegin)){
			con.append(" and jgksrq > '" + date_jgksbegin + "'");
		}
		if(!StringUtils.isEmpty(date_jgksend)){
			con.append(" and jgksrq < '" + date_jgksend + "'");
		}
		if(!StringUtils.isEmpty(date_jgjsbegin)){
			con.append(" and jgwcrq > '" + date_jgjsbegin + "'");
		}
		if(!StringUtils.isEmpty(date_jgjsend)){
			con.append(" and jgwcrq < '" + date_jgjsend + "'");
		}
		if(!StringUtils.isEmpty(ljids)){
			con.append(" and ljid in " + ljids);
		}
		
		int page = parameters.get("page") == null ? 1 : parameters.getInteger("page");
		int pageSize = parameters.get("pageSize") == null ? 10 : parameters.getInteger("pageSize");
		
		Dataset dataset = Sys.query("sc_scrw", "scrwid,ljid,scrwbh,jgksrq,jgwcrq,jgsl", con.toString(), null, (page - 1) * pageSize, pageSize, new Object[]{});
		
		List<Map<String, Object>> scrw_list = dataset.getList();
		
		int totalPage = dataset.getTotal() % pageSize == 0 ? dataset.getTotal() / pageSize
				: dataset.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", dataset.getCount());
		bundle.put("rows", scrw_list);
	}
	/**
	 * 根据生产任务信息
	 * @param parameters
	 * @param bundle
	 */
	public void query_scrw(Parameters parameters, Bundle bundle) {
//		String pcid = parameters.get("pcid").toString();
		Dataset dataset_ljxx = Sys.query("sc_scrw","scrwid,scrwbh,ljid,jgsl,dw,jgksrq,jgwcrq,bz,scrwlrsj,scrwztdm,scrwlydm,scrwfpztdm,scph,wlid,sjwlkcid,jgzt,jhsl,yxwc,mplh", null, null, new Object[]{});
		bundle.put("scrw", dataset_ljxx.getList());
	}
	
	public void query_scrwandpic(Parameters parameters, Bundle bundle){
		String scrwid = parameters.get("scrwid").toString();
		
		Dataset scxx_set = Sys.query(new String[]{"sc_scrw", "sc_scrwpc"},
				"sc_scrw left join sc_scrwpc on sc_scrw.scrwid = sc_scrwpc.scrwid", 
				"jgsl,scrwpcid,sc_scrw.scrwid", "sc_scrw.scrwid = '" + scrwid +"'",
				null, new Object[]{});
		
		List<Map<String, Object>> scxx_list = scxx_set.getList();
		
		bundle.put("scxx_list", scxx_list);
	}
	
	/**
	 * 根据生产批次id，得到生产任务
	 * @param parameters
	 * @param bundle
	 */
	public void scrwInfoByPcid(Parameters parameters, Bundle bundle) {
		String[] models = new String[] { TableConstant.生产任务批次, TableConstant.生产任务 };
		String join = new StringBuffer().append(TableConstant.生产任务批次).append(" join ").append(TableConstant.生产任务)
				.append(" on ").append(TableConstant.生产任务批次).append(".scrwid = ").append(TableConstant.生产任务)
				.append(".scrwid").toString();
		StringBuffer fieldsSb = new StringBuffer().append(TableConstant.生产任务).append(".scrwid,")
				.append(TableConstant.生产任务).append(".ljid,")// 零件编号
				.append(TableConstant.生产任务).append(".jgsl,")// 加工数量
				.append(TableConstant.生产任务).append(".jgksrq,")// 加工开始日期
				.append(TableConstant.生产任务).append(".jgwcrq,")// 加工完成日期
				.append(TableConstant.生产任务).append(".bz,")// 备注
				.append(TableConstant.生产任务).append(".scrwlrsj,")// 生产任务录入时间
				.append(TableConstant.生产任务).append(".scrwztdm,")// 生产任务状态代码
				.append(TableConstant.生产任务).append(".scrwlydm,")// 生产任务来源代码
				.append(TableConstant.生产任务).append(".scrwfpztdm,")// 生产任务分批状态代
				.append(TableConstant.生产任务).append(".scrwbh,")// 生产任务编号
				.append(TableConstant.生产任务).append(".wlid,")// 物料ID
				.append(TableConstant.生产任务).append(".sjwlkcid,")// 实际物料库存ID
				.append(TableConstant.生产任务).append(".mplh,")// 毛坯炉号
				.append(TableConstant.生产任务).append(".scph,")// 生产批号
				.append(TableConstant.生产任务).append(".jgzt,");// 加工状态
		String fields = fieldsSb.deleteCharAt(fieldsSb.length() - 1).toString();
		String orderby = null;
		List<Object> conditionValue = new ArrayList<Object>();
		conditionValue.add(parameters.getString("pcid"));
		Dataset dataset = Sys.query(models, join, fields, "scrwpcid = ?", orderby, conditionValue.toArray());
		bundle.put("scrw", dataset.getMap());
	}
}
