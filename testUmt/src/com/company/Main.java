package com.company;

import java.io.*;
import java.util.Vector;

/*
Presupuneri:
- intervalele sunt ordonate
- toate orele sa fie doar pm sau am
- toate intervalele de timp sa fie din aceeasi zi

Rezolvare:
- Pentru fiecare calendar am transformat intervalele de ora in minute
- Toate minutele ocupate din interval le-am marcat cu 0
- Am verificat intervalele de timp cumune (diferite de 0) care sunt mai mari sau egale cu meeting time
 */

public class Main {

    // citirea datelor din fisier
    Vector<String> calendar1 = new Vector<>();
    Vector<String> calendar2 = new Vector<>();
    String startCalendar1, endCalendar1, startCalendar2, endCalendar2;
    Integer meetingTime;
    public void readFromFile() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("inputData.txt"));
            String line = reader.readLine(); // read first line
            String[] sol = line.split("'");
            for (int i = 1; i < sol.length-1; i= i + 2) {
                calendar1.add(sol[i]);
            }
            line = reader.readLine(); // read 2nd line
            sol = line.split("'");
            startCalendar1 = sol[1];
            endCalendar1 = sol[3];
            line = reader.readLine(); // read 3rd line
            sol = line.split("'");
            for (int i = 1; i < sol.length-1; i= i + 2) {
                calendar2.add(sol[i]);
            }
            line = reader.readLine(); // read 4th line
            sol = line.split("'");
            startCalendar2 = sol[1];
            endCalendar2 = sol[3];
            line = reader.readLine(); // read 5th line
            meetingTime = Integer.parseInt(line);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // functie pentru sscrire in fisier a rezultatului din sirul sol (sir de stringuri)
    public static void writeInFile(Vector<String> sol)
    {
        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter("outputData.txt"));
            if(sol.size() == 0)
                myWriter.write("Nu exista intervale");
            else
            {
                myWriter.write("[");
                for(int i = 0; i < sol.size()-2; i= i+2){
                    myWriter.write("['" + sol.get(i) + "','" + sol.get(i+1)+"'], ");
                }
                myWriter.write("['" + sol.get(sol.size()-2) + "','" + sol.get(sol.size()-1)+"']");
                myWriter.write("]");

            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // functie care converteste un String in ora si minute de tip int (adaugate intr-un vector si returnate)
    public Vector<Integer> convertStringToTime(String data)
    {
        Vector<Integer> hourMinutes = new Vector<>();
        String[] splitArray = data.split(":");
        hourMinutes.add(Integer.parseInt(splitArray[0]));
        hourMinutes.add(Integer.parseInt(splitArray[1]));
        return hourMinutes;
    }


    // functie care converteste in string in minute si il returneaza
    public int convertHourInMinutes(String hour)
    {
        Vector<Integer> rezultat = convertStringToTime(hour);
        return rezultat.get(0) * 60 + rezultat.get(1);
    }

    // functie care returneaza pozitia din sirul array la care se afla valaore value sau altfel -1
    public int getPosition(Vector<Integer> array, int value)
    {
        for(int i = 0; i < array.size(); i++)
            if(value == array.get(i))
                return i;
        return -1;
    }

    // functie care converteste un minut(int) in ore si minute care sunt adaugate intr-un vector si returnate
    public Vector<Integer> convertMinutesInHour(int minutes)
    {
        int hour = minutes/60;
        int min = minutes % 60;
        Vector<Integer> vectHour = new Vector<>();
        vectHour.add(hour);
        vectHour.add(min);
        return vectHour;
    }

    // functie care adauga in sirul intervaleInMinute minutele de la capeele fiecarui interval pentru rezultatul final
    Vector<Vector<Integer>> intervaleInMinute = new Vector<>();   // matrice care contine pe fiecare linie 2 valuri in minute
    public void intervale()
    {
        int startCalendar1Min = convertHourInMinutes(startCalendar1);
        int startCalendar2Min = convertHourInMinutes(startCalendar2);
        int endCalendar1Min = convertHourInMinutes(endCalendar1);
        int endCalendar2Min = convertHourInMinutes(endCalendar2);
        Vector<Integer> minutesCalendar1 = new Vector<>();
        Vector<Integer> minutesCalendar2 = new Vector<>();


        for(int i = startCalendar1Min; i <= endCalendar1Min; i++)
            minutesCalendar1.add(i);

        for(int i = startCalendar2Min; i <= endCalendar2Min; i++)
            minutesCalendar2.add(i);

        Vector<Integer> minutesIntervalCalendar1 = new Vector<>(minutesCalendar1);
        Vector<Integer> minutesIntervalCalendar2 = new Vector<>(minutesCalendar2);

        for(int i = 0; i < calendar1.size(); i = i + 2)
        {
            String start = calendar1.get(i);
            String end = calendar1.get(i+1);
            int startMin = convertHourInMinutes(start);
            int endMin = convertHourInMinutes(end);
            for(int j = startMin; j <= endMin; j++)
                minutesIntervalCalendar1.set(getPosition(minutesCalendar1, j), 0);

        }

        for(int i = 0; i < calendar2.size(); i = i + 2)
        {
            String start = calendar2.get(i);
            String end = calendar2.get(i+1);
            int startMin = convertHourInMinutes(start);
            int endMin = convertHourInMinutes(end);
            for(int j = startMin; j <= endMin; j++)
                minutesIntervalCalendar2.set(getPosition(minutesCalendar2, j), 0);
        }

        // alegem intervalul cel mai mic comun
        if(startCalendar1Min < startCalendar2Min)
            startCalendar1Min = startCalendar2Min;
        if(endCalendar1Min > endCalendar2Min)
            endCalendar1Min = endCalendar2Min;

        for(int i = startCalendar1Min; i <= endCalendar1Min;)
        {
            int j = i;
            while(j <= endCalendar1Min && minutesIntervalCalendar1.get(getPosition(minutesCalendar1, j)) != 0 && minutesIntervalCalendar2.get(getPosition(minutesCalendar2, j)) != 0)
                j++;

            Vector<Integer> pereche = new Vector<>();
            if(j != i)
            {
                pereche.add(i-1);
                pereche.add(j);
                i = j;
                intervaleInMinute.add(pereche);
            }
            else  i++;
        }

    }

    /*
        functie care converteste minutele din sirul intervaleInMinute in stringuri si le adauga intr-un sir pentru a fi
        afisate in fisier.
    */
    Vector<String> intervalsInHour = new Vector<>();
    public void afisareRaspuns()
    {
        //intervale();
        for(int i = 0; i < intervaleInMinute.size(); i++)
        {
            int startMin = intervaleInMinute.get(i).get(0);
            int endMin = intervaleInMinute.get(i).get(1);
            int totalMinute = endMin-startMin;
            if(i == intervaleInMinute.size() - 1)
                endMin = intervaleInMinute.get(i).get(1)-1;
            if(totalMinute >= meetingTime)
            {
                Vector<Integer> rezultat1 = convertMinutesInHour(startMin);
                Vector<Integer> rezultat2 = convertMinutesInHour(endMin);
                String min1 = String.valueOf(rezultat1.get(1));
                if(min1.equals("0"))
                    min1 = "00";

                String min2 = String.valueOf(rezultat2.get(1));
                if(min2.equals("0"))
                    min2 = "00";
                String start = String.valueOf(rezultat1.get(0)) + ":" + min1;
                String end = String.valueOf(rezultat2.get(0)) + ":" + min2;
                intervalsInHour.add(start);
                intervalsInHour.add(end);
            }
        }
        writeInFile(intervalsInHour);
    }


    public static void main(String[] args) {
        Main m = new Main();
        m.readFromFile();
        m.intervale();
        m.afisareRaspuns();
    }
}
