package carsharing;

import carsharing.dao.CarDAO;
import carsharing.dao.CompanyDAO;
import carsharing.dao.CustomerDAO;
import carsharing.dao.impl.CarDAOImpl;
import carsharing.dao.impl.CompanyDAOImpl;
import carsharing.dao.impl.CustomerDAOImpl;
import carsharing.dao.model.Car;
import carsharing.dao.model.CarToCustomer;
import carsharing.dao.model.Company;
import carsharing.dao.model.Customer;
import carsharing.dao.util.DatabaseManager;

import java.sql.*;
import java.util.*;

enum Status {
    MAIN,
    LOGGED_IN_ADMIN,
    LOGGED_IN_CUSTOMER,
    COMPANY_LIST,
    CREATE_COMPANY,
    CREATE_CUSTOMER,
    INVALID_INPUT,
    EXIT,
    CREATE_CAR, COMPANY_CAR_LIST, CUSTOMER_SELECTION, RENT_A_CAR, RETURN_RENTED_CAR, MY_RENTED_CAR, RENT_A_CAR_CONTINUED, COMPANY_DETAILS
}
public class Main {
    final static int INVALID = -1;
    public static void main(String[] args) {
        int companyPick = INVALID;
        Status s = Status.MAIN;
        String databaseName = args.length > 0 ? args[1] : "carcos";
        Scanner input = new Scanner(System.in);
        DatabaseManager dm = new DatabaseManager(databaseName);
        dm.prepareDatabase(databaseName);
        CompanyDAO cDAO = new CompanyDAOImpl(dm);ca
        CarDAO carDAO = new CarDAOImpl(dm);
        CustomerDAO cusDAO = new CustomerDAOImpl(dm);
        int choice = 0;
        Status previous = null;
        Optional<Company> currentCompany = Optional.empty();
        Optional<Customer> currentCustomer = Optional.empty();
        Map<Integer, Company> companyMapping = Map.of();
        while (true) {
            switch (s) {
                case MAIN:
                    System.out.println("""
                    1. Log in as a manager
                    2. Log in as a customer
                    3. Create a customer
                    0. Exit
                    """);
                    choice = input.hasNextInt() ? input.nextInt(): INVALID;
                    s = choice == 1 ? Status.LOGGED_IN_ADMIN : choice == 2 ? Status.CUSTOMER_SELECTION : choice == 3 ? Status.CREATE_CUSTOMER : choice == 0 ? Status.EXIT : Status.INVALID_INPUT;
                    previous = Status.MAIN;
                    input.nextLine();
                    break;
                case LOGGED_IN_ADMIN:
                    System.out.println("""
                            1. Company list
                            2. Create a company
                            0. Back""");
                    choice = input.hasNextInt() ? input.nextInt(): INVALID;
                    s = choice == 1 ? Status.COMPANY_LIST : choice == 2 ? Status.CREATE_COMPANY : choice == 0 ? Status.MAIN : Status.INVALID_INPUT;
                    previous = Status.LOGGED_IN_ADMIN;
                    input.nextLine();
                    break;
                case CUSTOMER_SELECTION:
                    List<Customer> resultt = cusDAO.getAllCustomers();
                    Set<Integer> ids = new HashSet<>();

                    if (resultt.isEmpty()) {
                        System.out.println("The customer list is empty!");
                        s = Status.MAIN;
                    } else {
                        for (Customer c : resultt) {
                            System.out.println(c.getId() + ". " + c.getName());
                            ids.add(c.getId());
                        }
                        System.out.println("0. Back");

                        choice = input.hasNextInt() ? input.nextInt(): INVALID;
                        int finalChoice = choice;
                        s = choice == 0 ? Status.MAIN : ids.contains(choice) ? Status.LOGGED_IN_CUSTOMER : Status.INVALID_INPUT;
                        currentCustomer = resultt.stream().filter(a -> a.getId() == finalChoice).findFirst();
                    }




                    break;
                case LOGGED_IN_CUSTOMER:
//                    System.out.println("Welcome " + currentCustomer.get().getName());
                    System.out.println("""
                            1. Rent a car
                            2. Return a rented car
                            3. My rented car
                            0. Back""");
                    choice = input.hasNextInt() ? input.nextInt(): INVALID;
                    s = choice == 1 ? Status.RENT_A_CAR : choice == 2 ? Status.RETURN_RENTED_CAR : choice == 3 ? Status.MY_RENTED_CAR : choice == 0 ? Status.MAIN : Status.INVALID_INPUT;
                    break;
                case RENT_A_CAR:
                    List<CarToCustomer> checkIfRented = cusDAO.getRentedCarsByCustomerId(currentCustomer.get().getId());

                    if (!checkIfRented.isEmpty()) {
                        System.out.println("You've already rented a car!");
                        s = Status.LOGGED_IN_CUSTOMER;
                    } else {
                        List<Company> companies = cDAO.getAll();
                        if (companies.isEmpty()) {
                            System.out.println("The company list is empty!");
                            s = Status.LOGGED_IN_CUSTOMER;
                        } else {
                            companyMapping = new HashMap<>();
                            System.out.println("Choose a company:");
                            int i = 1;
                            for (Company c : companies) {
                                System.out.println(i + ". " + c.getName());
                                companyMapping.put(i, c);
                                i++;
                            }
                            System.out.println("0. Back");
                            choice = input.hasNextInt() ? input.nextInt(): INVALID;
                            if (choice == 0) {
                                s = Status.LOGGED_IN_CUSTOMER;
                            } else {
                                boolean correctOption = companyMapping.containsKey(choice);
                                if (correctOption ) {
                                    s = Status.RENT_A_CAR_CONTINUED;
                                } else {
                                    previous = Status.RENT_A_CAR;
                                }
                            }

                        }
                    }

                    break;
                case RENT_A_CAR_CONTINUED:
                    List<Car> availableCars = carDAO.getAllAvailableCarsByCompanyId(companyMapping.get(choice).getID());
                    if (availableCars.isEmpty()) {
                        System.out.printf("No available cars in the '%s' company \n", companyMapping.get(choice).getName());
                        s = Status.LOGGED_IN_CUSTOMER;
                    } else {
                        Map<Integer, Car> carMapping = new HashMap<>();
                        int c_i = 1;
                        for (Car c : availableCars) {
                            System.out.println(c.getId() + ". " + c.getName());
                            carMapping.put(c_i, c);
                            c_i++;
                        }
                        System.out.println("0. Exit");
                        choice = input.hasNextInt() ? input.nextInt(): INVALID;
                        if (choice == 0) {
                            s = Status.RENT_A_CAR;
                        } else {
                            boolean validChoice = carMapping.containsKey(choice);
                            if (validChoice) {
                                cusDAO.rentACar(currentCustomer.get().getId(), carMapping.get(choice).getId());
                                System.out.printf("You rented '%s' \n", carMapping.get(choice).getName());
                                s = Status.LOGGED_IN_CUSTOMER;
                            } else {
                                s = Status.INVALID_INPUT;
                            }
                        }


                    }
                    break;
                case RETURN_RENTED_CAR:
                    int cId = currentCustomer.get().getId();
                    if (!cusDAO.isCarRented(cId)) {
                        System.out.println("You didn't rent a car!");
                    } else {
                        System.out.println("You've returned a rented car!");
                        cusDAO.returnCar(cId);
                    }
                    s = Status.LOGGED_IN_CUSTOMER;
                    break;
                case MY_RENTED_CAR:
                    List<CarToCustomer> rentedCars = cusDAO.getRentedCarsByCustomerId(currentCustomer.get().getId());
                    if (rentedCars.isEmpty()) {
                        System.out.println("You didn't rent a car!");
                    } else {
                        for (CarToCustomer c : rentedCars) {
                            System.out.println("Your rented car:");
                            System.out.println(c.getName());
                            System.out.println("Company:");
                            System.out.println(c.getCompanyName());
                        }
                    }
                    s = Status.LOGGED_IN_CUSTOMER;
                    break;
                case CREATE_CUSTOMER:
                    System.out.println("Enter the customer name:");
                    String username = input.nextLine();
                    cusDAO.createCustomer(username);
                    s = Status.MAIN;
                    break;
                case COMPANY_LIST:
                    List<Company> result = null;
                    Set<Integer> companyIDs = new HashSet<>();
                    result = cDAO.getAll();
                    if (result.isEmpty()) {
                        System.out.println("The company list is empty!");
                        s = Status.LOGGED_IN_ADMIN;
                    } else {
                        for (Company c : result) {
                            System.out.printf("%d. %s \n", c.getID(), c.getName());
                            companyIDs.add(c.getID());
                        }

                        System.out.println("0. Back");
//                        companyPick = INVALID;
                        if (input.hasNextInt()) {
                            companyPick = input.nextInt();
                        }
                        choice = companyIDs.contains(companyPick) ? companyPick : INVALID;
                        int finalCompanyPick = companyPick;
                        currentCompany = result.stream().filter(c -> c.getID() == finalCompanyPick).findFirst();
                        s = choice == 0 ? Status.LOGGED_IN_ADMIN : choice == INVALID ? Status.INVALID_INPUT : Status.COMPANY_DETAILS;

                    }
                    break;
                case COMPANY_DETAILS:
                    if (companyPick == INVALID) {
                        s = Status.COMPANY_LIST;
                    }
                    System.out.println(currentCompany.get().getName() + """
                             company:
                            1. Car list
                            2. Create a car
                            0. Back""");
                    choice = input.hasNextInt() ? input.nextInt() : INVALID;
                    s = choice == 1 ? Status.COMPANY_CAR_LIST : choice == 2 ? Status.CREATE_CAR : choice == 0 ? Status.LOGGED_IN_ADMIN : Status.INVALID_INPUT;
                    input.nextLine();


                    break;
                case COMPANY_CAR_LIST:
                    Map<Integer, Car> mappingICar = new HashMap<>();
                    List<Car> resultCarByCompanyId = carDAO.getAllCarsByCompanyId(currentCompany.get().getID());
                    if (resultCarByCompanyId.isEmpty()) {
                        System.out.println("The car list is empty!");
                    } else {
                        int j = 1;
                        for (Car c : resultCarByCompanyId) {
                            System.out.printf("%d. %s \n", j++, c.getName());
                            mappingICar.put(j, c); // to keep track of ID in the database while keeping sequential from 1
                        }
                    }

                    s = Status.COMPANY_DETAILS;
                    break;
                case CREATE_COMPANY:
                    System.out.println("Enter the company name:");
                    String name = input.nextLine();
                    cDAO.create(name);
                    s = Status.LOGGED_IN_ADMIN;
                    break;
                case CREATE_CAR:
                    System.out.println("Enter the car name:");
                    String carName = input.nextLine();
                    carDAO.createCarByCompanyId(carName, currentCompany.get().getID());
                    s = Status.COMPANY_DETAILS;
                    break;
                case INVALID_INPUT:
                    s = previous;
                    break;
                case EXIT:
                    try {
                        dm.getConn().close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.exit(0);
            }

        }
    }
}