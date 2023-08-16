package mil.pusdalops.webui.kejadian;

import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kerugian.Pihak;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;

public class KejadianData {

	private Pihak paraPihak;

	private Kejadian kejadian;
	
	private Kotamaops settingsKotamaops;
	
	private KotamaopsType kotamaopsType;

	public Pihak getParaPihak() {
		return paraPihak;
	}

	public void setParaPihak(Pihak paraPihak) {
		this.paraPihak = paraPihak;
	}

	public Kejadian getKejadian() {
		return kejadian;
	}

	public void setKejadian(Kejadian kejadian) {
		this.kejadian = kejadian;
	}

	public Kotamaops getSettingsKotamaops() {
		return settingsKotamaops;
	}

	public void setSettingsKotamaops(Kotamaops settingsKotamaops) {
		this.settingsKotamaops = settingsKotamaops;
	}

	public KotamaopsType getKotamaopsType() {
		return kotamaopsType;
	}

	public void setKotamaopsType(KotamaopsType kotamaopsType) {
		this.kotamaopsType = kotamaopsType;
	}


	
	
}
