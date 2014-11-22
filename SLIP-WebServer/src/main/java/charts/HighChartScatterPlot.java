package charts;

import java.util.ArrayList;
import java.util.List;

import statistics.PositionPoint;

public class HighChartScatterPlot extends AbstractChart<HighChartScatterPlotData> {

	private static final long serialVersionUID = 8187628046604201225L;
	private List<HighChartScatterPlotData> data;
	private List<PositionPoint> points;
	
	public HighChartScatterPlot(long sessionID, List<PositionPoint> points) {
		this.data = new ArrayList<HighChartScatterPlotData>();
		this.points = points;
		super.setType(ChartType.HIGHCHART_SCATTER_PLOT);
		
		createChart(sessionID);
	}
	
	private void addData(long timestamp, PositionPoint coords) {
		System.out.println("inserted HighChartScatterPlotData timestamp: " + timestamp + "\tcoordinates: " + coords);
		data.add(new HighChartScatterPlotData(timestamp,coords));
	}
	
	@Override
	public List<HighChartScatterPlotData> getData() {
		return data;
	}
	
	@Override
	protected void createChart (long sessionID) {
		
		for (PositionPoint point : points) {
			if (point == null) {
				System.out.println("Added null coordinate");
				data.add(null);
			} else {
				addData(point.timestamp, point);
			}
		}
		
		System.out.println("Size of highChartScatterPlot data: " + getData().size());
	}
}