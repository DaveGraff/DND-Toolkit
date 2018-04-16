package dnd.toolbox;

import java.io.Serializable;

public class Character extends Base implements Serializable{
    private String name;
    private String job;
    private int level;
    private int[] stats;//STR INT WIS DEX CON CHA
    private int ac;
    private int hp;
    //private String weapon;
    private int hpMax;
    private int dmgRoll = 8;
    private int numDmgRoll = 1;//FIX ASAP
    private int dmgMod = 0;
    private int hitMod;
    
    Character(String iName, String iJob, int iLevel, int[] iStats, int iAC, int iHP){
        name = iName;
        job = iJob;
        level = iLevel;
        stats = iStats;
        ac = iAC;
        hp = iHP;
        hpMax = hp;
        hitMod = hitMod(job, level, stats);
    }
    
    Character(String iName, String iJob, int iLevel, int[] iStats,
            int iAC, int iHP, int iDmgRoll, int iNumDmgRoll, int iDmgMod, int iHitMod){
        name = iName;
        job = iJob;
        level = iLevel;
        stats = iStats;
        ac = iAC;
        hp = iHP;
        hpMax = hp;
        hitMod = iHitMod;
        dmgRoll = iDmgRoll;
        numDmgRoll = iNumDmgRoll;
        dmgMod = iDmgMod;
    }
    
    public String getName(){return name;}
    public void setName(String n){name = n;}
    public String getJob(){return job;}
    public void setJob(String j){job = j;}
    public int getLevel(){return level;}
    public void setlevel(int n){level = n;}
    public int[] getStats(){return stats;}
    public void setStats(int[] n){stats = n;}
    //public String getWeapon(){return weapon;}
    //public void setWeapon(String n){weapon = n;}
    public int getHP(){return hp;}
    public void setHP(int n){hp = n;}
    public int getAC(){return ac;}
    public void setAC(int n){ac = n;}
    public int getHPMax(){return hpMax;}
    public void setHPMax(int n){hpMax = n;}
    public int getDmgRoll(){return dmgRoll;}
    public void setDmgRoll(int n){dmgRoll = n;}
    public int getNumDmgRoll(){return numDmgRoll;}
    public void setNumDmgRoll(int n){numDmgRoll = n;}
    public int getHitMod(){return hitMod;}
    public void setHitMod(int h){hitMod = h;}
    public int getDmgMod(){return dmgMod;}
    public void setDmgMod(int h){dmgMod = h;}
    
    public void UpdateHP(int n){
        hp -= n;
    }    
    
    public int hitMod (String job, int level, int[] stats){
    int cHitMod = 0;
    if(!"MU".equals(job)) {
        if (stats[0] > 16)
            cHitMod++;
        if (stats[3] > 15)
            cHitMod = stats[3] - 15;
    }

    if("fighter".equals(job)){
        if(level == 0)
            return 0;
        return level + 1;
    }
    if("MU".equals(job) && level == 0)
        return 0 + cHitMod;
    if(("thief".equals(job) && level < 3) || ("MU".equals(job) && level < 4))
        return 1 + cHitMod;
    if(("cleric".equals(job) && level < 3) || ("thief".equals(job) && level < 5) || ("MU".equals(job) && level < 6))
        return 2 + cHitMod;
    if(("cleric".equals(job) && level < 4) || ("thief".equals(job) && level < 6) || ("MU".equals(job) && level < 8))
        return 3 + cHitMod;
    if(("cleric".equals(job) && level < 6) || ("thief".equals(job) && level < 8) || ("MU".equals(job) && level < 10))
        return 4 + cHitMod;
    if(("cleric".equals(job) && level < 7) || ("thief".equals(job) && level < 9) || ("MU".equals(job) && level < 11))
        return 5 + cHitMod;
    if(("cleric".equals(job) && level < 9) || ("thief".equals(job) && level < 11) || ("MU".equals(job) && level < 13))
        return 6 + cHitMod;
    if(("cleric".equals(job) && level < 10) || ("thief".equals(job) && level < 13) || ("MU".equals(job) && level < 14))
        return 7 + cHitMod;
    if(("cleric".equals(job) && level < 12) || ("thief".equals(job) && level < 15) || ("MU".equals(job) && level < 16))
        return 8 + cHitMod;
    if(("cleric".equals(job) && level < 13) || ("thief".equals(job) && level < 17) || ("MU".equals(job) && level < 18))
        return 9 + cHitMod;
    if(("cleric".equals(job) && level < 15) || ("thief".equals(job) && level < 21) || ("MU".equals(job)))
        return 10 + cHitMod;
    if(("cleric".equals(job) && level < 16) || ("thief".equals(job) && level < 21))
        return 11 + cHitMod;
    if(("cleric".equals(job) && level < 18) || ("thief".equals(job)))
        return 12 + cHitMod;
    if("cleric".equals(job) && level < 19)
        return 13 + cHitMod;
    if("cleric".equals(job))
        return 14 + cHitMod;
    return 0;
    }
    
    /*public int[] pickWeapon(String job, int level, int[] stats){
        //It is assumed characters will use one of the most effective weapons they have
        //Thief: short sword MU: dagger fighter: bastard sword cleric: hammer
        if(job.equals("MU")){
            
        }
        int[] dmg = new int[3];
        dmg[0]=0;
        dmg[1]=1;
        return dmg;
    }*/
}