package com.client.util;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Database {
    private static Database instance;
    private static MongoClient mongoClient;
    //private static MongoDatabase database;
    private static DB database;

    private Database() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        database = mongoClient.getDB("Java-Chat");
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public DB getDatabase() {
        return database;
    }

    public static boolean checkAccount(String username) {
        if(database == null)
            new Database();

        DBCollection dbCollection = database.getCollection("Accounts");

        DBCursor cursor = dbCollection.find(new BasicDBObject("email", username));
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

        DBCollection dbCollection = database.getCollection("Accounts");

        DBCursor cursor = dbCollection.find(new BasicDBObject("email", email));
        try{
            while(cursor.hasNext()){
                String displayName = cursor.next().get("displayName").toString();
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

        DBCollection dbCollection = database.getCollection("Accounts");

        DBCursor cursor = dbCollection.find(new BasicDBObject("email", username));
        try{
            while(cursor.hasNext()){
                String passwordFromDatabase = cursor.next().get("password").toString();

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

//        // TO DO -> Password encryption

        DBCollection dbCollection = database.getCollection("Accounts");
        BasicDBObject document = new BasicDBObject("email", username);
        document.append("password", password);
        document.append("displayName", displayName);

        dbCollection.insert(document);

        if(checkAccount(username))
            return true;

        return false;
    }

    /**
     * Function that search in MongoDB for the account with the name from parameters
     * @param name name of the account
     * @return email - Email of the account
     */
    public static String getEmailFromMongoDB(String name) {
        if(database == null)
            new Database();

        DBCollection dbCollection = database.getCollection("Accounts");

        DBCursor cursor = dbCollection.find(new BasicDBObject("displayName", name));
        try{
            while(cursor.hasNext()){
                String email = cursor.next().get("email").toString();
                return email;
            }
        }
        finally {
            cursor.close();
        }

        return null;
    }

    public static InputStream getImageFromMongoDB(String email) throws IOException {
        if(database == null)
            new Database();

        GridFS gfsPhoto = new GridFS(Database.getInstance().getDatabase(), "photo");
        GridFSDBFile imageForOutput = gfsPhoto.findOne("ProfilePicture_" + email);

        return imageForOutput.getInputStream();
    }

    public static void saveImageIntoMongoDb(String pathToProfileImage, String email) throws IOException {
        if(pathToProfileImage != null) {
            String dbFileName = "ProfilePicture_" + email;
            File imageFile = new File(pathToProfileImage);
            GridFS gfsPhoto = new GridFS(Database.getInstance().getDatabase(), "photo");
            GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);
            gfsFile.setFilename(dbFileName);
            gfsFile.save();
        }
    }

//    public static void deleteAccount(String username, String password){
//        if(database == null)
//            new Database();
//
//        MongoCollection<Document> collection = database.getCollection("Accounts");
//        Document query = new Document("email", username);
//        query.append("password", password);
//
//        collection.deleteOne(query);
//    }
}
