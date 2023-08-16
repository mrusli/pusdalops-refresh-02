package mil.pusdalops.webui.error;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import mil.pusdalops.webui.common.GFCBaseController;

public class ErrorControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5225639215905786925L;
	
	private Window errorWin;
	private Label errorMessageLabel;
	
	private static final Logger log = Logger.getLogger(ErrorControl.class);
	
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.select.SelectorComposer#doAfterCompose(org.zkoss.zk.ui.Component)
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		log.info("ErrorControl - doAfterCompose()");
		
        // via execution.getAttribute()
        Execution execution = Executions.getCurrent();
        Exception ex1 = (Exception) execution.getAttribute("javax.servlet.error.exception");
        log.info(ex1.getMessage()+" - "+ex1.getLocalizedMessage());
 
        // via requestScope map
        // Exception ex2 = (Exception) requestScope.get("javax.servlet.error.exception");
        
        errorMessageLabel.setValue(ex1.getMessage());
	}
	
	public void onClick$closeButton(Event event) throws Exception {
		errorWin.detach();
	}
}
