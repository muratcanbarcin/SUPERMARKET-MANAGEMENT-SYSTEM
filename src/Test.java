package src;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Test {

    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();

        int counter =0;

        File test_file = new File("supermarket_dataset_50K.csv");

        BufferedReader br = new BufferedReader(new FileReader(test_file));
        Scanner scan = new Scanner(System.in);
        System.out.println("\nShould your hashing be done using Simple Summation Function (SSF) or Polynomial Accumulation Function (PAF)? \n(SSF: 1, PAF:2): ");
        String SSForPAF = scan.next();
        HashTableLP<String,String> Customer_History= new HashTableLP<>(500,SSForPAF); //change for other data sets

        String line;

        while ((line = br.readLine()) != null) {
            if (line.equals("Customer ID,Customer Name,Date,Product Name")){
                continue;
            }

            StringTokenizer tokenizer = new StringTokenizer(line, ","); //scanning text files and tokenizizng lines 
            
            String customer_id = tokenizer.nextToken();
            String customer_name = tokenizer.nextToken();
            String purchase_date = tokenizer.nextToken();
            String product_name = tokenizer.nextToken();

            Customer new_customer = new Customer(customer_id, customer_name);
            new_customer.addPurchase(purchase_date, product_name);
            
            Customer_History.put(customer_id,new_customer);

            // counter++;
            // System.out.println(counter + " purchase added"); //test

        }
        br.close();
        Instant end = Instant.now();
        Duration time_elapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ time_elapsed.toMillis() +" ms" + "  Probe: " + HashTableLP.PROBE_TIME.toMillis() + " ms");



        while(true){ //UI (terminal)

            System.out.println("\nPlease enter customer ID: ");
            String input_ID = scan.next();

            System.out.print("\nView Purchases (1) / Remove Customer Data (2) : ");
            int choice1 = scan.nextInt();

            if (choice1 == 1){
                
                if (Customer_History.getValue(input_ID) != null){
                    ArrayList<Purchase> purchases = Customer_History.getValue(input_ID).getPurchases();
            
                    System.out.println("\n\nCustomer Name: " + Customer_History.getValue(input_ID).getCustomerName());
            
                    for (int i = 0; i < purchases.size(); i++){
                        System.out.println(purchases.get(i).getDate() + " " + purchases.get(i).getProductName());
                    }
                }
                else {
                    System.out.println("Invalid customer ID.");
                }
            }
            else {
                System.out.print("Customer data will be removed. Are you sure? Y/N: ");
                String choice2 = scan.next();

                if (choice2.toLowerCase().equals("y")){
                    if (Customer_History.getValue(input_ID) != null){
                        Customer_History.remove(input_ID);
                        System.out.println("Customer data deleted.");
                    }
                    else
                        System.out.println("Invalid customer ID.");
                }
            }

            System.out.print("\nContinue Y/N: ");

            String choice = scan.next().toLowerCase();

            if (choice.equals("y"))
                continue;
            else
                break;
        }
    }
}
