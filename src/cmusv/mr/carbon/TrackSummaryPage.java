package cmusv.mr.carbon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cmusv.mr.carbon.data.Track;
import cmusv.mr.carbon.data.stats.TripStatistics;
import cmusv.mr.carbon.db.DatabaseHelper;

public class TrackSummaryPage extends Activity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.stats);
		Bundle bundle = this.getIntent().getExtras();
		long trackId = bundle.getLong("trackId", TrafficLog.NO_TRACK);
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		if (trackId == TrafficLog.NO_TRACK) {
			finish();
		}
		Button button = (Button) findViewById(R.id.stats_resume_button);
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}

		});
		Log.d("track!?!?!?",""+trackId);
		Track track = dbHelper.getTrack(trackId);
		if (track != null) {
			TripStatistics tripStatistics = track.getTripStatistics();

			// ((TextView)
			// findViewById(R.id.stats_datatype_value)).setText(""+tripStatistics.getTotalDistance());
			((TextView) findViewById(R.id.stats_total_distance_value))
					.setText("" + tripStatistics.getTotalDistance());
			((TextView) findViewById(R.id.stats_total_time_value)).setText(""
					+ tripStatistics.getTotalTime());
			((TextView) findViewById(R.id.stats_average_speed_value))
					.setText("" + tripStatistics.getAverageSpeed());
			((TextView) findViewById(R.id.stats_max_speed_value)).setText(""
					+ tripStatistics.getMaxSpeed());

		
		}

		/*
		 * stats_speed_label
		 * 
		 * stats_total_time_label stats_total_time_value
		 * stats_average_speed_label stats_max_speed_label
		 */

	}
}
