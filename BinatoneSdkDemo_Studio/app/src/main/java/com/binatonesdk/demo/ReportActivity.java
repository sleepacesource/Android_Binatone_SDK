package com.binatonesdk.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.binatonesdk.demo.view.AnalysisChart;
import com.sleepace.sdk.binatone.domain.Analysis;
import com.sleepace.sdk.binatone.domain.HistoryData;
import com.sleepace.sdk.binatone.util.SleepState;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportActivity extends BaseActivity {

	private AnalysisChart chart;
	private TextView tvDuration, tvAwakeDuration, tvOutOfBedDuration, tvNotMonitorDuration, tvAvgHeartRate, tvAvgBreathRate;
	private TextView tvBreathPause, tvOutOfBed;
	private LinearLayout layoutBreathPause, layoutOutOfBed;
	private TextView tvReportDate;

	public static final int REPORT_TYPE_24 = 1;
	public static final int REPORT_TYPE_HISOTRY = 2;
	public static final int REPORT_TYPE_DEMO = 3;
	public static final String EXTRA_REPORT_TYPE = "extra_report_type";
	public static final String EXTRA_DATA = "extra_data";
	private int reportType;
	private HistoryData historyData;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		tvReportDate = findViewById(R.id.tv_report_date);
		chart = (AnalysisChart) findViewById(R.id.chart);
		tvDuration = (TextView) findViewById(R.id.tv_sleep_duration);
		tvAwakeDuration = (TextView) findViewById(R.id.tv_awake_duration);
		tvOutOfBedDuration = (TextView) findViewById(R.id.tv_out_of_bed_duration);
		tvNotMonitorDuration = (TextView) findViewById(R.id.tv_no_monitor_duration);
		tvAvgHeartRate = (TextView) findViewById(R.id.tv_avg_heartrate);
		tvAvgBreathRate = (TextView) findViewById(R.id.tv_avg_breathrate);
		tvBreathPause = findViewById(R.id.tv_breath_pause);
		layoutBreathPause = findViewById(R.id.layout_breath_pause);
		tvOutOfBed = findViewById(R.id.tv_out_of_bed);
		layoutOutOfBed = findViewById(R.id.layout_out_of_bed);
	}

	public void initListener() {
		super.initListener();
	}

	public void initUI() {
		reportType = getIntent().getIntExtra(EXTRA_REPORT_TYPE, REPORT_TYPE_24);
		historyData = (HistoryData) getIntent().getSerializableExtra(EXTRA_DATA);

		switch (reportType) {
		case REPORT_TYPE_24:
			tvTitle.setText(R.string.data24);
			break;
		case REPORT_TYPE_HISOTRY:
			tvTitle.setText(R.string.Daily_Report);
			break;
		case REPORT_TYPE_DEMO:
			tvTitle.setText(R.string.simulation_data);
			break;
		}

		Analysis analysis = null;
		if (historyData != null) {
			analysis = historyData.getAnalysis();
		}

		if (analysis == null) {
			analysis = new Analysis();
			analysis.setDuration(610);
			byte[] sca_array = new byte[30];
			Random random = new Random();
			for (int i = 0; i < sca_array.length; i++) {
				if (i < 4) {
					sca_array[i] = SleepState.SLEEP_STATE_AWAKE;
				} else {
					sca_array[i] = (byte) random.nextInt(4);
				}
			}
			analysis.setSca_array(sca_array);
		}

		chart.refreshChart(historyData);
		tvDuration.setText(getDuration(analysis.getDuration()));
		tvAwakeDuration.setText(getDuration(analysis.getWake()));
		tvOutOfBedDuration.setText(getDuration(analysis.getOutOfBedDuration()));
		tvNotMonitorDuration.setText(getDuration(getNotMonitorDuration()));
		tvAvgHeartRate.setText(getTimes(analysis.getAvgHeartRate(), true));
		tvAvgBreathRate.setText(getTimes(analysis.getAvgBreathRate(), false));

		SdkLog.log(TAG+" initUI reportType:" + reportType);
		if (reportType == REPORT_TYPE_HISOTRY) {
			tvReportDate.setText(dateFormat.format(new Date(historyData.getSummary().getStartTime() * 1000l)));
			if (historyData != null) {
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				LayoutInflater inflater = LayoutInflater.from(this);
				tvBreathPause.setText(R.string.nothing);
				layoutBreathPause.setVisibility(View.GONE);
				tvOutOfBed.setText(R.string.nothing);
				layoutOutOfBed.setVisibility(View.GONE);
			} else {
				tvBreathPause.setText(R.string.nothing);
				tvOutOfBed.setText(R.string.nothing);
				layoutBreathPause.setVisibility(View.GONE);
				layoutOutOfBed.setVisibility(View.GONE);
			}
		} else {
			tvReportDate.setVisibility(View.GONE);
			tvBreathPause.setVisibility(View.GONE);
			tvOutOfBed.setVisibility(View.GONE);
			layoutBreathPause.setVisibility(View.GONE);
			layoutOutOfBed.setVisibility(View.GONE);
		}
	}

	private int getNotMonitorDuration() {
		int duration = 0;
		if (historyData != null) {
			byte[] state = historyData.getAnalysis().getSca_array();
			int len = state == null ? 0 : state.length;
			for (int i = 0; i < len; i++) {
				if (state[i] == SleepState.SLEEP_STATE_NONE) {
					duration++;
				}
			}
		}
		return duration;
	}

	private String getDuration(int duration) {
		int hour = duration / 60;
		int min = duration % 60;
		return hour + getString(R.string.hour) + min + getString(R.string.min);
	}

	private String getTimes(int times, boolean isHeartRate) {
		if (isHeartRate) {
			return times + getString(R.string.beats_min);
		} else {
			return times + getString(R.string.times_min);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

}
