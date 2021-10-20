package com.smileparser.medijunctions.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardik on 30/11/17.
 */

public class ServiceBase<T> {

    Context context;
    Class<T> GenericClazz;
    private OrmliteDataBaseHelper helper = null;
    SQLiteDatabase db;


    public ServiceBase(Context context) {
        this.context = context;
    }

    public ServiceBase(Context context, Class<T> clazz) {
        this.context = context;
        this.GenericClazz = clazz;
    }

    public OrmliteDataBaseHelper getHelper() {
        if (helper == null)
            helper = (OrmliteDataBaseHelper) OpenHelperManager.getHelper(context,
                    OrmliteDataBaseHelper.class);
        return helper;
    }


    @SuppressWarnings("unchecked")
    public static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(first);
            Object value2 = field.get(second);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }


    @SuppressWarnings("unchecked")
    public List<T> GetAll(Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            List<T> list = dao.queryForAll();
            if (list != null && list.size() > 0)
                return list;
        } catch (Exception e) {
            
            Log.d("GetAll", e.getMessage());
        }
        return null;
    }





    public void Insert(T object, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            dao.create(object);

        } catch (Exception e) {
            Log.d("Insert", e.getMessage());
        }
    }


    public void InsertMany(List<T> list, Class<T> clazz) {

        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);

            for (T Entity : list) {
                dao.create(Entity);
            }
            Log.d("Many record inserted", "");
        } catch (Exception e) {
            Log.d("Exeption in InsertMany", e.getMessage());
        }
    }


    public QueryBuilder<T, Integer> GetQB() {
        Dao<T, Integer> dao;
        try {
            dao = getHelper().getDao(GenericClazz);
            return dao.queryBuilder();
        } catch (SQLException e) {
            Log.d("GetQb", e.getMessage());
        }
        return null;
    }


    public QueryBuilder<T, Integer> GetQB(Class<T> clazz) {
        Dao<T, Integer> dao;
        try {
            dao = getHelper().getDao(clazz);
            return dao.queryBuilder();
        } catch (SQLException e) {
            Log.d("GetQb(clazz)", e.getMessage());
        }
        return null;
    }

    public DeleteBuilder<T, Integer> GetDelB(Class<T> clazz) {
        Dao<T, Integer> dao;
        try {
            dao = getHelper().getDao(clazz);
            return dao.deleteBuilder();
        } catch (SQLException e) {
            
            Log.d("GetDelB(clazz)", e.getMessage());
        }
        return null;
    }

    public T FindById(Integer Id, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            return dao.queryForId(Id);
        } catch (Exception e) {
            Log.d("GetQb(id,clazz)", e.getMessage());
        }
        return null;
    }



    public void Update(T object, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            dao.update(object);
        } catch (Exception e) {
            
            Log.d("update(object,clazz)", e.getMessage());
        }
    }

    public void InsertOrUpdate(T object, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            dao.createOrUpdate(object);
        } catch (Exception e) {
            
            Log.d("InsertOrUpdate", e.getMessage());
        }
    }

    public void InsertOrUpdateMany(List<T> list, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            for (T Entity : list)
                dao.createOrUpdate(Entity);
        } catch (Exception e) {
            
            Log.d("UpdateMany(list)", e.getMessage());
        }
    }


    public void UpdateMany(List<T> list) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(GenericClazz);
            for (T Entity : list)
                dao.update(Entity);
        } catch (Exception e) {
            
            Log.d("UpdateMany(list)", e.getMessage());
        }
    }

    public void UpdateMany(List<T> list, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            for (T Entity : list) {
                dao.createOrUpdate(Entity);
            }
        } catch (Exception e) {
            
            Log.d("UpdateMany(list,clazz)", e.getMessage());
        }
    }

    public void Delete(T object, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            dao.delete(object);
        } catch (Exception e) {
            
            Log.d("Delete(object,clazz)", e.getMessage());
        }
    }


    public void DeleteMany(List<T> list, Class<T> clazz) {
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);
            for (T Entity : list)
                dao.delete(Entity);
        } catch (Exception e) {
            
            Log.d("Delete(object,clazz)", e.getMessage());
        }
    }





    /*

    filter contact query by account name

            select Id,Contact_Id,ACCOUNT_NAME,ACCOUNT_TYPE, ProfileImage, DisplayName,PhoneBookLable,Favourite  from(select c.*,s.AllDetails from contacts as c inner join contactdata as s on c.Contact_Id = s.Contact_Id)as tbljoin where ACCOUNT_NAME='rajen.ppl@gmail.com' group by Contact_Id order by DisplayName

     */

    public boolean isAnyRecordAvailable(Class<T> clazz) {

        long count = 0;
        boolean avaliable = false;
        try {
            Dao<T, Integer> dao = getHelper().getDao(clazz);

            count = dao.countOf();
            if (count > 0) {
                avaliable = true;
            }

        } catch (Exception e) {
            
            Log.d("isAnyRecordAvailable = ", e.getMessage());
        }
        return avaliable;
    }


    public void ResetTableData(Class<T> clazz)
    {
        db=getHelper().getWritableDatabase();
        getHelper().ResetTable(db,clazz);
    }

}
