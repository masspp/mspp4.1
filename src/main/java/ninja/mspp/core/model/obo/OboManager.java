package ninja.mspp.core.model.obo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OboManager {
	private static OboManager instance = null;
		
	private List<Obo> list;
	
	private OboManager() {
		this.list = new ArrayList<Obo>();
	}
	
	private void load() throws IOException {
		InputStream stream = this.getClass().getResourceAsStream("psi-ms.obo");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String line;
		boolean inTerm = false;
		boolean isObsolete = false;
        String id = null;
        String name = null;
        String def = null;
        List<String> parents = null;
        Map<Obo, List<String>> parentMap = new HashMap<Obo, List<String>>();
        Map<String, Obo> oboMap = new HashMap<String, Obo>();
        
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("[Term]")) {
				inTerm = true;
				isObsolete = false;
				id = null;
				name = null;
				def = null;
				parents = new ArrayList<String>();
			}
			else if(line.isEmpty()) {
				if (inTerm && !isObsolete) {
					Obo obo = new Obo(id, name, def);
					this.list.add(obo);
					oboMap.put(id, obo);
					parentMap.put(obo, parents);
				}
				inTerm = false;
			}
			else if(line.startsWith("id:")) {
				id = line.replace("id:", "").trim();
			}
			else if (line.startsWith("name:")) {
				name = line.replace("name:", "").trim();
			}
			else if (line.startsWith("def:")) {
				def = line.replace("def:", "").trim();
			}
			else if (line.startsWith("is_a:")) {
				String parent = line.replace("is_a:", "").trim();
				int index = parent.indexOf("!");
				if (index >= 0) {
					parent = parent.substring(0, index).trim();
				}
				parents.add(parent);
			}
			else if (line.startsWith("is_obsolete:")) {
				isObsolete = true;
			}
		}
		
		for (Obo obo : parentMap.keySet()) {
			List<String> parentList = parentMap.get(obo);
			List<Obo> list = new ArrayList<Obo>();
			for (String parent : parentList) {
				Obo parentObo = oboMap.get(parent);
				if (parentObo != null) {
					list.add(parentObo);
				}
			}
			obo.setParents(list);
		}
		
		reader.close();
	}
	
	public List<Obo> getOboList() {
		return this.list;
	}
	
	public Obo getObo(String id) {
		for (Obo obo : this.list) {
			if (obo.getId().equals(id)) {
				return obo;
			}
		}
		return null;
	}
	
	public static OboManager getInstance() throws IOException {
		if (instance == null) {
			instance = new OboManager();
			instance.load();
		}
		return instance;
	}
	
}
