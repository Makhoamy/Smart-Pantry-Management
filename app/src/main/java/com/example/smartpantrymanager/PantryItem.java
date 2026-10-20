package com.example.smartpantrymanager;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double quantity;
    public String unit;
    public String expiryDate;
}
