/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package cmusv.mr.carbon.io.file;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import cmusv.mr.carbon.data.Track;
import cmusv.mr.carbon.utils.StringUtils;

import CMU.SV.R;
import android.content.Context;
import android.location.Location;



/**
 * Write track as CSV to a file. See RFC 4180 for info on CSV. Output three
 * tables.<br>
 * The first table contains the track info. Its columns are:<br>
 * "Track name","Activity type","Track description" <br>
 * <br>
 * The second table contains the markers. Its columns are:<br>
 * "Marker name","Marker type","Marker description","Latitude (deg)","Longitude
 * (deg)","Altitude (m)","Bearing (deg)","Accuracy (m)","Speed (m/s)","Time"<br>
 * <br>
 * The thrid table contains the points. Its columns are:<br>
 * "Segment","Point","Latitude (deg)","Longitude (deg)","Altitude (m)","Bearing
 * (deg)","Accuracy (m)","Speed (m/s)","Time","Power (W)","Cadence (rpm)","Heart
 * rate (bpm)","Battery level (%)"<br>
 *
 * @author Rodrigo Damazio
 */
public class CsvTrackWriter{

  private static final NumberFormat SHORT_FORMAT = NumberFormat.getInstance();

  static {
    SHORT_FORMAT.setMaximumFractionDigits(4);
  }

  private final Context context;
  private PrintWriter printWriter;
  private Track track = null;
  private int segmentIndex;
  private int pointIndex;

  public CsvTrackWriter(Context context) {
    this.context = context;
  }


  
  public void prepare(Track aTrack, OutputStream out) {
	track = aTrack;
    printWriter = new PrintWriter(out);
    segmentIndex = 0;
    pointIndex = 0;
  }

  
  public void close() {
    printWriter.close();
  }

  
  public void writeHeader() {
    writeCommaSeparatedLine(context.getString(R.string.generic_name),
        context.getString(R.string.track_edit_activity_type_hint),
        context.getString(R.string.generic_description));
    writeCommaSeparatedLine(track.getName(), track.getCategory(), track.getDescription());
    writeCommaSeparatedLine();
  }

  
  public void writeFooter() {
    // Do nothing
  }

  

  
  public void writeBeginTrack() {
    writeCommaSeparatedLine(context.getString(R.string.description_track_segment),
        context.getString(R.string.description_track_point),
        context.getString(R.string.description_location_latitude),
        context.getString(R.string.description_location_longitude),
        context.getString(R.string.description_location_altitude),
        context.getString(R.string.description_location_bearing),
        context.getString(R.string.description_location_accuracy),
        context.getString(R.string.description_location_speed),
        context.getString(R.string.description_time),
        context.getString(R.string.description_sensor_power),
        context.getString(R.string.description_sensor_cadence),
        context.getString(R.string.description_sensor_heart_rate),
        context.getString(R.string.description_sensor_battery_level));
  }

  public void writeLocations(){
	  assert(track!=null);
	  for(Location location:track.getLocations()){
		  writeLocation(location);
	  }
  }

  
  public void writeOpenSegment() {
    segmentIndex++;
    pointIndex = 0;
  }

  
  public void writeCloseSegment() {
    // Do nothing
  }

  
  public void writeLocation(Location location) {
    String power = null;
    String cadence = null;
    String heartRate = null;
    String batteryLevel = null;
    
   
    pointIndex++;
    writeCommaSeparatedLine(Integer.toString(segmentIndex),
        Integer.toString(pointIndex),
        Double.toString(location.getLatitude()),
        Double.toString(location.getLongitude()),
        Double.toString(location.getAltitude()),
        Double.toString(location.getBearing()),
        SHORT_FORMAT.format(location.getAccuracy()),
        SHORT_FORMAT.format(location.getSpeed()),
        StringUtils.formatDateTimeIso8601(location.getTime()),
        power,
        cadence,
        heartRate,
        batteryLevel);
  }

  /**
   * Writes a single line of a CSV file.
   *
   * @param values the values to be written as CSV
   */
  private void writeCommaSeparatedLine(String... values) {
    StringBuilder builder = new StringBuilder();
    boolean isFirst = true;
    for (String value : values) {
      if (!isFirst) {
        builder.append(',');
      }
      isFirst = false;

      if (value != null) {
        builder.append('"');
        builder.append(value.replaceAll("\"", "\"\""));
        builder.append('"');
      }
    }
    printWriter.println(builder.toString());
  }
}
