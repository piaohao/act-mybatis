package org.piaohao.act.db.mybatis;

import org.osgl.inject.Module;

/**
 * Configure BaseMapper injection
 */
public class MybatisModule extends Module {
    @Override
    protected void configure() {
        registerGenericTypedBeanLoader(BaseMapper.class, new MapperLoader());
    }
}
