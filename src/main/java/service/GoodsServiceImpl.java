package service;

import dao.GoodsDao;
import dao.OrderDao;
import dao.StationBusinessGoodsDao;
import entity.Goods;
import entity.OrderDetail;
import entity.StationBusinessGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utils.StringUtil;
import utils.UUIDUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 * @date 2019-04-23 14:06
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements GoodsService{

	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private StationBusinessGoodsDao sbgDao;
	@Autowired
	private OrderDao orderDao;

	/**
	 * 审批商品上架下架
	 */
	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void updateStatus(Integer status,List<String> pkGoodsIds) {
		goodsDao.updateStatus(status,pkGoodsIds);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void save(Goods goods) {
		if(!StringUtil.notNull(goods.getPkGoodsId())){
			goods.setPkGoodsId(UUIDUtil.getUUID());
		}
		goodsDao.save(goods);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void saveRange(String goodsId, String[] stationIds,String busId) {
		//删除原有可见范围
		sbgDao.deleteByGoodsId(goodsId);
		//重新插入新的可见范围
		List<StationBusinessGoods> list = new ArrayList<>();
		if(stationIds!=null && stationIds.length>0){
			for (String stationId : stationIds) {
				StationBusinessGoods sbg = new StationBusinessGoods();
				sbg.setPkSbgId(UUIDUtil.getUUID());
				sbg.setBusinessId(busId);
				sbg.setGoodsId(goodsId);
				sbg.setStationId(stationId);
				//商品无需审批
				sbg.setIfApproval(1);
				list.add(sbg);
			}
			sbgDao.save(list);
		}
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public String setGoodsPerc(String perc,  String[] goodsIdsAndStationIds) {
		//检查商品的商户是否已经设置了提成
		String res = "";
		String readedStationId = "";
		for (String gs : goodsIdsAndStationIds) {
			String stationId = gs.split("-")[1];
			if(readedStationId.indexOf(stationId)==-1){
				readedStationId = readedStationId + stationId + ";";
				List<String> goodsList = new ArrayList<>();
				for (String gss : goodsIdsAndStationIds) {
					String[] split = gss.split("-");
					if(split[1].equals(stationId)){
						goodsList.add(split[0]);
					}
				}
				List<String> businessIds = sbgDao.findByGoodsIdInAndStationId(goodsList, stationId);
				List<StationBusinessGoods> list = sbgDao.checkBusiPerc(stationId, businessIds);
				if(list!=null && list.size()>0){
					for (StationBusinessGoods sbg : list) {
						res = res + sbg.getUser().getUsername()==null?sbg.getUser().getPhone():sbg.getUser().getUsername()+";";
					}
				}else{
					sbgDao.updateGoodsPerc(new BigDecimal(perc),stationId,goodsList);
				}
			}
		}
		if(res==""){
			res = "ok";
		}
		return res;
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Goods getOne(String goodsId) {
		return goodsDao.findByPkGoodsId(goodsId);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void exchange(String goodsId, String userId,String dealPrice,String stationId) {
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setPkOrderId(UUIDUtil.getUUID());
		orderDetail.setStatus(0);
		orderDetail.setGoodsId(goodsId);
		orderDetail.setCreateTime(LocalDateTime.now().withNano(0));
		orderDetail.setUserId(userId);
		orderDetail.setPrice(dealPrice);
		orderDetail.setGoodsName(goodsDao.findByPkGoodsId(goodsId).getName());
		orderDetail.setStationId(stationId);
		orderDao.save(orderDetail);
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Page<Goods> getGoodsList(Goods goods, String pageNum, String pageSize) {
		Pageable pageable = new PageRequest(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize),null);
		Page<Goods> page = goodsDao.findAll((Root<Goods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(StringUtil.notNull(goods.getUserId())){
				predicates.add(criteriaBuilder.equal(root.get("userId"), goods.getUserId()));
			}
			if(goods.getStatus()!=null){
				predicates.add(criteriaBuilder.equal(root.get("status"), goods.getStatus()));
			}
			if(goods.getType()!=null){
				predicates.add(criteriaBuilder.equal(root.get("type"), goods.getType()));
			}
			if(StringUtil.notNull(goods.getName())){
				predicates.add(criteriaBuilder.like(root.get("name"), "%"+goods.getName()+"%"));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		},pageable);
		return page;
	}
}
