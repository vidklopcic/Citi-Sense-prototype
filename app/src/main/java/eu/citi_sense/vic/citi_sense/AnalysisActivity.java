package eu.citi_sense.vic.citi_sense;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.support_classes.Charts;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuHandler;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuListeners;


public class AnalysisActivity extends Activity {
    private GlobalVariables mGVar;
    private Charts mCharts = new Charts();
    private SlidingMenuHandler mSlidingMenu;
    private LatLng location;
    private TextView mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mGVar = (GlobalVariables) getApplicationContext();
        mSlidingMenu = new SlidingMenuHandler(this);
        mActionBarTitle = (TextView) findViewById(R.id.analysis_action_bar_title);
        LineData data = mGVar.data.getAQIData(24, 5);
        LineChart chart = (LineChart) findViewById(R.id.sliding_menu_chart);
        mCharts.setupAQISlidingChart(data, chart);
        LinearLayout mapButton = (LinearLayout) findViewById(R.id.sliding_menu_map);
        LinearLayout analysisButton = (LinearLayout) findViewById(R.id.sliding_menu_analysis);
        LinearLayout stationsButton = (LinearLayout) findViewById(R.id.sliding_menu_stations);
        new SlidingMenuListeners(mapButton, analysisButton, stationsButton,
                SlidingMenuListeners.ANALYSIS_ACTIVITY, mSlidingMenu, this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
    }

    public void menuBtnClicked(View view) {
        mSlidingMenu.menu.showMenu(true);
    }

    private void setActionBarTitle(String title) {
        mActionBarTitle.setText(title);
    }
}
