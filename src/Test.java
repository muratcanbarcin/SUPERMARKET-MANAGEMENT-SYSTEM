import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Test {

    public static void main(String[] args) throws IOException {

        File test_file = new File("supermarket_dataset_50K.csv");

        BufferedReader br = new BufferedReader(new FileReader(test_file));
        Scanner scan = new Scanner(System.in);

        HashTable<String,String> Customer_History= new HashTable<>(16); //change for another file

        String line;
        int counter = 0;

        while ((line = br.readLine()) != null) {
            if (line.equals("Customer ID,Customer Name,Date,Product Name")){
                continue;
            }
            
            StringTokenizer tokenizer = new StringTokenizer(line, ","); //scanning text files and tokenizizng lines 
            
            String customer_id = tokenizer.nextToken();
            String customer_name = tokenizer.nextToken();
            String purchase_date = tokenizer.nextToken();
            String product_name = tokenizer.nextToken();

            // Customer new_customer = new Customer(split(line, ',')[0], split(line, ',')[1]);
            // new_customer.addPurchase(split(line, ',')[2], split(line, ',')[3]);

            // Customer_History.put(split(line, ',')[0],new_customer,split(line, ',')[2],split(line, ',')[3]);

            Customer new_customer = new Customer(customer_id, customer_name);
            new_customer.addPurchase(purchase_date, product_name);

            Customer_History.put(customer_id,new_customer);

            counter++;
            System.out.println(counter + " purchase added" + "load faact: " + Customer_History.getload() + "Entries: " + Customer_History.getentries()); //test
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
    public static String[] split(final String line, final char delimiter)
    {
        CharSequence[] temp = new CharSequence[(line.length() / 2) + 1];
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(delimiter, 0); // first substring

        while (j >= 0)
        {
            temp[wordCount++] = line.substring(i, j);
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }

        temp[wordCount++] = line.substring(i); // last substring

        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);

        return result;
    }
}
