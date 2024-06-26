package ninja.mspp.io.mzml.test;

import org.junit.Test;

import io.github.msdk.MSDKException;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.io.mzml.MzmlReader;

public class MzmlTest {
	@Test
	public void test() throws MSDKException {
		String file = "/data/small.pwiz.1.1.mzML";
		MzmlReader reader = new MzmlReader();
		Sample sample = reader.read(file);
	}

}
