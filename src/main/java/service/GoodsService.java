package service;

import entity.Goods;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author
 * @time 2019-04-23 14:04
 **/
public interface GoodsService {
	/**
	 * 商品上架下架
	 **/
	void updateStatus(Integer status,List<String> pkGoodsIds);

	Page<Goods> getGoodsList(Goods goods, String pageNum, String pageSize);

	void save(Goods goods);

	void saveRange(String goodsId,String[] stationIds,String busId);

	String setGoodsPerc(String perc, String[] goodsIdsAndStationIds);

	Goods getOne(String goodsId);

	void exchange(String goodsIds,String userId,String dealPrice,String stationId);
}
