package common.model;

import org.joda.time.DateTime;

public class TargetYear {
    
    public int birthYear;
    
    private TargetYear(DateTime birthday) {
        this.birthYear = birthday.getYear();
    }

    public int getBirthYear() {
        return this.birthYear;
    }

    public static TargetYear valueOf(DateTime birthday) {
        return new TargetYear(birthday);
    }
    
    @Override
    public String toString() {
        return "[birthYear=" + this.birthYear + "]";
    }
}
