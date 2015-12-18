package dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.BSON;
import org.bson.BsonArray;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
public class wordDAO {
	
	
	
	private static final String GLOBAL = "Global";
	private static final String NAME_COLLECTION = "words";
	private static final String CHAMP_WORD = "word";
	private static final String CHAMP_COUNT = "count";
	private static final String CHAMP_DOCS = "docs";
	private static final String MOTEUR_RECHERCHE = "MoteurRecherche";
	private static final MongoClient mongoclient = new MongoClient();
	private static final MongoDatabase db = mongoclient.getDatabase(MOTEUR_RECHERCHE);
	private static final MongoCollection<Document> collection = db.getCollection(NAME_COLLECTION);
	private static final MongoCollection<Document> count = db.getCollection(GLOBAL);
	
	public static void init(){
		Document compteur = new Document("name","compteur").append("numberofwords",0);
		count.insertOne(compteur);
	}
	
	public static void save(String word, String docWord, int numberWordDocu){
		
		//Récupère le Document correspondant au word
		Document result = collection.find(eq(CHAMP_WORD,word)).first();
		
		//Recupère le nombre de mot totaux
		Document compteur = count.find().first();
		
		//Save in variable and increment
		int com = (int) compteur.get("numberofwords") +1;
		//System.err.println(com);
		
		//Update le nombre de mot
		BasicDBObject update = new BasicDBObject().append("numberofwords", com);
		BasicDBObject setQuery = new BasicDBObject();
		setQuery.append("$set", update);
		count.updateOne(compteur,setQuery );
		
		
		if(result != null){
			//Si le mot existe
			//Increment le champs Count ( Occurences)
			
			int count = result.getInteger(CHAMP_COUNT)+1;
			
			//Recupère la list de Document
			List<Document> list = (ArrayList<Document>) result.get(CHAMP_DOCS);
			
			//Parcours la liste des documents
			//System.out.println("LISTE REINITIALISE MTHAFUCKA");
			List<Document> aux  = new ArrayList<Document>();
			boolean nouv = true;
			for(Document ob : list){
				
				//Recupère le nom du document
				String name = ob.getString("name");
				//System.err.println("The document is already in the list :");
				//System.err.println(word + "  Name of the  current doc in the loop : " + name);
				//System.err.println(word + "  Name of the doc we try to insert : " + docWord);
				//System.err.println(word + "  List before : " + aux.toString());
				// Si le document est déjà dans la liste, incrémente son compteur pour ce mot
				if(name.equals(docWord)){
					//int c  = ob.getInt("count");
					//System.err.println("testEquals");
					//System.out.println("AVANT " + aux.toString());
					int c = ob.getInteger("count") +1;
					//System.err.println("Name of the current doc equals name of the inserting doc");
					
					ob.put("count",c);
					//System.err.println(ob.toString());
					aux.add(ob);
					//System.out.println("Apres " + aux.toString());
					nouv = false;

				}else{
					//System.err.println("Name are different");

					//Si le document n'est pas dans la liste, on le rajoute
					//System.err.println("NIKSAMAIRE");
					
					//Document a = new Document("name",docWord).append("count", 1).append("countg",numberWordDocu);
					aux.add(ob);
					
				}
			}
			if(nouv){
				aux.add(new Document(new Document("name",docWord).append("count", 1).append("countg",numberWordDocu)));
			}
			//System.err.println(word +" List after : " + aux.toString());
			//System.err.println(aux.toString());
				
			//Update la liste des documents
			//System.out.println(word +"  "+ aux.toString());

			if(nouv){
				Document update2 = new Document().append(CHAMP_COUNT, count).append(CHAMP_DOCS,aux);
				BasicDBObject setQuery2 = new BasicDBObject();
				setQuery2.append("$set", update2);
				collection.updateOne(eq(CHAMP_WORD,word),setQuery2 );
				
			}else{
				collection.deleteOne(eq(CHAMP_WORD,word));
				Document doc = new Document(CHAMP_WORD,word).append(CHAMP_COUNT,count).append(CHAMP_DOCS, aux);
				collection.insertOne(doc);
			}
			
		}
		//Si le mot n'existe pas dans la collection
		else{
			//Create the list of Docs
			List<Document> list =new ArrayList<Document>();
			//Add the current doc
			list.add(new Document("name",docWord).append("count",1).append("countg",numberWordDocu));
			//Create a new Docu corresponding to the new word
			Document doc = new Document(CHAMP_WORD,word).append(CHAMP_COUNT,1).append(CHAMP_DOCS, list);
			
			//Insert it
			collection.insertOne(doc);
		}
		
	}
	
	public static void calculateTF(){
		FindIterable<Document> result = collection.find();
		result.forEach(new Block<Document>(){
			@Override
			public void apply(final Document doc){
				int count = (Integer)doc.get("count");
			}
		});
	}


	
	
}
