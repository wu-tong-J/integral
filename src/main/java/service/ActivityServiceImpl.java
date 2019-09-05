package service;

import dao.ActivityDao;
import dao.ActivityFeedbackDao;
import dao.SignDao;
import dao.UserDao;
import entity.Activity;
import entity.ActivityFeedback;
import entity.Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.List;

/**
 * @author
 * @date 2019-04-23 14:06
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ActivityServiceImpl implements ActivityService{

	@Autowired
	private ActivityDao activityDao;
	@Autowired
	private ActivityFeedbackDao activityFeedbackDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SignDao signDao;

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Page<Activity> getActivityList(Activity activity, String pageNum, String pageSize) {
		Pageable pageable = new PageRequest(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize),new Sort(Sort.Direction.DESC,"publishTime"));
		Page<Activity> page = activityDao.findAll((Root<Activity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (activity.getStatus() != null){
				predicates.add(criteriaBuilder.equal(root.get("status"), activity.getStatus()));
			}
			if (StringUtil.notNull(activity.getTitle())){
				predicates.add(criteriaBuilder.like(root.get("title"), "%"+activity.getTitle()+"%"));
			}
			if (StringUtil.notNull(activity.getSubTitle())){
				predicates.add(criteriaBuilder.like(root.get("subTitle"), "%"+activity.getSubTitle()+"%"));
			}
			if(StringUtil.notNull(activity.getUserId())){
				predicates.add(criteriaBuilder.equal(root.get("userId"), activity.getUserId()));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		},pageable);
		return page;
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Page<ActivityFeedback> getFeedBackList(ActivityFeedback af, String pageNum, String pageSize) {
		Pageable pageable = new PageRequest(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize),new Sort(Sort.Direction.DESC,"feedTime"));
		Page<ActivityFeedback> page = activityFeedbackDao.findAll((Root<ActivityFeedback> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtil.notNull(af.getActId())){
				predicates.add(criteriaBuilder.equal(root.get("actId"), af.getActId()));
			}
			if (StringUtil.notNull(af.getUserId())){
				predicates.add(criteriaBuilder.equal(root.get("userId"), af.getUserId()));
			}
			if(StringUtil.notNull(af.getFeedback())){
				if("isNotNull".equals(af.getFeedback())){
					predicates.add(criteriaBuilder.isNotNull(root.get("feedback")));
				}
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		},pageable);
		return page;
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public BigDecimal countAllStar(String actId) {
		return activityFeedbackDao.countAllStar(actId);
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public ActivityFeedback getUserFeedback(String actId, String userId) {
		return activityFeedbackDao.findByActIdAndUserId(actId,userId);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public ActivityFeedback join(String actId, String userId, String register) {
		ActivityFeedback af = new ActivityFeedback();
		af.setPkAfId(UUIDUtil.getUUID());
		af.setActId(actId);
		af.setUserId(userId);
		af.setRegister(register);
		ActivityFeedback activityFeedback = activityFeedbackDao.save(af);
		return activityFeedback;
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public String signIn(String actId, String userId, String signInId) {
		/*//先看是否报名
		ActivityFeedback check = activityFeedbackDao.findByActIdAndUserId(actId, userId);
		if(check!=null){
			//已报名
			activityFeedbackDao.updateSignIn(actId,userId,signInId);
		}else{
			return "noJoin";
		}*/
		return "ok";
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void evaluate(String feedback, BigDecimal star, String actId,String userId) {
		activityFeedbackDao.updateFeedback(feedback,star,actId,userId);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public Activity publishAct(Activity activity) {
		activity.setPkActId(UUIDUtil.getUUID());
		return activityDao.save(activity);
	}

	@Scheduled(cron = "0 1 0 * * ?")//每天00:01执行
	//@Scheduled(cron = "0 0/1 * * * ?")//每分钟执行
	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void autoStopAct() {
		List<Activity> list = activityDao.getStopAct();
		if(list!=null && list.size()>0){
			List<String> ids = new ArrayList<String>();
			for (Activity a : list) {
				ids.add(a.getPkActId());
				//给义工加工分
				List<String> userIds = new ArrayList<String>();
				if(StringUtil.notNull(a.getServiceProvider())){
					String[] split = a.getServiceProvider().split(";");
					for (String s : split) {
						userIds.add(s);
					}
				}
				if(a.getBonus()!=null && userIds.size()>0){
					userDao.updateIntegral(new BigDecimal(0),new BigDecimal(0),a.getBonus(),userIds);
				}
			}
			activityDao.updateStatus(2,ids);
		}
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void updateStatus(Integer status, List<String> actId) {
		activityDao.updateStatus(status,actId);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public Activity save(Activity activity) {
		if(!StringUtil.notNull(activity.getPkActId())){
			activity.setPkActId(UUIDUtil.getUUID());
		}
		if(activity.getPublishTime()==null){
			activity.setPublishTime(LocalDateTime.now().withNano(0));
		}
		return activityDao.save(activity);
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Activity getOne(String actId) {
		return activityDao.findByPkActId(actId);
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Integer getCount(String actId) {
		int count = activityDao.getCount(actId);
		return count;
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public List<Sign> getSignByUserId(String actId, String userId, Integer type) {
		return signDao.findByActIdAndUserIdAndType(actId,userId,type);
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Activity getOneAct() {
		return activityDao.getOneAct();
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public List<ActivityFeedback> findByActId(String actId) {
		return activityFeedbackDao.findByActId(actId);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void saveFeedBack(ActivityFeedback af) {
		if(!StringUtil.notNull(af.getPkAfId())){
			af.setPkAfId(UUIDUtil.getUUID());
		}
		activityFeedbackDao.save(af);
	}

	@Transactional(propagation= Propagation.SUPPORTS,readOnly=true)
	public Sign findByPkSignId(String signId) {
		return signDao.findByPkSignId(signId);
	}

	@Transactional(propagation= Propagation.REQUIRED,readOnly=false)
	public void saveSign(Sign sign) {
		if(!StringUtil.notNull(sign.getPkSignId())){
			sign.setPkSignId(UUIDUtil.getUUID());
		}
		signDao.save(sign);
	}
}
