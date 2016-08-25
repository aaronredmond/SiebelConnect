package connect.siebel;

import java.io.File;
import java.util.Enumeration;

import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessServiceException;

public class SiebelConnect {

	public static void main(String[] args) throws SiebelBusinessServiceException {

		SiebelPropertySet inputs = null;
		SiebelPropertySet outputs = null;
		SiebelPropertySet productRecord;
		boolean tns_found = false;
		String columnName = "", columnData = "";
		
		tns_found = setTnsAdmin();

		if (tns_found) {
			SqlProcessor _sqlProcessor = new SqlProcessor();
			
			inputs = new SiebelPropertySet();
			outputs = new SiebelPropertySet();
			
			//INSERT YOUR DATABASE DETAILS HERE
			inputs.setProperty("tnsNameEntry", "<tnsnames Entry>");
			inputs.setProperty("dbUser", "<Database Username>");
			inputs.setProperty("dbPassword", "<Database Password>");
					
			_sqlProcessor.doInvokeMethod("MethodName", inputs, outputs);
		
			if (outputs.getChildCount() > 0) {
				//Process Query Output
				for (int rowCounter = 0; rowCounter < outputs.getChildCount(); rowCounter++) {
					productRecord = outputs.getChild(rowCounter);
					@SuppressWarnings("unchecked")
					Enumeration<String> productRecordData = productRecord.getPropertyNames();
					while (productRecordData.hasMoreElements()) {
						columnName = productRecordData.nextElement();
						columnData = productRecord.getProperty(columnName);
					}
					System.out.println("The value of column " + columnName + " is " + columnData);
				}
			}
			
		}
		
	}

	public static boolean setTnsAdmin() {

		String tnsAdmin = System.getenv("TNS_ADMIN");

		if (tnsAdmin == null) {

			String oracleHome = System.getenv("ORACLE_HOME");

			if (oracleHome == null) {
				return false;
			}

			tnsAdmin = oracleHome + File.separatorChar + "network" + File.separatorChar + "admin";

		}

		System.setProperty("oracle.net.tns_admin", tnsAdmin);

		return true;

	}
	
}
