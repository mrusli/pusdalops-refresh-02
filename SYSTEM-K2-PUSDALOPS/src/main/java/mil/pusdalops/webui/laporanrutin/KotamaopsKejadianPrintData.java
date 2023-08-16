package mil.pusdalops.webui.laporanrutin;

import java.util.List;

public class KotamaopsKejadianPrintData {

	private String namaKotamaops;
	
	private List<KejadianPrintData> kejadianPrintData;

	@Override
	public String toString() {
		return "KotamaopsKejadianPrintData [namaKotamaops=" + namaKotamaops + ", kejadianPrintData=" + kejadianPrintData
				+ "]";
	}

	public String getNamaKotamaops() {
		return namaKotamaops;
	}

	public void setNamaKotamaops(String namaKotamaops) {
		this.namaKotamaops = namaKotamaops;
	}

	public List<KejadianPrintData> getKejadianPrintData() {
		return kejadianPrintData;
	}

	public void setKejadianPrintData(List<KejadianPrintData> kejadianPrintData) {
		this.kejadianPrintData = kejadianPrintData;
	}
	
}
