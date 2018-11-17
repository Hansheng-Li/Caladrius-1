package edu.ua.cs.cs495.caladrius.android;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.ua.cs.cs495.caladrius.android.graphData.GraphContract.GraphEntry;
import org.json.JSONException;

import static edu.ua.cs.cs495.caladrius.android.Caladrius.getContext;

/**
 * This is a cursor adapter for update graph list value by getting value from graph setting table.
 *
 * @author Hansheng Li
 */
public class GraphCursorAdapter extends CursorAdapter {


    public final FitbitGraphView.GraphViewGraph[][] defaultGraphTypes = {
            {FitbitGraphView.GraphViewGraph.BarGraph},
            {FitbitGraphView.GraphViewGraph.PointsGraph},
            {FitbitGraphView.GraphViewGraph.PointsGraph},
            {FitbitGraphView.GraphViewGraph.BarGraph, FitbitGraphView.GraphViewGraph.LineGraph},
            {FitbitGraphView.GraphViewGraph.BarGraph, FitbitGraphView.GraphViewGraph.LineGraph,
                    FitbitGraphView.GraphViewGraph.PointsGraph},
    };
    public final String[][] defaultGraphStats = {
            {"calories"},
            {"steps"},
            {"caloriesBMR"},
            {"steps", "minutesSedentary"},
            {"minutesLightlyActive", "minutesFairlyActive", "minutesVeryActive"},
    };

    public final String[] defaultGraphTitles = {
            "Calories",
            "Steps",
            "CaloriesBMR",
            "Steps vs Minutes Sedentary",
            "Minutes of Activity",
    };

    private int GetColour(Integer selection){
        if (selection == GraphEntry.COLOR_BLACK) {
            return Color.BLACK;
        } else if (selection == GraphEntry.COLOR_BLUE) {
            return Color.BLUE;
        } else if (selection == GraphEntry.COLOR_CYAN) {
            return Color.CYAN;
        } else if (selection == GraphEntry.COLOR_GRAY) {
            return Color.GRAY;
        } else if (selection == GraphEntry.COLOR_GREEN) {
            return Color.GREEN;
        } else if (selection == GraphEntry.COLOR_RED) {
            return Color.RED;
        } else if (selection == GraphEntry.COLOR_YELLOW) {
            return Color.YELLOW;
        }
        return Color.DKGRAY;
    }

    public GraphCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.graph_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // Find the columns of graph attributes that we're interested in
        int timeRangeColumnIndex = cursor.getColumnIndex(GraphEntry.COLUMN_GRAPH_TIME_RANGE);
        int typeColumnIndex = cursor.getColumnIndex(GraphEntry.COLUMN_GRAPH_TYPE);
        int statsColumnIndex = cursor.getColumnIndex(GraphEntry.COLUMN_GRAPH_STATS);
        int colorColumnIndex = cursor.getColumnIndex(GraphEntry.COLUMN_GRAPH_COLORS);
        int titleColumnIndex = cursor.getColumnIndex(GraphEntry.COLUMN_GRAPH_TITLE);

        // Read the graph attributes from the Cursor for the current graph
        String graphTimeRange = cursor.getString(timeRangeColumnIndex);
        String graphType = cursor.getString(typeColumnIndex);
        final String graphStats = cursor.getString(statsColumnIndex);
        String graphColor = cursor.getString(colorColumnIndex);
        String graphTitle = cursor.getString(titleColumnIndex);

//        FitbitGraphView.GraphViewGraph[] defaultGraphTypes =
//                {FitbitGraphView.GraphViewGraph.BarGraph};
//
//        String[] defaultGraphStats = {"Heart Rate"};
//
//        ArrayList<FitbitGraphView.GraphViewGraph> graphTypes =
//                new ArrayList<>(Arrays.asList(defaultGraphTypes));
//
//        Integer[] defaultGraphColors = {Color.CYAN};
//
//        String[] defaultGraphTitles = {"Heart Rate"};
//
//        ArrayList<String> stats = new ArrayList<>(Arrays.asList(defaultGraphStats));
//
//        ArrayList<Integer> color = new ArrayList<>(Arrays.asList(defaultGraphColors));
//
//        String title = defaultGraphTitles[0];
//
//        Query query = new Query(graphTypes,
//                stats,
//                color,
//                title);
//
//        FitbitGraphView fgv = new FitbitGraphView(getContext(),
//                query
//        );
//
//        graphList.addView(fgv, 0);
//        List<String> timeRangeArrayList = Arrays.asList(Resources.getSystem().getStringArray(R.array.array_time_range_options));
//        String[] timeRangeArrayList = Resources.getSystem().getStringArray(R.array.array_time_range_options);

        List<String> timeRangeList = Arrays.asList(context.getResources().getStringArray(R.array.array_time_range_options));

        List<String> typeList = Arrays.asList(context.getResources().getStringArray(R.array.array_graph_type_options));


        final List<String> statsList = Arrays.asList(context.getResources().getStringArray(R.array.array_graph_stats_options));


        List<String> colorList = Arrays.asList(context.getResources().getStringArray(R.array.array_graph_color_options));


        LinearLayout graph_container = view.findViewById(R.id.graph_container);

        assert defaultGraphStats.length == defaultGraphTypes.length;
        for (int c = 0; c >= 0; c--) {
            ArrayList<FitbitGraphView.GraphViewGraph> graphTypes =
                    new ArrayList<>(Arrays.asList(defaultGraphTypes[c]));

            ArrayList<String> stats = new ArrayList<String>(){{
                add(statsList.get(Integer.valueOf(graphStats)));
            }};

            ArrayList<Integer> color = new ArrayList<>(Arrays.asList(GetColour(Integer.valueOf(graphColor))));

            String title = defaultGraphTitles[c];
            Query query = new Query(graphTypes,
                    stats,
                    color,
                    graphTitle);
            FitbitGraphView fgv = null;
            try {
                fgv = new FitbitGraphView(getContext(),
                        query
                );
            } catch (JSONException | InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
                continue;
            }

            if((graph_container).getChildCount() > 0)
                (graph_container).removeAllViews();
            graph_container.addView(fgv);
            break;
        }



        TextView timeRangeTextView = view.findViewById(R.id.time_range);
        timeRangeTextView.setText(timeRangeList.get(Integer.valueOf(graphTimeRange)));

        TextView typeTextView = view.findViewById(R.id.graph_type);
        typeTextView.setText(typeList.get(Integer.valueOf(graphType)));

        TextView statsTextView = view.findViewById(R.id.graph_stats);
        statsTextView.setText(statsList.get(Integer.valueOf(graphStats)));

        TextView colorTextView = view.findViewById(R.id.graph_color);
        colorTextView.setText(colorList.get(Integer.valueOf(graphColor)));

        TextView titleTextView = view.findViewById(R.id.graph_title);
        titleTextView.setText(graphTitle);



    }
}