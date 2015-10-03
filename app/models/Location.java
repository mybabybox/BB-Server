package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.db.jpa.JPA;

/**
 * No UI Crud operation for this model. Static Lookup for country.
 * 
 * TODO - keith
 * Put all locations into local cache. (May need to clear cache when locations update)
 */
@Entity
public class Location  {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @ManyToOne
    public Location parent;
    
    public String name;
    
    public String displayName;
    
    @Enumerated(EnumType.STRING)
    public LocationType locationType;
    
    @Enumerated(EnumType.STRING)
    public LocationCode locationCode;
    
    public static enum LocationType {
        COUNTRY,
        STATE,
        CITY,
        REGION,
        DISTRICT,
        AREA,
        LOCATION
    }
    
    public static enum LocationCode {
        HK
    }
    
    public Location() {}

    public Location(LocationCode locationCode, String name) {
        this(locationCode, name, name);
    }
    
    public Location(LocationCode locationCode, String name, String displayName) {
        this.parent = null;
        this.locationType = LocationType.COUNTRY;
        this.locationCode = locationCode;
        this.name = name;
        this.displayName = displayName;
    }
    
    public Location(Location parent, String name) {
        this(parent, name, name);
    }
    
    public Location(Location parent, String name, String displayName) {
        this.parent = parent;
        this.locationCode = parent.locationCode;
        this.name = name;
        this.displayName = displayName;
        
        if (LocationType.COUNTRY.equals(parent.locationType)) {
            this.locationType = LocationType.STATE;
        } else if (LocationType.STATE.equals(parent.locationType)) {
            this.locationType = LocationType.CITY;
        } else if (LocationType.CITY.equals(parent.locationType)) {
            this.locationType = LocationType.REGION;
        } else if (LocationType.REGION.equals(parent.locationType)) {
            this.locationType = LocationType.DISTRICT;
        } else if (LocationType.DISTRICT.equals(parent.locationType)) {
            this.locationType = LocationType.AREA;
        } else if (LocationType.AREA.equals(parent.locationType)) {
            this.locationType = LocationType.LOCATION;
        }
    }

    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Traverse all children and add to the list if flagged.
     * 
     * @param country
     * @param locationTypes
     * @return
     */
    public static List<Location> getLocationsByCountry(Location country, LocationType[] locationTypes) {
        if (!LocationType.COUNTRY.equals(country.locationType))
            throw new RuntimeException(country + " is not a country");
        
        List<Location> locations = new ArrayList<Location>();
        List<LocationType> types = Arrays.asList(locationTypes);
        
        if (types.contains(LocationType.COUNTRY)) {
            locations.add(country);
        }

        List<Location> states = getStatesByCountry(country.id);
        for (Location state : states) {
            if (types.contains(LocationType.STATE)) 
                locations.add(state);
            
            List<Location> cities = getCitiesByState(state.id);
            for (Location city : cities) {
                if (types.contains(LocationType.CITY)) 
                    locations.add(city);
                
                List<Location> regions = getRegionsByCity(city.id);
                for (Location region : regions) {
                    if (types.contains(LocationType.REGION)) 
                        locations.add(region);
                    
                    List<Location> districts = getDistrictsByRegion(region.id);
                    for (Location district : districts) {
                        if (types.contains(LocationType.DISTRICT)) 
                            locations.add(district);
                        
                        List<Location> areas = getAreasByDistrict(district.id);
                        for (Location area : areas) {
                            if (types.contains(LocationType.AREA)) 
                                locations.add(area);
                            
                            if (types.contains(LocationType.LOCATION))
                                locations.addAll(getLocationsByArea(area.id));
                        }
                    }
                }
            }
        }
        return locations;
    }
    
    public static Location getLocation(LocationCode code, LocationType type) {
        Query q = JPA.em().createQuery("select l from Location l where locationCode = ?1 and locationType = ?2");
        q.setParameter(1, code);
        q.setParameter(2, type);
        return (Location)q.getSingleResult();
    }
    
    public static Location getParentLocation(Location location, LocationType locationType) {
        if (location.locationType == locationType) {
            return location;
        }
        
        while (location.parent != null) {
            Location parent = location.parent;
            if (parent.locationType == locationType) {
                return parent;
            }
        }
        throw new RuntimeException("Location " + location + " is not under a " + locationType.name());
    }
    
    public static Location getLocationById(long id) {
        Query q = JPA.em().createQuery("Select l from Location l where id = ?1");
        q.setParameter(1, id);
        return (Location)q.getSingleResult();
    }
    
    public static List<Location> getAllCountries() {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1");
        q.setParameter(1, LocationType.COUNTRY);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getStatesByCountry(long countryId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.STATE);
        q.setParameter(2, countryId);
        return (List<Location>)q.getResultList();
    }

    public static List<Location> getCitiesByState(long stateId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.CITY);
        q.setParameter(2, stateId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getRegionsByCity(long cityId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.REGION);
        q.setParameter(2, cityId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getDistrictsByRegion(long regionId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.DISTRICT);
        q.setParameter(2, regionId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getAreasByDistrict(long districtId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.AREA);
        q.setParameter(2, districtId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getLocationsByArea(long areaId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.LOCATION);
        q.setParameter(2, areaId);
        return (List<Location>)q.getResultList();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Location) {
            final Location other = (Location) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + locationCode + "|" + locationType + "|" + name + "|" + displayName + "]";
    }
    
    // HONG KONG specific
    
    public static List<Location> getHongKongDistricts() {
        Query q = JPA.em().createQuery("select l from Location l where locationType = ?1 and locationCode = ?2");
        q.setParameter(1, LocationType.DISTRICT);
        q.setParameter(2, LocationCode.HK);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getHongKongRegions() {
        Query q = JPA.em().createQuery("select l from Location l where locationType = ?1 and locationCode = ?2");
        q.setParameter(1, LocationType.REGION);
        q.setParameter(2, LocationCode.HK);
        return (List<Location>)q.getResultList();
    }
    
    public static Location getHongKongCity() {
        return getLocation(LocationCode.HK, LocationType.CITY);
    }
    
    public static List<Location> getHongKongCityRegionsDistricts() {
        return getLocationsByCountry(getLocation(LocationCode.HK, LocationType.COUNTRY), 
                new LocationType[] { LocationType.CITY, LocationType.REGION, LocationType.DISTRICT });
    }
}
