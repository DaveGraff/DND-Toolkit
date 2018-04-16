/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnd.toolbox;

/**
 *
 * @author HP
 */
public class Base {
    public int roll(int num){
        return (int)(Math.random() * num) + 1;
    }
    
    public int roll4D6(){
        int min = 10;int total = 0;
        for(int i = 0; i < 4; i++){
            int temp = roll(6);
            if(min > temp)
                min = temp;
            total += temp;
        }
        return total - min;
    }
    
    public boolean isAHit(int roll, int ac){
        if(ac < -2){
            return roll + ac > 16;
        } else if (ac < 3){
            return roll > 19;
        } else {
            return roll + ac > 21;
        }
    }
}
