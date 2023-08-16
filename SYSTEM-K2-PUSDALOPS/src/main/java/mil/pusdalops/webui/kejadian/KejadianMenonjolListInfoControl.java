package mil.pusdalops.webui.kejadian;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import mil.pusdalops.domain.authorization.User;
import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kerugian.Pihak;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.domain.settings.Settings;
import mil.pusdalops.domain.wilayah.Propinsi;
import mil.pusdalops.persistence.kejadian.dao.KejadianDao;
import mil.pusdalops.persistence.kotamaops.dao.KotamaopsDao;
import mil.pusdalops.persistence.settings.dao.SettingsDao;
import mil.pusdalops.persistence.user.dao.UserDao;
import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.security.UserSecurityDetails;

public class KejadianMenonjolListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8339162966385303228L;

	private SettingsDao settingsDao;
	private KejadianDao kejadianDao;
	private KotamaopsDao kotamaopsDao;
	private UserDao userDao;
	
	private Window kejadianMenonjolListInfoWin;
	private Label formTitleLabel, infoResultlabel;
	private Listbox kejadianListbox;
	private Combobox matraCombobox, yearCombobox, sortCombobox;
	private Textbox searchIdTextbox;
	
	private UserSecurityDetails userSecurityDetails;
	
	private Settings settings;
	private Kotamaops kotamaops;
	private KotamaopsType kotamaopsType;
	private List<Kejadian> kejadianList;
	
	// private final long SETTINGS_DEFAULT_ID = 1L;
	private static final Logger log = Logger.getLogger(KejadianMenonjolListInfoControl.class);
	
	public void onCreate$kejadianMenonjolListInfoWin(Event event) throws Exception {
		log.info("Creating KejadianMenonjolListInfo Control...");
		
		setUserSecurityDetails(
				(UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		User loginUser = getUserSecurityDetails().getLoginUser();
		User loginUserByProxy = getUserDao().findUserKotamaopsByProxy(loginUser.getId());
		
		setKotamaops(
				loginUserByProxy.getKotamaops());
		setKotamaopsType(
				loginUserByProxy.getKotamaops().getKotamaopsType());

		// setSettings(
		//		getSettingsDao().findSettingsById(SETTINGS_DEFAULT_ID));
		// setKotamaops(
		//		getSettings().getSelectedKotamaops());
		// setKotamaopsType(
		//		getSettings().getKotamaopsType());
		
		formTitleLabel.setValue("Data Input | Kejadian Menonjol - Kotamaops / Angkatan: "+
				getKotamaops().getKotamaopsName());

		// load
		loadMatraCombobox();

		// load year
		loadYearCombobox();
		
		// load kejadian list
		loadKejadianByMatraYear();
		
		// sort field
		loadSortFields();
		
		// display
		displayKejadianListInfo();
	}

	private void loadMatraCombobox() {
		Comboitem comboitem;
		if (getKotamaopsType().equals(KotamaopsType.PUSDALOPS)) {
			// semua
			comboitem = new Comboitem();
			comboitem.setLabel("--Semua Matra--");
			comboitem.setValue(null);
			comboitem.setParent(matraCombobox);
			// matra
			for (KotamaopsType kotamaopsMatraType : KotamaopsType.values()) {
				if ((kotamaopsMatraType.equals(KotamaopsType.PUSDALOPS)) || (kotamaopsMatraType.equals(KotamaopsType.LAIN_LAIN))) {
					// do nothing
				} else {
					comboitem = new Comboitem();
					comboitem.setLabel(kotamaopsMatraType.toString());
					comboitem.setValue(kotamaopsMatraType);
					comboitem.setParent(matraCombobox);
				}
			}
			matraCombobox.setSelectedIndex(0);			
		} else {
			KotamaopsType kotamaopsMatraType = getKotamaops().getKotamaopsType();
			// display kotamaops matra
			comboitem = new Comboitem();
			comboitem.setLabel(kotamaopsMatraType.toString());
			comboitem.setValue(kotamaopsMatraType);
			comboitem.setParent(matraCombobox);
			// set selected
			matraCombobox.setSelectedItem(comboitem);
			// disable
			matraCombobox.setDisabled(true);
		}
	}

	private void loadYearCombobox() throws Exception {
		List<Integer> yearList = getKejadianDao().findDistinctYearKejadian();
		
		Comboitem comboitem;
		for (Integer year : yearList) {
			comboitem = new Comboitem();
			comboitem.setLabel(String.valueOf(year));
			comboitem.setValue(year);
			comboitem.setParent(yearCombobox);
		}
		
		yearCombobox.setSelectedIndex(0);
	}

	public void onSelect$yearCombobox(Event event) throws Exception {
		loadKejadianByMatraYear();
		
		displayKejadianListInfo();
	}
	
	public void onSelect$matraCombobox(Event event) throws Exception {
		if (matraCombobox.getSelectedItem().getValue()==null) {
			// semua
			loadAllKejadian();		
		} else {
			// display selected matra
			KotamaopsType selKotamaopsMatraType = matraCombobox.getSelectedItem().getValue();

			// check the kotamaops under Pusat / Angkatan
			Kotamaops kotamaopsKotamaopsByProxy = 
					getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList = kotamaopsKotamaopsByProxy.getKotamaops();

			int defaultYear = yearCombobox.getSelectedItem().getValue();
			
			setKejadianList(
					getKejadianDao().findAllKejadianInKotamaopsByMatraType(true, defaultYear, kotamaopsList, selKotamaopsMatraType));
		}
		displayKejadianListInfo();
	}

	private void loadAllKejadian() throws Exception {
		if (getKotamaops().getKotamaopsType().compareTo(KotamaopsType.PUSDALOPS)==0) {
			// kejadian in which kotamaops? -- depending on the kotamaops listed in Kotamaops-Propinsi menu
			Kotamaops kotamaopsKotamaopsByProxy = 
					getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList = kotamaopsKotamaopsByProxy.getKotamaops();
			
			kotamaopsList.forEach(kotamaops->log.info(kotamaops.toString()));
			
			// get the selected year in combobox
			int defaultYear = yearCombobox.getSelectedItem().getValue();
						
			setKejadianList(
					getKejadianDao().findAllKejadianInKotamaops(true, defaultYear, kotamaopsList));
		} else {
			// display kejadian for this kotamaops only
			// by ascending order, newest kejadian first in the list
			Kotamaops kotamaopsPropinsiByProxy =
					getKotamaopsDao().findKotamaopsPropinsiByProxy(getKotamaops().getId());
			List<Propinsi> propinsiList = kotamaopsPropinsiByProxy.getPropinsis();
			
			setKejadianList(
					getKejadianDao().findAllKejadianInPropinsisByKotamaops(true, getKotamaops(), propinsiList));
		}
		
	}
	
	private void loadKejadianByMatraYear() throws Exception {
		// get the selected year in combobox
		int defaultYear = yearCombobox.getSelectedItem().getValue();

		if (getKotamaops().getKotamaopsType().compareTo(KotamaopsType.PUSDALOPS)==0) {
			// kejadian in which kotamaops? -- depending on the kotamaops listed in Kotamaops-Propinsi menu
			Kotamaops kotamaopsKotamaopsByProxy = 
					getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList = kotamaopsKotamaopsByProxy.getKotamaops();
			
			// kotamaopsList.forEach(kotamaops->log.info(kotamaops.toString()));
			
			// get the selected matra in combobox
			KotamaopsType selKotamaopsMatraType = matraCombobox.getSelectedItem().getValue();
			
			if (selKotamaopsMatraType==null) {
				log.info("matra not selected");
				setKejadianList(
						getKejadianDao().findAllKejadianInKotamaops(true, defaultYear, kotamaopsList));				
			} else {
				setKejadianList(
						getKejadianDao().findAllKejadianInKotamaopsByMatraType(true, defaultYear, kotamaopsList, selKotamaopsMatraType));
			}
			
		} else {
			// check whether this kotamaops / angkatan has any kotamaops
			log.info("check whether this kotamaops / angkatan has any kotamaops");
			
			// Kotamaops kotamaopsKotamaopsByProxy = getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList = new ArrayList<Kotamaops>();
			kotamaopsList.add(getKotamaops());
					// kotamaopsKotamaopsByProxy.getKotamaops();
			
			// if (kotamaopsList.isEmpty()) {
			//	log.info("kotamaops is empty - find all the propinsis");
				// find all kejadian in propinsi by kotamaops
			//	Kotamaops kotamaopsPropinsiByProxy =
			//			getKotamaopsDao().findKotamaopsPropinsiByProxy(getKotamaops().getId());
			//	List<Propinsi> propinsiList = kotamaopsPropinsiByProxy.getPropinsis();
				
			//	setKejadianList(
			//			getKejadianDao().findAllKejadianInPropinsisByKotamaops(true, getKotamaops(), propinsiList));				
			//} else {
				log.info("kotamaops: ");
				kotamaopsList.forEach(kotamaops -> log.info(kotamaops.toString()));
				// find all kejadian in each kotamaops
				setKejadianList(
						getKejadianDao().findAllKejadianInKotamaops(true, defaultYear, kotamaopsList));
				
			// }
			
		}
	}

	private void loadSortFields() {
		String[] fields = { "ID", "TW Kejadian", "TW Pembuatan" };
		
		Comboitem comboitem;
		for (String field : fields) {
			comboitem = new Comboitem();
			comboitem.setLabel(field.toString());
			comboitem.setValue(field);
			comboitem.setParent(sortCombobox);
		}
		
		// default selected sort 'ID' field
		getKejadianList().sort((o1, o2) -> {
			return o1.getSerialNumber().getSerialComp().compareTo(o2.getSerialNumber().getSerialComp());
		});
	}	
	
	public void onSelect$sortCombobox(Event event) throws Exception {
		int id = sortCombobox.getSelectedIndex();
		switch (id) {
		case 0:
			// 'ID'
			getKejadianList().sort((o1, o2) -> {
				return o1.getSerialNumber().getSerialComp().compareTo(o2.getSerialNumber().getSerialComp());
			});
			break;
		case 1:
			// 'TW Kejadian'
			getKejadianList().sort((o1, o2) -> {
				return o1.getTwKejadianDateTime().compareTo(o2.getTwKejadianDateTime());
			});
			break;
		case 2:
			// 'TW Pembuatan'
			getKejadianList().sort((o1, o2) -> {
				return o1.getTwPembuatanDateTime().compareTo(o2.getTwPembuatanDateTime());
			});			
			break;
		default:
			break;
		}
		
		displayKejadianListInfo();
	}
	
	public void onClick$searchIdButton(Event event) throws Exception {
		log.info("Search Id Kejadian");				
		if (searchIdTextbox.getValue().isEmpty()) {
			return;
		}
		
		// getKejadianDao().createIndexer();
		
		// search using 'wildcard'
		setKejadianList(
				getKejadianDao().searchKejadianId(searchIdTextbox.getValue()));
		
		// reset 'Tahun' to blank
		yearCombobox.setValue("");
		yearCombobox.setSelectedItem(null);
		// reset the sort combobox to 'ID'
		sortCombobox.setSelectedIndex(0);
		// sort by ID
		getKejadianList().sort((o1, o2) -> {
			return o1.getSerialNumber().getSerialComp().compareTo(o2.getSerialNumber().getSerialComp());
		});		
		// display
		displayKejadianListInfo();
	}
	
	private void displayKejadianListInfo() {
		kejadianListbox.setModel(
				new ListModelList<Kejadian>(getKejadianList()));
		kejadianListbox.setItemRenderer(getKejadianListitemRenderer());
	}

	private ListitemRenderer<Kejadian> getKejadianListitemRenderer() {
		
		return new ListitemRenderer<Kejadian>() {
			
			@Override
			public void render(Listitem item, Kejadian kejadian, int index) throws Exception {
				Listcell lc;
				
				item.setSclass("autopaging-content-kejadian-menonjol");
				
				// ID
				lc = new Listcell(kejadian.getSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// TW Kejadian
				lc = new Listcell(kejadian.getTwKejadianDateTime().toString()+" "+kejadian.getTwKejadianTimezone().toString());
				lc.setParent(item);
				
				// TW Pembuatan
				lc = new Listcell(kejadian.getTwPembuatanDateTime().toString()+" "+kejadian.getTwKejadianTimezone().toString());
				lc.setParent(item);
				
				// Kotamops
				Kejadian kejadianKotamaopsByProxy = getKejadianDao().findKejadianKotamaopsByProxy(kejadian.getId());
				lc = new Listcell(kejadianKotamaopsByProxy.getKotamaops().getKotamaopsName());
				lc.setParent(item);
				
				// Propinsi
				Kejadian kejadianPropinsiByProxy = getKejadianDao().findKejadianPropinsiByProxy(kejadian.getId());
				lc = new Listcell(kejadianPropinsiByProxy.getPropinsi().getNamaPropinsi());
				lc.setParent(item);
				
				// Jenis
				String namaJenisKejadian = kejadian.getJenisKejadian()==null ? 
						"" :  kejadian.getJenisKejadian().getNamaJenis();
				lc = new Listcell(namaJenisKejadian);
				lc.setParent(item);

				// Motif
				String namaMotifKejadian = kejadian.getMotifKejadian()==null ?
						"" : kejadian.getMotifKejadian().getNamaMotif();
				lc = new Listcell(namaMotifKejadian);
				lc.setParent(item);
								
				// edit
				lc = initEdit(new Listcell(), kejadian);
				lc.setParent(item);

				// delete
				lc = initDelete(new Listcell(), kejadian);
				lc.setParent(item);
				
				// Kerugian
				lc = initKerugian(new Listcell(), kejadian);
				lc.setParent(item);
				
				// Sinkronisasi
				lc = new Listcell(kejadian.getSynchAt()==null? " " : kejadian.getSynchAt().toString());
				lc.setParent(item);
				
				item.setValue(kejadian);
			}

			private Listcell initKerugian(Listcell listcell, Kejadian kejadian) {
				Hlayout hlayout = new Hlayout();
				// PIHAK_KITA
				Button phkKitaButton = new Button();
				phkKitaButton.setLabel("Pihak Kita");
				phkKitaButton.setClass("phkKitaEditButton");
				phkKitaButton.setParent(hlayout);
				phkKitaButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						KejadianData kejadianData = new KejadianData();
						kejadianData.setParaPihak(Pihak.KITA);
						kejadianData.setKejadian(kejadian);
						
						Map<String, KejadianData> args = Collections.singletonMap("kejadianData", kejadianData);
						
						Window kerugianDialogWin = 
								(Window) Executions.createComponents(
										"/kejadian/KerugianListInfo.zul", kejadianMenonjolListInfoWin, args);
						
						kerugianDialogWin.doModal();
					}

				});
				// PIHAK MUSUH
				Button phkMusuhButton = new Button();
				phkMusuhButton.setLabel("Pihak Musuh");
				phkMusuhButton.setClass("phkMusuhEditButton");
				phkMusuhButton.setParent(hlayout);
				phkMusuhButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						KejadianData kejadianData = new KejadianData();
						kejadianData.setParaPihak(Pihak.MUSUH);
						kejadianData.setKejadian(kejadian);
						
						Map<String, KejadianData> args = Collections.singletonMap("kejadianData", kejadianData);
						
						Window kerugianDialogWin = 
								(Window) Executions.createComponents(
										"/kejadian/KerugianListInfo.zul", kejadianMenonjolListInfoWin, args);
						
						kerugianDialogWin.doModal();
						
					}
				});
				// PIHAK LAIN-LAIN
				Button phkLainButton = new Button();
				phkLainButton.setLabel("Pihak Lain");
				phkLainButton.setClass("phkLainEditButton");
				phkLainButton.setParent(hlayout);
				phkLainButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						KejadianData kejadianData = new KejadianData();
						kejadianData.setParaPihak(Pihak.LAIN_LAIN);
						kejadianData.setKejadian(kejadian);
						
						Map<String, KejadianData> args = Collections.singletonMap("kejadianData", kejadianData);
						
						Window kerugianDialogWin = 
								(Window) Executions.createComponents(
										"/kejadian/KerugianListInfo.zul", kejadianMenonjolListInfoWin, args);
						
						kerugianDialogWin.doModal();
						
					}
				});

				hlayout.setParent(listcell);
				
				return listcell;
			}

			private Listcell initEdit(Listcell listcell, Kejadian kejadian) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setClass("listinfoEditButton");
				editButton.setParent(listcell);
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						KejadianData kejadianData = new KejadianData();
						kejadianData.setKejadian(kejadian);
						kejadianData.setSettingsKotamaops(getKotamaops());
						kejadianData.setKotamaopsType(getKotamaopsType());
						
						Map<String, KejadianData> args = Collections.singletonMap("kejadianData", kejadianData);
						Window kejadianDialogWin = (Window) Executions.createComponents(
								"/kejadian/KejadianMenonjolDialog.zul", kejadianMenonjolListInfoWin, args);
						kejadianDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Kejadian kejadian = (Kejadian) event.getData();
								
								// update
								getKejadianDao().update(kejadian);
								
								// re-index
								// getKejadianDao().createIndexer();
								
								// load
								loadKejadianByMatraYear();
								
								// display
								displayKejadianListInfo();
							}
						});
						
						kejadianDialogWin.doModal();
					}
				});
				
				return listcell;
			}
			
			private Listcell initDelete(Listcell listcell, Kejadian kejadian) {
				Button button = new Button();
				button.setIconSclass("z-icon-minus-square");
				button.setClass("listinfoDeleteButton");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Delete");
						// asking for confirmation
						Messagebox.show("Hapus Kejadian Ini?",
							    "Confirmation", 
							    Messagebox.YES | Messagebox.CANCEL,  
							    Messagebox.QUESTION, new EventListener<Event>() {
									
									@Override
									public void onEvent(Event event) throws Exception {
										log.info(event.getName());
										if (Messagebox.ON_YES.equals(event.getName())) {
											log.info("confirmed delete : "+kejadian.toString());
											
											// delete
											getKejadianDao().delete(kejadian);
											
											// re-index
											// getKejadianDao().createIndexer();
											
											// load
											loadKejadianByMatraYear();
											
											// display
											displayKejadianListInfo();											
										} else {
											log.info("cancel delete...");
										}
									}
								});
						
					}
				});
				return listcell;
			}

		};
	}

	public void onAfterRender$kejadianListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: " + kejadianListbox.getItemCount() + " kejadian");
	}
	
	public void onClick$newButton(Event event) throws Exception {
		KejadianData kejadianData = new KejadianData();
		kejadianData.setKejadian(new Kejadian());
		kejadianData.setSettingsKotamaops(getKotamaops());
		kejadianData.setKotamaopsType(getKotamaopsType());
		
		Map<String, KejadianData> args = Collections.singletonMap("kejadianData", kejadianData);
		Window kejadianDialogWin = (Window) Executions.createComponents(
				"/kejadian/KejadianMenonjolDialog.zul", kejadianMenonjolListInfoWin, args);
		kejadianDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Kejadian kejadian = (Kejadian) event.getData();
				
				// save
				getKejadianDao().save(kejadian);
				
				// re-index
				// getKejadianDao().createIndexer();
				
				// load
				loadKejadianByMatraYear();
				
				// display
				displayKejadianListInfo();
			}
		});
		kejadianDialogWin.doModal();
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

	public KejadianDao getKejadianDao() {
		return kejadianDao;
	}

	public void setKejadianDao(KejadianDao kejadianDao) {
		this.kejadianDao = kejadianDao;
	}

	public List<Kejadian> getKejadianList() {
		return kejadianList;
	}

	public void setKejadianList(List<Kejadian> kejadianList) {
		this.kejadianList = kejadianList;
	}

	public KotamaopsDao getKotamaopsDao() {
		return kotamaopsDao;
	}

	public void setKotamaopsDao(KotamaopsDao kotamaopsDao) {
		this.kotamaopsDao = kotamaopsDao;
	}

	public KotamaopsType getKotamaopsType() {
		return kotamaopsType;
	}

	public void setKotamaopsType(KotamaopsType kotamaopsType) {
		this.kotamaopsType = kotamaopsType;
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
