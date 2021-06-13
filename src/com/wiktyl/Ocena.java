package com.wiktyl;

import com.google.gson.stream.JsonReader;
import com.wiktyl.model.Data;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import net.sourceforge.jFuzzyLogic.Gpr;

import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import org.python.util.PythonInterpreter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import com.google.gson.*;

import java.io.FileReader;

public class Ocena {
	public static Double score = 0d;

	public static void main(String[] args) throws Exception {

		Data data = new Data();

		setUpFrame();
		
		setFclFile();

		// Load from 'FCL' file
		String fileName = "./ocena.fcl";
		FIS fis = FIS.load(fileName, true);
		if( fis == null ) { 
			System.err.println("Nie moge zaladowc pliku: '" + fileName + "'");
			return;
		}


//		try(PythonInterpreter pyInterp = new PythonInterpreter()) {
//			pyInterp.exec("print('Hello Python World!')");
//			pyInterp.execfile("src/com/wiktyl/main.py");
//		}

		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		JsonReader reader = new JsonReader(new FileReader("src/com/wiktyl/test_project.pdf.json"));
		Gson gson = builder.create();
		Data data_x = gson.fromJson(reader,Data.class);

		//way to get data from json - simplify this in some way
//		System.out.println(data_x.getKoszt());

		// Pokazuje reguly
		FunctionBlock functionBlock = fis.getFunctionBlock(null);
//		JFuzzyChart.get().chart(functionBlock);

		// Ustawia wejscia
		fis.setVariable("koszty", 5000);
		fis.setVariable("trudnosc", 8);
		fis.setVariable("dlugosc", 6);

		// Wylicza zbiory rozmyte
		fis.evaluate();

		// Ustawia wyjscia
		Variable projekt = functionBlock.getVariable("projekt");



		// Pokazuje wykres zmiennych wyjsciowych

//		JFuzzyChart.get().chart(projekt, projekt.getDefuzzifier(), true);

		// Drukuje reguly
		System.out.println("fis");
		System.out.println(fis);
		System.out.println("Wynik liczbowy:" + fis.getVariable("projekt").getValue());
		System.out.println("Metoda:" + fis.getVariable("projekt").getName());
		System.out.println("Projekt:" + fis.getVariable("projekt"));
		System.out.println("% przynależności do zbioru \"dobry\":" + projekt.getMembership("dobry")*100 + "%");

		score = fis.getVariable("projekt").getValue();
	}

	private static void setFclFile() {
	}

	private static void setUpFrame() {
		JFrame frame = new JFrame("InputForm");
		frame.setContentPane(new InputForm().rootPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(500,500);
		frame.setVisible(true);
		frame.setTitle("System oceny projektów");
	}

}
