package com.jk.eis.commen.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig fieldDaoConfig;
    private final DaoConfig segmentDaoConfig;
    private final DaoConfig depositionDaoConfig;
    private final DaoConfig segTypeDaoConfig;
    private final DaoConfig userDaoConfig;

    private final FieldDao fieldDao;
    private final SegmentDao segmentDao;
    private final DepositionDao depositionDao;
    private final SegTypeDao segTypeDao;
    private final UserDao userDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        fieldDaoConfig = daoConfigMap.get(FieldDao.class).clone();
        fieldDaoConfig.initIdentityScope(type);

        segmentDaoConfig = daoConfigMap.get(SegmentDao.class).clone();
        segmentDaoConfig.initIdentityScope(type);

        depositionDaoConfig = daoConfigMap.get(DepositionDao.class).clone();
        depositionDaoConfig.initIdentityScope(type);

        segTypeDaoConfig = daoConfigMap.get(SegTypeDao.class).clone();
        segTypeDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        fieldDao = new FieldDao(fieldDaoConfig, this);
        segmentDao = new SegmentDao(segmentDaoConfig, this);
        depositionDao = new DepositionDao(depositionDaoConfig, this);
        segTypeDao = new SegTypeDao(segTypeDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);

        registerDao(Field.class, fieldDao);
        registerDao(Segment.class, segmentDao);
        registerDao(Deposition.class, depositionDao);
        registerDao(SegType.class, segTypeDao);
        registerDao(User.class, userDao);
    }
    
    public void clear() {
        fieldDaoConfig.getIdentityScope().clear();
        segmentDaoConfig.getIdentityScope().clear();
        depositionDaoConfig.getIdentityScope().clear();
        segTypeDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
    }

    public FieldDao getFieldDao() {
        return fieldDao;
    }

    public SegmentDao getSegmentDao() {
        return segmentDao;
    }

    public DepositionDao getDepositionDao() {
        return depositionDao;
    }

    public SegTypeDao getSegTypeDao() {
        return segTypeDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}