package ninja.mspp.io.mzml;

import java.io.File;
import java.util.List;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.Chromatogram;
import io.github.msdk.datamodel.MsScan;
import io.github.msdk.datamodel.RawDataFile;
import io.github.msdk.io.mzml.MzMLFileImportMethod;
import ninja.mspp.core.model.ms.Sample;



public class MzmlReader {
	public Sample read(String path) throws MSDKException {
		File file = new File(path);
		MzMLFileImportMethod importer = new MzMLFileImportMethod(file);
		RawDataFile rawFile = importer.execute();
		Sample sample = new Sample(file.getAbsolutePath(), file.getName());
		
		List<Chromatogram> chromatograms = rawFile.getChromatograms();
		for (Chromatogram chromatogram : chromatograms) {
			MzmlChromatogram mzmlChromatogram = new MzmlChromatogram(chromatogram);
			sample.getChromatograms().add(mzmlChromatogram);
		}
		
		List<MsScan> scans = rawFile.getScans();
		for(MsScan scan : scans) {
			MzmlSpectrum mzmlSpectrum = new MzmlSpectrum(scan);
			sample.getSpectra().add(mzmlSpectrum);
        }
		System.out.println("Completed reading the file.");
		return sample;
	}
}
