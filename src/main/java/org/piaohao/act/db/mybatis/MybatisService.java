package org.piaohao.act.db.mybatis;

import act.Act;
import act.app.App;
import act.db.Dao;
import act.db.sql.DataSourceConfig;
import act.db.sql.SqlDbService;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.osgl.$;
import org.osgl.inject.Genie;
import org.osgl.util.E;

import javax.inject.Provider;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implement `act.db.DbService` using Mybatis
 */
public class MybatisService extends SqlDbService {
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private ConcurrentMap<Class, Object> mapperMap = new ConcurrentHashMap<>();

    public MybatisService(String dbId, App app, Map<String, String> config) {
        super(dbId, app, config);
    }

    @Override
    protected void dataSourceProvided(DataSource dataSource, DataSourceConfig dsConfig) {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Override
    protected DataSource createDataSource() {
        throw E.unsupport("External datasource solution must be provided. E.g. hikaricp");
    }

    @Override
    protected boolean supportDdl() {
        return false;
    }

    @Override
    public <DAO extends Dao> DAO defaultDao(Class<?> aClass) {
        throw E.unsupport("BeetlSql does not support DAO. Please use mapper instead");
    }

    @Override
    public <DAO extends Dao> DAO newDaoInstance(Class<DAO> aClass) {
        throw E.unsupport("BeetlSql does not support DAO. Please use mapper instead");
    }

    @Override
    public Class<? extends Annotation> entityAnnotationType() {
        return Table.class;
    }

    @Override
    protected void releaseResources() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("beetsql shutdown: %s", id());
        }
        super.releaseResources();
    }

    Object mapper(Class mapperClass) {
        return mapperMap.get(mapperClass);
    }

    @SuppressWarnings("unchecked")
    public void prepareMapperClass(Class<? extends BaseMapper> mapperClass) {
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();
        }
        BaseMapper mapperBean = null;
        try {
            MapperProxy mapperProxy = new MapperProxy(sqlSession, mapperClass, new ConcurrentHashMap<>());
            mapperBean = $.cast(Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass}, mapperProxy));
        } catch (Exception e) {
            return;
        }
        mapperMap.put(mapperClass, mapperBean);
        Genie genie = Act.getInstance(Genie.class);
        BaseMapper finalMapperBean = $.cast(mapperBean);
        genie.registerProvider(mapperClass, (Provider) () -> finalMapperBean);
    }

}
