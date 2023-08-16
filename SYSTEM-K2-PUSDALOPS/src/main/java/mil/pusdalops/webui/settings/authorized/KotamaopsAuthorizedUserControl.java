package mil.pusdalops.webui.settings.authorized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
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
import mil.pusdalops.domain.authorization.UserRole;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.persistence.user.dao.UserDao;
import mil.pusdalops.persistence.userrole.dao.UserRoleDao;
import mil.pusdalops.webui.common.GFCBaseController;
import mil.pusdalops.webui.dialogs.KotamaopsListDialogData;

public class KotamaopsAuthorizedUserControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7566340762873666636L;
	
	private UserDao userDao;
	private UserRoleDao userRoleDao;
	
	private Window kotamaopsAuthorizedUserWin;
	private Label formTitleLabel, infoResultlabel;
	private Textbox kotamaopsTextbox;
	private Listbox authUserListbox;
	
	private Kotamaops selectedKotamaops;
	private List<User> users;
	
	private static final Long ROLE_USER_ID = 3L;
	
	private static final Logger log = Logger.getLogger(KotamaopsAuthorizedUserControl.class);
	
	public void onCreate$kotamaopsAuthorizedUserWin(Event event) throws Exception {
		formTitleLabel.setValue("Settings | Kotamaops");
		
		setSelectedKotamaops(null);
	}
	
	public void onClick$selectKotamaopsButton(Event event) throws Exception {
		KotamaopsListDialogData dialogData = new KotamaopsListDialogData();
		dialogData.setKotamaopsType(KotamaopsType.PUSDALOPS);
		dialogData.setKotamaopsToExclude(new ArrayList<Kotamaops>());
		
		Map<String, KotamaopsListDialogData> args = Collections.singletonMap("dialogData", dialogData);
		Window kotamaopsSelectWin = (Window) Executions.createComponents(
				"/dialogs/KotamaopsListDialog.zul", kotamaopsAuthorizedUserWin, args);
		kotamaopsSelectWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Kotamaops selKotamaops = (Kotamaops) event.getData();
				
				log.info(selKotamaops.toString());
				
				kotamaopsTextbox.setValue(selKotamaops.getKotamaopsName());
				setSelectedKotamaops(selKotamaops);
				
				displayUserForKotamaops(selKotamaops);
			}
		});
		
		kotamaopsSelectWin.doModal();
	}

	protected void displayUserForKotamaops(Kotamaops selKotamaops) throws Exception {
		log.info("display user for Kotamaops: "+selKotamaops.getKotamaopsDisplayName());
		
		setUsers(getUserDao().findAllUsersByKotamaops(selKotamaops));
		
		authUserListbox.setModel(new ListModelList<User>(getUsers()));
		authUserListbox.setItemRenderer(getAuthUserListitemRenderer());
	}

	private ListitemRenderer<User> getAuthUserListitemRenderer() {

		return new ListitemRenderer<User>() {

			@Override
			public void render(Listitem item, User user, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(String.valueOf(index+1)+".");
				lc.setParent(item);
				
				// Nama Login
				lc = new Listcell(user.getUserName());
				lc.setParent(item);
				
				// Aktif
				lc = new Listcell(user.isActive()? "Aktif" : "Non-Aktif");
				lc.setParent(item);
				
				// Edit
				lc = initEdit(new Listcell(), user);
				lc.setParent(item);
				
			}

			private Listcell initEdit(Listcell listcell, User user) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setClass("listinfoEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, User> args = Collections.singletonMap("user", user);
						Window userEditWin = (Window) Executions.createComponents(
								"/settings/authorized/KotamaopsAuthorizedUserDialog.zul", kotamaopsAuthorizedUserWin, args);
						userEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								User user = (User) event.getData();
								
								// save
								getUserDao().update(user);
								
								// load and display
								User userKotamaopsByProxy = getUserDao().findUserKotamaopsByProxy(user.getId());
								displayUserForKotamaops(userKotamaopsByProxy.getKotamaops());
							}
						});
						userEditWin.doModal();
					}
				});
				
				Button deleteButton = new Button();
				deleteButton.setLabel(" - ");
				deleteButton.setClass("listinfoEditButton");
				deleteButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Messagebox.show("Hapus User: "+user.getUserName()+" ?", "Konfirmasi", 
								Messagebox.OK | Messagebox.CANCEL, 
								Messagebox.QUESTION, new EventListener<Event>() {
									
									@Override
									public void onEvent(Event event) throws Exception {
										if (event.getName().equals("onOK")) {
											try {
												// delete
												getUserDao().delete(user);												
												
												// display
												displayUserForKotamaops(getSelectedKotamaops());
											} catch (Exception e) {
												throw new Exception("Tidak dapat dihapus. User: "+
														user.getUserName()+
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
	
	public void onAfterRender$authUserListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+authUserListbox.getItemCount()+" pengguna (user)");
	}	

	public void onClick$newButton(Event event) throws Exception {
		User user = new User();
		user.setKotamaops(getSelectedKotamaops());
		user.setActive(true);

		// ONLY for NEW USER
		// index 3L in the db - 'ROLE_USER'
		UserRole userRoleUser = getUserRoleDao().findUserRoleById(ROLE_USER_ID);

		Set<UserRole> userRoles = new HashSet<UserRole>();
		userRoles.add(userRoleUser);
		user.setUserRoles(userRoles);
		
		Map<String, User> args = Collections.singletonMap("user", user);
		Window userCreateWin = (Window) Executions.createComponents(
				"/settings/authorized/KotamaopsAuthorizedUserDialog.zul", kotamaopsAuthorizedUserWin, args);
		userCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				User user = (User) event.getData();
				
				try {					
					// save
					getUserDao().save(user);
				} catch (org.hibernate.exception.ConstraintViolationException e) {
					log.info(e.getMessage());
					Messagebox.show("Duplicate entry '" + user.getUserName() + "'");
				}
				
				// load and display
				displayUserForKotamaops(getSelectedKotamaops());
			}
		});

		userCreateWin.doModal();
	}
	
	public Kotamaops getSelectedKotamaops() {
		return selectedKotamaops;
	}

	public void setSelectedKotamaops(Kotamaops selectedKotamaops) {
		this.selectedKotamaops = selectedKotamaops;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserRoleDao getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

}
