package net.ieldor.game.model.region;


public final class Region {

	public static final int IDLE_REGION_PULSES = 400;

	public enum RegionState {
		ACTIVE, IDLE, DESTROYED
	}

	private int pulses;
	private RegionState state;
	private final RegionCoordinates regionCoordinates;
	private final RegionRepository regionRepository;

	public Region(RegionCoordinates regionCoordinates, RegionRepository regionRepository) {
		this.regionCoordinates = regionCoordinates;
		this.regionRepository = regionRepository;
	}
	
	public int getId() {
		return regionRepository.id;
	}

	public int getPulses() {
		return pulses;
	}

	public RegionState getState() {
		return state;
	}

	public RegionCoordinates getRegionCoordinates() {
		return regionCoordinates;
	}

	public RegionRepository getRegionRepository() {
		return regionRepository;
	}

	public void addPulse() {
		pulses++;
	}

	public void setState(RegionState state) {
		this.state = state;
	}

	public void purgeData() {
		RegionRepository.getActiveRegions().remove(regionCoordinates);
		//TODO remove NPC spawns here
		//TODO remove landscape data here
		//regionRepository.getRegionNPCs().clear();
		//regionRepository.getRegionObjects().clear();
		//regionRepository.getRegionItems().clear();
		
	}
}
