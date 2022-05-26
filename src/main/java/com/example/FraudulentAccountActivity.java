package com.example;

import java.util.Scanner;

public class FraudulentAccountActivity {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        String firstLine = input.nextLine();
        String secondLine = input.nextLine();

        String[] totalDaysAndPriorDays = firstLine.split(" ");
        int totalDays = Integer.parseInt(totalDaysAndPriorDays[0]);
        int priorDays = Integer.parseInt(totalDaysAndPriorDays[1]);

        String[] trans = secondLine.split(" ");

        System.out.println(checkUnusualTransaction(trans, totalDays, priorDays));

    }


    public static int checkUnusualTransaction(String[] listOfTrans, int totalDays, int priorDays){
        int notification = 0;
        int[] setOfDaysTransaction = new int[priorDays];
        int priorDaysCount;
        int currentTran;

        if(totalDays <= priorDays)
            return 0;

        for(int i = 0; i < totalDays-priorDays; i++){
            priorDaysCount = 0;

            for(int j=i; j<priorDays+i; j++){
                setOfDaysTransaction[priorDaysCount] = Integer.parseInt(listOfTrans[j]);

                priorDaysCount++;
            }
            currentTran = Integer.parseInt(listOfTrans[priorDays+i]);
            if(sendNotification(setOfDaysTransaction, priorDays, currentTran))
                notification++;

        }

        return notification;
    }

    public static boolean sendNotification(int[] setOfDaysTransaction, int priorDays, int currentTran){
        sortTransactions(priorDays, setOfDaysTransaction);

        if(currentTran >= median(setOfDaysTransaction, priorDays) * 2)
            return true;

        return false;
    }

    public static int median(int[] setOfDaysTransaction, int priorDays){
        int median;
        int position = priorDays/2;
        if(priorDays % 2 == 0 )
            median = (setOfDaysTransaction[position-1] + setOfDaysTransaction[position])/2;
        else
            median = setOfDaysTransaction[position];

        return median;
    }

    public static void sortTransactions(int priorDays, int[] setOfDaysTransaction){
        int temp;
        int i =0;
        while(i < priorDays - 1){
            if(setOfDaysTransaction[i] > setOfDaysTransaction[i+1]){
                temp = setOfDaysTransaction[i];
                setOfDaysTransaction[i] = setOfDaysTransaction[i+1];
                setOfDaysTransaction[i+1] = temp;

                i = (i != 0) ? i - 1 : i++;
            }
            else
                i++;
        }

    }
}
