package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dto.PageDTO;
import dto.ResDTO;
import dto.SelfAuthDTO;
import entity.*;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ConfigService;
import service.SbgService;
import service.StationService;
import service.UserService;
import utils.ComplexPropertyPreFilter;
import utils.MD5Util;
import utils.StringUtil;
import utils.VerifyCodeUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static utils.VerifyCodeUtil.TYPE_ALL_MIXED;
import static utils.VerifyCodeUtil.TYPE_NUM_ONLY;

/**
 * @author
 * @date 2019-04-23 14:00
 **/
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private SbgService sbgService;
	@Autowired
	private StationService stationService;
	@Autowired
	private ConfigService configService;

	/**
	 * 各类积分明细
	 **/
	@RequestMapping(value = "/appGetIntegralList", produces = "text/html;charset=UTF-8")
	public String appGetIntegralList(HttpServletRequest request, HttpServletResponse response, String pageNum, String limit, String type) {
		Page<IntegralDetail> page = null;
		try {
			if (!StringUtil.notNull(pageNum) || !StringUtil.notNull(limit)) {
				pageNum = "1";
				limit = "10";
			}
			IntegralDetail integralDetail = new IntegralDetail();
			HttpSession session = request.getSession(false);
			User sessionUser = (User) session.getAttribute("user");
			integralDetail.setPointsType(Integer.parseInt(type));
			integralDetail.setUserId(sessionUser.getPkUserId());
			page = userService.getIntegralList(integralDetail, pageNum, limit);
			request.setAttribute("list", JSON.toJSON(page.getContent()));
			//当前页码
			request.setAttribute("curr", page.getNumber() + 1);
			request.setAttribute("count", page.getTotalElements());
			request.setAttribute("type", type);
			String typeName = "";
			String countName = "";
			if ("1".equals(type)) {
				typeName = "积分明细";
				countName = "累计积分";
			} else if ("2".equals(type)) {
				typeName = "赠分明细";
				countName = "累计赠分";
			} else if ("3".equals(type)) {
				typeName = "工分明细";
				countName = "累计工分";
			}
			request.setAttribute("typeName", typeName);
			request.setAttribute("countName", countName);
			//计算累计收入
			BigDecimal comeIn = userService.countComeIn(sessionUser.getPkUserId(), Integer.parseInt(type));
			request.setAttribute("comeIn", comeIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "integarl-details";
	}

	@RequestMapping(value = "/toIntegralList")
	public String toIntegralList(HttpServletRequest request, HttpServletResponse response) {
		return "views/integralList";
	}

	//兑换赠分页面
	@RequestMapping(value = "/appToExchangeBonus")
	public String appToExchangeBonus(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		User sessionUser = (User) session.getAttribute("user");
		User one = userService.getOne(sessionUser.getPkUserId());
		request.setAttribute("interPoints",one.getIntegralPoints());
		request.setAttribute("workPoints",one.getWorkPoints());
		//兑换规则
		List<Config> configWork = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1工分可兑换");
		List<Config> configIntre = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1积分可兑换");
		request.setAttribute("transWork",configWork.get(0).getContent());
		request.setAttribute("transIntre",configIntre.get(0).getContent());
		return "b_exchange";
	}

	/**
	 * 各类积分明细
	 *
	 * @param type 1加分2减分
	 **/
	@RequestMapping(value = "/getIntegralList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getIntegralList(HttpServletRequest request, HttpServletResponse response, String pageNum, String limit, String type, User user, IntegralDetail integralDetail) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(pageNum) || !StringUtil.notNull(limit)) {
				pageNum = "1";
				limit = "10";
			}
			integralDetail.setUser(user);
			HttpSession session = request.getSession(false);
			User sessionUser = (User) session.getAttribute("user");
			if (StringUtil.notNull(type)) {
				integralDetail.setPoints(new BigDecimal(Integer.parseInt(type)));
			}
			//处理可见范围
			List<String> stationIds = new ArrayList<>();
			if (sessionUser.getRole() == Constant.ROLE_SUPER_ADMIN) {
				//所有人，不用处理
			} else if (sessionUser.getRole() == Constant.ROLE_STATION_ADMIN || sessionUser.getRole() == Constant.ROLE_EMPLOYEE) {
				//所在的驿站下的所有人
				stationIds.add(sessionUser.getStationId());
			} else if (sessionUser.getRole() == Constant.ROLE_HIGHER_ADMIN) {
				//下属驿站管理员管理的驿站下的所有人
				List<User> users = userService.findByUserId(sessionUser.getPkUserId());
				List<String> ids = users.stream().map(User::getStationId).collect(Collectors.toList());
				stationIds.addAll(ids);
			} else {
				//只能看自己的
				integralDetail.setUserId(sessionUser.getPkUserId());
			}
			integralDetail.setStationIds(stationIds);
			Page<IntegralDetail> integralList = userService.getIntegralList(integralDetail, pageNum, limit);
			pageDTO = new PageDTO("", String.valueOf(integralList.getTotalElements()), integralList.getContent());
			if (integralList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	//保存个人资料
	@RequestMapping("/appMyIntegral")
	public String appMyIntegral(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession(false);
			User sessionUser = (User) session.getAttribute("user");
			User user = userService.getOne(sessionUser.getPkUserId());
			if (user.getRole() == Constant.ROLE_VOLUNTEER_WORKER || user.getRole()==Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
				//计算贡献度属于几星
				Integer star = configService.getStarLvlByUserId(user.getPkUserId());
				if (star == null) {
					request.setAttribute("gxd", "系统未设置贡献度规则");
				} else {
					request.setAttribute("gxd", star + "星");
				}
			}
			request.setAttribute("integralUser", user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "integral";
	}

	//保存个人资料
	@RequestMapping("/appSaveInfo")
	public String appSaveInfo(HttpServletRequest request, HttpServletResponse response, User user, Authentication auth, String authRole, String birth) {
		//保存个人基本资料
		HttpSession session = request.getSession(false);
		User sessionUser = (User) session.getAttribute("user");
		sessionUser.setSex(user.getSex());
		sessionUser.setPic(user.getPic());
		sessionUser.setUsername(user.getUsername());
		if (StringUtil.notNull(birth)) {
			LocalDate parse = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			sessionUser.setBirthday(parse);
		}
		if (StringUtil.notNull(authRole)) {
			sessionUser.setRoleStatus(0);
			auth.setToChangeRole(Integer.valueOf(authRole));
			auth.setStatus(0);
			auth.setUserId(sessionUser.getPkUserId());
			//保存认证信息
			userService.saveAuth(auth);
		}
		userService.saveInfo(sessionUser);
		//更新session
		session.setAttribute("user", sessionUser);
		return "centre";
	}

	//跳转个人中心
	@RequestMapping("/appToInfo")
	public String appToInfo(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			List<Authentication> selfAuthList = userService.getSelfAuthList(user.getPkUserId());
			request.setAttribute("userJson", JSON.toJSON(user));
			//如果是老人，查询紧急联系人信息
			if (user.getRole() == Constant.ROLE_OLD_MAN || user.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
				for (Authentication a : selfAuthList) {
					if ((a.getToChangeRole() == Constant.ROLE_OLD_MAN || a.getToChangeRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) && a.getStatus() == 1) {
						request.setAttribute("emergencyPerson", a.getEmergencyPerson());
						request.setAttribute("emergencyPhone", a.getEmergencyPhone());
					}
				}
			}
			List<SelfAuthDTO> roleList = new ArrayList<>();
			roleList.add(new SelfAuthDTO(Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER, "老人+义工", 0, ""));
			roleList.add(new SelfAuthDTO(Constant.ROLE_OLD_MAN, "老人", 0, ""));
			roleList.add(new SelfAuthDTO(Constant.ROLE_VOLUNTEER_WORKER, "义工", 0, ""));

			//增加状态信息
			for (SelfAuthDTO r : roleList) {
				for (Authentication a : selfAuthList) {
					if (r.getRole() == a.getToChangeRole()) {
						if (a.getStatus() == 0) {
							//审核中
							r.setStatus(1);
							r.setStatusName("(审核中)");
						} else if (a.getStatus() == 1) {
							//已通过
							r.setStatus(2);
							r.setStatusName("(已通过)");
						}
					}
				}
			}
			//去除不该显示的选项
			for (int i = 0; i < roleList.size(); i++) {
				SelfAuthDTO dto = roleList.get(i);
				if (dto.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
					if (dto.getStatus() != 0) {
						SelfAuthDTO temp = dto;
						roleList.removeAll(roleList);
						roleList.add(temp);
						break;
					}
				} else {
					if (dto.getStatus() == 1) {
						for (int j = 0; j < roleList.size(); j++) {
							SelfAuthDTO d = roleList.get(j);
							if (d.getRole() != dto.getRole() && d.getStatus() != 2) {
								roleList.remove(j);
								j--;
							}
						}
						break;
					} else if (dto.getStatus() == 2) {
						for (int j = 0; j < roleList.size(); j++) {
							SelfAuthDTO d = roleList.get(j);
							if (d.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
								roleList.remove(j);
								break;
							}
						}
					}
				}
			}
			request.setAttribute("selfAuthList", JSON.toJSON(roleList));
			//驿站列表
			List<Station> stationList = stationService.findAll();
			request.setAttribute("stationList", stationList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "sate";
	}

	@RequestMapping("/toUpdateInfo")
	public String toUpdateInfo(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			request.setAttribute("userJson", JSON.toJSON(user));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "views/set/user/info";
	}

	@RequestMapping("/saveInfo")
	@ResponseBody
	public String saveInfo(HttpServletRequest request, HttpServletResponse response, User user, String code, String birth) {
		ResDTO resDTO = new ResDTO();
		try {
			if (StringUtil.notNull(birth)) {
				LocalDate parse = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				user.setBirthday(parse);
			}
			HttpSession session = request.getSession(false);
			User sessionUser = (User) session.getAttribute("user");
			//判断是否更改了手机号
			if (!sessionUser.getPhone().equals(user.getPhone())) {
				String sessionPassPhone = (String) session.getAttribute("passPhone");
				//判断是否在获取验证码后又修改了手机号
				if (!sessionPassPhone.equals(user.getPhone())) {
					resDTO.setMsg("changePhoneError");
				} else {
					String sessionVerifyCode = (String) session.getAttribute("passCode");
					if (!code.equalsIgnoreCase(sessionVerifyCode)) {
						resDTO.setMsg("codeError");
					}
				}
			}
			if (!("codeError".equals(resDTO.getMsg()) || ("changePhoneError".equals(resDTO.getMsg())))) {
				sessionUser.setUsername(user.getUsername());
				sessionUser.setPhone(user.getPhone());
				sessionUser.setBirthday(user.getBirthday());
				sessionUser.setPic(user.getPic());
				sessionUser.setSex(user.getSex());
				userService.saveInfo(sessionUser);
				session.setAttribute("user", sessionUser);
				resDTO.setMsg("ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 登录
	 **/
	@RequestMapping("/toLogin")
	public String toLogin(HttpServletRequest request, HttpServletResponse response) {
		return "redirect:/views/user/login.jsp";
	}

	/**
	 * 跳转主页
	 **/
	@RequestMapping("/toMain")
	public String toMain(HttpServletRequest request, HttpServletResponse response) {
		return "/views/index";
	}

	/**
	 * 跳转用户列表页
	 *
	 * @param type 请求的按钮类型 1 身份认证 2 手动增分 3 商户管理 4 人员管理
	 **/
	@RequestMapping("/toUserList")
	public String toUserList(HttpServletRequest request, HttpServletResponse response, String type) {
		request.setAttribute("selectType", type);
		return "/views/user/user/list";
	}

	/**
	 * 跳转管理员工页
	 **/
	@RequestMapping("/toManageEmplyoee")
	public String toManageEmplyoee(HttpServletRequest request, HttpServletResponse response, String stationId) {
		request.setAttribute("stationId", stationId);
		return "/views/manageEmployee";
	}

	/**
	 * 跳转管理驿站管理员工页
	 **/
	@RequestMapping("/toManageStationManager")
	public String toManageStationManager(HttpServletRequest request, HttpServletResponse response, String userId) {
		request.setAttribute("userId", userId);
		return "/views/manageStationManager";
	}

	@RequestMapping("/appTocenter")
	public String appTocenter(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().setAttribute("mean", 4);
		return "centre";
	}

	@RequestMapping("/appLogin")
	@ResponseBody
	public String appLogin(HttpServletRequest request, HttpServletResponse response, String phone, String password) {
		User user = userService.login(phone, password);
		if (user != null) {
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			session.setAttribute("mean", 1);
		} else {
			return "fail";
		}
		return "ok";
	}

	/**
	 * 登录
	 **/
	@RequestMapping("/login")
	@ResponseBody
	public String login(HttpServletRequest request, HttpServletResponse response, String phoneOrUsername, String password, String verifyCode) {
		ResDTO resDTO = new ResDTO();
		try {
			HttpSession session = request.getSession();
			String sessionVerifyCode = (String) session.getAttribute("verifyCode");
			//if(verifyCode.equalsIgnoreCase(sessionVerifyCode)){
			User user = userService.login(phoneOrUsername, password);
			if (user == null) {
				resDTO.setMsg("fail");
			} else {
				resDTO.setMsg("ok");
				session.setAttribute("user", user);
				//没有活动30分钟后，session将失效(30*60)
				session.setMaxInactiveInterval(60 * 60);
			}
			//}else{
			//	resDTO.setMsg("verifyCodeError");
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	@RequestMapping("/del")
	@ResponseBody
	public String del(HttpServletRequest request, HttpServletResponse response, String pkUserId) {
		try {
			userService.del(pkUserId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(new ResDTO("ok"));
	}

	@RequestMapping("/logout")
	@ResponseBody
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			clearSessionAndCookie(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(new ResDTO("ok"));
	}

	private void clearSessionAndCookie(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		/*//清除cookie
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("username")){
				cookie.setMaxAge(0);
				cookie.setPath(request.getContextPath());
				response.addCookie(cookie);
			}else if (cookie.getName().equals("password")){
				cookie.setMaxAge(0);
				cookie.setPath(request.getContextPath());
				response.addCookie(cookie);
			}
		}*/
	}

	/**
	 * @Description:异步加载验证码图片
	 **/
	@RequestMapping("/getCodeImg")
	@ResponseBody
	public void getCodeImg(HttpServletRequest request, HttpServletResponse response) {
		try {
			//设置浏览器不缓存本页
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			//生成验证码，写入用户session
			String verifyCode = VerifyCodeUtil.generateTextCode(TYPE_ALL_MIXED, 4, null);
			System.out.println(verifyCode);
			request.getSession().setAttribute("verifyCode", verifyCode);
			//输出验证码给客户端
			response.setContentType("image/jpeg");
			BufferedImage bim = VerifyCodeUtil.generateImageCode(verifyCode, 70, 22, 15, true, Color.WHITE, Color.BLACK, null);
			ServletOutputStream out = response.getOutputStream();
			ImageIO.write(bim, "JPEG", out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送手机验证码
	 **/
	@ResponseBody
	@RequestMapping("/sendPassCode")
	public String sendPassCode(HttpServletRequest request, HttpServletResponse response, String phone) {
		ResDTO resDTO = new ResDTO();
		try {
			//检测手机号是否已经注册
			boolean ifPhoneExiste = userService.ifPhoneExist(phone);
			if (ifPhoneExiste) {
				resDTO.setMsg("phoneExist");
			} else {
				String passCode = VerifyCodeUtil.generateTextCode(TYPE_NUM_ONLY, 5, null);
				//发送验证码
				System.out.println("发送成功：" + passCode);

				HttpSession session = request.getSession();
				session.setAttribute("passCode", passCode);
				session.setAttribute("passPhone", phone);
				//验证码30分钟内有效
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						session.removeAttribute("passCode");
						timer.cancel();
					}
				}, 30 * 60 * 1000);
				resDTO.setMsg("ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 发送手机验证码(不检查是否手机被注册)
	 **/
	@ResponseBody
	@RequestMapping("/sendPassCodeNoCheck")
	public String sendPassCodeNoCheck(HttpServletRequest request, HttpServletResponse response, String phone) {
		ResDTO resDTO = new ResDTO();
		try {
			String passCode = VerifyCodeUtil.generateTextCode(TYPE_NUM_ONLY, 5, null);
			//发送验证码
			System.out.println("发送成功：" + passCode);

			HttpSession session = request.getSession();
			session.setAttribute("passCode", passCode);
			//验证码30分钟内有效
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					session.removeAttribute("passCode");
					timer.cancel();
				}
			}, 30 * 60 * 1000);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 验证用户名唯一性
	 **/
	@ResponseBody
	@RequestMapping("/checkNameUnique")
	public String checkNameUnique(HttpServletRequest request, HttpServletResponse response, String username) {
		ResDTO resDTO = new ResDTO();
		try {
			Boolean b = userService.ifExistByName(username);
			if (!b) {
				resDTO.setMsg("ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 修改密码
	 **/
	@ResponseBody
	@RequestMapping("/updatePassword")
	public String updatePassword(HttpServletRequest request, HttpServletResponse response, String type, String passcode, String oldPassword, String password) {
		ResDTO resDTO = new ResDTO();
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			if ("2".equals(type)) {
				String passCode = (String) session.getAttribute("passCode");
				if (!StringUtil.notNull(passCode)) {
					resDTO.setMsg("passcodeTimeOut");
				} else if (!passcode.equalsIgnoreCase(passCode)) {
					resDTO.setMsg("passcodeError");
				}
			}
			if (!("passcodeTimeOut".equals(resDTO.getMsg()) || "passcodeError".equals(resDTO.getMsg()))) {
				String res = userService.updatePassword(user.getPkUserId(), type, passcode, oldPassword, password);
				resDTO.setMsg(res);
				if ("ok".equals(res)) {
					//更新session
					user.setPassword(MD5Util.parseStringToMD5(password));
					session.setAttribute("user", user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 注册
	 **/
	@RequestMapping("/register")
	@ResponseBody
	public String register(HttpServletRequest request, HttpServletResponse response, String phone, String password, String passCode) {
		ResDTO resDTO = new ResDTO();
		try {
			HttpSession session = request.getSession();
			String code = (String) session.getAttribute("passCode");
			String res = userService.register(phone, password);
			resDTO.setMsg(res);
			/*if (passCode.equalsIgnoreCase(code)) {
				String res = userService.register(phone, password);
				resDTO.setMsg(res);
			}else{
				resDTO.setMsg("codeError");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 修改隶属关系
	 **/
	@RequestMapping("/updateUserId")
	@ResponseBody
	public String updateUserId(HttpServletRequest request, HttpServletResponse response, String[] userIds, String userId) {
		ResDTO resDTO = new ResDTO();
		try {
			if (!StringUtil.notNull(userId)) {
				userService.addOrDelUserId(Arrays.asList(userIds), null);
			} else {
				userService.addOrDelUserId(Arrays.asList(userIds), userId);
			}
			resDTO.setMsg("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 获取需要进行身份验证的用户列表
	 **/
	@RequestMapping(value = "/getNeedDealList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getNeedDealList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setRoleStatus(Constant.ROLE_STATUS_NEEDDEAL);
			user.setPhone(phone);
			user.setUsername(username);
			List<Integer> roles = new ArrayList<>();
			/*roles.add(Constant.ROLE_COMMON);
			roles.add(Constant.ROLE_OLD_MAN);
			roles.add(Constant.ROLE_VOLUNTEER_WORKER);*/
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//查询申请的角色
			for (User u : userList) {
				Authentication authInfo = userService.getAuthInfo(u.getPkUserId());
				u.setChangeRole(authInfo.getToChangeRole());
			}
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 人员管理
	 **/
	@RequestMapping(value = "/getStationUsers", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationUsers(HttpServletRequest request, HttpServletResponse response, String page, String limit, User user) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User sessionUser = (User) request.getSession(false).getAttribute("user");
			List<Integer> roles = new ArrayList<>();
			if(user.getRole()==null){
				roles.add(Constant.ROLE_OLD_MAN);
				roles.add(Constant.ROLE_VOLUNTEER_WORKER);
				roles.add(Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER);
				roles.add(Constant.ROLE_EMPLOYEE);
			}
			if(sessionUser.getRole()==Constant.ROLE_HIGHER_ADMIN){
				//查找下属驿站管理员管理的驿站
				List<Station> stations = userService.getStationListByuserId(user.getPkUserId());
				List<String> stringList = stations.stream().map(Station::getPkStationId).collect(Collectors.toList());
				user.setStations(stringList);
			}else if(sessionUser.getRole()==Constant.ROLE_STATION_ADMIN || sessionUser.getRole()==Constant.ROLE_EMPLOYEE){
				List<String> stationIds = new ArrayList<>();
				stationIds.add(sessionUser.getStationId());
				user.setStations(stationIds);
			}
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取该驿站下需要进行审批可见性的商户列表
	 **/
	@RequestMapping(value = "/getStationNeedCheckBusiness", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationNeedCheckBusiness(HttpServletRequest request, HttpServletResponse response, String page, String limit, User user) {
		PageDTO pageDTO = new PageDTO();
		try {
			User sessionUser = (User) request.getSession(false).getAttribute("user");
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			StationBusinessGoods sbg = new StationBusinessGoods();
			sbg.setUser(user);
			if (StringUtil.notNull(sessionUser.getStationId())) {
				//超级管理员和上级可以看到所有待审批驿站申请
				sbg.setStationId(sessionUser.getStationId());
			}
			Page<StationBusinessGoods> list = sbgService.getStationNeedCheckBusiness(sbg, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(list.getTotalElements()), list.getContent());
			if (list.getTotalElements() == 0) {
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
		return JSON.toJSONString(pageDTO, filter, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取驿站下商户列表(错误的，带改正)
	 **/
	@RequestMapping(value = "/getStationBusinessList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationBusinessList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			User sessionUser = (User) request.getSession(false).getAttribute("user");
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			user.setStationId(sessionUser.getStationId());
			List<Integer> roles = new ArrayList<>();
			roles.add(Constant.ROLE_BUSINESS);
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取可以手动增分的用户列表
	 **/
	@RequestMapping(value = "/getAddPointsList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getAddPointsList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			List<Integer> roles = new ArrayList<>();
			roles.add(Constant.ROLE_COMMON);
			roles.add(Constant.ROLE_OLD_MAN);
			roles.add(Constant.ROLE_VOLUNTEER_WORKER);
			roles.add(Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER);
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取驿站员工列表
	 **/
	@RequestMapping(value = "/getStationUserList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationUserList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String stationId, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			user.setStationId(stationId);
			List<Integer> roles = new ArrayList<>();
			roles.add(Constant.ROLE_EMPLOYEE);
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取下属驿站管理员列表 或 可分配驿站管理员
	 **/
	@RequestMapping(value = "/getStationManagerListByUserId", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationManagerListByUserId(HttpServletRequest request, HttpServletResponse response, String page, String limit, String userId, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			if (StringUtil.notNull(userId)) {
				//获取下属驿站管理员
				user.setUserId(userId);
			} else {
				//可分配驿站管理员
				user.setUserId("null");
			}
			List<Integer> roles = new ArrayList<>();
			roles.add(Constant.ROLE_STATION_ADMIN);
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取管辖的上级和驿站管理员列表
	 **/
	@RequestMapping(value = "/getNoStationManagerList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getNoStationManagerList(HttpServletRequest request, HttpServletResponse response, String page, String limit,User user) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User sessionUser = (User) request.getSession(false).getAttribute("user");
			//user.setStationId("null");
			if(sessionUser.getRole() == Constant.ROLE_HIGHER_ADMIN){
				user.setUserId(sessionUser.getPkUserId());
			}
			List<Integer> roles = new ArrayList<>();
			if(user.getRole()==null && sessionUser.getRole() == Constant.ROLE_SUPER_ADMIN){
				roles.add(Constant.ROLE_HIGHER_ADMIN);
				roles.add(Constant.ROLE_STATION_ADMIN);
			}else if(user.getRole()==null){
				roles.add(Constant.ROLE_STATION_ADMIN);
			}else if(user.getRole()!=null){
				roles.add(user.getRole());
			}
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取普通用户列表
	 **/
	@RequestMapping(value = "/getCommonUserList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getCommonUserList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			//有待处理身份认证的，排除掉
			user.setRoleStatus(10);
			List<Integer> roles = new ArrayList<>();
			roles.add(Constant.ROLE_COMMON);
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取带有驿站范围的商户列表
	 **/
	@RequestMapping(value = "/getBusiList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBusiList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User sessionUser = (User) request.getSession(false).getAttribute("user");
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			StationBusinessGoods sbg = new StationBusinessGoods();
			sbg.setUser(user);
			sbg.setGoodsId("null");
			sbg.setIfApproval(1);
			if(sessionUser.getRole()==Constant.ROLE_STATION_ADMIN){
				sbg.setStationId(sessionUser.getStationId());
			}else if(sessionUser.getRole()==Constant.ROLE_HIGHER_ADMIN){
				List<User> list = userService.findByUserId(sessionUser.getPkUserId());
				List<String> ids = list.stream().map(User::getStationId).collect(Collectors.toList());
				sbg.setStationIds(ids);
			}
			Page<StationBusinessGoods> userList = sbgService.getList(sbg, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
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
		return JSON.toJSONString(pageDTO, filter, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 根据角色获取用户列表
	 **/
	@RequestMapping(value = "/getUserListByRoles", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getUserListByRoles(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username, Integer[] roles) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			List<Integer> rolesList = Arrays.asList(roles);
			Page<User> userList = userService.getUserList(user, rolesList, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping("/addOrDelEmp")
	@ResponseBody
	public String addOrDelEmp(HttpServletRequest request, HttpServletResponse response, String[] userIds, String stationId) {
		ResDTO resDTO = new ResDTO();
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			List<String> list = Arrays.asList(userIds);
			userService.addOrDelEmp(list, stationId, user.getPkUserId());
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setMsg("error");
			resDTO.setCode(1);
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 驿站管理员列表
	 **/
	@RequestMapping(value = "/getStationManagerList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationManagerList(HttpServletRequest request, HttpServletResponse response, String page, String limit, String phone, String username) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = new User();
			user.setPhone(phone);
			user.setUsername(username);
			user.setStationId("null");
			List<Integer> roles = new ArrayList<>();
			roles.add(Constant.ROLE_STATION_ADMIN);
			Page<User> userList = userService.getUserList(user, roles, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(userList.getTotalElements()), userList.getContent());
			if (userList.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 身份认证
	 *
	 * @param userId 要更改的用户id
	 **/
	@ResponseBody
	@RequestMapping("/changeRole")
	public String changeRole(HttpServletRequest request, HttpServletResponse response, String userId, Integer role, Integer roleStatus, String pkAuthId, String remark) {
		String res = null;
		User user = (User) request.getSession(false).getAttribute("user");
		try {
			res = userService.changeRole(userId, role, user.getPkUserId(), roleStatus, pkAuthId, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(new ResDTO(res));
	}

	@ResponseBody
	@RequestMapping("/toBeBusi")
	public String toBeBusi(HttpServletRequest request, HttpServletResponse response, String[] userIds) {
		String res = "";
		try {
			userService.toBeBusi(Arrays.asList(userIds));
			res = "ok";
		} catch (Exception e) {
			e.printStackTrace();
			res = "error";
		}
		return JSON.toJSONString(new ResDTO(res));
	}

	/**
	 * 新增管理员（上级、驿站）
	 *
	 * @param passcode 验证码
	 **/
	@ResponseBody
	@RequestMapping("/createManager")
	public String createManager(HttpServletRequest request, HttpServletResponse response, User user, String passcode) {
		ResDTO resDTO = new ResDTO();
		try {
			user.setPassword(MD5Util.parseStringToMD5(user.getPassword()));
			User sessionUser = (User) request.getSession(false).getAttribute("user");
			//待删除===================
			if ("".equals(user.getPhone())) {
				user.setPhone(null);
			}
			if ("".equals(user.getUsername())) {
				user.setUsername(null);
			}
			userService.createManager(user);
			resDTO.setMsg("ok");
			if (sessionUser.getRole() == Constant.ROLE_HIGHER_ADMIN && user.getRole() == Constant.ROLE_STATION_ADMIN) {
				//上级管理员创建的驿站管理员直接带上隶属关系
				user.setUserId(sessionUser.getPkUserId());
			}
			//================================
			/*if (StringUtil.notNull(passcode)) {
				HttpSession session = request.getSession();
				String passCode = (String) session.getAttribute("passCode");
				if (!passcode.equalsIgnoreCase(passCode)) {
					resDTO.setMsg("passcodeError");
				}else{
					if ("".equals(user.getPhone())) {
						user.setPhone(null);
					}
					if ("".equals(user.getUsername())) {
						user.setUsername(null);
					}
					user.setPassword(MD5Util.parseStringToMD5(user.getPassword()));
					if(sessionUser.getRole()==Constant.ROLE_HIGHER_ADMIN && user.getRole()==Constant.ROLE_STATION_ADMIN){
						//上级管理员创建的驿站管理员直接带上隶属关系
						user.setUserId(sessionUser.getPkUserId());
					}
					userService.createManager(user);
					resDTO.setMsg("ok");
				}
			}else{
				if ("".equals(user.getPhone())) {
					user.setPhone(null);
				}
				if ("".equals(user.getUsername())) {
					user.setUsername(null);
				}
				user.setPassword(MD5Util.parseStringToMD5(user.getPassword()));
				if(sessionUser.getRole()==Constant.ROLE_HIGHER_ADMIN && user.getRole()==Constant.ROLE_STATION_ADMIN){
					//上级管理员创建的驿站管理员直接带上隶属关系
					user.setUserId(sessionUser.getPkUserId());
				}
				userService.createManager(user);
				resDTO.setMsg("ok");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 获取身份认证信息
	 **/
	@RequestMapping("/getAuthInfo")
	public String getAuthInfo(HttpServletRequest request, HttpServletResponse response, String userId) {
		try {
			Authentication authInfo = userService.getAuthInfo(userId);
			request.setAttribute("authInfo", authInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "views/user/user/authInfo";
	}

	/**
	 * 给用户加分
	 *
	 * @param pointType 分数类型 1 积分 2 赠分 3 工分
	 **/
	@RequestMapping("/addPoints")
	@ResponseBody
	public String addPoints(HttpServletRequest request, HttpServletResponse response, String userId, String remark, String points, String pointType) {
		ResDTO resDTO = new ResDTO();
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			BigDecimal bigDecimal = new BigDecimal(points);
			List<String> ids = new ArrayList<>();
			ids.add(userId);
			if ("1".equals(pointType)) {
				userService.addPoints(bigDecimal, new BigDecimal(0), new BigDecimal(0), ids, remark, user.getPkUserId());
			} else if ("2".equals(pointType)) {
				userService.addPoints(new BigDecimal(0), bigDecimal, new BigDecimal(0), ids, remark, user.getPkUserId());
			} else if ("3".equals(pointType)) {
				userService.addPoints(new BigDecimal(0), new BigDecimal(0), bigDecimal, ids, remark, user.getPkUserId());
			}
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setCode(1);
			resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 审批商户可见性
	 **/
	@RequestMapping("/checkBusinessSee")
	@ResponseBody
	public String checkBusinessSee(HttpServletRequest request, HttpServletResponse response, Integer status, String[] pkSbgIds) {
		ResDTO resDTO = new ResDTO();
		try {
			sbgService.checkBusinessSee(status, pkSbgIds);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			resDTO.setCode(1);
			resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 设置商户提成
	 **/
	@RequestMapping(value = "/setBusinessPerc", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String setBusinessPerc(HttpServletRequest request, HttpServletResponse response, String perc, String[] pkBusiIds, String[] stationIds) {
		ResDTO resDTO = new ResDTO();
		try {
			String res = sbgService.setBusinessPerc(perc, pkBusiIds, stationIds);
			resDTO.setMsg(res);
		} catch (Exception e) {
			resDTO.setCode(1);
			resDTO.setMsg("error");
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 设置提成商户列表
	 **/
	@RequestMapping(value = "/getSetPercBusiList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getSetPercBusiList(HttpServletRequest request, HttpServletResponse response, String page, String limit, StationBusinessGoods sbg, User user) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			sbg.setUser(user);
			sbg.setGoodsId("null");
			Page<StationBusinessGoods> list = sbgService.getList(sbg, page, limit);
			//构造可以用于layui展示的实体
			pageDTO = new PageDTO("", String.valueOf(list.getTotalElements()), list.getContent());
			if (list.getTotalElements() == 0) {
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
		return JSON.toJSONString(pageDTO, filter, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/getStationListByUserId", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getStationListByUserId(HttpServletRequest request, HttpServletResponse response) {
		//获取用户下属驿站列表
		List<Station> stationList = null;
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			if (user.getRole() == Constant.ROLE_SUPER_ADMIN) {
				//直接获取所有驿站
				stationList = stationService.findAll();
			} else if (user.getRole() == Constant.ROLE_HIGHER_ADMIN) {
				//查找下属驿站管理员管理的驿站
				stationList = userService.getStationListByuserId(user.getPkUserId());
			} else {
				//查找自身管理的驿站
				List<String> ids = new ArrayList<>();
				ids.add(user.getPkUserId());
				stationList = stationService.findByManagerIn(ids);
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
		return JSON.toJSONString(stationList, filter, SerializerFeature.DisableCircularReferenceDetect);
	}
}
