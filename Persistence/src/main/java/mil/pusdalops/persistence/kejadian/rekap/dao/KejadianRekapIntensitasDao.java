package mil.pusdalops.persistence.kejadian.rekap.dao;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kejadian.KejadianJenis;
import mil.pusdalops.domain.kejadian.KejadianMotif;
import mil.pusdalops.domain.kejadian.KejadianPelaku;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.wilayah.Propinsi;

public interface KejadianRekapIntensitasDao {

	public BigInteger countKejadianByPropinsi(Propinsi propinsi, Date twAwal, Date twAkhir);

	public List<Kejadian> findAllDistinctKejadianByJenisKejadian(Date twAwal, Date twAkhir);
	
	public List<Kejadian> findInKotamaopsDistinctKejadianByJenisKejadian(List<Kotamaops> kotamaopsList, Date twAwal, Date twAkhir) throws Exception;

	public BigInteger countJenisKejadian(KejadianJenis jenisKejadian, LocalDateTime twAwal, 
			LocalDateTime twAkhir);

	public List<Kejadian> findDistinctKejadianByJenisKejadian(Kotamaops kotamaops, Date twAwal, Date twAkhir);

	public BigInteger countJenisKejadian(KejadianJenis jenisKejadian, Kotamaops kotamaops,
			LocalDateTime twAwal, LocalDateTime twAkhir);

	public BigInteger countAllMotifKejadian(KejadianMotif motifKejadian, LocalDateTime twAwal,
			LocalDateTime twAkhir);
	
	public BigInteger countMotifKejadianInKotamaops(List<Kotamaops> kotamaopsList, KejadianMotif motifKejadian, LocalDateTime twAwal,
			LocalDateTime twAkhir) throws Exception;

	public BigInteger countMotifKejadian(KejadianMotif motifKejadian, Kotamaops kotamaops,
			LocalDateTime twAwal, LocalDateTime twAkhir);

	public BigInteger countPelakuKejadian(KejadianPelaku kejadianPelaku, LocalDateTime twAwal,
			LocalDateTime twAkhir);
	
	public BigInteger countPelakuKejadianInKotamaops(List<Kotamaops> kotamaopsList, KejadianPelaku kejadianPelaku, LocalDateTime twAwal,
			LocalDateTime twAkhir) throws Exception;
	

	public BigInteger countPelakuKejadian(KejadianPelaku kejadianPelaku, Kotamaops kotamaops,
			LocalDateTime twAwal, LocalDateTime twAkhir);

}
