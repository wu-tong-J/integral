package dao;

import entity.IntegralDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;

/**
 * @description
 **/
public interface IntegralDetailDao extends JpaRepository<IntegralDetail,String>,JpaSpecificationExecutor<IntegralDetail>,PagingAndSortingRepository<IntegralDetail,String> {
	@Query("select sum(points) from IntegralDetail where pointsType = ?2 and userId=?1 and points>0")
	BigDecimal countComeIn(String userId, Integer type);
}
