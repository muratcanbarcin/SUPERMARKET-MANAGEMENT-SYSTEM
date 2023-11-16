package src;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Test {

    public static void main(String[] args) throws IOException {
        int counter =0;

        File test_file = new File("src\\supermarket_dataset_5.csv");

        BufferedReader br = new BufferedReader(new FileReader(test_file));
        Scanner scan = new Scanner(System.in);
        System.out.println("\nShould your hashing be done using Simple Summation Function (SSF) or Polynomial Accumulation Function (PAF)? \n(SSF: 1, PAF:2): ");
        String SSForPAF = scan.next();
        HashTable<String,String> Customer_History= new HashTable<>(50000,SSForPAF); //change for another file

        String line;
        // int counter = 0;

        while ((line = br.readLine()) != null) {
            if (line == "Customer ID,Customer Name,Date,Product Name"){
                continue;
            }

            StringTokenizer tokenizer = new StringTokenizer(line, ","); //scanning text files and tokenizizng lines 
            
            String customer_id = tokenizer.nextToken();
            String customer_name = tokenizer.nextToken();
            String purchase_date = tokenizer.nextToken();
            String product_name = tokenizer.nextToken();

            Customer new_customer = new Customer(customer_id, customer_name);
            new_customer.addPurchase(purchase_date, product_name);

            Customer_History.put(customer_id,new_customer,purchase_date,product_name);
            counter++;
            System.out.println(counter + " purchase added"); //test
        }
        br.close();

        boolean customer_search = true;

        while(customer_search){ //UI (terminal)

            System.out.println("\nPlease enter customer ID: ");
            String input_ID = scan.next();
    
            ArrayList<Purchase> purchases = Customer_History.getValue(input_ID).getPurchases();
    
            System.out.println("\nCustomer Name: " + Customer_History.getValue(input_ID).getCustomerName());
    
            for (int i = 0; i < purchases.size(); i++){
                System.out.println(purchases.get(i).getDate() + " " + purchases.get(i).getProductName());
            }
            System.out.print("\nContinue Y/N: ");

            String choice = scan.next().toLowerCase();

            if (choice.equals("n")){
                customer_search = false;
            }
        }
    }
}
