package mil.pusdalops.persistence.kejadian.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import mil.pusdalops.domain.kejadian.Kejadian;
import mil.pusdalops.domain.kejadian.KejadianJenis;
import mil.pusdalops.domain.kotamaops.Kotamaops;
import mil.pusdalops.domain.kotamaops.KotamaopsType;
import mil.pusdalops.domain.wilayah.Propinsi;
import mil.pusdalops.persistence.common.dao.hibernate.DaoHibernate;
import mil.pusdalops.persistence.kejadian.dao.KejadianDao;

public class KejadianHibernate extends DaoHibernate implements KejadianDao {

	@Override
	public Kejadian findKejadianById(long id) throws Exception {

		return (Kejadian) super.findById(Kejadian.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadian(boolean desc) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		criteria.addOrder(desc ? 
				Order.desc("twPembuatanDateTime") : 
				Order.asc("twPembuatanDateTime"));
		
		try {
			
			return criteria.list();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Long save(Kejadian kejadian) throws Exception {
		
		return super.save(kejadian);
	}

	@Override
	public void update(Kejadian kejadian) throws Exception {

		super.update(kejadian);
	}

	@Override
	public void delete(Kejadian kejadian) throws Exception {
		
		super.delete(kejadian);
	}
	
	@Override
	public Kejadian findKejadianKotamaopsByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		Kejadian kejadian = (Kejadian) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kejadian.getKotamaops());
			
			return kejadian;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Kejadian findKejadianKerugiansByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		Kejadian kejadian = (Kejadian) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kejadian.getKerugians());
			
			return kejadian;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianByKotamaops(Kotamaops kotamaops, boolean desc) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		criteria.add(Restrictions.eq("kotamaops", kotamaops));
		criteria.addOrder(desc ? 
				Order.desc("twPembuatanDateTime") :
				Order.asc("twPembuatanDateTime"));
		
		try {
			return criteria.list();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

	@Override
	public Kejadian findKejadianPropinsiByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		Kejadian kejadian = (Kejadian) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kejadian.getPropinsi());
			
			return kejadian;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Kejadian findKejadianKabupatenByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		Kejadian kejadian = (Kejadian) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kejadian.getKabupatenKotamadya());
			
			return kejadian;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Kejadian findKejadianKecamatanByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		Kejadian kejadian = (Kejadian) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kejadian.getKecamatan());
			
			return kejadian;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@Override
	public Kejadian findKejadianKelurahanByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class);
		Kejadian kejadian = (Kejadian) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kejadian.getKelurahan());
			
			return kejadian;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findKotamaopsNonSynchronizedKejadian(Kotamaops kotamaops, boolean nonSynchData) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kejadian.class, "kejadian");
		criteria.createAlias("kejadian.serialNumber", "serialNumber");
		
		String docCode = kotamaops.getDocumentCode();
		
		criteria.add(Restrictions.eq("serialNumber.documentCode", docCode));
		if (nonSynchData) {
			criteria.add(Restrictions.isNull("synchAt"));			
		} else {
			criteria.add(Restrictions.isNotNull("synchAt"));						
		}
		criteria.addOrder(Order.desc("twPembuatanDateTime"));
		
		try {
			return criteria.list();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public void createIndexer() throws Exception {
		Session session = getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			
			fullTextSession.createIndexer().startAndWait();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologis(String searchString, List<Kotamaops> kotamaops) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.in("kotamaops", kotamaops));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianInKotamaops(boolean desc, int defaultYear, List<Kotamaops> kotamaops) throws Exception {
		Session session = getSessionFactory().openSession();
		
		String orderBy = desc ? " ORDER BY kejadian.id asc " : 
			" ORDER BY kejadian.id desc ";
		
		try {
			SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM e010_k2_pusdalops.kejadian kejadian"
					+ " LEFT OUTER JOIN document_serial_number document ON kejadian.serial_number_id_fk = document.id "
					+ " WHERE "
					// + " document.document_code is null AND "
					+ " YEAR(kejadian.tw_kejadian_datetime) = :year AND "
					+ " kejadian.kotamaops_id_fk in (:kotamaops) " 
					+ orderBy);
			sqlQuery.setParameter("year", defaultYear);
			sqlQuery.setParameterList("kotamaops", kotamaops);
			sqlQuery.addEntity(Kejadian.class);
			
			return sqlQuery.list(); 
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianInKotamaopsByMatraType(boolean desc, int defaultYear, List<Kotamaops> kotamaopsList,
			KotamaopsType kotamaopsMatraType) throws Exception {
		
		Session session = getSessionFactory().openSession();
		
		String orderBy = desc ? " kejadian.id asc " : 
			" kejadian.id desc ";
		
		try {
			SQLQuery sqlQuery = session.createSQLQuery("SELECT kejadian.* FROM e010_k2_pusdalops.kejadian kejadian"
					+ " LEFT OUTER JOIN kotamaops kotamaops ON kejadian.kotamaops_id_fk = kotamaops.id "
					+ " LEFT OUTER JOIN document_serial_number document ON kejadian.serial_number_id_fk = document.id "
					+ " WHERE "
					+ " YEAR(kejadian.tw_kejadian_datetime) = :year AND "
					+ " document.document_code is null AND "
					+ " kejadian.kotamaops_id_fk in (:kotamaops) AND "
					+ " kotamaops.type=:matraType "
					+ " ORDER BY "+orderBy);
			sqlQuery.setParameter("year", defaultYear);
			sqlQuery.setParameterList("kotamaops", kotamaopsList);
			sqlQuery.setParameter("matraType", kotamaopsMatraType.ordinal());
			sqlQuery.addEntity(Kejadian.class);
			
			return sqlQuery.list(); 
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianInPropinsisByKotamaops(boolean desc, Kotamaops kotamaops,
			List<Propinsi> propinsis) throws Exception {
		
		Session session = getSessionFactory().openSession();

		String orderBy = desc ? " ORDER BY kejadian.id asc " : 
			" ORDER BY kejadian.tw_kejadian_datetime desc ";		
		
		try {
			SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM e010_k2_pusdalops.kejadian kejadian "
					+ " LEFT OUTER JOIN document_serial_number document ON kejadian.serial_number_id_fk = document.id "
					+ " WHERE "
					+ " kejadian.kotamaops_id_fk = :kotamaops AND "
					+ " document.document_code = :documentCode AND "
					+ " kejadian.propinsi_id_fk in (:propinsis) "
					+ orderBy);
			sqlQuery.setParameter("kotamaops", kotamaops.getId());
			sqlQuery.setParameter("documentCode", kotamaops.getDocumentCode());
			sqlQuery.setParameterList("propinsis", propinsis);
			sqlQuery.addEntity(Kejadian.class);

			return sqlQuery.list();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologis(String searchString, Kotamaops selKotamaops) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.eq("kotamaops", selKotamaops));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologis(String searchString, List<Kotamaops> kotamaops, Date twAwal,
			Date twAkhir) {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.in("kotamaops", kotamaops));
			criteria.add(Restrictions.ge("twKejadianDateTime", twAwal));
			criteria.add(Restrictions.le("twKejadianDateTime", twAkhir));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologis(String searchString, Kotamaops selKotamaops, Date twAwal, Date twAkhir)
			throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.eq("kotamaops", selKotamaops));
			criteria.add(Restrictions.ge("twKejadianDateTime", twAwal));
			criteria.add(Restrictions.le("twKejadianDateTime", twAkhir));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologisByKejadianList(String searchString, List<KejadianJenis> kejadianJenisList,
			Date twAwal, Date twAkhir) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.in("jenisKejadian", kejadianJenisList));
			criteria.add(Restrictions.ge("twKejadianDateTime", twAwal));
			criteria.add(Restrictions.le("twKejadianDateTime", twAkhir));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologisByKejadian(String searchString, KejadianJenis kejadianJenis, Date twAwal,
			Date twAkhir) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.eq("jenisKejadian", kejadianJenis));
			criteria.add(Restrictions.ge("twKejadianDateTime", twAwal));
			criteria.add(Restrictions.le("twKejadianDateTime", twAkhir));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologisByPropinsiList(String searchString, List<Propinsi> propinsiList) {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.in("propinsi", propinsiList));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologisByPropinsiList(String searchString, List<Propinsi> propinsiList, Date twAwal,
			Date twAkhir) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.in("propinsi", propinsiList));
			criteria.add(Restrictions.ge("twKejadianDateTime", twAwal));
			criteria.add(Restrictions.le("twKejadianDateTime", twAkhir));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologisByPropinsi(String searchString, Propinsi selPropinsi, Date twAwal,
			Date twAkhir) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		Session sessionCriteria = null;
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			sessionCriteria = getSessionFactory().openSession();
			Criteria criteria = sessionCriteria.createCriteria(Kejadian.class);
			criteria.add(Restrictions.eq("propinsi", selPropinsi));
			criteria.add(Restrictions.ge("twKejadianDateTime", twAwal));
			criteria.add(Restrictions.le("twKejadianDateTime", twAkhir));
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findDistinctYearKejadian() throws Exception {
		Session session = getSessionFactory().openSession();
		
		SQLQuery sqlQuery = session.createSQLQuery("SELECT DISTINCT (YEAR(kejadian.tw_kejadian_datetime)) as Year "
				+ " FROM e010_k2_pusdalops.kejadian kejadian "
				+ " ORDER BY Year desc");
		
		try {
			
			return sqlQuery.list();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKronologis(String searchString) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get();
			
			Query qr = qb.keyword()
					.onField("kronologis")
					.matching(searchString)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
						
			tx.commit();

			return fullTextQuery.list();
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> searchKejadianId(String kejadianId) throws Exception {
		Transaction tx = null;		
		Session session = getSessionFactory().openSession();

		FullTextSession fullTextSession = Search.getFullTextSession(session);

		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Kejadian.class).get(); 
					
			Query qr = qb.keyword().wildcard()
					.onField("serialNumber.serialComp")
					.matching(kejadianId+"*")
					.createQuery(); 
			
			// Query qr = qb.keyword()
			//		.onField("serialNumber.serialComp")
			//		.matching(kejadianId)
			//		.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qr, Kejadian.class);
			
			tx.commit();

			return fullTextQuery.list();			
			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianByTwPembuatan(String startTimestamp, String endTimestamp) throws Exception {
		Session session = getSessionFactory().openSession();
		
		String orderBy = " ORDER BY kejadian.tw_pembuatan_datetime asc ";
		
		try {
			SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM e010_k2_pusdalops.kejadian kejadian"
					+ " LEFT OUTER JOIN document_serial_number document ON kejadian.serial_number_id_fk = document.id "
					+ " WHERE kejadian.tw_pembuatan_datetime "
					+ " BETWEEN :twPembuatanAwal AND :twPembuatanAkhir " 
					+ orderBy);
			sqlQuery.setParameter("twPembuatanAwal", startTimestamp);
			sqlQuery.setParameter("twPembuatanAkhir", endTimestamp);
			sqlQuery.addEntity(Kejadian.class);
			
			return sqlQuery.list(); 
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianByTwKejadian(String startTimestamp, String endTimestamp) throws Exception {
		Session session = getSessionFactory().openSession();
		
		String orderBy = " ORDER BY kejadian.tw_kejadian_datetime asc ";
		
		try {
			SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM e010_k2_pusdalops.kejadian kejadian"
					+ " LEFT OUTER JOIN document_serial_number document ON kejadian.serial_number_id_fk = document.id "
					+ " WHERE kejadian.tw_kejadian_datetime "
					+ " BETWEEN :twKejadianAwal AND :twKejadianAkhir " 
					+ orderBy);
			sqlQuery.setParameter("twKejadianAwal", startTimestamp);
			sqlQuery.setParameter("twKejadianAkhir", endTimestamp);
			sqlQuery.addEntity(Kejadian.class);
			
			return sqlQuery.list(); 
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kejadian> findAllKejadianByKotamaopsByTwKejadian(Kotamaops kotamaops, String startTimestamp,
			String endTimestamp) throws Exception {
		
		Session session = getSessionFactory().openSession();
		
		String orderBy = " ORDER BY kejadian.tw_kejadian_datetime asc ";
		
		try {
			SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM e010_k2_pusdalops.kejadian kejadian"
					+ " LEFT OUTER JOIN document_serial_number document ON kejadian.serial_number_id_fk = document.id "
					+ " WHERE kejadian.tw_kejadian_datetime "
					+ " BETWEEN :twKejadianAwal AND :twKejadianAkhir " 
					+ " AND kejadian.kotamaops_id_fk = :kotamaops "
					+ orderBy);
			sqlQuery.setParameter("twKejadianAwal", startTimestamp);
			sqlQuery.setParameter("twKejadianAkhir", endTimestamp);
			sqlQuery.setParameter("kotamaops", kotamaops.getId());
			sqlQuery.addEntity(Kejadian.class);
			
			return sqlQuery.list(); 
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}
}
