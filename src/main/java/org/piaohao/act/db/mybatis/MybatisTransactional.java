package org.piaohao.act.db.mybatis;

import org.osgl.mvc.annotation.After;
import org.osgl.mvc.annotation.Before;
import org.osgl.mvc.annotation.Catch;
import org.osgl.mvc.annotation.Finally;

import javax.inject.Singleton;

/**
 * An injector support Transaction
 */
@Singleton
public class MybatisTransactional {

    @Before
    public void start() {
    }

    @After
    public void commit() {
    }

    @Catch(Exception.class)
    public void rollback() {
    }

    @Finally
    public void clear() {
    }

}
