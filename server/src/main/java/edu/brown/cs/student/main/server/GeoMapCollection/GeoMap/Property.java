package edu.brown.cs.student.main.server.GeoMapCollection.GeoMap;

import edu.brown.cs.student.main.server.handlers.Inspectable;
import edu.brown.cs.student.main.server.handlers.Utils;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents the properties of a features, containing information such as name, state, city, etc.
 */
public class Property extends GeoMap implements Inspectable {

    public String name;

    public String state;

    public String city;

    public String holc_id;

    public String holc_grade;

    public Double neighborhood_id;

    public Map<String, String> area_description_data;

    /**
     * Checks if this property is equal to another object.
     *
     * @param obj The object to compare with this property.
     * @return True if the object is a Property with equal name, state, city, HOLC ID, HOLC grade, and
     * neighborhood ID, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Property p)) {
            return false;
        }
        return this.name.equals(p.name)
                && this.state.equals(p.state)
                && this.city.equals(p.city)
                && this.holc_id.equals(p.holc_id)
                && this.holc_grade.equals(p.holc_grade)
                && this.neighborhood_id.equals(p.neighborhood_id);
    }

    /**
     * Checks if this property passes the filter conditions based on the provided properties.
     *
     * @param properties The properties to filter the property by.
     * @return True if the property passes the filter conditions, false otherwise.
     */
    public boolean filterCheck(Map<String, Object> properties) {
        boolean desc_match = false;
        if (properties.containsKey("area_description_data")
                && properties.get("area_description_data") != null
                && properties.get("area_description_data") instanceof String targetDesc) {
            for (String desc : this.area_description_data.values()) {
                if (desc.contains(targetDesc)) {
                    desc_match = true;
                    break;
                }
            }
        } else {
            desc_match = true;
        }
        String[] exclude = {"area_description_data"};
        return desc_match && Utils.hasFields(this, properties, Arrays.asList(exclude));
    }

    public Map<String, Object> getFields() {
        return null;
    }
}
