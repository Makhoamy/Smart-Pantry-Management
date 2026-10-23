package com.example.smartpantrymanager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class AppExecutors {
    public static final ExecutorService DATABASE_EXECUTOR =
            Executors.newSingleThreadExecutor();
}
