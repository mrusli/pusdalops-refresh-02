package mil.pusdalops.webui.laporanrutin;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.common.PageHandler;

public class LaporanRutinPrintA4Control extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296326699748097371L;

	private Iframe iframe;
	private PageHandler handler= new PageHandler();
	
	private List<KotamaopsKejadianPrintData> kotamaopsKejadianList;
	
	private static final Logger log = Logger.getLogger(LaporanRutinPrintA4Control.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// kotamaopsKejadianList
		kotamaopsKejadianList = (List<KotamaopsKejadianPrintData>) arg.get("kotamaopsKejadianList");
	}

	public void onCreate$laporanRutinPrintA4Win(Event event) throws Exception {
		log.info("generateReportAMedia...");
		
		kotamaopsKejadianList.forEach(k -> log.info(k.toString()));
		
		HashMap<String, Object> dataField = new HashMap<String, Object>();
		HashMap<String, Object> dataList = new HashMap<String, Object>();

		dataField.put("kotamaopsKejadian", kotamaopsKejadianList);
		
		iframe.setContent(handler.generateReportAMedia(dataField, dataList, 
				"/laporanrutin/jasper/Laporan_Rutin_A4.jasper", "Laporan_Rutin_A4"));

	}
	
}
