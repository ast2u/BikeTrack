package com.example.biketrackcba;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

public class PlaceSearchHelper {
    public static void performSearch(String query, PlacesClient placesC, ArrayAdapter<String> suggestionAdapter,
                                     List<String> suggestionList){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("PH")
                .setQuery(query)
                .setSessionToken(token)
                .build();
        placesC.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            suggestionList.clear();
            for (AutocompletePrediction prediction : predictions) {
                suggestionList.add(prediction.getFullText(null).toString());
            }
            suggestionAdapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            Log.e("Place Prediction", "Error getting place predictions", exception);
        });
    }
}
