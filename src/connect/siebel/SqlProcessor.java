package connect.siebel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessServiceException;

public class SqlProcessor extends com.siebel.eai.SiebelBusinessService {

	@Override
	public void doInvokeMethod(String methodName, SiebelPropertySet input, SiebelPropertySet output)
			throws SiebelBusinessServiceException {

		Connection cxn = null;

		cxn = getConnection(input.getProperty("tnsNameEntry"), input.getProperty("dbUser"),
				input.getProperty("dbPassword"));

		if (methodName.equals("MethodName") ) {
			 try {
				doQuery(cxn, "SELECT sysdate FROM dual", output);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	}

	private Connection getConnection(String tnsName, String dbUser, String dbPassword)
			throws SiebelBusinessServiceException {

		try {
			OracleDataSource dataSource = new OracleDataSource();

			dataSource.setTNSEntryName(tnsName);
			dataSource.setDriverType("thin");
			dataSource.setUser(dbUser);
			dataSource.setPassword(dbPassword);

			return dataSource.getConnection();
		} catch (SQLException e) {
			SiebelBusinessServiceException sblEx = new SiebelBusinessServiceException("SQLEXCEPTION",
					"Error in esablish connection, TNSEntry: " + tnsName + " user " + dbUser + " pwd " + dbPassword);
			sblEx.setStackTrace(e.getStackTrace());
			throw sblEx;
		}
	}


	private void doQuery(Connection connection, String query, SiebelPropertySet output)
			throws SiebelBusinessServiceException, SQLException {

		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;
		ResultSetMetaData metaData = null;
		SiebelPropertySet tmpPS = null;
		String fieldValue = null;
				
		try {
						
			preparedStmt = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			preparedStmt.execute();
			resultSet = preparedStmt.getResultSet();
			metaData = resultSet.getMetaData();
	
			while (resultSet.next()) {								
				tmpPS = new SiebelPropertySet();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {	
					fieldValue = resultSet.getTimestamp(i).toString();
					tmpPS.setProperty(metaData.getColumnLabel(i), fieldValue);
				}
				output.addChild(tmpPS);
			}

		} catch (Exception e) {
			SiebelBusinessServiceException sblEx = new SiebelBusinessServiceException("SQLEXCEPTION",
					"Error running: " + query);
			sblEx.setStackTrace(e.getStackTrace());
			throw sblEx;
		} finally {
			if (preparedStmt != null)
				preparedStmt.close();
			if (resultSet != null)
				resultSet.close();
			connection.close();
		}
	}

	

}
