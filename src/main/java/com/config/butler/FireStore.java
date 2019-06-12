package com.config.butler;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FireStore {
    public static void init() {
        FileInputStream refreshToken = null;
        try {
            refreshToken = new FileInputStream("glitch-2eadc-firebase-adminsdk-wmtii-304d05a0f0.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(refreshToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String projectId = "glitch-2eadc";

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(options);
    }

    public void write(String user, String email, boolean isActive, String activationDate, String key, String status) {
        // Use the application default credentials


        Firestore db = FirestoreClient.getFirestore();

        DocumentReference docRef = db.collection("users").document(user);
// Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("activated", isActive ? "YES" : "NO");
        data.put("activated_on", activationDate);
        data.put("key", key);
        data.put("status", status);
//asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
// ...
// result.get() blocks on response

    }

    public void read() {
        try {
            FileInputStream refreshToken = new FileInputStream("glitch-2eadc-firebase-adminsdk-wmtii-304d05a0f0.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(refreshToken);
            String projectId = "glitch-2eadc";

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();
            FirebaseApp.initializeApp(options);

            Firestore db = FirestoreClient.getFirestore();
            // asynchronously retrieve all users
            ApiFuture<QuerySnapshot> query = db.collection("users").get();
// ...
// query.get() blocks on response
            QuerySnapshot querySnapshot = null;

            querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                System.out.println("user: " + document.getId());
                System.out.println("email: " + document.getString("email"));
                if (document.contains("activated")) {
                    System.out.println("Activated? " + document.getString("activated"));
                }
                System.out.println("Activated On: " + document.getString("activated_on"));
                System.out.println("Status: " + document.getLong("status"));
            }
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getDocument(String doc) {
        try {


            Firestore db = FirestoreClient.getFirestore();
            // asynchronously retrieve all users
            ApiFuture<QuerySnapshot> query = db.collection("users").get();
// ...
// query.get() blocks on response
            QuerySnapshot querySnapshot = null;

            querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            for (QueryDocumentSnapshot document : documents) {

                if (document.contains(doc)) {
                    return document.getString(doc);
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
