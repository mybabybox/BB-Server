package common.model;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/5/14
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TargetGender {
    Both(0),
    Male(1),
    Female(2);

    private int code;

    TargetGender(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public TargetGender getOppositeGender() {
        switch(this) {
            case Male:
                return TargetGender.Female;
            case Female:
                return TargetGender.Male;
            default:
                throw new IllegalStateException("Opposite gender not applicable to "+this.name());
        }
    }

    public static TargetGender valueOfStr(String genderStr) {
        TargetGender result = TargetGender.Both;

        if (genderStr != null) {
            if ("FEMALE".equals(genderStr.trim().toUpperCase())) {
                result = TargetGender.Female;
            } else if (genderStr.trim().toUpperCase().contains("F")) {
                result = TargetGender.Female;
            } else {
                result = TargetGender.Male;
            }
        }
        return result;
    }

    public static TargetGender valueOfInt(int genderInt) {
        for (TargetGender g : TargetGender.values()) {
            if (g.getCode() == genderInt) {
                return g;
            }
        }
        return TargetGender.Both;
    }
}
