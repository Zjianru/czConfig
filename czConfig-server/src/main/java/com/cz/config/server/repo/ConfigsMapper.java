package com.cz.config.server.repo;

import com.cz.config.server.meta.Configs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * code desc
 *
 * @author Zjianru
 */
@Mapper
public interface ConfigsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Configs record);

    int insertSelective(Configs record);

    Configs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Configs record);

    int updateByPrimaryKey(Configs record);

    int updateBatch(List<Configs> list);

    int batchInsert(@Param("list") List<Configs> list);

    List<Configs> findByAppAndEnvAndNs(@Param("app") String app, @Param("env") String env, @Param("ns") String ns);

    List<Configs> findByAppAndEnvAndNsAndProperties(@Param("app") String app, @Param("env") String env, @Param("ns") String ns, @Param("properties") String properties);

    int updatePlaceholderByOther(@Param("updatedPlaceholder") String updatedPlaceholder, @Param("app") String app, @Param("env") String env, @Param("ns") String ns, @Param("properties") String properties);


}