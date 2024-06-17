package com.cz.config.server.repo;

import com.cz.config.server.meta.Locks;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * code desc
 *
 * @author Zjianru
 */
@Mapper
public interface LocksMapper {
    /**
     * delete by primary key
     *
     * @param id primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(Locks record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(Locks record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    Locks selectByPrimaryKey(Integer id);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(Locks record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(Locks record);

    int updateBatch(List<Locks> list);

    int batchInsert(@Param("list") List<Locks> list);

    @Select("select app from locks where id = 1 for update")
    String selectAppForUpdate();


}