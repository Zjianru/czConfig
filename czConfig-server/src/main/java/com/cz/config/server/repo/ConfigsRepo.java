package com.cz.config.server.repo;

import com.cz.config.server.meta.Configs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * code desc
 *
 * @author Zjianru
 */
@Repository
public interface ConfigsRepo extends JpaRepository<Configs, Long> {

    @Query("select c from Configs c where c.app = ?1 and c.env = ?2 and c.ns = ?3")
    List<Configs> findByAppAndEnvAndNs(String app, String env, String ns);

    @Query("select c from Configs c where c.app = ?1 and c.env = ?2 and c.ns = ?3 and c.properties = ?4")
    List<Configs> findByAppAndEnvAndNsAndProperties(String app, String env, String ns, String properties);

    @Transactional
    @Modifying
    @Query("update Configs c set c.placeholder = ?1 where c.app = ?2 and c.env = ?3 and c.ns = ?4 and c.properties = ?5")
    int updateConfigsPlaceholder(String placeholder, String app, String env, String ns, String properties);
}