package ninja.mspp.io.heatmap;

import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.clazz.Listener;
import ninja.mspp.core.annotation.method.OnHeatMap;
import ninja.mspp.core.annotation.method.OnSelectSample;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.core.model.view.HeatMap;

@Listener("Heatmap Listener")
public class HeatMapListener {
	@OnSelectSample
	public void onSelectSample(Sample sample) {
		MsppManager manager = MsppManager.getInstance();
		
		HeatMap heatmap = new HeatMap(sample.getSpectra());
		manager.invoke(OnHeatMap.class, heatmap);
	}
}
