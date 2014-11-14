package dataAccessLayer;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import statistics.PositionPoint;

@Repository
public class StatisticsQueries {
	
	private JdbcTemplate jdbcTemplateObject;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		System.out.println("setDataSource. Datasource: " + (dataSource != null));
		jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public JdbcTemplate getJdbcTemplate(){
		return jdbcTemplateObject;
	}
	
	public Long getMinTime(long sessionID) {
		String sql =	"SELECT MIN(\"Timestamp\") " +
						"FROM \"Game\" " +
						"WHERE \"SessionID\" = ?";
		
		return jdbcTemplateObject.queryForObject(sql, new Object[] {sessionID}, Long.class);
	}
	
	public Long getMaxTime(long sessionID) {
		String sql =	"SELECT MAX(\"Timestamp\") " +
						"FROM \"Game\" " +
						"WHERE \"SessionID\" = ?";
		
		return jdbcTemplateObject.queryForObject(sql, new Object[] {sessionID}, Long.class);
	}
	
	/**
	 * Get the corresponding coordinate that has a time stamp closest to the time stamp specified <br>
	 * We use lowTimestamp and highTimestamp to search within a range around the timestampExpected
	 * @param sessionID the session ID
	 * @param timestampExpected the time stamp you want the result to be closest to
	 * @param lowTimestamp
	 * @param highTimestamp
	 * @return
	 */
	public PositionPoint getClosestPoint(long sessionID, long timestampExpected, long lowTimestamp, long highTimestamp) {
		
		String sql = 	"SELECT \"PayloadID\", \"xPosition\", \"yPosition\", \"TimeDifference\"" +
						"FROM ( " +
						"SELECT \"PayloadID\", \"xPosition\", \"yPosition\", abs(\"Timestamp\" - ?) AS \"TimeDifference\" " +
						"FROM \"Game\" " +
						"WHERE \"Timestamp\" >= ? AND \"Timestamp\" <= ? AND \"SessionID\" = ? " +
						"ORDER BY \"TimeDifference\"" +
						") AS \"ClosestPosition\" " +
						"LIMIT 1";
		
		 return jdbcTemplateObject.queryForObject(sql, new Object[] {timestampExpected, lowTimestamp, highTimestamp, sessionID}, 
				new RowMapper<PositionPoint>() {

					@Override
					public PositionPoint mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						
						int x = rs.getInt("xPosition");
						int y = rs.getInt("yPosition");
						
						return new PositionPoint(x,y);
					}
		 });
	}	
}