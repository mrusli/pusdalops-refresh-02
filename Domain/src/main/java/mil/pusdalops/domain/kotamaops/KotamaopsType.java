package mil.pusdalops.domain.kotamaops;

public enum KotamaopsType {
	PUSDALOPS(0), MATRA_DARAT(1), MATRA_UDARA(2), MATRA_LAUT(3), LAIN_LAIN(4);
	
	private int value;
	
	KotamaopsType(int value) {
		setValue(value);
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public static String toString(int value) {
		switch (value) {
			case 0: return "PUSDALOPS";
			case 1: return "MATRA_DARAT";
			case 2: return "MATRA_UDARA";
			case 3: return "MATRA_LAUT";
			case 4: return "LAIN_LAIN";
			default:
				return null;
		}
	}
	
	public static KotamaopsType toKotamaopsType(int value) {
		switch (value) {
			case 0: return MATRA_DARAT;
			case 1: return PUSDALOPS;
			case 2: return MATRA_UDARA;
			case 3: return MATRA_LAUT;
			case 4: return LAIN_LAIN;
			default:
				return null;
		}
	}
	
}
