package org.piaohao.act.db.mybatis;

import act.app.App;
import act.app.DbServiceManager;
import act.app.event.AppEventId;
import act.db.DbService;
import act.db.EntityClassRepository;
import act.util.AnnotatedClassFinder;
import act.util.SubClassFinder;
import org.osgl.$;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Find classes with annotation `Table`
 */
@Singleton
public class MybatisClassFinder {

    private static final Logger LOGGER = LogManager.get(MybatisClassFinder.class);

    private final EntityClassRepository repo;
    private final App app;

    @Inject
    public MybatisClassFinder(EntityClassRepository repo, App app) {
        this.repo = $.notNull(repo);
        this.app = $.notNull(app);
    }

    @AnnotatedClassFinder(Table.class)
    public void foundEntity(Class<?> modelClass) {
        repo.registerModelClass(modelClass);
    }

    @AnnotatedClassFinder(Entity.class)
    public void foundEntity2(Class<?> modelClass) {
        repo.registerModelClass(modelClass);
    }

    @SubClassFinder(value = BaseMapper.class, noAbstract = false, callOn = AppEventId.PRE_START)
    public void foundMapper(Class<? extends BaseMapper> mapperClass) {
        DbServiceManager dbServiceManager = app.dbServiceManager();
        DbService dbService = dbServiceManager.dbService("mybatis");
        ((MybatisService) dbService).prepareMapperClass(mapperClass);
    }

}
