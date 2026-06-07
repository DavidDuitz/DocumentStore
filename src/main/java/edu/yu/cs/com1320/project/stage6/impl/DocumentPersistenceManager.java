package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.DatatypeConverter;

public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    File baseDirectory;

    //Constructor for DocPersistenceManager
    public DocumentPersistenceManager(File baseDir){
        if(baseDir != null){
            //make the base dir that was passed in the directory where (de)serialization occurs
            this.baseDirectory = baseDir;
        }
        else{
            //make the base directory the rood directory of this project
            this.baseDirectory = new File(System.getProperty("user.dir"));
        }
    }

    @Override
    public void serialize(URI key, Document val) throws IOException{
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new CustomSerializer()).create();
        String docJson = gson.toJson(val);
        String uriStr = key.toString();
        if(uriStr.charAt(4) == ':'){
            uriStr = uriStr.substring(7) + ".json";
        }
        else{
            uriStr = uriStr.substring(8) + ".json";
        }
        uriStr = uriStr.replace("/", File.separator);
        File myFile = new File(this.baseDirectory, uriStr);
        myFile.getParentFile().mkdirs();
        //Add a Json file to disk
        try(FileWriter writer = new FileWriter(myFile)){
            writer.write(docJson);
        }
        catch (IOException e){
            throw new IOException("Issue in writing file in serialize", e);
        }
    }

    //Private custom serializer class for Document
    private class CustomSerializer implements JsonSerializer<Document> {
        @Override
        public JsonElement serialize(Document doc, Type typeOfSrc, JsonSerializationContext context){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("documentTxt", doc.getDocumentTxt());
            byte[] binaryData = doc.getDocumentBinaryData();
            if(binaryData != null){
                String base64Serialized = DatatypeConverter.printBase64Binary(binaryData);
                jsonObject.addProperty("documentBinaryData", base64Serialized);
            }
            //Manually add all metadata into json map
            JsonObject metadataJson = new JsonObject();
            Map<String, String> metaMap = doc.getMetadata();
            for(String metaKey : metaMap.keySet()){
                metadataJson.addProperty(metaKey, metaMap.get(metaKey));
            }
            jsonObject.add("metadata", metadataJson);
            jsonObject.addProperty("uri", doc.getKey().toString());
            //Manually add all words to json word map
            JsonObject wordMapJson = new JsonObject();
            Map<String, Integer> wordMapTemp = doc.getWordMap();
            if(wordMapTemp != null){
                for(String wordKey : wordMapTemp.keySet()){
                    wordMapJson.addProperty(wordKey, wordMapTemp.get(wordKey));
                }
                jsonObject.add("wordMap", wordMapJson);
            }
            return jsonObject;
        }
    }

    @Override
    public Document deserialize(URI key) throws IOException{
        String uriStr = key.toString();
        if(uriStr.charAt(4) == ':'){
            uriStr = uriStr.substring(7) + ".json";
        }
        else{
            uriStr = uriStr.substring(8) + ".json";
        }
        uriStr = uriStr.replace("/", File.separator);
        //Deserialize the file and delete it
        File myFile = new File(this.baseDirectory, uriStr);
        try(FileReader fileRead = new FileReader(myFile)){
            Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new CustomDeserializer()).create();
            Document mydoc = gson.fromJson(fileRead, DocumentImpl.class);
            //Delete it from disk
            this.delete(key);
            return mydoc;
        }
        catch (IOException e){
            throw new IOException("Issue in reading file in deserialize", e);
        }
    }

    //Private custom deserializer class for Document
    private class CustomDeserializer implements JsonDeserializer<Document> {
        @Override
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonObject metadataJson = jsonObject.getAsJsonObject("metadata");
            HashMap<String, String> metadata = new HashMap<>();
            for(String metaKey : metadataJson.keySet()){
                metadata.put(metaKey, metadataJson.get(metaKey).getAsString());
            }
            URI uri;
            try {
                uri = new URI(jsonObject.get("uri").getAsString());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            JsonElement jsonText = jsonObject.get("documentTxt");
            Document myDoc;
            //If it is a text doc...
            if (jsonText != null){
                String docText = jsonText.getAsString();
                JsonObject wordMapJson = jsonObject.getAsJsonObject("wordMap");
                HashMap<String, Integer> wordMap = new HashMap<>();
                for(String wordMapKey : wordMapJson.keySet()){
                    wordMap.put(wordMapKey, wordMapJson.get(wordMapKey).getAsInt());
                }
                myDoc = new DocumentImpl(uri, docText, wordMap);
            }
            //Otherwise it is a binary document...
            else{
                String base64Deserialized = jsonObject.get("documentBinaryData").getAsString();
                byte[] docBinary = DatatypeConverter.parseBase64Binary(base64Deserialized);
                myDoc = new DocumentImpl(uri, docBinary);
            }
            myDoc.setMetadata(metadata);
            myDoc.setLastUseTime(System.nanoTime());
            return myDoc;
        }

    }

    /**
     * delete the file stored on disk that corresponds to the given key
     * @param key
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    @Override
    public boolean delete(URI key) throws IOException{
        String uriStr = key.toString();
        if(uriStr.charAt(4) == ':'){
            uriStr = uriStr.substring(7) + ".json";
        }
        else{
            uriStr = uriStr.substring(8) + ".json";
        }
        uriStr = uriStr.replace("/", File.separator);
        //Delete the file
        File myFile = new File(this.baseDirectory, uriStr);
        boolean deleted = myFile.delete();
        if(deleted){
            return true;
        }
        else{
            return false;
        }
    }


}
