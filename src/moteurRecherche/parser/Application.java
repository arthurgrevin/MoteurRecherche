package moteurRecherche.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import dao.wordDAO;

public class Application {
	
	
	public static void main(String[] args){
		try {
			Files.walk(Paths.get("CORPUS")).forEach(x -> {
				if(Files.isRegularFile(x)){
					System.out.println(x.toAbsolutePath().toString());
					ArrayList <String> list = Parser.parser(x.toAbsolutePath().toString());
					for(String mot : list){
						if(mot != null){
							wordDAO.save(mot, x.toString());
						}
					}
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
}
