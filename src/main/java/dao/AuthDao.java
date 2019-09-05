package dao;

import entity.Activity;
import entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description
 **/
public interface AuthDao extends JpaRepository<Authentication,String>,JpaSpecificationExecutor<Authentication>,PagingAndSortingRepository<Authentication,String> {
	//根据用户id查询认证信息
	Authentication findByUserIdAndStatus(String userId,Integer status);

	//更新审核信息
	@Modifying
	@Query("update Authentication a set a.operUserId=?1 ,a.operTime=?2,a.remark=?3,a.status=?5 where a.pkAuthId=?4")
	int updateOper(String userId, LocalDateTime now, String remark,String pkAuthId,Integer status);

	List<Authentication> findByUserId(String userId);

	Authentication findByPkAuthId(String authId);
}
