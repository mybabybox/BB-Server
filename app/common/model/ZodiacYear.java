package common.model;

import common.model.TargetYear.Zodiac;

/**
 * update community set targetinginfo='2008_RAT' where targetinginfo='RAT';
 * update community set targetinginfo='2009_OX' where targetinginfo='OX';
 * update community set targetinginfo='2010_TIGER' where targetinginfo='TIGER';
 * update community set targetinginfo='2011_RABBIT' where targetinginfo='RABBIT';
 * update community set targetinginfo='2012_DRAGON' where targetinginfo='DRAGON';
 * update community set targetinginfo='2013_SNAKE' where targetinginfo='SNAKE';
 * update community set targetinginfo='2014_HORSE' where targetinginfo='HORSE';
 * update community set targetinginfo='2015_GOAT' where targetinginfo='GOAT';
 * update community set targetinginfo='2016_MONKEY' where targetinginfo='MONKEY';
 * update community set targetinginfo='2017_ROOSTER' where targetinginfo='ROOSTER';
 * update community set targetinginfo='2018_DOG' where targetinginfo='DOG';
 * update community set targetinginfo='2019_PIG' where targetinginfo='PIG';
 */
public class ZodiacYear {
    
    public int year;
    
    public Zodiac zodiac;
    
    public ZodiacYear(int year, Zodiac zodiac) {
        this.year = year;
        this.zodiac = zodiac;
    }
    
    public int getYear() {
        return this.year;
    }
    
    public Zodiac getZodiac() {
        return this.zodiac;
    }
    
    public static ZodiacYear parse(String value) {
        try {
            int year = Integer.valueOf(value.substring(0, 4));
            Zodiac zodiac = Zodiac.valueOf(value.substring(value.indexOf("_")+1));
            return new ZodiacYear(year, zodiac);
        } catch (NumberFormatException e) {
            ;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.year + "_" + this.zodiac.name();
    }
}