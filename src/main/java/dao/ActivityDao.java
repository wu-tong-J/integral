package dao;

import entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @description
 **/
public interface ActivityDao extends JpaRepository<Activity,String>,JpaSpecificationExecutor<Activity>,PagingAndSortingRepository<Activity,String> {
	//修改活动状态
	@Modifying
	@Query("update Activity a set a.status=?1 where a.pkActId in (?2)")
	int updateStatus(Integer status,List<String> actId);
	//查询已经结束的活动
	@Query(value = "select * from activity where e_time < NOW() LIMIT 999",nativeQuery = true)
	List<Activity> getStopAct();
	//
	Activity findByPkActId(String actId);
	//
	@Query(value = "select count(1) from activity_feedback where act_id=?1",nativeQuery = true)
	int getCount(String actId);

	@Query(value = "select * from activity where status=1 order by publish_time limit 1",nativeQuery = true)
	Activity getOneAct();
}
