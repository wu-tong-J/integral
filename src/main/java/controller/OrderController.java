package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dto.PageDTO;
import entity.Goods;
import entity.OrderDetail;
import entity.Station;
import entity.User;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.GoodsService;
import service.OrderService;
import service.StationService;
import utils.QRCodeUtil;
import utils.StringUtil;
import utils.VerifyCodeUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

/**
 * @author
 * @date 2019-04-23 14:00
 **/
@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private StationService stationService;

	/**
	 * 评价商品
	 **/
	@RequestMapping(value = "/eval",produces = "application/json; charset=utf-8")
	@ResponseBody
	public String eval(HttpServletRequest request, HttpServletResponse response,String orderId, String star,String eval) {
		String res = "";
		try {
			orderService.evaluation(eval,new BigDecimal(star),orderId);
			res = "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(res);
	}

	/**
	 * 商品评价
	 **/
	@RequestMapping(value = "/appGetListByGoodsId",produces = "application/json; charset=utf-8")
	@ResponseBody
	public String appGetListByGoodsId(HttpServletRequest request, HttpServletResponse response,String page, String limit,String goodsId) {
		PageDTO pageDTO = new PageDTO();
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)){
				page = "1";
				limit = "5";
			}
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setGoodsId(goodsId);
			orderDetail.setStatus(2);
			Page<OrderDetail> list = orderService.getList(orderDetail, page, limit);
			for (OrderDetail detail : list.getContent()) {
				detail.setContentString(new String(detail.getEvaluation()));
			}
			pageDTO.setCount(String.valueOf(list.getTotalElements()));
			pageDTO.setData(list.getContent());
			if(list.getTotalElements()==0){
				pageDTO.setMsg("无数据");
				pageDTO.setCode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(pageDTO, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue/*null值依旧参与序列化*/);
	}

	/**
	 * 我的兑换
	 **/
	@RequestMapping(value = "/appGetMyOrderList",produces = "application/json; charset=utf-8")
	public String appGetMyOrderList(HttpServletRequest request, HttpServletResponse response,String page, String limit) {
		try {
			if (!StringUtil.notNull(page) || !StringUtil.notNull(limit)){
				page = "1";
				limit = "5";
			}
			OrderDetail orderDetail = new OrderDetail();
			User user = (User) request.getSession(false).getAttribute("user");
			orderDetail.setUserId(user.getPkUserId());
			Page<OrderDetail> list = orderService.getList(orderDetail, page, limit);
			request.setAttribute("count",list.getTotalElements());
			request.setAttribute("curr",page);
			request.setAttribute("list",JSON.toJSON(list.getContent()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "exchange_centre";
	}

	/**
	 * 去评价
	 **/
	@RequestMapping(value = "/appToEval",produces = "application/json; charset=utf-8")
	public String appToEval(HttpServletRequest request, HttpServletResponse response,String orderId,String page) {
		OrderDetail orderDetail = orderService.getOne(orderId);
		request.setAttribute("star",orderDetail.getStar());
		if(orderDetail.getEvaluation()!=null){
			request.setAttribute("content",new String(orderDetail.getEvaluation()));
		}
		request.setAttribute("orderId",orderId);
		request.setAttribute("curr",page);
		request.setAttribute("goodsName",orderDetail.getGoodsName());
		return "ex-evaluate";
	}

	/**
	 * 去领取
	 **/
	@RequestMapping(value = "/appToGet",produces = "application/json; charset=utf-8")
	public String appToGet(HttpServletRequest request, HttpServletResponse response,String orderId,String page) {
		try {
			OrderDetail orderDetail = orderService.getOne(orderId);
			Goods goods = goodsService.getOne(orderDetail.getGoodsId());
			request.setAttribute("orderDetail",orderDetail);
			request.setAttribute("curr",page);
			if(goods.getGetType() == Constant.GET_TYPE_BUSINESS){
				request.setAttribute("getType","实体店");
				request.setAttribute("address",goods.getAddress());
			}else if(goods.getGetType() == Constant.GET_TYPE_STATION){
				request.setAttribute("getType","驿站");
				Station station = stationService.getOne(orderDetail.getStationId());
				request.setAttribute("address",station.getAddress());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ex-details";
	}
}
