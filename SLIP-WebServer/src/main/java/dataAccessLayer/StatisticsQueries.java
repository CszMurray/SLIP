package dataAccessLayer;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import statistics.PositionPoint;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StatisticsQueries extends DAO  implements StatisticsDAO{
	
	public Long getMinTime(long sessionID) {
		
		String sql =	"SELECT MIN(\"Timestamp\") " +
						"FROM \"SessionPayload\" " +
						"WHERE \"SessionID\" = ?";
		
		return jdbcTemplateObject.queryForObject(sql, new Object[] {sessionID}, Long.class);
	}
	
	public Long getMaxTime(long sessionID) {
		String sql =	"SELECT MAX(\"Timestamp\") " +
						"FROM \"SessionPayload\" " +
						"WHERE \"SessionID\" = ?";
		
		return jdbcTemplateObject.queryForObject(sql, new Object[] {sessionID}, Long.class);
	}
	
	/**
	 * Get the corresponding coordinate that has a timestamp closest to the time stamp specified <br>
	 * We use lowTimestamp and highTimestamp to search within a range around the timestampExpected
	 * @param sessionID the session ID
	 * @param timestampExpected the time stamp you want the result to be closest to
	 * @param lowTimestamp
	 * @param highTimestamp
	 * @return
	 */
	public PositionPoint getClosestPoint(long sessionID, long timestampExpected, long lowTimestamp, long highTimestamp) {
		
		String sql = 	"SELECT \"PayloadID\", \"xPosition\", \"yPosition\", \"Timestamp\", \"TimeDifference\" " +
						"FROM ( " +
						"SELECT \"PayloadID\", \"xPosition\", \"yPosition\", \"Timestamp\", abs(\"Timestamp\" - ?) AS \"TimeDifference\" " +
						"FROM \"SessionPayload\" " +
						"WHERE \"Timestamp\" >= ? AND \"Timestamp\" <= ? AND \"SessionID\" = ? " +
						"ORDER BY \"TimeDifference\"" +
						") AS \"ClosestPosition\" " +
						"LIMIT 1";
		
		PositionPoint point = new PositionPoint(timestampExpected, null, null);
		
		// TODO create a new class for the row mapper and put it into a new package.
		// TODO find a way to get rid of this try catch block
		try {
			point =  jdbcTemplateObject.queryForObject(sql, new Object[] {timestampExpected, lowTimestamp, highTimestamp, sessionID}, 
					new RowMapper<PositionPoint>() {
				
				@Override
				public PositionPoint mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					long timestamp = rs.getLong("Timestamp");
					int x = rs.getInt("xPosition");
					int y = rs.getInt("yPosition");
					
					return new PositionPoint(timestamp, x,y);
				}
			});
			
		} catch (EmptyResultDataAccessException e) {
			System.err.println("No position found near timestamp: " + timestampExpected);
		}
		
		return point;
	}

	@Override
	public List<PositionPoint> getPoints(long sessionID) {

		String sql = 	"SELECT *" +
						"FROM \"SessionPayload\"" +
						"WHERE \"SessionID\" = ?";

		return jdbcTemplateObject.query(sql, new Object[]{sessionID}, new RowMapper<PositionPoint>() {
			@Override
			public PositionPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new PositionPoint(rs.getLong("Timestamp"), rs.getInt("xPosition"), rs.getInt("yPosition"));
			}
		});
	}
}
