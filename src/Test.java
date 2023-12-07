package src;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Test {

    // Constants and variables declaration
    private static String SSForPAF = "1"; // Default hash function (SSF:1 PAF:2)
    private static double MAX_LOAD_FACTOR = 0.5; // Default max load factor
    private static HashTableLP<String, Customer> Customer_History;


    public static void main(String[] args) {
        try {
            int counter = 0;

            // Input file for customer data
            String test_file = "supermarket_dataset_50K.csv";

            // Reading customer data from file
            BufferedReader br = new BufferedReader(new FileReader(test_file));
            Scanner scan = new Scanner(System.in);

            // Display menu
            menu();

            // Initialize customer history hash table
            Customer_History = new HashTableLP<>(128, SSForPAF, MAX_LOAD_FACTOR);
            System.out.println(Customer_History.getType());


            String line;

            // Record start time for loading data
            Instant start = Instant.now();

            // File loop
            while ((line = br.readLine()) != null) {
                // Skip header line
                if (line.equals("Customer ID,Customer Name,Date,Product Name")) {
                    continue;
                }

                // Tokenize the line
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                // Extract data from tokens
                String customer_id = tokenizer.nextToken();
                String customer_name = tokenizer.nextToken();
                String purchase_date = tokenizer.nextToken();
                String product_name = tokenizer.nextToken();

                // Create a new customer and add purchase information
                Customer new_customer = new Customer(customer_id, customer_name);
                new_customer.addPurchase(purchase_date, product_name);

                // Insert the customer into the hash table
                Customer_History.put(customer_id, new_customer);
            }
            br.close();

            // Record end time for loading data
            Instant end = Instant.now();
            Duration time_elapsed = Duration.between(start, end);

            // Display loading statistics
            System.out.println(String.format("\nLoading customer data from %s completed in %d ms.", test_file, time_elapsed.toMillis()));
            System.out.println(String.format("Customers: %d \t Purchases: %d \t Collisions: %d", Customer_History.get_numberofcustomers(), counter, Customer_History.get_collisioncount()));

            // Perform test input for a specific dataset
            if (test_file.equals("supermarket_dataset_50K.csv")) {
                test_input("customer_1K.txt");
            }

            // UI
            while (true) {
                // Get user input for customer ID
                System.out.println("\nPlease enter customer ID: ");
                String input_ID = scan.next();

                // Get user choice for viewing purchases or removing customer data
                System.out.print("\nView Purchases (1) / Remove Customer Data (2) : ");
                int choice1 = scan.nextInt();

                // View purchases or remove customer data based on user choice
                if (choice1 == 1) {
                    if (Customer_History.getValue(input_ID) != null) {
                        Customer_History.getValue(input_ID).display_purchase();
                    } else {
                        System.out.println("Invalid customer ID.");
                    }
                } else {
                    System.out.print("Customer data will be removed. Are you sure? Y/N: ");
                    String choice2 = scan.next();

                    if (choice2.toLowerCase().equals("y")) {
                        if (Customer_History.getValue(input_ID) != null) {
                            Customer_History.remove(input_ID);
                            System.out.println("Customer data deleted.");
                        } else {
                            System.out.println("Customer not found!");
                        }
                    }
                }

                System.out.print("\nContinue Y/N: ");
                String choice = scan.next().toLowerCase();

                if (choice.equals("y"))
                    continue;
                else
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Test input method for searching 1K customer IDs
    public static void test_input(String uuid_txt) throws IOException, InterruptedException {
        Scanner scan = new Scanner(System.in);
        System.out.print("\n\nDo you want to start 1K customer id test search Y/N: ");
        String test_choice = scan.next();
        int customer_count = 0;

        if (test_choice.equalsIgnoreCase("y")) {

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

                if (Customer_History.getValue(input_ID) != null) {
                    customer_found = true;
                }

                test_end = Instant.now();

                if (customer_found) {
                    Customer_History.getValue(input_ID).display_purchase();
                    customer_count++;
                } else {
                    System.out.println("Customer not found!");
                }

                Duration test_time = Duration.between(test_start, test_end);

                total_duration = total_duration.plus(test_time);

                if (test_time.compareTo(max_duration) >= 0) {
                    max_duration = test_time;
                }
                if (test_time.compareTo(min_duration) <= 0) {
                    min_duration = test_time;
                }

            }
            br.close();

            double average_time = total_duration.getNano() / 1000.0;

            System.out.println("\nmax: " + max_duration.toNanos() + " min: " + min_duration.toNanos()
                    + " Total Duration: " + total_duration.toNanos() + " Average Time: " + average_time
                    + " customers: " + customer_count);
        }
    }

    // Display menu
    public static void menu() {
        try {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Database");
            System.out.println("2. Settings");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Display database information and exit menu
                    System.out.print("SSF or PAF(SSF: 1 PAF: 2):" + SSForPAF + "\nMax Load Factor:" + MAX_LOAD_FACTOR + "\nLinear Probing(LP) or Double Hashing(DH):");
                    return;
                case 2:
                    // Go to settings menu
                    settingsMenu(scanner);
                    break;
                case 3:
                    // Exit the program
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid option.");
        }
    }

// Display the settings menu
public static void settingsMenu(Scanner scanner) {
    try {
    while (true) {
        System.out.println("1. Hash Function");
        System.out.println("2. Max Load Factor");
        System.out.println("3. Back to main menu");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                // Hash Function selected, go to hash function menu
                hashFunctionMenu(scanner);
                break;
            case 2:
                // Max Load Factor selected, update the max load factor
                System.out.print("Enter Max Load Factor: ");
                String temp1 = scanner.next();
                MAX_LOAD_FACTOR = Double.parseDouble(temp1);
                break;
            case 3:
                // Back to the main menu
                return;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
    }
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid option.");
    }
}

    // Display the hash function menu
    public static void hashFunctionMenu(Scanner scanner) {
        try {
        System.out.println("1. Simple Summation Function (SSF)");
        System.out.println("2. Polynomial Accumulation Function (PAF)");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                // Simple Summation Function selected
                SSForPAF = "1";
                break;
            case 2:
                // Polynomial Accumulation Function selected
                SSForPAF = "2";
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid option.");
        }
    }
}
