package com.thespace.thespace.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class DataBaseCleaner implements InitializingBean {
    // 테이블 이름들을 저장할 List
    private final List<String> tables = new ArrayList<>();

    // 엔티티 매니저를 선언한다
    @PersistenceContext
    private EntityManager entityManager;

    // 테이블 이름을 조회한다
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void findDatabaseTableNames() {
        List<String> tableNames = entityManager.createNativeQuery("SHOW TABLES").getResultList();
            tables.addAll(tableNames);
    }

    // 테이블을 초기화한다
    private void truncate() {
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = %d", 0)).executeUpdate();
        for (String tableName : tables) {
            entityManager.createNativeQuery(String.format("TRUNCATE TABLE %s", tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = %d", 1)).executeUpdate();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        findDatabaseTableNames();
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }
}
