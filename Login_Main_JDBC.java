import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

abstract class Employee {
    private String username, password;
    private int age;
    public double balance;

    abstract double getBalance();

    public final String getUsername() {
        return username;
    }

    public final void setUsername(String username) {
        this.username = username;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", balance=" + balance +
                '}';
    }
}

class BankCustomer extends Employee {
    private double accountNumber;

    public double getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(double accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    double getBalance() {
        return balance;
    }

    public void transaction(double amount) {
        if (amount < 0) {
            if (-(amount) > balance) {
                System.out.println("You can only withdraw up to Rs. " + balance);
            } else {
                balance += amount;
            }
        } else {
            balance += amount;
        }
    }

    public void transaction(double amount, BankCustomer obj) {
        if (amount > balance) {
            System.out.println("You can only transfer up to Rs. " + balance);
        } else {
            balance -= amount;
            obj.balance += amount;
            System.out.println("Transction Succesfull..");
        }
    }
}

public class Login_Main_JDBC {
    static String userName, password;
    static int age, choice, totalCustomer = 0, index, i;
    static double balance, amount;
    static ArrayList<BankCustomer> customers = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static Connection connection = null;
    static Statement statement = null;
    static ResultSet resultSet = null;

    static void registration() {
        BankCustomer customer = new BankCustomer();
        System.out.print("Enter Username: ");
        userName = sc.next();
        for (BankCustomer bankCustomer : customers) {
            if (bankCustomer.getUsername().equals(userName)) {
                System.out.println("Already Registered...");
                return;
            }
        }
        customer.setUsername(userName);
        customers.add(customer);
        System.out.print("Enter Password: ");
        customer.setPassword(sc.next());
        System.out.print("Enter Age: ");
        customer.setAge(sc.nextInt());
        totalCustomer += 1;
        double accountNumber = 180910400.0 + totalCustomer;
        customer.setAccountNumber(accountNumber);
        customer.balance = 0;

        // Inserting the customer details into the database
        try {
            statement = connection.createStatement();
            String sql = "INSERT INTO customers (username, password, age, account_number, balance) VALUES ('" +
                    customer.getUsername() + "', '" +
                    customer.getPassword() + "', " +
                    customer.getAge() + ", " +
                    customer.getAccountNumber() + ", " +
                    customer.getBalance() + ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static int login(String username, String password) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getUsername().equals(username) && customers.get(i).getPassword().equals(password)) {
                return i;
            }
        }
        return -1;
    }

    static void getPassword(String username) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getUsername().equals(username)) {
                System.out.println("Password: " + customers.get(i).getPassword());
            } else if (i == customers.size() - 1) {
                System.out.println("Username does not exist...");
            }
        }
    }

    static void bankMenu(int index) {
        do {
            System.out.println("Enter 1: Deposit");
            System.out.println("Enter 2: Withdraw");
            System.out.println("Enter 3: Check Balance");
            System.out.println("Enter 4: Transfer");
            System.out.println("Enter 9: Logout");
            System.out.print("-->");
            choice = sc.nextInt();
            switch (choice) {
                case 1: {
                    System.out.print("Enter Amount to Deposit: ");
                    amount = sc.nextDouble();
                    customers.get(index).transaction(amount);

                    // Update balance in the database
                    String updateQuery = "UPDATE customers SET balance = ? WHERE username = ?";
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setDouble(1, customers.get(index).getBalance());
                        preparedStatement.setString(2, userName);
                        preparedStatement.executeUpdate();
                        System.out.println("Amount Added Successfully...");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case 2: {
                    System.out.print("Enter Amount to Withdraw: ");
                    amount = sc.nextDouble();
                    customers.get(index).transaction(-(amount));

                    // Update balance in the database
                    String updateQuery = "UPDATE customers SET balance = ? WHERE username = ?";
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setDouble(1, customers.get(index).getBalance());
                        preparedStatement.setString(2, userName);
                        preparedStatement.executeUpdate();
                        System.out.println("Amount Withdrawn Successfully...");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case 3: {
                    System.out.println("Balance Amount: " + customers.get(index).getBalance());
                }
                break;
                case 4: {
                    System.out.print("Enter Account Number: ");
                    double acNumber = sc.nextDouble();
                    for (int i = 0; i < customers.size(); i++) {
                        if ((int) customers.get(i).getAccountNumber() == (int) acNumber) {
                            System.out.print("Enter Amount to Transfer: ");
                            amount = sc.nextDouble();
                            customers.get(index).transaction(amount, customers.get(i));

                            // Update balance in the database
                            String updateQuery = "UPDATE customers SET balance = ? WHERE username = ?";
                            try {
                                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                                preparedStatement.setDouble(1, customers.get(index).getBalance());
                                preparedStatement.setString(2, userName);
//
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            String updateQuery1 = "UPDATE customers SET balance = ? WHERE account_number = ?";
                            try {
                                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery1);
                                preparedStatement.setDouble(1, customers.get(i).getBalance());
                                preparedStatement.setDouble(2, acNumber);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        } else if (i == customers.size() - 1) {
                            System.out.println("Account Number Doesn't Exist.");
                            break;
                        }
                    }
                }
                break;
                case 9:
                    break;
                default:
                    break;
            }
        } while (choice != 9);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        int choice;
        String dbURL = "jdbc:mysql://localhost:3306/bankdb?characterEncoding=utf8";
        String username = "root";
        String password = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database...");

            statement = connection.createStatement();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(100) NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "age INT NOT NULL," +
                    "account_number DOUBLE NOT NULL," +
                    "balance DOUBLE NOT NULL)";
            statement.executeUpdate(createTableQuery);
            System.out.println("Customers table created...");

            resultSet = statement.executeQuery("SELECT * FROM customers");
            while (resultSet.next()) {
                BankCustomer customer = new BankCustomer();
                customer.setUsername(resultSet.getString("username"));
                customer.setPassword(resultSet.getString("password"));
                customer.setAge(resultSet.getInt("age"));
                customer.setAccountNumber(resultSet.getDouble("account_number"));
                customer.balance = resultSet.getDouble("balance");
                customers.add(customer);
                totalCustomer++;
            }
            resultSet.close();

            do {
                System.out.println("\tEnter 1: Register");
                System.out.println("\tEnter 2: Login");
                System.out.println("\tEnter 3: Forgot Password");
                System.out.println("\tEnter 4: Display");
                System.out.println("\tEnter 9: Exit");
                System.out.print("-->");
                choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        registration();
                        break;
                    case 2:
                        System.out.print("Enter Username: ");
                        userName = sc.next();
                        System.out.print("Enter Password: ");
                        password = sc.next();
                        index = login(userName, password);
                        if (index >= 0) {
                            System.out.println("Welcome " + userName + "...");
                            bankMenu(index);
                        } else {
                            System.out.println("Invalid Login Credentials...");
                        }
                        break;
                    case 3:
                        System.out.print("Enter Username: ");
                        userName = sc.next();
                        getPassword(userName);
                        break;
                    case 4:
                        System.out.print("Enter Security Code: ");
                        String pass = sc.next();
                        if(pass.equals("1234"))
                        {
                            if(totalCustomer==0)
                                System.out.println("There is no user enrolled for this APP");
                            else
                                for(int i=0;i<totalCustomer;i++)
                                {
                                    System.out.println(customers.get(i));
                                    System.out.println("----------------------------");
                                }
                        }
                        else
                        {
                            System.out.println("Security code Did not Match");
                        }
                        break;
                    case 9:
                        System.out.println("Exiting...");
                        break;
                    default:
                        break;
                }
            } while (choice != 9);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
