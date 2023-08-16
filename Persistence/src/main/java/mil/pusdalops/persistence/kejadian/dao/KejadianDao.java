package mil.pusdalops.persistence.kejadian.dao;

import java.util.Date;
import java.util.List;

import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kejadian.KejadianJenis;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.domain.wilayah.Propinsi;

public interface KejadianDao {

	/**
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianById(long id) throws Exception;

	/**
	 * Find all kejadian as a list, with descending (true) or 
	 * ascending (false) order
	 * 
	 * @param desc - true - descending, false - ascending
	 * @return List<Kejadian>
	 * @throws Exception
	 */
	public List<Kejadian> findAllKejadian(boolean desc) throws Exception;
	
	/**
	 * @param kejadian
	 * @return Long
	 * @throws Exception
	 */
	public Long save(Kejadian kejadian) throws Exception;
	
	/**
	 * @param kejadian
	 * @throws Exception
	 */
	public void update(Kejadian kejadian) throws Exception;
	
	/**
	 * @param kejadian
	 * @throws Exception
	 */
	public void delete(Kejadian kejadian) throws Exception;

	/**
	 * Kotamaops is fetched lazy
	 * 
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianKotamaopsByProxy(long id) throws Exception;

	/**
	 * Kerugians is fetched lazy
	 * 
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianKerugiansByProxy(long id) throws Exception;

	/**
	 * Find all kejadian as a list, with descending (true) or 
	 * ascending (false) order
	 *  
	 * @param kotamaops
	 * @param desc - true - descending, false - ascending
	 * @return List<Kejadian>
	 * @throws Exception
	 */
	public List<Kejadian> findAllKejadianByKotamaops(Kotamaops kotamaops, boolean desc) throws Exception;


	/**
	 * Find kejadian by Kotamaoops and by TwKejadian (start to end date)
	 * 
	 * @param kotamaops
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return {@link List} of {@link Kejadian}
	 * @throws Exception
	 */
	public List<Kejadian> findAllKejadianByKotamaopsByTwKejadian(Kotamaops kotamaops, String startTimestamp, String endTimestamp) throws Exception;
	
	
	/**
	 * Propinsi is fetched lazy
	 * 
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianPropinsiByProxy(long id) throws Exception;

	/**
	 * Kabupaten is fetched lazy
	 * 
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianKabupatenByProxy(long id) throws Exception;

	/**
	 * Kecamatan is fetched lazy
	 * 
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianKecamatanByProxy(long id) throws Exception;

	/**
	 * Kelurahan is fetched lazy
	 * 
	 * @param id
	 * @return Kejadian
	 * @throws Exception
	 */
	public Kejadian findKejadianKelurahanByProxy(long id) throws Exception;

	/**
	 * @param nonSynchData 
	 * @param asLastSynchDate
	 * @return List<Kejadian>
	 * @throws Exception
	 */
	public List<Kejadian> findKotamaopsNonSynchronizedKejadian(Kotamaops kotamaops, boolean nonSynchData) throws Exception;
	
	/**
	 * Index / Re-Index kronologis
	 * 
	 * @throws Exception
	 */
	public void createIndexer() throws Exception;
	
	
	
	/**
	 * search a text string in Kronologis
	 * 
	 * @param searchString
	 * @param kotamaops 
	 * @param kotamaops 
	 * @return List<Kejadian>
	 * @throws Exception
	 */
	public List<Kejadian> searchKronologis(String searchString, List<Kotamaops> kotamaops) throws Exception;

	public List<Kejadian> findAllKejadianInKotamaops(boolean desc, int defaultYear, List<Kotamaops> kotamaops) throws Exception;

	public List<Kejadian> findAllKejadianInKotamaopsByMatraType(boolean desc, int defaultYear, List<Kotamaops> kotamaopsList,
			KotamaopsType kotamaopsMatraType) throws Exception;	
	
	public List<Kejadian> findAllKejadianInPropinsisByKotamaops(boolean desc, Kotamaops kotamaops, List<Propinsi> propinsis) throws Exception;

	public List<Kejadian> searchKronologis(String searchString, Kotamaops selKotamaops) throws Exception;

	public List<Kejadian> searchKronologis(String searchString, List<Kotamaops> kotamaops, Date twAwal,
			Date twAkhir) throws Exception;

	public List<Kejadian> searchKronologis(String searchString, Kotamaops selKotamaops, Date twAwal, Date twAkhir) throws Exception;

	public List<Kejadian> searchKronologisByKejadianList(String searchString, List<KejadianJenis> kejadianJenisList, Date twAwal,
			Date twAkhir) throws Exception;

	public List<Kejadian> searchKronologisByKejadian(String searchString, KejadianJenis kejadianJenis, Date twAwal,
			Date twAkhir) throws Exception;

	public List<Kejadian> searchKronologisByPropinsiList(String searchString, List<Propinsi> propinsiList);

	public List<Kejadian> searchKronologisByPropinsiList(String searchString, List<Propinsi> propinsiList, Date twAwal,
			Date twAkhir) throws Exception;

	public List<Kejadian> searchKronologisByPropinsi(String searchString, Propinsi selPropinsi, Date twAwal,
			Date twAkhir) throws Exception;

	public List<Integer> findDistinctYearKejadian() throws Exception;
	
	public List<Kejadian> searchKronologis(String searchString) throws Exception;

	public List<Kejadian> searchKejadianId(String kejadianId) throws Exception;

	/**
	 * Example: WHERE kejadian.tw_pembuatan_datetime
	 * 			BETWEEN '2020-01-01 00:00:00' AND '2020-03-31 00:00:00'
	 * 
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return
	 * @throws Exception
	 */
	public List<Kejadian> findAllKejadianByTwPembuatan(String startTimestamp, String endTimestamp) throws Exception;

	/**
	 * Example: WHERE kejadian.tw_kejadian_datetime
	 * 			BETWEEN '2020-01-01 00:00:00' AND '2020-03-31 00:00:00'
	 * 
	 * @param string
	 * @param string2
	 * @return
	 * @throws Exception
	 */
	public List<Kejadian> findAllKejadianByTwKejadian(String startTimestamp, String endTimestamp) throws Exception;
}
