package cmusv.mr.carbon.db;




import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import android.util.Log;
import cmusv.mr.carbon.data.Track;
import cmusv.mr.carbon.data.stats.TripStatistics;

public class DatabaseHelper extends SQLiteOpenHelper {
	static final String DATABASE_NAME = "mrcarbon.db";
	private static final int DATABASE_VERSION = 21;
	private SQLiteDatabase db;
	public static final String TAG = DatabaseHelper.class.getSimpleName();

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = this.getWritableDatabase();
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

	public long insertTrack(Track track) {
		ContentValues values = createContentValues(track);
		boolean hasStartTime = values.containsKey(TracksColumns.STARTTIME);
		boolean hasStartId = values.containsKey(TracksColumns.STARTID);
		if (!hasStartTime || !hasStartId) {
			throw new IllegalArgumentException(
					"Both start time and start id values are required.");
		}

		long rowId = db.insert(TracksColumns.TABLE_NAME, TracksColumns._ID,
				values);

		if (rowId >= 0) {
			return rowId;
		}
		throw new SQLException("Failed to insert a track " + track);

	}

	public Track getTrack(long trackId) {
		if (trackId < 0) {
			return null;
		}
		Cursor cursor = null;
		try {
			cursor = getTracksCursor(null, TracksColumns._ID + "=?",
					new String[] { Long.toString(trackId) }, TracksColumns._ID);
			if (cursor != null && cursor.moveToNext()) {
				return createTrack(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	private ArrayList<Location> getLocationsByTrack(long trackId, long startTrackPointId){
		ArrayList<Location> locations = new ArrayList<Location>(); 
		//getLocationsCursor()
		Cursor cursor = getLocationsCursor(trackId, startTrackPointId,	-1, false);
		
		if(cursor!=null && cursor.moveToFirst()){
			do{
				Location location = createLocation(cursor);
				locations.add(location);
				Log.d(TAG,"location:"+location);
			}while(cursor.moveToNext());
			
			return locations;
		}
		//db.query(TracksPointsColumns.TABLE_NAME,);
		return null;
	}
	private Cursor getTracksCursor(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		return db.query(TracksColumns.TABLE_NAME, TracksColumns.COLUMNS,
				selection, selectionArgs, null, null, sortOrder);
	}

	public Track createTrack(Cursor cursor) {
		int idIndex = cursor.getColumnIndexOrThrow(TracksColumns._ID);
		int nameIndex = cursor.getColumnIndexOrThrow(TracksColumns.NAME);
		int descriptionIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.DESCRIPTION);
		int categoryIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.CATEGORY);
		int startIdIndex = cursor.getColumnIndexOrThrow(TracksColumns.STARTID);
		int stopIdIndex = cursor.getColumnIndexOrThrow(TracksColumns.STOPID);
		int startTimeIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.STARTTIME);
		int stopTimeIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.STOPTIME);
		int numPointsIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.NUMPOINTS);
		int totalDistanceIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.TOTALDISTANCE);
		int totalTimeIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.TOTALTIME);
		int movingTimeIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.MOVINGTIME);
		int minLatIndex = cursor.getColumnIndexOrThrow(TracksColumns.MINLAT);
		int maxLatIndex = cursor.getColumnIndexOrThrow(TracksColumns.MAXLAT);
		int minLonIndex = cursor.getColumnIndexOrThrow(TracksColumns.MINLON);
		int maxLonIndex = cursor.getColumnIndexOrThrow(TracksColumns.MAXLON);
		int maxSpeedIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.MAXSPEED);
		int minElevationIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.MINELEVATION);
		int maxElevationIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.MAXELEVATION);
		int elevationGainIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.ELEVATIONGAIN);
		int minGradeIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.MINGRADE);
		int maxGradeIndex = cursor
				.getColumnIndexOrThrow(TracksColumns.MAXGRADE);
		int mapIdIndex = cursor.getColumnIndexOrThrow(TracksColumns.MAPID);
		//int tableIdIndex = cursor.getColumnIndexOrThrow(TracksColumns.TABLEID);
		int iconIndex = cursor.getColumnIndexOrThrow(TracksColumns.ICON);

		Track track = new Track();
		TripStatistics tripStatistics = track.getTripStatistics();
		if (!cursor.isNull(idIndex)) {
			track.setId(cursor.getLong(idIndex));
		}
		if (!cursor.isNull(nameIndex)) {
			track.setName(cursor.getString(nameIndex));
		}
		if (!cursor.isNull(descriptionIndex)) {
			track.setDescription(cursor.getString(descriptionIndex));
		}
		if (!cursor.isNull(categoryIndex)) {
			track.setCategory(cursor.getString(categoryIndex));
		}
		if (!cursor.isNull(startIdIndex)) {
			track.setStartId(cursor.getLong(startIdIndex));
		}
		if (!cursor.isNull(stopIdIndex)) {
			track.setStopId(cursor.getLong(stopIdIndex));
		}
		if (!cursor.isNull(startTimeIndex)) {
			tripStatistics.setStartTime(cursor.getLong(startTimeIndex));
		}
		if (!cursor.isNull(stopTimeIndex)) {
			tripStatistics.setStopTime(cursor.getLong(stopTimeIndex));
		}
		if (!cursor.isNull(numPointsIndex)) {
			track.setNumberOfPoints(cursor.getInt(numPointsIndex));
		}
		if (!cursor.isNull(totalDistanceIndex)) {
			tripStatistics
					.setTotalDistance(cursor.getFloat(totalDistanceIndex));
		}
		if (!cursor.isNull(totalTimeIndex)) {
			tripStatistics.setTotalTime(cursor.getLong(totalTimeIndex));
		}
		if (!cursor.isNull(movingTimeIndex)) {
			tripStatistics.setMovingTime(cursor.getLong(movingTimeIndex));
		}
		if (!cursor.isNull(minLatIndex) && !cursor.isNull(maxLatIndex)
				&& !cursor.isNull(minLonIndex) && !cursor.isNull(maxLonIndex)) {
			int bottom = cursor.getInt(minLatIndex);
			int top = cursor.getInt(maxLatIndex);
			int left = cursor.getInt(minLonIndex);
			int right = cursor.getInt(maxLonIndex);
			tripStatistics.setBounds(left, top, right, bottom);
		}
		if (!cursor.isNull(maxSpeedIndex)) {
			tripStatistics.setMaxSpeed(cursor.getFloat(maxSpeedIndex));
		}
		if (!cursor.isNull(minElevationIndex)) {
			tripStatistics.setMinElevation(cursor.getFloat(minElevationIndex));
		}
		if (!cursor.isNull(maxElevationIndex)) {
			tripStatistics.setMaxElevation(cursor.getFloat(maxElevationIndex));
		}
		if (!cursor.isNull(elevationGainIndex)) {
			tripStatistics.setTotalElevationGain(cursor
					.getFloat(elevationGainIndex));
		}
		if (!cursor.isNull(minGradeIndex)) {
			tripStatistics.setMinGrade(cursor.getFloat(minGradeIndex));
		}
		if (!cursor.isNull(maxGradeIndex)) {
			tripStatistics.setMaxGrade(cursor.getFloat(maxGradeIndex));
		}
		if (!cursor.isNull(mapIdIndex)) {
			track.setMapId(cursor.getString(mapIdIndex));
		}
		/*if (!cursor.isNull(tableIdIndex)) {
			track.setTableId(cursor.getString(tableIdIndex));
		}*/
		if (!cursor.isNull(iconIndex)) {
			track.setIcon(cursor.getString(iconIndex));
		}
		
		track.setLocations(getLocationsByTrack(track.getId(), track.getStartId()));
		return track;
	}

	public long insertTrackPoint(Location location, long trackId) {

		ContentValues values = createContentValues(location, trackId);
		long rowId = db.insert(TrackPointsColumns.TABLE_NAME,
				TrackPointsColumns._ID, values);

		if (rowId >= 0) {
			return rowId;
		}
		throw new SQLiteException("Failed to insert a track point " + location);

	}

	private Cursor getTrackPointsCursor(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return db.query(TrackPointsColumns.TABLE_NAME,
				TrackPointsColumns.COLUMNS, selection, selectionArgs, null,
				null, sortOrder);
	}

	private Location findLocationBy(String selection, String[] selectionArgs) {
		Cursor cursor = null;
		try {
			cursor = getTrackPointsCursor(null, selection, selectionArgs,
					TrackPointsColumns._ID);
			if (cursor != null && cursor.moveToNext()) {
				return createLocation(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public Location getFirstLocation() {
		String selection = TrackPointsColumns._ID + "=(select min("
				+ TrackPointsColumns._ID + ") from "
				+ TrackPointsColumns.TABLE_NAME + ")";
		return findLocationBy(selection, null);
	}

	public Location getLastLocation() {
		String selection = TrackPointsColumns._ID + "=(select max("
				+ TrackPointsColumns._ID + ") from "
				+ TrackPointsColumns.TABLE_NAME + ")";
		return findLocationBy(selection, null);
	}

	public Location getLocation(long trackPointId) {
		if (trackPointId < 0) {
			return null;
		}
		return findLocationBy(TrackPointsColumns._ID + "=?",
				new String[] { Long.toString(trackPointId) });
	}

	public Cursor getLocationsCursor(long trackId, long startTrackPointId,
			int maxLocations, boolean descending) {
		if (trackId < 0) {
			return null;
		}

		String selection;
		String[] selectionArgs;
		if (startTrackPointId >= 0) {
			String comparison = descending ? "<=" : ">=";
			selection = TrackPointsColumns.TRACKID + "=? AND "
					+ TrackPointsColumns._ID + comparison + "?";
			selectionArgs = new String[] { Long.toString(trackId),
					Long.toString(startTrackPointId) };
		} else {
			selection = TrackPointsColumns.TRACKID + "=?";
			selectionArgs = new String[] { Long.toString(trackId) };
		}

		String sortOrder = TrackPointsColumns._ID;
		if (descending) {
			sortOrder += " DESC";
		}
		if (maxLocations > 0) {
			sortOrder += " LIMIT " + maxLocations;
		}
		return getTrackPointsCursor(null, selection, selectionArgs, sortOrder);
	}

	/**
	 * Creates the {@link ContentValues} for a {@link Location}.
	 * 
	 * @param location
	 *            the location
	 * @param trackId
	 *            the track id
	 */
	private ContentValues createContentValues(Location location, long trackId) {
		ContentValues values = new ContentValues();
		values.put(TrackPointsColumns.TRACKID, trackId);
		values.put(TrackPointsColumns.LONGITUDE,
				(int) (location.getLongitude() * 1E6));
		values.put(TrackPointsColumns.LATITUDE,
				(int) (location.getLatitude() * 1E6));

		// Hack for Samsung phones that don't properly populate the time field
		long time = location.getTime();
		if (time == 0) {
			time = System.currentTimeMillis();
		}
		values.put(TrackPointsColumns.TIME, time);
		if (location.hasAltitude()) {
			values.put(TrackPointsColumns.ALTITUDE, location.getAltitude());
		}
		if (location.hasAccuracy()) {
			values.put(TrackPointsColumns.ACCURACY, location.getAccuracy());
		}
		if (location.hasSpeed()) {
			values.put(TrackPointsColumns.SPEED, location.getSpeed());
		}
		if (location.hasBearing()) {
			values.put(TrackPointsColumns.BEARING, location.getBearing());
		}

		/*
		 * if (location instanceof MyTracksLocation) { MyTracksLocation
		 * myTracksLocation = (MyTracksLocation) location; if
		 * (myTracksLocation.getSensorDataSet() != null) {
		 * values.put(TrackPointsColumns.SENSOR,
		 * myTracksLocation.getSensorDataSet().toByteArray()); } }
		 */
		return values;
	}

	public ContentValues createContentValues(Track track) {
		ContentValues values = new ContentValues();
		TripStatistics tripStatistics = track.getTripStatistics();

		// Value < 0 indicates no id is available
		if (track.getId() >= 0) {
			values.put(TracksColumns._ID, track.getId());
		}
		values.put(TracksColumns.NAME, track.getName());
		values.put(TracksColumns.DESCRIPTION, track.getDescription());
		values.put(TracksColumns.CATEGORY, track.getCategory());
		values.put(TracksColumns.STARTID, track.getStartId());
		values.put(TracksColumns.STOPID, track.getStopId());
		values.put(TracksColumns.STARTTIME, tripStatistics.getStartTime());
		values.put(TracksColumns.STOPTIME, tripStatistics.getStopTime());
		values.put(TracksColumns.NUMPOINTS, track.getNumberOfPoints());
		values.put(TracksColumns.TOTALDISTANCE,
				tripStatistics.getTotalDistance());
		values.put(TracksColumns.TOTALTIME, tripStatistics.getTotalTime());
		values.put(TracksColumns.MOVINGTIME, tripStatistics.getMovingTime());
		values.put(TracksColumns.MINLAT, tripStatistics.getBottom());
		values.put(TracksColumns.MAXLAT, tripStatistics.getTop());
		values.put(TracksColumns.MINLON, tripStatistics.getLeft());
		values.put(TracksColumns.MAXLON, tripStatistics.getRight());
		values.put(TracksColumns.AVGSPEED, tripStatistics.getAverageSpeed());
		values.put(TracksColumns.AVGMOVINGSPEED,
				tripStatistics.getAverageMovingSpeed());
		values.put(TracksColumns.MAXSPEED, tripStatistics.getMaxSpeed());
		values.put(TracksColumns.MINELEVATION, tripStatistics.getMinElevation());
		values.put(TracksColumns.MAXELEVATION, tripStatistics.getMaxElevation());
		values.put(TracksColumns.ELEVATIONGAIN,
				tripStatistics.getTotalElevationGain());
		values.put(TracksColumns.MINGRADE, tripStatistics.getMinGrade());
		values.put(TracksColumns.MAXGRADE, tripStatistics.getMaxGrade());
		values.put(TracksColumns.MAPID, track.getMapId());
		//values.put(TracksColumns.TABLEID, track.getTableId());
		values.put(TracksColumns.ICON, track.getIcon());
		return values;
	}

	public Location createLocation(Cursor cursor) {
		//Location location = new MyTracksLocation("");
		Location location = new Location("");
		fillLocation(cursor, location);
		return location;
	}

	private void fillLocation(Cursor cursor, CachedTrackPointsIndexes indexes,
			Location location) {
		location.reset();

		if (!cursor.isNull(indexes.longitudeIndex)) {
			location.setLongitude(((double) cursor
					.getInt(indexes.longitudeIndex)) / 1E6);
		}
		if (!cursor.isNull(indexes.latitudeIndex)) {
			location.setLatitude(((double) cursor.getInt(indexes.latitudeIndex)) / 1E6);
		}
		if (!cursor.isNull(indexes.timeIndex)) {
			location.setTime(cursor.getLong(indexes.timeIndex));
		}
		if (!cursor.isNull(indexes.altitudeIndex)) {
			location.setAltitude(cursor.getFloat(indexes.altitudeIndex));
		}
		if (!cursor.isNull(indexes.accuracyIndex)) {
			location.setAccuracy(cursor.getFloat(indexes.accuracyIndex));
		}
		if (!cursor.isNull(indexes.speedIndex)) {
			location.setSpeed(cursor.getFloat(indexes.speedIndex));
		}
		if (!cursor.isNull(indexes.bearingIndex)) {
			location.setBearing(cursor.getFloat(indexes.bearingIndex));
		}
		/*if (location instanceof MyTracksLocation
				&& !cursor.isNull(indexes.sensorIndex)) {
			MyTracksLocation myTracksLocation = (MyTracksLocation) location;
			try {
				myTracksLocation.setSensorDataSet(SensorDataSet
						.parseFrom(cursor.getBlob(indexes.sensorIndex)));
			} catch (InvalidProtocolBufferException e) {
				Log.w(TAG, "Failed to parse sensor data.", e);
			}
		}*/
	}

	
	public void fillLocation(Cursor cursor, Location location) {
		fillLocation(cursor, new CachedTrackPointsIndexes(cursor), location);
	}
	
	
	  /**
	   * A cache of track points indexes.
	   */
	  private static class CachedTrackPointsIndexes {
	    public final int idIndex;
	    public final int longitudeIndex;
	    public final int latitudeIndex;
	    public final int timeIndex;
	    public final int altitudeIndex;
	    public final int accuracyIndex;
	    public final int speedIndex;
	    public final int bearingIndex;
	    public final int sensorIndex;

	    public CachedTrackPointsIndexes(Cursor cursor) {
	      idIndex = cursor.getColumnIndex(TrackPointsColumns._ID);
	      longitudeIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.LONGITUDE);
	      latitudeIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.LATITUDE);
	      timeIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.TIME);
	      altitudeIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.ALTITUDE);
	      accuracyIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.ACCURACY);
	      speedIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.SPEED);
	      bearingIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.BEARING);
	      sensorIndex = cursor.getColumnIndexOrThrow(TrackPointsColumns.SENSOR);
	    }
	  }
	  public void updateTrack(Track track){
		  db.update(TracksColumns.TABLE_NAME,createContentValues(track), TracksColumns._ID + "=?", new String[] { Long.toString(track.getId()) });
	  }
	  public long getLastLocationId(long trackId) {
		    if (trackId < 0) {
		      return -1L;
		    }
		    Cursor cursor = null;
		    try {
		      String selection = TrackPointsColumns._ID + "=(select max(" + TrackPointsColumns._ID
		          + ") from " + TrackPointsColumns.TABLE_NAME + " WHERE " + TrackPointsColumns.TRACKID
		          + "=?)";
		      String[] selectionArgs = new String[] { Long.toString(trackId) };
		      cursor = getTrackPointsCursor(new String[] { TrackPointsColumns._ID }, selection,
		          selectionArgs, TrackPointsColumns._ID);
		      if (cursor != null && cursor.moveToFirst()) {
		        return cursor.getLong(cursor.getColumnIndexOrThrow(TrackPointsColumns._ID));
		      }
		    } finally {
		      if (cursor != null) {
		        cursor.close();
		      }
		    }
		    return -1L;
		  }

}
