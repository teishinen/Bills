package com.example.bills.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bills.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserDBHelper extends SQLiteOpenHelper {
   private static final String DB_NAME = "user.db";
   private static final String TABLE_NAME = "user_info";
   private static final int DB_VERSION = 2;
   private static UserDBHelper mHelper = null;
   private SQLiteDatabase mRDB = null;
   private SQLiteDatabase mWDB = null;

   private UserDBHelper(Context context) {
      super(context, DB_NAME, null, DB_VERSION);
   }

   //利用单例模式获取数据库帮助器的唯一示例
   public static UserDBHelper getInstance(Context context){
      if (mHelper == null){
         mHelper = new UserDBHelper(context);
      }
      return mHelper;
   }

   //打开数据库的读连接
   public SQLiteDatabase openReadLink(){
      if (mRDB == null || !mRDB.isOpen()){
         mRDB = mHelper.getReadableDatabase();
      }
      return mRDB;
   }

   //打开数据库的写连接
   public SQLiteDatabase openWriteLink(){
      if (mWDB == null || !mWDB.isOpen()){
         mWDB = mHelper.getWritableDatabase();
      }
      return mRDB;
   }

   //关闭数据库
   public void closeLink(){
      if (mRDB !=null && mRDB.isOpen()){
         mRDB.close();
         mRDB = null;
      }

      if (mWDB !=null && mWDB.isOpen()){
         mWDB.close();
         mWDB = null;
      }
   }
   //创建数据库，执行建表语句
   @Override
   public void onCreate(SQLiteDatabase db) {
      String sql = "CREATE TABLE IF NOT EXISTS " +TABLE_NAME+" (" +
              "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
              " name VARCHAR NOT NULL," +
              " age LONG NOT NULL," +
              " hight FLOAT NOT NULL," +
              " weight FLOAT NOT NULL," +
              " married INTEGERV NOT NULL);";
      db.execSQL(sql);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      String sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN phone VARCHAR;";
      db.execSQL(sql);
      sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN password VARCHAR;";
      db.execSQL(sql);
   }

   public long insert(User user){
      ContentValues values = new ContentValues();
      values.put("name", user.name);
      values.put("age", user.age);
      values.put("height", user.height);
      values.put("weight", user.weight);
      values.put("married", user.married);
      return mWDB.insert(TABLE_NAME, null, values);
   }

   public long deleteByName(String name){
      //删除所有
      //mWDB.delete(TABLE_NAME, "1=1", null);
      return mWDB.delete(TABLE_NAME, "name=?", new String[]{name});
   }

   public long update(User user){
      ContentValues values = new ContentValues();
      values.put("name", user.name);
      values.put("age", user.age);
      values.put("height", user.height);
      values.put("weight", user.weight);
      values.put("married", user.married);
      //return mWDB.update(TABLE_NAME, values, "name=?", new String[]{user.name});

      try{
         mWDB.beginTransaction();
         mWDB.insert(TABLE_NAME, null, values);
         //int i = 10 / 0;
         mWDB.insert(TABLE_NAME, null, values);
         mWDB.setTransactionSuccessful();
      }catch (Exception e){
            e.printStackTrace();
      }finally {
         mWDB.endTransaction();
      }

      return 1;
   }

   public List<User> queryAll(){
      List<User> list = new ArrayList<>();
      //执行记录查询动作，该语句返回结果集的游标
      Cursor cursor = mRDB.query(TABLE_NAME, null, null, null, null, null, null);

      while (cursor.moveToNext()){
         User user = new User();
         user.id = cursor.getInt(0);
         user.name = cursor.getString(1);
         user.age = cursor.getInt(2);
         user.height = cursor.getLong(3);
         user.weight = cursor.getFloat(4);
         //SQLite没有布尔型，用0表示false，用1表示true
         user.married = (cursor.getInt(5) == 0) ? false : true;
         list.add(user);
      }
      return list;
   }

   public List<User> queryByName(String name) {
      List<User> list = new ArrayList<>();
      //执行记录查询动作，该语句返回结果集的游标
      Cursor cursor = mRDB.query(TABLE_NAME, null, "name=?", new String[]{name}, null, null, null);

      while (cursor.moveToNext()) {
         User user = new User();
         user.id = cursor.getInt(0);
         user.name = cursor.getString(1);
         user.age = cursor.getInt(2);
         user.height = cursor.getLong(3);
         user.weight = cursor.getFloat(4);
         //SQLite没有布尔型，用0表示false，用1表示true
         user.married = (cursor.getInt(5) == 0) ? false : true;
         list.add(user);
      }
      return list;
   }
}