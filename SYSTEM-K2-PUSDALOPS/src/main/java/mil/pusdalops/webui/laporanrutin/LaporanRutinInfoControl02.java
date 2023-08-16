package mil.pusdalops.webui.laporanrutin;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import mil.pusdalops.domain.gmt.TimezoneInd;
import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kejadian.KejadianJenis;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.domain.settings.Settings;
import mil.pusdalops.persistence.kejadian.dao.KejadianDao;
import mil.pusdalops.persistence.kejadian.jenis.dao.KejadianJenisDao;
import mil.pusdalops.persistence.kotamaops.dao.KotamaopsDao;
import mil.pusdalops.persistence.laporanrutin.dao.LaporanRutinDao;
import mil.pusdalops.persistence.settings.dao.SettingsDao;
import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.common.PrintUtil;
import mil.pusdalops.webui.common.UniqueList;
import mil.pusdalops.webui.dialogs.DatetimeData;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class LaporanRutinInfoControl02 extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5010802710014380723L;

	private SettingsDao settingsDao;
	private KotamaopsDao kotamaopsDao;
	private KejadianJenisDao kejadianJenisDao; 
	private LaporanRutinDao laporanRutinDao;
	private KejadianDao kejadianDao;
	
	private Window laporanRutinInfoWin02;
	private Label jenisKejadianLabel, formTitleLabel, title01, title02, title03, title04;
	private Textbox twAwalTahunTextbox, twAwalTanggalJamTextbox, twAwalTimeZoneTextbox,
		twAkhirTahunTextbox, twAkhirTanggalJamTextbox, twAkhirTimeZoneTextbox;
	private Column idUrtCol, idNoCol, twCol, uraiCol, jenKejCol;
	private Grid kejadianGrid;
	private Button printButton, exportButton;
	private Vbox printVbox;
	private Combobox matraCombobox, jenisKejadianCombobox;
	
	private Settings settings;
	private Kotamaops kotamaops;
	private ZoneId zoneId;
	private boolean twAwalAkhirProper;
	private LocalDateTime currentLocalDateTime, awalLocalDateTime, akhirLocalDateTime;
	private KotamaopsType kotamaopsType;
	private Long matraDaratID, matraLautID, matraUdaraID;
	private String outputFileDir;
	private int seqNumber = 1;
	
	public static final Logger log = Logger.getLogger(LaporanRutinInfoControl02.class);
	private final long SETTINGS_DEFAULT_ID = 1L;
	
	public void onCreate$laporanRutinInfoWin02(Event event) throws Exception {
		log.info("Creating Laporan Rutin...");

		Properties mainProperties = new Properties();
		mainProperties.load(new FileInputStream("/pusdalops/application-back-end.properties"));
		
		matraDaratID = Long.parseLong((String) mainProperties.get("matra_darat.id")); 
		matraLautID = Long.parseLong((String) mainProperties.get("matra_laut.id"));
		matraUdaraID = Long.parseLong((String) mainProperties.get("matra_udara.id"));
		
		outputFileDir = (String) mainProperties.get("output.file.dir");
		
		setSettings(
				getSettingsDao().findSettingsById(SETTINGS_DEFAULT_ID));
		setKotamaops(
				getSettings().getSelectedKotamaops());
		formTitleLabel.setValue("Laporan | Laporan Rutin - Kotamaops : "+
				getKotamaops().getKotamaopsName());

		setMatraCombobox();
		
		// set the timezone for this kotamaops
		int timezoneOrdinal = getKotamaops().getTimeZone().ordinal();
		setZoneId(getKotamaops().getTimeZone().toZoneId(timezoneOrdinal));			

		// set current localdatetime
		setCurrentLocalDateTime(getLocalDateTime(getZoneId()));
		
		setupTwAwalAkhir();
		
		setReportTitle(null);
		
		setReportColumnTitle();
	}

	private void setMatraCombobox() {
		Comboitem comboitem;

		// all matra, equivalent to 'PUSDALOPS'
		comboitem = new Comboitem();
		comboitem.setLabel("-");
		comboitem.setValue(null);
		comboitem.setParent(matraCombobox);
		
		for(KotamaopsType kotamaopsType : KotamaopsType.values()) {
			if ((kotamaopsType.getValue()==4) || (kotamaopsType.getValue()==0)) {
				continue;
			}
			comboitem = new Comboitem();
			comboitem.setLabel(kotamaopsType.name());
			comboitem.setValue(kotamaopsType);
			comboitem.setParent(matraCombobox);
		}

		matraCombobox.setSelectedIndex(0);

		// pass null value -- equivalent to 'PUSDALOPS'
		setKotamaopsType(null);
	}

	public void onSelect$matraCombobox(Event event) throws Exception {
		KotamaopsType selMatra = matraCombobox.getSelectedItem().getValue();
		setReportTitle(selMatra);
		
		// kotamaopsType is Matra
		setKotamaopsType(selMatra);
	}
	
	private void setReportTitle(KotamaopsType kotamaopsType) {
		title01.setValue("DATA KEJADIAN MENONJOL");
		title02.setValue("BESERTA REKAPITULASI KERUGIAN PERSONEL AND MATERIIL TNI");
		if (kotamaopsType==null) {
			log.info("all matra");
			title03.setValue("DI SELURUH KOTAMAOPS TNI");			
		} else switch (kotamaopsType) {
			case MATRA_DARAT:
				title03.setValue("DI KOTAMAOPS MATRA DARAT");				
				break;
			case MATRA_UDARA:
				title03.setValue("DI KOTAMAOPS MATRA UDARA");				
				break;
			case MATRA_LAUT:	
				title03.setValue("DI KOTAMAOPS MATRA LAUT");
				break;
			default:
				break;
		}
		title04.setValue("PERIODE BULAN "+ (
				getLocalDateTimeString(awalLocalDateTime, "MMMM")+" "+
				getLocalDateTimeString(awalLocalDateTime, "YYYY")).toUpperCase());
	}

	private void setReportColumnTitle() {
		Label col01Label = new Label();
		col01Label.setPre(true);
		col01Label.setValue("No.\nURT");
		col01Label.setParent(idUrtCol);
		
		Label col02Label = new Label();
		col02Label.setValue("No.");
		col02Label.setParent(idNoCol);
		
		Label col03Label = new Label();
		col03Label.setValue("TW");
		col03Label.setParent(twCol);
		
		Label col04Label = new Label();
		col04Label.setValue("URAIAN KEJADIAN");
		col04Label.setParent(uraiCol);
		
		Label col05Label = new Label();
		col05Label.setPre(true);
		col05Label.setValue("JENIS\nKEJADIAN");
		col05Label.setParent(jenKejCol);
	}

	public void onClick$twAwalRubahButton(Event event) throws Exception {
		// create the data object
		DatetimeData datetimeData = new DatetimeData();
		// set the object
		datetimeData.setDialogWinTitle("Rubah TW Awal");
		datetimeData.setTimezoneInd(getKotamaops().getTimeZone());
		datetimeData.setZoneId(getZoneId());
		datetimeData.setLocalDateTime(getAwalLocalDateTime());

		// pass to the dialog window
		Map<String, DatetimeData> args = Collections.singletonMap("datetimeData", datetimeData);
		Window datetimeWin = (Window) Executions.createComponents(
				"/dialogs/DatetimeWinDialog.zul", laporanRutinInfoWin02, args);
		datetimeWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				DatetimeData datetimeData = (DatetimeData) event.getData();
				
				log.info(datetimeData.getLocalDateTime());
				setAwalLocalDateTime(datetimeData.getLocalDateTime());
				
				twAwalTahunTextbox.setValue(getLocalDateTimeString(datetimeData.getLocalDateTime(), "YYYY"));
				twAwalTanggalJamTextbox.setValue(
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "MM")+
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "dd")+".0000");
				
				if (getAwalLocalDateTime().isAfter(getAkhirLocalDateTime())) {
					setTwAwalAkhirProper(false);
					throw new Exception("TW Awal TIDAK melewati TW Akhir");
				} else {
					setTwAwalAkhirProper(true);
				}
				
				title04.setValue("PERIODE BULAN "+ (
						getLocalDateTimeString(awalLocalDateTime, "MMMM")+" "+
						getLocalDateTimeString(awalLocalDateTime, "YYYY")).toUpperCase());
			}
		});
		
		datetimeWin.doModal();		
	}
	
	public void onClick$twAkhirRubahButton(Event event) throws Exception {
		// create the data object
		DatetimeData datetimeData = new DatetimeData();
		// set the object
		datetimeData.setDialogWinTitle("Rubah TW Akhir");
		datetimeData.setTimezoneInd(getKotamaops().getTimeZone());
		datetimeData.setZoneId(getZoneId());
		datetimeData.setLocalDateTime(getAkhirLocalDateTime());

		// pass to the dialog window
		Map<String, DatetimeData> args = Collections.singletonMap("datetimeData", datetimeData);
		Window datetimeWin = (Window) Executions.createComponents(
				"/dialogs/DatetimeWinDialog.zul", laporanRutinInfoWin02, args);
		datetimeWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				DatetimeData datetimeData = (DatetimeData) event.getData();
				
				log.info(datetimeData.getLocalDateTime());
				setAkhirLocalDateTime(datetimeData.getLocalDateTime());
				
				twAkhirTahunTextbox.setValue(getLocalDateTimeString(datetimeData.getLocalDateTime(), "YYYY"));
				twAkhirTanggalJamTextbox.setValue(
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "MM")+
						getLocalDateTimeString(datetimeData.getLocalDateTime(), "dd")+".0000");

				if (getAkhirLocalDateTime().isBefore(getAwalLocalDateTime())) {
					setTwAwalAkhirProper(false);
					throw new Exception("TW Akhir TIDAK mengawali TW Awal");
				} else {
					setTwAwalAkhirProper(true);
				}
			}
		});
		
		datetimeWin.doModal();		
	}
	
	private void setupTwAwalAkhir() {		
		LocalDate twAwalDate = getFirstdateOfTheMonth(asLocalDate(getCurrentLocalDateTime()));
		LocalTime twNoTime = LocalTime.of(0, 0, 0);
		
		// awal
		twAwalTahunTextbox.setValue(
				getLocalDateTimeString(asLocalDateTime(twAwalDate, twNoTime), "YYYY"));
		twAwalTanggalJamTextbox.setValue(
				getLocalDateTimeString(asLocalDateTime(twAwalDate, twNoTime), "MM")+
				getLocalDateTimeString(asLocalDateTime(twAwalDate, twNoTime), "dd")+".0000");
		twAwalTimeZoneTextbox.setValue(getKotamaops().getTimeZone().toString());

		// set awal
		setAwalLocalDateTime(asLocalDateTime(twAwalDate, twNoTime));
		
		// akhir
		twAkhirTahunTextbox.setValue(getLocalDateTimeString(getCurrentLocalDateTime(), "YYYY"));
		twAkhirTanggalJamTextbox.setValue(
				getLocalDateTimeString(getCurrentLocalDateTime(), "MM")+
				getLocalDateTimeString(getCurrentLocalDateTime(), "dd")+".0000");
		twAkhirTimeZoneTextbox.setValue(getKotamaops().getTimeZone().toString());
		
		// set akhir
		setAkhirLocalDateTime(asLocalDateTime(getCurrentLocalDateTime().toLocalDate(), twNoTime));
		
		setTwAwalAkhirProper(true);
	}

	private void setupJenisKejadianFromKejadianList(List<Kejadian> kejadianList) throws Exception {
		// clear the comboitem
		jenisKejadianCombobox.getItems().clear();
		// create a unique list
		UniqueList ul = new UniqueList();
		for (Kejadian kejadian : kejadianList) {
			ul.add(kejadian.getJenisKejadian());
		}
		Object[] contents = ul.toObjectArray();
		// setup KejadianJenis list
		List<KejadianJenis> kejadianJenisList = new ArrayList<KejadianJenis>();
		for (Object object : contents) {
			KejadianJenis kejadianJenis = new KejadianJenis();
			kejadianJenis = (KejadianJenis) object;
			
			kejadianJenisList.add(kejadianJenis);
		}
		// sort
		kejadianJenisList.sort((o1, o2) -> {
			return Long.compare(o1.getId(), o2.getId());
		});
		// kejadianJenisList.forEach(j -> log.info(j.getNamaJenis()));
		
		Comboitem comboitem;
		
		// selection for all
		comboitem = new Comboitem();
		comboitem.setLabel("-");
		comboitem.setValue(null);
		comboitem.setParent(jenisKejadianCombobox);
		
		for (KejadianJenis kejadianJenis : kejadianJenisList) {
			comboitem = new Comboitem();
			comboitem.setLabel(kejadianJenis.getNamaJenis());
			comboitem.setValue(kejadianJenis);
			comboitem.setParent(jenisKejadianCombobox);
		}
		
		jenisKejadianCombobox.setSelectedIndex(0);
	}	
	
	public void onSelect$jenisKejadianCombobox(Event event) throws Exception {
		KejadianJenis selJenKejadian = jenisKejadianCombobox.getSelectedItem().getValue();
		List<Kejadian> kejadianList = getAllKejadianByDate();
		
		if (selJenKejadian==null) {
			log.info("all jenis kejadian");
			
			kejadianGrid.setModel(new ListModelList<KejadianPrintData>(
					getKotamaopsKejadianDisplayList(null, kejadianList)));
			kejadianGrid.setRowRenderer(getKotamaopsKejadianRowRenderer());
		} else {
			log.info(selJenKejadian.toString());
			
			kejadianGrid.setModel(new ListModelList<KejadianPrintData>(
					getKotamaopsKejadianDisplayList(selJenKejadian, kejadianList)));
			kejadianGrid.setRowRenderer(getKotamaopsKejadianRowRenderer());			
		}
	}
	
	public void onClick$executeButton(Event event) throws Exception {		
		List<Kejadian> kejadianList = getAllKejadianByDate();
		// make combobox visible
		jenisKejadianLabel.setVisible(true);
		jenisKejadianCombobox.setVisible(true);
		// populate the jenis kejadian combobox
		setupJenisKejadianFromKejadianList(kejadianList);		

		List<KejadianPrintData> kotamaopsKejadianList = 
				getKotamaopsKejadianDisplayList(null, kejadianList);
		
		kejadianGrid.setModel(new ListModelList<KejadianPrintData>(kotamaopsKejadianList));
		kejadianGrid.setRowRenderer(getKotamaopsKejadianRowRenderer());
		
		printButton.setVisible(true);
		exportButton.setVisible(true);
	}

	private List<Kejadian> getAllKejadianByDate() throws Exception {
		List<Kejadian> kejadianList = null;
		Kotamaops kotamaopsMatra = null;
		
		if (getKotamaops().getKotamaopsType().equals(KotamaopsType.PUSDALOPS)) {

			if (getKotamaopsType()==null) {
				// null is equivalent to 'PUSADALOPS'
				log.info("list kejadian in all matras...");
				Kotamaops kotamaopsKotamaopsByProxy = 
						getKotamaopsDao().findKotamaopsKotamaopsByProxy(getKotamaops().getId());
				List<Kotamaops> kotamaopsList = kotamaopsKotamaopsByProxy.getKotamaops();
				
				kejadianList = getLaporanRutinDao().findAllKejadianInKotamaops(kotamaopsList, 
						asDate(awalLocalDateTime), asDate(akhirLocalDateTime));							
			} else {
				// list only kotamaops filtered by matra -- kotamaopsType
				log.info("list kejadian in matra: "+getKotamaopsType().name());
				
				switch (getKotamaopsType()) {
				case MATRA_DARAT:
					// find out which kotamaops (in db) is the kotamaopsType (or matra) 
					kotamaopsMatra = getKotamaopsDao().findKotamaopsById(matraDaratID);					
					break;
				case MATRA_LAUT:
					// find out which kotamaops (in db) is the kotamaopsType (or matra) 
					kotamaopsMatra = getKotamaopsDao().findKotamaopsById(matraLautID);					
					break;
				case MATRA_UDARA:
					// find out which kotamaops (in db) is the kotamaopsType (or matra) 
					kotamaopsMatra = getKotamaopsDao().findKotamaopsById(matraUdaraID);										
					break;
				default:
					break;
				}
				
				log.info(kotamaopsMatra.toString());
				// find all kotamaops under this matra
				Kotamaops kotamaopsKotamaopsMatraByProxy = getKotamaopsDao().findKotamaopsKotamaopsByProxy(kotamaopsMatra.getId());
				List<Kotamaops> kotamaopsList = kotamaopsKotamaopsMatraByProxy.getKotamaops();
				
				kotamaopsList.forEach(kotamaops->log.info(kotamaops.toString()));
				kejadianList = getLaporanRutinDao().findAllKejadianInKotamaops(kotamaopsList, 
						asDate(awalLocalDateTime), asDate(akhirLocalDateTime));							
			}
			
		} else {
			kejadianList = getLaporanRutinDao().findAllKejadianByKotamaops(getKotamaops(),
					asDate(awalLocalDateTime), asDate(akhirLocalDateTime));
		}
		
		return kejadianList;
	}
	
	private RowRenderer<KejadianPrintData> getKotamaopsKejadianRowRenderer() {
		
		return new RowRenderer<KejadianPrintData>() {
			
			@Override
			public void render(Row row, KejadianPrintData kejadianPrintData, int index) throws Exception {
				row.setSclass("kejadian-content");

				if (kejadianPrintData.getUraianKejadian()==null) {
					// display the kotamaops
					Cell kotamaopsCell = new Cell();
					kotamaopsCell.setSclass("kotamaopsCell");
					
					Label noUrtLabel = new Label();
					// noUrtLabel.setValue(String.valueOf(index+1)+".");
					noUrtLabel.setValue(kejadianPrintData.getNamaKotamaops());
					noUrtLabel.setParent(kotamaopsCell);
					
					kotamaopsCell.setColspan(5);
					kotamaopsCell.setStyle("text-align:left;");
					kotamaopsCell.setParent(row);					
				} else {
					// no urut
					Cell noUrtCell = new Cell();
					noUrtCell.setSclass("noUrtCell");
					
					Label noUrtLabel = new Label();
					// noUrtLabel.setValue(String.valueOf(index+1)+".");
					noUrtLabel.setValue(String.valueOf(kejadianPrintData.getNoUrt()));
					noUrtLabel.setParent(noUrtCell);
					
					noUrtCell.setParent(row);
					
					// no
					Cell noCell = new Cell();
					noCell.setSclass("noCell");
					
					Label noLabel = new Label();
					noLabel.setValue(String.valueOf(kejadianPrintData.getNoKejadianKotamaops()));
					noLabel.setParent(noCell);
					
					noCell.setParent(row);
					
					// tw
					Cell twCell = new Cell();
					twCell.setClass("twCell");
					
					Label twLabel = new Label();
					// twLabel.setValue(getLocalDateTimeString(asLocalDateTime(kejadian.getTwKejadianDateTime()), "MMdd.HHmm"));
					twLabel.setValue(kejadianPrintData.getTw());
					twLabel.setParent(twCell);
					
					twCell.setParent(row);
					
					// uraian kejadian
					
					Cell uraiCell = new Cell();
					uraiCell.setSclass("uraiCell");
					
					Label uraiLabel = new Label();
					// uraiLabel.setValue(kejadian.getKronologis());
					uraiLabel.setValue(kejadianPrintData.getUraianKejadian());
					uraiLabel.setParent(uraiCell);
					
					uraiCell.setParent(row);
					
					// jenis kejadian
					
					Cell jenKejCell = new Cell();
					jenKejCell.setSclass("jenKejCell");
					
					Label jenKejLabel = new Label();
					// jenKejLabel.setValue(kejadian.getJenisKejadian().getNamaJenis());
					jenKejLabel.setValue(kejadianPrintData.getJenisKejadian());
					jenKejLabel.setParent(jenKejCell);
					
					jenKejCell.setParent(row);
				}
				
			}


		};
	}	
	

	public void onClick$resetButton(Event event) throws Exception {
		// hide combobox
		jenisKejadianLabel.setVisible(false);
		jenisKejadianCombobox.setVisible(false);
		// clear combobox
		jenisKejadianCombobox.getItems().clear();
		jenisKejadianCombobox.setValue("");
		
		kejadianGrid.setModel(new ListModelList<Kejadian>());
		kejadianGrid.setRowRenderer(getKotamaopsKejadianRowRenderer());

		printButton.setVisible(false);
		exportButton.setVisible(false);
	}
	
	public void onClick$printButton(Event event) throws Exception {
		PrintUtil.print(printVbox);
	}
	
	private List<KejadianPrintData> getKotamaopsKejadianDisplayList(KejadianJenis filteredByJenisKejadian, List<Kejadian> kejadianList) throws Exception {
		List<Kotamaops> kotamaopsList = new ArrayList<Kotamaops>();
		
		for (Kejadian kejadian : kejadianList) {
			Kejadian kejadianKotamaopsByProxy =
					getKejadianDao().findKejadianKotamaopsByProxy(kejadian.getId());

			kotamaopsList.add(kejadianKotamaopsByProxy.getKotamaops());
		}

		kotamaopsList.sort((o1, o2) -> {
			return Integer.compare(o1.getKotamaopsSequence(), o2.getKotamaopsSequence());
		});

		UniqueList ul = new UniqueList();
		
		for (Kotamaops kotamaops : kotamaopsList) {
			ul.add(kotamaops.getId());			
		}
		Object[] content = ul.toObjectArray();
		// log.info(Arrays.toString(content));
		
		List<KejadianPrintData> kejadianPrintDataList =
				new ArrayList<KejadianPrintData>();
		seqNumber = 1;
		
		for (Object obj : content) {
			Long id = (Long) obj;
			// log.info(id);
			
			Kotamaops kotamaops =
					getKotamaopsDao().findKotamaopsById(id);
			// log.info(kotamaops.toString());
			// log.info(getAwalLocalDateTime().toString());
			// log.info(getAkhirLocalDateTime().toString());
			
			List<Kejadian> kejadianListByKotamaops = 
					getKejadianDao().findAllKejadianByKotamaopsByTwKejadian(kotamaops, 
							getAwalLocalDateTime().toString(), getAkhirLocalDateTime().toString());
			// kejadianListByKotamaops.forEach(k -> log.info(k.toString()));
			
			if (filteredByJenisKejadian != null) {
				List<Kejadian> filteredByJenisKejadianList = new ArrayList<Kejadian>();
				
				for (Kejadian kejadian : kejadianListByKotamaops) {
					if (kejadian.getJenisKejadian().equals(filteredByJenisKejadian)) {
						// log.info(kejadian.getJenisKejadian().getNamaJenis()+" - "+selJenKejadian.getNamaJenis());
						filteredByJenisKejadianList.add(kejadian);
					}
				}
				
				if (!filteredByJenisKejadianList.isEmpty()) {
					kejadianPrintDataList.addAll(
							getKejadianPrintDataList(kotamaops, filteredByJenisKejadianList));					
				}
				
			} else {

				kejadianPrintDataList.addAll(
					getKejadianPrintDataList(kotamaops, kejadianListByKotamaops));
		
			}
			// kejadianPrintDataList.forEach(k -> log.info(k.toString()));
		}
		
		
		return kejadianPrintDataList;
	}
	
	private List<KejadianPrintData> getKejadianPrintDataList(Kotamaops kotamaops, List<Kejadian> kejadianList) {
		List<KejadianPrintData> kejadianPrintDataList = new ArrayList<KejadianPrintData>();
		int seqNumberKotamaops = 1;
		
		KejadianPrintData kotamaopsKejadianPrintData = new KejadianPrintData();
		kotamaopsKejadianPrintData.setNamaKotamaops(kotamaops.getKotamaopsName());
		kotamaopsKejadianPrintData.setKotamaopsSequence(kotamaops.getKotamaopsSequence());
		kotamaopsKejadianPrintData.setNoUrt(0);
		kotamaopsKejadianPrintData.setNoKejadianKotamaops(0);
		kotamaopsKejadianPrintData.setTw(null);
		kotamaopsKejadianPrintData.setUraianKejadian(null);
		kotamaopsKejadianPrintData.setJenisKejadian(null);
		
		kejadianPrintDataList.add(kotamaopsKejadianPrintData);		
		
		for (Kejadian kejadian : kejadianList) {
			TimezoneInd timezoneInd = kejadian.getTwKejadianTimezone();
			int timezoneIndOrdinal = timezoneInd.ordinal();
			ZoneId zoneId = timezoneInd.toZoneId(timezoneIndOrdinal);
						
			KejadianPrintData kejadianPrintData = new KejadianPrintData();
			kejadianPrintData.setNoUrt(seqNumber);
			kejadianPrintData.setNoKejadianKotamaops(seqNumberKotamaops);
			kejadianPrintData.setTw(getLocalDateTimeString(asLocalDateTime(kejadian.getTwKejadianDateTime(), zoneId), "MMdd.HHmm"));
			kejadianPrintData.setUraianKejadian(kejadian.getKronologis()+" ["+
					kejadian.getSerialNumber().getSerialComp()+"]");
			kejadianPrintData.setJenisKejadian(kejadian.getJenisKejadian().getNamaJenis());
			
			kejadianPrintDataList.add(kejadianPrintData);
			
			seqNumberKotamaops++;
			seqNumber++;
		}

		return kejadianPrintDataList;
	}

	public void onClick$exportButton(Event event) throws Exception {
		log.info("export report to ms document");
				
		// export report to a file and save in local dir in the server
		String exportFilePathName = outputFileDir+getUniqueFileName()+".docx";
		
		// get all kejadian
		List<Kejadian> kejadianList = getAllKejadianByDate();
		
		// transform kejadian list to print objects KotamaopsKejadianPrintData
		// including JenisKejadian filter if any
		List<KotamaopsKejadianPrintData> kotamaopsKejadianList = 
				getKotamaopsKejadianPrintList(kejadianList);
		
		// dataField and dataList for Jasperreport
		HashMap<String, Object> dataField = new HashMap<String, Object>();
		HashMap<String, Object> dataList = new HashMap<String, Object>();
		
		dataField.put("wilayahMatra", title03.getValue());
		dataField.put("laporanPeriode", title04.getValue());
		dataField.put("kotamaopsKejadian", kotamaopsKejadianList);
		dataList.put("details", null);

		// export report
		File exportFile = jasperReportExport(dataField, dataList, exportFilePathName);

		// save to client directory
		Filedownload.save(exportFile, "docx");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private File jasperReportExport(HashMap<String, Object> dataField, HashMap<String, Object> dataList,
			String exportFilePathName) throws JRException {
		String key = "";
		Collection<?> value = null;
		JRBeanCollectionDataSource list = null;
		
		Collection<Map<String, ?>> datasource = new ArrayList<Map<String, ?>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if (dataList != null) {
			Set dataListSet = dataList.keySet();
			for (Iterator i = dataListSet.iterator(); i.hasNext();) {
				key = (String) i.next();
				value = (Collection<?>) dataList.get(key);
				list = new JRBeanCollectionDataSource(value);
				dataField.put(key, list);
			}
		}
		
		datasource.add(dataField);
		
		JRMapCollectionDataSource ds = new JRMapCollectionDataSource(datasource);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport("/pusdalops/jasper/Laporan_Rutin_A4.jasper", map, ds);
		
		Exporter exporter = new JRDocxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		
		File exportReportFile = new File(exportFilePathName);
		if (exportReportFile.exists()) {
			log.info("Overwrite : "+exportFilePathName);
		} else {
			log.info("New File : "+exportFilePathName);
		}
		
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportReportFile));
		exporter.exportReport();
		
		log.info("Export Sucess !!!");
		
		return exportReportFile;
	}
	
	private String getUniqueFileName() {
		setCurrentLocalDateTime(getLocalDateTime(getZoneId()));
		
		String baseFilename = "Laporan_Rutin_";
		String year = getLocalDateTimeString(getCurrentLocalDateTime(), "YYYY");
		String month = getLocalDateTimeString(getCurrentLocalDateTime(), "MM");
		String day = getLocalDateTimeString(getCurrentLocalDateTime(), "dd");
		String time = getLocalDateTimeString(getCurrentLocalDateTime(), "HHmmss");
		
		return baseFilename+year+month+day+time;
	}

	private List<KotamaopsKejadianPrintData> getKotamaopsKejadianPrintList(List<Kejadian> kejadianList) throws Exception {
		List<Kotamaops> kotamaopsList = new ArrayList<Kotamaops>();
		
		for (Kejadian kejadian : kejadianList) {
			Kejadian kejadianKotamaopsByProxy =
					getKejadianDao().findKejadianKotamaopsByProxy(kejadian.getId());

			kotamaopsList.add(kejadianKotamaopsByProxy.getKotamaops());
		}
		
		kotamaopsList.sort((o1, o2) -> {
			return Integer.compare(o1.getKotamaopsSequence(), o2.getKotamaopsSequence());
		});
				
		UniqueList ul = new UniqueList();
		
		for (Kotamaops kotamaops : kotamaopsList) {
			ul.add(kotamaops.getId());			
		}
		
		Object[] content = ul.toObjectArray();
		log.info("Kotamaops ID: "+Arrays.toString(content));
		
		List<KotamaopsKejadianPrintData> kotamaposKejadianPrintDataList =
				new ArrayList<KotamaopsKejadianPrintData>();
		seqNumber = 1;
		
		// using jenisKejadian filter ?
		KejadianJenis filteredByJenisKejadian = jenisKejadianCombobox.getSelectedItem().getValue();
		
		for (Object obj : content) {
			Long id = (Long) obj;
			// log.info(id);
			
			Kotamaops kotamaops =
					getKotamaopsDao().findKotamaopsById(id);
			// log.info(kotamaops.toString());
			// log.info(getAwalLocalDateTime().toString());
			// log.info(getAkhirLocalDateTime().toString());
			
			List<Kejadian> kejadianListByKotamaops = 
					getKejadianDao().findAllKejadianByKotamaopsByTwKejadian(kotamaops, 
							getAwalLocalDateTime().toString(), getAkhirLocalDateTime().toString());
			// kejadianListByKotamaops.forEach(k -> log.info(k.toString()));
			
			if (filteredByJenisKejadian != null) {
				List<Kejadian> filteredByJenisKejadianList = new ArrayList<Kejadian>();
				
				for (Kejadian kejadian : kejadianListByKotamaops) {
					if (kejadian.getJenisKejadian().equals(filteredByJenisKejadian)) {
						filteredByJenisKejadianList.add(kejadian);
					}
				}
				
				if (!filteredByJenisKejadianList.isEmpty()) {
					kotamaposKejadianPrintDataList.add(
							getKotamaopsKejadianPrintData(kotamaops, filteredByJenisKejadianList));					
				}
				
			} else {
				kotamaposKejadianPrintDataList.add(
						getKotamaopsKejadianPrintData(kotamaops, kejadianListByKotamaops));
				
			}
			
			// kotamaposKejadianPrintDataList.forEach(k -> log.info(k.toString()));
		}
		
		return kotamaposKejadianPrintDataList;
	}

	private KotamaopsKejadianPrintData getKotamaopsKejadianPrintData(Kotamaops kotamaops,
			List<Kejadian> kejadianListByKotamaops) {
		
		KotamaopsKejadianPrintData kotamaopsKejadianPrintData = 
				new KotamaopsKejadianPrintData();
		
		kotamaopsKejadianPrintData.setNamaKotamaops(kotamaops.getKotamaopsName());
		kotamaopsKejadianPrintData.setKejadianPrintData(getKejadianListByKotamaopsPrintData(kejadianListByKotamaops));
		
		return kotamaopsKejadianPrintData;
	}

	private List<KejadianPrintData> getKejadianListByKotamaopsPrintData(List<Kejadian> kejadianListByKotamaops) {
		
		List<KejadianPrintData> kejadianPrintDataList = new ArrayList<KejadianPrintData>();
		int seqNumberKotamaops = 1;
		
		for (Kejadian kejadian : kejadianListByKotamaops) {
			TimezoneInd timezoneInd = kejadian.getTwKejadianTimezone();
			int timezoneIndOrdinal = timezoneInd.ordinal();
			ZoneId zoneId = timezoneInd.toZoneId(timezoneIndOrdinal);
			
			KejadianPrintData kejadianPrintData = new KejadianPrintData();
			kejadianPrintData.setNoUrt(seqNumber);
			kejadianPrintData.setNoKejadianKotamaops(seqNumberKotamaops);
			kejadianPrintData.setTw(getLocalDateTimeString(asLocalDateTime(kejadian.getTwKejadianDateTime(), zoneId), "MMdd.HHmm"));
			kejadianPrintData.setUraianKejadian(kejadian.getKronologis()+" ["+
					kejadian.getSerialNumber().getSerialComp()+"]");
			kejadianPrintData.setJenisKejadian(kejadian.getJenisKejadian().getNamaJenis());
			
			kejadianPrintDataList.add(kejadianPrintData);
			
			seqNumberKotamaops++;
			seqNumber++;
		}
		
		return kejadianPrintDataList;
	}

	public SettingsDao getSettingsDao() {
		return settingsDao;
	}
	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
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
	public LaporanRutinDao getLaporanRutinDao() {
		return laporanRutinDao;
	}
	public void setLaporanRutinDao(LaporanRutinDao laporanRutinDao) {
		this.laporanRutinDao = laporanRutinDao;
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

	public ZoneId getZoneId() {
		return zoneId;
	}

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public boolean isTwAwalAkhirProper() {
		return twAwalAkhirProper;
	}

	public void setTwAwalAkhirProper(boolean twAwalAkhirProper) {
		this.twAwalAkhirProper = twAwalAkhirProper;
	}

	public LocalDateTime getCurrentLocalDateTime() {
		return currentLocalDateTime;
	}

	public void setCurrentLocalDateTime(LocalDateTime currentLocalDateTime) {
		this.currentLocalDateTime = currentLocalDateTime;
	}

	public LocalDateTime getAwalLocalDateTime() {
		return awalLocalDateTime;
	}

	public void setAwalLocalDateTime(LocalDateTime awalLocalDateTime) {
		this.awalLocalDateTime = awalLocalDateTime;
	}

	public LocalDateTime getAkhirLocalDateTime() {
		return akhirLocalDateTime;
	}

	public void setAkhirLocalDateTime(LocalDateTime akhirLocalDateTime) {
		this.akhirLocalDateTime = akhirLocalDateTime;
	}

	public KejadianDao getKejadianDao() {
		return kejadianDao;
	}

	public void setKejadianDao(KejadianDao kejadianDao) {
		this.kejadianDao = kejadianDao;
	}

	public KotamaopsType getKotamaopsType() {
		return kotamaopsType;
	}

	public void setKotamaopsType(KotamaopsType kotamaopsType) {
		this.kotamaopsType = kotamaopsType;
	}

}
