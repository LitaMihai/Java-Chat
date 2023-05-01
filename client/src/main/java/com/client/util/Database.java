package com.client.util;

import com.mongodb.client.*;
import org.bson.Document;

public class Database {
    private static Database instance;
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private Database() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("Java-Chat");
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public static boolean checkAccount(String username) {
        if(database == null)
            new Database();

        MongoCollection<Document> collection = database.getCollection("Accounts");
        Document query = new Document("email", username);

        MongoCursor<Document> cursor = collection.find(query).iterator();
        try{
            while(cursor.hasNext()){
                return true;
            }
        }
        finally {
            cursor.close();
        }
        return false;
    }

    public static String getName(String email){
        if(database == null)
            new Database();

        MongoCollection<Document> collection = database.getCollection("Accounts");
        Document query = new Document("email", email);

        MongoCursor<Document> cursor = collection.find(query).iterator();
        try{
            while(cursor.hasNext()){
                Document auxiliaryDocument = cursor.next();
                String displayName = auxiliaryDocument.getString("displayName");
                return displayName;
            }
        }
        finally {
            cursor.close();
        }

        return null;
    }

    public static boolean getAccount(String username, String password) {
        if(database == null)
            new Database();

        MongoCollection<Document> collection = database.getCollection("Accounts");
        Document query = new Document("email", username);

        MongoCursor<Document> cursor = collection.find(query).iterator();
        try{
            while(cursor.hasNext()){
                Document auxiliaryDocument = cursor.next();
                String passwordFromDatabase = auxiliaryDocument.getString("password");

                if(passwordFromDatabase.equals(password))
                    return true;
                else
                    return false;
            }
        }
        finally {
            cursor.close();
        }
        return false;
    }

    public static boolean insertAccount(String username, String password, String displayName) {
        if(database == null)
            new Database();

        // TO DO -> Password encryption



        MongoCollection<Document> collection = database.getCollection("Accounts");
        Document query = new Document("email", username);
        query.append("password", password);
        query.append("displayName", displayName);

        collection.insertOne(query);

        if(checkAccount(username))
            return true;

        return false;
    }

    public static void deleteAccount(String username, String password){
        if(database == null)
            new Database();

        MongoCollection<Document> collection = database.getCollection("Accounts");
        Document query = new Document("email", username);
        query.append("password", password);

        collection.deleteOne(query);
    }
}
