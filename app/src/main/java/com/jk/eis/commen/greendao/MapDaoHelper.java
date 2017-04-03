package com.jk.eis.commen.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jk.eis.commen.greendao.DaoMaster.DevOpenHelper;
import com.jk.eis.commen.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapDaoHelper {

    public DaoMaster getMaster() {
        return master;
    }

    private SQLiteDatabase db;
    private DaoMaster master;
    private DaoSession session;
    private DevOpenHelper helper;
    private FieldDao mFieldDao;
    private SegmentDao mSegmentDao;
    private SegTypeDao mSegTypeDao;
    private DepositionDao mDepositionDao;
    private UserDao mUserDao;

    private static MapDaoHelper instance;

    private MapDaoHelper(Context context) {
        init(context);
    }

    public static MapDaoHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (MapDaoHelper.class) {
                if (instance == null) {
                    instance = new MapDaoHelper(context);
                }
            }
        }
        return instance;
    }

    //测试数据
    public void initdata() {

        SegType st1 = new SegType(1l);
        st1.setTypeNum(Constants.FIELDTYPE_TYPE1);
        st1.setDescription("重要类型分段场地");
        SegType st2 = new SegType(2l);
        st2.setTypeNum(Constants.FIELDTYPE_TYPE2);
        st2.setDescription("一般类型分段场地");
        SegType st3 = new SegType(3l);
        st3.setTypeNum(Constants.FIELDTYPE_TYPE3);
        st3.setDescription("特殊类型分段场地");
        SegType st4 = new SegType(4l);
        st4.setTypeNum(Constants.FIELDTYPE_TYPE4);
        st4.setDescription("集装箱场地");

        Segment s1 = new Segment(11l);
        s1.setName("重要部件一");
        s1.setDescription("用于装配a1部分");
        Segment s2 = new Segment(12l);
        s2.setName("重要部件二");
        s2.setDescription("用于装配a2部分");
        Segment s3 = new Segment(13l);
        s3.setName("重要部件三");
        s3.setDescription("用于装配a3部分");
        Segment s4 = new Segment(14l);
        s4.setName("重要部件四");
        s4.setDescription("用于装配a4部分");

        Segment s5 = new Segment(21l);
        s5.setName("一般部件一");
        s5.setDescription("用于装配b1部分");
        Segment s6 = new Segment(22l);
        s6.setName("一般部件二");
        s6.setDescription("用于装配b2部分");
        Segment s7 = new Segment(23l);
        s7.setName("一般部件三");
        s7.setDescription("用于装配b3部分");

        Segment s8 = new Segment(31l);
        s8.setName("特殊部件一");
        s8.setDescription("用于装配c1部分");
        Segment s9 = new Segment(32l);
        s9.setName("特殊部件一");
        s9.setDescription("用于装配c2部分");
        Segment s10 = new Segment(33l);
        s10.setName("特殊部件一");
        s10.setDescription("用于装配c3部分");

        Segment s11 = new Segment(41l);
        s11.setName("集装箱一");
        s11.setDescription("装载d1部件");
        Segment s12 = new Segment(42l);
        s12.setName("集装箱二");
        s12.setDescription("装载d2部件");
        Segment s13 = new Segment(43l);
        s13.setName("集装箱三");
        s13.setDescription("装载d3部件");
        Segment s14 = new Segment(44l);
        s14.setName("集装箱四");
        s14.setDescription("装载d4部件");
        Segment s15 = new Segment(45l);
        s15.setName("集装箱五");
        s15.setDescription("装载d4部件");

        Field f1 = new Field(11l);
        f1.setName("场地1");
        Field f2 = new Field(12l);
        f2.setName("场地2");
        Field f3 = new Field(13l);
        f3.setName("场地3");

        Field f4 = new Field(21l);
        f4.setName("场地4");
        Field f5 = new Field(22l);
        f5.setName("场地5");
        Field f6 = new Field(23l);
        f6.setName("场地6");
        Field f7 = new Field(24l);
        f7.setName("场地7");

        Field f8 = new Field(31l);
        f8.setName("场地8");
        Field f9 = new Field(32l);
        f9.setName("场地9");

        Field f10 = new Field(41l);
        f10.setName("场地10");
        Field f11 = new Field(42l);
        f11.setName("场地11");
        Field f12 = new Field(43l);
        f12.setName("场地12");

        addSegType(st1);
        addSegType(st2);
        addSegType(st3);
        addSegType(st4);

        addSegment(s1);
        addSegment(s2);
        addSegment(s3);
        addSegment(s4);
        addSegment(s5);
        addSegment(s6);
        addSegment(s7);
        addSegment(s8);
        addSegment(s9);
        addSegment(s10);
        addSegment(s11);
        addSegment(s12);
        addSegment(s13);
        addSegment(s14);
        addSegment(s15);

        addField(f1);
        addField(f2);
        addField(f3);
        addField(f4);
        addField(f5);
        addField(f6);
        addField(f7);
        addField(f8);
        addField(f9);
        addField(f10);
        addField(f11);
        addField(f12);

        assignSegmentToField(s1, f1, 5, 20161112, 20161218);
        assignSegmentToField(s2, f1, 12, 20170112, 20170215);
        assignSegmentToField(s2, f1, 12, 20170312, 20170406);
        assignSegmentToField(s4, f2, 66, 2017217, 20160310);
        assignSegmentToField(s5, f5, 20, 20170304, 20170518);
        assignSegmentToField(s6, f5, 31, 20170104, 20170320);
        assignSegmentToField(s3, f6, 6, 20161212, 20161218);
        assignSegmentToField(s8, f9, 8, 20170104, 20180315);
        assignSegmentToField(s11, f10, 30, 20161204, 20170415);
        assignSegmentToField(s12, f10, 100, 20170504, 20170912);
        assignSegmentToField(s11, f10, 60, 20171204, 20180501);
    }

    /*********************************************************************************
     * 操作场地方法
     */

    /**
     * 得到所有场地
     *
     * @return
     */
    public List<Field> getFieldAll() {
        List<Field> fieldList = mFieldDao.queryBuilder().listLazy();
        return fieldList;
    }

    /**
     * 根据type查找场地
     *
     * @param id
     * @return
     */
    public Field getField(int id) {
        Field field = mFieldDao.queryBuilder().where(FieldDao.Properties.Id.eq(id)).unique();
        if (field != null) {
            return field;
        }
        return null;
    }

    public List<Field> getFieldByType(int type) {
        List<Field> fieldList = mFieldDao.queryBuilder().where(FieldDao.Properties.Type.eq(type)).listLazy();
        return fieldList;
    }

    public List<Field> getFieldListBySegId(int id) {
        List<Deposition> depositionList = mDepositionDao.queryBuilder().where(DepositionDao.Properties.Segment_id.eq(id)).list();
        HashSet<Long> fieldIds = new HashSet<Long>();
        List<Field> fieldList = new ArrayList<Field>();
        for (Deposition d :
                depositionList) {
            fieldIds.add(d.getField_id());
        }
        for (Long l :
                fieldIds) {
            Field field = getField(l.intValue());
            if (field != null) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    public int getFieldCount() {
        List<Field> fieldList = mFieldDao.queryBuilder().listLazy();
        return fieldList.size();
    }

    public void addField(Field field) {
        mFieldDao.insert(field);
    }

    public void deleteField(Field field) {
        Field f = mFieldDao.queryBuilder().where(FieldDao.Properties.Id.eq(field.getId())).unique();
        // 删除堆放信息
        List<Deposition> depositionList = mDepositionDao.queryBuilder().where(DepositionDao.Properties.Field_id.eq(field.getId())).list();
        for (Deposition d :
                depositionList) {
            mDepositionDao.delete(d);
        }
        mFieldDao.delete(f);
    }

    public void updateField(Field field) {
        mFieldDao.update(field);
    }

    /*********************************************************************************
     * 操作分段方法
     */

    public int getSegmentCount() {
        List<Segment> segmentList = mSegmentDao.queryBuilder().listLazy();
        return segmentList.size();
    }

    /**
     * 得到所有分段
     *
     * @return
     */
    public List<Segment> getSegmentAll() {
        List<Segment> segmentList = mSegmentDao.queryBuilder().listLazy();
        return segmentList;
    }

    /**
     * 根据type查找分段
     *
     * @param type
     * @return
     */
    public List<Segment> getSegmentByType(int type) {
        List<Segment> segmentList = mSegmentDao.queryBuilder().where(SegmentDao.Properties.Type.eq(type)).listLazy();
        return segmentList;
    }

    public Segment getSegmentById(int id) {
        Segment segment = mSegmentDao.queryBuilder().where(SegmentDao.Properties.Id.eq(id)).unique();
        return segment;
    }

    public void addSegment(Segment segment) {
        mSegmentDao.insert(segment);
    }

    public void deleteSegment(Segment segment) {
        Segment s = mSegmentDao.queryBuilder().where(SegmentDao.Properties.Id.eq(segment.getId())).unique();
        // 删除堆放信息
        List<Deposition> depositionList = mDepositionDao.queryBuilder().where(DepositionDao.Properties.Segment_id.eq(segment.getId())).list();
        for (Deposition d :
                depositionList) {
            mDepositionDao.delete(d);
        }
        mSegmentDao.delete(s);
    }

    public void updateSegment(Segment segment) {
        mSegmentDao.update(segment);
    }

    /*********************************************************************************
     * 操作场地类型方法
     */
    /**
     * 根据id得到类型
     */
    public SegType getSegType(int id) {
        SegType segType = mSegTypeDao.queryBuilder().where(SegTypeDao.Properties.Id.eq(id)).unique();
        return segType;
    }

    public List<SegType> getSegTypeAll() {
        List<SegType> segTypeList = mSegTypeDao.queryBuilder().listLazy();
        return segTypeList;
    }

    public void addSegType(SegType setType) {
        mSegTypeDao.insert(setType);
    }

    public void updateSegType(SegType setType) {
        mSegTypeDao.update(setType);
    }

    public void deleteSegType(SegType setType) {
        mSegTypeDao.delete(setType);
    }

    /*********************************************************************************
     * 操作用户方法
     *
     * @return
     */
    public List<User> getUserAll(User user) {
        List<User> userList = mUserDao.queryBuilder().listLazy();
        return userList;
    }

    public User getUserByUsername(String username) {
        User u = mUserDao.queryBuilder().where(UserDao.Properties.Username.eq(username)).unique();
        return u;
    }

    public void addUser(User user) {
        User u = getUserByUsername(user.getUsername());
        if (u == null) {
            mUserDao.insert(user);
        } else {
            user.setId(u.getId());
            mUserDao.update(user);
        }
    }

    public void deleteUser(User user) {
        mUserDao.delete(user);
    }

    public void updateUser(User user) {
        mUserDao.update(user);
    }

    /*********************************************************************************
     * 储存方法
     */
    /**
     * 根据场地获得
     */
    public List<Deposition> getDepositionByField(Field field) {
        List<Deposition> depositionList = mDepositionDao.queryBuilder().where(DepositionDao.Properties.Field_id.eq(field.getId())).orderAsc(DepositionDao.Properties.Begin_date).listLazy();
        return depositionList;
    }

    /**
     * 根据船舶分段获得
     */
    public List<Deposition> getDepositionBySegment(Segment segment) {
        List<Deposition> depositionList = mDepositionDao.queryBuilder().where(DepositionDao.Properties.Segment_id.eq(segment.getId())).orderAsc(DepositionDao.Properties.Begin_date).listLazy();
        return depositionList;
    }

    /**
     * 根据场地和船舶分段获得
     */
    public List<Deposition> getDepositionBySegAndField(Segment segment, Field field) {
        List<Deposition> depositionList = mDepositionDao.queryBuilder()
                .where(DepositionDao.Properties.Segment_id.eq(segment.getId()), DepositionDao.Properties.Field_id.eq(field.getId())).orderAsc(DepositionDao.Properties.Begin_date).listLazy();
        return depositionList;
    }


    /*********************************************************************************
     * 逻辑方法
     */

    /**
     * 得到分段种类
     */
    public HashSet<Integer> getSegmentTypes() {
        HashSet<Integer> types = new HashSet<Integer>();
        List<Segment> segmentList = mSegmentDao.queryBuilder().listLazy();
        for (Segment s : segmentList) {
            types.add(s.getType());
        }
        return types;
    }

    /**
     * 为指定场地分配船舶分段
     *
     * @param segment
     * @param field
     * @param count
     * @param beginDate
     * @param endDate
     */
    public void assignSegmentToField(Segment segment, Field field, Integer count, Integer beginDate, Integer endDate) {
        /**********************************************************
         * 时间节点控制交给逻辑层
         */
        if (field.getType() != segment.getType()) {
            return;
        }

        //插入deposition
        Deposition deposition = new Deposition();
        deposition.setSegment_id(segment.getId());
        deposition.setField_id(field.getId());
        deposition.setSeg_count(count);
        deposition.setBegin_date(beginDate);
        deposition.setEnd_date(endDate);
        mDepositionDao.insert(deposition);
    }


    public void queryAll() {
        List<Field> fieldList = mFieldDao.queryBuilder().list();
        List<Segment> segmentList = mSegmentDao.queryBuilder().list();
        List<Deposition> depositionList = mDepositionDao.queryBuilder().list();

        for (Field f : fieldList) {
            Log.i("jk", "所有的场地：" + f);
        }
        for (Segment s : segmentList) {
            Log.i("jk", "所有的部件：" + s);
        }
        for (Deposition d :
                depositionList) {
            Log.i("jk", "所有的储存记录: " + d);
        }
    }


    public void init(Context context) {
        helper = new DevOpenHelper(context, "eis.db", null); // 起一个数据库的名字
        db = helper.getWritableDatabase();
        master = new DaoMaster(db);
        session = master.newSession();
        mFieldDao = session.getFieldDao();
        mSegmentDao = session.getSegmentDao();
        mSegTypeDao = session.getSegTypeDao();
        mDepositionDao = session.getDepositionDao();
        mUserDao = session.getUserDao();
    }
}
