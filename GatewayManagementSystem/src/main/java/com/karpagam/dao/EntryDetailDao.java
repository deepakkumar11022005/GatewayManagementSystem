package com.karpagam.dao;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.karpagam.bean.EntryDetail;
import com.karpagam.bean.StudentHistory;
import com.karpagam.util.DBUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class EntryDetailDao {

	public boolean checkEntered(String rollNumber) {

		EntryDetail entryDetail = null;
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<EntryDetail> cq = cb.createQuery(EntryDetail.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		Predicate p1 = cb.equal(root.get("rollNumber"), rollNumber);
		Predicate p2 = cb.equal(root.get("isEntered"), true);
		Predicate p3 = cb.isNull(root.get("outDate"));
		Predicate p4 = cb.and(p1, p2, p3);
		cq.select(root).where(p4);

		entryDetail = session.createQuery(cq).getSingleResultOrNull();

		session.close();
		sf.close();
		if (entryDetail != null) {
			return true;
		}
		return false;
	}

	public boolean insert(EntryDetail entryDetail) {
		try {
			SessionFactory sf = DBUtil.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();

			session.persist(entryDetail);

			session.getTransaction().commit();
			session.close();
			sf.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(String rollNumber, Date date, Time time) {
		try {
			EntryDetail entryDetail = null;
			SessionFactory sf = DBUtil.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();

			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<EntryDetail> cq = cb.createQuery(EntryDetail.class);
			Root<EntryDetail> root = cq.from(EntryDetail.class);

			Predicate p1 = cb.equal(root.get("rollNumber"), rollNumber);
			Predicate p2 = cb.equal(root.get("isEntered"), true);
			Predicate p3 = cb.isNull(root.get("outDate"));
			Predicate p4 = cb.and(p1, p2, p3);
			cq.select(root).where(p4);

			entryDetail = session.createQuery(cq).getSingleResult();
			entryDetail.setOutDate(date);
			entryDetail.setOutTime(time);
			entryDetail.setEntered(false);
			session.merge(entryDetail);

			session.getTransaction().commit();
			session.close();
			sf.close();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<EntryDetail> getRecords() {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<EntryDetail> cq = cb.createQuery(EntryDetail.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		cq.select(root).orderBy(cb.desc(root.get("inTime")));

		List<EntryDetail> li = session.createQuery(cq).getResultList();
		session.getTransaction().commit();
		session.close();
		sf.close();
		return li;
	}

	public List<Object> getTodayStudentDetailInFilter(Date date ,boolean inDate, Time fromTime, Time toTime) {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		Predicate p1 = cb.greaterThanOrEqualTo(root.get("inTime"), fromTime);
		Predicate p2 = cb.isNull(root.get("outTime"));
		Predicate p3 = cb.lessThanOrEqualTo(root.get("outTime"), toTime);
		Predicate p4 = cb.or(p2,p3);
		Predicate p6 = null;
		if(inDate) {
			p6 = cb.equal(root.get("inDate"), date);
		}else {
			p6 = cb.equal(root.get("outDate"), date);
		}
		Predicate p5 = cb.and(p1,p4,p6);
		cq.select(root).where(p5);

		List<Object> li = session.createQuery(cq).getResultList();
		session.getTransaction().commit();
		session.close();
		sf.close();
		return li;
	}

	public List<Object> getTodayStudentDetailInFilter(Date date,boolean inDate, Time fromTime, Time toTime, String rollNumber) {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		Predicate p1 = cb.equal(root.get("rollNumber"),rollNumber);
		Predicate p2 = cb.equal(root.get("inDate"),date);
		Predicate p3 = cb.greaterThanOrEqualTo(root.get("inTime"), fromTime);
		Predicate p4 = cb.isNull(root.get("outTime"));
		Predicate p5 = cb.lessThanOrEqualTo(root.get("outTime"), toTime);
		Predicate p6 = cb.or(p4,p5);
		Predicate p8 = null;
		if(inDate) {
			p8 = cb.equal(root.get("inDate"), date);
		}else {
			p8 = cb.equal(root.get("outDate"), date);
		}
		Predicate p7 = cb.and(p1,p2,p3,p6,p8);
		
		cq.select(root).where(p7);

		List<Object> li = session.createQuery(cq).getResultList();
		session.getTransaction().commit();
		session.close();
		sf.close();
		return li;
	}

	public Long getTodayStudentCount() {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		cq.select(cb.countDistinct(root.get("rollNumber")));

		Long count = (Long) session.createQuery(cq).getSingleResult();
		session.getTransaction().commit();
		session.close();
		sf.close();
		return count;
	}
	
	public Long getInCount() {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		
		Predicate p1 = cb.isNotNull(root.get("inTime"));
		cq.select(cb.count(root)).where(p1);
		
		Long count = (Long) session.createQuery(cq).getSingleResult();
		session.getTransaction().commit();
		session.close();
		sf.close();
		return count;
	}
	
	public Long getOutCount() {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		
		Predicate p1 = cb.isNotNull(root.get("outTime"));
		cq.select(cb.count(root)).where(p1);
		
		Long count = (Long) session.createQuery(cq).getSingleResult();
		session.getTransaction().commit();
		session.close();
		sf.close();
		return count;
	}
	
	public boolean shiftAllRecords() {
		try {
			SessionFactory sf = DBUtil.getSessionFactory();
			Session session = sf.openSession();
			
			session.beginTransaction();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<EntryDetail> cq = cb.createQuery(EntryDetail.class);
			Root<EntryDetail> root = cq.from(EntryDetail.class);
			cq.select(root);
			List<EntryDetail> li = session.createQuery(cq).getResultList();
			session.getTransaction().commit();
			session.close();
			
			Session session2 = sf.openSession();
			
			session2.beginTransaction();			
			for(EntryDetail ed : li) {
				StudentHistory sh = new StudentHistory();
				sh.setRollNumber(ed.getRollNumber());
				sh.setName(ed.getName());
				sh.setEntered(ed.isEntered());
				sh.setInDate(ed.getInDate());
				sh.setOutDate(ed.getOutDate());
				sh.setInTime(ed.getInTime());
				sh.setOutTime(ed.getOutTime());
				sh.setId(ed.getId());
				session2.persist(sh);
			}
			session2.getTransaction().commit();
			session2.close();
			
			sf.close();
			
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	public void truncateTable() {
		SessionFactory sf = DBUtil.getSessionFactory();
		Session session = sf.openSession();
		
		session.beginTransaction();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<EntryDetail> cq = cb.createQuery(EntryDetail.class);
		Root<EntryDetail> root = cq.from(EntryDetail.class);
		cq.select(root);
		List<EntryDetail> li = session.createQuery(cq).getResultList();
		session.getTransaction().commit();
		session.close();
		
		Session session2 = sf.openSession();
		
		session2.beginTransaction();			
		for(EntryDetail ed : li) {
			session2.remove(ed);
		}
		session2.getTransaction().commit();
		session2.close();
		
		sf.close();
	}

}
