package service;

import entity.Goods;
import entity.OrderDetail;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @time 2019-04-23 14:04
 **/
public interface OrderService {
	//评价
	void evaluation(String evaluation, BigDecimal star, String pkOrderId) throws Exception;

	int getCount(String goodsId);

	List<OrderDetail> findByGoodsId(String goodsId);

	Page<OrderDetail> getList(OrderDetail orderDetail, String pageNum, String pageSize);

	OrderDetail getOne(String orderId);

	List<OrderDetail> findByGoodsIdAndStationId(String goodsId,String stationId);

	void updateStatus(Integer status,String pkOrderId);
}
