import java.io.*;
import java.util.ArrayList;

/**
 * ULL - IA Avanzada - Practica Lenguaje Natural
 * Aprendizaje.java
 * Programa que dados dos ficheros, uno con vocabulario y otro
 * con texto genera un tercer fichero con las probabilidades
 * de cada palabra del vocabulario de aparecer en el texto.
 * 
 * @author Jesús Ramos Álvarez - Cristina Garrido Amador
 * @version 01/05/2018
 */
public class Aprendizaje {
	
	private static ArrayList<String> palabrasTexto;
	private static int tamVocabulario;
	
	private static void guardarPalabrasTexto (String ficheroTexto) throws IOException {
		palabrasTexto = new ArrayList<String>();
		BufferedReader readerTexto = new BufferedReader (new FileReader (ficheroTexto));
		String [] linea;
		String palabra;
		while (readerTexto.ready()) {
			linea = readerTexto.readLine().split("\\s+");
			for (int i = 0; i < linea.length; i++) {
				palabra = linea [i];
				if (!palabra.startsWith("@") && !palabra.startsWith("#") && !palabra.contains("http://") && !Vocabulario.contieneNumero(palabra)) {
					palabrasTexto.add(Vocabulario.quitarSimbolos(palabra));
				}
			}
		}
		readerTexto.close();
	}
	
	private static double logProbabilidad (int numeroVeces) {
		double prob = (numeroVeces + 1) / (palabrasTexto.size() + tamVocabulario);
		return Math.log(prob);
	}

	public static void main(String[] args) throws IOException {
		String ficheroVocabulario = args [0];
		String ficheroTexto = args [1];
		String ficheroSalida = args [2];
		
		guardarPalabrasTexto(ficheroTexto);
		
		BufferedReader readerVocabulario = new BufferedReader (new FileReader (ficheroVocabulario));
		BufferedWriter writer = new BufferedWriter (new FileWriter (ficheroSalida));		
		writer.write(ficheroSalida + ": \r\n");
		writer.write("Numero de palabras del corpus: " + palabrasTexto.size() + "\r\n\r\n");
		
		readerVocabulario.readLine();
		tamVocabulario = Integer.parseInt(readerVocabulario.readLine().replaceAll("[^0-9]", ""));
		while (readerVocabulario.ready()) {
			int numeroVeces = 0;
			String palabraVocabulario = readerVocabulario.readLine().trim();
			for (int i = 0; i < palabrasTexto.size(); i++) {
				if (palabraVocabulario.equals(palabrasTexto.get(i)))
					numeroVeces++;
			}
			writer.write("Palabra: " + palabraVocabulario + " Frec: " + numeroVeces + " LogProb: " + 
							logProbabilidad(numeroVeces) + "\r\n");
		}
		writer.close();
		readerVocabulario.close();
	}

}
