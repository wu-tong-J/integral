package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dto.PageDTO;
import dto.ResDTO;
import entity.Activity;
import entity.ActivityFeedback;
import entity.Sign;
import entity.User;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ActivityService;
import service.UserService;
import utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2019-04-23 14:00
 **/
@Controller
@RequestMapping("/activity")
public class ActivityController {
	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserService userService;

	@RequestMapping("/toSeeBusiAct")
	public String toSeeBusiAct(HttpServletRequest request, HttpServletResponse response, String userId) {
		request.setAttribute("userId",userId);
		return "views/seeActList";
	}

	@RequestMapping("/save")
	@ResponseBody
	public String toLogin(HttpServletRequest request, HttpServletResponse response, Activity activity) {
		ResDTO resDTO = new ResDTO();
		try {
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			activity.setUserId(user.getPkUserId());
			activityService.save(activity);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * @param userId 实际需要报名的用户id，不为null说明是代报名
	 **/
	@RequestMapping("/appJoinAct")
	@ResponseBody
	public String appJoinAct(HttpServletRequest request, HttpServletResponse response, String actId, String userId) {
		try {
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			ActivityFeedback af = new ActivityFeedback();
			String realUserId = "";
			if (StringUtil.notNull(userId)) {
				//说明是代报名
				realUserId = userId;
			}else{
				realUserId = user.getPkUserId();
			}
			//是否已经报过名了
			ActivityFeedback userFeedback = activityService.getUserFeedback(actId, realUserId);
			if(userFeedback!=null){
				return "joined";
			}
			//查看代报名用户是否是义工
			User one = userService.getOne(realUserId);
			if (one.getRole() != Constant.ROLE_VOLUNTEER_WORKER) {
				return "notYG";
			}
			af.setUserId(realUserId);
			//查看该活动是否还有名额
			List<ActivityFeedback> feedbackList = activityService.findByActId(actId);
			Activity activity = activityService.getOne(actId);
			if(activity.getNum()!=null){
				if(activity.getNum()-feedbackList.size()<=0){
					return "canNotJoin";
				}
			}
			af.setActId(actId);
			af.setRegister(user.getPkUserId());
			activityService.saveFeedBack(af);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "ok";
	}

	/**
	 * @param userId 实际需要报名的用户id
	 * @param type   1 签到 2 签退
	 **/
	@RequestMapping("/appSignInOrOut")
	@ResponseBody
	public String appSignInOrOut(HttpServletRequest request, HttpServletResponse response, String actId, String userId, String type) {
		try {
			Sign sign = new Sign();
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			String selectUserId = "";
			if (StringUtil.notNull(userId)) {
				//代签
				selectUserId = userId;
				sign.setRealUserId(user.getPkUserId());
				sign.setUserId(userId);
			} else {
				selectUserId = user.getPkUserId();
				sign.setRealUserId(user.getPkUserId());
				sign.setUserId(user.getPkUserId());
			}
			//判断今儿是否已经签了
			List<Sign> signList = activityService.getSignByUserId(actId, selectUserId, Integer.parseInt(type));
			LocalDateTime now = LocalDateTime.now().withNano(0);
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String nowString = df.format(now);
			for (Sign s : signList) {
				String day = df.format(s.getCreateTime());
				if (nowString.equals(day)) {
					//今天已经签过了
					return "signed";
				}
			}
			sign.setActId(actId);
			sign.setCreateTime(now);
			sign.setType(Integer.parseInt(type));
			activityService.saveSign(sign);
			//奖励分
			if("2".equals(type)){
				Activity activity = activityService.getOne(actId);
				List<String> ids = new ArrayList<>();
				ids.add(selectUserId);
				userService.addPoints(new BigDecimal(0),new BigDecimal(0),activity.getRewardPoints(),ids,"活动签到签退",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "ok";
	}

	@RequestMapping("/getInfo")
	public String getInfo(HttpServletRequest request, HttpServletResponse response, String actId) {
		try {
			Activity activity = activityService.getOne(actId);
			String serviceProvider = activity.getServiceProvider();
			if (StringUtil.notNull(serviceProvider)) {
				String serviceProviderName = "";
				String[] arr = serviceProvider.split(";");
				for (String userId : arr) {
					User user = userService.getOne(userId);
					if (StringUtil.notNull(user.getUsername())) {
						serviceProviderName = serviceProviderName + user.getUsername() + ";";
					} else {
						serviceProviderName = serviceProviderName + user.getPhone() + ";";
					}
				}
				request.setAttribute("serviceProviderName", serviceProviderName);
			}
			if (activity.getContent() != null) {
				request.setAttribute("content", new String(activity.getContent()));
			}
			request.setAttribute("act", JSON.toJSON(activity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "views/actForm";
	}

	@RequestMapping("/appGetInfo")
	public String appGetInfo(HttpServletRequest request, HttpServletResponse response, String actId) {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			Activity activity = activityService.getOne(actId);
			if (activity.getNum() != null) {
				//计算剩余报名人数
				Integer count = activityService.getCount(activity.getPkActId());
				activity.setRemain(activity.getNum() - count);
			}
			if (activity.getContent() != null) {
				request.setAttribute("content", new String(activity.getContent()));
			}
			request.setAttribute("act", JSON.toJSON(activity));
			//计算评星
			ActivityFeedback af = new ActivityFeedback();
			af.setActId(activity.getPkActId());
			Page<ActivityFeedback> feedBackList = activityService.getFeedBackList(af, "1", "5");
			if (feedBackList != null && feedBackList.getContent().size() > 0) {
				BigDecimal allStar = activityService.countAllStar(activity.getPkActId());
				if (allStar != null) {
					allStar = allStar.divide(new BigDecimal(feedBackList.getTotalElements()), 1);
					request.setAttribute("evalStar", allStar);//表示保留一位小数，默认用四舍五入方式
				}
			}
			request.setAttribute("feedBackSize", feedBackList.getTotalElements());//表示保留一位小数，默认用四舍五入方式
			//用户名报名状态
			ActivityFeedback feedback = activityService.getUserFeedback(actId, user.getPkUserId());
			if (feedback == null) {
				//控制报名按钮显示与否
				request.setAttribute("ifJoin", 0);
			}else{
				request.setAttribute("ifJoin", 1);
			}
			//判断活动是否开始，控制签到签退按钮的显示
			LocalDateTime sTime = activity.getsTime();
			LocalDateTime now = LocalDateTime.now().withNano(0);
			if(now.compareTo(sTime)>=0){
				request.setAttribute("actStart",1);
			}
			//我的活动列表，即使活动结束，依旧可以看见，所以需要验证活动结束
			/*LocalDateTime eTime = activity.geteTime();
			if(now.compareTo(eTime)>=0){
				request.setAttribute("actEnd",1);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "com-details";
	}

	/**
	 * 评价活动
	 **/
	@RequestMapping(value = "/eval", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String eval(HttpServletRequest request, HttpServletResponse response, String actId, String star, String eval) {
		String res = "";
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			activityService.evaluate(eval, new BigDecimal(star), actId, user.getPkUserId());
			res = "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(res);
	}

	/**
	 * 去评价
	 **/
	@RequestMapping(value = "/appToEval", produces = "application/json; charset=utf-8")
	public String appToEval(HttpServletRequest request, HttpServletResponse response, String actId, String page) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		ActivityFeedback feedback = activityService.getUserFeedback(actId, user.getPkUserId());
		if (feedback != null) {
			request.setAttribute("star", feedback.getStar());
			request.setAttribute("content", feedback.getFeedback());
		}
		request.setAttribute("actId", actId);
		request.setAttribute("curr", page);
		return "act-evaluate";
	}

	/**
	 * 活动评价
	 **/
	@RequestMapping(value = "/appGetFeedBackListByActId", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String appGetFeedBackListByActId(HttpServletRequest request, HttpServletResponse response, String page, String limit, String actId) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "5";
			}
			ActivityFeedback af = new ActivityFeedback();
			af.setActId(actId);
			af.setFeedback("isNotNull");
			Page<ActivityFeedback> feedBackList = activityService.getFeedBackList(af, page, limit);
			pageDTO.setCount(String.valueOf(feedBackList.getTotalElements()));
			pageDTO.setData(feedBackList.getContent());
			if (feedBackList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue/*null值依旧参与序列化*/);
	}

	/**
	 * 活动列表
	 **/
	@RequestMapping(value = "/getActList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getActList(HttpServletRequest request, HttpServletResponse response, String page, String limit, Activity activity) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			HttpSession session = request.getSession();
			User sessionUser = (User) session.getAttribute("user");
			if(sessionUser.getRole()==Constant.ROLE_BUSINESS){
				activity.setUserId(sessionUser.getPkUserId());
			}
			Page<Activity> activityList = activityService.getActivityList(activity, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(activityList.getTotalElements()), activityList.getContent());
			if (activityList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 前台活动列表
	 **/
	@RequestMapping(value = "/appGetActList", produces = "text/html;charset=UTF-8")
	public String appGetActList(HttpServletRequest request, HttpServletResponse response, String pageNum, String limit, Activity activity) {
		Page<Activity> page = null;
		try {
			if (!StringUtil.notNull(pageNum) || !StringUtil.notNull(limit)) {
				pageNum = "1";
				limit = "6";
			}
			activity.setStatus(1);
			page = activityService.getActivityList(activity, pageNum, limit);
			for (Activity act : page.getContent()) {
				if (act.getNum() != null) {
					//计算剩余报名人数
					Integer count = activityService.getCount(act.getPkActId());
					act.setRemain(act.getNum() - count);
				}
			}
			request.setAttribute("list", page.getContent());
			//当前页码
			request.setAttribute("curr", page.getNumber() + 1);
			request.setAttribute("count", page.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute("mean", 3);
		return "community";
	}

	/**
	 * 前台我的活动
	 **/
	@RequestMapping(value = "/appMyActList", produces = "text/html;charset=UTF-8")
	public String appMyActList(HttpServletRequest request, HttpServletResponse response, String pageNum, String limit, ActivityFeedback activityFeedback) {
		Page<ActivityFeedback> page = null;
		try {
			if (!StringUtil.notNull(pageNum) || !StringUtil.notNull(limit)) {
				pageNum = "1";
				limit = "6";
			}
			HttpSession session = request.getSession();
			User sessionUser = (User) session.getAttribute("user");
			activityFeedback.setUserId(sessionUser.getPkUserId());
			page = activityService.getFeedBackList(activityFeedback, pageNum, limit);
			for (ActivityFeedback af : page.getContent()) {
				//签到多少天
				List<Sign> signList = activityService.getSignByUserId(af.getActId(), sessionUser.getPkUserId(), 1);
				af.getActivity().setSignInDays(signList.size());
			}
			request.setAttribute("list", JSON.toJSON(page.getContent()));
			//当前页码
			request.setAttribute("curr", page.getNumber() + 1);
			request.setAttribute("count", page.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "myActList";
	}
}
