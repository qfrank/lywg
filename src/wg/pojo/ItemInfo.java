package wg.pojo;

public class ItemInfo {

	public int entryId;
	
	public String name;
	
	public String description;
	
	public int requiredLevel;
	
	public int itemLevel;

	@Override
	public String toString() {
		return "ItemInfo [entryId=" + entryId + ", name=" + name + ", description=" + description + ", requiredLevel=" + requiredLevel + ", itemLevel="
				+ itemLevel + "]";
	}
	
}
