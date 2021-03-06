package me.wiefferink.areashop.handlers;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wiefferink.areashop.interfaces.AreaShopInterface;
import me.wiefferink.areashop.interfaces.RegionAccessSet;
import me.wiefferink.areashop.interfaces.WorldGuardInterface;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldGuardHandler6 extends WorldGuardInterface {

	public WorldGuardHandler6(AreaShopInterface pluginInterface) {
		super(pluginInterface);
	}

	@Override
	public Set<ProtectedRegion> getApplicableRegionsSet(Location location) {
		Set<ProtectedRegion> result = new HashSet<>();
		Vector vector = new Vector(location.getX(), location.getY(), location.getZ());
		for(ProtectedRegion region : pluginInterface.getWorldGuard().getRegionManager(location.getWorld()).getRegions().values()) {
			if(region.contains(vector)) {
				result.add(region);
			}
		}
		return result;
	}

	@Override
	public void setOwners(ProtectedRegion region, RegionAccessSet regionAccessSet) {
		DefaultDomain defaultDomain = buildDomain(regionAccessSet);
		if(!region.getOwners().toUserFriendlyString().equals(defaultDomain.toUserFriendlyString())) {
			region.setOwners(defaultDomain);
		}
	}

	@Override
	public void setMembers(ProtectedRegion region, RegionAccessSet regionAccessSet) {
		DefaultDomain defaultDomain = buildDomain(regionAccessSet);
		if(!region.getMembers().toUserFriendlyString().equals(defaultDomain.toUserFriendlyString())) {
			region.setMembers(defaultDomain);
		}
	}

	/**
	 * Build a DefaultDomain from a RegionAccessSet.
	 * @param regionAccessSet RegionAccessSet to read
	 * @return DefaultDomain containing the entities from the RegionAccessSet
	 */
	private DefaultDomain buildDomain(RegionAccessSet regionAccessSet) {
		DefaultDomain owners = new DefaultDomain();

		for(String playerName : regionAccessSet.getPlayerNames()) {
			owners.addPlayer(playerName);
		}

		for(UUID uuid : regionAccessSet.getPlayerUniqueIds()) {
			owners.addPlayer(uuid);
		}

		for(String group : regionAccessSet.getGroupNames()) {
			owners.addGroup(group);
		}

		return owners;
	}

	@Override
	public boolean containsMember(ProtectedRegion region, UUID player) {
		return region.getMembers().contains(player);
	}

	@Override
	public boolean containsOwner(ProtectedRegion region, UUID player) {
		return region.getOwners().contains(player);
	}

	@Override
	public Flag<?> fuzzyMatchFlag(String flagName) {
		return DefaultFlag.fuzzyMatchFlag(flagName);
	}

	@Override
	public <V> V parseFlagInput(Flag<V> flag, String input) throws InvalidFlagFormat {
		return flag.parseInput(WorldGuardPlugin.inst(), null, input);
	}

	@Override
	public RegionGroup parseFlagGroupInput(RegionGroupFlag flag, String input) throws InvalidFlagFormat {
		return flag.parseInput(WorldGuardPlugin.inst(), null, input);
	}
}
