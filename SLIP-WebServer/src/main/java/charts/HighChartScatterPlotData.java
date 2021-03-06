package charts;

import java.io.Serializable;

import statistics.PositionPoint;

public class HighChartScatterPlotData implements Serializable{

	private static final long serialVersionUID = -6488664015497188149L;
	private long timestamp;
	private PositionPoint position;
	
	public HighChartScatterPlotData(long timestamp, PositionPoint position) {
		this.timestamp = timestamp;
		this.position = position;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public int[] getPosition() {
		
		if (position.hasPositionPoint()) {
			int[] positionArray = new int[2];
			
			positionArray[0] = this.position.xPosition;
			positionArray[1] = this.position.yPosition;
			
			return positionArray;
		} else {
			return null;
		}
	}
}
