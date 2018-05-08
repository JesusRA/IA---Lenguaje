import java.io.*;
import java.util.ArrayList;

/**
 * ULL - IA Avanzada - Practica Lenguaje Natural
 * Clasificacion.java
 * Programa que dado un fichero de vocabulario y unos ficheros de aprendizaje
 * es capaz de clasificar otro fichero segun este aprendizaje
 * 
 * @author Jesús Ramos Álvarez - Cristina Garrido Amador
 * @version 08/05/2018
 */
public class Clasificacion {
	
	private static double [] probAprendizaje;
	private static int [] numeroTweetsClasificados; // numero de tweets clasificados como cada corpus
	
	private static ArrayList<String> palabrasLinea (String linea) {
		ArrayList<String> palabras = new ArrayList <String> ();
		String [] p = linea.split("\\s+");
		for (int i = 0; i < p.length; i++) {
			String palabra = p [i];
			if (!palabra.startsWith("@") && !palabra.startsWith("#") && !palabra.contains("http://") && !Vocabulario.contieneNumero(palabra)) {
				palabras.add(Vocabulario.quitarSimbolos(palabra));
			}
		}
		return palabras;
	}
	
	private static double buscarValorUNK (String nombreFichero) throws IOException {
		BufferedReader readerA = new BufferedReader (new FileReader (nombreFichero));
		for (int k = 0; k < 3; k++)
			readerA.readLine();
		while (readerA.ready()) {
			String [] aprendizaje = readerA.readLine().split("\\s+");
			if (aprendizaje [1].equals("<unk>")) {
				readerA.close();
				return Double.parseDouble(aprendizaje [5]);
			}							
		}
		readerA.close();
		return 0;
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 3)
			System.err.println("Argumentos necesarios: ficheroSalida ficheroVocabulario ficheroAClasificar ficheroAprendizaje1...ficheroAprendizajeN");			
		else {
			String ficheroSalida = args [0];
			String ficheroVocabulario = args [1];
			String ficheroClasificar = args [2];
			String [] ficherosAprendizaje = new String [args.length - 3];
			for (int i = 3; i < args.length; i++)
				ficherosAprendizaje[i - 3] = args [i];
			
			probAprendizaje = new double [args.length - 3];
			numeroTweetsClasificados = new int [args.length - 3];	
			
			BufferedReader readerVocabulario = new BufferedReader (new FileReader (ficheroVocabulario));
			BufferedReader readerClasificar = new BufferedReader (new FileReader (ficheroClasificar));
			BufferedWriter writer = new BufferedWriter (new FileWriter (ficheroSalida));
			
			// Calcular P(I), P(D), P(A)
			
			readerVocabulario.readLine();
			int numTotalTweets = Integer.parseInt(readerVocabulario.readLine().replaceAll("[^0-9]", ""));
			
			for (int i = 0; i < ficherosAprendizaje.length; i++) {
				BufferedReader readerAprendizaje = new BufferedReader (new FileReader (ficherosAprendizaje[i]));
				readerAprendizaje.readLine();
				int numTweetsAprendizaje = Integer.parseInt(readerAprendizaje.readLine().replaceAll("[^0-9]", ""));
				double temp = (double) numTweetsAprendizaje / numTotalTweets;
				probAprendizaje [i] = Math.log(temp);
				readerAprendizaje.close();
			}
			
			// Clasificar
			
			System.out.println("Clasificando...");
			
			int [] clasificacionArray = new int [numTotalTweets]; // array que contendra los resultados de la clasificacion
			int l = 0;
				
			// Se separa cada Tweet en sus palabras
			while (readerClasificar.ready()) {
				double [] sumaProb = new double [args.length - 3];
				String tweet = readerClasificar.readLine();
				ArrayList<String> palabrasTweet = palabrasLinea(tweet);	
				
				// Por cada fichero de aprendizaje se busca su valor de <unk>, sus palabras y las probabilidades de cada una
				for (int i = 0; i < ficherosAprendizaje.length; i++) {
					double valorUNK = (double) buscarValorUNK (ficherosAprendizaje[i]);
					
					BufferedReader readerAprendizaje = new BufferedReader (new FileReader (ficherosAprendizaje[i]));
					for (int k = 0; k < 3; k++)
						readerAprendizaje.readLine();
					ArrayList<String> palabrasAprendizaje = new ArrayList <String>();
					ArrayList<Double> probPalabraAprendizaje = new ArrayList <Double>();
					while (readerAprendizaje.ready()) {
						String [] aprendizaje = readerAprendizaje.readLine().split("\\s+");
						palabrasAprendizaje.add(aprendizaje [1]);
						probPalabraAprendizaje.add(Double.parseDouble(aprendizaje [5]));
					}
					
					// Por cada palabra del Tweet se analiza si aparece en el fichero de aprendizaje
					// y se suma su probabilidad o la de <unk> en caso de no aparecer
					for (int j = 0; j < palabrasTweet.size(); j++) {
						boolean encontrada = false;	
						for (int k = 0; k < palabrasAprendizaje.size(); k++) {
							if (palabrasTweet.get(j).toLowerCase().equals(palabrasAprendizaje.get(k))) {
								sumaProb [i] += (double) probPalabraAprendizaje.get(k);
								encontrada = true;
							}								
						}											
						if (!encontrada) {
							sumaProb [i] += valorUNK;
						}
					}
					// Se suma a la suma de probabilidades la probabilidad general del fichero de aprendizaje
					sumaProb [i] += probAprendizaje [i];
					readerAprendizaje.close();
				}
				
				// Se calcula el maximo de las probabilidades obtenidas
				Double maxProb = sumaProb [0];
				int clasificacion = 0;
				
				for (int i = 1; i < sumaProb.length; i++)
					if (sumaProb [i] > maxProb) {
						maxProb = sumaProb [i];
						clasificacion = i;
					}
				
				// Se escribe en el fichero el resultado de la clasificacion y el tweet completo
				switch (clasificacion) {
					case 0: writer.write("Informacion "); break;
					case 1: writer.write("Dialogo "); break;
					case 2: writer.write("Accion "); break;
				}
				writer.write(tweet + "\r\n");
				
				// Se suma 1 al numero de Tweets clasificados en ese corpus
				numeroTweetsClasificados[clasificacion]++;
				
				// Se incluye en el array el resultado de la clasificacion
				clasificacionArray[l] = clasificacion;
				l++;
			}
			
			readerVocabulario.close();
			readerClasificar.close();
			writer.close();
			
			/*for (int i = 0; i < 3; i++) {
				System.out.println(numeroTweetsClasificados[i]);
			}
			System.out.println();*/
			
			// Calculo de los porcentajes de acierto
			double aciertosA = 0;
			double aciertosD = 0;
			double aciertosI = 0;
			for (int i = 0; i < 652; i++)
				if (clasificacionArray[i] == 2)
					aciertosA++;
			for (int i = 652; i < 652 + 205; i++)
				if (clasificacionArray[i] == 1)
					aciertosD++;
			for (int i = 652 + 205; i < numTotalTweets; i++)
				if (clasificacionArray[i] == 0)
					aciertosI++;
			
			System.out.println("El porcentaje de acierto de A es: " + (aciertosA / 652) * 100);
			System.out.println("El porcentaje de acierto de D es: " + (aciertosD / 205) * 100);
			System.out.println("El porcentaje de acierto de I es: " + (aciertosI / 859) * 100);
			System.out.println("El porcentaje de acierto total es: " + ((aciertosI + aciertosD + aciertosA) / numTotalTweets) * 100);
			
			System.out.println("Finalizado");
		}		
		
	}

}
