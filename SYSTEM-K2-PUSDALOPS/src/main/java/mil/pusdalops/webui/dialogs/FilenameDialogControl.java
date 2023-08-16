package mil.pusdalops.webui.dialogs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.laporanrutin.ExportFileData;

public class FilenameDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8597220744360600614L;

	private Window filenameDialogWin;
	private Textbox directoryTextbox, filenameTextbox;
	private Combobox fileExtensionCombobox;
	
	private ExportFileData exportFileData;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// exportFileData
		setExportFileData((ExportFileData) arg.get("exportFileData"));
	}

	public void onCreate$filenameDialogWin(Event event) throws Exception {
		setupFileExensionCombobox();
		
		// extension
		for (Comboitem comboitem : fileExtensionCombobox.getItems()) {
			if (comboitem.getValue().equals(getExportFileData().getExtension())) {
				fileExtensionCombobox.setSelectedItem(comboitem);
			}
		}
		
		// filename+ext
		filenameTextbox.setValue(getExportFileData().getFilename());
		
		// directory
		directoryTextbox.setValue(getExportFileData().getDirectory());
	}
	
	private void setupFileExensionCombobox() {
		String[] extensions = { "docx", "doc" };
		
		Comboitem comboitem;
		for (String extension : extensions) {
			comboitem = new Comboitem();
			comboitem.setLabel(extension);
			comboitem.setValue(extension);
			comboitem.setParent(fileExtensionCombobox);
		}
	}
	
	public void onClick$lanjutButton(Event event) throws Exception {
		String fileExt = fileExtensionCombobox.getSelectedItem().getLabel();
		String fileName = filenameTextbox.getValue();
		String directory = directoryTextbox.getValue();
		
		Events.sendEvent(Events.ON_OK, filenameDialogWin, directory+fileName+"."+fileExt);
		
		filenameDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		filenameDialogWin.detach();
	}

	public ExportFileData getExportFileData() {
		return exportFileData;
	}

	public void setExportFileData(ExportFileData exportFileData) {
		this.exportFileData = exportFileData;
	}
}
