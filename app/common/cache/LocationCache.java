package common.cache;

import models.Location;
import viewmodel.LocationVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/7/14
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocationCache {
    // Permanent cache loaded up on system startup.

    // Districts
    private static List<Location> hkDistrictsList;
    private static final Map<Long, Location> hkDistrictsMap = new HashMap<>();
    private static final List<LocationVM> hkDistrictsVMList = new ArrayList<>();

    // Regions
    private static final Map<Long, Location> hkRegionsMap = new HashMap<>();


    static {
        hkDistrictsList = Location.getHongKongDistricts();
        for (Location district : hkDistrictsList) {
            hkDistrictsMap.put(district.id, district);
            hkDistrictsVMList.add(new LocationVM(district));
        }

        List<Location> hkRegionsList = Location.getHongKongRegions();
        for (Location region : hkRegionsList) {
            hkRegionsMap.put(region.id, region);
        }
    }

    public static Location getDistrict(Long locationId) {
        return hkDistrictsMap.get(locationId);
    }

    public static List<Location> getHongKongDistricts() {
        return hkDistrictsList;
    }

    // for GUI drop-down
    public static List<LocationVM> getHongKongDistrictsVM() {
        return hkDistrictsVMList;
    }

    public static Location getRegion(Long regionId) {
        return hkRegionsMap.get(regionId);
    }
}
