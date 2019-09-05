package dao;

import entity.ActivityFeedback;
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
public interface ActivityFeedbackDao extends JpaRepository<ActivityFeedback,String>,JpaSpecificationExecutor<ActivityFeedback>,PagingAndSortingRepository<ActivityFeedback,String> {
	//评价
	@Modifying
	@Query("update ActivityFeedback af set af.feedback=?1,af.star=?2 where af.actId=?3 and userId=?4")
	int updateFeedback(String feedback,BigDecimal star,String actId,String userId);
	//检查是否报名签到了活动
	@Query(value = "select * from activity_feedback where act_id=?1 and user_id=?2 and register is not null and sign_in is not null",nativeQuery = true)
	ActivityFeedback checkIfJoin(String actId,String userId);
	//查看是否报名
	ActivityFeedback findByActIdAndUserId(String actId,String userId);

	@Query(value = "select sum(star) from activity_feedback where act_id=?1 and star is not null",nativeQuery = true)
	BigDecimal countAllStar(String actId);

	List<ActivityFeedback> findByActId(String actId);
}
