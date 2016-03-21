package edu.uta.mavpark.models;

import java.util.Dictionary;

/**
 * Created by krish on 3/21/2016.
 */
public class ErrorModel {

    public ErrorModel(String message, Dictionary<String, String[]> modelState) {
        this.message = message;
        this.modelState = modelState;
    }

    private String message;
    private Dictionary<String, String[]> modelState;
}
