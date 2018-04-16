package dnd.toolbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMain extends Application implements Serializable{
    ArrayList<Team> parties = new ArrayList<>();
    ArrayList<Character> population = new ArrayList<>();
    
    boolean isExceptional = false;
    TextField nameT;
    ComboBox jobC;
    TextField levelT;
    TextField chosenJob;//Just print selected...
    TextField hpT;
    TextField acT;
    TextField strT;
    TextField intT;
    TextField wisT;
    TextField dexT;
    TextField conT;
    TextField chaT;
    VBox changeableSpace;
    VBox innerSpace = new VBox();
    Stage popup = new Stage();
    TextArea action;
    Team side1;
    Team side2;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        loadData();
        ToolBar toolbar = new ToolBar();
        changeableSpace = new VBox();
        changeableSpace.setPrefHeight(400);changeableSpace.setPrefWidth(500);
        changeableSpace.setSpacing(5);
        VBox whole = new VBox();
        whole.getChildren().addAll(toolbar, changeableSpace);
        Scene scene = new Scene(whole);
        
        Button partyMaker = new Button();
        partyMaker.setText("Party Maker");
        partyMaker.setOnAction(e ->{partyMaker();});
        
        Button battleSimulator = new Button();
        battleSimulator.setText("Battle Simulator");
        battleSimulator.setOnAction(e -> {battleSimulator();});
                
        Button characterLibrary = new Button();
        characterLibrary.setText("Character Library");
        characterLibrary.setOnAction(e -> {characterLibrary();});
        toolbar.getItems().addAll(partyMaker, battleSimulator, characterLibrary);
        
        primaryStage.setTitle("D&D Toolkit");
        primaryStage.setScene(scene);
        primaryStage.show();
        partyMaker();
        
        jobC.valueProperty().addListener(e ->{
            chosenJob.setText((String)jobC.getValue());
        });
    }
    
    public void setUpSpace(VBox vbox){
        HBox name = new HBox();
        Label nameL = new Label(); nameL.setText("Name:");
        nameT = new TextField();
        name.getChildren().addAll(nameL, nameT);
        
        HBox job = new HBox();
        Label jobL = new Label(); jobL.setText("Class: ");
        ObservableList<String> options = FXCollections.observableArrayList(
                "Random", "Fighter", "Cleric" ,"Magic-User", "Thief");
        jobC = new ComboBox(options);
        jobC.getSelectionModel().selectFirst();
        Label levelL = new Label(); levelL.setText("Level:");
        levelT = new TextField();levelT.setText("1");
        levelT.setMaxWidth(45);
        job.getChildren().addAll(jobL, jobC, levelL, levelT);
        
        HBox forSpacing = new HBox();
        chosenJob = new TextField(); chosenJob.setMaxWidth(100);
        chosenJob.setDisable(true);
        Label spacing = new Label(); spacing.setText("\t ");
        Label exceptionalL = new Label(); exceptionalL.setText("Include non-exceptional Characters: ");
        CheckBox exceptionalC = new CheckBox();
        exceptionalC.setOnAction(e -> {isExceptional = !isExceptional;});
        forSpacing.getChildren().addAll(spacing, chosenJob, exceptionalL, exceptionalC);
        
        HBox hpAC = new HBox();
        Label hpL = new Label(); hpL.setText("HP:\t ");
        hpT = new TextField(); hpT.setMaxWidth(50);
        Label acL = new Label(); acL.setText("AC:");
        acT = new TextField(); acT.setMaxWidth(40);
        hpAC.getChildren().addAll(hpL, hpT, acL, acT);
        
        HBox strInt = new HBox();
        Label strL = new Label(); strL.setText("STR:\t ");
        strT = new TextField();
        Label intL = new Label(); intL.setText("INT:\t");
        intT = new TextField();
        strInt.getChildren().addAll(strL, strT, intL, intT);
        
        HBox wisDex = new HBox();
        Label wisL = new Label(); wisL.setText("WIS:\t ");
        wisT = new TextField();
        Label dexL = new Label(); dexL.setText("DEX:\t");
        dexT = new TextField();
        wisDex.getChildren().addAll(wisL, wisT, dexL, dexT);
        
        HBox conCha = new HBox();
        Label conL = new Label(); conL.setText("CON:");
        conT = new TextField();
        Label chaL = new Label(); chaL.setText("CHA:");
        chaT = new TextField();
        conCha.getChildren().addAll(conL, conT, chaL, chaT);
        
        strT.setMaxWidth(40);intT.setMaxWidth(40);wisT.setMaxWidth(40);
        dexT.setMaxWidth(40);conT.setMaxWidth(40);chaT.setMaxWidth(40);
        
        vbox.getStylesheets().add(FXMain.class.getResource("Formatting.css").toExternalForm());
        vbox.getChildren().addAll(name, job, forSpacing, hpAC, strInt, wisDex, conCha);
    }
        
    public void partyMaker(){
        changeableSpace.getChildren().clear();
        
        setUpSpace(changeableSpace);
        
        HBox commands = new HBox();
        Label textSaved = new Label();
        Button save = new Button(); save.setText("Save");
        save.setOnAction(e -> {
            Character temp = saveButton(false);
            if (temp != null){
                textSaved.setText("Character has been saved.");
                population.add(temp);
                saveData();
            } else  textSaved.setText("");
        });
        Button generate = new Button(); generate.setText("Generate");
        generate.setOnAction(e -> {generate(); textSaved.setText("");});
        commands.getChildren().addAll(generate, save, textSaved);
                
        changeableSpace.getChildren().addAll(commands);
    }
    
    public boolean checkName(String name){
        return population.stream().noneMatch((character) -> (character.getName().equals(name)));
    }
    
    public Character saveButton(boolean condition){
        if (!nameT.getText().equals("")){
            if(!checkIfNulls()){
                if(!chosenJob.getText().equals("Random")){
                    if(condition || checkName(nameT.getText())){
                        try{
                            int[] stats = new int[6];
                            stats[0] = Integer.parseInt(strT.getText());
                            stats[1] = Integer.parseInt(intT.getText());
                            stats[2] = Integer.parseInt(wisT.getText());
                            stats[3] = Integer.parseInt(dexT.getText());
                            stats[4] = Integer.parseInt(conT.getText());
                            stats[5] = Integer.parseInt(chaT.getText());
                            int hp = Integer.parseInt(hpT.getText());
                            int ac = Integer.parseInt(acT.getText());
                            int lvl = Integer.parseInt(levelT.getText());
                            Character temp = new Character(nameT.getText(), chosenJob.getText(), lvl, stats, hp, ac);
                            return temp;
                        } catch(NumberFormatException e){
                            alert("Error", "Stats must be whole numbers");
                        }
                    } else alert("Error", "Two characters cannot have the same name");
                } else alert("Error", "Please use generate if you wish to use the random character option");
            } else alert("Error", "Please make sure there are no empty fields");
        } else alert("Error", "Please set a name for the character first");
        return null;
    }
    
    public boolean checkIfNulls(){
        if(strT.getText().equals(""))
            return true;
        if(intT.getText().equals(""))
            return true;
        if(wisT.getText().equals(""))
            return true;
        if(dexT.getText().equals(""))
            return true;
        if(conT.getText().equals(""))
            return true;
        if(chaT.getText().equals(""))
            return true;
        if(hpT.getText().equals(""))
            return true;
        if(acT.getText().equals(""))
            return true;
        if(levelT.getText().equals(""))
            return true;
        if(chosenJob.getText().equals(""))
            return true;
        return false;
    }
    
    public void alert(String title, String message){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void saveData(){
        PrintWriter pw;
        try {
            pw = new PrintWriter("DnDProject.ser");
            pw.close();
        } catch (FileNotFoundException ex) {
            System.out.println("This text should never be visible.");
        } try{   
            FileOutputStream file = new FileOutputStream("DnDProject.ser");
            ObjectOutputStream out = new ObjectOutputStream(file);
            
            out.writeObject(parties);
            out.writeObject(population);
             
            out.close();
            file.close();     
        } catch(IOException ex){
            System.out.println("IOException is caught");
        }
    }
    
    public void loadData(){
        parties = new ArrayList<>();
        population = new ArrayList<>();
        try{   
            FileInputStream file = new FileInputStream("DnDProject.ser");
            ObjectInputStream in = new ObjectInputStream(file);
             
            parties = (ArrayList<Team>) in.readObject();
            population = (ArrayList<Character>) in.readObject();
            
            in.close();
            file.close();
        } 
        catch(IOException ex){
            System.out.println("No data to load");
        } 
        catch(ClassNotFoundException ex){
            System.out.println("ClassNotFoundException is caught");
        }
    }

    public void battleSimulator(){
        changeableSpace.getChildren().clear();
        
        HBox pickTeams = new HBox(); pickTeams.setSpacing(5);
        ComboBox side1B = new ComboBox(); side1B.setMaxWidth(100);
        ComboBox side2B = new ComboBox(); side2B.setMaxWidth(100);
        Label textSide1 = new Label();
        textSide1.setText("Side 1");
        Label textSide2 = new Label();
        textSide2.setText("Side 2");
        pickTeams.getChildren().addAll(textSide1, side1B, textSide2, side2B);
        
        parties.forEach(e -> {
            side1B.getItems().add(e.getName());
            side2B.getItems().add(e.getName());
        });
        
        action = new TextArea();
        Button resolve = new Button("Resolve");
        Button processTurn = new Button("Process Turn");
        Button cancel = new Button("Cancel"); cancel.setDisable(true);cancel.setOpacity(0);
        HBox buttons = new HBox(); buttons.getChildren().addAll(resolve, processTurn, cancel);
        
        resolve.setOnAction(e -> {
            if(!checkBoxValidity(side1B, side2B))
                alert("Error", "Please select a team first");
            else{
                if(side1 == null){
                    for(Team team : parties){
                        if(team.getName().equals(side1B.getValue()))
                            side1 = (Team)deepCopy(team);
                        if(team.getName().equals(side2B.getValue()))
                            side2 = (Team)deepCopy(team);
                    }
                }
                resolve(side1, side2);
                cancel.setDisable(false);
                cancel.setOpacity(100);
                resolve.setDisable(true);
                processTurn.setDisable(true);
            }
        });
        
        processTurn.setOnAction(e -> {
            if(!checkBoxValidity(side1B, side2B))
                alert("Error", "Please select a team first");
            else{
                if(side1 == null) {
                    for(Team team : parties){
                        if(team.getName().equals(side1B.getValue()))
                            side1 = (Team)deepCopy(team);
                        if(team.getName().equals(side2B.getValue()))
                            side2 = (Team)deepCopy(team);
                    }
                }
                action.setText(action.getText().concat(processTurn()));
                cancel.setDisable(false);cancel.setOpacity(100);
            }
        });
        
        cancel.setOnAction(e -> {
            side1 = null;
            side2 = null;
            action.setText("");
            cancel.setDisable(true);
            cancel.setOpacity(0);
            resolve.setDisable(false);
            processTurn.setDisable(false);
        });
        
        changeableSpace.getChildren().addAll(pickTeams, buttons, action);
    }
    
    private static Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean checkBoxValidity(ComboBox a, ComboBox b){
        if(a.getItems().isEmpty() || b.getItems().isEmpty())
            return false;
        if(a.getValue() == null || b.getValue() == null)
            return false;
        return true;
    }
    
    public void resolve(Team side1, Team side2){
        //processTurns...
        String combat = action.getText();
        while(!side1.getTeam().isEmpty() && !side2.getTeam().isEmpty()){
            combat = combat.concat(processTurn());
        }
        if(side1.getTeam().isEmpty())
            combat = combat.concat(side2.getName() + " has won!\nSurvivors:\n" + side2.toString());
        else
            combat = combat.concat(side1.getName() + " has won!\nSurvivors:\n" + side1.toString());
        action.setText(combat);
    }
    
    public String processTurn(){
        String writeTo = action.getText();
        writeTo = writeTo.concat("Next round! \n");
        
        if(side1.getTeam().isEmpty()){
            //stop tho
            writeTo = writeTo.concat(side2.getName() + " has won!\nSurvivors:\n" + side2.toString());
            return writeTo;
        } else if (side2.getTeam().isEmpty()){
            //also stop
            writeTo = writeTo.concat(side1.getName() + " has won!\nSurvivors:\n" + side1.toString());
            return writeTo;
        } else{//Check in between turns!
            int side1Roll = roll(6);
            int side2Roll = roll(6);
            if (side1Roll > side2Roll){//side1 first
                writeTo = writeTo.concat(assault(side1, side2, 2));
                writeTo = writeTo.concat(side2.clearDead());
                writeTo = writeTo.concat(assault(side2, side1, 1));
                writeTo = writeTo.concat(side1.clearDead());
            } else if(side2Roll > side1Roll){//side2 first
                writeTo = writeTo.concat(assault(side2, side1, 1));
                writeTo = writeTo.concat(side1.clearDead());
                writeTo = writeTo.concat(assault(side1, side2, 2));
                writeTo = writeTo.concat(side2.clearDead());
            } else {//simultaneous turns
                writeTo = writeTo.concat(assault(side2, side1, 1));
                writeTo = writeTo.concat(assault(side1, side2, 2));
                writeTo = writeTo.concat(side1.clearDead());
                writeTo = writeTo.concat(side2.clearDead());
            }
        }
       return writeTo;
    }
    
    public String assault(Team team, Team enemy, int determiner){
        String writeTo = "";
        for(Character character : team.getTeam()){
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
                writeTo = writeTo.concat(character.getName() + " has dealt " + dmgRoll + 
                        " damage to " + enemy.getTeam().get(enemyID).getName() + "!\n");
            }
        }
        if(determiner == 1)
            side1 = enemy;
        else if(determiner == 2)
            side2 = enemy;
            
        return writeTo;
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
    
    public void characterLibrary(){
        changeableSpace.getChildren().clear();
        
        Button addTeam = new Button(); addTeam.setText("Add Team");
        addTeam.setOnAction(e -> {
            buildATeam();
            innerSpace.getChildren().clear();
            displayTeams();
            displayCharacters();
        });
        renderInnerSpace();
        ScrollPane pls = new ScrollPane();
        pls.contentProperty().set(innerSpace);
        changeableSpace.getChildren().addAll(addTeam, pls);
    }
    
    public void buildATeam(){
        Label label = new Label(); label.setText("Team Name:");
        TextField name = new TextField();
        HBox hbox = new HBox(label, name); hbox.setSpacing(5);
        Button saveTeam = new Button(); saveTeam.setText("Save");
        VBox vbox = new VBox(hbox, saveTeam); vbox.setSpacing(5);
        Scene scene = new Scene(vbox);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        saveTeam.setOnAction(e -> {
            boolean exists = false;
            for(Team team : parties){
                if(team.getName().equals(name.getText()))
                    exists = true;
            }
            if(exists || name.getText().equals(""))
                alert("Error" , "Two teams cannot have identical names");
            else {
                parties.add(new Team(name.getText()));
                stage.close();
                saveData();
                renderInnerSpace();
            }
        });
    }
    
    public void renderInnerSpace(){
        innerSpace.getChildren().clear();
        displayTeams();
        displayCharacters();
    }
    
    public void teamEditor(Team team){
        Team tempTeam = new Team("");
        tempTeam.setTeam(team);
        //Stage popup = new Stage();

        Label teamNameL = new Label();
        TextField teamNameT = new TextField(team.getName());
        HBox teamName = new HBox(teamNameL, teamNameT);
        VBox content = new VBox(teamName);

        content.getChildren().add(new Label("Current Roster"));
        //Show current roster [Name][Num][Remove 1][Remove all]
        tempTeam.getTeam().forEach(member -> {
           Button name = new Button(member.getName());
           Button remove = new Button("Remove");
           HBox characterCell = new HBox(name, remove);
           content.getChildren().add(characterCell);
           remove.setOnAction(tt -> {
               tempTeam.removeCharacter(member);
               //newStage.close();//A better way is definitely available...
               teamEditor(tempTeam);
           });
        });
        content.getChildren().add(new Label("Available Characters"));
        //Show all available [Name][Add]
        population.forEach(character -> {
            Button name = new Button(character.getName());
            Button add = new Button("Add");
            HBox characterCell = new HBox(name, add);
            content.getChildren().add(characterCell);
            add.setOnAction(value -> {
                tempTeam.addCharacter(character);
                //newStage.close();
                teamEditor(tempTeam);
            });
        });

        Button save = new Button("Save"); save.setDefaultButton(true);
        save.setOnAction(thing -> {
            boolean isShared = false;
            for (Team teams : parties) {
                if (teams.getName().equals(teamNameT.getText())) {
                    isShared = true;
                }
            }
            if (teamNameT.getText().equals(team.getName())) {
                popup.close();
            } else if (teamNameT.getText().equals("")) {
                alert("Error", "Please set a name for the team");
            } else if (!isShared) {
                tempTeam.setName(teamNameT.getText());
                team.setTeam(tempTeam);
                renderInnerSpace();
                popup.close();
                saveData();
            } else {
                alert("Error", "Two teams cannot have the same name");
            }
        });
        
        Button cancel = new Button("Cancel"); cancel.setCancelButton(true);
        cancel.setOnAction(e -> popup.close());
        
        HBox options = new HBox(save, cancel);
        content.getChildren().add(options);

        content.getStylesheets().add(FXMain.class.getResource("Formatting.css").toExternalForm());

        Scene newScene = new Scene(content);
        popup.setScene(newScene);
        //popup.show();
    }
    
    public void displayTeams(){
        innerSpace.getChildren().add(new Label("Teams"));
        parties.forEach((team) ->{
            Button label = new Button(); label.setDisable(true);
            label.setText(team.getName());
            Button edit = new Button(); edit.setText("Edit");
            Button remove = new Button(); remove.setText("Remove");
            remove.setOnAction(e -> {
                parties.remove(team);
                renderInnerSpace();
                saveData();
            });
            HBox teamOptions = new HBox(label, edit, remove);
            teamOptions.setSpacing(5);
            innerSpace.getChildren().add(teamOptions);
            edit.setOnAction(e -> {
                teamEditor(team);
                popup.show();
            });
        });
    }
    
    public void displayCharacters(){
        Label characters = new Label("Characters");
        characters.setId("randomName");
        innerSpace.getChildren().add(characters);
        population.forEach((character) -> {
            HBox characterOptions = new HBox();
            Button label = new Button();
            label.setDisable(true);
            label.setText(character.getName() +" lvl "+character.getLevel() +" " + character.getJob());
            Button edit = new Button("Edit");
            edit.setMinWidth(50);
            edit.setOnAction((ActionEvent e) -> {
                Stage newWindow = new Stage();
                VBox vbox = new VBox();
                Scene scene = new Scene(vbox);
                newWindow.setScene(scene);
                setUpSpace(vbox);
                chosenJob.setText(character.getJob());
                nameT.setText(character.getName());
                levelT.setText(Integer.toString(character.getLevel()));
                hpT.setText(Integer.toString(character.getHP()));
                acT.setText(Integer.toString(character.getAC()));
                strT.setText(Integer.toString(character.getStats()[0]));
                intT.setText(Integer.toString(character.getStats()[1]));
                wisT.setText(Integer.toString(character.getStats()[2]));
                dexT.setText(Integer.toString(character.getStats()[3]));
                conT.setText(Integer.toString(character.getStats()[4]));
                chaT.setText(Integer.toString(character.getStats()[5]));
                
                Button save = new Button("Save"); save.setDefaultButton(true);
                save.setOnAction(g -> {
                    Character temp = saveButton(true);
                    if(temp != null){
                        character.setDmgMod(temp.getDmgMod());
                        character.setDmgRoll(temp.getDmgRoll());
                        character.setHP(temp.getHP());
                        character.setHPMax(temp.getHPMax());
                        character.setHitMod(temp.getHitMod());
                        character.setlevel(temp.getLevel());
                        character.setJob(temp.getJob());
                        character.setStats(temp.getStats());
                        character.setNumDmgRoll(temp.getNumDmgRoll());
                        character.setAC(temp.getAC());
                        character.setName(temp.getName());
                        saveData();
                    }
                    newWindow.close();
                    renderInnerSpace();
                });
                
                Button cancel = new Button("Cancel"); cancel.setCancelButton(true);
                cancel.setOnAction(q -> newWindow.close());
                
                HBox options = new HBox(save, cancel);
                
                vbox.getChildren().add(options);
                newWindow.show();
            });
            Button remove = new Button("Remove");
            remove.setMinWidth(75);
            remove.setOnAction(e -> {
                population.remove(character);
                saveData();
                renderInnerSpace();
            });
            characterOptions.getChildren().addAll(label, edit, remove);
            innerSpace.getChildren().add(characterOptions);
        });
    }
    
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
    
    public int roll4D6Until(int limit){
        if(limit > 17)
            return limit;
        int temp = 0;
        while (temp < limit){
            temp = roll4D6();
        }
        return temp;
    }
    
    public void setStatValues(){
        Integer temp;
        temp = roll4D6();strT.setText(temp.toString());
        temp = roll4D6();intT.setText(temp.toString());
        temp = roll4D6();wisT.setText(temp.toString());
        temp = roll4D6();dexT.setText(temp.toString());
        temp = roll4D6();conT.setText(temp.toString());
        temp = roll4D6();chaT.setText(temp.toString());
    }
    
    public boolean checkExceptional(){
        if (Integer.parseInt(strT.getText()) > 15)
            return true;
        if (Integer.parseInt(intT.getText()) > 15)
            return true;
        if (Integer.parseInt(wisT.getText()) > 15)
            return true;
        if (Integer.parseInt(dexT.getText()) > 15)
            return true;
        if (Integer.parseInt(conT.getText()) > 15)
            return true;
        if (Integer.parseInt(chaT.getText()) > 15)
            return true;
        return false;
    }
    
    public void generate(){
        String combobox = (String)jobC.getValue();
        if(combobox.equals("Random")){
            int i = roll(4);
            switch(i){
                case 1: chosenJob.setText("Fighter");
                break;
                case 2: chosenJob.setText("Cleric");
                break;
                case 3: chosenJob.setText("Magic-User");
                break;
                case 4: chosenJob.setText("Thief");
                break;
            }
            combobox = chosenJob.getText();
        }
        chosenJob.setText(combobox);
        
        Integer temp;
        boolean option = isExceptional;
        setStatValues();
        while(!isExceptional){
            setStatValues();
            switch (combobox) {
                case "Fighter":
                    temp = roll4D6Until(9);
                    strT.setText(temp.toString());
                    temp = roll4D6Until(7);
                    conT.setText(temp.toString());
                    break;
                case "Cleric":
                    temp = roll4D6Until(9);
                    wisT.setText(temp.toString());
                    break;
                case "Thief":
                    temp = roll4D6Until(9);
                    dexT.setText(temp.toString());
                    break;
                case "Magic-User":
                    temp = roll4D6Until(9);
                    intT.setText(temp.toString());
                    temp = roll4D6Until(6);
                    dexT.setText(temp.toString());
                    break;
                default:
                    break;
            }
            isExceptional = checkExceptional();
        }
        isExceptional = option;
        int[] stats = new int[6];
        stats[0] = Integer.parseInt(strT.getText());
        stats[1] = Integer.parseInt(strT.getText());
        stats[2] = Integer.parseInt(strT.getText());
        stats[3] = Integer.parseInt(strT.getText());
        stats[4] = Integer.parseInt(strT.getText());
        stats[5] = Integer.parseInt(strT.getText());
        int ac = getArmor(Integer.parseInt(levelT.getText()), combobox, stats);
        temp = ac; acT.setText(temp.toString());
        int hp = makeHP(Integer.parseInt(levelT.getText()), stats, combobox);
        temp = hp; hpT.setText(temp.toString());
    }
    
    public int getArmor(int level, String job, int[] stats){
        int ac = 10; int mod = 0;
        if(stats[3] < 7)
            ac += 7 - stats[3];
        if(stats[3] > 14)
            ac += 14 - stats[3];
        
        if(job.equals("Fighter") || job.equals("Cleric")){
            if(level == 0)
                ac -= 6;//chain mail w/ shield
            if (level == 1)
                ac -= 8;//plate mail w/ shield
            if(level == 2)
                ac -= 9;//plate armor w/ shield
            //Assume +'s to armor/shield
            else{
                mod = level -2;
                if(mod > 10)
                    mod = 10;
                ac -= mod;
            }
        }
        
        if(job.equals("Magic-User")){
            mod = level/3;//Ring of Protection
            if(mod > 5)
                mod = 5;
            ac -= mod;
        }
        
        if(job.equals("Thief")){
            ac -= 4;//Studded leather + shield
            mod = (level - 1) / 2;//shield plus
            if(mod > 5)
                mod = 5;
            ac -= mod;
        }
        
        if(ac > 10)
            ac = 10;
        if (ac < -10)
            ac = -10;
        return ac;
    }
    
    public int makeHP(int level, int[] stats, String job){
        int hpMod = 0;int hp = 0;
        switch (job) {
            case "Fighter":
                if(level == 0)
                    return roll(7);
                hpMod = 10;
                break;
            case "Magic-User":
                hpMod = 4;
                break;
            case "Thief":
                hpMod = 6;
                break;
            case "Cleric":
                hpMod = 8;
                break;
            default:
                break;
        }
        for(int i = 0; i < level; i++){
            hp += roll(hpMod);
        }
        return hp;
    }
}