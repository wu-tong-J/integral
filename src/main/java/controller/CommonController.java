package controller;

import com.alibaba.fastjson.JSON;
import dto.EditorResultDTO;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import service.*;
import utils.ConfigUtil;
import utils.QRCodeUtil;
import utils.StringUtil;
import utils.UUIDUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xh
 * @description 公共功能控制器
 * @date 2018-04-06 22:27
 **/
@Controller
@RequestMapping("/common")
public class CommonController {
	@Autowired
	private SbgService sbgService;
	@Autowired
	private StationService stationService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private GoodsService goodsService;

	/**
	 * 跳转富文本详情页面
	 **/
	@RequestMapping(value = "/toDetail")
	public String toDetail(HttpServletRequest request,String actId,String goodsId) {
		if(StringUtil.notNull(actId)){
			Activity activity = activityService.getOne(actId);
			request.setAttribute("detail",new String(activity.getContent()));
		}else if(StringUtil.notNull(goodsId)){
			Goods goods = goodsService.getOne(goodsId);
			request.setAttribute("detail",new String(goods.getContent()));
		}
		return "views/detail";
	}

	/**
	 * 跳转上传页面
	 * @param inputId 打开上传页的input的id
	 * @param hiddenId 用于存储图片url的id
	 **/
	@RequestMapping(value = "/appToUpload")
	public String appToUpload(HttpServletRequest request,String inputId,String hiddenId) {
		request.setAttribute("inputId",inputId);
		request.setAttribute("hiddenId",hiddenId);
		return "upload";
	}

	/**
	 * editor富文本图片上传
	 * @return  图片url，使用相对路径
	 */
	@RequestMapping(value = "/editorFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String editorFileUpload(List<MultipartFile> list, HttpServletRequest request) {
		System.out.println("上传图片文件个数："+list.size());
		EditorResultDTO editorResultDTO = new EditorResultDTO();
		try {
			//判断是否存在上传目录
			//String uploadRealPath = request.getServletContext().getRealPath("/images/editorUpload");
			String uploadRealPath = ConfigUtil.picPath + "/editorUpload";
			if (!(new File(uploadRealPath).exists())) {
				new File(uploadRealPath).mkdir();
			}
			List<String> urlsList = new ArrayList<>();
			for (MultipartFile uploadFile : list) {
				if (uploadFile != null){
					if (uploadFile.getSize()>20*1024*1024){
						//文件过大，超过20M
						editorResultDTO.setErrno(1);
					}
					File dest = new File(uploadRealPath + "/"+ UUIDUtil.getUUID() + uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."),uploadFile.getOriginalFilename().length()));
					uploadFile.transferTo(dest);
					String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
					if (StringUtil.notNull(dest.getPath())) {
						//返回的图片路径不能使用图片真实本地路径，要使用服务器路径
						//urlsList.add(basePath + "/images/editorUpload/" + dest.getName());
						urlsList.add("/uploadImages/editorUpload/" + dest.getName());
						editorResultDTO.setErrno(0);
					}
				}
			}
			String[] urlsArr = new String[urlsList.size()];
			editorResultDTO.setData(urlsList.toArray(urlsArr));
		} catch (Exception e) {
			editorResultDTO.setErrno(2);
			e.printStackTrace();
		}
		return JSON.toJSONString(editorResultDTO);
	}

	/**
	 * @Description:上传图片
	 * @return:图片访问地址，使用相对路径
	 **/
	@RequestMapping(value = "/toUploadPic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String toUpload(MultipartFile uploadFile, HttpServletRequest request, HttpServletResponse response){
		String result = "";
		try {
			//判断是否存在上传目录
			String uploadRealPath = ConfigUtil.picPath + "/customerPic";
			if (!(new File(uploadRealPath).exists())) {
				new File(uploadRealPath).mkdir();
			}
			if (uploadFile != null){
				if (uploadFile.getSize()>20*1024*1024){
					//文件过大，超过20M
					result = "outOfSize";
				}
				//uploadFile.getOriginalFilename();可以获取上传文件的真实带有后缀的名称
				File dest = new File(uploadRealPath + "/"+ UUIDUtil.getUUID() + uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."),uploadFile.getOriginalFilename().length()));
				uploadFile.transferTo(dest);
				String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
				if (StringUtil.notNull(dest.getPath())) {
					//返回的图片路径不能使用图片真实本地路径，要使用服务器路径
					//result = basePath + "/images/customerPic/" + dest.getName();
					result = "/uploadImages/customerPic/" + dest.getName();
				}
			}else {
				result = "noFile";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(result);
	}

	/**
	 * @Description:layui按钮上传图片
	 * @return:图片访问地址，使用相对路径
	 **/
	@RequestMapping(value = "/layuiUploadPic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String layuiUploadPic(MultipartFile file, HttpServletRequest request, HttpServletResponse response){
		Map<String, String> map = new HashMap<>();
		try {
			//判断是否存在上传目录
			//String uploadRealPath = request.getServletContext().getRealPath("/images/customerPic");
			String uploadRealPath = ConfigUtil.picPath + "/customerPic";
			if (!(new File(uploadRealPath).exists())) {
				new File(uploadRealPath).mkdir();
			}
			if (file != null){
				if (file.getSize()>50*1024*1024){
					map.put("msg","文件过大，不能超过50M");
				}
				//uploadFile.getOriginalFilename();可以获取上传文件的真实带有后缀的名称
				File dest = new File(uploadRealPath + "/"+ UUIDUtil.getUUID() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."),file.getOriginalFilename().length()));
				file.transferTo(dest);
				String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
				if (StringUtil.notNull(dest.getPath())) {
					//返回的图片路径不能使用图片真实本地路径，要使用服务器路径
					//result = basePath + "/images/customerPic/" + dest.getName();
					map.put("src","/uploadImages/customerPic/" + dest.getName());
				}
			}else {
				map.put("msg","文件为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(map);
	}

	/**
	 * @Description:异步加载二维码图片
	 **/
	@RequestMapping("/getQRImage")
	@ResponseBody
	public void getQRImage(HttpServletRequest request, HttpServletResponse response,String text) {
		try {
			//设置浏览器不缓存本页
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			response.setContentType("image/jpg");
			BufferedImage image = QRCodeUtil.createImage(text, "", false);
			ServletOutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPG", out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/appToMain")
	public String appToMain(HttpServletRequest request, HttpServletResponse response) {
		//驿站推荐，按驿站下商品数量倒序取部分
		List<Station> stationList = stationService.findByIfRecommend(1);
		for (Station station : stationList) {
			List<StationBusinessGoods> list = sbgService.findGoodsByStationId(station.getPkStationId());
			station.setGoodsNum(list.size());
		}
		request.setAttribute("stationList",stationList);

		//取一个商品
		String goodsId = "";
		String stationId = "";
		for (Station station : stationList) {
			goodsId = sbgService.getOneGoodsByStationId(station.getPkStationId());
			if(StringUtil.notNull(goodsId)){
				stationId = station.getPkStationId();
				break;
			}
		}
		if(StringUtil.notNull(goodsId)){
			Goods goods = goodsService.getOne(goodsId);
			request.setAttribute("goods",goods);
			request.setAttribute("goodsStationId",stationId);
			if(goods!=null){
				List<OrderDetail> sellList = orderService.findByGoodsIdAndStationId(goods.getPkGoodsId(), stationId);
				request.setAttribute("haveGoodsNum",goods.getNum()-sellList.size());
			}
		}

		//活动推荐，按报名人数倒序取部分活动
		Activity act = activityService.getOneAct();
		if(act!=null){
			request.setAttribute("act",act);
			List<ActivityFeedback> actList = activityService.findByActId(act.getPkActId());
			if(act.getNum()!=null){
				request.setAttribute("haveActNum",act.getNum()-actList.size());
			}
		}
		request.getSession().setAttribute("mean", 1);
		return "index";
	}

	//跳转扫码页面
	@RequestMapping("/appToScan")
	public String appToScan(HttpServletRequest request, HttpServletResponse response,String parm1,String parm2,String actId,String type) {
		request.setAttribute("parm1",parm1);
		request.setAttribute("parm2",parm2);
		request.setAttribute("actId",actId);
		request.setAttribute("type",type);
		return "barcode";
	}
}
