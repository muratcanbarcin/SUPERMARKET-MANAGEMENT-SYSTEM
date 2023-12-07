package src;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Test {

    private static String SSForPAF ="1";
    private static String LPorDH ="LP";
    private static double MAX_LOAD_FACTOR =0.5;
    private static HashTableLP<String,Customer> Customer_History;
    public static void main(String[] args) throws IOException, InterruptedException {
        int counter =0;

        String test_file = "supermarket_dataset_50K.csv";

        BufferedReader br = new BufferedReader(new FileReader(test_file));
        Scanner scan = new Scanner(System.in);
        menu();

        System.out.println(SSForPAF +  " "+ MAX_LOAD_FACTOR + " "+ LPorDH);

        Customer_History = new HashTableLP<>(128,SSForPAF,MAX_LOAD_FACTOR); //change for other data sets

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
        }
        br.close();

        Instant end = Instant.now();
        Duration time_elapsed = Duration.between(start, end);

        System.out.println(String.format("\nLoading customer data from %s completed in %d ms.", test_file, time_elapsed.toMillis()));
        System.out.println(String.format("Customers: %d \t Purchases: %d \t Collisions: %d",Customer_History.get_numberofcustomers(),counter,Customer_History.get_collisioncount()));


        if (test_file.equals("supermarket_dataset_50K.csv")){ //test file only works with 50K customer dataset
            test_input("customer_1K.txt");
        }


        while(true){ //UI (terminal)

            System.out.println("\nPlease enter customer ID: ");
            String input_ID = scan.next();

            System.out.print("\nView Purchases (1) / Remove Customer Data (2) : ");
            int choice1 = scan.nextInt();

            if (choice1 == 1){
                
                if (Customer_History.getValue(input_ID) != null){
                    Customer_History.getValue(input_ID).display_purchase();
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
                        System.out.println("Customer not found!");
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

    public static void test_input(String uuid_txt) throws IOException, InterruptedException{
        Scanner scan = new Scanner(System.in);
        System.out.print("\n\nDo you want to start 1K customer id test search Y/N: ");
        String test_choice = scan.next();
        int customer_count = 0;

        if (test_choice.equalsIgnoreCase("y")){

            BufferedReader br = new BufferedReader(new FileReader(uuid_txt));
            String line;

            Instant test_start, test_end;
            Duration max_duration = Duration.ZERO;
            Duration min_duration = Duration.ofSeconds(99);
            Duration total_duration = Duration.ZERO;

            while ((line = br.readLine()) != null) {
                String input_ID = line;
                Boolean customer_found = false;

                test_start = Instant.now();

                if (Customer_History.getValue(input_ID) != null){
                    customer_found = true;
                }

                test_end = Instant.now();

                if (customer_found){
                    Customer_History.getValue(input_ID).display_purchase();
                    customer_count++;
                }
                else{
                    System.out.println("Customer not found!");
                }

                Duration test_time = Duration.between(test_start, test_end);

                total_duration = total_duration.plus(test_time);

                if (test_time.compareTo(max_duration) >= 0){
                    max_duration = test_time;
                }
                if (test_time.compareTo(min_duration)<=0){
                    min_duration = test_time;
                }

            }
            br.close();

            double average_time = total_duration.getNano() / customer_count;     

            System.out.println("\nmax: " + max_duration.toNanos() + " min: " + min_duration.toNanos() 
                            + " Total Duration: " + total_duration.toNanos() + " Average Time: " + average_time 
                            + " customers: " + customer_count);
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
