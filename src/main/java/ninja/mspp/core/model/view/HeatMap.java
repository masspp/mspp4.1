package ninja.mspp.core.model.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Point;
import ninja.mspp.core.model.ms.Spectrum;

public class HeatMap {
	public static final int RT_SIZE = 512;
	public static final int MZ_SIZE = 512;
	private static final double RT_DULATION = 1.0;
	private static final double MZ_DULATION = 0.1;
	
	private double[][] data;
	private Range mzRange;
	private Range rtRange;
	private double maxIntensity;
	
	private List<Spectrum> spectra;
	
	public HeatMap(List<Spectrum> spectra) {
		this.data = null;
		this.mzRange = null;
		this.rtRange = null;
		this.maxIntensity = 0.0;
		
		this.spectra = spectra;
		
		if(spectra != null && !spectra.isEmpty()) {
			double startRt = spectra.getFirst().getRt();
			double endRt = spectra.getLast().getRt();
			endRt = Math.max(endRt, startRt + 0.0001);
			this.rtRange = new Range(startRt, endRt);
			
			double startMz = Double.POSITIVE_INFINITY;
			double endMz = Double.NEGATIVE_INFINITY;
			for(Spectrum spectrum : spectra) {
				if(spectrum.getMsLevel() == 1) {
					startMz = Math.min(startMz, spectrum.getMinMz());
					endMz = Math.max(endMz, spectrum.getMaxMz());
				}
			}
			endMz = Math.max(endMz,  startMz + 0.0001);
			this.mzRange = new Range(startMz, endMz);
			
			this.data = this.createData(spectra, this.rtRange, this.mzRange);
		}
	}
	
	public double[][] getData() {
		return data;
	}
	
	public void setData(double[][] data) {
		this.data = data;
	}
	
	public Range getRtRange() {
		return this.rtRange;
	}
	
	public void setRtRange(Range range) {
		this.rtRange = range;
	}
	
	public Range getMzRange() {
		return this.mzRange;
	}
	
	public void setMzRange(Range range) {
		this.mzRange = range;
	}
	
	public double getMaxIntensity() {
		return this.maxIntensity;
	}
	
	public void setMaxIntensity(double intensity) {
		this.maxIntensity = intensity;
	}
	
	public void changeRange(double startRt, double endRt, double startMz, double endMz) {
		this.rtRange = new Range(startRt, endRt);
		this.mzRange = new Range(startMz, endMz);
		this.data = this.createData(this.spectra, this.rtRange, this.mzRange);
	}
	
	protected double[][] createData(
			List<Spectrum> spectra,
			Range rtRange,
			Range mzRange
	) {
		System.out.println("Creating Heatmap.....");
		double[][] data = new double[RT_SIZE][MZ_SIZE];
		for( double[] array : data ) {
			Arrays.fill( array,  0.0 );
		}

		double rtUnit = (rtRange.getEnd() - rtRange.getStart()) / (double)(RT_SIZE - 1);
		double mzUnit = (mzRange.getEnd() - mzRange.getStart()) / (double)(MZ_SIZE - 1);
		int rtDulationRange = (int)Math.max(0, Math.round(RT_DULATION / rtUnit));
		int mzDulationRange = (int)Math.max(0, Math.round(MZ_DULATION / mzUnit));

		List<Spectrum> msList = new ArrayList< Spectrum >();
		for(Spectrum spectrum : spectra) {
			if(spectrum.getMsLevel() == 1) {
				msList.add( spectrum );
			}
		}

		List<Spectrum> spectrumList = new ArrayList<Spectrum>();
		Spectrum prevSpectrum = null;
		for(Spectrum spectrum : msList) {
			double rt = spectrum.getRt();
			int rtIndex = (int)Math.round((rt - rtRange.getStart()) / rtUnit);
			if(rtIndex >= 0 && rtIndex < RT_SIZE) {
				if(prevSpectrum != null) {
					spectrumList.add(prevSpectrum);
					prevSpectrum = null;
				}
				spectrumList.add(spectrum);
			}
			else {
				if(rtIndex >= RT_SIZE && prevSpectrum == null) {
					spectrumList.add(spectrum);
				}
				prevSpectrum = spectrum;
			}
		}

		int prevRtIndex = - 1;
		double[] prevMzArray = new double[ MZ_SIZE ];
		Arrays.fill( prevMzArray, 0.0 );
		double maxIntensity = 1.0;
		for(Spectrum spectrum : spectrumList) {
			System.out.println("    Reading Spectrum [" + spectrum.getName() + "].....");
			double rt = spectrum.getRt();
			int rtIndex = (int)Math.round((rt - rtRange.getStart()) / rtUnit);
			
			DataPoints points = spectrum.readDataPoints();

			double[] mzArray = new double[MZ_SIZE];
			Arrays.fill(mzArray,  0.0);

			double prevValue = 0.0;
			int prevMzIndex = -1;
			for(Point point : points) {
				double mz = point.getX();
				double intensity = point.getY();
				int mzIndex = (int)Math.round((mz - mzRange.getStart()) / mzUnit);
				if(mzIndex >= 0 && mzIndex < MZ_SIZE) {
					mzArray[mzIndex] = Math.max(mzArray[mzIndex], intensity);
					if(rtIndex >= 0 && rtIndex < RT_SIZE) {
						maxIntensity  = Math.max(maxIntensity, intensity);
					}
				}

				if((prevMzIndex >= 0 || mzIndex >= 0) && (prevMzIndex < MZ_SIZE || mzIndex < MZ_SIZE)) {
					int diff = mzIndex - prevMzIndex;
					if(diff > 1) {
						if(diff <= mzDulationRange) {
							for(int i = 1; i < diff; i++) {
								int index = prevMzIndex + i;
								if(index >= 0 && index < MZ_SIZE) {
									mzArray[index] = prevValue + (intensity - prevValue) * (double)i / (double)diff;
								}
							}
						}
						else {
							for(int i = 1; i < mzDulationRange; i++) {
								int index = mzIndex - i;
								if(index >= 0 && index < MZ_SIZE) {
									mzArray[index] = intensity * (double)(mzDulationRange - i) / (double)mzDulationRange;
								}
								index = prevMzIndex + i;
								if(index >= 0 && index < MZ_SIZE) {
									mzArray[index] = Math.max(mzArray[index], prevValue * (double)(mzDulationRange - i) / (double)mzDulationRange);
								}
							}
						}
					}
				}
				prevMzIndex = mzIndex;
				prevValue = intensity;
			}
			if((rtIndex >= 0 || prevRtIndex >= 0) && (rtIndex < RT_SIZE || prevRtIndex < RT_SIZE)) {
				int diff = rtIndex - prevRtIndex;
				if(diff > 1) {
					if(diff < rtDulationRange) {
						for(int i = 1; i < diff; i++) {
							int index = prevRtIndex + i;
							if(index >= 0 && index < RT_SIZE) {
								if(prevMzArray != null) {
									for(int j = 0; j < mzArray.length; j++) {
										data[index][j] = prevMzArray[j] + (mzArray[j] - prevMzArray[j] ) * (double)i / (double)diff;
									}
								}
							}
						}
					}
					else {
						for(int i = 1; i < rtDulationRange; i++) {
							int index = rtIndex - i;
							if(index >= 0 && index < RT_SIZE) {
								for(int j = 0; j < mzArray.length; j++) {
									data[index][j] = mzArray[j] * (double)(rtDulationRange - i) / (double)rtDulationRange;
								}
							}
							index = prevRtIndex + i;
							if(index >= 0 && index < RT_SIZE) {
								if(prevMzArray != null) {
									for(int j = 0; j < mzArray.length; j++) {
										data[index][j] = Math.max(data[index][j],  prevMzArray[j] * (double)(rtDulationRange - i) / (double)rtDulationRange);
									}
								}
							}
						}
					}
				}
			}
			if(rtIndex >= 0 && rtIndex < RT_SIZE) {
				for(int i = 0; i < mzArray.length; i++) {
					data[rtIndex][i] = Math.max(mzArray[i],  data[rtIndex][i]);
				}
			}
			prevMzArray = mzArray;
			prevRtIndex = rtIndex;
		}
		this.maxIntensity = maxIntensity;

		for(double[] mzArray : data) {
			for(int i = 0; i < mzArray.length; i++) {
				mzArray[i] = Math.min(1.0, mzArray[ i ] / maxIntensity);
			}
		}

		return data;
	}
}
