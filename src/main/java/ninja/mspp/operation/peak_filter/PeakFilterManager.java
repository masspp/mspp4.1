package ninja.mspp.operation.peak_filter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javafx.scene.control.TextInputDialog;
import ninja.mspp.MsppManager;
import ninja.mspp.operation.peak_filter.model.entity.FilterPeak;
import ninja.mspp.operation.peak_filter.model.entity.FilterPeakSet;

public class PeakFilterManager {
	private static PeakFilterManager instance;
	public static final String SET_KEY = "PEAK_FILTER_SET";
	
	private PeakFilterManager() {
	}
	
	public List<FilterPeakSet> getFilterPeakSets() {
		MsppManager manager = MsppManager.getInstance();
		Session session = manager.getSession();
		
		Query<FilterPeakSet> query = session.createQuery("from FilterPeakSet", FilterPeakSet.class);
		List<FilterPeakSet> list = query.getResultList();
		
		session.close();
		
		return list;
	}
	
	
	public FilterPeakSet saveNew(String name) {
		MsppManager manager = MsppManager.getInstance();
		Session session = manager.getSession();

		FilterPeakSet set = new FilterPeakSet();
		set.setName(name);
		set.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		session.save(set);

		session.close();

		return set;
	}
	
	
	protected void deletePeaks(long setId, Session session) {
		Query<FilterPeak> query = session.createQuery("from FilterPeak where setId = :setId", FilterPeak.class);
		query.setParameter("setId", setId);
		List<FilterPeak> list = query.getResultList();
		for (FilterPeak peak : list) {
			session.delete(peak);
		}				
	}
	
	public void delete(FilterPeakSet set) {
		MsppManager manager = MsppManager.getInstance();
		Session session = manager.getSession();
		session.beginTransaction();
		
		this.deletePeaks(set.getId(), session);
		session.delete(set);
		
		session.getTransaction().commit();
		session.close();
	}
	
	
	public void savePeaks(long setId, List<FilterPeak> peaks) {
		MsppManager manager = MsppManager.getInstance();
		Session session = manager.getSession();
		
		session.beginTransaction();
		
		this.deletePeaks(setId, session);
		for (FilterPeak peak : peaks) {
			peak.setSetId(setId);
			session.save(peak);
		}
		
		session.getTransaction().commit();		
		session.close();
	}
	
	public List<FilterPeak> getPeaks(long setId) {
		MsppManager manager = MsppManager.getInstance();
		Session session = manager.getSession();

		Query<FilterPeak> query = session.createQuery("from FilterPeak where setId = :setId", FilterPeak.class);
		query.setParameter("setId", setId);
		List<FilterPeak> list = query.getResultList();

		session.close();

		return list;
	}
	
	public FilterPeakSet saveNew(List<FilterPeak> peaks) {
		FilterPeakSet set = null;
		
		MsppManager manager = MsppManager.getInstance();
		ResourceBundle messages = manager.getMessages();
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Save As");
		dialog.setHeaderText(messages.getString("peak_filter.saveas.title"));
		dialog.setContentText(messages.getString("peak_filter.saveas.title"));
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String name = result.get();
			set = this.saveNew(name);
			this.savePeaks(set.getId(), peaks);
		}
		
		return set;
	}

	
	public static PeakFilterManager getInstance() {
		if (instance == null) {
			instance = new PeakFilterManager();
		}
		return instance;
	}
}
