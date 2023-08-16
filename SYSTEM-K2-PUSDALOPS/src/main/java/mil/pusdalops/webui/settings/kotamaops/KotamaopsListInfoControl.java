package mil.pusdalops.webui.settings.kotamaops;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
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
import org.zkoss.zul.Window;

import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.persistence.kotamaops.dao.KotamaopsDao;
import mil.pusdalops.webui.common.GFCBaseController;

public class KotamaopsListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8194729544364348368L;

	private KotamaopsDao kotamaopsDao;
	
	private Window kotamaopsListInfoWin;
	private Label formTitleLabel, infoResultlabel;
	private Listbox kotamaopsListbox;
	private Combobox kotamaopsMatraCombobox;
	
	private List<Kotamaops> kotamaopsList;
	private long matraDaratID;
	private long matraLautID;
	private long matraUdaraID;
	
	private final int KOTAMAOPS_PUST_IDX = 0;
	private static final Logger log = Logger.getLogger(KotamaopsListInfoControl.class);
	
	public void onCreate$kotamaopsListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Settings | Kotamaops");
		
 		Properties mainProperties = new Properties();
		mainProperties.load(new FileInputStream("/pusdalops/application-back-end.properties"));
		
		matraDaratID = Long.parseLong((String) mainProperties.get("matra_darat.id")); 
		matraLautID = Long.parseLong((String) mainProperties.get("matra_laut.id"));
		matraUdaraID = Long.parseLong((String) mainProperties.get("matra_udara.id"));		
		
		loadKotamaopsMatraCombobox();
		
		// load data
		loadKotamaops();
		
		// display
		displayKotamaops();
	}

	private void loadKotamaopsMatraCombobox() {
		Comboitem comboitem;
		// semua matra
		comboitem = new Comboitem();
		comboitem.setLabel("--Semua Matra--");
		comboitem.setValue(null);
		comboitem.setParent(kotamaopsMatraCombobox);
		// matra lain -- kecuali pusdalops
		for (KotamaopsType kotamaopsMatra : KotamaopsType.values()) {
			if (kotamaopsMatra.equals(KotamaopsType.PUSDALOPS)||(kotamaopsMatra.equals(KotamaopsType.LAIN_LAIN))) {
				// don't add
			} else {
				comboitem = new Comboitem();
				comboitem.setLabel(kotamaopsMatra.toString());
				comboitem.setValue(kotamaopsMatra);
				comboitem.setParent(kotamaopsMatraCombobox);
			}
		}
		// default -- semua matra
		kotamaopsMatraCombobox.setSelectedIndex(0);
	}
	
	private void loadKotamaops() throws Exception {
		setKotamaopsList(
				getKotamaopsDao().findAllKotamaops());
		
		// remove kotamaops pusat
		getKotamaopsList().remove(KOTAMAOPS_PUST_IDX);
				
		// remove angkatan darat
		for (int i = 0; i < kotamaopsList.size(); i++) {
			Kotamaops kotamaops = kotamaopsList.get(i);
			if (kotamaops.getId().compareTo(matraDaratID)==0) {
				kotamaopsList.remove(i);
				break;
			}
		}
		// remove angkatan Laut
		for (int i = 0; i < kotamaopsList.size(); i++) {
			Kotamaops kotamaops = kotamaopsList.get(i);
			if (kotamaops.getId().compareTo(matraLautID)==0) {
				kotamaopsList.remove(i);
				break;
			}
		}
		// remove angkatan Udara
		for (int i = 0; i < kotamaopsList.size(); i++) {
			Kotamaops kotamaops = kotamaopsList.get(i);
			if (kotamaops.getId().compareTo(matraUdaraID)==0) {
				kotamaopsList.remove(i);
				break;
			}
		}
		
		// sort
		getKotamaopsList().sort((o1, o2) -> {
			return Integer.compare(o1.getKotamaopsSequence(), o2.getKotamaopsSequence());
		});
	}

	public void onSelect$kotamaopsMatraCombobox(Event event) throws Exception {
		if (kotamaopsMatraCombobox.getSelectedItem().getValue()==null) {
			// findAll -- loadKotamaops and displayKotamaops
			loadKotamaops();
		} else {
			KotamaopsType selKotMatra = kotamaopsMatraCombobox.getSelectedItem().getValue();
			setKotamaopsList(
					getKotamaopsDao().findKotamaopsByKotamaopsTypeMatra(selKotMatra));
			
			// remove angkatan darat
			for (int i = 0; i < kotamaopsList.size(); i++) {
				Kotamaops kotamaops = kotamaopsList.get(i);
				if (kotamaops.getId().compareTo(matraDaratID)==0) {
					kotamaopsList.remove(i);
					break;
				}
			}
			// remove angkatan Laut
			for (int i = 0; i < kotamaopsList.size(); i++) {
				Kotamaops kotamaops = kotamaopsList.get(i);
				if (kotamaops.getId().compareTo(matraLautID)==0) {
					kotamaopsList.remove(i);
					break;
				}
			}
			// remove angkatan Udara
			for (int i = 0; i < kotamaopsList.size(); i++) {
				Kotamaops kotamaops = kotamaopsList.get(i);
				if (kotamaops.getId().compareTo(matraUdaraID)==0) {
					kotamaopsList.remove(i);
					break;
				}
			}			
			
			// sort
			getKotamaopsList().sort((o1, o2) -> {
				return Integer.compare(o1.getKotamaopsSequence(), o2.getKotamaopsSequence());
			});
		}
		displayKotamaops();		
	}
	
	private void displayKotamaops() {
		kotamaopsListbox.setModel(
			new ListModelList<Kotamaops>(getKotamaopsList()));
		kotamaopsListbox.setItemRenderer(getKotamaopsListitemRenderer());
	}

	private ListitemRenderer<Kotamaops> getKotamaopsListitemRenderer() {

		return new ListitemRenderer<Kotamaops>() {
			
			BufferedImage buffImg = null;
			
			@Override
			public void render(Listitem item, Kotamaops kotamaops, int index) throws Exception {
				Listcell lc;
				
				item.setClass("expandedRow");
		
				try {
					buffImg = ImageIO.read(new File("/pusdalops/img/logo/"+kotamaops.getImagedId()));
				} catch (MalformedURLException e) {
					log.error("Malformed Image URL");
				} catch (IOException e) {
					log.error("IO Image Error");
				}
								
				// logo image
				lc = new Listcell();
				lc.setImageContent(buffImg);					
				// lc.setImage("/img/logo/"+kotamaops.getImagedId());
				lc.setStyle("text-align: center;");
				lc.setParent(item);
				
				// Nama Kotamaops
				lc = new Listcell(kotamaops.getKotamaopsName());
				lc.setParent(item);
				
				// Matra
				lc = new Listcell(kotamaops.getKotamaopsType().toString());
				lc.setParent(item);
				
				// Alamat
				lc = initAddress(new Listcell(), kotamaops);
				lc.setParent(item);
				
				// Komunikasi
				lc = initKomunikasi(new Listcell(), kotamaops);
				lc.setParent(item);
				
				// Zona-Waktu
				lc = initZonaWaktu(new Listcell(), kotamaops);
				lc.setParent(item);
				
				// edit and delete
				lc = initEdit(new Listcell(), kotamaops);
				lc.setParent(item);
				
				item.setValue(kotamaops);
			}

			private Listcell initAddress(Listcell listcell, Kotamaops kotamaops) {
				Label lb1 = new Label();
				lb1.setPre(true);
				// System.out.println(kotamaops.getAddress01()==null ? " " : kotamaops.getAddress01() +" \n"+
				//		kotamaops.getAddress02());
				lb1.setValue(kotamaops.getAddress01()==null ? "" : kotamaops.getAddress01() +" \n"+
						kotamaops.getAddress02() +" \n"+
						kotamaops.getCity());
				// lb1.setValue(
				//		kotamaops.getAddress01()==null ? " " : kotamaops.getAddress01() +" \n "+
				//		kotamaops.getAddress02()==null ? " " : kotamaops.getAddress02() +" \n "+
				//		kotamaops.getCity()==null ? " " : kotamaops.getCity());
				lb1.setParent(listcell);
				
				return listcell;
			}

			private Listcell initKomunikasi(Listcell listcell, Kotamaops kotamaops) {
				Label lb1 = new Label();
				lb1.setMultiline(true);
				lb1.setValue(kotamaops.getPhone()==null ? " " : kotamaops.getPhone() + "\n" +
						kotamaops.getFax() + "\n" + 
						kotamaops.getEmail());
				lb1.setParent(listcell);

				return listcell;
			}

			private Listcell initZonaWaktu(Listcell listcell, Kotamaops kotamaops) {
				Label lb1 = new Label();
				lb1.setValue(kotamaops.getTimeZone()==null? " " : kotamaops.getTimeZone().toString());
				lb1.setParent(listcell);
				
				return listcell;
			}

			private Listcell initEdit(Listcell listcell, Kotamaops kotamaops) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setClass("listinfoEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, Kotamaops> args = Collections.singletonMap("kotamaops", kotamaops);
						Window kotamaopsDialogEditWin = (Window) Executions.createComponents(
								"/settings/kotamaops/KotamaopsDialog.zul", kotamaopsListInfoWin, args);
						kotamaopsDialogEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								// get the object
								Kotamaops editedkotamaops = (Kotamaops) event.getData();
								
								// update
								getKotamaopsDao().update(editedkotamaops);
								
								// load
								loadKotamaops();
								
								// display
								displayKotamaops();
							}
						});
						kotamaopsDialogEditWin.doModal();
					}
				});
				
				Button deleteButton = new Button();
				deleteButton.setLabel(" - ");
				deleteButton.setClass("listinfoEditButton");
				deleteButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Messagebox.show("Hapus Kotamaops "+kotamaops.getKotamaopsName()+" ?", "Konfirmasi", 
								Messagebox.OK | Messagebox.CANCEL, 
								Messagebox.QUESTION, new EventListener<Event>() {
									
									@Override
									public void onEvent(Event event) throws Exception {
										if (event.getName().equals("onOK")) {
											try {
												// delete
												getKotamaopsDao().delete(kotamaops);												

												// load
												loadKotamaops();
												
												// display
												displayKotamaops();
											} catch (Exception e) {
												throw new Exception("Tidak dapat dihapus. Kotamaops "+kotamaops.getKotamaopsName()+
														" mempunyai hubungan data.");
											}
										}
									}
								}
						);
						
					}
				});
				
				// use Hlayout as parent for the buttons
				Hlayout hlayout = new Hlayout();
				editButton.setParent(hlayout);
				deleteButton.setParent(hlayout);
				
				hlayout.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$kotamaopsListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+kotamaopsListbox.getItemCount()+" Kotamaops");
	}
		
	public void onClick$newButton(Event event) throws Exception {
		Map<String, Kotamaops> args = Collections.singletonMap("kotamaops", new Kotamaops());
		Window kotamaopsDialogWin = (Window) Executions.createComponents(
				"/settings/kotamaops/KotamaopsDialog.zul", kotamaopsListInfoWin, args);
		kotamaopsDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the object
				Kotamaops kotamaops = (Kotamaops) event.getData();
				
				// save
				getKotamaopsDao().save(kotamaops);
				
				// load
				loadKotamaops();
				
				// display
				displayKotamaops();
			}
		});
		
		kotamaopsDialogWin.doModal();
	}
	
	public KotamaopsDao getKotamaopsDao() {
		return kotamaopsDao;
	}

	public void setKotamaopsDao(KotamaopsDao kotamaopsDao) {
		this.kotamaopsDao = kotamaopsDao;
	}

	public List<Kotamaops> getKotamaopsList() {
		return kotamaopsList;
	}

	public void setKotamaopsList(List<Kotamaops> kotamaopsList) {
		this.kotamaopsList = kotamaopsList;
	}

	
	
}
