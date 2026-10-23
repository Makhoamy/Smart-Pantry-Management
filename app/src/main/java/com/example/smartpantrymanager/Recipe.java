package com.example.smartpantrymanager;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String instructions;

    public Recipe(String name, String instructions) {
        this.name = name;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
