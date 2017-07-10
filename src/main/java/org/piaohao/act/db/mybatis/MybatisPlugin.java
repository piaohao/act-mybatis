package org.piaohao.act.db.mybatis;

import act.app.App;
import act.db.DbPlugin;
import act.db.DbService;

import java.util.Map;

/**
 * Responsible for init Mybatis DB service
 */
public class MybatisPlugin extends DbPlugin {

    @Override
    public DbService initDbService(String id, App app, Map<String, String> conf) {
        return new MybatisService(id, app, conf);
    }

}
