package ninja.mspp.operation.peak_filter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import ninja.mspp.MsppManager;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Point;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.core.model.ms.Spectrum;
import ninja.mspp.core.view.ViewInfo;
import ninja.mspp.operation.peak_filter.model.HitPeak;
import ninja.mspp.operation.peak_filter.model.entity.FilterPeak;
import ninja.mspp.operation.peak_filter.model.entity.FilterPeakSet;

public class PeakFilterManager {
	private static PeakFilterManager instance;
	public static final String SET_KEY = "PEAK_FILTER_SET";
	
	private ViewInfo<PeakFilterDialog> activeDialog;
	
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
	
	
	private double getIntensityThreshold(DataPoints points, double threshold, String unit) {
		double intensityThreshold = threshold;
		if(unit.equals("%")) {
			double maxIntensity = 1.0;
			for (Point point : points) {
				if (point.getY() > maxIntensity) {
					maxIntensity = point.getY();
				}
			}
			intensityThreshold = maxIntensity * threshold / 100.0;
		}
		return intensityThreshold;
	}
	
	
	private Point searchPeak(DataPoints points, FilterPeak peak, double tolerance, double intensityThreshold) {
		Point result = null;
		double mz = peak.getMz();
		double minMz = mz - tolerance;
		double maxMz = mz + tolerance;
		double maxIntensity = 0.0;
		
		int startIndex = Collections.binarySearch(points, new Point(minMz, 0.0));
		if(startIndex < 0) {
			startIndex = - startIndex - 1;
		}
		int endIndex = Collections.binarySearch(points, new Point(maxMz, 0.0));
		if (endIndex < 0) {
			endIndex = - endIndex - 2;
		}
		
		for (int i = startIndex; i <= endIndex; i++) {
			Point point = points.get(i);
			if (point.getY() >= intensityThreshold) {
				if (point.getY() > maxIntensity) {
					maxIntensity = point.getY();
					result = point;
				}
			}
		}
		
		return result;
	}
	
	
	public List<HitPeak> searchPeaks(Sample sample, List<FilterPeak> peaks, double tolerance, double threshold, String unit) {
		List<HitPeak> result = new ArrayList<HitPeak>();
		
		for(Spectrum spectrum : sample.getSpectra()) {
			if(spectrum.getMsLevel() >= 2) {
				System.out.println("Searching..." + spectrum.getTitle());
				DataPoints points = spectrum.readDataPoints();
				HitPeak hitPeak = null;
				double intensityThreshold = getIntensityThreshold(points, threshold, unit);
				for (FilterPeak peak : peaks) {
					Point point = this.searchPeak(points, peak , tolerance, intensityThreshold);
					if(point != null) {
						if(hitPeak == null) {
							hitPeak = new HitPeak(spectrum);
						}
						hitPeak.getHitMap().put(peak, point.getY());
					}
				}
				if(hitPeak != null) {
					result.add(hitPeak);
				}
			}
		}
		
		return result;
	}

	
	public void openResultDialog(List<FilterPeak> peaks, List<HitPeak> result) throws IOException {
		MsppManager manager = MsppManager.getInstance();
		ViewInfo<ResultDialog> viewInfo = manager.showDialog(ResultDialog.class, "ResultDialog.fxml");
		ResultDialog dialog = viewInfo.getController();
		dialog.setResult(peaks, result);
	}
	
	public void openDialog(Sample sample) throws IOException {
		MsppManager manager = MsppManager.getInstance();
		ViewInfo<PeakFilterDialog> viewInfo = manager.showDialog(PeakFilterDialog.class, "PeakFilterDialog.fxml");
		PeakFilterDialog dialog = viewInfo.getController();
		dialog.setSample(sample);
		this.activeDialog = viewInfo;
	}
	
	public void setActiveDialog(ViewInfo<PeakFilterDialog> dialog) {
		this.activeDialog = dialog;
	}
	
	
	public ViewInfo<PeakFilterDialog> getActiveDialog() throws IOException {
		MsppManager manager = MsppManager.getInstance();
		
		if (this.activeDialog == null) {
			Sample sample = manager.getActiveSample();
			if(sample == null) {
				Alert alert = new Alert(AlertType.ERROR);
				ResourceBundle messages = manager.getMessages();
				alert.setTitle("Error");
				alert.setHeaderText(messages.getString("sample.not_selected.title"));
				alert.setContentText(messages.getString("sample.not_selected.content"));
				alert.showAndWait();
			}
			else {
				this.openDialog(sample);
			}
		}
		return this.activeDialog;
	}

	
	public static PeakFilterManager getInstance() {
		if (instance == null) {
			instance = new PeakFilterManager();
		}
		return instance;
	}
}
