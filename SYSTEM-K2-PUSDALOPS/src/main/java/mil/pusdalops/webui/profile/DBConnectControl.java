package mil.pusdalops.webui.profile;

import java.io.FileInputStream;
import java.util.Properties;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import mil.pusdalops.persistence.kejadian.dao.KejadianDao;
import mil.pusdalops.webui.common.GFCBaseController;

public class DBConnectControl extends GFCBaseController { 

	private static final long serialVersionUID = 1L;

	private KejadianDao kejadianDao;
	
	private Label formTitleLabel, reIndexOutputLabel;
	private Textbox driverTextbox, urlTextbox, mysqlDialectTextbox, 
		indexBaseTextbox;
		// as of 2023 no more 'sinkronisasi' to cloud
		// , cloudDriverTextbox, cloudUrlTextbox, cloudMysqlDialectTextbox, 
		// cloudIndexBaseTextbox;
	private Properties mainProperties, cloudProperties;
	
	// private static final Logger log = Logger.getLogger(DBConnectControl.class);
	
	public void onCreate$dbConnectWin(Event event) throws Exception {
		formTitleLabel.setValue("Profil | Database Server");
		
		// setup the properties
		mainProperties = new Properties();
		mainProperties.load(new FileInputStream("/pusdalops/hibernate.properties"));
		
		// local db
		displayLocalDBConnectionData();

		cloudProperties = new Properties();
		cloudProperties.load(new FileInputStream("/pusdalops/hibernate-cloud.properties"));
		
		// cloud db
		displayCloudDBConnectionData();
	}


	private void displayLocalDBConnectionData() {
		driverTextbox.setValue(mainProperties.get("hibernate.connection.driver_class").toString());
		urlTextbox.setValue(mainProperties.get("hibernate.connection.url").toString());
		mysqlDialectTextbox.setValue(mainProperties.get("hibernate.dialect").toString());
		indexBaseTextbox.setValue(mainProperties.get("hibernate.search.default.indexBase").toString());
	}
	
	private void displayCloudDBConnectionData() {
		// cloudDriverTextbox.setValue(cloudProperties.get("hibernate.connection.driver_class").toString()); 
		// cloudUrlTextbox.setValue(cloudProperties.get("hibernate.connection.url").toString());
		// cloudMysqlDialectTextbox.setValue(cloudProperties.get("hibernate.dialect").toString());
		// cloudIndexBaseTextbox.setValue(cloudProperties.get("hibernate.search.default.indexBase").toString());
	}
	
	public void onClick$reIndexDBButton(Event event) throws Exception {
		getKejadianDao().createIndexer();
		reIndexOutputLabel.setValue("Re-Index Database Completed.");
	}

	public KejadianDao getKejadianDao() {
		return kejadianDao;
	}

	public void setKejadianDao(KejadianDao kejadianDao) {
		this.kejadianDao = kejadianDao;
	}
	
}
