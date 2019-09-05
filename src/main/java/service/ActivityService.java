package service;

import entity.Activity;
import entity.ActivityFeedback;
import entity.Sign;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @time 2019-04-23 14:04
 **/
public interface ActivityService {
	//展示所有活动列表
	Page<Activity> getActivityList(Activity activity, String pageNum, String pageSize);
	//参加活动
	ActivityFeedback join(String actId,String userId,String register);
	//签到
	String signIn(String actId, String userId, String signInId);
	//评价
	void evaluate(String feedback, BigDecimal star, String actId,String userId);
	//发布活动
	Activity publishAct(Activity activity);
	//自动结束活动
	void autoStopAct();

	//修改活动状态
	void updateStatus(Integer status, List<String> actId);

	Activity save(Activity activity);

	Activity getOne(String actId);

	Integer getCount(String actId);

	List<Sign> getSignByUserId(String actId,String userId,Integer type);

	//评论列表
	Page<ActivityFeedback> getFeedBackList(ActivityFeedback af, String pageNum, String pageSize);

	BigDecimal countAllStar(String actId);

	ActivityFeedback getUserFeedback(String actId,String userId);

	Activity getOneAct();

	List<ActivityFeedback> findByActId(String actId);

	void saveFeedBack(ActivityFeedback af);

	Sign findByPkSignId(String signId);

	void saveSign(Sign sign);
}
