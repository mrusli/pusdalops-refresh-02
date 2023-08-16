package mil.pusdalops.webui.kejadian;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import mil.pusdalops.domain.authorization.User;
import mil.pusdalops.domain.gmt.TimezoneInd;
import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kejadian.KejadianJenis;
import mil.pusdalops.domain.kejadian.KejadianMotif;
import mil.pusdalops.domain.kejadian.KejadianPelaku;
import mil.pusdalops.domain.kerugian.Kerugian;
import mil.pusdalops.domain.kerugian.KerugianJenis;
import mil.pusdalops.domain.kerugian.KerugianKondisi;
import mil.pusdalops.domain.kerugian.KerugianSatuan;
import mil.pusdalops.domain.kerugian.Lembaga;
import mil.pusdalops.domain.kerugian.Pihak;
import mil.pusdalops.domain.kerugian.TipeKerugian;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.domain.serial.DocumentSerialNumber;
import mil.pusdalops.domain.settings.Settings;
import mil.pusdalops.domain.wilayah.Kabupaten_Kotamadya;
import mil.pusdalops.domain.wilayah.Kecamatan;
import mil.pusdalops.domain.wilayah.Kelurahan;
import mil.pusdalops.domain.wilayah.Propinsi;
import mil.pusdalops.persistence.kabupaten_kotamadya.dao.Kabupaten_KotamadyaDao;
import mil.pusdalops.persistence.kecamatan.dao.KecamatanDao;
import mil.pusdalops.persistence.kejadian.dao.KejadianDao;
import mil.pusdalops.persistence.kejadian.jenis.dao.KejadianJenisDao;
import mil.pusdalops.persistence.kejadian.motif.dao.KejadianMotifDao;
import mil.pusdalops.persistence.kejadian.pelaku.dao.KejadianPelakuDao;
import mil.pusdalops.persistence.kelurahan.dao.KelurahanDao;
import mil.pusdalops.persistence.kerugian.jenis.dao.KerugianJenisDao;
import mil.pusdalops.persistence.kerugian.kondisi.dao.KerugianKondisiDao;
import mil.pusdalops.persistence.kerugian.satuan.dao.KerugianSatuanDao;
import mil.pusdalops.persistence.kotamaops.dao.KotamaopsDao;
import mil.pusdalops.persistence.propinsi.dao.PropinsiDao;
import mil.pusdalops.persistence.settings.dao.SettingsDao;
import mil.pusdalops.persistence.user.dao.UserDao;
import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.common.SerialNumberGenerator;
import mil.pusdalops.webui.common.SuppressedException;
import mil.pusdalops.webui.common.TwConversion;
import mil.pusdalops.webui.dialogs.DatetimeData;
import mil.pusdalops.webui.security.UserSecurityDetails;

public class KejadianMenonjolWinControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2956545191197992134L;

	private SettingsDao settingsDao;
	private KejadianDao kejadianDao;
	private KotamaopsDao kotamaopsDao;
	private KejadianJenisDao kejadianJenisDao;
	private KejadianMotifDao kejadianMotifDao;
	private KejadianPelakuDao kejadianPelakuDao;	
	private SerialNumberGenerator serialNumberGenerator;
	private TwConversion twConversion;
	private PropinsiDao propinsiDao;
	private Kabupaten_KotamadyaDao kabupaten_KotamadyaDao;
	private KecamatanDao kecamatanDao;
	private KelurahanDao kelurahanDao;
	private KerugianJenisDao kerugianJenisDao;
	private KerugianKondisiDao kerugianKondisiDao;
	private KerugianSatuanDao kerugianSatuanDao;
	private UserDao userDao;
	
	private Window kejadianMenonjolWin;
	private Label formTitleLabel, kerugian0CaptionLabel, kerugian1CaptionLabel, kerugian2CaptionLabel,
		newKejadianLabel, splitLabel, editKejadianLabel, saveKejadianLabel, idLabel, cancelKejadianLabel;
	private Checkbox kerugian0Checkbox, kerugian1Checkbox, kerugian2Checkbox;
	private Textbox namaPersMat0Textbox, namaPersMat1Textbox, namaPersMat2Textbox, kejadianIdTextbox,
		keterangan0Textbox, keterangan1Textbox, keterangan2Textbox, searchIdTextbox, searchWordTextbox,
		twBuatTahunTextbox, twBuatTanggalJamTextbox, twJadiTahunTextbox, twJadiTanggalJamTextbox, twJadiTimeZoneTextbox,
		koordGpsTextbox, koordPetaTextbox, bujurLintangTextbox, kampungTextbox, jalanTextbox, keteranganPelakuTextbox, 
		sasaranTextbox, kronologisTextbox, twAwalTahunTextbox, twAwalTanggalJamTextbox, twAwalTimeZoneTextbox,
		twAkhirTahunTextbox, twAkhirTanggalJamTextbox, twAkhirTimeZoneTextbox;
	private Combobox twBuatTimeZoneCombobox, kotamaopsCombobox, propCombobox, kabupatenCombobox, kecamatanCombobox,
		kelurahanCombobox, jenisKejadianCombobox, motifKejadianCombobox, pelakuKejadianCombobox, 
		tipeKerugian0Combobox, lembaga0Combobox, kerugianJenis0Combobox, kondisi0Combobox, satuan0Combobox,
		tipeKerugian1Combobox, lembaga1Combobox, kerugianJenis1Combobox, kondisi1Combobox, satuan1Combobox, 
		tipeKerugian2Combobox, lembaga2Combobox, kerugianJenis2Combobox, kondisi2Combobox, satuan2Combobox;
	private Intbox jumlah0Intbox, jumlah1Intbox, jumlah2Intbox;
	private Button twJadiRubahButton,
		kerugian0AddButton, kerugian0DeleteButton, kerugian0EditButton, kerugian0SaveButton, kerugian0PrevButton, kerugian0NextButton,
		kerugian1AddButton, kerugian1DeleteButton, kerugian1EditButton, kerugian1SaveButton, kerugian1PrevButton, kerugian1NextButton,
		kerugian2AddButton, kerugian2DeleteButton, kerugian2EditButton, kerugian2SaveButton, kerugian2PrevButton, kerugian2NextButton;
	private Listbox searchResultListbox;
	private Radiogroup twRadioGroup;
	
	private State state, stateKerugian0, stateKerugian1, stateKerugian2;
	private Settings settings;
	private Kejadian kejadian;
	private Kotamaops kotamaops;
	private KotamaopsType kotamaopsType;
	private ZoneId zoneId;
	private LocalDateTime currentLocalDateTime;
	private DocumentSerialNumber documentSerialNumber;
	private List<Kerugian> kerugian0List = new ArrayList<Kerugian>();
	private List<Kerugian> kerugian1List = new ArrayList<Kerugian>();
	private List<Kerugian> kerugian2List = new ArrayList<Kerugian>();
	private int kr0CurrIndex = 0;
	private int kr1CurrIndex = 0;
	private int kr2CurrIndex = 0;
	
	private UserSecurityDetails userSecurityDetails;
	
	// private Kotamaops selectedKotamaops;
	private Propinsi selectedPropinsi;
	private Kabupaten_Kotamadya selectedKabupatenKot;
	private Kecamatan selectedKecamatan;
	
	// private final long 		SETTINGS_DEFAULT_ID = 1L;
	private final String 	NO_INFO 			= "-Tidak Ada Info-";
	
	private static final Logger log = Logger.getLogger(KejadianMenonjolWinControl.class);
	
	public void onCreate$kejadianMenonjolWin(Event event) throws Exception {
		log.info("onCreate$kejadianMenonojolWin()");
		
		setUserSecurityDetails(
				(UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		User loginUser = getUserSecurityDetails().getLoginUser();
		User loginUserByProxy = getUserDao().findUserKotamaopsByProxy(loginUser.getId());
		
		setKotamaops(
				loginUserByProxy.getKotamaops());
		setKotamaopsType(
				loginUserByProxy.getKotamaops().getKotamaopsType());

		// Kotamaops kotamaops = loginUserByProxy.getKotamaops();	
		// String username = getUserSecurityDetails().getLoginUser().getUserName();		
		// setSettings(
		//		getSettingsDao().findSettingsById(SETTINGS_DEFAULT_ID));		
		// setKotamaops(
		//		getSettings().getSelectedKotamaops());
		// setKotamaopsType(
		//		getSettings().getKotamaopsType());
		
		formTitleLabel.setValue("Data Input | Kejadian Menonjol - Kronologis dan Kerugian - Kotamaops / Angkatan : "+
				getKotamaops().getKotamaopsName() + " - ");
				
		// set the timezone for this kotamaops
		int timezoneOrdinal = getKotamaops().getTimeZone().ordinal();
		setZoneId(getKotamaops().getTimeZone().toZoneId(timezoneOrdinal));			
		// current datetime -- according to time zone (not just id)
		setCurrentLocalDateTime(getLocalDateTime(getZoneId()));		
		
		// load zonawaktu twBuatTimeZoneCombobox
		loadTwBuatZonaWaktuCombobox();
		// load kotamaopsCombobox
		loadKotamaopsCombobox();
		// load jenis kejadian
		loadJenisKejadianCombobox();
		// load motif kejadian
		loadMotifKejadianCombobox();
		// load pelaku kejadian
		loadPelakuKejadianCombobox();				
		
		// load kerugian comboboxes
		loadKerugianComboboxes();
		
		setState(State.EMPTY);
	}
	

	private void loadTwBuatZonaWaktuCombobox() {
		Comboitem comboitem;
		
		for (TimezoneInd timezoneInd : TimezoneInd.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(timezoneInd.toString());
			comboitem.setValue(timezoneInd);
			comboitem.setParent(twBuatTimeZoneCombobox);
		}	
	}

	private void loadKotamaopsCombobox() throws Exception {
		if (getKotamaopsType().compareTo(KotamaopsType.PUSDALOPS)==0) {
			log.info("load all kotamatops...");
			// populate the combobox with all the kotamaops
			Kotamaops kotamaopsByProxy = 
					getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList = kotamaopsByProxy.getKotamaops();

			// 23/11/2021 : sort no longer needed because in the Kotamaops entity class
			// has '@orderBy("kotamaopsSequence") when the list of Kotamaops is picked up
			//
			// kotamaopsList.sort((o1, o2) -> {
			//	return Integer.compare(o1.getKotamaopsSequence(), o2.getKotamaopsSequence());
			// });
			
			Comboitem comboitem;
			for (Kotamaops kotamaops : kotamaopsList) {
				comboitem = new Comboitem();
				comboitem.setLabel(kotamaops.getKotamaopsName());
				comboitem.setValue(kotamaops);
				comboitem.setParent(kotamaopsCombobox);
			}
			
		} else {
			// matra
			log.info("matra: "+getKotamaopsType().name());
			
			// kotamaops name
			log.info("kotamaops: "+getKotamaops().getKotamaopsName());
			
			Kotamaops kotamaopsByProxy = 
					getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList =
					kotamaopsByProxy.getKotamaops();
			
			if (kotamaopsList.isEmpty()) {
				log.info("no kotamaops.  default to settingsKotamaops: "+getKotamaops().getKotamaopsName());

				// must create comboitem
				Comboitem comboitem = new Comboitem();
				comboitem.setLabel(getKotamaops().getKotamaopsName());
				comboitem.setValue(getKotamaops());
				comboitem.setParent(kotamaopsCombobox);
				// no more kotamaops selection
				kotamaopsCombobox.setSelectedItem(comboitem);
				kotamaopsCombobox.setDisabled(true);
				
				// display the propinsi of the default kotamaops 
				Kotamaops kotamaopsPropinsiByProxy = 
						getKotamaopsDao().findKotamaopsPropinsiByProxy(getKotamaops().getId());
				List<Propinsi> propinsiList = kotamaopsPropinsiByProxy.getPropinsis();
				
				for (Propinsi propinsi : propinsiList) {
					comboitem = new Comboitem();
					comboitem.setLabel(propinsi.getNamaPropinsi());
					comboitem.setValue(propinsi);
					comboitem.setParent(propCombobox);
				}
			} else {				
				// 23/11/2021 : sort no longer needed because in the Kotamaops entity class
				// has '@orderBy("kotamaopsSequence") when the list of Kotamaops is picked up
				//
				//kotamaopsList.sort((o1, o2) -> {
				//	return Integer.compare(o1.getKotamaopsSequence(), o2.getKotamaopsSequence());
				//});
				
				log.info("Kotamaops:");
				kotamaopsList.forEach(kotamaops -> log.info(kotamaops.toString()));
				
				// create comboitem for each kotamaops
				Comboitem comboitem;
				for (Kotamaops kotamaops : kotamaopsList) {
					comboitem = new Comboitem();
					comboitem.setLabel(kotamaops.getKotamaopsName());
					comboitem.setValue(kotamaops);
					comboitem.setParent(kotamaopsCombobox);				
				}
			}			
		}		
	}

	private void loadJenisKejadianCombobox() throws Exception {
		List<KejadianJenis> kejadianJenisList = 
				getKejadianJenisDao().findAllKejadianJenisOrderBy(true);
		
		Comboitem comboitem;
		for (KejadianJenis kejadianJenis : kejadianJenisList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kejadianJenis.getNamaJenis());
			comboitem.setValue(kejadianJenis);
			comboitem.setParent(jenisKejadianCombobox);
		}
		// create comboitem to add to the jenis kejadian list
		// -- in case a new jenis kejadian not in the list
		comboitem = new Comboitem();
		comboitem.setLabel("Tambah Jenis Kejadian...");
		comboitem.setValue(null);
		comboitem.setParent(jenisKejadianCombobox);		
	}

	private void loadMotifKejadianCombobox() throws Exception {
		List<KejadianMotif> kejadianMotifList = 
				getKejadianMotifDao().findAllKejadianMotifOrderBy(true);
		
		Comboitem comboitem;
		for (KejadianMotif kejadianMotif : kejadianMotifList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kejadianMotif.getNamaMotif());
			comboitem.setValue(kejadianMotif);
			comboitem.setParent(motifKejadianCombobox);
		}
		// create comboitem to add to the motif kejadian list
		// -- in case a new motif kejadian not in the list
		comboitem = new Comboitem();
		comboitem.setLabel("Tambah Motif Kejadian...");
		comboitem.setValue(null);
		comboitem.setParent(motifKejadianCombobox);			
	}

	private void loadPelakuKejadianCombobox() throws Exception {
		List<KejadianPelaku> kejadianPelakuList = 
				getKejadianPelakuDao().findAllKejadianPelaku();
		
		Comboitem comboitem;
		for (KejadianPelaku kejadianPelaku : kejadianPelakuList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kejadianPelaku.getNamaPelaku());
			comboitem.setValue(kejadianPelaku);
			comboitem.setParent(pelakuKejadianCombobox);
		}	
	}

	private void loadKerugianComboboxes() throws Exception {
		Comboitem comboitem;
		
		// TipeKerugian
		for(TipeKerugian tipeKerugian : TipeKerugian.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(tipeKerugian.toString());
			comboitem.setValue(tipeKerugian);
			comboitem.setParent(tipeKerugian0Combobox);
			
			comboitem = new Comboitem();
			comboitem.setLabel(tipeKerugian.toString());
			comboitem.setValue(tipeKerugian);
			comboitem.setParent(tipeKerugian1Combobox);
			
			comboitem = new Comboitem();
			comboitem.setLabel(tipeKerugian.toString());
			comboitem.setValue(tipeKerugian);
			comboitem.setParent(tipeKerugian2Combobox);			
		}
		
		// Lembaga
		for(Lembaga lembaga : Lembaga.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(lembaga.toString());
			comboitem.setValue(lembaga);
			comboitem.setParent(lembaga0Combobox);

			comboitem = new Comboitem();
			comboitem.setLabel(lembaga.toString());
			comboitem.setValue(lembaga);
			comboitem.setParent(lembaga1Combobox);
			
			comboitem = new Comboitem();
			comboitem.setLabel(lembaga.toString());
			comboitem.setValue(lembaga);
			comboitem.setParent(lembaga2Combobox);
		}
		
		// KerugianJenis
		List<KerugianJenis> kerugianJenisList =	getKerugianJenisDao().findAllKerugianJenis(true);
		for(KerugianJenis kerugianJenis : kerugianJenisList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kerugianJenis.getNamaJenis());
			comboitem.setValue(kerugianJenis);
			comboitem.setParent(kerugianJenis0Combobox);
			
			comboitem = new Comboitem();
			comboitem.setLabel(kerugianJenis.getNamaJenis());
			comboitem.setValue(kerugianJenis);
			comboitem.setParent(kerugianJenis1Combobox);

			comboitem = new Comboitem();
			comboitem.setLabel(kerugianJenis.getNamaJenis());
			comboitem.setValue(kerugianJenis);
			comboitem.setParent(kerugianJenis2Combobox);
		}
		
		// KerugianKondisi
		List<KerugianKondisi> kerugianKondisiList = getKerugianKondisiDao().findAllKerugianKondisi(true);
		for(KerugianKondisi kerugianKondisi : kerugianKondisiList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kerugianKondisi.getNamaKondisi());
			comboitem.setValue(kerugianKondisi);
			comboitem.setParent(kondisi0Combobox);
			
			comboitem = new Comboitem();
			comboitem.setLabel(kerugianKondisi.getNamaKondisi());
			comboitem.setValue(kerugianKondisi);
			comboitem.setParent(kondisi1Combobox);

			comboitem = new Comboitem();
			comboitem.setLabel(kerugianKondisi.getNamaKondisi());
			comboitem.setValue(kerugianKondisi);
			comboitem.setParent(kondisi2Combobox);
		}
		
		// KerugianSatuan
		List<KerugianSatuan> kerugianSatuanList = getKerugianSatuanDao().findAllKerugianSatuan(true);
		for(KerugianSatuan kerugianSatuan : kerugianSatuanList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kerugianSatuan.getSatuan());
			comboitem.setValue(kerugianSatuan);
			comboitem.setParent(satuan0Combobox);
			
			comboitem = new Comboitem();
			comboitem.setLabel(kerugianSatuan.getSatuan());
			comboitem.setValue(kerugianSatuan);
			comboitem.setParent(satuan1Combobox);

			comboitem = new Comboitem();
			comboitem.setLabel(kerugianSatuan.getSatuan());
			comboitem.setValue(kerugianSatuan);
			comboitem.setParent(satuan2Combobox);
		}
	}
	
	
	public void onClick$twJadiRubahButton(Event event) throws Exception {
		log.info("Rubah TW Kejadian...");
		// create the data object
		DatetimeData datetimeData = new DatetimeData();
		// set the dialog title
		datetimeData.setDialogWinTitle("Rubah TW Kejadian");

		TimezoneInd selTimezoneInd = null;
		ZoneId zoneId = null;
		if (getState().equals(State.NEW)) {
			datetimeData.setTimezoneInd(getKotamaops().getTimeZone());
			selTimezoneInd = 
				(TimezoneInd) twJadiTimeZoneTextbox.getAttribute("twJadiTimeZoneInd");
			int timezoneIndOrdinal;
			zoneId = null;
			if (selTimezoneInd==null) {
				timezoneIndOrdinal = getKotamaops().getTimeZone().ordinal();
				zoneId = getKotamaops().getTimeZone().toZoneId(timezoneIndOrdinal);
			} else {
				timezoneIndOrdinal = selTimezoneInd.ordinal();
				zoneId = selTimezoneInd.toZoneId(timezoneIndOrdinal);
			}
		
			datetimeData.setZoneId(zoneId);
			datetimeData.setLocalDateTime(getCurrentLocalDateTime());			
		} else {
			selTimezoneInd = getKejadian().getTwKejadianTimezone();
			int timezoneIndOrdinal = selTimezoneInd.ordinal();
			zoneId = selTimezoneInd.toZoneId(timezoneIndOrdinal);
			
			datetimeData.setTimezoneInd(selTimezoneInd);
			datetimeData.setZoneId(zoneId);
			datetimeData.setLocalDateTime(asLocalDateTime(getKejadian().getTwKejadianDateTime(), zoneId));
		}
		
		Map<String, DatetimeData> args = Collections.singletonMap("datetimeData", datetimeData);
		Window datetimeWin = (Window) Executions.createComponents(
				"/dialogs/DatetimeWinDialog.zul", kejadianMenonjolWin, args);
		datetimeWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				DatetimeData datetimeData = (DatetimeData) event.getData();
				
				// year
				twJadiTahunTextbox.setValue(
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "yyyy"));			
				// mmdd.HHmm
				twJadiTanggalJamTextbox.setValue(					
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "MM")+
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "dd")+"."+
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "HH")+
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "mm"));
				// zona waktu
				twJadiTimeZoneTextbox.setValue(datetimeData.getTimezoneInd().toString());
				// save the object in the attribute
				twJadiTimeZoneTextbox.setAttribute("twJadiTimeZoneInd", datetimeData.getTimezoneInd());				
			}
		});
		datetimeWin.doModal();
	}
	
	public void onSelect$kotamaopsCombobox(Event event) throws Exception {	
		resetOtherComboboxes(4);
		
		// only for users in PUSDALOPS 
		Kotamaops selectedKotamaops = null;
		
		if (kotamaopsCombobox.getSelectedItem() != null) {
			selectedKotamaops = kotamaopsCombobox.getSelectedItem().getValue();
			// set timezone kejadian according to selected kotamaops
			twJadiTimeZoneTextbox.setValue(selectedKotamaops.getTimeZone().toString());
			twJadiTimeZoneTextbox.setAttribute("twJadiTimeZoneInd", selectedKotamaops.getTimeZone());
			
			loadPropinsiCombobox(selectedKotamaops);	
		}
	}
	
	private void loadPropinsiCombobox(Kotamaops selKotamaops) throws Exception {
		Kotamaops kotamaopsByProxy = 
				getKotamaopsDao().findKotamaopsPropinsiByProxy(selKotamaops.getId());
		List<Propinsi> propinsiList = kotamaopsByProxy.getPropinsis();
		
		// sort
		Collections.sort(propinsiList, new Comparator<Propinsi>() {

			@Override
			public int compare(Propinsi p1, Propinsi p2) {

				return p1.getNamaPropinsi().compareTo(p2.getNamaPropinsi());
			}
		});
		
		// reset
		propCombobox.getItems().clear();
		
		Comboitem comboitem;
		for (Propinsi propinsi : propinsiList) {
			comboitem = new Comboitem();
			comboitem.setLabel(propinsi.getNamaPropinsi());
			comboitem.setValue(propinsi);
			comboitem.setParent(propCombobox);
		}
	}

	public void onSelect$propCombobox(Event event) throws Exception {
		resetOtherComboboxes(3);
		// Propinsi selPropinsi = propCombobox.getSelectedItem().getValue();
		selectedPropinsi = propCombobox.getSelectedItem().getValue();
		
		loadKabupatenCombobox(selectedPropinsi);
	}
	
	
	private void loadKabupatenCombobox(Propinsi selPropinsi) throws Exception {
		Propinsi selPropinsiByProxy = 
				getPropinsiDao().findKabupatenKotamadyaByProxy(selPropinsi.getId());
		List<Kabupaten_Kotamadya> kabupaten_Kotamadyas = 
				selPropinsiByProxy.getKabupatenkotamadyas();
		
		// sort
		Collections.sort(kabupaten_Kotamadyas, new Comparator<Kabupaten_Kotamadya>() {

			@Override
			public int compare(Kabupaten_Kotamadya kabKot1, Kabupaten_Kotamadya kabKot2) {
				
				return kabKot1.getNamaKabupaten().compareTo(kabKot2.getNamaKabupaten());
			}
		});
		
		// reset
		kabupatenCombobox.getItems().clear();
		
		Comboitem comboitem;
		for (Kabupaten_Kotamadya kabupaten_Kotamadya : kabupaten_Kotamadyas) {
			comboitem = new Comboitem();
			comboitem.setLabel(kabupaten_Kotamadya.getNamaKabupaten());
			comboitem.setValue(kabupaten_Kotamadya);
			comboitem.setParent(kabupatenCombobox);
		}			
		// create comboitem to add to the kabupaten
		// -- in case the kabupaten not in the list
		comboitem = new Comboitem();
		comboitem.setLabel("Tambah Kabupaten/Kot...");
		comboitem.setValue(null);
		comboitem.setParent(kabupatenCombobox);		
	}

	public void onSelect$kabupatenCombobox(Event event) throws Exception {
		if (kabupatenCombobox.getSelectedItem().getValue()==null) {
			// display textentry dialog
			Map<String, String> args = Collections.singletonMap("name", "Kabupaten/Kot:");
			Window textEntryWindow = (Window) Executions.createComponents("/kejadian/WilayahTextEntry.zul", kejadianMenonjolWin, args);
			textEntryWindow.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					String namaKabupaten = (String) event.getData();
										
					List<Kabupaten_Kotamadya> kabupatenKotList;
					Propinsi propByProxy;
					Propinsi selPropinsi = propCombobox.getSelectedItem().getValue();
					propByProxy = getPropinsiDao().findKabupatenKotamadyaByProxy(selPropinsi.getId());
					kabupatenKotList = propByProxy.getKabupatenkotamadyas();
					// check whether the name exists in the list
					for (Kabupaten_Kotamadya kabupatenKotExisting : kabupatenKotList) {
						if (kabupatenKotExisting.getNamaKabupaten().compareTo(namaKabupaten)==0) {
							throw new Exception("Kabupaten/Kot "+namaKabupaten+" sudah terdaftar sebelumnya.");
						}
					}

					// create a new kabupaten object
					Kabupaten_Kotamadya kabupatenKot = new Kabupaten_Kotamadya();
					kabupatenKot.setCreatedAt(asDate(getCurrentLocalDateTime()));
					kabupatenKot.setEditedAt(asDate(getCurrentLocalDateTime()));
					kabupatenKot.setNamaKabupaten(namaKabupaten);
					
					// add the kabupaten to the propinsi
					kabupatenKotList.add(kabupatenKot);
					
					// update propinsi
					getPropinsiDao().update(propByProxy);
					
					// notif
					Clients.showNotification("Kabupaten berhasil ditambahkan dibawah Propinsi");
					
					// display the kabupaten
					resetOtherComboboxes(3);
					
					loadKabupatenCombobox(selectedPropinsi);

					for (Comboitem comboitemKab : kabupatenCombobox.getItems()) {
						if (comboitemKab.getLabel().compareTo(namaKabupaten)==0) {
							kabupatenCombobox.setSelectedItem(comboitemKab);
							
							selectedKabupatenKot = comboitemKab.getValue();
							
							break;
						}						
					}
					loadKecamatanCombobox(selectedKabupatenKot);
				}
			});
			textEntryWindow.addEventListener(Events.ON_CANCEL, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					resetOtherComboboxes(3);
					
					loadKabupatenCombobox(selectedPropinsi);
				}
			});
			textEntryWindow.doModal();
		} else {
			resetOtherComboboxes(2);
			
			selectedKabupatenKot = kabupatenCombobox.getSelectedItem().getValue();
			
			loadKecamatanCombobox(selectedKabupatenKot);			
		}
	}
	
	private void loadKecamatanCombobox(Kabupaten_Kotamadya selKabupaten) throws Exception {
		Kabupaten_Kotamadya kabupatenKotByProxy = 
				getKabupaten_KotamadyaDao().findKecamatanByProxy(selKabupaten.getId());
		List<Kecamatan> kecamatanList = 
				kabupatenKotByProxy.getKecamatans();
		
		log.info(kecamatanList.toString());
		
		// sort
		Collections.sort(kecamatanList, new Comparator<Kecamatan>() {

			@Override
			public int compare(Kecamatan kec1, Kecamatan kec2) {
				
				return kec1.getNamaKecamatan().compareTo(kec2.getNamaKecamatan());
			}
		});
		
		
		if (selKabupaten.getNamaKabupaten().compareTo(NO_INFO)==0) {
			if (kecamatanList.isEmpty()) {
				// create comboitem to add to the kabupaten_kotmadya list
				Comboitem comboitem = new Comboitem();
				comboitem.setLabel("Tambah Kecamatan...");
				comboitem.setValue(null);
				comboitem.setParent(kecamatanCombobox);
			} else {
				// no info for kecamatan
				noInfoForKecamatan(kecamatanList);
				// no info for kelurahan
				noInfoForKelurahan();
			}
		} else {
			
			// reset
			kecamatanCombobox.getItems().clear();
			
			Comboitem comboitem;
			for (Kecamatan kecamatan : kecamatanList) {
				comboitem = new Comboitem();
				comboitem.setLabel(kecamatan.getNamaKecamatan());
				comboitem.setValue(kecamatan);
				comboitem.setParent(kecamatanCombobox);
			}
			// create comboitem to add to the propinsi
			// -- in case the kabupaten not in the list
			comboitem = new Comboitem();
			comboitem.setLabel("Tambah Kecamatan...");
			comboitem.setValue(null);
			comboitem.setParent(kecamatanCombobox);
		}
	}

	private void noInfoForKecamatan(List<Kecamatan> kecamatanList) {
		for (Kecamatan kecamatan : kecamatanList) {
			if (kecamatan.getNamaKecamatan().compareTo(NO_INFO)==0) {
				Comboitem comboitem = new Comboitem();
				comboitem.setLabel(NO_INFO);
				comboitem.setValue(kecamatan);
				comboitem.setParent(kecamatanCombobox);
				break;
			}
		}
		for (Comboitem comboitem : kecamatanCombobox.getItems()) {
			if (comboitem.getLabel().compareTo(NO_INFO)==0) {
				kecamatanCombobox.setSelectedItem(comboitem);
			}
		}
	}

	private void noInfoForKelurahan() throws Exception {
		Kecamatan selKecmatan = kecamatanCombobox.getSelectedItem().getValue();
		Kecamatan kecamatanByProxy = 
				getKecamatanDao().findKelurahanByProxy(selKecmatan.getId());
		List<Kelurahan> kelurahanList =
				kecamatanByProxy.getKelurahans();
		
		log.info(kelurahanList.toString());
		
		// sort
		Collections.sort(kelurahanList, new Comparator<Kelurahan>() {

			@Override
			public int compare(Kelurahan kel1, Kelurahan kel2) {
				
				return kel1.getNamaKelurahan().compareTo(kel2.getNamaKelurahan());
			}
		});
		
		// reset
		kelurahanCombobox.getItems().clear();
		
		if (kelurahanList.isEmpty()) {
			// create comboitem to add to the kecamatan list
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("Tambah Kelurahan...");
			comboitem.setValue(null);
			comboitem.setParent(kelurahanCombobox);					
		} else {
			for (Kelurahan kelurahan : kelurahanList) {
				if (kelurahan.getNamaKelurahan().compareTo(NO_INFO)==0) {
					Comboitem comboitem = new Comboitem();
					comboitem.setLabel(NO_INFO);
					comboitem.setValue(kelurahan);
					comboitem.setParent(kelurahanCombobox);
					break;							
				}
			}
			for (Comboitem comboitem : kelurahanCombobox.getItems()) {
				if (comboitem.getLabel().compareTo(NO_INFO)==0) {
					kelurahanCombobox.setSelectedItem(comboitem);
					break;
				}
			}
		}
	}	
	
	public void onSelect$kecamatanCombobox(Event event) throws Exception {
		if (kecamatanCombobox.getSelectedItem().getValue()==null) {
			// display textentry dialog
			Map<String, String> args = Collections.singletonMap("name", "Kecamatan:");
			Window textEntryWindow = (Window) Executions.createComponents("/kejadian/WilayahTextEntry.zul", kejadianMenonjolWin, args);
			textEntryWindow.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					String namaKecamatan = (String) event.getData();

					List<Kecamatan> kecamatanList;
					Kabupaten_Kotamadya kabupatenKotByProxy;
					Kabupaten_Kotamadya selKabupatenKot = kabupatenCombobox.getSelectedItem().getValue();
					kabupatenKotByProxy = getKabupaten_KotamadyaDao().findKecamatanByProxy(selKabupatenKot.getId());
					kecamatanList = kabupatenKotByProxy.getKecamatans();
					// check whether the name exists in the list
					for (Kecamatan kecamatanExisting : kecamatanList) {
						if (kecamatanExisting.getNamaKecamatan().compareTo(namaKecamatan)==0) {
							throw new Exception("Kecamatan "+namaKecamatan+" sudah terdaftar sebelumnya.");
						}
					}
					
					// create kecamatan object
					Kecamatan kecamatan = new Kecamatan();
					kecamatan.setCreatedAt(asDate(getCurrentLocalDateTime()));
					kecamatan.setEditedAt(asDate(getCurrentLocalDateTime()));
					kecamatan.setNamaKecamatan(namaKecamatan);
					
					// add kecamatan to Kabupaten
					kecamatanList.add(kecamatan);
					
					// update Kabupaten
					getKabupaten_KotamadyaDao().update(kabupatenKotByProxy);
					
					// notif
					Clients.showNotification("Kecamatan berhasil ditambahkan dibawah Kabupaten/Kotamadya");
					
					// display the kecamatan
					resetOtherComboboxes(2);
					
					loadKecamatanCombobox(selectedKabupatenKot);
					
					for (Comboitem comboitemKec : kecamatanCombobox.getItems()) {
						if (comboitemKec.getLabel().compareTo(namaKecamatan)==0) {
							kecamatanCombobox.setSelectedItem(comboitemKec);
							
							selectedKecamatan = comboitemKec.getValue();
							log.info("selected kecamatan: "+selectedKecamatan.getNamaKecamatan());
							
							break;
						}
					}
					
					loadKelurahanCombobox(selectedKecamatan);
				}
			});
			textEntryWindow.addEventListener(Events.ON_CANCEL, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					resetOtherComboboxes(2);

					loadKecamatanCombobox(selectedKabupatenKot);						
				}
			});
			textEntryWindow.doModal();
		} else {
			resetOtherComboboxes(1);
			
			loadKelurahanCombobox(kecamatanCombobox.getSelectedItem().getValue());
		}
	}

	private void loadKelurahanCombobox(Kecamatan selKecamatan) throws Exception {
		Kecamatan selKecamatanByProxy = 
				getKecamatanDao().findKelurahanByProxy(selKecamatan.getId());
		List<Kelurahan> kelurahanList = 
				selKecamatanByProxy.getKelurahans();
		
		// reset
		kelurahanCombobox.getItems().clear();
		
		if (selKecamatan.getNamaKecamatan().compareTo(NO_INFO)==0) {
			if (kelurahanList.isEmpty()) {
				// create comboitem to add to the kecamatan list
				Comboitem comboitem = new Comboitem();
				comboitem.setLabel("Tambah Kelurahan...");
				comboitem.setValue(null);
				comboitem.setParent(kelurahanCombobox);
			} else {
				noInfoForKelurahan();
			}
		} else {
			Comboitem comboitem;
			for (Kelurahan kelurahan : kelurahanList) {
				comboitem = new Comboitem();
				comboitem.setLabel(kelurahan.getNamaKelurahan());
				comboitem.setValue(kelurahan);
				comboitem.setParent(kelurahanCombobox);
			}
			// create comboitem to add to the propinsi
			// -- in case the kabupaten not in the list
			comboitem = new Comboitem();
			comboitem.setLabel("Tambah Kelurahan...");
			comboitem.setValue(null);
			comboitem.setParent(kelurahanCombobox);
		}		
	}
	
	public void onSelect$kelurahanCombobox(Event event) throws Exception {
		if (kelurahanCombobox.getSelectedItem().getValue()==null) {
			// display textentry dialog
			Map<String, String> args = Collections.singletonMap("name", "Kelurahan:");
			Window textEntryWindow = (Window) Executions.createComponents("/kejadian/WilayahTextEntry.zul", kejadianMenonjolWin, args);
			textEntryWindow.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					String namaKelurahan = (String) event.getData();
					
					List<Kelurahan> kelurahanList;
					Kecamatan kecamatanByProxy;
					Kecamatan selKecamatan = kecamatanCombobox.getSelectedItem().getValue();
					kecamatanByProxy = getKecamatanDao().findKelurahanByProxy(selKecamatan.getId());
					kelurahanList = kecamatanByProxy.getKelurahans();
					// check whether the name exists in the list
					for (Kelurahan kelurahan : kelurahanList) {
						if (kelurahan.getNamaKelurahan().compareTo(namaKelurahan)==0) {
							throw new Exception("Kelurahan "+namaKelurahan+" sudah terdaftar sebelumnya.");
						}
					}
					// create kelurahan object
					Kelurahan kelurahan = new Kelurahan();
					kelurahan.setCreatedAt(asDate(getCurrentLocalDateTime()));
					kelurahan.setEditedAt(asDate(getCurrentLocalDateTime()));
					kelurahan.setNamaKelurahan(namaKelurahan);
					// add kelurahan to kecamatan
					kelurahanList.add(kelurahan);
					// update
					getKecamatanDao().update(kecamatanByProxy);
					// notif
					Clients.showNotification("Kelurahan berhasil ditambahkan dibawah Kecamatan");
					// display the kelurahan
					kelurahanCombobox.getItems().clear();
					// re-list
					kecamatanByProxy = getKecamatanDao().findKelurahanByProxy(selKecamatan.getId());
					kelurahanList = kecamatanByProxy.getKelurahans();
					Comboitem comboitem;
					for (Kelurahan kelurahanObj : kelurahanList) {
						comboitem = new Comboitem();
						comboitem.setLabel(kelurahanObj.getNamaKelurahan());
						comboitem.setValue(kelurahanObj);
						comboitem.setParent(kelurahanCombobox);
					}
					// select
					for (Comboitem comboitemKel : kelurahanCombobox.getItems()) {
						if (comboitemKel.getLabel().compareTo(namaKelurahan)==0) {
							kelurahanCombobox.setSelectedItem(comboitemKel);
							break;
						}
					}
				}
			});
			textEntryWindow.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					kelurahanCombobox.getItems().clear();
					
					loadKelurahanCombobox(kecamatanCombobox.getSelectedItem().getValue());
				}
				
			});
			textEntryWindow.addEventListener(Events.ON_CANCEL, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					resetOtherComboboxes(1);
					
					loadKelurahanCombobox(kecamatanCombobox.getSelectedItem().getValue());
				}
			});
			textEntryWindow.doModal();
		}
	}
		
	public State getState() {
		return this.state;
	}
	
	public void setState(State state) {
		this.state = state;
		
		switch (state) {
		// EMPTY - data NOT displayed - create 'new' kejadian ONLY
		case EMPTY:
			kejadianIdTextbox.setValue("");
			twBuatTahunTextbox.setDisabled(true);
			twBuatTahunTextbox.setValue("");
			twBuatTanggalJamTextbox.setDisabled(true);
			twBuatTanggalJamTextbox.setValue("");
			twBuatTimeZoneCombobox.setDisabled(true);
			twBuatTimeZoneCombobox.setValue("");
			twJadiTahunTextbox.setDisabled(true);
			twJadiTahunTextbox.setValue("");
			twJadiTanggalJamTextbox.setDisabled(true);
			twJadiTanggalJamTextbox.setValue("");
			twJadiTimeZoneTextbox.setDisabled(true);
			twJadiTimeZoneTextbox.setValue("");
			twJadiRubahButton.setDisabled(true);
			
			kotamaopsCombobox.setDisabled(true);
			kotamaopsCombobox.setValue("");
			propCombobox.setDisabled(true);
			propCombobox.setValue("");
			koordGpsTextbox.setDisabled(true);
			koordGpsTextbox.setValue("");
			koordPetaTextbox.setDisabled(true);
			koordPetaTextbox.setValue("");
			bujurLintangTextbox.setDisabled(true);
			bujurLintangTextbox.setValue("");
			kabupatenCombobox.setDisabled(true);
			kabupatenCombobox.setValue("");
			kecamatanCombobox.setDisabled(true);
			kecamatanCombobox.setValue("");
			kelurahanCombobox.setDisabled(true);
			kelurahanCombobox.setValue("");
			kampungTextbox.setDisabled(true);
			kampungTextbox.setValue("");
			jalanTextbox.setDisabled(true);
			jalanTextbox.setValue("");
			jenisKejadianCombobox.setDisabled(true);
			jenisKejadianCombobox.setValue("");
			motifKejadianCombobox.setDisabled(true);
			motifKejadianCombobox.setValue("");
			pelakuKejadianCombobox.setDisabled(true);
			pelakuKejadianCombobox.setValue("");
			keteranganPelakuTextbox.setDisabled(true);
			keteranganPelakuTextbox.setValue("");
			sasaranTextbox.setDisabled(true);
			sasaranTextbox.setValue("");
			kronologisTextbox.setDisabled(true);
			kronologisTextbox.setValue("");
			kerugian0Checkbox.setDisabled(true);
			kerugian0CaptionLabel.setStyle("color: gray; font-weight: normal;");
			kerugian1Checkbox.setDisabled(true);
			kerugian1CaptionLabel.setStyle("color: gray; font-weight: normal;");
			kerugian2Checkbox.setDisabled(true);
			kerugian2CaptionLabel.setStyle("color: gray; font-weight: normal;");
			
			newKejadianLabel.setVisible(true);
			splitLabel.setVisible(false);
			editKejadianLabel.setVisible(false);
			saveKejadianLabel.setVisible(false);
			cancelKejadianLabel.setVisible(false);		
			break;
		// VIEW - data is displayed - either create 'new' kejadian or 'edit' existing kejadian
		case VIEW:			
			twBuatTahunTextbox.setDisabled(true);
			twBuatTanggalJamTextbox.setDisabled(true);
			twBuatTimeZoneCombobox.setDisabled(true);
			twJadiTahunTextbox.setDisabled(true);
			twJadiTanggalJamTextbox.setDisabled(true);
			twJadiTimeZoneTextbox.setDisabled(true);
			twJadiRubahButton.setDisabled(true);
			kotamaopsCombobox.setDisabled(true);
			propCombobox.setDisabled(true);
			koordGpsTextbox.setDisabled(true);
			koordPetaTextbox.setDisabled(true);
			bujurLintangTextbox.setDisabled(true);
			kabupatenCombobox.setDisabled(true);
			kecamatanCombobox.setDisabled(true);
			kelurahanCombobox.setDisabled(true);
			kampungTextbox.setDisabled(true);
			jalanTextbox.setDisabled(true);
			jenisKejadianCombobox.setDisabled(true);
			motifKejadianCombobox.setDisabled(true);
			pelakuKejadianCombobox.setDisabled(true);
			keteranganPelakuTextbox.setDisabled(true);
			sasaranTextbox.setDisabled(true);
			kronologisTextbox.setDisabled(true);
			
			kerugian0Checkbox.setDisabled(true);
			kerugian0CaptionLabel.setStyle("color: gray; font-weight: normal;");
			kerugian1Checkbox.setDisabled(true);
			kerugian1CaptionLabel.setStyle("color: gray; font-weight: normal;");
			kerugian2Checkbox.setDisabled(true);
			kerugian2CaptionLabel.setStyle("color: gray; font-weight: normal;");
			
			newKejadianLabel.setVisible(true);
			splitLabel.setVisible(true);
			editKejadianLabel.setVisible(true);
			saveKejadianLabel.setVisible(false);
			cancelKejadianLabel.setVisible(false);
			break;
		case EDIT:
			twBuatTahunTextbox.setDisabled(false);
			twBuatTanggalJamTextbox.setDisabled(false);
			twBuatTimeZoneCombobox.setDisabled(false);
			twJadiTahunTextbox.setDisabled(false);
			twJadiTanggalJamTextbox.setDisabled(false);
			twJadiTimeZoneTextbox.setDisabled(false);
			twJadiRubahButton.setDisabled(false);
			kotamaopsCombobox.setDisabled(false);
			propCombobox.setDisabled(false);
			koordGpsTextbox.setDisabled(false);
			koordPetaTextbox.setDisabled(false);
			bujurLintangTextbox.setDisabled(false);
			kabupatenCombobox.setDisabled(false);
			kecamatanCombobox.setDisabled(false);
			kelurahanCombobox.setDisabled(false);
			kampungTextbox.setDisabled(false);
			jalanTextbox.setDisabled(false);
			jenisKejadianCombobox.setDisabled(false);
			motifKejadianCombobox.setDisabled(false);
			pelakuKejadianCombobox.setDisabled(false);
			keteranganPelakuTextbox.setDisabled(false);
			sasaranTextbox.setDisabled(false);
			kronologisTextbox.setDisabled(false);

			kerugian0Checkbox.setDisabled(false);
			kerugian1Checkbox.setDisabled(false);
			kerugian2Checkbox.setDisabled(false);

			newKejadianLabel.setVisible(false);
			splitLabel.setVisible(false);
			editKejadianLabel.setVisible(false);
			saveKejadianLabel.setVisible(true);
			cancelKejadianLabel.setVisible(true);
			break;
		case NEW:
			idLabel.setValue("");
			twBuatTahunTextbox.setDisabled(false);
			twBuatTanggalJamTextbox.setDisabled(false);
			twBuatTimeZoneCombobox.setDisabled(false);
			twJadiTahunTextbox.setDisabled(false);
			twJadiTanggalJamTextbox.setDisabled(false);
			twJadiTimeZoneTextbox.setDisabled(false);
			twJadiRubahButton.setDisabled(false);
			kotamaopsCombobox.setDisabled(false);
			propCombobox.setDisabled(false);
			koordGpsTextbox.setDisabled(false);
			koordPetaTextbox.setDisabled(false);
			bujurLintangTextbox.setDisabled(false);
			kabupatenCombobox.setDisabled(false);
			kecamatanCombobox.setDisabled(false);
			kelurahanCombobox.setDisabled(false);
			kampungTextbox.setDisabled(false);
			jalanTextbox.setDisabled(false);
			jenisKejadianCombobox.setDisabled(false);
			motifKejadianCombobox.setDisabled(false);
			pelakuKejadianCombobox.setDisabled(false);
			keteranganPelakuTextbox.setDisabled(false);
			sasaranTextbox.setDisabled(false);
			kronologisTextbox.setDisabled(false);
			
			kerugian0Checkbox.setDisabled(false);
			kerugian0Checkbox.setChecked(false);
			kerugian0CaptionLabel.setStyle("color: gray; font-weight: normal;");
			kerugian1Checkbox.setDisabled(false);
			kerugian1Checkbox.setChecked(false);
			kerugian1CaptionLabel.setStyle("color: gray; font-weight: normal;");
			kerugian2Checkbox.setDisabled(false);
			kerugian2Checkbox.setChecked(false);
			kerugian2CaptionLabel.setStyle("color: gray; font-weight: normal;");
			
			newKejadianLabel.setVisible(false);
			splitLabel.setVisible(false);
			editKejadianLabel.setVisible(false);
			saveKejadianLabel.setVisible(true);
			cancelKejadianLabel.setVisible(true);
			break;
		default:
			break;
		}
		
	}
	
	private void resetOtherComboboxes(int numComboboxes) {
		switch (numComboboxes) {
		case 4:
			// when kotamaops is selected
			propCombobox.setValue("");
			kabupatenCombobox.setValue("");
			kecamatanCombobox.setValue("");
			kelurahanCombobox.setValue("");
			
			propCombobox.getItems().clear();
			kabupatenCombobox.getItems().clear();
			kecamatanCombobox.getItems().clear();
			kelurahanCombobox.getItems().clear();
			break;
		case 3:
			// when propinsi is selected
			kabupatenCombobox.setValue("");
			kecamatanCombobox.setValue("");
			kelurahanCombobox.setValue("");

			kabupatenCombobox.getItems().clear();
			kecamatanCombobox.getItems().clear();
			kelurahanCombobox.getItems().clear();			
			break;
		case 2:
			// when kabupaten is selected
			kecamatanCombobox.setValue("");
			kelurahanCombobox.setValue("");

			kecamatanCombobox.getItems().clear();
			kelurahanCombobox.getItems().clear();			
			break;
		case 1:
			// when kecamatan is selected
			kelurahanCombobox.setValue("");
			kelurahanCombobox.getItems().clear();			
			break;

		default:
			break;
		}
	}
	
	/**
	 * KERUGIAN - PIHAKKITA
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$kerugian0Checkbox(Event event) throws Exception {
		log.info("kerugian0_PihakKita Check : "+kerugian0Checkbox.isChecked());
		if (kerugian0Checkbox.isChecked()) {
			kerugian0CaptionLabel.setStyle("color: green; font-weight: bold;");
			setStateKerugian0(State.NEW);			
		} else {
			kerugian0CaptionLabel.setStyle("color: gray; font-weight: normal;");
			setStateKerugian0(State.EMPTY);
			
			kerugian0List.clear();
			log.info("Remove all kerugian0_PihakKita");
		}		
	}
	
	public void onClick$kerugian0SaveButton(Event event) throws Exception {
		log.info("Save kerugian0_PihakKita");
		Kerugian kerugian = null;
		if (getStateKerugian0().equals(State.NEW)) {
			kerugian = new Kerugian();
			kerugian.setParaPihak(Pihak.KITA);
			kerugian.setCreatedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setEditedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setNamaMaterial(namaPersMat0Textbox.getValue());
			kerugian.setTipeKerugian(tipeKerugian0Combobox.getSelectedItem()!=null ? 
					tipeKerugian0Combobox.getSelectedItem().getValue() : null);
			kerugian.setLembagaTerkait(lembaga0Combobox.getSelectedItem()!=null ?
					lembaga0Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianJenis(kerugianJenis0Combobox.getSelectedItem()!=null ?
					kerugianJenis0Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianKondisi(kondisi0Combobox.getSelectedItem()!=null ?
					kondisi0Combobox.getSelectedItem().getValue() : null);
			kerugian.setJumlah(jumlah0Intbox.getValue()!=null ?
					jumlah0Intbox.getValue() : 0);
			kerugian.setKerugianSatuan(satuan0Combobox.getSelectedItem()!=null ?
					satuan0Combobox.getSelectedItem().getValue() : null);
			kerugian.setKeterangan(keterangan0Textbox.getValue());

			kerugian0List.add(kerugian);
		} else {
			kerugian = kerugian0List.get(kr0CurrIndex);
			kerugian.setEditedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setNamaMaterial(namaPersMat0Textbox.getValue());
			kerugian.setTipeKerugian(tipeKerugian0Combobox.getSelectedItem()!=null ? 
					tipeKerugian0Combobox.getSelectedItem().getValue() : null);				
			kerugian.setLembagaTerkait(lembaga0Combobox.getSelectedItem()!=null ?
					lembaga0Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianJenis(kerugianJenis0Combobox.getSelectedItem()!=null ?
					kerugianJenis0Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianKondisi(kondisi0Combobox.getSelectedItem()!=null ?
					kondisi0Combobox.getSelectedItem().getValue() : null);
			kerugian.setJumlah(jumlah0Intbox.getValue()!=null ?
					jumlah0Intbox.getValue() : 0);
			kerugian.setKerugianSatuan(satuan0Combobox.getSelectedItem()!=null ?
					satuan0Combobox.getSelectedItem().getValue() : null);
			kerugian.setKeterangan(keterangan0Textbox.getValue());
		}		
		
		setStateKerugian0(State.VIEW);
	}
	
	public void onClick$kerugian0AddButton(Event event) throws Exception {
		log.info("Add kerugian0_PihakKita");
		
		setStateKerugian0(State.NEW);
	}
	
	public void onClick$kerugian0EditButton(Event event) throws Exception {
		log.info("Edit kerugian0_PihakKita - Index : "+kr0CurrIndex);
		
		setStateKerugian0(State.EDIT);
	}
	
	public void onClick$kerugian0DeleteButton(Event event) throws Exception {
		log.info("Delete kerugian0_PihakKita - Index : "+kr0CurrIndex);
		
		kerugian0List.remove(kr0CurrIndex);
		kerugian0List.forEach(kerugian -> log.info(kerugian.toString()));
		
		// reset index
		kr0CurrIndex = 0;
		displayKerugian0_PihakKita(kr0CurrIndex);
	}
	
	public void onClick$kerugian0PrevButton(Event event) throws Exception {
		if (kr0CurrIndex==0) {
			// do nothing
		} else {
			kr0CurrIndex = kr0CurrIndex - 1;
		}
		
		displayKerugian0_PihakKita(kr0CurrIndex);
	}
	
	public void onClick$kerugian0NextButton(Event event) throws Exception {
		if (kr0CurrIndex < (kerugian0List.size()-1)) {
			kr0CurrIndex = kr0CurrIndex + 1;
		} else {
			// do nothing
		}
		
		displayKerugian0_PihakKita(kr0CurrIndex);
	}
	
	/**
	 * KERUGIAN - PIHAKMUSUH
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$kerugian1Checkbox(Event event) throws Exception {
		log.info("kerugian1_PihakMusuh Check : "+kerugian1Checkbox.isChecked());
		if (kerugian1Checkbox.isChecked()) {
			kerugian1CaptionLabel.setStyle("color: blue; font-weight: bold;");
			setStateKerugian1(State.NEW);
		} else {
			kerugian1CaptionLabel.setStyle("color: gray; font-weight: normal;");
			setStateKerugian1(State.EMPTY);
			
			kerugian1List.clear();
			log.info("Remove all kerugian1_PihakMusuh");			
		}
	}
	
	public void onClick$kerugian1SaveButton(Event event) throws Exception {
		log.info("Save kerugian1_PihakMusuh");
		Kerugian kerugian = null;
		if (getStateKerugian1().equals(State.NEW)) {
			kerugian = new Kerugian();
			kerugian.setParaPihak(Pihak.MUSUH);
			kerugian.setCreatedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setEditedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setNamaMaterial(namaPersMat1Textbox.getValue());
			kerugian.setTipeKerugian(tipeKerugian1Combobox.getSelectedItem()!=null ? 
					tipeKerugian1Combobox.getSelectedItem().getValue() : null);				
			kerugian.setLembagaTerkait(lembaga1Combobox.getSelectedItem()!=null ?
					lembaga1Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianJenis(kerugianJenis1Combobox.getSelectedItem()!=null ?
					kerugianJenis1Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianKondisi(kondisi1Combobox.getSelectedItem()!=null ?
					kondisi1Combobox.getSelectedItem().getValue() : null);
			kerugian.setJumlah(jumlah1Intbox.getValue()!=null ?
					jumlah1Intbox.getValue() : 0);
			kerugian.setKerugianSatuan(satuan1Combobox.getSelectedItem()!=null ?
					satuan1Combobox.getSelectedItem().getValue() : null);
			kerugian.setKeterangan(keterangan1Textbox.getValue());

			kerugian1List.add(kerugian);
		} else {
			kerugian = kerugian1List.get(kr1CurrIndex);
			kerugian.setEditedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setNamaMaterial(namaPersMat1Textbox.getValue());
			kerugian.setTipeKerugian(tipeKerugian1Combobox.getSelectedItem()!=null ? 
					tipeKerugian1Combobox.getSelectedItem().getValue() : null);				
			kerugian.setLembagaTerkait(lembaga1Combobox.getSelectedItem()!=null ?
					lembaga1Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianJenis(kerugianJenis1Combobox.getSelectedItem()!=null ?
					kerugianJenis1Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianKondisi(kondisi1Combobox.getSelectedItem()!=null ?
					kondisi1Combobox.getSelectedItem().getValue() : null);
			kerugian.setJumlah(jumlah1Intbox.getValue()!=null ?
					jumlah1Intbox.getValue() : 0);
			kerugian.setKerugianSatuan(satuan1Combobox.getSelectedItem()!=null ?
					satuan1Combobox.getSelectedItem().getValue() : null);
			kerugian.setKeterangan(keterangan1Textbox.getValue());
		}		
		
		setStateKerugian1(State.VIEW);
	}
	
	public void onClick$kerugian1AddButton(Event event) throws Exception {
		log.info("Add kerugian1_PihakMusuh");
		
		setStateKerugian1(State.NEW);	
	}
	
	public void onClick$kerugian1EditButton(Event event) throws Exception {
		log.info("Edit kerugian1_PihakMusuh - Index : "+kr1CurrIndex);
		
		setStateKerugian1(State.EDIT);		
	}	

	public void onClick$kerugian1DeleteButton(Event event) throws Exception {
		log.info("Delete kerugian1_PihakMusuh - Index : "+kr1CurrIndex);
		
		kerugian1List.remove(kr1CurrIndex);
		kerugian1List.forEach(kerugian -> log.info(kerugian.toString()));
		
		// reset index
		kr1CurrIndex = 0;
		displayKerugian1_PihakMusuh(kr1CurrIndex);
	}
	
	public void onClick$kerugian1PrevButton(Event event) throws Exception {
		if (kr1CurrIndex==0) {
			// do nothing
		} else {
			kr1CurrIndex = kr1CurrIndex - 1;
		}
		
		displayKerugian1_PihakMusuh(kr1CurrIndex);
	}
	
	public void onClick$kerugian1NextButton(Event event) throws Exception {
		if (kr1CurrIndex < (kerugian1List.size()-1)) {
			kr1CurrIndex = kr1CurrIndex + 1;
		} else {
			// do nothing
		}
		
		displayKerugian1_PihakMusuh(kr1CurrIndex);
	}

	/**
	 * KERUGIAN - PIHAKLAIN
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$kerugian2Checkbox(Event event) throws Exception {
		log.info("kerugian2_PihakLain Check : "+kerugian2Checkbox.isChecked());
		if (kerugian2Checkbox.isChecked()) {
			kerugian2CaptionLabel.setStyle("color: orange; font-weight: bold;");
			setStateKerugian2(State.NEW);
		} else {
			kerugian2CaptionLabel.setStyle("color: gray; font-weight: normal;");
			setStateKerugian2(State.EMPTY);

			kerugian2List.clear();
			log.info("Remove all kerugian2_PihakLain");
		}
	}
	
	public void onClick$kerugian2SaveButton(Event event) throws Exception {
		log.info("Save kerugian2_PihakLain");
		Kerugian kerugian = null;
		if (getStateKerugian2().equals(State.NEW)) {
			kerugian = new Kerugian();
			kerugian.setParaPihak(Pihak.LAIN_LAIN);
			kerugian.setCreatedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setEditedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setNamaMaterial(namaPersMat2Textbox.getValue());
			kerugian.setTipeKerugian(tipeKerugian2Combobox.getSelectedItem()!=null ?  
					tipeKerugian2Combobox.getSelectedItem().getValue() : null);				
			kerugian.setLembagaTerkait(lembaga2Combobox.getSelectedItem()!=null ?
					lembaga2Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianJenis(kerugianJenis2Combobox.getSelectedItem()!=null ?
					kerugianJenis2Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianKondisi(kondisi2Combobox.getSelectedItem()!=null ?
					kondisi2Combobox.getSelectedItem().getValue() : null);
			kerugian.setJumlah(jumlah2Intbox.getValue()!=null ?
					jumlah2Intbox.getValue() : 0);
			kerugian.setKerugianSatuan(satuan2Combobox.getSelectedItem()!=null ?
					satuan2Combobox.getSelectedItem().getValue() : null);
			kerugian.setKeterangan(keterangan2Textbox.getValue());
			
			kerugian2List.add(kerugian);
		} else {
			kerugian = kerugian2List.get(kr2CurrIndex);
			kerugian.setEditedAt(asDate(getCurrentLocalDateTime()));
			kerugian.setNamaMaterial(namaPersMat2Textbox.getValue());
			kerugian.setTipeKerugian(tipeKerugian2Combobox.getSelectedItem()!=null ? 
					tipeKerugian2Combobox.getSelectedItem().getValue() : null);				
			kerugian.setTipeKerugian(tipeKerugian2Combobox.getSelectedItem()!=null ?  
					tipeKerugian2Combobox.getSelectedItem().getValue() : null);				
			kerugian.setLembagaTerkait(lembaga2Combobox.getSelectedItem()!=null ?
					lembaga2Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianJenis(kerugianJenis2Combobox.getSelectedItem()!=null ?
					kerugianJenis2Combobox.getSelectedItem().getValue() : null);
			kerugian.setKerugianKondisi(kondisi2Combobox.getSelectedItem()!=null ?
					kondisi2Combobox.getSelectedItem().getValue() : null);
			kerugian.setJumlah(jumlah2Intbox.getValue()!=null ?
					jumlah2Intbox.getValue() : 0);
			kerugian.setKerugianSatuan(satuan2Combobox.getSelectedItem()!=null ?
					satuan2Combobox.getSelectedItem().getValue() : null);
			kerugian.setKeterangan(keterangan2Textbox.getValue());
		}
		
		setStateKerugian2(State.VIEW);
	}
	
	public void onClick$kerugian2AddButton(Event event) throws Exception {
		log.info("Add kerugian2_PihakLain");

		setStateKerugian2(State.NEW);
	}
	
	public void onClick$kerugian2EditButton(Event event) throws Exception {
		log.info("Edit kerugian2_PihakLain - Index : "+kr2CurrIndex);

		setStateKerugian2(State.EDIT);
	}
	
	public void onClick$kerugian2DeleteButton(Event event) throws Exception {
		log.info("Delete kerugian2_PihakLain - Index : "+kr2CurrIndex);
		
		kerugian2List.remove(kr2CurrIndex);
		kerugian2List.forEach(kerugian -> log.info(kerugian.toString()));
		
		// reset index
		kr2CurrIndex = 0;
		displayKerugian2_PihakLain(kr2CurrIndex);
	}
	
	public void onClick$kerugian2PrevButton(Event event) throws Exception {
		if (kr2CurrIndex==0) {
			// do nothing
		} else {
			kr2CurrIndex = kr2CurrIndex - 1;
		}
		
		displayKerugian2_PihakLain(kr2CurrIndex);
		
	}
	
	public void onClick$kerugian2NextButton(Event event) throws Exception {
		if (kr2CurrIndex < (kerugian2List.size()-1)) {
			kr2CurrIndex = kr2CurrIndex + 1;
		} else {
			// do nothing
		}
		
		displayKerugian2_PihakLain(kr2CurrIndex);
	}
	
	/**
	 * Search - By TW Pembuatan / Kejadian
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$twAwalRubahButton(Event event) throws Exception {
		log.info("Rubah TW Awal to search - "+twRadioGroup.getSelectedItem().getLabel());
		int timezoneIndOrdinal = getKotamaops().getTimeZone().ordinal();
		ZoneId zoneId = getKotamaops().getTimeZone().toZoneId(timezoneIndOrdinal);
		// create the data object
		DatetimeData datetimeData = new DatetimeData();
		// set datetimeData object
		datetimeData.setDialogWinTitle("Rubah AWAL "+twRadioGroup.getSelectedItem().getLabel());
		datetimeData.setTimezoneInd(getKotamaops().getTimeZone());
		datetimeData.setZoneId(zoneId);
		if (twAwalTanggalJamTextbox.getValue().isEmpty()) {
			datetimeData.setLocalDateTime(getCurrentLocalDateTime());						
		} else {
			datetimeData.setLocalDateTime(
					(LocalDateTime) twAwalTanggalJamTextbox.getAttribute("twAwalLocalDateTime"));
		}
		Map<String, DatetimeData> args = Collections.singletonMap("datetimeData", datetimeData);
		Window datetimeWin = (Window) Executions.createComponents(
				"/dialogs/DatetimeWinDialog.zul", kejadianMenonjolWin, args);
		datetimeWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the data
				DatetimeData datetimeData = (DatetimeData) event.getData();
				LocalDateTime twAwalLocalDateTime = datetimeData.getLocalDateTime();
				
				// save in the attribute
				twAwalTanggalJamTextbox.setAttribute("twAwalLocalDateTime", twAwalLocalDateTime);
				
				// display to user
				twAwalTahunTextbox.setValue(getLocalDateTimeString(twAwalLocalDateTime, "yyyy"));
				twAwalTanggalJamTextbox.setValue(
						getLocalDateTimeString(twAwalLocalDateTime, "MM")+
						getLocalDateTimeString(twAwalLocalDateTime, "dd")+"."+
						getLocalDateTimeString(twAwalLocalDateTime, "HH")+
						getLocalDateTimeString(twAwalLocalDateTime, "mm"));
				twAwalTimeZoneTextbox.setValue(datetimeData.getTimezoneInd().toString());				
			}
		});
		datetimeWin.doModal();
	}
	
	public void onClick$twSearchButton(Event event) throws Exception {
		log.info("Rubah TW Akhir to Search - "+twRadioGroup.getSelectedItem().getLabel());
		int timezoneIndOrdinal = getKotamaops().getTimeZone().ordinal();
		ZoneId zoneId = getKotamaops().getTimeZone().toZoneId(timezoneIndOrdinal);
		// create the data object
		DatetimeData datetimeData = new DatetimeData();
		// set datetimeData object
		datetimeData.setDialogWinTitle("Rubah AKHIR "+twRadioGroup.getSelectedItem().getLabel());
		datetimeData.setTimezoneInd(getKotamaops().getTimeZone());
		datetimeData.setZoneId(zoneId);
		if (twAkhirTanggalJamTextbox.getValue().isEmpty()) {
			datetimeData.setLocalDateTime(getCurrentLocalDateTime());						
		} else {
			datetimeData.setLocalDateTime(
					(LocalDateTime) twAkhirTanggalJamTextbox.getAttribute("twAkhirLocalDateTime"));
		}
		Map<String, DatetimeData> args = Collections.singletonMap("datetimeData", datetimeData);
		Window datetimeWin = (Window) Executions.createComponents(
				"/dialogs/DatetimeWinDialog.zul", kejadianMenonjolWin, args);
		
		datetimeWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the data - twAkhir
				DatetimeData datetimeData = (DatetimeData) event.getData();
				LocalDateTime twAkhirLocalDateTime = datetimeData.getLocalDateTime();
				
				// save in the attribute
				twAkhirTanggalJamTextbox.setAttribute("twAkhirLocalDateTime", twAkhirLocalDateTime);
				
				// display to user
				twAkhirTahunTextbox.setValue(getLocalDateTimeString(twAkhirLocalDateTime, "yyyy"));
				twAkhirTanggalJamTextbox.setValue(
						getLocalDateTimeString(twAkhirLocalDateTime, "MM")+
						getLocalDateTimeString(twAkhirLocalDateTime, "dd")+"."+
						getLocalDateTimeString(twAkhirLocalDateTime, "HH")+
						getLocalDateTimeString(twAkhirLocalDateTime, "mm"));
				twAkhirTimeZoneTextbox.setValue(datetimeData.getTimezoneInd().toString());
				
				// get the data from textbox - twAwal
				LocalDateTime twAwalLocalDateTime = 
					(LocalDateTime) twAwalTanggalJamTextbox.getAttribute("twAwalLocalDateTime");
				log.info("twAwal:  "+twAwalLocalDateTime.toString());
				log.info("twAkhir: "+twAkhirLocalDateTime.toString());
				
				List<Kejadian> kejadianResults = null;
				
				if (twRadioGroup.getSelectedIndex()==0) {
					// by twPembuatan
					kejadianResults =
							getKejadianDao().findAllKejadianByTwPembuatan(twAwalLocalDateTime.toString(), twAkhirLocalDateTime.toString());
					
				} else {
					// by twKejadian
					kejadianResults =
							getKejadianDao().findAllKejadianByTwKejadian(twAwalLocalDateTime.toString(), twAkhirLocalDateTime.toString());
					
				}
				
				searchResultListbox.setModel(new ListModelList<Kejadian>(kejadianResults));
				searchResultListbox.setItemRenderer(getSearchResultsListitemRenderer());
			}
		});
		
		datetimeWin.doModal();		
	}
	
	/**
	 * Search - By Text (in Kronologis)
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$searchTextButton(Event event) throws Exception {
		log.info("Search Kronologis");
		// set state to view
		setState(State.VIEW);
		
		List<Kejadian> kejadianResults =
				getKejadianDao().searchKronologis(searchWordTextbox.getValue());
		List<Kejadian> sortByTWKejadian = kejadianResults.stream()
				.sorted(sort_KejadianByTWKejadian())
				.collect(Collectors.toList());
		sortByTWKejadian.forEach(result -> log.info(result.toString()));
				
		// Window searchKronologisWin = 
		//		(Window) Executions.createComponents("/kejadian/SearchKronologis.zul", kejadianMenonjolWin, null);
		
		searchResultListbox.setModel(new ListModelList<Kejadian>(sortByTWKejadian));
		searchResultListbox.setItemRenderer(getSearchResultsListitemRenderer());		
		
		// searchKronologisWin.doModal();
	}
	
	public void onClick$searchIdButton(Event event) throws Exception {
		if (searchIdTextbox.getValue().isEmpty()) {
			return;
		}
	
		log.info("Search Id Kejadian");		
		// set state to view
		setState(State.VIEW);
		
		// getKejadianDao().createIndexer();
		
		List<Kejadian> kejadianResults =
				getKejadianDao().searchKejadianId(searchIdTextbox.getValue());
		List<Kejadian> sortByTWPembuatan = kejadianResults.stream()
				.sorted(sort_KejadianByTWPembuatan())
				.collect(Collectors.toList());
		
		searchResultListbox.setModel(new ListModelList<Kejadian>(sortByTWPembuatan));
		searchResultListbox.setItemRenderer(getSearchResultsListitemRenderer());
	}

	public void onCheck$twRadioGroup(Event event) throws Exception {
		if (searchIdTextbox.getValue().isEmpty()) {
			return;
		}
		
		log.info(twRadioGroup.getSelectedIndex()+" - "+twRadioGroup.getSelectedItem().getLabel());
		List<Kejadian> kejadianResults =
				getKejadianDao().searchKejadianId(searchIdTextbox.getValue());
		
		switch (twRadioGroup.getSelectedIndex()) {
		case 0:
			// 0 - TW Pembuatan
			List<Kejadian> sortByTWPembuatan = kejadianResults.stream()
				.sorted(sort_KejadianByTWPembuatan())
				.collect(Collectors.toList());			
			searchResultListbox.setModel(new ListModelList<Kejadian>(sortByTWPembuatan));
			break;
		case 1:
			// 1 - TW Kejadian
			List<Kejadian> sortByTWKejadian = kejadianResults.stream()
					.sorted(sort_KejadianByTWKejadian())
					.collect(Collectors.toList());			
			searchResultListbox.setModel(new ListModelList<Kejadian>(sortByTWKejadian));
			break;
		default:
			break;
		}
		// render
		searchResultListbox.setItemRenderer(getSearchResultsListitemRenderer());
	}
	
	
	private Comparator<Kejadian> sort_KejadianByTWPembuatan() {
		Comparator<Kejadian> compareByTWPembuatan = Comparator
				.comparing(Kejadian::getTwPembuatanDateTime)
				.thenComparing(Kejadian::getTwPembuatanTimezone);
		
		return compareByTWPembuatan;
	}


	private Comparator<Kejadian> sort_KejadianByTWKejadian() {
		Comparator<Kejadian> compareByTWKejadian = Comparator
				.comparing(Kejadian::getTwKejadianDateTime)
				.thenComparing(Kejadian::getTwKejadianTimezone);
		
		return compareByTWKejadian;
	}

	private ListitemRenderer<Kejadian> getSearchResultsListitemRenderer() {

		return new ListitemRenderer<Kejadian>() {
			
			@Override
			public void render(Listitem item, Kejadian kejadian, int index) throws Exception {
				Listcell lc;
				
				lc = new Listcell(kejadian.getSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// twPembuatan
				lc = new Listcell();
				int twPembuatanTZValue = kejadian.getTwPembuatanTimezone().getValue();
				LocalDateTime twPembuatanLocDateTime =
					asLocalDateTime(kejadian.getTwPembuatanDateTime(), 
						kejadian.getTwPembuatanTimezone().toZoneId(twPembuatanTZValue));				
				lc.setLabel(
						getLocalDateTimeString(twPembuatanLocDateTime, "yyyy")+"."+
						getLocalDateTimeString(twPembuatanLocDateTime, "MM")+
						getLocalDateTimeString(twPembuatanLocDateTime, "dd")+"."+
						getLocalDateTimeString(twPembuatanLocDateTime, "HH")+
						getLocalDateTimeString(twPembuatanLocDateTime, "mm")+" "+
						kejadian.getTwPembuatanTimezone().toString());
				lc.setParent(item);
				
				// twKejadian
				lc = new Listcell();
				int twKejadianTZValue = kejadian.getTwKejadianTimezone().getValue();
				LocalDateTime twKejadianLocDateTime = 
					asLocalDateTime(kejadian.getTwKejadianDateTime(), 
						kejadian.getTwKejadianTimezone().toZoneId(twKejadianTZValue));
				lc.setLabel(
						getLocalDateTimeString(twKejadianLocDateTime, "yyyy")+"."+
						getLocalDateTimeString(twKejadianLocDateTime, "MM")+
						getLocalDateTimeString(twKejadianLocDateTime, "dd")+"."+
						getLocalDateTimeString(twKejadianLocDateTime, "HH")+
						getLocalDateTimeString(twKejadianLocDateTime, "mm")+" "+
						kejadian.getTwKejadianTimezone().toString());
				lc.setParent(item);
				
				item.setValue(kejadian);
			}
		};
	}

	public void onSelect$searchResultListbox(Event event) throws Exception {
		Listitem selectedItem = searchResultListbox.getSelectedItem();
		setKejadian(selectedItem.getValue());
		
		log.info("selected kejadian: "+getKejadian().toString());
		setState(State.VIEW);
		
		displayKejadianData();
	}
	
	public void onClick$newKejadianLabel(Event event) throws Exception {
		log.info("Input Kejadian Baru...");	
		setState(State.NEW);
		setKejadian(new Kejadian());
		
		displayKejadianData();
	}
	
	public void onClick$editKejadianLabel(Event event) throws Exception {
		log.info("Edit - "+getKejadian().toString());

		setState(State.EDIT);
		displayKejadianData();
	}
	
	private void displayKejadianData() throws Exception {
		if (getKejadian().getId().compareTo(Long.MIN_VALUE)==0) {
			log.info("displayKejadianData() - New Kejadian");
			// get the document code from kotamaops
			String documentCode = getKotamaops().getDocumentCode();			
			
			// create a new ID : YYYYMMDDHHMMddd
			setDocumentSerialNumber(createSerialNumber(getCurrentLocalDateTime(), documentCode));
			kejadianIdTextbox.setValue(getDocumentSerialNumber().getSerialComp());
			twBuatTahunTextbox.setValue(getLocalDateTimeString(getCurrentLocalDateTime(), "yyyy"));
			// format to 0518.1313
			twBuatTanggalJamTextbox.setValue(
					getLocalDateTimeString(getCurrentLocalDateTime(), "MM")+
					getLocalDateTimeString(getCurrentLocalDateTime(), "dd")+"."+
					getLocalDateTimeString(getCurrentLocalDateTime(), "HH")+
					getLocalDateTimeString(getCurrentLocalDateTime(), "mm"));
			
			twBuatTimeZoneCombobox.getItems().clear();
			loadTwBuatZonaWaktuCombobox();
			
			for (Comboitem comboitem : twBuatTimeZoneCombobox.getItems()) {
				if (comboitem.getValue().equals(getKotamaops().getTimeZone())) {
					twBuatTimeZoneCombobox.setSelectedItem(comboitem);
					
					break;
				}
			}
			// year
			twJadiTahunTextbox.setValue(getLocalDateTimeString(getCurrentLocalDateTime(), "yyyy"));
			// reset
			twJadiTanggalJamTextbox.setValue("");
			// timezone
			twJadiTimeZoneTextbox.setValue(getKotamaops().getTimeZone().toString());
			// reset kotamaops
			kotamaopsCombobox.setSelectedItem(null);
			kotamaopsCombobox.setValue("");
			// reset propinsi
			propCombobox.setSelectedItem(null);
			propCombobox.setValue("");
			// reset kab/kotamadya
			kabupatenCombobox.setSelectedItem(null);
			kabupatenCombobox.setValue("");
			// reset kecamatan
			kecamatanCombobox.setSelectedItem(null);
			kecamatanCombobox.setValue("");
			// reset kelurahan
			kelurahanCombobox.setSelectedItem(null);
			kelurahanCombobox.setValue("");
			// reset lain2
			koordGpsTextbox.setValue("");
			koordPetaTextbox.setValue("");
			bujurLintangTextbox.setValue("");
			kampungTextbox.setValue("");
			jalanTextbox.setValue("");
			// kejadian
			jenisKejadianCombobox.setSelectedItem(null);
			jenisKejadianCombobox.setValue("");
			motifKejadianCombobox.setSelectedItem(null);
			motifKejadianCombobox.setValue("");
			pelakuKejadianCombobox.setSelectedItem(null);
			pelakuKejadianCombobox.setValue("");
			keteranganPelakuTextbox.setValue("");
			sasaranTextbox.setValue("");
			// kronologis
			kronologisTextbox.setValue("");
			// clean up kerugian
			setStateKerugian0(State.EMPTY);
			setStateKerugian1(State.EMPTY);
			setStateKerugian2(State.EMPTY);
		} else {
			log.info("displayKejadianData() - Kejadian ID: "+getKejadian().getId());
			
			idLabel.setValue("id#"+getKejadian().getId().toString());
			// display existing ID
			kejadianIdTextbox.setValue(getKejadian().getSerialNumber().getSerialComp());
			
			TimezoneInd timezoneInd = getKejadian().getTwKejadianTimezone();
			int timezoneIndOrdinal = timezoneInd.ordinal();
			ZoneId zoneId = timezoneInd.toZoneId(timezoneIndOrdinal);
			
			// pembuatan year
			twBuatTahunTextbox.setValue(getLocalDateTimeString(asLocalDateTime(getKejadian().getTwPembuatanDateTime(), zoneId),"yyyy"));
			// format to 0518.1313
			twBuatTanggalJamTextbox.setValue(
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwPembuatanDateTime(), zoneId),"MM")+
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwPembuatanDateTime(), zoneId),"dd")+"."+
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwPembuatanDateTime(), zoneId), "HH")+
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwPembuatanDateTime(), zoneId), "mm"));
			// time zone
			for (Comboitem comboitem : twBuatTimeZoneCombobox.getItems()) {
				if (comboitem.getValue().equals(getKejadian().getTwPembuatanTimezone())) {
					twBuatTimeZoneCombobox.setSelectedItem(comboitem);
					
					break;
				}
			}
			// kejadian year
			twJadiTahunTextbox.setValue(
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwKejadianDateTime(), zoneId), "yyyy"));
			// format to 0518.1313
			twJadiTanggalJamTextbox.setValue(
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwKejadianDateTime(), zoneId),"MM")+
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwKejadianDateTime(), zoneId),"dd")+"."+
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwKejadianDateTime(), zoneId), "HH")+
					getLocalDateTimeString(asLocalDateTime(getKejadian().getTwKejadianDateTime(), zoneId), "mm"));
			// time zone
			twJadiTimeZoneTextbox.setValue(getKejadian().getTwKejadianTimezone().toString());
			twJadiTimeZoneTextbox.setAttribute("twJadiTimeZoneInd", getKejadian().getTwKejadianTimezone());
			
			Kejadian kejKrugianByProxy = 
					getKejadianDao().findKejadianKerugiansByProxy(getKejadian().getId());

			// kotamaopsCombobox
			Kejadian kejadianByProxy = getKejadianDao().findKejadianKotamaopsByProxy(getKejadian().getId());			
			for (Comboitem comboitem : kotamaopsCombobox.getItems()) {
				if (comboitem.getLabel().compareTo(kejadianByProxy.getKotamaops().getKotamaopsName())==0) {
					kotamaopsCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			if (kotamaopsCombobox.getSelectedItem()==null) {			
				return;
			}

			// propCombobox
			loadPropinsiCombobox(kotamaopsCombobox.getSelectedItem().getValue());
			Kejadian kejadianPropinsiByProxy = getKejadianDao().findKejadianPropinsiByProxy(getKejadian().getId());
			for (Comboitem comboitem : propCombobox.getItems()) {
				Propinsi propinsi = comboitem.getValue();
				String namaPropinsi = kejadianPropinsiByProxy.getPropinsi()==null?
						"" : kejadianPropinsiByProxy.getPropinsi().getNamaPropinsi();
				if (propinsi.getNamaPropinsi().compareTo(namaPropinsi)==0) {
					propCombobox.setSelectedItem(comboitem);
					break;
				}
			}

			// load the kabupaten based on the selected propinsi
			loadKabupatenCombobox(propCombobox.getSelectedItem().getValue());
			Kejadian kejadianKabupatenByProxy = getKejadianDao().findKejadianKabupatenByProxy(getKejadian().getId());
			for (Comboitem comboitem : kabupatenCombobox.getItems()) {
				Kabupaten_Kotamadya kabupatenKotamadya = comboitem.getValue();
				String namaKabupatenKot = kejadianKabupatenByProxy.getKabupatenKotamadya()==null?
						"" : kejadianKabupatenByProxy.getKabupatenKotamadya().getNamaKabupaten();
				if (kabupatenKotamadya.getNamaKabupaten().compareTo(namaKabupatenKot)==0) {
					kabupatenCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			
			// load the kecamatan based on the selected kabupaten
			loadKecamatanCombobox(kabupatenCombobox.getSelectedItem().getValue());
			Kejadian kejadianKecamatanByProxy = getKejadianDao().findKejadianKecamatanByProxy(getKejadian().getId());
			for (Comboitem comboitem : kecamatanCombobox.getItems()) {
				Kecamatan kecamatan = comboitem.getValue();
				String namaKecamatan = kejadianKecamatanByProxy.getKecamatan()==null?
						"" : kejadianKecamatanByProxy.getKecamatan().getNamaKecamatan();
				if (kecamatan.getNamaKecamatan().compareTo(namaKecamatan)==0) {
					kecamatanCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			
			// load the kelurahan based on the selected kecamatan
			loadKelurahanCombobox(kecamatanCombobox.getSelectedItem().getValue());
			Kejadian kejadianKelurahanByProxy = getKejadianDao().findKejadianKelurahanByProxy(getKejadian().getId());
			for (Comboitem comboitem : kelurahanCombobox.getItems()) {
				Kelurahan kelurahan = comboitem.getValue();
				String namaKelurahan = kejadianKelurahanByProxy.getKelurahan()==null?
						"" : kejadianKelurahanByProxy.getKelurahan().getNamaKelurahan();
				if (kelurahan.getNamaKelurahan().compareTo(namaKelurahan)==0) {
					kelurahanCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			
			koordGpsTextbox.setValue(getKejadian().getKoordinatGps());
			koordPetaTextbox.setValue(getKejadian().getKoordinatPeta());
			bujurLintangTextbox.setValue(getKejadian().getBujurLintang());
			kampungTextbox.setValue(getKejadian().getKampung());
			jalanTextbox.setValue(getKejadian().getJalan());
			
			// kronologis
			kronologisTextbox.setValue(getKejadian().getKronologis());

			for (Comboitem comboitem : jenisKejadianCombobox.getItems()) {
				String namaJenisKejadian = getKejadian().getJenisKejadian()==null?
						"" : getKejadian().getJenisKejadian().getNamaJenis();
				if (comboitem.getLabel().compareTo(namaJenisKejadian)==0) {
					jenisKejadianCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			
			for (Comboitem comboitem : motifKejadianCombobox.getItems()) {
				String namaMotifKejadian = getKejadian().getMotifKejadian()==null?
						"" : getKejadian().getMotifKejadian().getNamaMotif();
				if (comboitem.getLabel().compareTo(namaMotifKejadian)==0) {
					motifKejadianCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			
			for (Comboitem comboitem : pelakuKejadianCombobox.getItems()) {
				String namaPelakuKejadian = getKejadian().getPelakuKejadian()==null?
						"" : getKejadian().getPelakuKejadian().getNamaPelaku();
				if (comboitem.getLabel().compareTo(namaPelakuKejadian)==0) {
					pelakuKejadianCombobox.setSelectedItem(comboitem);
					break;
				}
			}
			// keterangan Pelaku
			keteranganPelakuTextbox.setValue(getKejadian().getKeteranganPelaku());
			// sasaran
			sasaranTextbox.setValue(getKejadian().getSasaran());
			
			// kerugian0-PihakKita
			kerugian0List.clear();
			kr0CurrIndex = 0;
			Pihak pihak0 = Pihak.KITA;
			for (Kerugian kerugian : kejKrugianByProxy.getKerugians()) {
				if (kerugian.getParaPihak().equals(pihak0)) {
					kerugian0List.add(kerugian);
				}
			}
			kerugian0List.forEach(kerugian->log.info(kerugian.toString()));
			if (!kerugian0List.isEmpty()) {
				if (getState().equals(State.EDIT)) {
					setStateKerugian0(State.VIEW);
					kerugian0Checkbox.setChecked(true);
				} else {
					setStateKerugian0(State.EMPTY);
					if (!kerugian0List.isEmpty()) {
						displayKerugian0_PihakKita(kr0CurrIndex);
						
						// enable navigation
						kerugian0PrevButton.setVisible(true);
						kerugian0NextButton.setVisible(true);
					}
				}				
			}
			
			// kerugian1-PihakMusuh
			kerugian1List.clear();
			kr1CurrIndex = 0;
			Pihak pihak1 = Pihak.MUSUH;
			for (Kerugian kerugian : kejKrugianByProxy.getKerugians()) {
				if (kerugian.getParaPihak().equals(pihak1)) {
					kerugian1List.add(kerugian);
				}
			}
			kerugian1List.forEach(kerugian->log.info(kerugian.toString()));
			if (!kerugian1List.isEmpty()) {
				if (getState().equals(State.EDIT)) {
					setStateKerugian1(State.VIEW);
					kerugian1Checkbox.setChecked(true);
				} else {
					setStateKerugian1(State.EMPTY);
					if (!kerugian1List.isEmpty()) {
						displayKerugian1_PihakMusuh(kr1CurrIndex);
						
						// enable navigation
						kerugian1PrevButton.setVisible(true);
						kerugian1NextButton.setVisible(true);
					}
				}
			}
			
			// kerugian2-PihakLain
			kerugian2List.clear();
			kr2CurrIndex = 0;
			Pihak pihak2 = Pihak.LAIN_LAIN;
			for (Kerugian kerugian : kejKrugianByProxy.getKerugians()) {
				if (kerugian.getParaPihak().equals(pihak2)) {
					kerugian2List.add(kerugian);
				}
			}
			kerugian2List.forEach(kerugian->log.info(kerugian.toString()));
			if (!kerugian2List.isEmpty()) {
				if (getState().equals(State.EDIT)) {
					setStateKerugian2(State.VIEW);
					kerugian2Checkbox.setChecked(true);
				} else {
					setStateKerugian2(State.EMPTY);
					if (!kerugian2List.isEmpty()) {
						displayKerugian2_PihakLain(kr2CurrIndex);
						
						// enable navigation
						kerugian2PrevButton.setVisible(true);
						kerugian2NextButton.setVisible(true);
					}
				}
			}
		}
	}

	private void displayKerugian0_PihakKita(int currentIndex) {
		if (kerugian0List.isEmpty()) {
			namaPersMat0Textbox.setValue("");
			tipeKerugian0Combobox.setValue("");
			lembaga0Combobox.setValue("");
			kerugianJenis0Combobox.setValue("");
			kondisi0Combobox.setValue("");
			jumlah0Intbox.setValue(null);
			satuan0Combobox.setValue("");
			keterangan0Textbox.setValue("");			
			
			return;
		}
		Kerugian kerugian0 = kerugian0List.get(currentIndex);
		
		namaPersMat0Textbox.setValue(kerugian0.getNamaMaterial());
		if (kerugian0.getTipeKerugian()!=null) {
			for(Comboitem comboitem : tipeKerugian0Combobox.getItems()) {
				if (comboitem.getValue().equals(kerugian0.getTipeKerugian())) {
					tipeKerugian0Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			tipeKerugian0Combobox.setSelectedItem(null);
			tipeKerugian0Combobox.setValue("");
		}
		if (kerugian0.getLembagaTerkait()!=null) {
			for(Comboitem comboitem : lembaga0Combobox.getItems()) {
				if (comboitem.getValue().equals(kerugian0.getLembagaTerkait())) {
					lembaga0Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			lembaga0Combobox.setSelectedItem(null);
			lembaga0Combobox.setValue("");
		}
		if (kerugian0.getKerugianJenis()!=null) {
			for(Comboitem comboitem : kerugianJenis0Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian0.getKerugianJenis().getNamaJenis())==0) {
					kerugianJenis0Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			kerugianJenis0Combobox.setSelectedItem(null);
			kerugianJenis0Combobox.setValue("");
		}
		if (kerugian0.getKerugianKondisi()!=null) {
			for(Comboitem comboitem : kondisi0Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian0.getKerugianKondisi().getNamaKondisi())==0) {
					kondisi0Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			kondisi0Combobox.setSelectedItem(null);
			kondisi0Combobox.setValue("");
		}
		jumlah0Intbox.setValue(kerugian0.getJumlah());
		if (kerugian0.getKerugianSatuan()!=null) {
			for(Comboitem comboitem : satuan0Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian0.getKerugianSatuan().getSatuan())==0) {
					satuan0Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			satuan0Combobox.setSelectedItem(null);
			satuan0Combobox.setValue("");
		}
		keterangan0Textbox.setValue(kerugian0.getKeterangan());
	}
	
	private void displayKerugian1_PihakMusuh(int currentIndex) {
		if (kerugian1List.isEmpty()) {
			namaPersMat1Textbox.setValue("");
			tipeKerugian1Combobox.setValue("");
			lembaga1Combobox.setValue("");
			kerugianJenis1Combobox.setValue("");
			kondisi1Combobox.setValue("");
			jumlah1Intbox.setValue(null);
			satuan1Combobox.setValue("");
			keterangan1Textbox.setValue("");
			
			return;
		}
		Kerugian kerugian1 = kerugian1List.get(currentIndex);
		
		namaPersMat1Textbox.setValue(kerugian1.getNamaMaterial());
		if (kerugian1.getTipeKerugian()!=null) {
			for(Comboitem comboitem : tipeKerugian1Combobox.getItems()) {
				if (comboitem.getValue().equals(kerugian1.getTipeKerugian())) {
					tipeKerugian1Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			tipeKerugian1Combobox.setSelectedItem(null);
			tipeKerugian1Combobox.setValue("");
		}
		if (kerugian1.getLembagaTerkait()!=null) {
			for(Comboitem comboitem : lembaga1Combobox.getItems()) {
				if (comboitem.getValue().equals(kerugian1.getLembagaTerkait())) {
					lembaga1Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			lembaga1Combobox.setSelectedItem(null);
			lembaga1Combobox.setValue("");
		}
		if (kerugian1.getKerugianJenis()!=null) {
			for(Comboitem comboitem : kerugianJenis1Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian1.getKerugianJenis().getNamaJenis())==0) {
					kerugianJenis1Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			kerugianJenis1Combobox.setSelectedItem(null);
			kerugianJenis1Combobox.setValue("");
		}
		if (kerugian1.getKerugianKondisi()!=null) {
			for(Comboitem comboitem : kondisi1Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian1.getKerugianKondisi().getNamaKondisi())==0) {
					kondisi1Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			kondisi1Combobox.setSelectedItem(null);
			kondisi1Combobox.setValue("");
		}
		jumlah1Intbox.setValue(kerugian1.getJumlah());
		if (kerugian1.getKerugianSatuan()!=null) {
			for(Comboitem comboitem : satuan1Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian1.getKerugianSatuan().getSatuan())==0) {
					satuan1Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			satuan1Combobox.setSelectedItem(null);
			satuan1Combobox.setValue("");
		}
		keterangan1Textbox.setValue(kerugian1.getKeterangan());
	}
		

	private void displayKerugian2_PihakLain(int currentIndex) {
		if (kerugian2List.isEmpty()) {
			namaPersMat2Textbox.setValue("");
			tipeKerugian2Combobox.setValue("");
			lembaga2Combobox.setValue("");
			kerugianJenis2Combobox.setValue("");
			kondisi2Combobox.setValue("");
			jumlah2Intbox.setValue(null);
			satuan2Combobox.setValue("");
			keterangan2Textbox.setValue("");
			
			return;
		}
		Kerugian kerugian2 = kerugian2List.get(currentIndex);
		
		namaPersMat2Textbox.setValue(kerugian2.getNamaMaterial());
		for(Comboitem comboitem : tipeKerugian2Combobox.getItems()) {
			if (comboitem.getValue().equals(kerugian2.getTipeKerugian())) {
				tipeKerugian2Combobox.setSelectedItem(comboitem);
				break;
			} else {
				tipeKerugian2Combobox.setSelectedItem(null);
				tipeKerugian2Combobox.setValue("");
			}
		}
		for(Comboitem comboitem : lembaga2Combobox.getItems()) {
			if (comboitem.getValue().equals(kerugian2.getLembagaTerkait())) {
				lembaga2Combobox.setSelectedItem(comboitem);
				break;
			} else {
				lembaga2Combobox.setSelectedItem(null);
				lembaga2Combobox.setValue("");
			}
		}
		if (kerugian2.getKerugianJenis()!=null) {
			for(Comboitem comboitem : kerugianJenis2Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian2.getKerugianJenis().getNamaJenis())==0) {
					kerugianJenis2Combobox.setSelectedItem(comboitem);
					break;
				}
			}			
		} else {
			kerugianJenis2Combobox.setSelectedItem(null);
			kerugianJenis2Combobox.setValue("");
		}
		if (kerugian2.getKerugianKondisi()!=null) {
			for(Comboitem comboitem : kondisi2Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian2.getKerugianKondisi().getNamaKondisi())==0) {
					kondisi2Combobox.setSelectedItem(comboitem);
					break;
				} 
			}			
		} else {
			kondisi2Combobox.setSelectedItem(null);
			kondisi2Combobox.setValue("");
		}
		jumlah2Intbox.setValue(kerugian2.getJumlah());
		if (kerugian2.getKerugianSatuan()!=null) {
			for(Comboitem comboitem : satuan2Combobox.getItems()) {
				if (comboitem.getLabel().compareTo(kerugian2.getKerugianSatuan().getSatuan())==0) {
					satuan2Combobox.setSelectedItem(comboitem);
					break;
				} 
			}			
		} else {
			satuan2Combobox.setSelectedItem(null);
			satuan2Combobox.setValue("");
		}
		keterangan2Textbox.setValue(kerugian2.getKeterangan());
	}	
	
	public void onClick$cancelKejadianLabel(Event event) throws Exception {
		log.info("Cancel Input Kejadian Baru...");
		
		// clear all 
		resetOtherComboboxes(4);
		
		if (getState().equals(State.NEW)) {
			setState(State.EMPTY);			
		} else {
			setState(State.VIEW);
		}
	}
	
	public void setStateKerugian0(State state) {
		this.stateKerugian0 = state;
		
		log.info("StateKerugian0 : "+this.stateKerugian0.toString());
		
		switch (state) {
		case VIEW:
			namaPersMat0Textbox.setDisabled(true);
			tipeKerugian0Combobox.setDisabled(true);
			lembaga0Combobox.setDisabled(true);
			kerugianJenis0Combobox.setDisabled(true);
			kondisi0Combobox.setDisabled(true);
			satuan0Combobox.setDisabled(true);
			jumlah0Intbox.setDisabled(true);
			keterangan0Textbox.setDisabled(true); 
		
			kerugian0AddButton.setVisible(true);
			kerugian0DeleteButton.setVisible(true);
			kerugian0EditButton.setVisible(true);
			kerugian0SaveButton.setVisible(false);
			kerugian0PrevButton.setVisible(true);
			kerugian0NextButton.setVisible(true);
			
			break;
		case NEW:
			namaPersMat0Textbox.setDisabled(false);
			namaPersMat0Textbox.setValue("");
			tipeKerugian0Combobox.setDisabled(false);
			tipeKerugian0Combobox.setValue("");
			tipeKerugian0Combobox.setSelectedItem(null);			
			lembaga0Combobox.setDisabled(false);
			lembaga0Combobox.setValue("");
			lembaga0Combobox.setSelectedItem(null);
			kerugianJenis0Combobox.setDisabled(false);
			kerugianJenis0Combobox.setValue("");
			kerugianJenis0Combobox.setSelectedItem(null);
			kondisi0Combobox.setDisabled(false);
			kondisi0Combobox.setValue("");
			kondisi0Combobox.setSelectedItem(null);
			satuan0Combobox.setDisabled(false);
			satuan0Combobox.setValue("");
			satuan0Combobox.setSelectedItem(null);
			jumlah0Intbox.setDisabled(false);
			jumlah0Intbox.setValue(null);
			keterangan0Textbox.setDisabled(false); 
			keterangan0Textbox.setValue("");

			kerugian0AddButton.setVisible(false);
			kerugian0DeleteButton.setVisible(false);
			kerugian0EditButton.setVisible(false);
			kerugian0SaveButton.setVisible(true);
			kerugian0PrevButton.setVisible(false);
			kerugian0NextButton.setVisible(false);
			
			break;
		case EDIT:
			namaPersMat0Textbox.setDisabled(false);
			tipeKerugian0Combobox.setDisabled(false);
			lembaga0Combobox.setDisabled(false);
			kerugianJenis0Combobox.setDisabled(false);
			kondisi0Combobox.setDisabled(false);
			satuan0Combobox.setDisabled(false);
			jumlah0Intbox.setDisabled(false);
			keterangan0Textbox.setDisabled(false); 

			kerugian0AddButton.setVisible(false);
			kerugian0DeleteButton.setVisible(false);
			kerugian0EditButton.setVisible(false);
			kerugian0SaveButton.setVisible(true);
			kerugian0PrevButton.setVisible(false);
			kerugian0NextButton.setVisible(false);
			
			break;
		case EMPTY:
			namaPersMat0Textbox.setDisabled(true);
			namaPersMat0Textbox.setValue("");			
			tipeKerugian0Combobox.setDisabled(true);
			tipeKerugian0Combobox.setValue("");
			tipeKerugian0Combobox.setSelectedItem(null);
			lembaga0Combobox.setDisabled(true);
			lembaga0Combobox.setValue("");
			lembaga0Combobox.setSelectedItem(null);
			kerugianJenis0Combobox.setDisabled(true);
			kerugianJenis0Combobox.setValue("");
			kerugianJenis0Combobox.setSelectedItem(null);
			kondisi0Combobox.setDisabled(true);
			kondisi0Combobox.setValue("");
			kondisi0Combobox.setSelectedItem(null);
			satuan0Combobox.setDisabled(true);
			satuan0Combobox.setValue("");
			satuan0Combobox.setSelectedItem(null);
			jumlah0Intbox.setDisabled(true);
			jumlah0Intbox.setValue(null);
			keterangan0Textbox.setDisabled(true); 
			keterangan0Textbox.setValue("");

			kerugian0AddButton.setVisible(false);
			kerugian0DeleteButton.setVisible(false);
			kerugian0EditButton.setVisible(false);
			kerugian0SaveButton.setVisible(false);
			kerugian0PrevButton.setVisible(false);
			kerugian0NextButton.setVisible(false);
			
			break;
		default:
			break;
		}
	}
	
	public State getStateKerugian0() {
		
		return this.stateKerugian0;
	}
	
	public void setStateKerugian1(State state) {
		this.stateKerugian1 = state;
		
		log.info("StateKerugian1 : "+this.stateKerugian1.toString());
		
		switch (state) {
		case VIEW:
			namaPersMat1Textbox.setDisabled(true);
			tipeKerugian1Combobox.setDisabled(true);
			lembaga1Combobox.setDisabled(true);
			kerugianJenis1Combobox.setDisabled(true);
			kondisi1Combobox.setDisabled(true);
			satuan1Combobox.setDisabled(true);
			jumlah1Intbox.setDisabled(true);
			keterangan1Textbox.setDisabled(true); 
		
			kerugian1AddButton.setVisible(true);
			kerugian1DeleteButton.setVisible(true);
			kerugian1EditButton.setVisible(true);
			kerugian1SaveButton.setVisible(false);
			kerugian1PrevButton.setVisible(true);
			kerugian1NextButton.setVisible(true);
			
			break;
		case NEW:
			namaPersMat1Textbox.setDisabled(false);
			namaPersMat1Textbox.setValue("");
			tipeKerugian1Combobox.setDisabled(false);			
			tipeKerugian1Combobox.setValue("");
			tipeKerugian1Combobox.setSelectedItem(null);			
			lembaga1Combobox.setDisabled(false);
			lembaga1Combobox.setValue("");
			lembaga1Combobox.setSelectedItem(null);
			kerugianJenis1Combobox.setDisabled(false);
			kerugianJenis1Combobox.setValue("");
			kerugianJenis1Combobox.setSelectedItem(null);
			kondisi1Combobox.setDisabled(false);
			kondisi1Combobox.setValue("");
			kondisi1Combobox.setSelectedItem(null);
			satuan1Combobox.setDisabled(false);
			satuan1Combobox.setValue("");
			satuan1Combobox.setSelectedItem(null);
			jumlah1Intbox.setDisabled(false);
			jumlah1Intbox.setValue(null);
			keterangan1Textbox.setDisabled(false); 
			keterangan1Textbox.setValue("");

			kerugian1AddButton.setVisible(false);
			kerugian1DeleteButton.setVisible(false);
			kerugian1EditButton.setVisible(false);
			kerugian1SaveButton.setVisible(true);
			kerugian1PrevButton.setVisible(false);
			kerugian1NextButton.setVisible(false);
			
			break;
		case EDIT:
			namaPersMat1Textbox.setDisabled(false);
			tipeKerugian1Combobox.setDisabled(false);
			lembaga1Combobox.setDisabled(false);
			kerugianJenis1Combobox.setDisabled(false);
			kondisi1Combobox.setDisabled(false);
			satuan1Combobox.setDisabled(false);
			jumlah1Intbox.setDisabled(false);
			keterangan1Textbox.setDisabled(false); 

			kerugian1AddButton.setVisible(false);
			kerugian1DeleteButton.setVisible(false);
			kerugian1EditButton.setVisible(false);
			kerugian1SaveButton.setVisible(true);
			kerugian1PrevButton.setVisible(false);
			kerugian1NextButton.setVisible(false);
			
			break;
		case EMPTY:
			namaPersMat1Textbox.setDisabled(true);
			namaPersMat1Textbox.setValue("");			
			tipeKerugian1Combobox.setDisabled(true);
			tipeKerugian1Combobox.setValue("");
			tipeKerugian1Combobox.setSelectedItem(null);			
			lembaga1Combobox.setDisabled(true);
			lembaga1Combobox.setValue("");
			lembaga1Combobox.setSelectedItem(null);
			kerugianJenis1Combobox.setDisabled(true);
			kerugianJenis1Combobox.setValue("");
			kerugianJenis1Combobox.setSelectedItem(null);
			kondisi1Combobox.setDisabled(true);
			kondisi1Combobox.setValue("");
			kondisi1Combobox.setSelectedItem(null);
			satuan1Combobox.setDisabled(true);
			satuan1Combobox.setValue("");
			satuan1Combobox.setSelectedItem(null);
			jumlah1Intbox.setDisabled(true);
			jumlah1Intbox.setValue(null);
			keterangan1Textbox.setDisabled(true); 
			keterangan1Textbox.setValue("");

			kerugian1AddButton.setVisible(false);
			kerugian1DeleteButton.setVisible(false);
			kerugian1EditButton.setVisible(false);
			kerugian1SaveButton.setVisible(false);
			kerugian1PrevButton.setVisible(false);
			kerugian1NextButton.setVisible(false);
			
			break;
		default:
			break;
		}
	}

	public State getStateKerugian1() {
		
		return this.stateKerugian1;
	}
	
	public void setStateKerugian2(State state) {
		this.stateKerugian2 = state;
		
		log.info("StateKerugian2 : "+this.stateKerugian2.toString());
		
		switch (state) {
		case VIEW:
			namaPersMat2Textbox.setDisabled(true);
			tipeKerugian2Combobox.setDisabled(true);
			lembaga2Combobox.setDisabled(true);
			kerugianJenis2Combobox.setDisabled(true);
			kondisi2Combobox.setDisabled(true);
			satuan2Combobox.setDisabled(true);
			jumlah2Intbox.setDisabled(true);
			keterangan2Textbox.setDisabled(true); 
		
			kerugian2AddButton.setVisible(true);
			kerugian2DeleteButton.setVisible(true);
			kerugian2EditButton.setVisible(true);
			kerugian2SaveButton.setVisible(false);
			kerugian2PrevButton.setVisible(true);
			kerugian2NextButton.setVisible(true);
			
			break;
		case NEW:
			namaPersMat2Textbox.setDisabled(false);
			namaPersMat2Textbox.setValue("");
			tipeKerugian2Combobox.setDisabled(false);
			tipeKerugian2Combobox.setValue("");
			tipeKerugian2Combobox.setSelectedItem(null);			
			lembaga2Combobox.setDisabled(false);
			lembaga2Combobox.setValue("");
			lembaga2Combobox.setSelectedItem(null);
			kerugianJenis2Combobox.setDisabled(false);
			kerugianJenis2Combobox.setValue("");
			kerugianJenis2Combobox.setSelectedItem(null);
			kondisi2Combobox.setDisabled(false);
			kondisi2Combobox.setValue("");
			kondisi2Combobox.setSelectedItem(null);
			satuan2Combobox.setDisabled(false);
			satuan2Combobox.setValue("");
			satuan2Combobox.setSelectedItem(null);
			jumlah2Intbox.setDisabled(false);
			jumlah2Intbox.setValue(null);
			keterangan2Textbox.setDisabled(false); 
			keterangan2Textbox.setValue("");

			kerugian2AddButton.setVisible(false);
			kerugian2DeleteButton.setVisible(false);
			kerugian2EditButton.setVisible(false);
			kerugian2SaveButton.setVisible(true);
			kerugian2PrevButton.setVisible(false);
			kerugian2NextButton.setVisible(false);
			
			break;
		case EDIT:
			namaPersMat2Textbox.setDisabled(false);
			tipeKerugian2Combobox.setDisabled(false);
			lembaga2Combobox.setDisabled(false);
			kerugianJenis2Combobox.setDisabled(false);
			kondisi2Combobox.setDisabled(false);
			satuan2Combobox.setDisabled(false);
			jumlah2Intbox.setDisabled(false);
			keterangan2Textbox.setDisabled(false); 

			kerugian2AddButton.setVisible(false);
			kerugian2DeleteButton.setVisible(false);
			kerugian2EditButton.setVisible(false);
			kerugian2SaveButton.setVisible(true);
			kerugian2PrevButton.setVisible(false);
			kerugian2NextButton.setVisible(false);
			
			break;
		case EMPTY:
			namaPersMat2Textbox.setDisabled(true);
			namaPersMat2Textbox.setValue("");			
			tipeKerugian2Combobox.setDisabled(true);
			tipeKerugian2Combobox.setValue("");
			tipeKerugian2Combobox.setSelectedItem(null);			
			lembaga2Combobox.setDisabled(true);
			lembaga2Combobox.setValue("");
			lembaga2Combobox.setSelectedItem(null);
			kerugianJenis2Combobox.setDisabled(true);
			kerugianJenis2Combobox.setValue("");
			kerugianJenis2Combobox.setSelectedItem(null);
			kondisi2Combobox.setDisabled(true);
			kondisi2Combobox.setValue("");
			kondisi2Combobox.setSelectedItem(null);
			satuan2Combobox.setDisabled(true);
			satuan2Combobox.setValue("");
			satuan2Combobox.setSelectedItem(null);
			jumlah2Intbox.setDisabled(true);
			jumlah2Intbox.setValue(null);
			keterangan2Textbox.setDisabled(true); 
			keterangan2Textbox.setValue("");

			kerugian2AddButton.setVisible(false);
			kerugian2DeleteButton.setVisible(false);
			kerugian2EditButton.setVisible(false);
			kerugian2SaveButton.setVisible(false);
			kerugian2PrevButton.setVisible(false);
			kerugian2NextButton.setVisible(false);
			
			break;
		default:
			break;
		}
	}
	
	public State getStateKerugian2() {
		
		return this.stateKerugian2;
	}
	
	private DocumentSerialNumber createSerialNumber(LocalDateTime currentDateTime, String documentCode) throws Exception {
		
		// get the serial number
		int serNum = getSerialNumberGenerator().getSerialNumber(documentCode, getZoneId(), currentDateTime);

		// create a new object
		DocumentSerialNumber serialNumber = new DocumentSerialNumber();
		serialNumber.setDocumentCode(getKotamaops().getDocumentCode());
		serialNumber.setCreatedAt(asDate(getCurrentLocalDateTime()));
		serialNumber.setEditedAt(asDate(getCurrentLocalDateTime()));
		serialNumber.setSerialDate(asDate(currentDateTime));
		serialNumber.setSerialNo(serNum);
		serialNumber.setSerialComp(formatSerialComp(documentCode, currentDateTime, serNum));
		
		return serialNumber;
	}
	
	
	public void onClick$saveKejadianLabel(Event event) throws Exception {
		log.info("Save Kejadian...");

		// obtain the object
		Kejadian userModKejadian = getKejadian();
		List<Kerugian> kerugianList = null;
		
		if (userModKejadian.getId().compareTo(Long.MIN_VALUE)==0) {
			log.info("new kejadian report. create new serial number.");
			// new serial number
			userModKejadian.setSerialNumber(getDocumentSerialNumber());
			// timestamp
			userModKejadian.setCreatedAt(asDate(getCurrentLocalDateTime()));
			userModKejadian.setEditedAt(asDate(getCurrentLocalDateTime()));

			// any kerugian data?
			kerugianList = new ArrayList<Kerugian>();
		} else {
			// no changes to serial number

			// timestamp
			userModKejadian.setEditedAt(asDate(getCurrentLocalDateTime()));
			
			// update kerugian data
			Kejadian kejadianKerugianByProxy =
					getKejadianDao().findKejadianKerugiansByProxy(userModKejadian.getId());
			kerugianList = kejadianKerugianByProxy.getKerugians();
		}
		
		// tgl pembuatan
		LocalDateTime twPembuatanLocalDateTime =
				getTwConversion().convertTwToLocalDateTime(
						twBuatTahunTextbox.getValue(),
						twBuatTanggalJamTextbox.getValue());
		userModKejadian.setTwPembuatanDateTime(asDate(twPembuatanLocalDateTime));
		userModKejadian.setTwPembuatanTimezone(twBuatTimeZoneCombobox.getSelectedItem().getValue());
		// tgl kejadian
		LocalDateTime twKejadianLocalDateTime =
				getTwConversion().convertTwToLocalDateTime(
						twJadiTahunTextbox.getValue(),
						twJadiTanggalJamTextbox.getValue());
		userModKejadian.setTwKejadianDateTime(asDate(twKejadianLocalDateTime));
		userModKejadian.setTwKejadianTimezone(
				(TimezoneInd) twJadiTimeZoneTextbox.getAttribute("twJadiTimeZoneInd"));

		// Kotamaops
		if (getKotamaopsType().compareTo(KotamaopsType.PUSDALOPS)==0) {
			log.info("kotamaops is: "+getKotamaopsType().name());
			
			// save based on user selection of the kotamaops
			userModKejadian.setKotamaops(kotamaopsCombobox.getSelectedItem().getValue());			
		} else {
			// check to see whether there're kotamaops under this kotamaops
			// for example kotamaops Angkatan Darat has several kotamaops
			Kotamaops kotamaopsByProxy = 
					getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
			List<Kotamaops> kotamaopsList =
					kotamaopsByProxy.getKotamaops();
			
			if (kotamaopsList.isEmpty()) {
				log.info("no kotamaops.  save using the settingsKotamaops");
				// user cannot choose, therefore assigned to settings kotamaops
				userModKejadian.setKotamaops(getKotamaops());				
			} else {
				log.info("save using the user selected kotamaops");
				// user's selection of the kotamaops
				userModKejadian.setKotamaops(kotamaopsCombobox.getSelectedItem().getValue());
			}
		}
		
		userModKejadian.setPropinsi(propCombobox.getSelectedItem().getValue());
		userModKejadian.setKabupatenKotamadya(kabupatenCombobox.getSelectedItem().getValue());
		userModKejadian.setKecamatan(kecamatanCombobox.getSelectedItem().getValue());
		userModKejadian.setKelurahan(kelurahanCombobox.getSelectedItem().getValue());
		
		userModKejadian.setKoordinatGps(koordGpsTextbox.getValue());
		userModKejadian.setKoordinatPeta(koordPetaTextbox.getValue());
		userModKejadian.setBujurLintang(bujurLintangTextbox.getValue());
		userModKejadian.setKampung(kampungTextbox.getValue());
		userModKejadian.setJalan(jalanTextbox.getValue());
		
		userModKejadian.setKronologis(kronologisTextbox.getValue());

		userModKejadian.setJenisKejadian(jenisKejadianCombobox.getSelectedItem().getValue());
		userModKejadian.setMotifKejadian(motifKejadianCombobox.getSelectedItem().getValue());
		userModKejadian.setPelakuKejadian(pelakuKejadianCombobox.getSelectedItem().getValue());
		userModKejadian.setKeteranganPelaku(keteranganPelakuTextbox.getValue());
		userModKejadian.setSasaran(sasaranTextbox.getValue());
		
		kerugianList.clear();
		if (kerugian0Checkbox.isChecked()) {
			log.info("collecting Kerugian-PihakKita data");
		
			kerugianList.addAll(kerugian0List);
		}
		if (kerugian1Checkbox.isChecked()) {
			log.info("collecting Kerugian-PihakMusuh data");
		
			kerugianList.addAll(kerugian1List);
		}
		if (kerugian2Checkbox.isChecked()) {
			log.info("collecting Kerugian-PihakLain data");
			
			kerugianList.addAll(kerugian2List);
		}
		
		userModKejadian.setKerugians(kerugianList);
		
		log.info(userModKejadian.toString());
		userModKejadian.getKerugians().forEach(kerugian->log.info(kerugian.toString()));
		Long id = null;
		if (userModKejadian.getId().compareTo(Long.MIN_VALUE)==0) {
			// save kejadian and get the kejadian ID
			id = getKejadianDao().save(userModKejadian);
			log.info("Successfully Saved Kejadian - new id: "+id);			
		} else {
			// update kejadian
			id = userModKejadian.getId();
			getKejadianDao().update(userModKejadian);
			log.info("Successfully Updated Kejadian - id: "+id);
		}
		// find the kejadian with ID and set
		setKejadian(getKejadianDao().findKejadianById(id));
		log.info(getKejadian().toString());
		
		// set to VIEW
		setState(State.VIEW);
		// display
		displayKejadianData();

		// re-index - to enable search
		// getKejadianDao().createIndexer();
	}

	@SuppressWarnings("unused")
	private void checkEmptyFields() throws Exception {
		if (twBuatTahunTextbox.getValue().isEmpty()) {
			throw new SuppressedException("Tw Pembuatan Kejadian Tahun belum diisi.");
		}
		if (twBuatTanggalJamTextbox.getValue().isEmpty()) {
			throw new SuppressedException("Tw Pembuatan Kejadian Tanggal dan Jam belum diisi.");
		}
		if (twJadiTanggalJamTextbox.getValue().isEmpty()) {
			throw new SuppressedException("Tw Kejadian Tanggal dan Jam belum diisi.");
		}
		if (kotamaopsCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Kotamaops belum dipilih.");
		}
		if (propCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Propinsis belum dipilih.");
		}
		if (kabupatenCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Kabupaten / Kotamadya belum dipilih.");
		}
		if (kecamatanCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Kecamatan belum dipilih.");
		}
		if (kelurahanCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Kelurahan belum dipilih.");
		}
		if (jenisKejadianCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Jenis Kejadian belum dipilih.");
		}
		if (motifKejadianCombobox.getValue().isEmpty()) {
			throw new SuppressedException("Motif Kejadian belum dipilih.");
		}
	}

	public KejadianDao getKejadianDao() {
		return kejadianDao;
	}

	public void setKejadianDao(KejadianDao kejadianDao) {
		this.kejadianDao = kejadianDao;
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

	public KotamaopsType getKotamaopsType() {
		return kotamaopsType;
	}

	public void setKotamaopsType(KotamaopsType kotamaopsType) {
		this.kotamaopsType = kotamaopsType;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public LocalDateTime getCurrentLocalDateTime() {
		return currentLocalDateTime;
	}

	public void setCurrentLocalDateTime(LocalDateTime currentLocalDateTime) {
		this.currentLocalDateTime = currentLocalDateTime;
	}

	public KotamaopsDao getKotamaopsDao() {
		return kotamaopsDao;
	}

	public void setKotamaopsDao(KotamaopsDao kotamaopsDao) {
		this.kotamaopsDao = kotamaopsDao;
	}

	public KejadianJenisDao getKejadianJenisDao() {
		return kejadianJenisDao;
	}

	public void setKejadianJenisDao(KejadianJenisDao kejadianJenisDao) {
		this.kejadianJenisDao = kejadianJenisDao;
	}

	public KejadianMotifDao getKejadianMotifDao() {
		return kejadianMotifDao;
	}

	public void setKejadianMotifDao(KejadianMotifDao kejadianMotifDao) {
		this.kejadianMotifDao = kejadianMotifDao;
	}

	public KejadianPelakuDao getKejadianPelakuDao() {
		return kejadianPelakuDao;
	}

	public void setKejadianPelakuDao(KejadianPelakuDao kejadianPelakuDao) {
		this.kejadianPelakuDao = kejadianPelakuDao;
	}

	public Kejadian getKejadian() {
		return kejadian;
	}

	public void setKejadian(Kejadian kejadian) {
		this.kejadian = kejadian;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public DocumentSerialNumber getDocumentSerialNumber() {
		return documentSerialNumber;
	}

	public void setDocumentSerialNumber(DocumentSerialNumber documentSerialNumber) {
		this.documentSerialNumber = documentSerialNumber;
	}

	public PropinsiDao getPropinsiDao() {
		return propinsiDao;
	}

	public void setPropinsiDao(PropinsiDao propinsiDao) {
		this.propinsiDao = propinsiDao;
	}

	public Kabupaten_KotamadyaDao getKabupaten_KotamadyaDao() {
		return kabupaten_KotamadyaDao;
	}

	public void setKabupaten_KotamadyaDao(Kabupaten_KotamadyaDao kabupaten_KotamadyaDao) {
		this.kabupaten_KotamadyaDao = kabupaten_KotamadyaDao;
	}

	public KecamatanDao getKecamatanDao() {
		return kecamatanDao;
	}

	public void setKecamatanDao(KecamatanDao kecamatanDao) {
		this.kecamatanDao = kecamatanDao;
	}

	public KelurahanDao getKelurahanDao() {
		return kelurahanDao;
	}

	public void setKelurahanDao(KelurahanDao kelurahanDao) {
		this.kelurahanDao = kelurahanDao;
	}

	public TwConversion getTwConversion() {
		return twConversion;
	}

	public void setTwConversion(TwConversion twConversion) {
		this.twConversion = twConversion;
	}


	public KerugianJenisDao getKerugianJenisDao() {
		return kerugianJenisDao;
	}


	public void setKerugianJenisDao(KerugianJenisDao kerugianJenisDao) {
		this.kerugianJenisDao = kerugianJenisDao;
	}


	public KerugianKondisiDao getKerugianKondisiDao() {
		return kerugianKondisiDao;
	}


	public void setKerugianKondisiDao(KerugianKondisiDao kerugianKondisiDao) {
		this.kerugianKondisiDao = kerugianKondisiDao;
	}


	public KerugianSatuanDao getKerugianSatuanDao() {
		return kerugianSatuanDao;
	}


	public void setKerugianSatuanDao(KerugianSatuanDao kerugianSatuanDao) {
		this.kerugianSatuanDao = kerugianSatuanDao;
	}


	public UserSecurityDetails getUserSecurityDetails() {
		return userSecurityDetails;
	}


	public void setUserSecurityDetails(UserSecurityDetails userSecurityDetails) {
		this.userSecurityDetails = userSecurityDetails;
	}


	public UserDao getUserDao() {
		return userDao;
	}


	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
