package dnd.toolbox;

import java.io.Serializable;
import java.util.ArrayList;

public class Team extends Base implements Serializable{
    private ArrayList<Character> team;
    private String name;
    
    public ArrayList<Character> getTeam(){return team;}
    public void setTeam(ArrayList<Character> c){team = c;}
    public String getName(){return name;}
    public void setName(String s){name = s;}
    
    Team(String iName){
        name = iName;
        team = new ArrayList<>();
    }
    
    Team(String iName, ArrayList<Character> c){
        name = iName;
        team = c;
    }
    
    public void addCharacter(Character c){
        team.add(c);
    }
    
    public void removeCharacter(Character c){
        team.remove(c);
    }
    
    public Character random(){
        int num = team.size()-1;
        return team.get(roll(num));
    }
    
    public String clearDead(){
        int i = 0;
        ArrayList<Integer> dead =  new ArrayList<>();//////////////////
        String deaths = "";
        for(Character character : team){
            if(character.getHP() < 1){
                deaths = deaths.concat(character.getName() + " has died! \n");
                //team.remove(i);
                dead.add(i);
            }
            i++;
        }
        dead.forEach((win) -> {
            team.remove(win.intValue());
        });
        return deaths;
    }
    
    public void setTeam(Team t){
        team = t.getTeam();
        name = t.getName();
    }
    
    public Team assault(Team enemy){
        team.forEach((character) ->{
            int roll = roll(20);
            int enemyID = roll(enemy.getTeam().size())-1;
            Character random = enemy.getTeam().get(enemyID);
            
            roll += character.getHitMod();
            if(isAHit(roll, random.getAC())){
                int dmgRoll = 0;
                for(int i = 0; i < character.getNumDmgRoll(); i++){
                    dmgRoll += roll(character.getDmgRoll());
                }
                dmgRoll += character.getDmgMod();
                enemy.getTeam().get(enemyID).UpdateHP(dmgRoll);
                enemy.clearDead();
            }
        });
        return enemy;
    }
    
    @Override
    public String toString(){
        String charList = "";
        for(Character character : team){
            charList = charList.concat(character.getName() + "\n");
        }
        return charList;
    }
}
