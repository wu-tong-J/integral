package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dto.PageDTO;
import dto.ResDTO;
import entity.Config;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ConfigService;
import utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2019-04-23 14:00
 **/
@Controller
@RequestMapping("/config")
public class ConfigController {

	@Autowired
	private ConfigService configService;

	@RequestMapping("/toConfig")
	public String toConfig(HttpServletRequest request, HttpServletResponse response) {
		//查询配置项
		List<String> list = new ArrayList<>();
		list.add(Constant.CONFIG_EXCHANGE);
		list.add(Constant.CONFIG_AUTH_OLD_MAN);
		list.add(Constant.CONFIG_AUTO_POINTS);
		list.add(Constant.CONFIG_STAR);
		list.add(Constant.CONFIG_DEDUCTION);
		request.setAttribute("selectList",list);
		return "/views/configList";
	}

	@RequestMapping(value = "/toAddConfig")
	public String toAddConfig(HttpServletRequest request, HttpServletResponse response,String configType,String pkCfgId) {
		try {
			if(StringUtil.notNull(configType)){
				//新增
				//configType = new String(configType.getBytes("iso8859-1"),"utf-8");
				request.setAttribute("configType",configType);
			}else{
				//修改
				Config config = configService.getOne(pkCfgId);
				request.setAttribute("config",config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/addConfig";
	}

	@RequestMapping(value = "/updateStatus")
	@ResponseBody
	public String updateStatus(HttpServletRequest request, HttpServletResponse response,Integer status,String name) {
		ResDTO resDTO = new ResDTO();
		try {
			configService.updateStatus(status,name);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setCode(1);
			resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	@ResponseBody
	@RequestMapping(value = "/save",produces = "text/html;charset=UTF-8")
	public String save(HttpServletRequest request, HttpServletResponse response, Config config,String myKey1,String myKey2){
		ResDTO resDTO = new ResDTO();
		if(StringUtil.notNull(myKey1) && StringUtil.notNull(myKey2)){
			config.setTerm(myKey1+"~"+myKey2);
		}
		try {
			//查看同系配置项状态
			List<Config> byName = configService.findByName(config.getName());
			if(byName!=null && byName.size()>0){
				Integer status = byName.get(0).getStatus();
				config.setStatus(status);
			}else{
				config.setStatus(1);
			}
			String msg = configService.saveOrUpdate(config);
			resDTO.setMsg(msg);
		} catch (Exception e) {
			resDTO.setCode(1);
			resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	@ResponseBody
	@RequestMapping(value = "/del",produces = "text/html;charset=UTF-8")
	public String del(HttpServletRequest request, HttpServletResponse response, String pkCfgId){
		ResDTO resDTO = new ResDTO();
		try {
			configService.del(pkCfgId);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setCode(1);
			resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	@ResponseBody
	@RequestMapping(value = "/getConfigList",produces = "text/html;charset=UTF-8")
	public String getConfigList(HttpServletRequest request, HttpServletResponse response, Config config){
		PageDTO pageDTO = new PageDTO();
		try {
			List<Config> configList = configService.getConfigList(config);
			if(configList==null || configList.size()==0){
				pageDTO.setCode(1);
				pageDTO.setMsg("无数据");
			}else{
				pageDTO.setCount(String.valueOf(configList.size()));
				pageDTO.setData(configList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}
}
