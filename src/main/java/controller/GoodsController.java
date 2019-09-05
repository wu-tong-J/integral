package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dto.PageDTO;
import dto.ResDTO;
import entity.*;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.*;
import utils.ComplexPropertyPreFilter;
import utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author
 * @date 2019-04-23 14:00
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private SbgService sbgService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private StationService stationService;

	@RequestMapping("/toSeeBusiGoods")
	public String toSeeBusiGoods(HttpServletRequest request, HttpServletResponse response, String userId) {
		request.setAttribute("userId",userId);
		return "views/seeGoodsList";
	}

	/**
	 * 商户下的商品列表
	 **/
	@RequestMapping(value = "/getBusinessGoodsList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBusinessGoodsList(HttpServletRequest request, HttpServletResponse response, String page, String limit, Goods goods) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			User user = (User) request.getSession(false).getAttribute("user");
			if(user.getRole()==Constant.ROLE_BUSINESS){
				goods.setUserId(user.getPkUserId());
			}else{
				if(StringUtil.notNull(goods.getUserId())){
					goods.setUserId(goods.getUserId());
				}
			}
			Page<Goods> list = goodsService.getGoodsList(goods, page, limit);
			pageDTO = new PageDTO("", String.valueOf(list.getTotalElements()), list.getContent());
			if (list.getTotalElements() == 0) {
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 商户发货
	 **/
	@RequestMapping(value = "/appSendOutGoods", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String appSendOutGoods(HttpServletRequest request, HttpServletResponse response, String orderId) {
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			//校验订单信息
			OrderDetail order = orderService.getOne(orderId);
			//是否已经领取
			if(order.getStatus()!=0){
				//商品已经领取
				return "sended";
			}
			//当前登录商户是否是订单里的商户或商品所在驿站的管理员
			Goods goods = goodsService.getOne(order.getGoodsId());
			Station station = stationService.getOne(order.getStationId());
			if(!(goods.getUserId().equals(user.getPkUserId()) || station.getManager().equals(user.getPkUserId()))){
				//不是该商品的商户或商品所在驿站的管理员
				return "notThisBusiOrMana";
			}
			//发货
			orderService.updateStatus(1,orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ok";
	}

	/**
	 **/
	@RequestMapping(value = "/toUpdateInfo", produces = "text/html;charset=UTF-8")
	public String toUpdateInfo(HttpServletRequest request, HttpServletResponse response, String goodsId) {
		Goods goods = goodsService.getOne(goodsId);
		if (goods.getContent() != null) {
			request.setAttribute("content", new String(goods.getContent()));
		}
		request.setAttribute("goods", goods);
		return "views/goodsForm";
	}

	@RequestMapping(value = "/appGetInfo", produces = "text/html;charset=UTF-8")
	public String appGetInfo(HttpServletRequest request, HttpServletResponse response, String goodsId, String stationId) {
		try {
			Goods goods = goodsService.getOne(goodsId);
			if (goods.getContent() != null) {
				request.setAttribute("content", new String(goods.getContent()));
			}
			//计算销售量和评价星级
			List<OrderDetail> sellList = orderService.findByGoodsId(goodsId);
			BigDecimal evalStar = null;
			//参与评分人数
			int n = 0;
			//已评价人数
			int hadEval = 0;
			for (OrderDetail o : sellList) {
				if (o.getStar() != null) {
					evalStar = evalStar.add(o.getStar());
					n++;
				}
				if (o.getStatus() != null && o.getStatus() == 2) {
					hadEval++;
				}
			}
			//销量
			goods.setSellCount(sellList.size());
			if (n != 0) {
				evalStar = evalStar.divide(new BigDecimal(n), 1, RoundingMode.HALF_UP);
			}
			request.setAttribute("evalStar", evalStar);//表示保留一位小数，默认用四舍五入方式
			request.setAttribute("count", hadEval);
			if (StringUtil.notNull(goods.getPriceWork()) && goods.getPriceWork().contains("~")) {
				User user = (User) request.getSession(false).getAttribute("user");
				if (user.getRole() == Constant.ROLE_VOLUNTEER_WORKER) {
					//计算当前用户是几星义工
					Integer star = configService.getStarLvlByUserId(user.getPkUserId());
					String s = goods.getPriceWork().split("~")[star - 1];
					goods.setPriceWork(s);
				}
			}
			request.setAttribute("goods", goods);
			if(goods.getGetType() == Constant.GET_TYPE_BUSINESS){
				request.setAttribute("getType","实体店");
				request.setAttribute("address",goods.getAddress());
			}else if(goods.getGetType() == Constant.GET_TYPE_STATION){
				request.setAttribute("getType","驿站");
				Station station = stationService.getOne(stationId);
				request.setAttribute("address",station.getAddress());
			}
			request.setAttribute("stationId", stationId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "exchange-details";
	}

	/**
	 * 上架下架
	 **/
	@ResponseBody
	@RequestMapping("/updateStatus")
	public String updateStatus(HttpServletRequest request, HttpServletResponse response, String status, String[] pkGoodsIds) {
		ResDTO resDTO = new ResDTO();
		try {
			List<String> list = Arrays.asList(pkGoodsIds);
			goodsService.updateStatus(Integer.valueOf(status), list);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 跳转商品兑换扣分详情页
	 **/
	@RequestMapping("/toExchange")
	public String toExchange(HttpServletRequest request, HttpServletResponse response, String goodsId, String stationId) {
		//单商品抵扣细则
		//最终成交价
		request.setAttribute("stationId",stationId);
		String dealPrice = "";
		User user = (User) request.getSession(false).getAttribute("user");
		List<String> ids = new ArrayList<>();
		ids.add(user.getPkUserId());
		Goods goods = goodsService.getOne(goodsId);
		if (goods.getType() == 1) {
			//实体商品
			if (user.getRole() == Constant.ROLE_VOLUNTEER_WORKER || user.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
				List<Config> config = configService.findByNameAndStatus(Constant.CONFIG_DEDUCTION, 1);
				//看是否设置了汇率
				if (config == null || config.size() == 0) {
					//只能按全额积分兑换
					BigDecimal decimal = user.getIntegralPoints().subtract(goods.getPriceIntegral());
					if (decimal.compareTo(new BigDecimal(0)) > -1) {
						//大于等于0
						dealPrice = goods.getPriceIntegral().toString() + "积分";
					} /*else {
						//全额扣分积分不足
						return "onlyIntreButNotEnough";
					}*/
				} else {
					//计算汇率
					//BigDecimal trans = new BigDecimal(1).divide(new BigDecimal(config.get(0).getContent()), 2, RoundingMode.HALF_UP);
					//工分兑换积分汇率修改为，抵扣1积分需要多少工分
					BigDecimal trans = new BigDecimal(config.get(0).getContent());
					BigDecimal canUse = null;
					if (goods.getPriceWork().contains("~")) {
						//按星级划分,按照当前用户等级可兑换最高分来兑换，有多少兑换多少
						String[] splitPri = goods.getPriceWork().split("~");
						//计算当前用户是几星义工
						Integer star = configService.getStarLvlByUserId(user.getPkUserId());
						//看用户拥有的是否足够抵扣当前星级积分，不够就按照全积分抵扣
						canUse = new BigDecimal(splitPri[star - 1]).multiply(trans);
					} else {
						canUse = new BigDecimal(goods.getPriceWork()).multiply(trans);
					}
					if(user.getWorkPoints().compareTo(canUse)>-1){
						//按最高可抵扣的兑换
						dealPrice = goods.getPriceIntegral().subtract(canUse.divide(trans,2, RoundingMode.HALF_UP)).toString() + "积分" + "+" + canUse.toString() + "工分";
					}
					/*if (canUse.compareTo(user.getWorkPoints()) > -1) {
						//拥有的全兑换
						dealPrice = goods.getPriceIntegral().subtract(user.getWorkPoints().divide(trans,2, RoundingMode.HALF_UP)).toString() + "积分" + "+" + user.getWorkPoints().toString() + "工分";
					} else {
						//按最高可抵扣的兑换
						dealPrice = goods.getPriceIntegral().subtract(canUse.divide(trans,2, RoundingMode.HALF_UP)).toString() + "积分" + "+" + canUse.toString() + "工分";
					}*/
				}
			}else{
				//只能按全额积分兑换
				BigDecimal decimal = user.getIntegralPoints().subtract(goods.getPriceIntegral());
				if (decimal.compareTo(new BigDecimal(0)) > -1) {
					//大于等于0
					dealPrice = goods.getPriceIntegral().toString() + "积分";
				} /*else {
					//全额扣分积分不足
					return "onlyIntreButNotEnough";
				}*/
			}
		} else if (goods.getType() == 2) {
			//服务
			/*List<Config> configWork = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1工分可兑换");
			List<Config> configIntre = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1积分可兑换");
			BigDecimal transWork = new BigDecimal(1).divide(new BigDecimal(configWork.get(0).getContent()), 2,RoundingMode.HALF_UP);
			BigDecimal transIntre = new BigDecimal(1).divide(new BigDecimal(configIntre.get(0).getContent()), 2, RoundingMode.HALF_UP);*/
			BigDecimal priceBonus = goods.getPriceBonus();
			//先扣赠分
			if (user.getBonusPoints().compareTo(priceBonus) > -1) {
				//直接扣除赠分
				dealPrice = priceBonus.toString() + "赠分";
			} /*else {
				//赠分不足，扣工分，然后扣积分
				//扣除拥有的赠分后的服务价格
				BigDecimal subedBonPrice = priceBonus.subtract(user.getBonusPoints());
				if (user.getRole() == Constant.ROLE_VOLUNTEER_WORKER || user.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
					if (configWork != null && configWork.size() > 0) {
						//判断用所有工分抵扣后价格是否满足用户条件
						//抵扣赠分后的服务价格需要多少工分兑换
						BigDecimal needWork = subedBonPrice.multiply(transWork);
						//扣除工分后的价格
						BigDecimal subedWorkPrice = subedBonPrice.subtract(needWork.divide(transWork,2, RoundingMode.HALF_UP));
						if (user.getWorkPoints().compareTo(needWork) > -1) {
							//直接扣除所需工分
							dealPrice = user.getBonusPoints().toString() + "赠分" + "+" + needWork + "工分";
						} else {
							//工分不足，计算拥有的工分全部抵扣后的赠分价格
							BigDecimal subedAllWorkPrice = subedBonPrice.subtract(user.getWorkPoints().divide(transWork,2, RoundingMode.HALF_UP));
							if (configIntre != null && configIntre.size() > 0) {
								//抵扣后的服务价格需要多少积分兑换
								BigDecimal needIntre = subedAllWorkPrice.multiply(transIntre);
								if (user.getIntegralPoints().compareTo(needIntre) > -1) {
									//直接扣除所需积分
									dealPrice = needIntre.toString() + "积分" + "+" + user.getBonusPoints().toString() + "赠分" + "+" + user.getWorkPoints() + "工分";
								} *//*else {
									//分数不足
									return "notEnough";
								}*//*
							} *//*else {
								//未设置积分兑换汇率导致没有足够分数
								return "noSetIntreNotEnough";
							}*//*
						}
					} else {
						//用积分兑换
						if (configIntre != null && configIntre.size() > 0) {
							//抵扣赠分后的服务价格需要多少积分兑换
							BigDecimal needIntre = subedBonPrice.multiply(transIntre);
							if(user.getIntegralPoints().compareTo(needIntre)>-1){
								//直接扣除所需积分
								dealPrice = needIntre.toString() + "积分" + "+" + user.getBonusPoints() + "赠分";
							}*//*else{
								return "notEnough";
							}*//*
						}*//*else{
							return "noSetIntreNotEnough";
						}*//*
					}
				} else {
					//不包含义工角色，直接扣积分
					//抵扣赠分后的服务价格需要多少积分兑换
					BigDecimal needIntre = subedBonPrice.multiply(transIntre);
					//用户是否有这些积分
					if (user.getIntegralPoints().compareTo(needIntre) > -1) {
						dealPrice = needIntre.toString() + "积分" + "+" + user.getBonusPoints().toString() + "赠分";
					} *//*else {
						//积分不足
						return "notEnough";
					}*//*
				}
			}*/
		}
		//积分
		String dealPrice1 = "0";
		//工分
		String dealPrice2 = "0";
		//赠分
		String dealPrice3 = "0";
		if(dealPrice.contains("+")){
			String[] split = dealPrice.split("\\+");
			for (String s : split) {
				if(s.contains("积分")){
					dealPrice1 = s.replace("积分","");
				}else if(s.contains("工分")){
					dealPrice2 = s.replace("工分","");
				}else if(s.contains("赠分")){
					dealPrice3 = s.replace("赠分","");
				}
			}
		}else{
			if(dealPrice.contains("积分")){
				dealPrice1 = dealPrice.replace("积分","");
			}else if(dealPrice.contains("工分")){
				dealPrice2 = dealPrice.replace("工分","");
			}else if(dealPrice.contains("赠分")){
				dealPrice3 = dealPrice.replace("赠分","");
			}
		}
		//判断是否有某项积分不足
		if(new BigDecimal(dealPrice1).compareTo(user.getIntegralPoints())>0 || new BigDecimal(dealPrice2).compareTo(user.getWorkPoints())>0 || new BigDecimal(dealPrice3).compareTo(user.getBonusPoints())>0){
			dealPrice1 = "0";
			dealPrice2 = "0";
			dealPrice3 = "0";
		}
		request.setAttribute("dealPrice1",dealPrice1);
		request.setAttribute("dealPrice2",dealPrice2);
		request.setAttribute("dealPrice3",dealPrice3);
		//汇率说明
		List<Config> configs1 = configService.findByNameAndStatus(Constant.CONFIG_EXCHANGE, 1);
		List<Config> configs2 = configService.findByNameAndStatus(Constant.CONFIG_DEDUCTION, 1);
		List<Config> configs3 = configService.findByNameAndStatus(Constant.CONFIG_STAR, 1);
		request.setAttribute("configs1",configs1);
		request.setAttribute("configs2",configs2);
		request.setAttribute("configs3",configs3);
		//用户当前积分
		User one = userService.getOne(user.getPkUserId());
		request.getSession(false).setAttribute("user",one);
		//商品信息
		request.setAttribute("goods",goods);
		//库存
		List<OrderDetail> sellList = orderService.findByGoodsId(goodsId);
		request.setAttribute("haveNum",goods.getNum() - sellList.size());
		return "exchange-pay";
	}

	/**
	 * 工分/积分兑换成赠分
	 * @param type 1 积分 2 工分
	 **/
	@ResponseBody
	@RequestMapping("/appExchangeBonus")
	public String appExchangeBonus(HttpServletRequest request, HttpServletResponse response, String type, BigDecimal val) {
		User user = (User) request.getSession(false).getAttribute("user");
		User one = userService.getOne(user.getPkUserId());
		List<String> ids = new ArrayList<>();
		ids.add(user.getPkUserId());
		if("1".equals(type)){
			//是否超出现有分值
			List<Config> configIntre = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1积分可兑换");
			BigDecimal trans = new BigDecimal(configIntre.get(0).getContent());
			//分数转化
			BigDecimal point = val.divide(trans, 2, RoundingMode.HALF_UP);
			if(one.getIntegralPoints().compareTo(point)>-1){
				userService.addPoints(point.negate(),val,new BigDecimal(0),ids,"积分兑换赠分",user.getPkUserId());
			}else{
				return "interNotEnough";
			}
		}else if("2".equals(type)){
			//是否超出现有分值
			List<Config> configWork = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1工分可兑换");
			BigDecimal trans = new BigDecimal(configWork.get(0).getContent());
			//分数转化
			BigDecimal point = val.divide(trans, 2, RoundingMode.HALF_UP);
			if(one.getWorkPoints().compareTo(point)>-1){
				userService.addPoints(new BigDecimal(0),val,point.negate(),ids,"工分兑换赠分",user.getPkUserId());
			}else{
				return "workNotEnough";
			}
		}
		User one2 = userService.getOne(user.getPkUserId());
		request.getSession(false).setAttribute("user",one2);
		return "ok";
	}

	/**
	 * 商品兑换
	 **/
	@ResponseBody
	@RequestMapping("/exchange")
	public String exchange(HttpServletRequest request, HttpServletResponse response, String goodsId, String stationId,BigDecimal num) {
		try {
			//最终成交价
			String dealPrice = "";
			User user = (User) request.getSession(false).getAttribute("user");
			List<String> ids = new ArrayList<>();
			ids.add(user.getPkUserId());
			//查看库存
			Goods goods = goodsService.getOne(goodsId);
			List<OrderDetail> sellList = orderService.findByGoodsId(goodsId);
			if (sellList != null) {
				if (goods.getNum() - sellList.size() < num.intValue()) {
					return "noGoods";
				}
			} else {
				if (goods.getNum() == null || goods.getNum() == 0) {
					return "noGoods";
				}
			}
			//提成
			//查看商品提成
			BigDecimal dealPerc = null;
			BigDecimal goodsPercentage = sbgService.findByStationIdAndGoodsId(stationId, goodsId).getPercentage();
			if (goodsPercentage != null) {
				dealPerc = goodsPercentage;
			} else {
				BigDecimal busiPercentage = sbgService.findByStationIdAndBusinessIdAndGoodsIdIsNull(stationId, goods.getUserId()).getPercentage();
				if (busiPercentage != null) {
					dealPerc = busiPercentage;
				}
			}
			if(dealPerc!=null){
				dealPerc = dealPerc.divide(new BigDecimal(100),4, RoundingMode.HALF_UP);
			}
			List<String> busiIds = new ArrayList<>();
			List<String> managerIds = new ArrayList<>();
			//查找该商品的商户和所在驿站管理员
			busiIds.add(goods.getUserId());
			Station station = stationService.getOne(stationId);
			managerIds.add(station.getManager());

			//扣除分数
			if (goods.getType() == 1) {
				//实体商品
				if (user.getRole() == Constant.ROLE_VOLUNTEER_WORKER || user.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
					List<Config> config = configService.findByNameAndStatus(Constant.CONFIG_DEDUCTION, 1);
					//看是否设置了汇率
					if (config == null || config.size() == 0) {
						//只能按全额积分兑换
						BigDecimal decimal = user.getIntegralPoints().subtract(goods.getPriceIntegral());
						if (decimal.compareTo(new BigDecimal(0)) > -1) {
							//大于等于0
							dealPrice = goods.getPriceIntegral().toString() + "积分";
						} else {
							//全额扣分积分不足
							return "onlyIntreButNotEnough";
						}
					} else {
						//计算汇率
						//BigDecimal trans = new BigDecimal(1).divide(new BigDecimal(config.get(0).getContent()), 2, RoundingMode.HALF_UP);
						//工分兑换积分汇率修改为，抵扣1积分需要多少工分
						BigDecimal trans = new BigDecimal(config.get(0).getContent());
						BigDecimal canUse = null;
						if (goods.getPriceWork().contains("~")) {
							//按星级划分,按照当前用户等级可兑换最高分来兑换，有多少兑换多少
							String[] splitPri = goods.getPriceWork().split("~");
							//计算当前用户是几星义工
							Integer star = configService.getStarLvlByUserId(user.getPkUserId());
							//看可抵扣的工分和用户拥有的谁多
							canUse = new BigDecimal(splitPri[star - 1]).multiply(trans);
						} else {
							canUse = new BigDecimal(goods.getPriceWork()).multiply(trans);
						}
						if(user.getWorkPoints().compareTo(canUse)>-1){
							//按最高可抵扣的兑换
							dealPrice = goods.getPriceIntegral().subtract(canUse.divide(trans,2, RoundingMode.HALF_UP)).toString() + "积分" + "+" + canUse.toString() + "工分";
						}else{
							return "notEnough";
						}
						/*if (canUse.compareTo(user.getWorkPoints()) > -1) {
							//拥有的全兑换
							dealPrice = goods.getPriceIntegral().subtract(user.getWorkPoints().divide(trans,2, RoundingMode.HALF_UP)).toString() + "积分" + "+" + user.getWorkPoints().toString() + "工分";
						} else {
							//按最高可抵扣的兑换
							dealPrice = goods.getPriceIntegral().subtract(canUse.divide(trans,2, RoundingMode.HALF_UP)).toString() + "积分" + "+" + canUse.toString() + "工分";
						}*/
					}
				}else{
					//只能按全额积分兑换
					BigDecimal decimal = user.getIntegralPoints().subtract(goods.getPriceIntegral());
					if (decimal.compareTo(new BigDecimal(0)) > -1) {
						//大于等于0
						dealPrice = goods.getPriceIntegral().toString() + "积分";
					} else {
						//全额扣分积分不足
						return "onlyIntreButNotEnough";
					}
				}
			} else if (goods.getType() == 2) {
				//服务
				/*List<Config> configWork = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1工分可兑换");
				List<Config> configIntre = configService.findByNameAndStatusAndTerm(Constant.CONFIG_EXCHANGE, 1, "1积分可兑换");
				BigDecimal transWork = new BigDecimal(1).divide(new BigDecimal(configWork.get(0).getContent()), 2,RoundingMode.HALF_UP);
				BigDecimal transIntre = new BigDecimal(1).divide(new BigDecimal(configIntre.get(0).getContent()), 2, RoundingMode.HALF_UP);*/
				BigDecimal priceBonus = goods.getPriceBonus();
				//先扣赠分
				if (user.getBonusPoints().compareTo(priceBonus) > -1) {
					//直接扣除赠分
					//userService.addPoints(new BigDecimal(0), num.multiply(priceBonus).negate(), new BigDecimal(0), ids, "兑换"+goods.getName(), null);
					dealPrice = priceBonus.toString() + "赠分";
				} /*else {
					//赠分不足，扣工分，然后扣积分
					//扣除拥有的赠分后的服务价格
					BigDecimal subedBonPrice = priceBonus.subtract(user.getBonusPoints());
					if (user.getRole() == Constant.ROLE_VOLUNTEER_WORKER || user.getRole() == Constant.ROLE_OLD_MAN_AND_VOLUNTEER_WORKER) {
						if (configWork != null && configWork.size() > 0) {
							//判断用所有工分抵扣后价格是否满足用户条件
							//抵扣赠分后的服务价格需要多少工分兑换
							BigDecimal needWork = subedBonPrice.multiply(transWork);
							//扣除工分后的价格
							BigDecimal subedWorkPrice = subedBonPrice.subtract(needWork.divide(transWork,2, RoundingMode.HALF_UP));
							if (user.getWorkPoints().compareTo(needWork) > -1) {
								//直接扣除所需工分
								//userService.addPoints(new BigDecimal(0), num.multiply(user.getBonusPoints()).negate(), num.multiply(needWork).negate(), ids, "兑换"+goods.getName(), null);
								dealPrice = user.getBonusPoints().toString() + "赠分" + "+" + needWork + "工分";
								*//*if (dealPerc != null) {
									userService.addPoints(new BigDecimal(0), num.multiply(user.getBonusPoints().multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(needWork.multiply(new BigDecimal(1).subtract(dealPerc))), busiIds, "用户兑换"+goods.getName()+"提成", null);
									userService.addPoints(new BigDecimal(0), num.multiply(user.getBonusPoints().multiply(dealPerc)), num.multiply(needWork.multiply(dealPerc)), managerIds, "用户兑换"+goods.getName()+"提成", null);
								}*//*
							} else {
								//工分不足，计算拥有的工分全部抵扣后的赠分价格
								BigDecimal subedAllWorkPrice = subedBonPrice.subtract(user.getWorkPoints().divide(transWork,2, RoundingMode.HALF_UP));
								if (configIntre != null && configIntre.size() > 0) {
									//抵扣后的服务价格需要多少积分兑换
									BigDecimal needIntre = subedAllWorkPrice.multiply(transIntre);
									if (user.getIntegralPoints().compareTo(needIntre) > -1) {
										//直接扣除所需积分
										//userService.addPoints(num.multiply(needIntre).negate(), num.multiply(user.getBonusPoints()).negate(), num.multiply(user.getWorkPoints()).negate(), ids, "兑换"+goods.getName(), null);
										dealPrice = needIntre.toString() + "积分" + "+" + user.getBonusPoints().toString() + "赠分" + "+" + user.getWorkPoints() + "工分";
										*//*if (dealPerc != null) {
											userService.addPoints(num.multiply(needIntre.multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(user.getBonusPoints().multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(user.getWorkPoints().multiply(new BigDecimal(1).subtract(dealPerc))), busiIds, "用户兑换"+goods.getName()+"提成", null);
											userService.addPoints(num.multiply(needIntre.multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(user.getBonusPoints().multiply(dealPerc)), num.multiply(user.getWorkPoints().multiply(dealPerc)), managerIds, "用户兑换"+goods.getName()+"提成", null);
										}*//*
									} else {
										//分数不足
										return "notEnough";
									}
								} else {
									//未设置积分兑换汇率导致没有足够分数
									return "noSetIntreNotEnough";
								}
							}
						} else {
							//用积分兑换
							if (configIntre != null && configIntre.size() > 0) {
								//抵扣赠分后的服务价格需要多少积分兑换
								BigDecimal needIntre = subedBonPrice.multiply(transIntre);
								if(user.getIntegralPoints().compareTo(needIntre)>-1){
									//直接扣除所需积分
									//userService.addPoints(num.multiply(needIntre).negate(), num.multiply(user.getBonusPoints()).negate(), new BigDecimal(0), ids, "兑换"+goods.getName(), null);
									dealPrice = needIntre.toString() + "积分" + "+" + user.getBonusPoints() + "赠分";
									*//*if (dealPerc != null) {
										userService.addPoints(num.multiply(needIntre.multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(user.getBonusPoints().multiply(new BigDecimal(1).subtract(dealPerc))), new BigDecimal(0), busiIds, "用户兑换"+goods.getName()+"提成", null);
										userService.addPoints(num.multiply(needIntre.multiply(dealPerc)), num.multiply(user.getBonusPoints().multiply(dealPerc)), new BigDecimal(0), managerIds, "用户兑换"+goods.getName()+"提成", null);
									}*//*
								}else{
									return "notEnough";
								}
							}else{
								return "noSetIntreNotEnough";
							}
						}
					} else {
						//不包含义工角色，直接扣积分
						//抵扣赠分后的服务价格需要多少积分兑换
						BigDecimal needIntre = subedBonPrice.multiply(transIntre);
						//用户是否有这些积分
						if (user.getIntegralPoints().compareTo(needIntre) > -1) {
							//userService.addPoints(num.multiply(needIntre).negate(), num.multiply(user.getBonusPoints()).negate(), new BigDecimal(0), ids, "兑换"+goods.getName(), null);
							dealPrice = needIntre.toString() + "积分" + "+" + user.getBonusPoints().toString() + "赠分";
							*//*if (dealPerc != null) {
								userService.addPoints(num.multiply(needIntre.multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(user.getBonusPoints().multiply(new BigDecimal(1).subtract(dealPerc))), new BigDecimal(0), busiIds, "用户兑换"+goods.getName()+"提成", null);
								userService.addPoints(num.multiply(needIntre.multiply(new BigDecimal(1).subtract(dealPerc))), num.multiply(user.getBonusPoints().multiply(dealPerc)), new BigDecimal(0), managerIds, "用户兑换"+goods.getName()+"提成", null);
							}*//*
						} else {
							//积分不足
							return "notEnough";
						}
					}
				}*/
			}
			//开始扣费，先计算单个商品需要的分数，直接乘以数量
			if(dealPrice!=""){
				//积分
				String dealPrice1 = "0";
				//工分
				String dealPrice2 = "0";
				//赠分
				String dealPrice3 = "0";
				if(dealPrice.contains("+")){
					String[] split = dealPrice.split("\\+");
					for (String s : split) {
						if(s.contains("积分")){
							dealPrice1 = s.replace("积分","");
						}else if(s.contains("工分")){
							dealPrice2 = s.replace("工分","");
						}else if(s.contains("赠分")){
							dealPrice3 = s.replace("赠分","");
						}
					}
				}else{
					if(dealPrice.contains("积分")){
						dealPrice1 = dealPrice.replace("积分","");
					}else if(dealPrice.contains("工分")){
						dealPrice2 = dealPrice.replace("工分","");
					}else if(dealPrice.contains("赠分")){
						dealPrice3 = dealPrice.replace("赠分","");
					}
				}
				if(user.getIntegralPoints().compareTo(new BigDecimal(dealPrice1).multiply(num))>-1 && user.getWorkPoints().compareTo(new BigDecimal(dealPrice2).multiply(num))>-1 && user.getBonusPoints().compareTo(new BigDecimal(dealPrice3).multiply(num))>-1){
					userService.addPoints(new BigDecimal(dealPrice1).multiply(num).negate(), new BigDecimal(dealPrice3).multiply(num).negate(), new BigDecimal(dealPrice2).multiply(num).negate(), ids, "兑换"+goods.getName(), null);
					//提成
					if (dealPerc != null) {
						userService.addPoints(new BigDecimal(dealPrice1).multiply(num).multiply(new BigDecimal(1).subtract(dealPerc)), new BigDecimal(dealPrice3).multiply(num).multiply(new BigDecimal(1).subtract(dealPerc)), new BigDecimal(dealPrice2).multiply(num).multiply(new BigDecimal(1).subtract(dealPerc)), busiIds, "用户兑换"+goods.getName()+"提成", null);
						userService.addPoints(new BigDecimal(dealPrice1).multiply(num).multiply(dealPerc), new BigDecimal(dealPrice3).multiply(num).multiply(dealPerc), new BigDecimal(dealPrice2).multiply(num).multiply(dealPerc), managerIds, "用户兑换"+goods.getName()+"提成", null);
					}
					//记录订单
					goodsService.exchange(goodsId, user.getPkUserId(), dealPrice,stationId);
				}else {
					return "notEnough";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			//更新用户积分
			User user = (User) request.getSession(false).getAttribute("user");
			User one = userService.getOne(user.getPkUserId());
			request.getSession(false).setAttribute("user",one);
		}
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/save")
	public String save(HttpServletRequest request, HttpServletResponse response, Goods goods) {
		ResDTO resDTO = new ResDTO();
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			goods.setUserId(user.getPkUserId());
			goodsService.save(goods);
			resDTO.setMsg("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	@RequestMapping("/getGoodsRange")
	public String getGoodsRange(HttpServletRequest request, HttpServletResponse response, String businessId, String goodsId) {
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			Map<String, List<StationBusinessGoods>> map = sbgService.getGoodsRangeMap(user.getPkUserId(), goodsId);
			request.setAttribute("selectList", map.get("selectList"));
			request.setAttribute("notSelectList", map.get("notSelectList"));
			request.setAttribute("goodsId", goodsId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "views/goodsSeeRange";
	}

	@RequestMapping("/saveRange")
	@ResponseBody
	public String saveRange(HttpServletRequest request, HttpServletResponse response, String goodsId, String[] stationIds) {
		ResDTO resDTO = new ResDTO();
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			goodsService.saveRange(goodsId, stationIds, user.getPkUserId());
			resDTO.setMsg("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 设置商品提成
	 **/
	@RequestMapping(value = "/setPerc", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String setPerc(HttpServletRequest request, HttpServletResponse response, String perc, String[] goodsIdsAndStationIds) {
		ResDTO resDTO = new ResDTO();
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			String res = goodsService.setGoodsPerc(perc, goodsIdsAndStationIds);
			resDTO.setMsg(res);
		} catch (Exception e) {
			e.printStackTrace();
			resDTO.setMsg("error");
		}
		return JSON.toJSONString(resDTO);
	}

	/**
	 * 设置提成商品列表
	 **/
	@RequestMapping(value = "/getSetPercGoodsList", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getSetPercGoodsList(HttpServletRequest request, HttpServletResponse response, String page, String limit, StationBusinessGoods sbg, Goods goods) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "20";
			}
			sbg.setGoods(goods);
			sbg.setGoodsId("notNull");
			sbg.setIfApproval(1);
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

	/**
	 * 驿站下商品列表
	 **/
	@RequestMapping(value = "/appGetListByStationId", produces = "application/json; charset=utf-8")
	public String appGetListByStationId(HttpServletRequest request, HttpServletResponse response, String page, String limit, String stationId, String goodsType) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)) {
				page = "1";
				limit = "6";
			}
			if (!StringUtil.notNull(goodsType)) {
				goodsType = "1";
			}
			Goods goods = new Goods();
			goods.setType(Integer.parseInt(goodsType));
			StationBusinessGoods sbg = new StationBusinessGoods();
			sbg.setGoods(goods);
			sbg.setStationId(stationId);
			Page<StationBusinessGoods> list = sbgService.getList(sbg, page, limit);
			request.setAttribute("count", list.getTotalElements());
			request.setAttribute("curr", page);
			request.setAttribute("list", list.getContent());
			request.setAttribute("goodsType", goodsType);
			request.setAttribute("stationId", stationId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "exchange";
	}
}
