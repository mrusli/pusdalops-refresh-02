package mil.pusdalops.webui.siaga;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import mil.pusdalops.domain.authorization.User;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.pejabat.Pejabat;
import mil.pusdalops.domain.settings.Settings;
import mil.pusdalops.domain.siaga.PejabatSiaga;
import mil.pusdalops.persistence.pejabat.dao.PejabatDao;
import mil.pusdalops.persistence.pejabat.siaga.dao.PejabatSiagaDao;
import mil.pusdalops.persistence.settings.dao.SettingsDao;
import mil.pusdalops.persistence.user.dao.UserDao;
import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.security.UserSecurityDetails;

public class PejabatSiagaListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2806782017535903867L;

	private SettingsDao settingsDao;
	private PejabatSiagaDao pejabatSiagaDao;
	private PejabatDao pejabatDao;
	private UserDao userDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox pejabatSiagaListbox;
	private Window pejabatSiagaListInfoWin;
	
	private UserSecurityDetails userSecurityDetails;
	
	private Settings settings;
	private Kotamaops kotamaops;
	private ZoneId zoneId;
	private List<PejabatSiaga> pejabatSiagaList;
	
	// private final long SETTINGS_DEFAULT_ID = 1L;

	private final Logger log = Logger.getLogger(PejabatSiagaListInfoControl.class);
	
	public void onCreate$pejabatSiagaListInfoWin(Event event) throws Exception {
		log.info("create pejabatSiagaListInfoWin");
		
		setUserSecurityDetails(
				(UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		User loginUser = getUserSecurityDetails().getLoginUser();
		User loginUserByProxy = getUserDao().findUserKotamaopsByProxy(loginUser.getId());
		
		setKotamaops(
				loginUserByProxy.getKotamaops());

		// setSettings(
		//		getSettingsDao().findSettingsById(SETTINGS_DEFAULT_ID));
		// setKotamaops(
		//		getSettings().getSelectedKotamaops());
				
		// set the timezone for this kotamaops
		int timezoneOrdinal = getKotamaops().getTimeZone().getValue();
		setZoneId(getKotamaops().getTimeZone().toZoneId(timezoneOrdinal));

		formTitleLabel.setValue("Data Input | Pejabat Siaga - Kotamaops: "+
				getKotamaops().getKotamaopsName());
		
		// load data
		loadPejabatSiagaList();
		
		// display
		displayPejabatSiagaListInfo();
	}
	
	private void loadPejabatSiagaList() throws Exception {
		List<PejabatSiaga> pejabatSiagaAll = 
				getPejabatSiagaDao().findAllPejabatSiaga();
		 
		List<PejabatSiaga> pejabatSiagaKotamaops = new ArrayList<PejabatSiaga>();
		
		for (PejabatSiaga pejabatSiaga : pejabatSiagaAll) {
			PejabatSiaga pejabatSiagaPejabatByProxy = getPejabatSiagaDao().findPejabatSiagaPejabatByProxy(pejabatSiaga.getId());
			Pejabat pejabat = pejabatSiagaPejabatByProxy.getPejabat();
			
			Pejabat pejabatKotamaopsByProxy = getPejabatDao().findPejabatKotamaopsByProxy(pejabat.getId());
			Kotamaops kotamaops = pejabatKotamaopsByProxy.getKotamaops();
			
			if (kotamaops.getKotamaopsName().compareTo(getKotamaops().getKotamaopsName())==0) {
				pejabatSiagaKotamaops.add(pejabatSiaga);
			}
		}
		
		setPejabatSiagaList(pejabatSiagaKotamaops);
		//		getPejabatSiagaDao().findAllPejabatSiagaByKotamaops(getKotamaops()));
	}

	private void displayPejabatSiagaListInfo() {
		pejabatSiagaListbox.setModel(
				new ListModelList<PejabatSiaga>(getPejabatSiagaList()));
		pejabatSiagaListbox.setItemRenderer(
				getPejabatSiagaListitemRenderer());
	}

	private ListitemRenderer<PejabatSiaga> getPejabatSiagaListitemRenderer() {

		return new ListitemRenderer<PejabatSiaga>() {
			
			@Override
			public void render(Listitem item, PejabatSiaga pejabatSiaga, int index) throws Exception {
				Listcell lc;

				// ID
				lc = new Listcell(pejabatSiaga.getSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// TW Awal-Akhir
				// getLocalDateTimeString(asLocalDateTime(gelarOperasi.getTwAwalOps()), "YYYY")+"/"+

				lc = new Listcell(
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAwal(), getZoneId()), "YYYY")+"/"+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAwal(), getZoneId()), "MM")+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAwal(), getZoneId()), "dd")+"."+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAwal(), getZoneId()), "HH")+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAwal(), getZoneId()), "mm")+
						" - "+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAkhir(), getZoneId()), "YYYY")+"/"+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAkhir(), getZoneId()), "MM")+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAkhir(), getZoneId()), "dd")+"."+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAkhir(), getZoneId()), "HH")+
						getLocalDateTimeString(asLocalDateTime(pejabatSiaga.getTwSiagaAkhir(), getZoneId()), "mm")
						);
						//pejabatSiaga.getTwSiagaAwal().toString()+" - "+
						// pejabatSiaga.getTwSiagaAkhir().toString());
				lc.setParent(item);
				
				// Nama Pejabat
				PejabatSiaga pejabatSiagaKotamaopsByProxy = 
						getPejabatSiagaDao().findPejabatSiagaPejabatByProxy(pejabatSiaga.getId());
				lc = new Listcell(pejabatSiagaKotamaopsByProxy.getPejabat().getNama());
				lc.setParent(item);
				
				// Pangkat-Jabatan
				lc = new Listcell(pejabatSiagaKotamaopsByProxy.getPejabat().getPangkat()+" - "+
						pejabatSiagaKotamaopsByProxy.getPejabat().getJabatan());
				lc.setParent(item);
				
				// NRP
				lc = new Listcell(pejabatSiagaKotamaopsByProxy.getPejabat().getNrp());
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), pejabatSiaga);
				lc.setParent(item);

				item.setValue(pejabatSiaga);
			}

			private Listcell initEdit(Listcell listcell, PejabatSiaga pejabatSiaga) {
				Button button = new Button();
				button.setLabel("...");
				button.setSclass("listinfoEditButton");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						PejabatSiagaData pejabatSiagaData = new PejabatSiagaData();
						pejabatSiagaData.setPejabatSiaga(pejabatSiaga);
						pejabatSiagaData.setKotamaops(getKotamaops());
						Map<String, PejabatSiagaData> arg = Collections.singletonMap("pejabatSiagaData", pejabatSiagaData);
						Window pejabatSiagaDialogWin = 
								(Window) Executions.createComponents("/siaga/PejabatSiagaDialog.zul", pejabatSiagaListInfoWin, arg);
						pejabatSiagaDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								PejabatSiaga pejabatSiaga = (PejabatSiaga) event.getData();
												
								// save
								getPejabatSiagaDao().update(pejabatSiaga);
								
								// load
								loadPejabatSiagaList();
								
								// display
								displayPejabatSiagaListInfo();
							}
						});
						pejabatSiagaDialogWin.doModal(); 
					}
				});
				
				return listcell;
			}
		};
	}

	public void onAfterRender$pejabatSiagaListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+pejabatSiagaListbox.getItemCount()+" pejabat siaga");
	}
	
	public void onClick$newButton(Event event) throws Exception {
		PejabatSiagaData pejabatSiagaData = new PejabatSiagaData();
		pejabatSiagaData.setPejabatSiaga(new PejabatSiaga());
		pejabatSiagaData.setKotamaops(getKotamaops());
		
		Map<String, PejabatSiagaData> arg = Collections.singletonMap("pejabatSiagaData", pejabatSiagaData);
		Window pejabatSiagaDialogWin = 
				(Window) Executions.createComponents("/siaga/PejabatSiagaDialog.zul", pejabatSiagaListInfoWin, arg);
		pejabatSiagaDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				PejabatSiaga pejabatSiaga = (PejabatSiaga) event.getData();
								
				// save
				getPejabatSiagaDao().save(pejabatSiaga);
				
				// load
				loadPejabatSiagaList();
				
				// display
				displayPejabatSiagaListInfo();
			}
		});
		pejabatSiagaDialogWin.doModal(); 
	}
	
	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Kotamaops getKotamaops() {
		return kotamaops;
	}

	public void setKotamaops(Kotamaops kotamaops) {
		this.kotamaops = kotamaops;
	}

	public List<PejabatSiaga> getPejabatSiagaList() {
		return pejabatSiagaList;
	}

	public void setPejabatSiagaList(List<PejabatSiaga> pejabatSiagaList) {
		this.pejabatSiagaList = pejabatSiagaList;
	}

	public PejabatSiagaDao getPejabatSiagaDao() {
		return pejabatSiagaDao;
	}

	public void setPejabatSiagaDao(PejabatSiagaDao pejabatSiagaDao) {
		this.pejabatSiagaDao = pejabatSiagaDao;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public PejabatDao getPejabatDao() {
		return pejabatDao;
	}

	public void setPejabatDao(PejabatDao pejabatDao) {
		this.pejabatDao = pejabatDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserSecurityDetails getUserSecurityDetails() {
		return userSecurityDetails;
	}

	public void setUserSecurityDetails(UserSecurityDetails userSecurityDetails) {
		this.userSecurityDetails = userSecurityDetails;
	}	
}
