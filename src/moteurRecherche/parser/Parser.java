package moteurRecherche.parser;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
	
	
	private static int TRONCLIMITE = 7;
	
	  public final static String[] FRENCH_STOP_WORDS = {
		    "a", "afin", "ai", "ainsi", "après", "attendu", "au", "aujourd", "auquel", "aussi",
		    "autre", "autres", "aux", "auxquelles", "auxquels", "avait", "avant", "avec", "avoir",
		    "c", "car", "ce", "ceci", "cela", "celle", "celles", "celui", "cependant", "certain",
		    "certaine", "certaines", "certains", "ces", "cet", "cette", "ceux", "chez", "ci",
		    "combien", "comme", "comment", "concernant", "contre", "d", "dans", "de", "debout",
		    "dedans", "dehors", "delà", "depuis", "derrière", "des", "désormais", "desquelles",
		    "desquels", "dessous", "dessus", "devant", "devers", "devra", "divers", "diverse",
		    "diverses", "doit", "donc", "dont", "du", "duquel", "durant", "dès", "elle", "elles",
		    "en", "entre", "environ", "est", "et", "etc", "etre", "eu", "eux", "excepté", "hormis",
		    "hors", "hélas", "hui", "il", "ils", "j", "je", "jusqu", "jusque", "l", "la", "laquelle",
		    "le", "lequel", "les", "lesquelles", "lesquels", "leur", "leurs", "lorsque", "lui", "là",
		    "ma", "mais", "malgré", "me", "merci", "mes", "mien", "mienne", "miennes", "miens", "moi",
		    "moins", "mon", "moyennant", "même", "mêmes", "n", "ne", "ni", "non", "nos", "notre",
		    "nous", "néanmoins", "nôtre", "nôtres", "on", "ont", "ou", "outre", "où", "par", "parmi",
		    "partant", "pas", "passé", "pendant", "plein", "plus", "plusieurs", "pour", "pourquoi",
		    "proche", "près", "puisque", "qu", "quand", "que", "quel", "quelle", "quelles", "quels",
		    "qui", "quoi", "quoique", "revoici", "revoilà", "s", "sa", "sans", "sauf", "se", "selon",
		    "seront", "ses", "si", "sien", "sienne", "siennes", "siens", "sinon", "soi", "soit",
		    "son", "sont", "sous", "suivant", "sur", "ta", "te", "tes", "tien", "tienne", "tiennes",
		    "tiens", "toi", "ton", "tous", "tout", "toute", "toutes", "tu", "un", "une", "va", "vers",
		    "voici", "voilà", "vos", "votre", "vous", "vu", "vôtre", "vôtres", "y", "à", "ça", "ès",
		    "été", "être", "ô","www","html"
		  };
	private static ArrayList<String> STOP_WORDS = new ArrayList<String> (Arrays.asList(FRENCH_STOP_WORDS));
	private static void parcourRecursif(Element e, ArrayList<String>result){
		Elements children = e.children();
		if (children.isEmpty()){
			result.add(e.text());
		}else{
			for(Element child : children){
				parcourRecursif(child, result);
			}
		}
	}
	
	private static ArrayList<String> filter(String corpus){
		
		//String result = corpus.replaceAll("!|\\?|,|\\.|'|:|«|", " ");
		ArrayList<String> stopWord = new ArrayList<String>();
		String result = corpus.toLowerCase();
		ArrayList<String> matches = new ArrayList<String>();
		Matcher m = Pattern.compile("(([a-z0-9éèêîïçëà]*))").matcher(result);
		while(m.find()){
			if(!STOP_WORDS.contains(m.group(1))){
				if(m.group(1).length()>0){
					if(m.group(1).length()>TRONCLIMITE){
						matches.add(m.group(1).substring(0,TRONCLIMITE));
					}else{
						matches.add(m.group(1));
					}
				}	
			}
		}
		
		return matches;
	}
	
	public static void main(String[] args){
		File file = new File("CORPUS/D1.html");
		try {
			ArrayList<String> result = new ArrayList<String>(); 
			Document doc = Jsoup.parse(file,"UTF8");
			Element e  = doc.select("body").first();
			ArrayList<String> corpus = new ArrayList<String>();
			parcourRecursif(e, corpus);
			String corpusStr = "";
			for(String mot : corpus){
				corpusStr += mot+" ";
			}
			System.out.println(corpusStr);
			for(String mot : filter(corpusStr)){
				System.out.println(mot);
			}
			
			FrenchAnalyzer frAn = new FrenchAnalyzer();
			TokenStream tks = frAn.tokenStream(null,new StringReader(corpusStr));
			tks.reset();
			
			while(tks.incrementToken()){
		        result.add(tks.getAttribute(CharTermAttribute.class).toString());			}
			for(String mot : result){
				//System.out.println(mot);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
