package service;

import entity.Config;

import java.util.List;

/**
 * @author
 * @time 2019-04-23 14:04
 **/
public interface ConfigService {
	//根据配置项名称查询已配置项列表
	List<Config> getConfigList(Config config);

	//保存/更新配置
	String saveOrUpdate(Config config);

	//删除（年龄赠分）
	void del(String pkCfgId);

	Config getOne(String pkCfgId);

	void updateStatus(Integer status,String name);

	List<Config> findByNameAndStatus(String name,Integer status);

	Integer getStarLvlByUserId(String userId);

	List<Config> findByNameAndStatusAndTerm(String name,Integer status,String term);

	List<Config> findByName(String name);
}
