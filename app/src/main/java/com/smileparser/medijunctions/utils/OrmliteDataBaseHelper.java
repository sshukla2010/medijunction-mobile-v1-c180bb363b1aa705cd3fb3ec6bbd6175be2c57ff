package com.smileparser.medijunctions.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.Allergy;
import com.smileparser.medijunctions.bean.DeleteSync;
import com.smileparser.medijunctions.bean.HealthCondition;
import com.smileparser.medijunctions.bean.RegisterPatient;


/**
 * Created by hardik on 30/11/17.
 */

public class OrmliteDataBaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "medijunction.db";

    public static final int DATABASE_VERSION = 2;

    public OrmliteDataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try
        {
            //TableUtils.createTableIfNotExists(connectionSource, ContactDataTest.class);
            TableUtils.createTableIfNotExists(connectionSource, RegisterPatient.class);
            TableUtils.createTableIfNotExists(connectionSource, Allergy.class);
            TableUtils.createTableIfNotExists(connectionSource, HealthCondition.class);
            TableUtils.createTableIfNotExists(connectionSource, DeleteSync.class);

        }
        catch (Exception e)
        {
            if(Global.IsNotNull(e))
                //Global.appendLog(e.getMessage());

                Log.e(OrmliteDataBaseHelper.class.getName(), "Unable to create datbases", e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int i, int i1) {

        try
        {
            if(connectionSource!=null)
            {

                TableUtils.dropTable(connectionSource, RegisterPatient.class,true);
                TableUtils.dropTable(connectionSource, Allergy.class,true);
                TableUtils.dropTable(connectionSource, HealthCondition.class,true);
                TableUtils.dropTable(connectionSource, DeleteSync.class,true);
                if(db!=null)
                {
                    onCreate(db, connectionSource);
                }
            }
        }
        catch(Exception e)
        {
            // Global.appendLog(e.getMessage());
            Log.e(OrmliteDataBaseHelper.class.getName(), "Unable to reset user data", e);
        }
    }

    public void ResetTable(SQLiteDatabase db,Class clazz)
    {
        try
        {
            ConnectionSource connectionSource=getConnectionSource();
            if(connectionSource!=null)
            {

                 TableUtils.dropTable(connectionSource, clazz,true);
                if(db!=null)
                {
                    onCreate(db, connectionSource);
                }
            }
        }
        catch(Exception e)
        {
           // Global.appendLog(e.getMessage());
            Log.e(OrmliteDataBaseHelper.class.getName(), "Unable to reset user data", e);
        }
    }

    public void ClearData(Integer id, SQLiteDatabase db)
    {
        try
        {
            ConnectionSource connectionSource = getConnectionSource();
            if (connectionSource != null)
            {
                if (db != null)
                {
                    onCreate(db, connectionSource);
                }
            }
        }
        catch(Exception e)
        {
            Log.e(OrmliteDataBaseHelper.class.getName(), "Unable to reset user data", e);
        }
    }


}
