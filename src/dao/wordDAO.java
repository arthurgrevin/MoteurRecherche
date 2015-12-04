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
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
public class wordDAO {
	
	
	
	private static final String NAME_COLLECTION = "words";
	private static final String CHAMP_WORD = "word";
	private static final String CHAMP_COUNT = "count";
	private static final String CHAMP_DOCS = "docs";
	private static final String MOTEUR_RECHERCHE = "MoteurRecherche";
	private static final MongoClient mongoclient = new MongoClient();
	private static final MongoDatabase db = mongoclient.getDatabase(MOTEUR_RECHERCHE);
	private static final MongoCollection<Document> collection = db.getCollection(NAME_COLLECTION);
	
	
	
	public static void save(String word, String docWord){
		
		Document result = collection.find(eq(CHAMP_WORD,word)).first();
		
		//System.err.println(word);
		
		if(result != null){
			int count = result.getInteger(CHAMP_COUNT);
			List<String> list = (ArrayList<String>) result.get(CHAMP_DOCS);
			if(!list.contains(docWord)){
				list.add(docWord);
			}
			count ++;
			
			for(int i = 0;i<list.size();i++){
				if(list.get(i)==null){
					System.err.println("AVANT "+list.toString());
					list.remove(i);
					System.err.println("APRES "+list.toString());

				}
			}
			
			BsonArray array = new BsonArray();
			
			//System.out.println(word+"  "+docWord +"  " + list);
			for(String x : list){	
			if(x == null)System.err.println("Sa mere");
			else array.add(new BsonString(x));
			}
			
			//System.err.println(array.toString());

			BasicDBObject update = new BasicDBObject().append(CHAMP_COUNT, count).append(CHAMP_DOCS,array);
			BasicDBObject setQuery = new BasicDBObject();
			setQuery.append("$set", update);
			collection.updateOne(eq(CHAMP_WORD,word),setQuery );
		}
		else{
					
			BsonArray array = new BsonArray();
			array.add(new BsonString(docWord));	
			Document doc = new Document(CHAMP_WORD,word).append(CHAMP_COUNT,1).append(CHAMP_DOCS, array);
			collection.insertOne(doc);
		}
		
	}
}
