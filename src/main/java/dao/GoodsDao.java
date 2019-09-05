package dao;

import entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description
 **/
public interface GoodsDao extends JpaRepository<Goods,String>,JpaSpecificationExecutor<Goods>,PagingAndSortingRepository<Goods,String> {
	//修改上下架状态
	@Modifying
	@Query("update Goods g set g.status=?1 where g.pkGoodsId in(?2)")
	int updateStatus(Integer status, List<String> pkGoodsIds);

	//修改商品提成
	@Modifying
	@Query("update Goods g set g.percentage=?1 where g.pkGoodsId=?2")
	int updatePercent(BigDecimal percent,String pkGoodsId);

	Page<Goods> findAll(Specification<Goods> spec, Pageable pageable);

	long count(Specification<Goods> spec);

	Goods findByPkGoodsId(String goodsId);
}
