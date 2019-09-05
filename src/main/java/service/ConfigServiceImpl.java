package service;

import dao.ConfigDao;
import dao.UserDao;
import entity.Config;
import entity.User;
import entity.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author
 * @date 2019-04-23 14:06
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private ConfigDao configDao;
	@Autowired
	private UserDao userDao;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Config> getConfigList(Config config) {
		List<Config> list = configDao.findAll((Root<Config> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtil.notNull(config.getName())) {
				predicates.add(criteriaBuilder.equal(root.get("name"), config.getName()));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		});
		return list;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveOrUpdate(Config config) {
		if(!StringUtil.notNull(config.getPkCfgId()) || (StringUtil.notNull(config.getPkCfgId()) && !Constant.CONFIG_AUTH_OLD_MAN.equals(config.getName()))){
			//新增、修改（除了认证老人）需要校验合法性
			List<Config> cfg = configDao.findByName(config.getName());
			if (cfg != null && cfg.size() > 0) {
				//修改时，要先去除本身项
				if(StringUtil.notNull(config.getPkCfgId())){
					for (int i = 0; i < cfg.size(); i++) {
						if(cfg.get(i).getPkCfgId().equals(config.getPkCfgId())){
							cfg.remove(i);
							break;
						}
					}
				}
				if (Constant.CONFIG_STAR.equals(config.getName())) {
					//规则：星级小的分值也要小，反之亦然，且不能和已配置星级相同
					for (Config c : cfg) {
						if(c.getTerm().equals(config.getTerm())){
							return "该星级已配置";
						}else if ((Integer.parseInt(c.getTerm()) > Integer.parseInt(config.getTerm())) && (Integer.parseInt(c.getContent()) <= Integer.parseInt(config.getContent()))) {
							return "分值不合法，应小于较小星级的分值";
						} else if ((Integer.parseInt(c.getTerm()) < Integer.parseInt(config.getTerm())) && (Integer.parseInt(c.getContent()) >= Integer.parseInt(config.getContent()))) {
							return "分值不合法，应大于较小星级的分值";
						}
					}
				} else if (Constant.CONFIG_AUTO_POINTS.equals(config.getName())) {
					//规则：年龄范围不能有重叠
					String[] configSplit = config.getTerm().split("~");
					for (Config c : cfg) {
						String[] cSplit = c.getTerm().split("~");
						if(Integer.parseInt(cSplit[0])<=Integer.parseInt(configSplit[0]) && Integer.parseInt(cSplit[1])>=Integer.parseInt(configSplit[0])){
							return "年龄范围不合法，与已有规则出现重叠";
						}else if(Integer.parseInt(cSplit[0])<=Integer.parseInt(configSplit[1]) && Integer.parseInt(cSplit[1])>=Integer.parseInt(configSplit[1])){
							return "年龄范围不合法，与已有规则出现重叠";
						}
					}
				} else if(Constant.CONFIG_EXCHANGE.equals(config.getName())){
					if(cfg.size() >= 2){
						return "不能添加更多汇率";
					} else if(cfg.get(0).getTerm().contains("积分") && config.getTerm().contains("积分")){
						return "不能再次添加积分兑换汇率";
					} else if(cfg.get(0).getTerm().contains("工分") && config.getTerm().contains("工分")){
						return "工分兑换汇率已添加";
					}
				} else if(Constant.CONFIG_AUTH_OLD_MAN.equals(config.getName())){
					if(cfg.size() >= 1){
						return "不能添加更多规则";
					}
				}
			}
			if(!StringUtil.notNull(config.getPkCfgId())){
				config.setPkCfgId(UUIDUtil.getUUID());
			}
		}
		configDao.save(config);
		return "ok";
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void del(String pkCfgId) {
		configDao.delete(pkCfgId);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Config getOne(String pkCfgId) {
		return configDao.findByPkCfgId(pkCfgId);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateStatus(Integer status,String name) {
		configDao.updateStatus(status,name);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Config> findByNameAndStatus(String name, Integer status) {
		return configDao.findByNameAndStatus(name,status);
	}

	//计算当前用户是几星义工
	public Integer getStarLvlByUserId(String userId) {
		List<Config> configs = findByNameAndStatus(Constant.CONFIG_STAR, 1);
		if(configs!=null && configs.size()>0){
			User user = userDao.findByPkUserId(userId);
			//排序
			Collections.sort(configs, new Comparator<Config>() {
				@Override
				public int compare(Config o1, Config o2) {
					if (Integer.valueOf(o1.getTerm()) > Integer.valueOf(o2.getTerm())) {
						return 1;
					} else if (Integer.valueOf(o1.getTerm()) < Integer.valueOf(o2.getTerm())) {
						return -1;
					}
					return 0;
				}
			});
			Integer star = null;
			Config temp = null;
			for (Config config : configs) {
				//compareTo相当于减号
				if (new BigDecimal(config.getContent()).compareTo(user.getWorkPoints()) == -1) {
					temp = config;
					continue;
				} else if (new BigDecimal(config.getContent()).compareTo(user.getWorkPoints()) == 1) {
					if (temp == null) {
						//1星都没到
						star = 0;
					} else {
						star = Integer.valueOf(temp.getTerm());
					}
				} else {
					star = Integer.valueOf(config.getTerm());
				}

			}
			if (star == null) {
				star = Integer.valueOf(configs.get(configs.size() - 1).getTerm());
			}
			if(star==null || star==0){
				//按照1星处理
				return 1;
			}else{
				return star;
			}
		}else {
			//按照1星处理
			return 1;
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Config> findByNameAndStatusAndTerm(String name, Integer status, String term) {
		return configDao.findByNameAndStatusAndTerm(name,status,term);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Config> findByName(String name) {
		return configDao.findByName(name);
	}
}
