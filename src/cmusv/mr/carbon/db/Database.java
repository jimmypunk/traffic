package cmusv.mr.carbon.db;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
	static final String DATABASE_NAME = "mytracks.db";
	private static final int DATABASE_VERSION = 20;
	class DatabaseHelper extends SQLiteOpenHelper{
		public DatabaseHelper(Context context) {
		      super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
	    public void onCreate(SQLiteDatabase db) {
	      db.execSQL(TrackPointsColumns.CREATE_TABLE);
	      db.execSQL(TracksColumns.CREATE_TABLE);
	      
	    }
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TrackPointsColumns.TABLE_NAME);
	        db.execSQL("DROP TABLE IF EXISTS " + TracksColumns.TABLE_NAME);
	        onCreate(db);
			
		}
		
	}
}
