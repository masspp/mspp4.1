package ninja.mspp.core.model.obo;

import java.util.List;

public class Obo {
	private String id;
	private String name;
	private String def;
	private List<Obo> parents;
	
	public Obo(String id, String name, String def) {
		this.id = id;
		this.name = name;
		this.def = def;
		this.parents = null;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDef() {
		return this.def;
	}
	
	public List<Obo> getParents() {
		return this.parents;
	}
	
	public void setParents(List<Obo> parents) {
		this.parents = parents;
	}
	
	public boolean isParent(String id) {
		return isParent(id, this);
	}
	
	private boolean isParent(String id, Obo parent) {
		boolean result = false;
		if (parent.getId().equals(id)) {
			result = true;
		}
		else {
			List<Obo> parents = parent.parents;
			for(int i = 0; i < parents.size() && !result; i++) {
				result = result || isParent(id, parents.get(i));
			}
		}
		return result;
	}
}
