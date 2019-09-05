package dao;

import entity.Config;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @description
 **/
public interface ConfigDao extends JpaRepository<Config,String>,JpaSpecificationExecutor<Config>,PagingAndSortingRepository<Config,String> {
	//根据名称查询
	List<Config> findByName(String name);

	List<Config> findAll(Specification<Config> spec);

	Config findByPkCfgId(String pkCfgId);

	@Modifying
	@Query("update Config c set c.status=?1 where c.name=?2")
	int updateStatus(Integer status, String name);

	List<Config> findByNameAndStatus(String name,Integer status);

	List<Config> findByNameAndStatusAndTerm(String name,Integer status,String term);
}
