package mil.pusdalops.webui.kejadian;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import mil.pusdalops.webui.common.GFCBaseController;

public class SearchKronologisDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -576597055101871594L;

	private Window searchKronologisDialogWin;
	
	public void onCreate$searchKronologisDialogWin(Event event) throws Exception {
		
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		searchKronologisDialogWin.detach();
	}
	
}
