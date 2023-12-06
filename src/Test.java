package src;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Test {

    private static String SSForPAF ="1";
    private static String LPorDH ="LP";
    private static double MAX_LOAD_FACTOR =0.5;
    public static void main(String[] args) throws IOException, InterruptedException {
        int counter =0;

        // File test_file = new File("test.txt");
        File test_file = new File("supermarket_dataset_50K.csv");

        BufferedReader br = new BufferedReader(new FileReader(test_file));
        Scanner scan = new Scanner(System.in);
        menu();
        System.out.println(SSForPAF +  " "+ MAX_LOAD_FACTOR + " "+ LPorDH);

        HashTableDH<String,String> Customer_History= new HashTableDH<>(10,SSForPAF,MAX_LOAD_FACTOR); //change for other data sets

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

    public static void menu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Database");
            System.out.println("2. Settings");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    // Database selected, exit menu
                    return;
                case 2:
                    // Settings selected
                    settingsMenu(scanner);
                    break;
                case 3:
                    // Exit program
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    public static void settingsMenu(Scanner scanner) {


        while (true) {
            System.out.println("1. Hash Function");
            System.out.println("2. Max Load Factor");
            System.out.println("3. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    // Hash Function selected
                    hashFunctionMenu(scanner);
                    break;
                case 2:
                    // Max Load Factor selected
                    System.out.print("Enter Max Load Factor: ");
                    String temp1= scanner.next();
                    MAX_LOAD_FACTOR = Double.parseDouble(temp1);
                    break;
                case 3:
                    // Back to main menu
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    public static void hashFunctionMenu(Scanner scanner) {
        System.out.println("1. Simple Summation Function (SSF)");
        System.out.println("2. Polynomial Accumulation Function (PAF)");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (choice) {
            case 1:
                SSForPAF = "1";
                break;
            case 2:
                SSForPAF = "2";
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
    }


}
