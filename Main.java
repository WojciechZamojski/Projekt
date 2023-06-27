import java.io.*;
import java.util.*;

// Klasa reprezentująca kartę elektroniczną
class ElectronicCard {
    private String cardNumber;
    private String pin;

    public ElectronicCard(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }
}

// Klasa reprezentująca bankomat
class Bankomat {
    private String databasePath;
    private String eventsPath;

    public Bankomat(String databasePath, String eventsPath) {
        this.databasePath = databasePath;
        this.eventsPath = eventsPath;
    }

    public double getAccountBalance(String cardNumber) {
        double accountBalance = 0.0;
        try (Scanner scanner = new Scanner(new File(databasePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                if (data[0].equals(cardNumber)) {
                    if (data.length > 1) {
                        accountBalance = Double.parseDouble(data[1]);
                    }
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Baza danych nie istnieje.");
        }
        return accountBalance;
    }

    public boolean verifyCardType(String cardNumber, String cardType) {
        try (Scanner scanner = new Scanner(new File(databasePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                if (data[0].equals(cardNumber)) {
                    String storedCardType = data[3];
                    return cardType.equalsIgnoreCase(storedCardType);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Baza danych nie istnieje.");
        }
        return false;
    }

    public boolean verifyPin(String cardNumber, String enteredPin) {
        try (Scanner scanner = new Scanner(new File(databasePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                if (data[0].equals(cardNumber)) {
                    String storedPin = data[2];
                    return enteredPin.equals(storedPin);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Baza danych nie istnieje.");
        }
        return false;
    }

    public void withdrawMoney(ElectronicCard card, double amount) {
        String cardNumber = card.getCardNumber();
        double accountBalance = getAccountBalance(cardNumber);

        if (amount > accountBalance) {
            System.out.println("Brak wystarczających środków na koncie.");
            return;
        }

        double newBalance = accountBalance - amount;
        updateAccountBalance(cardNumber, newBalance);
        logEvent("Wypłata", cardNumber, amount);

        System.out.println("Wypłacono kwotę: " + amount);
        System.out.println("Aktualny stan konta: " + newBalance);
    }

    private void updateAccountBalance(String cardNumber, double newBalance) {
        try {
            File inputFile = new File(databasePath);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(" ");
                if (data[0].equals(cardNumber)) {
                    writer.write(data[0] + " " + newBalance + " " + data[2] + " " + data[3] + "\n");
                } else {
                    writer.write(currentLine + "\n");
                }
            }

            reader.close();
            writer.close();

            if (!inputFile.delete()) {
                System.out.println("Błąd podczas aktualizacji konta.");
                return;
            }

            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Błąd podczas aktualizacji konta.");
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas aktualizacji konta.");
        }
    }

    private void logEvent(String operation, String cardNumber, double amount) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(eventsPath, true))) {
            writer.println("Operacja: " + operation);
            writer.println("Karta: " + cardNumber);
            writer.println("Kwota: " + amount);
            writer.println("----------------------------");
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisu zdarzenia.");
        }
    }
}

// Klasa reprezentująca kartę Visa
class VisaCard extends ElectronicCard {
    public VisaCard(String cardNumber, String pin) {
        super(cardNumber, pin);
    }
}

// Klasa reprezentująca kartę Mastercard
class Mastercard extends ElectronicCard {
    public Mastercard(String cardNumber, String pin) {
        super(cardNumber, pin);
    }
}

// Klasa reprezentująca kartę American Express
class AmericanExpressCard extends ElectronicCard {
    public AmericanExpressCard(String cardNumber, String pin) {
        super(cardNumber, pin);
    }
}

// Klasa reprezentująca kartę Visa Electron
class VisaElectronCard extends ElectronicCard {
    public VisaElectronCard(String cardNumber, String pin) {
        super(cardNumber, pin);
    }
}

// Klasa główna programu
class BankomatProgram {
    public static void main(String[] args) {
        Bankomat bankomat = new Bankomat("database.txt", "zdarzenia.txt");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj typ karty (Visa, Mastercard, American_Express, Visa_Electron): ");
        String cardType = scanner.nextLine();
        System.out.print("Podaj numer karty: ");
        String cardNumber = scanner.nextLine();

        ElectronicCard userCard;
        switch (cardType.toLowerCase()) {
            case "visa":
                userCard = new VisaCard(cardNumber, "");
                break;
            case "mastercard":
                userCard = new Mastercard(cardNumber, "");
                break;
            case "american_express":
                userCard = new AmericanExpressCard(cardNumber, "");
                break;
            case "visa_electron":
                userCard = new VisaElectronCard(cardNumber, "");
                break;
            default:
                System.out.println("Nieprawidłowy typ karty.");
                return;
        }

        if (!bankomat.verifyCardType(cardNumber, cardType)) {
            System.out.println("Podany numer karty nie odpowiada wybranemu typowi karty.");
            return;
        }

        System.out.print("Podaj PIN: ");
        String pin = scanner.nextLine();

        if (!bankomat.verifyPin(cardNumber, pin)) {
            System.out.println("Nieprawidłowy PIN.");
            return;
        }

        System.out.println("Witaj w bankomacie!");

        while (true) {
            System.out.println("\nMenu wyboru operacji:");
            System.out.println("1. Sprawdzenie stanu konta");
            System.out.println("2. Wypłata pieniędzy");
            System.out.println("3. Wyjście");

            System.out.print("Wybierz numer operacji: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Odczytujemy pozostały znak nowej linii

            switch (choice) {
                case 1:
                    double accountBalance = bankomat.getAccountBalance(cardNumber);
                    System.out.println("Stan konta: " + accountBalance);
                    break;
                case 2:
                    System.out.print("Podaj kwotę do wypłacenia: ");
                    double amount = scanner.nextDouble();
                    bankomat.withdrawMoney(userCard, amount);
                    break;
                case 3:
                    System.out.println("Dziękujemy za skorzystanie z bankomatu. Zapraszamy ponownie!");
                    return;
                default:
                    System.out.println("Nieprawidłowy numer operacji.");
                    break;
            }
        }
    }
}
