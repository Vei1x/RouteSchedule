package md;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;

class Program {

    private final Scanner sc = new Scanner(System.in);
    private final Routes newRoute = new Routes();
    private ArrayList<Routes> routeList = new ArrayList<>();


    void run() throws IOException {
        routeList = CSVReader.readFile();
        System.out.println("Nikita Hvoina 201RDB074 4.grupa");

        loop:
        while (true) {
            System.out.println("\n1) Print");
            System.out.println("2) Add");
            System.out.println("3) Delete");
            System.out.println("4) Edit");
            System.out.println("5) Sort");
            System.out.println("6) Search");
            System.out.println("7) Average");
            System.out.println("8) Help");
            System.out.println("9) Exit");

            String[] commandLine;
            System.out.println("\nInput your command: ");
            commandLine = parseInput(sc.nextLine());
            try {
                switch (commandLine[0]) {
                    case "print" -> print();
                    case "add" -> add(commandLine[1]);
                    case "del" -> delete(commandLine[1]);
                    case "edit" -> edit(commandLine[1]);
                    case "sort" -> sort();
                    case "find" -> search(commandLine[1]);
                    case "avg" -> avg();
                    case "help" -> help();
                    case "exit" -> { CSVReader.saveFile(routeList); break loop;}
                    default -> System.out.print("wrong command\n");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Not enough arguments!");
            }
        }
    }

    private String[] parseInput(String inputRAW) {
        String[] inputData;
        inputData = inputRAW.split(" ", 2);
        return inputData;
    }

    void help() {
        for (String s : Arrays.asList("add: add route\nSyntax: add <id(001-999)>;<city>;<date(DD/MM/YYYY)>;<days(int)>;<price(double)>;<vehicle(PLANE/BUS/BOAT/TRAIN)>\n\n", "del: delete route\nSyntax: del <id>\n\n", "edit: edit route\nSyntax: edit <id(000-999)>;<city>;<date(DD/MM/YYYY)>;<days(int)>;<price(double)>;<vehicle(PLANE/BUS/BOAT/TRAIN)>\n\n", "print: display current routes in table\nSyntax: print\n\n", "sort: sort all routes\nSyntax: sort\n\n", "find: Only display routes that have lower price than mentioned by user\nSyntax: find <price>\n\n", "avg: calculate average price of all routes\nSyntax: avg\n\n", "exit: close program and save database\nSyntax: exit\n")) {
            System.out.print(s);
        }
    }

     void print() {
        System.out.print("------------------------------------------------------------\n");
        System.out.print("ID  City                 Date         Days     Price Vehicle\n");
        System.out.print("------------------------------------------------------------\n");

        for (Routes route : routeList) {
            System.out.println(route.toString());
        }
        System.out.print("------------------------------------------------------------\n");
    }

    void add(String parameters) {
        String[] newData = parameters.split(";");

        if (newData.length != 6) {
            System.out.print("wrong field count");
        } else {

            /*check unique ID*/
            try {
                Integer.parseInt(newData[0]);
            } catch (Exception e) {
                System.out.println("Wrong ID");
                return;
            }
            for (Routes route : routeList) {
                if (route.getId() == Integer.parseInt(newData[0])) {
                    System.out.print("ID already exist");
                    return;
                }
            }
            if (newRoute.isValidId(newData[0])
                    && newRoute.isValidDate(newData[2])
                    && newRoute.isValidDays(newData[3])
                    && newRoute.isValidPrice(newData[4])
                    && newRoute.isValidVehicle(newData[5])) {
                routeList.add(new Routes(newData));
                System.out.print("added");
            }
        }
    }

    void delete(String ID) {
        boolean found = false;
        try {
            Integer.parseInt(ID);
        } catch (NumberFormatException e) {
            System.out.println("wrong ID");
            return;
        }
        if (ID.length() != 3) {
            System.out.println("wrong ID");

        } else {
            for (int i = 0; i < routeList.size(); i++) {
                if (routeList.get(i).getId() == Integer.parseInt(ID)) {
                    found = true;
                    routeList.remove(i);
                    break;
                }
            }
            if (found) {
                System.out.println("deleted");
            } else {
                System.out.println("wrong id");
            }
        }
    }

    void edit(String parameters) {

        boolean found = false;
        int count = 0;
        try {
            if (!newRoute.isValidEdit(parameters)) {
                System.out.println("wrong field count!");
                return;
            }
            String[] dataToEdit = newRoute.countParameters(parameters);

            if (dataToEdit.length == 6) {

                try {
                    Integer.parseInt(dataToEdit[0]);
                } catch (Exception e) {
                    System.out.println("Wrong ID");
                    return;
                }

                if (newRoute.isValidId(dataToEdit[0])) {

                    for (Routes routes : routeList) {
                        if (routes.getId() == Integer.parseInt(dataToEdit[0])) {
                            found = true;
                            break;
                        } else {
                            count++;
                        }
                    }

                    if (found) {
                        if (!dataToEdit[1].equals("")) {
                            routeList.get(count).setCity(newRoute.capitalizeWord(dataToEdit[1]));
                        }

                        if (!dataToEdit[2].equals("")
                                && newRoute.isValidDate(dataToEdit[2])) {
                            routeList.get(count).setDate(dataToEdit[2]);
                        }

                        if (!dataToEdit[3].equals("")
                                && newRoute.isValidDays(dataToEdit[3])) {
                            routeList.get(count).setDays(Integer.parseInt(dataToEdit[3]));
                        }

                        if (!dataToEdit[4].equals("")
                                && newRoute.isValidPrice(dataToEdit[4])) {
                            routeList.get(count).setPrice(Double.parseDouble(dataToEdit[4]));
                        }

                        if (!dataToEdit[5].equals("")
                                && newRoute.isValidVehicle(dataToEdit[5])) {
                            routeList.get(count).setVehicle(dataToEdit[5].toUpperCase(Locale.ROOT));
                        }

                        System.out.println("changed");
                    } else {
                        System.out.println("Route was no found");
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("wrong field count!");
        }
    }

    void sort() {
        routeList.sort(Comparator.comparing(Routes::getDate));
        System.out.println("sorted");
    }

    void search(String parameters) {
        if (newRoute.isValidPrice(parameters)) {
            System.out.print("------------------------------------------------------------\n");
            System.out.print("ID  City                 Date         Days     Price Vehicle\n");
            System.out.print("------------------------------------------------------------\n");

            for (Routes route : routeList) {
                if (route.getPrice() <= Double.parseDouble(parameters)) {
                    System.out.println(route);
                }
            }
            System.out.print("------------------------------------------------------------\n");
        }
    }

    void avg() {
        double AveragePrice = 0.00;
        for (Routes routes : routeList) {
            AveragePrice += routes.getPrice();
        }
        AveragePrice = AveragePrice / routeList.size();
        System.out.printf("%.2f", AveragePrice);
    }

    private static class CSVReader {
        private static final String CREATED_FILE = "db.csv";
        private static final String ORIGINAL_FILE = "db.csv";

        public static void saveFile(ArrayList<Routes> routes) throws IOException {
            File fileName = new File(CREATED_FILE);

            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Routes route : routes) {
                bufferedWriter.write(route.getId() + ";"
                        + route.getCity() + ";"
                        + route.getDateString() + ";"
                        + route.getDays() + ";"
                        + route.getPriceString() + ";"
                        + route.getVehicle());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }

        public static ArrayList<Routes> readFile() throws IOException {
            ArrayList<Routes> routesList = new ArrayList<>();

            String line;

            FileReader fileReader = new FileReader(ORIGINAL_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] routeData = line.split(";");
                Routes routes = new Routes();
                routes.setId(Integer.parseInt(routeData[0]));
                routes.setCity(routeData[1]);
                routes.setDate(routeData[2]);
                routes.setDays(Integer.parseInt(routeData[3]));
                routes.setPrice(Double.parseDouble(routeData[4]));
                routes.setVehicle(routeData[5]);

                routesList.add(routes);
            }
            bufferedReader.close();
            return routesList;
        }
    }
}

// user interface
public class Main {
    public static void main(String[] args) {
        try {
            new Program().run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class Routes {
    NumberFormat df = new DecimalFormat("#0.00");
    private int id;
    private int days;
    private String city;
    private String date;
    private String vehicle;
    private double price;

    public Routes() {
    }

    //constructor
    public Routes(String[] newData) {
        this.id = Integer.parseInt(newData[0]);
        this.city = capitalizeWord(newData[1]);
        this.date = newData[2];
        this.days = Integer.parseInt(newData[3]);
        this.price = Double.parseDouble(newData[4]);
        this.vehicle = newData[5].toUpperCase(Locale.ROOT);
    }

    String[] countParameters(String parameters) {
        char[] charOfWord = parameters.toCharArray();
        String[] newData = new String[]{"", "", "", "", "", ""};
        int i, count = 0;
        try {
            for (i = 0; i < parameters.length(); i++) {
                if (charOfWord[i] != ';') {
                    newData[count] += charOfWord[i];
                } else {
                    count++;
                }
            }
            return newData;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("wrong field count(count par)");

        }
        return null;
    }

    LocalDate formatDate(String date) throws DateTimeException {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatDate);
    }

    String capitalizeWord(String city) {
        String[] words = city.toLowerCase(Locale.ROOT).split("\\s");
        StringBuilder capitalizeWord = new StringBuilder();
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterFirst = w.substring(1);
            capitalizeWord.append(first.toUpperCase()).append(afterFirst).append(" ");
        }
        return capitalizeWord.toString().trim();
    }


    /*Validation booleans*/
    boolean isValidEdit(String parameters) {
        char[] charOfWord = parameters.toCharArray();
        int i, count = 0;
        for (i = 0; i < charOfWord.length; i++) {
            if (charOfWord[i] == ';') {
                count++;
            }
        }
        return count == 5;
    }

    boolean isValidId(String id) {
        if (Integer.parseInt(id) < 0 || Integer.parseInt(id) >= 1000) {
            System.out.println("ID should be higher than 0 and lower than 1000");
            return false;
        }
        return true;
    }

    boolean isValidDate(String data) {
        try {
            formatDate(data);
        } catch (DateTimeException ex) {
            System.out.println("wrong date");
            return false;
        }
        return true;
    }

    boolean isValidDays(String days) {
        try {
            Integer.parseInt(days);
        } catch (NumberFormatException e) {
            System.out.println("wrong day count");
            return false;
        }
        return true;
    }

    boolean isValidPrice(String price) {
        try {
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            System.out.println("wrong price");
            return false;
        }
        return true;
    }

    boolean isValidVehicle(String vehicle) {
        if (vehicle.matches("(?i)bus|train|plane|boat")) return true;
        else System.out.println("wrong vehicle");
        return false;
    }


    //getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getDate() {
        return formatDate(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateString() {
        return date;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceString() {
        return df.format(price);
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        return String.format("%03d %-21s%-11s%6d  %8.2f %-8s", id, city, date, days, price, vehicle);
    }

}