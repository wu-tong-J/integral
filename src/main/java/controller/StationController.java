package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dto.PageDTO;
import dto.ResDTO;
import entity.Station;
import entity.StationBusinessGoods;
import entity.User;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.SbgService;
import service.StationService;
import service.UserService;
import utils.ComplexPropertyPreFilter;
import utils.StringUtil;
import utils.UUIDUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @date 2019-04-23 14:00
 **/
@Controller
@RequestMapping("/station")
public class StationController {

	@Autowired
	private StationService stationService;
	@Autowired
	private SbgService sbgService;
	@Autowired
	private UserService userService;

	@RequestMapping("/toStationList")
	public String toStationList(HttpServletRequest request, HttpServletResponse response) {
		return "/views/stationList";
	}

	@RequestMapping(value = "/getStationList",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String name) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)){
				page = "1";
				limit = "20";
			}
			Station station = new Station();
			station.setName(name);
			//判断角色
			Boolean b = false;
			User user = (User) request.getSession(false).getAttribute("user");
			if(user.getRole()== Constant.ROLE_HIGHER_ADMIN){
				//查找下属驿站管理员管理的所有驿站
				List<User> userList = userService.findByUserId(user.getPkUserId());
				List<String> managers = userList.stream().map(User::getPkUserId).collect(Collectors.toList());
				if(userList==null || userList.size()==0){
					b = true;
				}
				station.setManagers(managers);
			}else if(user.getRole()== Constant.ROLE_STATION_ADMIN){
				//查找自身管理的驿站
				station.setManager(user.getPkUserId());
			}
			Page<Station> stationList = stationService.getStationList(station, page, limit);
			pageDTO.setCount(String.valueOf(stationList.getTotalElements()));
			pageDTO.setData(stationList.getContent());
			if(stationList.getTotalElements()==0 || b){
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ComplexPropertyPreFilter filter = new ComplexPropertyPreFilter();
		filter.setExcludes(new HashMap<Class<?>, String[]>() {
			{
				put(Station.class, new String[]{"sbgList"});
			}
		});
		return JSON.toJSONString(pageDTO, filter,SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/appGetStationList",produces = "text/html;charset=UTF-8")
	public String appGetStationList(HttpServletRequest request, HttpServletResponse response, String page, String limit) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)){
				page = "1";
				limit = "10";
			}
			Station station = new Station();
			Page<Station> stationList = stationService.getStationList(station, page, limit);
			for (Station s : stationList.getContent()) {
				List<StationBusinessGoods> list = sbgService.findGoodsByStationId(s.getPkStationId());
				s.setGoodsNum(list.size());
			}
			request.setAttribute("count",stationList.getTotalElements());
			request.setAttribute("curr",page);
			request.setAttribute("list",stationList.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute("mean",2);
		return "stationList";
	}

	@RequestMapping(value = "/getStationSBGList",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationSBGList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String name) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)){
				page = "1";
				limit = "20";
			}
			User user = (User) request.getSession(false).getAttribute("user");
			Station station = new Station();
			station.setName(name);
			Page<Station> stationList = stationService.getStationList(station, page, limit);
			//去掉不合条件的
			List<Station> content = stationList.getContent();
			for (Station s : content) {
				List<StationBusinessGoods> sbgList = s.getSbgList();
				for (int i = 0; i < sbgList.size(); i++) {
					if(sbgList.get(i).getGoodsId()!=null || !user.getPkUserId().equals(sbgList.get(i).getBusinessId())){
						sbgList.remove(i);
						i--;
					}
				}
			}
			pageDTO.setCount(String.valueOf(stationList.getTotalElements()));
			pageDTO.setData(content);
			if(stationList.getTotalElements()==0){
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ComplexPropertyPreFilter filter = new ComplexPropertyPreFilter();
		filter.setExcludes(new HashMap<Class<?>, String[]>() {
			{
				put(StationBusinessGoods.class, new String[]{"station"});
			}
		});
		//DisableCircularReferenceDetect防止引用相同对象时，出现转换错误$ref
		return JSON.toJSONString(pageDTO, filter,SerializerFeature.DisableCircularReferenceDetect);
	}

	@ResponseBody
	@RequestMapping("/save")
	public String save(HttpServletRequest request, HttpServletResponse response,Station station){
		ResDTO resDTO = new ResDTO();
		try {
			if(!StringUtil.notNull(station.getPkStationId())){
				station.setPkStationId(UUIDUtil.getUUID());
			}
			stationService.save(station);
			//修改被选择的驿站管理员的stationId
			userService.updateStationId(station.getPkStationId(),station.getManager());
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setCode(1);
			 resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * @param type 0 不可见 1 可见
	 **/
	@ResponseBody
	@RequestMapping("/seeOrNot")
	public String seeOrNot(HttpServletRequest request, HttpServletResponse response, StationBusinessGoods sbg,Integer type){
		ResDTO resDTO = new ResDTO();
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			sbg.setBusinessId(user.getPkUserId());
			sbgService.seeOrNot(sbg,type);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setCode(1);
			 resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 是否上首页推荐
	 **/
	@ResponseBody
	@RequestMapping("/ifRecommend")
	public String ifRecommend(HttpServletRequest request, HttpServletResponse response, Integer ifRecommend,String stationId){
		ResDTO resDTO = new ResDTO();
		try {
			if(ifRecommend==1){
				//最多允许三个推荐
				List<Station> recommend = stationService.findByIfRecommend(1);
				if(recommend!=null && recommend.size()>=3){
					resDTO.setMsg("outRange");
				}else{
					stationService.updateIfRecommend(ifRecommend,stationId);
					resDTO.setMsg("ok");
				}
			}else if(ifRecommend==0){
				stationService.updateIfRecommend(ifRecommend,stationId);
				resDTO.setMsg("ok");
			}
		} catch (Exception e) {
			resDTO.setCode(1);
			 resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	@RequestMapping("/getOne")
	public String getOne(HttpServletRequest request, HttpServletResponse response,String pkStationId){
		try {
			Station station = stationService.getOne(pkStationId);
			request.setAttribute("station",station);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "views/stationForm";
	}
}
