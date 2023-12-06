package src;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
        int counter =0;

        // File test_file = new File("test.txt");
        File test_file = new File("supermarket_dataset_50K.csv");

        BufferedReader br = new BufferedReader(new FileReader(test_file));
        Scanner scan = new Scanner(System.in);
        System.out.print("\nShould your hashing be done using Simple Summation Function (SSF) or Polynomial Accumulation Function (PAF)? \n(SSF: 1, PAF:2): ");
        String SSForPAF = scan.next();
        HashTableDH<String,String> Customer_History= new HashTableDH<>(10,SSForPAF); //change for other data sets

        String line;

        Instant start = Instant.now();

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

            counter++;
            //System.out.println(counter + " purchase added"); //test
        }
        br.close();

        Instant end = Instant.now();
        Duration time_elapsed = Duration.between(start, end);

        System.out.println(String.format("\nLoading customer data from %s completed in %d ms.", test_file, time_elapsed.toMillis()));
        System.out.println(String.format("Customers: %d \t Purchases: %d \t Collisions: %d",Customer_History.get_numberofcustomers(),counter,Customer_History.get_collisioncount()));

        // if (test_file.equals("supermarket_dataset_50K.csv")){ //test file only works with 50K customer dataset
        //     test_input("customer_1K.txt", Customer_History);
        // }



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

    public static void test_input(String uuid_txt, HashTableLP<String,String> Customer_History) throws IOException, InterruptedException{
        Scanner scan = new Scanner(System.in);
        System.out.print("\n\nDo you want to start 1K customer id test search Y/N: ");
        String test_choice = scan.next();

        if (test_choice.toLowerCase().equals("y")){

            BufferedReader br = new BufferedReader(new FileReader(uuid_txt));
            String line;

            Instant test_start = Instant.now();

            while ((line = br.readLine()) != null) {
                String input_ID = line;

                if (Customer_History.getValue(input_ID) != null){
                    ArrayList<Purchase> purchases = Customer_History.getValue(input_ID).getPurchases();
            
                    System.out.println("\n\nCustomer Name: " + Customer_History.getValue(input_ID).getCustomerName());
            
                    for (int i = 0; i < purchases.size(); i++){
                        System.out.println(purchases.get(i).getDate() + " " + purchases.get(i).getProductName());
                    }
                }
                else
                    System.out.println("Invalid Customer!");
                //Thread.sleep(200); //test icin (odev yuklemesinde sil)
            }
            br.close();

            Instant test_end = Instant.now();

            Duration test_time = Duration.between(test_start, test_end);

            System.out.println("\n1K Customer Search (ms): " + test_time.toMillis());
        
        }
    }
}
